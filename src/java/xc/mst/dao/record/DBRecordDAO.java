package xc.mst.dao.record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.utils.TimingLogger;

public class DBRecordDAO {
	
	protected PreparedStatement recordsInsertPS = null;
	protected PreparedStatement recordsInsertXmlPS = null;
	protected PreparedStatement recordsUpdatePS = null;
	protected Connection conn = null;
	
	protected int offset = 0;
	protected int readsAtOnce = 50000;
	protected int insertsAtOnce = 50000;
	protected int insertTally = 0;
	protected boolean shouldUpdate = false;
	
	public void reset() {
		recordsInsertPS = null;
		recordsInsertXmlPS = null;
		recordsUpdatePS = null;
		conn = null;
		
		offset = 0;
		readsAtOnce = 50000;
		insertsAtOnce = 50000;
		insertTally = 0;
		shouldUpdate = false;
	}
	
	public boolean insert(Record record) {
		TimingLogger.start("DBRecordDao.insert");
		try {
			if (conn == null || conn.isClosed()) {
				conn = MySqlConnectionManager.getConnection();
			}
			if (recordsInsertXmlPS == null) {
				String sql = 
					"insert into records_xml (xml) values (?)";
				recordsInsertXmlPS = conn.prepareStatement(sql);				
			}

			if (recordsInsertPS == null) {
				String sql = 
					"insert into records (service_id, identifier_full, identifier_1, identifier_2, datestamp, setSpec) "+
						" values (?, ?, ?, ?, current_timestamp, null)";
				recordsInsertPS = conn.prepareStatement(sql);			
			}

			int i = 1;
			recordsInsertXmlPS.setString(i++, record.getOaiXml());
			recordsInsertXmlPS.addBatch();
			
			i = 1;
			int serviceId = 0;
			try {
				serviceId = record.getService().getId();
			} catch (Throwable t) {
				//do nothing
			}
			recordsInsertPS.setInt(i++, serviceId);
			
			String[] tokens = null;
			int idx = record.getOaiIdentifier().indexOf("MetadataServicesToolkit"); 
			if (idx != -1) {
				tokens = new String[4];
				String[] slashTokens = record.getOaiIdentifier().split("/");
				tokens[2] = "norm";
				tokens[3] = slashTokens[2];
			} else {
				tokens = record.getOaiIdentifier().split(":");
			}
			
			if (tokens == null || tokens.length < 4) {
				if (record.getOaiIdentifier() == null || record.getOaiIdentifier().length() < 60) {
					recordsInsertPS.setString(i++, record.getOaiIdentifier());
				} else {
					recordsInsertPS.setString(i++, null);
					TimingLogger.log("record.getOaiIdentifier(): "+record.getOaiIdentifier());
				}
				recordsInsertPS.setString(i++, null);
				recordsInsertPS.setString(i++, null);
			} else {
				recordsInsertPS.setString(i++, null);
				recordsInsertPS.setString(i++, tokens[2]);
				recordsInsertPS.setString(i++, tokens[3]);
			}
			recordsInsertPS.addBatch();
			
			insertTally++;
			TimingLogger.stop("DBRecordDao.insert");
			return true;
		} catch (SQLException sqe) {
			throw new RuntimeException(sqe);
		}
	}
	
	public boolean update(Record record) {
		if (!shouldUpdate) {
			shouldUpdate = true;
			TimingLogger.log("toggling update");
		}
		return true;
		/*
		TimingLogger.start("DBRecordDao.update");
		try {
			if (conn == null || conn.isClosed()) {
				conn = MySqlConnectionManager.getConnection();
			}
			if (recordsUpdatePS == null) {
				String sql = 
					"update records set process_complete = ? where id = ? ";
				recordsUpdatePS = conn.prepareStatement(sql);			
			}

			int i = 1;
			recordsUpdatePS.setString(i++, "Y");
			recordsUpdatePS.setLong(i++, record.getId());
			recordsUpdatePS.addBatch();
			
			TimingLogger.stop("DBRecordDao.update");
			return true;
		} catch (SQLException sqe) {
			throw new RuntimeException(sqe);
		}
		*/
	}
	
	public void commit(int serviceId, boolean force) {
		int oldServiceId = 0;
		if (serviceId == 199) {
			oldServiceId = 99;
		}
		try {
			if (insertTally >= insertsAtOnce) {
				force = true;
			}
			if (force) {
				if (conn == null || conn.isClosed()) {
					conn = MySqlConnectionManager.getConnection();
				}
				insertTally = 0;
				TimingLogger.start("DBRecordDao.commit");
				if (recordsInsertPS != null) {
					TimingLogger.start("DBRecordDao.commit.insertRecordXml");
					recordsInsertXmlPS.executeBatch();
					TimingLogger.stop("DBRecordDao.commit.insertRecordXml");
					recordsInsertXmlPS = null;
				}
				if (recordsInsertPS != null) {
					TimingLogger.start("DBRecordDao.commit.insertRecord");
					recordsInsertPS.executeBatch();
					TimingLogger.stop("DBRecordDao.commit.insertRecord");
					recordsInsertPS = null;
				}
				
				if (shouldUpdate) {
					PreparedStatement ps1 = conn.prepareStatement(
						"update records set process_complete = 'Y' "+
						"where service_id = "+oldServiceId+" "+
						"and process_complete = 'N' "+
						"limit "+readsAtOnce
					);
					TimingLogger.start("DBRecordDao.commit.updateLimit");
					ps1.executeUpdate();
					TimingLogger.stop("DBRecordDao.commit.updateLimit");
					shouldUpdate = false;
				}
				if (recordsUpdatePS != null ) {
					TimingLogger.start("DBRecordDao.commit.updateRecord");
					recordsUpdatePS.executeBatch();
					TimingLogger.stop("DBRecordDao.commit.updateRecord");
					recordsUpdatePS = null;
				}
				TimingLogger.stop("DBRecordDao.commit");
				conn.close();
				TimingLogger.stop("sinceLastCommit");
				TimingLogger.start("sinceLastCommit");

				TimingLogger.reset(false);
			}
		} catch (SQLException sqe) {
			throw new RuntimeException(sqe);
		}
	}
	
	public List<Record> getInputForServiceToProcess(int serviceId, boolean reset) {
		if (reset) {
			offset = 0;
		}
		List<Record> records = new ArrayList<Record>();
		int previousServiceId = 0;
		if (serviceId == 199) {
			previousServiceId = 99;
		}
		String sql = 
			"select rx.xml, r.id, r.identifier_1, r.identifier_2, r.datestamp, r.setSpec " +
			"from records r, records_xml rx  "+
			"where r.id = rx.id "+
			"and r.service_id = "+previousServiceId+" "+
			"and process_complete = 'N' "+
			"limit "+readsAtOnce;
			//"limit "+offset+","+readsAtOnce;
		
		TimingLogger.log("sql: "+sql);
		Connection conn = null;
		try {
			conn = MySqlConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			//ps.setInt(1, serviceId);
			
			TimingLogger.start("DBRecordDao ps.executeQuery");
			TimingLogger.start("DBRecordDao create List");
			ResultSet rs = ps.executeQuery();
			TimingLogger.stop("DBRecordDao ps.executeQuery");
			while (rs.next()) {
				Record r = new Record();
				r.setOaiXml(rs.getString("rx.xml"));
				String id1 = rs.getString("r.identifier_1");
				String id2 = rs.getString("r.identifier_2");
				String id = "oai:library.rochester.edu:"+id1+":"+id2;
				r.setId(rs.getLong("r.id"));
				r.setOaiIdentifier(id);
				r.setUpdatedAt(rs.getDate("r.datestamp"));
				
				records.add(r);
				shouldUpdate = true;
			}
			offset += readsAtOnce;
			TimingLogger.stop("DBRecordDao create List");
		} catch (Throwable t) {
			DefaultRecordService.log.error("", t);
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (Throwable t) {
				DefaultRecordService.log.error("", t);
			}
		}
		return records;
	}

}
