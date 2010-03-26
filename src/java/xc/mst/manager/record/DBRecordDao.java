package xc.mst.manager.record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import xc.mst.bo.record.Record;
import xc.mst.dao.MySqlConnectionManager;

public class DBRecordDao {
	
	protected PreparedStatement recordsPS = null;
	protected PreparedStatement recordsXmlPS = null;
	protected Connection conn = null;
	
	public boolean insert(Record record) {
		try {
			if (conn == null) {
				conn = MySqlConnectionManager.getConnection();
				//create table records_xml (
				//id             int         primary key,
				//xml            varchar(21842)

				String sql = 
					"insert into records_xml (xml) values (?)";
				recordsXmlPS = conn.prepareStatement(sql);
				
				sql = 
					"insert into records (service_id, identifier_1, identifier_2, datestamp, setSpec) "+
						" values (null, ?, ?, current_timestamp, null)";
				recordsPS = conn.prepareStatement(sql);
			}
			int i = 1;
			recordsXmlPS.setString(i++, record.getOaiXml());
			recordsXmlPS.addBatch();
			
			i = 1;
			String[] tokens = record.getOaiIdentifier().split(":");
			recordsPS.setString(i++, tokens[2]);
			recordsPS.setString(i++, tokens[3]);
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
			conn.close();
		} catch (SQLException sqe) {
			throw new RuntimeException(sqe);
		}
	}

}
