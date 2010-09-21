/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.transformation.dao;

import gnu.trove.TLongArrayList;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongLongProcedure;
import gnu.trove.TLongProcedure;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import xc.mst.bo.record.Record;
import xc.mst.dao.BaseDAO;
import xc.mst.repo.RepositoryDAO;
import xc.mst.services.impl.dao.GenericMetadataServiceDAO;
import xc.mst.services.impl.service.GenericMetadataServiceService;
import xc.mst.test.BaseMetadataServiceTest;
import xc.mst.utils.TimingLogger;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class TransformationDAO extends GenericMetadataServiceDAO {
	
	private final static Logger LOG = Logger.getLogger(TransformationDAO.class);
	
	protected final static String bibsProcessedLongId_table = "bibsProcessedLongId";
	protected final static String bibsProcessedStringId_table = "bibsProcessedStringId";
	protected final static String bibsYet2ArriveLongId_table = "bibsYet2ArriveLongId";
	protected final static String bibsYet2ArriveStringId_table = "bibsYet2ArriveStringId";
	protected final static String links_table = "links";
	
	@SuppressWarnings("unchecked")
	public void persistBibMaps(
			TLongLongHashMap bibsProcessedLongId, 
			Map<String, Long> bibsProcessedStringId,
			TLongLongHashMap bibsYet2ArriveLongId,
			Map<String, Long> bibsYet2ArriveStringId ) {
		
		// TODO: probably change these string literals to the above statics
		Object[] objArr = new Object[] {
				bibsProcessedLongId, bibsProcessedLongId_table,
				bibsProcessedStringId, bibsProcessedStringId_table,
				bibsYet2ArriveLongId, bibsYet2ArriveLongId_table,
				bibsYet2ArriveStringId, bibsYet2ArriveStringId_table};
		for (int i=0; i<objArr.length; i+=2){
			String tableName = (String)objArr[i+1];
			final List<Object[]> params = new ArrayList<Object[]>();
			if (objArr[i] instanceof TLongLongHashMap) {
				TLongLongHashMap longKeyedMap = (TLongLongHashMap)objArr[i];
				if (longKeyedMap != null && longKeyedMap.size() > 0) {
					longKeyedMap.forEachEntry(new TLongLongProcedure() {
						public boolean execute(long key, long value) {
							params.add(new Object[] {key, value});
							return true;
						}
					});		
				}
			} else if (objArr[i] instanceof Map) {
				Map<String, Long> stringKeyedMap = (Map<String, Long>)objArr[i];
				if (stringKeyedMap != null && stringKeyedMap.size() > 0) {
					for (Map.Entry<String, Long> me : stringKeyedMap.entrySet()) {
						params.add(new Object[] {me.getKey(), me.getValue()});
					}
				}
			}
			if (params.size() > 0) {
				TimingLogger.start(tableName+".insert");
				String sql =
					"insert ignore into "+tableName+
					" (bib_001, record_id) "+
					"values (?,?) ;";
		        int[] updateCounts = this.simpleJdbcTemplate.batchUpdate(sql, params);
		        TimingLogger.stop(tableName+".insert");	
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void loadBibMaps	(
			TLongLongHashMap bibsProcessedLongId, 
			Map<String, Long> bibsProcessedStringId,
			TLongLongHashMap bibsYet2ArriveLongId,
			Map<String, Long> bibsYet2ArriveStringId ) {
		
		Object[] objArr = new Object[] {
				bibsProcessedLongId, bibsProcessedLongId_table,
				bibsProcessedStringId, bibsProcessedStringId_table,
				bibsYet2ArriveLongId, bibsYet2ArriveLongId_table,
				bibsYet2ArriveStringId, bibsYet2ArriveStringId_table};
		for (int i=0; i<objArr.length; i+=2){
			String tableName = (String)objArr[i+1];
			String sql = "select bib_001, record_id from " +tableName;
			List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql);
			if (results != null) {
				for (Map<String, Object> row : results) {
					if (objArr[i] instanceof TLongLongHashMap) {
						((TLongLongHashMap)objArr[i]).put((Long)row.get("bib_001"), (Long)row.get("record_id"));
					} else if (objArr[i] instanceof Map) {
						((Map)objArr[i]).put((String)row.get("bib_001"), (Long)row.get("record_id"));
					}		
				}
			}
		}
	}
	
	public List<Long> getLinkedRecordIds(Long toRecordId) {
		List<Long> linkedRecordsIds = new ArrayList<Long>();
		String sql = "select from_record_id from links where to_record_id = ?";
		List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, toRecordId);
		if (results != null) {
			for (Map<String, Object> row : results) {
				linkedRecordsIds.add((Long)row.get("from_record_id"));
			}
		}
		return linkedRecordsIds;
	}
	
	public void persistLinkedRecordIds(final List<long[]> links) {
		String sql = "insert into links (from_record_id, to_record_id) values (?,?)";
		TimingLogger.start("links.insert");
        int[] updateCounts = jdbcTemplate.batchUpdate(
        		sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int j) throws SQLException {
                    	long[] link = links.get(j);
                    	ps.setLong(1, link[0]);
                    	ps.setLong(2, link[1]);
                    }

                    public int getBatchSize() {
                        return links.size();
                    }
                } );
        TimingLogger.stop("links.insert");
	}
	
	public void activateHeldHoldings(final TLongArrayList manifestionIdsPreviouslyHeld) {
		if (manifestionIdsPreviouslyHeld.size() > 0) {
			TimingLogger.start("activateHeldHoldings");
			StringBuilder sb = new StringBuilder("update "+RepositoryDAO.RECORDS_TABLE+
				" set status='"+Record.ACTIVE+"'"+
				" where record_id in (select from_record_id from links where to_record_id in (");
			for (int i=0; i<manifestionIdsPreviouslyHeld.size(); i++) {
				sb.append("?");
				if (i+1 < manifestionIdsPreviouslyHeld.size()) {
					sb.append(", ");
				}
			}
			sb.append("))");
			LOG.debug("sb.toString(): "+sb.toString());
			
	        int updateCount = jdbcTemplate.update(
	        		sb.toString(), new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps) throws SQLException {
							for (int i=0; i<manifestionIdsPreviouslyHeld.size(); i++) {
								ps.setLong(i+1, manifestionIdsPreviouslyHeld.get(i));
							}
						}
					});
	        TimingLogger.stop("activateHeldHoldings");
		} else {
			LOG.debug("manifestionIdsPreviouslyHeld is null or empty");
		}
	}
}
