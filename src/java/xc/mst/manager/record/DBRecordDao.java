package xc.mst.manager.record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.utils.TimingLogger;

public class DBRecordDao {
	
	protected PreparedStatement recordsPS = null;
	protected PreparedStatement recordsXmlPS = null;
	protected Connection conn = null;
	
	protected int offset = 0;
	protected int recordsAtOnce = 5000;
	
	public boolean insert(Record record) {
		try {
			if (conn == null) {
				conn = MySqlConnectionManager.getConnection();
				//create table records_xml (
				//id             int         primary key,
				//xml            varchar(21842)


			}
			if (recordsXmlPS == null) {
				String sql = 
					"insert into records_xml (xml) values (?)";
				recordsXmlPS = conn.prepareStatement(sql);				
			}

			if (recordsPS == null) {
				String sql = 
					"insert into records (service_id, identifier_full, identifier_1, identifier_2, datestamp, setSpec) "+
						" values (?, ?, ?, ?, current_timestamp, null)";
				recordsPS = conn.prepareStatement(sql);			
			}

			int i = 1;
			recordsXmlPS.setString(i++, record.getOaiXml());
			recordsXmlPS.addBatch();
			
			i = 1;
			int serviceId = 0;
			try {
				serviceId = record.getService().getId();
			} catch (Throwable t) {
				//do nothing
			}
			recordsPS.setInt(i++, serviceId);
			
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
					recordsPS.setString(i++, record.getOaiIdentifier());
				} else {
					recordsPS.setString(i++, null);
					TimingLogger.log("record.getOaiIdentifier(): "+record.getOaiIdentifier());
				}
				recordsPS.setString(i++, null);
				recordsPS.setString(i++, null);
			} else {
				recordsPS.setString(i++, null);
				recordsPS.setString(i++, tokens[2]);
				recordsPS.setString(i++, tokens[3]);
			}
			recordsPS.addBatch();
			//recordsPS.setString(i++, record.getO)
			return true;
		} catch (SQLException sqe) {
			throw new RuntimeException(sqe);
		}
	}
	
	public void commit() {
		try {
			recordsXmlPS.executeBatch();
			recordsPS.executeBatch();
			recordsXmlPS = null;
			recordsPS = null;
			conn.close();
		} catch (SQLException sqe) {
			throw new RuntimeException(sqe);
		}
	}
	
	public List<Record> getInputForServiceToProcess(int serviceId, boolean reset) {
		if (reset) {
			offset = 0;
		}
		List<Record> records = new ArrayList<Record>();
		String sql = 
			"select rx.xml, r.identifier_1, r.identifier_2, r.datestamp, r.setSpec " +
			"from records r, records_xml rx  "+
			"where r.id = rx.id "+
			"and r.service_id = 0 "+
			"limit "+offset+","+recordsAtOnce;
		
		Connection conn = null;
		try {
			conn = MySqlConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			//ps.setInt(1, serviceId);
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Record r = new Record();
				r.setOaiXml(rs.getString("rx.xml"));
				String id1 = rs.getString("r.identifier_1");
				String id2 = rs.getString("r.identifier_2");
				String id = "oai:library.rochester.edu:"+id1+":"+id2;
				r.setOaiIdentifier(id);
				r.setUpdatedAt(rs.getDate("r.datestamp"));
				
				records.add(r);
				offset += recordsAtOnce;
			}
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
