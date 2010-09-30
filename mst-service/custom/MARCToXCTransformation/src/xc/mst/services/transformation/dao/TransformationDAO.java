/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.transformation.dao;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongLongProcedure;
import gnu.trove.TLongProcedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

import xc.mst.services.impl.dao.GenericMetadataServiceDAO;
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
	protected final static String held_holdings_table = "held_holdings";
	
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
	
	public TLongHashSet getHoldingIdsToActivate(final TLongHashSet manifestationIds) {
		final TLongHashSet holdings2activate = new TLongHashSet();
		if (manifestationIds != null && manifestationIds.size() > 0	) {
			TimingLogger.start(held_holdings_table+".getHoldingIdsToActivate");

			final TLongHashSet holdingsStillHeld = new TLongHashSet();
			final StringBuilder sb = new StringBuilder();
			sb.append(
					" select h2.held_holding_id, h2.manifestation_id "+
					" from "+held_holdings_table+" as h1, "+
						" "+held_holdings_table+" as h2 "+
					" where h1.held_holding_id = h2.held_holding_id " +
					"   and h1.manifestation_id in ( ");
			// Another option would be to add AND h2.manifestation_id NOT IN (?,?,...)
			for (int i=0; i<manifestationIds.size(); i++) {
				sb.append(" ?,");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			this.jdbcTemplate.query(
					new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection conn)
								throws SQLException {
							final PreparedStatement ps = conn.prepareStatement(sb.toString());
							TLongIterator it = manifestationIds.iterator();
							int i=1;
							while (it.hasNext()) {
								ps.setLong(i++, it.next());
							}
							return ps;
						}
					},
					new RowCallbackHandler() {
						public void processRow(ResultSet row) throws SQLException {
							Long heldHoldingId = (Long)row.getLong("h2.held_holding_id");
							Long manifestationId = (Long)row.getLong("h2.manifestation_id");
							holdings2activate.add(heldHoldingId);
							if (!manifestationIds.contains(manifestationId)) {
								holdingsStillHeld.add(heldHoldingId);
							}
						}
					});
			holdingsStillHeld.forEach(new TLongProcedure() {
				public boolean execute(long id) {
					holdings2activate.remove(id);
					return true;	
				}
			});
			TimingLogger.stop(held_holdings_table+".getHoldingIdsToActivate");
		}
		return holdings2activate;
	}
	
	public void persistHeldHoldings(final List<long[]> links) {
		String sql = "insert into "+held_holdings_table+" (held_holding_id, manifestation_id) values (?,?)";
		TimingLogger.start(held_holdings_table+".insert");
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
        TimingLogger.stop(held_holdings_table+".insert");
	}
	
	public void deleteHeldHoldings(final TLongHashSet manifestationIds) {
		TimingLogger.start("deleteHeldHoldings");
		String sql = "delete from held_holdings where manifestation_id = ?";
		final TLongIterator it = manifestationIds.iterator(); 
        int[] updateCounts = jdbcTemplate.batchUpdate(
        		sql,
        		new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int j) throws SQLException {
						ps.setLong(1, it.next());
					}
					public int getBatchSize() {
						return manifestationIds.size();
					}
				});
        TimingLogger.stop("deleteHeldHoldings");
	}
}
