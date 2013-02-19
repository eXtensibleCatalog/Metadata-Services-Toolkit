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
import gnu.trove.TLongLongIterator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

import xc.mst.bo.record.Marc001_003Holder;
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
    protected final static String holdingsProcessedLongId_table = "holdingsProcessedLongId";
    protected final static String holdingsProcessedStringId_table = "holdingsProcessedStringId";
    protected final static String bibsYet2ArriveLongId_table = "bibsYet2ArriveLongId";
    protected final static String bibsYet2ArriveStringId_table = "bibsYet2ArriveStringId";
    protected final static String bibRefs_table = "bibs_to_holdings";
    protected final static String held_holdings_table = "held_holdings";

    @SuppressWarnings("unchecked")
    public void persistBibMaps(
            Map<String, TLongLongHashMap> bibsProcessedLongIdAddedMap,
            Map<String, Map<String, Long>> bibsProcessedStringIdAddedMap,
            Map<String, TLongLongHashMap> bibsProcessedLongIdRemovedMap,
            Map<String, Map<String, Long>> bibsProcessedStringIdRemovedMap,
            Map<String, TLongLongHashMap> holdingsProcessedLongIdAddedMap,
            Map<String, Map<String, Long>> holdingsProcessedStringIdAddedMap,
            Map<String, TLongLongHashMap> holdingsProcessedLongIdRemovedMap,
            Map<String, Map<String, Long>> holdingsProcessedStringIdRemovedMap) {

        TimingLogger.start("TransformationDAO.persistBibMaps");

        Object[] objArr = new Object[] {
                bibsProcessedLongIdAddedMap, bibsProcessedLongId_table,
                bibsProcessedStringIdAddedMap, bibsProcessedStringId_table,
                holdingsProcessedLongIdAddedMap, holdingsProcessedLongId_table,
                holdingsProcessedStringIdAddedMap, holdingsProcessedStringId_table};
        for (int i = 0; i < objArr.length; i += 2) {

            for (Object keyObj : ((Map) objArr[i]).keySet()) {
                String orgCode = (String) keyObj;
                Object map = ((Map) objArr[i]).get(orgCode);

                try {
                    if (map == null) {
                        continue;
                    }
                    final byte[] orgCodeBytes = orgCode.getBytes("UTF-8");
                    String dbLoadFileStr = (MSTConfiguration.getUrlPath() + "/db_load.in").replace('\\', '/');
                    final byte[] tabBytes = "\t".getBytes();
                    final byte[] newLineBytes = "\n".getBytes();

                    File dbLoadFile = new File(dbLoadFileStr);
                    if (dbLoadFile.exists()) {
                        dbLoadFile.delete();
                    }
                    final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
                    final MutableInt j = new MutableInt(0);
                    final String tableName = (String) objArr[i + 1];
                    TimingLogger.start(tableName + ".insert");
                    TimingLogger.start(tableName + ".insert.create_infile");
                    if (map instanceof TLongLongHashMap) {
                        TLongLongHashMap longKeyedMap = (TLongLongHashMap) map;
                        LOG.debug("insert: " + tableName + ".size(): " + longKeyedMap.size());
                        if (longKeyedMap != null && longKeyedMap.size() > 0) {
                            longKeyedMap.forEachEntry(new TLongLongProcedure() {
                                public boolean execute(long key, long value) {
                                    try {
                                        if (j.intValue() > 0) {
                                            LOG.debug("line break!!! j:" + j.intValue());
                                            os.write(newLineBytes);
                                        } else {
                                            j.increment();
                                        }
                                        os.write(orgCodeBytes);
                                        os.write(tabBytes);
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
                    } else if (map instanceof Map) {
                        Map<String, Long> stringKeyedMap = (Map<String, Long>) map;
                        LOG.debug("insert: " + tableName + ".size(): " + stringKeyedMap.size());
                        if (stringKeyedMap != null && stringKeyedMap.size() > 0) {
                            for (Map.Entry<String, Long> me : stringKeyedMap.entrySet()) {
                                if (j.intValue() > 0) {
                                    os.write(newLineBytes);
                                } else {
                                    j.increment();
                                }
                                try {   // till we fix up our code me data can be null
                                    os.write(orgCodeBytes);
                                    os.write(tabBytes);
                                    os.write(me.getKey().getBytes());
                                    os.write(tabBytes);
                                    os.write(String.valueOf(me.getValue()).getBytes());
                                } catch (Exception e) {
                                    // TODO test whether catching this too early or whether we need to NOT REPLACE into table if
                                    //       we get the exception here.
                                    LOG.error("problem with data - ",e);
                                }
                            }
                        }
                    }

                    os.close();
                    TimingLogger.stop(tableName + ".insert.create_infile");
                    TimingLogger.start(tableName + ".insert.load_infile");
                    this.jdbcTemplate.execute(
                            "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
                                    tableName +
                                    " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
                            );
                    TimingLogger.stop(tableName + ".insert.load_infile");
                    TimingLogger.stop(tableName + ".insert");
                } catch (Throwable t) {
                    getUtil().throwIt(t);
                }
            }
        }

        objArr = new Object[] {
                bibsProcessedLongIdRemovedMap, bibsProcessedLongId_table,
                bibsProcessedStringIdRemovedMap, bibsProcessedStringId_table,
                holdingsProcessedLongIdRemovedMap, holdingsProcessedLongId_table,
                holdingsProcessedStringIdRemovedMap, holdingsProcessedStringId_table};
        for (int i = 0; i < objArr.length; i += 2) {
            for (Object keyObj : ((Map) objArr[i]).keySet()) {
                final String orgCode = (String) keyObj;
                Object map = ((Map) objArr[i]).get(orgCode);

                if (map == null) {
                    continue;
                }
                String tableName = (String) objArr[i + 1];
                final List<Object[]> params = new ArrayList<Object[]>();
                if (map instanceof TLongLongHashMap) {
                    TLongLongHashMap longKeyedMap = (TLongLongHashMap) map;
                    LOG.debug("delete: " + tableName + ".size(): " + longKeyedMap.size());
                    if (longKeyedMap != null && longKeyedMap.size() > 0) {
                        longKeyedMap.forEachEntry(new TLongLongProcedure() {
                            public boolean execute(long key, long value) {
                                params.add(new Object[] { key, value, orgCode });
                                return true;
                            }
                        });
                    }
                } else if (map instanceof Map) {
                    Map<String, Long> stringKeyedMap = (Map<String, Long>) map;
                    LOG.debug("delete: " + tableName + ".size(): " + stringKeyedMap.size());
                    if (stringKeyedMap != null && stringKeyedMap.size() > 0) {
                        for (Map.Entry<String, Long> me : stringKeyedMap.entrySet()) {
                            params.add(new Object[] { me.getKey(), me.getValue(), orgCode });
                        }
                    }
                }
                if (params.size() > 0) {
                    TimingLogger.start(tableName + ".delete");
                    String sql =
                            "delete from " + tableName +
                                    " where bib_001=? and record_id=? and org_code=?";
                    int[] updateCounts = this.simpleJdbcTemplate.batchUpdate(sql, params);
                    TimingLogger.stop(tableName + ".delete");
                }
            }
        }

        TimingLogger.stop("TransformationDAO.persistBibMaps");

    }
    
    public void removeRecordId4BibProcessed(Long l) {
        String sql =
                "delete from " + bibsProcessedLongId_table +
                        " where record_id=?";
        this.jdbcTemplate.update(sql, l);
        
        sql =
                "delete from " + bibsProcessedStringId_table +
                        " where record_id=?";
        
        this.jdbcTemplate.update(sql, l);        
    }

    @SuppressWarnings("unchecked")
    public void persistBibsYet2Arrive(
            Map<String, Map<Long, List<Long>>> bibsYet2ArriveLongIdAddedMap,
            Map<String, Map<String, List<Long>>> bibsYet2ArriveStringIdAddedMap,
            Map<String, Map<Long, List<Long>>> bibsYet2ArriveLongIdRemovedMap,
            Map<String, Map<String, List<Long>>> bibsYet2ArriveStringIdRemovedMap) {

        TimingLogger.start("TransformationDAO.persistBibMaps");

        Object[] objArr = new Object[] {
                bibsYet2ArriveLongIdAddedMap, bibsYet2ArriveLongId_table,
                bibsYet2ArriveStringIdAddedMap, bibsYet2ArriveStringId_table };
        for (int i = 0; i < objArr.length; i += 2) {

            for (Object keyObj : ((Map) objArr[i]).keySet()) {
                String orgCode = (String) keyObj;
                
                Object map = ((Map) objArr[i]).get(orgCode);

                try {
                    if (map == null) {
                        continue;
                    }
                    final byte[] orgCodeBytes = orgCode.getBytes("UTF-8");
                    String dbLoadFileStr = (MSTConfiguration.getUrlPath() + "/db_load.in").replace('\\', '/');
                    final byte[] tabBytes = "\t".getBytes();
                    final byte[] newLineBytes = "\n".getBytes();

                    File dbLoadFile = new File(dbLoadFileStr);
                    if (dbLoadFile.exists()) {
                        dbLoadFile.delete();
                    }
                    final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
                    final MutableInt j = new MutableInt(0);
                    final String tableName = (String) objArr[i + 1];
                    TimingLogger.start(tableName + ".insert");
                    TimingLogger.start(tableName + ".insert.create_infile");
                    if (i % 2 == 0) {
                    	Map<Long, List<Long>> longKeyedMap = (Map<Long, List<Long>>) map;
                        LOG.debug("insert: " + tableName + ".size(): " + longKeyedMap.size());
                        for (Long bib_id : longKeyedMap.keySet()) {
                        	for (Long record_id : longKeyedMap.get(bib_id)) {
                                if (j.intValue() > 0) {
                                    os.write(newLineBytes);
                                } else {
                                    j.increment();
                                }
                                try {   // till we fix up our code me data can be null
                                    os.write(orgCodeBytes);
                                    os.write(tabBytes);
                                    os.write(String.valueOf(bib_id).getBytes());
                                    os.write(tabBytes);
                                    os.write(String.valueOf(record_id).getBytes());
                                } catch (Exception e) {
                                    // TODO test whether catching this too early or whether we need to NOT REPLACE into table if
                                    //       we get the exception here.
                                    LOG.error("problem with data - ",e);
                                }
                        	}
                        }
                    } else {
                        Map<String, List<Long>> stringKeyedMap = (Map<String, List<Long>>) map;
                        LOG.debug("insert: " + tableName + ".size(): " + stringKeyedMap.size());
                        for (String bib_id : stringKeyedMap.keySet()) {
                        	for (Long record_id : stringKeyedMap.get(bib_id)) {
                                if (j.intValue() > 0) {
                                    os.write(newLineBytes);
                                } else {
                                    j.increment();
                                }
                                try {   // till we fix up our code me data can be null
                                    os.write(orgCodeBytes);
                                    os.write(tabBytes);
                                    os.write(bib_id.getBytes());
                                    os.write(tabBytes);
                                    os.write(String.valueOf(record_id).getBytes());
                                } catch (Exception e) {
                                    // TODO test whether catching this too early or whether we need to NOT REPLACE into table if
                                    //       we get the exception here.
                                    LOG.error("problem with data - ",e);
                                }
                        	}
                        }
                    }

                    os.close();
                    TimingLogger.stop(tableName + ".insert.create_infile");
                    TimingLogger.start(tableName + ".insert.load_infile");
                    this.jdbcTemplate.execute(
                            "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
                                    tableName +
                                    " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
                            );
                    TimingLogger.stop(tableName + ".insert.load_infile");
                    TimingLogger.stop(tableName + ".insert");
                } catch (Throwable t) {
                    getUtil().throwIt(t);
                }
            }
        }

        objArr = new Object[] {
                bibsYet2ArriveLongIdRemovedMap, bibsYet2ArriveLongId_table,
                bibsYet2ArriveStringIdRemovedMap, bibsYet2ArriveStringId_table };
        for (int i = 0; i < objArr.length; i += 2) {
            for (Object keyObj : ((Map) objArr[i]).keySet()) {
                final String orgCode = (String) keyObj;
                Object map = ((Map) objArr[i]).get(orgCode);

                if (map == null) {
                    continue;
                }
                String tableName = (String) objArr[i + 1];
                final List<Object[]> params = new ArrayList<Object[]>();
                if (i % 2 == 0) {
                    Map<Long, List<Long>> longKeyedMap = (Map<Long, List<Long>>) map;
                    LOG.debug("insert: " + tableName + ".size(): " + longKeyedMap.size());
                    for (Long bib_id : longKeyedMap.keySet()) {
                    	for (Long record_id : longKeyedMap.get(bib_id)) {
                            params.add(new Object[] { bib_id, record_id, orgCode });
                        }
                    }
                } else {
                    Map<String, List<Long>> stringKeyedMap = (Map<String, List<Long>>) map;
                    LOG.debug("insert: " + tableName + ".size(): " + stringKeyedMap.size());
                    for (String bib_id : stringKeyedMap.keySet()) {
                    	for (Long record_id : stringKeyedMap.get(bib_id)) {
                            params.add(new Object[] { bib_id, record_id, orgCode });
                        }
                    }
                }
                if (params.size() > 0) {
                    TimingLogger.start(tableName + ".delete");
                    String sql =
                            "delete from " + tableName +
                                    " where bib_001=? and record_id=? and org_code=?";
                    int[] updateCounts = this.simpleJdbcTemplate.batchUpdate(sql, params);
                    TimingLogger.stop(tableName + ".delete");
                }
            }
        }

        TimingLogger.stop("TransformationDAO.persistBibMaps");

    }


    @SuppressWarnings("unchecked")
    public void persistBibRefs(
            Map<String, Map<String, List<String>>> bibsToHoldingsAddedMap,
            Map<String, Map<String, List<String>>> bibsToHoldingsRemovedMap) {

        TimingLogger.start("TransformationDAO.persistBibRefs");

        for (String orgCode : bibsToHoldingsAddedMap.keySet()) {
            Map<String, List<String>> map = bibsToHoldingsAddedMap.get(orgCode);

            if (map == null) {
                continue;
            }

            if (map.size() > 0) {

            	try {
	                final byte[] orgCodeBytes = orgCode.getBytes("UTF-8");
	                String dbLoadFileStr = (MSTConfiguration.getUrlPath() + "/db_load.in").replace('\\', '/');
	                final byte[] tabBytes = "\t".getBytes();
	                final byte[] newLineBytes = "\n".getBytes();
	
	                File dbLoadFile = new File(dbLoadFileStr);
	                if (dbLoadFile.exists()) {
	                    dbLoadFile.delete();
	                }
	                final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
	                final MutableInt j = new MutableInt(0);
	                final String tableName = bibRefs_table;
	                TimingLogger.start(tableName + ".insert");
	                TimingLogger.start(tableName + ".insert.create_infile");
	                
	                for (String bib_001 : map.keySet()) {
	                	
	                	List<String> list = map.get(bib_001);
	                
	                    for (String holding_001 : list) {
	                        if (j.intValue() > 0) {
	                            os.write(newLineBytes);
	                        } else {
	                            j.increment();
	                        }
	                        try {
	                            os.write(orgCodeBytes);
	                            os.write(tabBytes);
	                            os.write(bib_001.getBytes());
	                            os.write(tabBytes);
	                            os.write(holding_001.getBytes());
	                        } catch (Exception e) {
	                            // TODO test whether catching this too early or whether we need to NOT REPLACE into table if
	                            //       we get the exception here.
	                            LOG.error("problem with data - ",e);
	                        }
	                    }
	                    
	                }
	
	                os.close();
	                TimingLogger.stop(tableName + ".insert.create_infile");
	                TimingLogger.start(tableName + ".insert.load_infile");
	                this.jdbcTemplate.execute(
	                        "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
	                                tableName +
	                                " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
	                        );
	                TimingLogger.stop(tableName + ".insert.load_infile");
	                TimingLogger.stop(tableName + ".insert");
	            } catch (Throwable t) {
	                getUtil().throwIt(t);
	            }
            
        }

    }


    for (String orgCode : bibsToHoldingsRemovedMap.keySet()) {
            Map<String, List<String>> map = bibsToHoldingsRemovedMap.get(orgCode);

            if (map == null) {
                continue;
            }
            final String tableName = bibRefs_table;
            final List<Object[]> params = new ArrayList<Object[]>();

            LOG.debug("delete: " + tableName + ".size(): " + map.size());
            if (map.size() > 0) {                	
                for (String bib_001 : map.keySet()) {
                	List<String> list = map.get(bib_001);
                	for (String holding_001 : list) {
                    	params.add(new Object[] { bib_001, holding_001, orgCode });
                    }
                }
            }
            
            if (params.size() > 0) {
                TimingLogger.start(tableName + ".delete");
                String sql =
                        "delete from " + tableName +
                                " where bib_001=? and holding_001=? and org_code=?";
                int[] updateCounts = this.simpleJdbcTemplate.batchUpdate(sql, params);
                TimingLogger.stop(tableName + ".delete");
            }
        }

        TimingLogger.stop("TransformationDAO.persistBibRefs");

    }
    
    
    
    protected List<Map<String, Object>> getBibMaps(String tableName, int page) {
        TimingLogger.start("getBibMaps");
        int recordsAtOnce = 250000;
        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
                "select org_code, bib_001, record_id from " + tableName +
                        " limit " + (page * recordsAtOnce) + "," + recordsAtOnce);
        TimingLogger.stop("getBibMaps");
        return rowList;
    }
    
    public List<Marc001_003Holder> getHoldingMarcId4RecordIdProcessed (long l) {
    	List<Marc001_003Holder> results = new ArrayList<Marc001_003Holder>();
    	    	
        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
                "select org_code, bib_001 from " + holdingsProcessedLongId_table +
                        " where record_id=?", new Object[] { l });
        
        if (rowList != null && rowList.size() > 0) {
            for (Map<String, Object> row : rowList) {
            	String org_code = (String) row.get("org_code");
                Long bib_001 = (Long) row.get("bib_001");
                results.add(new Marc001_003Holder(bib_001.toString(), org_code));
            }
        }
        
        rowList = this.jdbcTemplate.queryForList(
                "select org_code, bib_001 from " + holdingsProcessedStringId_table +
                        " where record_id=?", new Object[] { l });
        
        if (rowList != null && rowList.size() > 0) {
            for (Map<String, Object> row : rowList) {
            	String org_code = (String) row.get("org_code");
                String bib_001 = (String) row.get("bib_001");
                results.add(new Marc001_003Holder(bib_001, org_code));
            }
        }
    	
    	return results;
    }
    
    public List<String> getBibsForHoldings(Marc001_003Holder holding) {
    	List<String> results = new ArrayList<String>();
    	
        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
                "select bib_001 from " + bibRefs_table +
                        " where org_code=? and holding_001=?", new Object[] { holding.get003(), holding.get001() });
        
        if (rowList != null && rowList.size() > 0) {
            for (Map<String, Object> row : rowList) {
                results.add((String) row.get("bib_001"));
            }
        } 
        
        return results;
    }
    
    public List<String> getHoldingsForBib(Marc001_003Holder bib) {
    	List<String> results = new ArrayList<String>();
    	
        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
                "select holding_001 from " + bibRefs_table +
                        " where org_code=? and bib_001=?", new Object[] { bib.get003(), bib.get001() });
        
        if (rowList != null && rowList.size() > 0) {
            for (Map<String, Object> row : rowList) {
                results.add((String) row.get("holding_001"));
            }
        } 
        
        return results;    	
    }
    
    public void addBibforHolding(String org_code, String holding_id, String bib_id) {
        this.jdbcTemplate.update("replace into " + bibRefs_table +
                "(org_code, holding_001, bib_001) values (?, ?, ?)",
                org_code, holding_id, bib_id);
    }
    
    public void removeBibsForHoldings(Marc001_003Holder holding) {
        this.jdbcTemplate.update("delete from " + bibRefs_table +
                "where org_code=?, bib_001=?",
                holding.get003(), holding.get001());
    }

    public List<Long> getRecordId4BibProcessed(Marc001_003Holder bib) {
    	List<Long> results = new ArrayList<Long>();
    	
    	try {
    		long longVal = Integer.parseInt(bib.get001());
            List<Map<String, Object>> rowList;
            
            rowList= this.jdbcTemplate.queryForList(
                    "select record_id from " + bibsProcessedLongId_table +
                            " where bib_001=? and org_code=?", new Object[] { longVal, bib.get003() });
            
            if (rowList != null && rowList.size() > 0) {
                for (Map<String, Object> row : rowList) {
                    results.add((Long) row.get("record_id"));
                }
            } 
            
    	} catch (NumberFormatException nfe) {
    		String stringVal = bib.get001();
    		List<Map<String, Object>> rowList;
            
            rowList= this.jdbcTemplate.queryForList(
                    "select record_id from " + bibsProcessedStringId_table +
                            " where bib_001=? and org_code=?", new Object[] { stringVal, bib.get003() });
            
            if (rowList != null && rowList.size() > 0) {
                for (Map<String, Object> row : rowList) {
                    results.add((Long) row.get("record_id"));
                }
            } 
    	}

        return results;
    }
    
    public void addRecordId4BibProcessed(Marc001_003Holder bib, long record_id) {
    	try {
    		long longVal = Integer.parseInt(bib.get001());
            
            this.jdbcTemplate.update("replace into " + bibsProcessedLongId_table +
                    "(org_code, bib_001, record_id) values (?, ?, ?)",
                    bib.get003(), longVal, record_id);
                        
    	} catch (NumberFormatException nfe) {
    		String stringVal = bib.get001();
            
            this.jdbcTemplate.update("replace into " + bibsProcessedStringId_table +
                    "(org_code, bib_001, record_id) values (?, ?, ?)",
                    bib.get003(), stringVal, record_id);
            
    	}
    }
    
    public void addRecordId4HoldingProcessed(Marc001_003Holder hold, long record_id) {
    	try {
    		long longVal = Integer.parseInt(hold.get001());
            
            this.jdbcTemplate.update("replace into " + holdingsProcessedLongId_table +
                    "(org_code, bib_001, record_id) values (?, ?, ?)",
                    hold.get003(), longVal, record_id);
                        
    	} catch (NumberFormatException nfe) {
    		String stringVal = hold.get001();
            
            this.jdbcTemplate.update("replace into " + holdingsProcessedStringId_table +
                    "(org_code, bib_001, record_id) values (?, ?, ?)",
                    hold.get003(), stringVal, record_id);
            
    	}
    	
    }
    

    
    public void removeRecordId4HoldingProcessed(long record_id) {
        String sql =
                "delete from " + holdingsProcessedLongId_table +
                        " where record_id=?";
        this.jdbcTemplate.update(sql, record_id);
        
        sql =
                "delete from " + holdingsProcessedStringId_table +
                        " where record_id=?";
        
        this.jdbcTemplate.update(sql, record_id);

    }
    
    public void addManifestationId4BibYet2Arrive(Marc001_003Holder bib, long record_id) {
    	try {
    		long longVal = Integer.parseInt(bib.get001());
            
            this.jdbcTemplate.update("replace into " + bibsYet2ArriveLongId_table +
                    "(org_code, bib_001, record_id) values (?, ?, ?)",
                    bib.get003(), longVal, record_id);
                        
    	} catch (NumberFormatException nfe) {
    		String stringVal = bib.get001();
            
            this.jdbcTemplate.update("replace into " + bibsYet2ArriveStringId_table +
                    "(org_code, bib_001, record_id) values (?, ?, ?)",
                    bib.get003(), stringVal, record_id);
            
    	}
    }
    
    public List<Long> getManifestationId4BibYet2Arrive(Marc001_003Holder bib) {
        	List<Long> results = new ArrayList<Long>();
        	boolean isLong = true;

        	long longVal = -1;
        	try {
            	longVal = Long.parseLong(bib.get001());
        	} catch (NumberFormatException nfe) {
        		isLong = false;
        	}
        	
            List<Map<String, Object>> rowList;
            
            if (isLong) {
            	rowList = this.jdbcTemplate.queryForList(
                        "select record_id from " + holdingsProcessedLongId_table +
                        " where org_code=? and bib_001=?", new Object[] { bib.get003(), longVal });
            } else {
            	rowList = this.jdbcTemplate.queryForList(
                        "select record_id from " + holdingsProcessedStringId_table +
                        " where org_code=? and bib_001=?", new Object[] { bib.get003(), bib.get001() });            	
            }
        	            
            if (rowList != null && rowList.size() > 0) {
                for (Map<String, Object> row : rowList) {
                    results.add((Long) row.get("record_id"));
                }
            }
        	return results;	
    }

    public void removeManifestationId4BibYet2Arrive(Marc001_003Holder bib, long record_id) {
    	try {
    		long longVal = Integer.parseInt(bib.get001());
            
            this.jdbcTemplate.update("delete from " + bibsYet2ArriveLongId_table +
                    "where org_code=? and bib_001=? and record_id=?",
                    bib.get003(), longVal, record_id);
                        
    	} catch (NumberFormatException nfe) {
    		String stringVal = bib.get001();
            
            this.jdbcTemplate.update("delete from " + bibsYet2ArriveStringId_table +
                    "where org_code=? and bib_001=?, record_id=?",
                    bib.get003(), stringVal, record_id);
            
    	}
    }
    
    protected List<Map<String, Object>> getBibRefs(String tableName, int page) {
        TimingLogger.start("getBibRefs");
        int recordsAtOnce = 250000;
        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
                "select org_code, bib_001, holding_001 from " + tableName +
                        " limit " + (page * recordsAtOnce) + "," + recordsAtOnce);
        TimingLogger.stop("getBibRefs");
        return rowList;
    }

    
    @SuppressWarnings("unchecked")
    public void loadBibMaps(
            Map<String, TLongLongHashMap> bibsProcessedLongIdMap,
            Map<String, Map<String, Long>> bibsProcessedStringIdMap,
            Map<String, TLongLongHashMap> holdingsProcessedLongIdMap,
            Map<String, Map<String, Long>> holdingsProcessedStringIdMap) {

        Object[] objArr = new Object[] {
                bibsProcessedLongIdMap, bibsProcessedLongId_table,
                bibsProcessedStringIdMap, bibsProcessedStringId_table,
                holdingsProcessedLongIdMap, holdingsProcessedLongId_table,
                holdingsProcessedStringIdMap, holdingsProcessedStringId_table};
        for (int i = 0; i < objArr.length; i += 2) {
            String tableName = (String) objArr[i + 1];
            Map m1 = ((Map) objArr[i]);
            int page = 0;
            List<Map<String, Object>> rowList = getBibMaps(tableName, page);
            while (rowList != null && rowList.size() > 0) {
                for (Map<String, Object> row : rowList) {
                    String orgCode = (String) row.get("org_code");
                    TimingLogger.add(tableName, 0);
                    if (i % 4 == 0) {
                    	Long bib_001 = (Long) row.get("bib_001");
                    	Long record_id = (Long) row.get("record_id");
                        TLongLongHashMap m2 = (TLongLongHashMap) m1.get(orgCode);
                        if (m2 == null) {
                            m2 = new TLongLongHashMap();
                            m1.put(orgCode, m2);
                        }
                        m2.put(bib_001, record_id);
                    } else {
                    	String bib_001 = (String) row.get("bib_001");
                    	Long record_id = (Long) row.get("record_id");
                        Map<String, Long> m2 = (Map) m1.get(orgCode);
                        if (m2 == null) {
                            m2 = new HashMap<String, Long>();
                            m1.put(orgCode, m2);
                        }
                        m2.put(bib_001, record_id);                    		
                    }
                }
                rowList = getBibMaps(tableName, ++page);
            }
        }
    }

    public void loadBibsYet2Arrive(
            Map<String, Map<Long, List<Long>>> bibsYet2ArriveLongIdMap,
            Map<String, Map<String, List<Long>>> bibsYet2ArriveStringIdMap) {

        Object[] objArr = new Object[] {
                bibsYet2ArriveLongIdMap, bibsYet2ArriveLongId_table,
                bibsYet2ArriveStringIdMap, bibsYet2ArriveStringId_table };
        for (int i = 0; i < objArr.length; i += 2) {
            String tableName = (String) objArr[i + 1];
            Map m1 = ((Map) objArr[i]);
            int page = 0;
            List<Map<String, Object>> rowList = getBibMaps(tableName, page);
            while (rowList != null && rowList.size() > 0) {
                for (Map<String, Object> row : rowList) {
                    String orgCode = (String) row.get("org_code");
                    TimingLogger.add(tableName, 0);
                    if (i % 2 == 0) {
                    	Long bib_001 = (Long) row.get("bib_001");
                    	Long record_id = (Long) row.get("record_id");
                		Map<Long, List<Long>> m2 = (Map) m1.get(orgCode);
                		if (m2 == null) {
                			m2 = new HashMap<Long, List<Long>>();
                			m1.put(orgCode, m2);
                		}
                		List<Long> l = m2.get(bib_001);
                		if (l == null) {
                			l = new ArrayList<Long>();
                			m2.put(bib_001, l);
                		}
                		l.add(record_id);
                    } else {
                    	String bib_001 = (String) row.get("bib_001");
                    	Long record_id = (Long) row.get("record_id");
                		Map<String, List<Long>> m2 = (Map) m1.get(orgCode);
                		if (m2 == null) {
                			m2 = new HashMap<String, List<Long>>();
                			m1.put(orgCode, m2);
                		}
                		List<Long> l = m2.get(bib_001);
                		if (l == null) {
                			l = new ArrayList<Long>();
                			m2.put(bib_001, l);
                		}
                		l.add(record_id);
                    }
                }
                rowList = getBibMaps(tableName, ++page);
            }
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public void loadBibRefs(
        Map<String, Map<String, List<String>>> bibsToHoldings, 
        Map<String, Map<String, List<String>>> holdingsToBibs) {

        final String tableName = bibRefs_table;
        int page = 0;
        List<Map<String, Object>> rowList = getBibRefs(tableName, page);
        while (rowList != null && rowList.size() > 0) {
            for (Map<String, Object> row : rowList) {
                TimingLogger.add(tableName, 0);
            	final String orgCode = (String) row.get("org_code");
                final String bib_001 = (String) row.get("bib_001");
                final String holding_001 = (String)row.get("holding_001");

                Map<String, List<String>> m2 = bibsToHoldings.get(orgCode);
                if (m2 == null) {
                    m2 = new HashMap<String, List<String>>();
                    bibsToHoldings.put(orgCode, m2);
                }
                List<String> list2 = m2.get(bib_001);
                if (list2 == null) {
                	list2 = new ArrayList<String>();
                	m2.put(bib_001, list2);
                }
                list2.add(holding_001);
                
                Map<String, List<String>> m3 = holdingsToBibs.get(orgCode);
                if (m3 == null) {
                    m3 = new HashMap<String, List<String>>();
                    holdingsToBibs.put(orgCode, m3);
                }
                List<String> list3 = m3.get(holding_001);
                if (list3 == null) {
                	list3 = new ArrayList<String>();
                	m3.put(holding_001, list3);
                }
                list3.add(bib_001);
                
            }
            rowList = getBibRefs(tableName, ++page);
        }
    }
    
   
}
