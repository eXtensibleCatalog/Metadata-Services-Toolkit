package xc.mst.dao.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.dao.BaseDAO;
import xc.mst.utils.TimingLogger;

public class MessageDAO extends BaseDAO {
	
	private static final Logger LOG = Logger.getLogger(MessageDAO.class);
	
	public static final String MESSAGES_TABLE = "record_messages";
	public static final String MESSAGE_DETAILS_TABLE = "record_message_details";
	
	protected Lock idLock = new ReentrantLock();
	protected int nextId = -1;
	protected int nextIdInDB = -1;
	
	protected SimpleJdbcCall getNextRecordMessageId = null;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.getNextRecordMessageId = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_next_record_message_id");
	}
	
	public void injectId(RecordMessage rm) {
		rm.setId(getNextId());
	}
	
	public long getNextId() {
		idLock.lock();
		if (nextId == nextIdInDB) {
			int idsAtOnce = 100;
			nextId = this.getNextRecordMessageId.executeObject(Integer.class, idsAtOnce);
			nextIdInDB = nextId + idsAtOnce;
		}
		idLock.unlock();
		return this.nextId++;
	}
	
	@SuppressWarnings("unused")
	public void persistMessages(final List<RecordMessage> messages) {
		TimingLogger.start(MESSAGES_TABLE+".insert");
		String sql = 
			"insert into "+MESSAGES_TABLE+
			" (record_message_id, service_id, rec_in_out, record_id, msg_code, msg_level ) "+
			"values (          ?,          ?,          ?,         ?,        ?,         ? ) ";

		int[] updateCounts = this.jdbcTemplate.execute(sql, new PreparedStatementCallback<int[]>() {
			public int[] doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				for (RecordMessage rm : messages) {
	        		int i=1;
	        		ps.setLong(i++, rm.getId());
	        		ps.setLong(i++, rm.getServiceId());
	        		if (rm.isInputRecord()) {
	        			ps.setString(i++, "I");
	        		} else {
	        			ps.setString(i++, "O");
	        		}
	        		ps.setLong(i++, rm.getRecord().getId());
	        		ps.setInt(i++, rm.getCode());
	        		ps.setString(i++, rm.getLevel()+"");
	        		ps.addBatch();
	        	}
				if (messages.size() > 0) {
					return ps.executeBatch();
				} else {
					return null;
				}
			}
		});
		TimingLogger.stop(MESSAGES_TABLE+".insert");
		TimingLogger.start(MESSAGE_DETAILS_TABLE+".insert");
		sql = 
			"insert into "+MESSAGE_DETAILS_TABLE+
			" (record_message_id, detail ) "+
			"values (          ?,      ? ) ";
		updateCounts = this.jdbcTemplate.execute(sql, new PreparedStatementCallback<int[]>() {
			public int[] doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				boolean atLeastOneInsert = false;
				for (RecordMessage rm : messages) {
					if (rm.getDetail() != null) {
						atLeastOneInsert = true;
						int i=1;
		        		ps.setLong(i++, rm.getId());
		        		ps.setString(i++, rm.getDetail());
		        		ps.addBatch();
					}
	        	}
				if (atLeastOneInsert) {
					return ps.executeBatch();	
				} else {
					return null;
				}
			}
		});
        TimingLogger.stop(MESSAGE_DETAILS_TABLE+".insert");
	}
	
	public void injectMessages(List<Record> records) {
		TimingLogger.start("injectMessages");
		long lowestRecordId = records.get(0).getId();
		long highestRecordId = records.get(records.size()-1).getId();
		String sql = 
			" select m.record_id, m.rec_in_out, m.msg_code, m.msg_level, m.service_id, d.detail "+
			" from "+MESSAGES_TABLE+" as m "+
				" left outer join "+MESSAGE_DETAILS_TABLE+" as d on (m.record_message_id = d.record_message_id) "+
			" where m.record_id >= ? "+
				" and m.record_id <= ? "+
			" order by m.record_id ";
		
		Iterator<Record> recordIt = records.iterator();
		Record currentRecord = recordIt.next();
		List<RecordMessage> messages = this.jdbcTemplate.query(
				sql, new Object[] {lowestRecordId, highestRecordId}, new MessageMapper());
		
		for (RecordMessage rm : messages) {
			while (rm.getRecord().getId() != currentRecord.getId()) {
				currentRecord = recordIt.next();
			}
			rm.setRecord(currentRecord);
			currentRecord.addMessage(rm);
		}
		TimingLogger.stop("injectMessages");
	}
	
	public void injectMessages(Record r) {
		String sql = 
			" select m.record_id, m.rec_in_out, m.msg_code, m.msg_level, m.service_id, d.detail "+
			" from "+MESSAGES_TABLE+" as m "+
				" left outer join "+MESSAGE_DETAILS_TABLE+" as d on (m.record_message_id = d.record_message_id) "+
			" where m.record_id = ? ";

		List<RecordMessage> messages = this.jdbcTemplate.query(
				sql, new Object[] {r.getId()}, new MessageMapper());
		
		for (RecordMessage rm : messages) {
			r.addMessage(rm);
		}
	}
	
	private static final class MessageMapper implements RowMapper<RecordMessage> {
	    public RecordMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	RecordMessage rm = new RecordMessage();
	    	Record r = new Record();
	    	r.setId(rs.getLong("m.record_id"));
	    	rm.setRecord(r);
	    	rm.setInputRecord("O".equals(rs.getString("m.rec_in_out")));
	    	rm.setCode(rs.getInt("m.msg_code"));
	    	rm.setLevel(rs.getString("m.msg_level").charAt(0));
	    	rm.setServiceId(rs.getInt("m.service_id"));
	    	rm.setDetail(rs.getString("d.detail"));
	    	return rm;
	    }
	}

}
