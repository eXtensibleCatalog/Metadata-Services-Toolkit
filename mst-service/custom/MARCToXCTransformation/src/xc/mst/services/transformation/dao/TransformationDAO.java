/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.transformation.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.Marc001_003Holder;
import xc.mst.services.impl.dao.GenericMetadataServiceDAO;

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
    
    public void removeBibsForHoldings(String org_code, String holding_id) {
        this.jdbcTemplate.update("delete from " + bibRefs_table +
                " where org_code=? and holding_001=?",
                org_code, holding_id);
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
                        "select record_id from " + bibsYet2ArriveLongId_table +
                        " where org_code=? and bib_001=?", new Object[] { bib.get003(), longVal });
            } else {
            	rowList = this.jdbcTemplate.queryForList(
                        "select record_id from " + bibsYet2ArriveStringId_table +
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
                    " where org_code=? and bib_001=? and record_id=?",
                    bib.get003(), longVal, record_id);
                        
    	} catch (NumberFormatException nfe) {
    		String stringVal = bib.get001();
            
            this.jdbcTemplate.update("delete from " + bibsYet2ArriveStringId_table +
                    " where org_code=? and bib_001=?, record_id=?",
                    bib.get003(), stringVal, record_id);
            
    	}
    }
    
   
}
