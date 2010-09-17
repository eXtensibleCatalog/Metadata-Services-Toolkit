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

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import xc.mst.bo.record.Record;
import xc.mst.dao.BaseDAO;
import xc.mst.utils.TimingLogger;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class TransformationDAO extends BaseDAO {
	
	protected final static String bibsProcessedLongId_table = "bibsProcessedLongId";
	protected final static String bibsProcessedStringId_table = "bibsProcessedStringId";
	protected final static String bibsYet2ArriveLongId_table = "bibsYet2ArriveLongId";
	protected final static String bibsYet2ArriveStringId_table = "bibsYet2ArriveStringId";
	protected final static String links_table = "links";
	
	public void persistBibMaps(
			TLongLongHashMap bibsProcessedLongId, 
			Map<String, Long> bibsProcessedStringId,
			TLongLongHashMap bibsYet2ArriveLongId,
			Map<String, Long> bibsYet2ArriveStringId ) {
		if (bibsProcessedLongId != null && bibsProcessedLongId.size() > 0) {
			TimingLogger.start(bibsProcessedLongId_table+".insert");
			final List<Object[]> params = new ArrayList<Object[]>();
			bibsProcessedLongId.forEachEntry(new TLongLongProcedure() {
				public boolean execute(long key, long value) {
					params.add(new Object[] {key, value});
					return true;
				}
			});
			String sql =
				"insert ignore into "+bibsProcessedLongId_table+
				" (bib_001, record_id) "+
				"values (?,?) ;";
	        int[] updateCounts = this.simpleJdbcTemplate.batchUpdate(sql, params);
	        TimingLogger.stop(bibsProcessedLongId_table+".insert");
		}
	}
	
	public void loadBibMaps	(
			TLongLongHashMap bibsProcessedLongId, 
			Map<String, Long> bibsProcessedStringId,
			TLongLongHashMap bibsYet2ArriveLongId,
			Map<String, Long> bibsYet2ArriveStringId ) {

	}
	
	public List<Long> getLinkedRecordIds(Long toRecordId) {
		List<Long> linkedRecordsIds = new ArrayList<Long>();
		return linkedRecordsIds;
	}
	
	public void persistLinkedRecordIds(List<long[]> links) {
		
	}
	
	public void activateHeldHoldings(TLongArrayList manifestionIdsPreviouslyHeld) {
		
	}
}
