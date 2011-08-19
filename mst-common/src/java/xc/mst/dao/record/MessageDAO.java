package xc.mst.dao.record;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.dao.BaseDAO;
import xc.mst.utils.MSTConfiguration;
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

    public void deleteMessagesByRecordId(int serviceId, final TLongHashSet messages2deleteByRecordId) {
        final TLongIterator it = messages2deleteByRecordId.iterator();
        int[] updateCount = jdbcTemplate.batchUpdate(
                "delete from " + MESSAGES_TABLE + " where record_id = ? and service_id =" + serviceId,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int j) throws SQLException {
                        ps.setLong(1, it.next());
                    }

                    public int getBatchSize() {
                        return messages2deleteByRecordId.size();
                    }
                });
    }

    public void deleteMessages(final List<RecordMessage> messages2delete) {
        final Iterator it = messages2delete.iterator();
        int[] updateCount = jdbcTemplate.batchUpdate(
                "delete from " + MESSAGES_TABLE + " where record_id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int j) throws SQLException {
                        ps.setLong(1, ((RecordMessage) it.next()).getId());
                    }

                    public int getBatchSize() {
                        return messages2delete.size();
                    }
                });
    }

    @SuppressWarnings("unused")
    public void persistMessages(final List<RecordMessage> messages) {
        try {
            String dbLoadFileStr = (MSTConfiguration.getUrlPath() + "/db_load.in").replace('\\', '/');
            LOG.debug("dbLoadFileStr: " + dbLoadFileStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            byte[] tabBytes = "\t".getBytes();
            byte[] newLineBytes = "\n".getBytes();

            File dbLoadFile = new File(dbLoadFileStr);
            if (dbLoadFile.exists()) {
                dbLoadFile.delete();
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
            int i = 0;
            TimingLogger.start("MESSAGES_TABLE.insert");
            TimingLogger.start("MESSAGES_TABLE.insert.create_infile");
            for (RecordMessage rm : messages) {
                if (i++ > 0) {
                    os.write(newLineBytes);
                }
                os.write(String.valueOf(rm.getId()).getBytes());
                os.write(tabBytes);
                if (rm.isInputRecord()) {
                    os.write("I".getBytes());
                } else {
                    os.write("O".getBytes());
                }
                os.write(tabBytes);
                os.write(String.valueOf(rm.getRecord().getId()).getBytes());
                os.write(tabBytes);
                os.write(String.valueOf(rm.getCode()).getBytes());
                os.write(tabBytes);
                os.write(String.valueOf(rm.getLevel()).getBytes());
                os.write(tabBytes);
                os.write(String.valueOf(rm.getServiceId()).getBytes());
            }
            os.close();
            TimingLogger.stop("MESSAGES_TABLE.insert.create_infile");
            TimingLogger.start("MESSAGES_TABLE.insert.load_infile");
            this.jdbcTemplate.execute(
                    "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
                            MESSAGES_TABLE +
                            " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
                    );
            TimingLogger.stop("MESSAGES_TABLE.insert.load_infile");
            TimingLogger.stop("MESSAGES_TABLE.insert");

            dbLoadFile = new File(dbLoadFileStr);
            if (dbLoadFile.exists()) {
                dbLoadFile.delete();
            }
            os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
            i = 0;
            TimingLogger.start("MESSAGES_DETAIL_TABLE.insert");
            TimingLogger.start("MESSAGES_DETAIL_TABLE.insert.create_infile");
            for (RecordMessage rm : messages) {
                if (!StringUtils.isEmpty(rm.getDetail())) {
                    if (i++ > 0) {
                        os.write(newLineBytes);
                    }
                    os.write(String.valueOf(rm.getId()).getBytes());
                    os.write(tabBytes);
                    os.write(rm.getDetail().getBytes());
                }
            }
            os.close();
            TimingLogger.stop("MESSAGES_DETAIL_TABLE.insert.create_infile");
            TimingLogger.start("MESSAGES_DETAIL_TABLE.insert.load_infile");
            this.jdbcTemplate.execute(
                    "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
                            MESSAGE_DETAILS_TABLE +
                            " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
                    );
            TimingLogger.stop("MESSAGES_TABLE.insert.load_infile");
            TimingLogger.stop("MESSAGES_TABLE.insert");
        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
    }

    public void injectMessages(Record r) {
        String sql =
                " select m.record_id, m.rec_in_out, m.msg_code, m.msg_level, m.service_id, d.detail " +
                        " from " + MESSAGES_TABLE + " as m " +
                        " left outer join " + MESSAGE_DETAILS_TABLE + " as d on (m.record_message_id = d.record_message_id) " +
                        " where m.record_id = ? ";

        List<RecordMessage> messages = this.jdbcTemplate.query(
                sql, new Object[] { r.getId() }, new MessageMapper());

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
