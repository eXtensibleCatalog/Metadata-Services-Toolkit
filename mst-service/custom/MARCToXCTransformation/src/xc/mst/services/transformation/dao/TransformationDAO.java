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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

import xc.mst.bo.record.RecordMessage;
import xc.mst.cache.DynMap;
import xc.mst.services.impl.dao.GenericMetadataServiceDAO;
import xc.mst.utils.MSTConfiguration;
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
			TLongLongHashMap bibsProcessedLongIdAdded, 
			Map<String, Long> bibsProcessedStringIdAdded,
			TLongLongHashMap bibsProcessedLongIdRemoved, 
			Map<String, Long> bibsProcessedStringIdRemoved,
			TLongLongHashMap bibsYet2ArriveLongIdAdded,
			Map<String, Long> bibsYet2ArriveStringIdAdded,
			TLongLongHashMap bibsYet2ArriveLongIdRemoved,
			Map<String, Long> bibsYet2ArriveStringIdRemoved ) {
		
		TimingLogger.start("TransformationDAO.persistBibMaps");
		
		Object[] objArr = new Object[] {
				bibsProcessedLongIdAdded, bibsProcessedLongId_table,
				bibsProcessedStringIdAdded, bibsProcessedStringId_table,
				bibsYet2ArriveLongIdAdded, bibsYet2ArriveLongId_table,
				bibsYet2ArriveStringIdAdded, bibsYet2ArriveStringId_table};
		for (int i=0; i<objArr.length; i+=2){
			try {
				if (objArr[i] == null) {
					continue;
				}
				String dbLoadFileStr = (MSTConfiguration.getUrlPath()+"/db_load.in").replace('\\', '/');
				final byte[] tabBytes = "\t".getBytes();
				final byte[] newLineBytes = "\n".getBytes();
				
				File dbLoadFile = new File(dbLoadFileStr);
				if (dbLoadFile.exists()) {
					dbLoadFile.delete();
				}
				final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
				final MutableInt j = new MutableInt(0);
				final String tableName = (String)objArr[i+1];
				TimingLogger.start(tableName+".insert");
				TimingLogger.start(tableName+".insert.create_infile");
				final List<Object[]> params = new ArrayList<Object[]>();
				if (objArr[i] instanceof TLongLongHashMap) {
					TLongLongHashMap longKeyedMap = (TLongLongHashMap)objArr[i];
					LOG.debug("insert: "+tableName+".size(): "+longKeyedMap.size());
					if (longKeyedMap != null && longKeyedMap.size() > 0) {
						longKeyedMap.forEachEntry(new TLongLongProcedure() {
							public boolean execute(long key, long value) {
								try {
									if (j.intValue() > 0) {
										LOG.debug("line break!!! j:"+j.intValue());
										os.write(newLineBytes);
									} else {
										j.increment();
									}
									os.write(String.valueOf(key).getBytes());
									os.write(tabBytes);
									os.write(String.valueOf(value).getBytes());
								} catch (Throwable t) {
									getUtil().throwIt(t);
								}
								return true;
							}
						});		
					}
				} else if (objArr[i] instanceof Map) {
					Map<String, Long> stringKeyedMap = (Map<String, Long>)objArr[i];
					LOG.debug("insert: "+tableName+".size(): "+stringKeyedMap.size());
					if (stringKeyedMap != null && stringKeyedMap.size() > 0) {
						for (Map.Entry<String, Long> me : stringKeyedMap.entrySet()) {
							if (j.intValue() > 0) {
								os.write(newLineBytes);
							} else {
								j.increment();
							}
							os.write(me.getKey().getBytes());
							os.write(tabBytes);
							os.write(String.valueOf(me.getValue()).getBytes());
						}
					}
				}
	
				os.close();
				TimingLogger.stop(tableName+".insert.create_infile");
				TimingLogger.start(tableName+".insert.load_infile");
				this.jdbcTemplate.execute(
						"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
						tableName+
						" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
						);
				TimingLogger.stop(tableName+".insert.load_infile");
				TimingLogger.stop(tableName+".insert");
			} catch (Throwable t) {
				getUtil().throwIt(t);
			}
		}
		
		objArr = new Object[] {
				bibsProcessedLongIdRemoved, bibsProcessedLongId_table,
				bibsProcessedStringIdRemoved, bibsProcessedStringId_table,
				bibsYet2ArriveLongIdRemoved, bibsYet2ArriveLongId_table,
				bibsYet2ArriveStringIdRemoved, bibsYet2ArriveStringId_table};
		for (int i=0; i<objArr.length; i+=2){
			if (objArr[i] == null) {
				continue;
			}
			String tableName = (String)objArr[i+1];
			final List<Object[]> params = new ArrayList<Object[]>();
			if (objArr[i] instanceof TLongLongHashMap) {
				TLongLongHashMap longKeyedMap = (TLongLongHashMap)objArr[i];
				LOG.debug("delete: "+tableName+".size(): "+longKeyedMap.size());
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
				LOG.debug("delete: "+tableName+".size(): "+stringKeyedMap.size());
				if (stringKeyedMap != null && stringKeyedMap.size() > 0) {
					for (Map.Entry<String, Long> me : stringKeyedMap.entrySet()) {
						params.add(new Object[] {me.getKey(), me.getValue()});
					}
				}
			}
			if (params.size() > 0) {
				TimingLogger.start(tableName+".delete");
				String sql =
					"delete from "+tableName+
					" where bib_001=? and record_id=?";
		        int[] updateCounts = this.simpleJdbcTemplate.batchUpdate(sql, params);
		        TimingLogger.stop(tableName+".delete");	
			}
		}
		
		TimingLogger.stop("TransformationDAO.persistBibMaps");
		
	}
	
	protected List<Map<String, Object>> getBibMaps(String tableName, int page) {
		TimingLogger.start("getBibMaps");
		int recordsAtOnce = 250000;
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
				"select bib_001, record_id from "+tableName+
				" limit "+(page*recordsAtOnce)+","+recordsAtOnce);
		TimingLogger.stop("getBibMaps");
		return rowList;
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
			
			int page = 0;
			List<Map<String, Object>> rowList = getBibMaps(tableName, page);
			while (rowList != null && rowList.size() > 0) {
				for (Map<String, Object> row : rowList) {
					TimingLogger.add(tableName, 0);
					if (objArr[i] instanceof TLongLongHashMap) {
						((TLongLongHashMap)objArr[i]).put((Long)row.get("bib_001"), (Long)row.get("record_id"));
					} else if (objArr[i] instanceof Map) {
						((Map)objArr[i]).put((String)row.get("bib_001"), (Long)row.get("record_id"));
					}
				}
				rowList = getBibMaps(tableName, ++page);
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
		TimingLogger.add("holdings2activate", holdings2activate.size());
		return holdings2activate;
	}
	
	public void persistHeldHoldings(final List<long[]> links) {
		String sql = "insert ignore into "+held_holdings_table+" (held_holding_id, manifestation_id) values (?,?)";
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
