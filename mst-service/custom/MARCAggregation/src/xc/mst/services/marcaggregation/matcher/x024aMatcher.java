package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.utils.Util;

/**
 *
 * The Library of Congress Control Number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd024.html">MARC 024 field</a>
 *
 * Other Standard Identifier: 024a2 (Note: 1st indicator must also match.
 * If 1st indicator value is 7, then contents of Subfield 2 must also match)
 *
 * 024$a
 *
 * @author John Brand
 *
 */
public class x024aMatcher extends FieldMatcherService {

    // you can have multiple 024$a fields within a record (mult 024, each w/1 $a)
    protected Map<Long, List<String>> inputId2x024a = new HashMap<Long, List<String>>();

    // multiple records might have the same normalized 024$a, this would be an indication of a match
    protected Map<String, List<Long>> x024a2inputIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(x024aMatcher.class);
    private boolean debug = false;
    
    MarcAggregationService mas = null;
    
    private MarcAggregationService getMAS() {
		if (mas == null) {
			mas = (MarcAggregationService)config.getBean("MarcAggregationService");
		}
		return mas;
	}


    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        MarcAggregationServiceDAO masDao = getMAS().getMarcAggregationServiceDAO();

        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(24);

        final Long id = new Long(ir.recordId);
        
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');

            for (String subfield : subfields) {
                if (StringUtils.isNotEmpty(subfield)) {
                    String goods = getFieldDataIntoCorrectFormat(field, subfield);
                    List<Long> m = x024a2inputIds.get(goods);
                    if (m != null && m.size() > 0) {
                        results.addAll(m);
                        if (results.contains(id)) {
                            results.remove(id);
                        }
                    }

                    // now look in the database too!
                    //mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_022a where string_id = "24094664" '
                    List<Long> records = masDao.getMatchingRecords(MarcAggregationServiceDAO.matchpoints_024a_table, MarcAggregationServiceDAO.input_record_id_field,MarcAggregationServiceDAO.string_id_field,goods);
                    LOG.debug("024$a, DAO, getMatching records for "+goods+", numResults="+records.size());
                    for (Long record: records) {
                        if (!record.equals(id)) {
                            if (!results.contains(record)) {
                                results.add(record);
                                LOG.debug("**024$a, DAO,  record id: "+record +" matches id "+id);
                            }
                        }
                    }
                }
            }
        }
        LOG.debug("getMatchingInputIds, irId=" + ir.recordId + " results.size=" + results.size());
        return results;
    }

    @Override
    /**
     * when a record is updated/deleted, need to use this to
     */
    public void removeRecordFromMatcher(InputRecord ir) {

        Long id   = new Long(ir.getId());
        List<String> x024s = inputId2x024a.get(id);
        if (x024s != null) {
            for (String x024: x024s) {
                List<Long> inputIds = x024a2inputIds.get(x024);
                if (inputIds != null) {
                    inputIds.remove(id);
                    if (inputIds.size() > 0) {
                        x024a2inputIds.put(x024, inputIds);
                    }
                    else {
                        x024a2inputIds.remove(x024);
                    }
                }
            }
        }
        inputId2x024a.remove(id);

        // keep database in sync.  Don't worry about the one-off performance hit...yet.
        getMAS().getMarcAggregationServiceDAO().deleteMergeRow(MarcAggregationServiceDAO.matchpoints_024a_table, id);
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir) {
        List<Field> fields = r.getDataFields(24);

        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                LOG.debug("here we go, processing subfield: "+subfield+" recordId:"+id+" numSubfields="+subfields.size()+ "numFields="+fields.size());
                if (debug) {
                    Util.getUtil().printStackTrace("who got me here?");
                }
                List<String> list = inputId2x024a.get(id);
                String goods = getFieldDataIntoCorrectFormat(field, subfield);
                
                if (StringUtils.isEmpty(goods)) {
                    LOG.error("** problem with 024$a in: " + r.recordId);
                    continue;   // bad data will cause trouble up the road.                	
                }

                if (list == null) {
                    list = new ArrayList<String>();
                    list.add(goods);
                    inputId2x024a.put(id, list);
                    LOG.debug("*** 1.adding to recordId2x024a, for id: " + id + " for x024$a: " + goods);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                // TODO fix that!
                else if (list.contains(goods)) {
                    LOG.debug("** We have already seen x024$a " + goods + " for recordId: " + r.recordId);
                }
                else {
                    list.add(goods);
                    inputId2x024a.put(id, list);
                    LOG.debug("*** 2.adding to recordId2x024a, for id: " + id + " for x024$a: " + goods);
                }

                List<Long> ids = x024a2inputIds.get(goods);
                if (ids == null) {
                    ids = new ArrayList<Long>();
                }
                if (!ids.contains(r.recordId)) {
                    ids.add(r.recordId);
                    x024a2inputIds.put(goods, ids);
                    LOG.debug("*** adding to x024a2inputIds, for x024$a: " + goods);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else {
                    LOG.debug("** We have already seen recordId: " + r.recordId);
                }
            }
        }
    }

    private String getFieldDataIntoCorrectFormat(Field field, String goods) {
        goods = goods.trim();
        char indicator = SaxMarcXmlRecord.getIndicatorOfField(field, 1);
        goods = goods + indicator;  // thank you java
        if (indicator == '7') {
            goods += SaxMarcXmlRecord.getSubfieldOfField(field, '2'); //thanks again!
        }
        return goods;
    }
    
    public boolean matchpointsHaveChanged(SaxMarcXmlRecord r, InputRecord ir) {
    	LOG.debug("x024a matchpointsHaveChanged? ID: " + ir.getId()); 
    	Map<Long, List<String>> cachedListId2x024a = getMAS().getMarcAggregationServiceDAO().get1StrMatchpointsRecordsCache(Long.valueOf(ir.getId()), MarcAggregationServiceDAO.matchpoints_024a_table);
    	LOG.debug("cachedListId2x024a: " + cachedListId2x024a);    	
    	    	
    	List<String> cachedId2x024a = new ArrayList<String>();
    	if (cachedListId2x024a.containsKey(ir.getId())) {
    		cachedId2x024a = cachedListId2x024a.get(ir.getId());
    		LOG.debug("cachedId2x024a: " + cachedId2x024a);  
    	}
    	
        List<String> thisId2x024a = new ArrayList<String>();
        
        List<Field> fields = r.getDataFields(24);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            for (String subfield : subfields) {
                
            	String x024a = getFieldDataIntoCorrectFormat(field, subfield);
                
                if (StringUtils.isEmpty(x024a)) {
                    continue;   // bad data will cause trouble up the road.                	
                }

                LOG.debug("adding thisId2x024a: " + x024a);  
                thisId2x024a.add(x024a);
            }
        }
        LOG.error("gonna compare cachedId2x024a: " + cachedId2x024a + "  ...with... thisId2x024a: " + thisId2x024a);
        
        Set<String> setA = new HashSet<String>(cachedId2x024a);
        Set<String> setB = new HashSet<String>(thisId2x024a);
        boolean same = setA.containsAll(thisId2x024a) && setB.containsAll(cachedId2x024a);
	        
       	return (! same);        
    }

    @Override
    public void load(boolean firstTime) {
        // TODO may not use this for string-based matchers.

    }

    @Override
    public void flush(boolean freeUpMemory) {
        getMAS().getMarcAggregationServiceDAO().persist1StrMatchpointMaps(inputId2x024a, MarcAggregationServiceDAO.matchpoints_024a_table);
        inputId2x024a.clear();
        x024a2inputIds.clear();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        //return inputId2x024a.size();

        MarcAggregationService s = getMAS();
        LOG.debug("** 024 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_024a_table)+ " unique records in dB & "+inputId2x024a.size() +" records in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_024a_table);
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2x024a.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        //return x024a2inputIds.size();

        MarcAggregationService s = getMAS();
        LOG.debug("** 024 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueStringIds(MarcAggregationServiceDAO.matchpoints_024a_table)+ " unique strings in dB & "+x024a2inputIds.size() +" strs in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueStringIds(MarcAggregationServiceDAO.matchpoints_024a_table);
    }

}
