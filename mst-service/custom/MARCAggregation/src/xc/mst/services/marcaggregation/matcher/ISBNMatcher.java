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
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.utils.Util;

/**
 *
 * The Library of Congress Control Number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd020.html">MARC 020 field</a>
 *
 * 020$a
 *
 * Step 2. Exact match on multiple identifiers.
 * This step allows the service to consider multiple combinations of identifiers,
 * when each of the identifiers on its own is not considered reliable enough to be used alone.
 * If the content of ALL of the fields listed within one group of identifiers in an incoming record
 * (as defined in the Configuration File) matches the content of the same fields in an existing record,
 * then the records are to be considered a MATCH. If a match is not found for Step A,
 * the service would attempt Step B, and so forth. The default setting would use the following steps:
 *
 * Step 2A:
 * LCCN: 010a (current LCCN) AND
 * ISBN: 020a (current ISBN) � Note: number of digits must match for ISBN-10 vs ISBN-13.
 * Match only up to the first blank space, ignoring any characters after the blank.
 *
 * 020$a:
 * It should be ignored as part of the comparison, but retained.
 * We have had a Normalization Service step that would strip out the extra data,
 * but Kyushu has requested that it NOT be stripped out since they want to display it to users.
 * So just ignore it in the matching but keep it.
 *
 * 020 (R) $a (NR) - ISBN, 10 or 13 digit, note, number of digits must match for ISBN-10 vs ISBN-13.
 *
 * @author JohnB
 *
 */
public class ISBNMatcher extends FieldMatcherService {

    // you can have multiple 020$a fields within a record (mult 020, each w/1 $a)
    // thus use a list of string pairs, the pair values are the original string
    //    and the normalized string
    //  note, originally were saving the whole string too, now I don't see a need!
    //
    //protected Map<Long, List<String[]>> inputId2isbn = new HashMap<Long, List<String[]>>();
    protected Map<Long, List<String>> inputId2isbn = new HashMap<Long, List<String>>();

    // multiple records might have the same normalized 020$a, this would be an indication of a match
    protected Map<String, List<Long>> isbn2inputIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(ISBNMatcher.class);

    private boolean debug = false;
    
    MarcAggregationService mas = null;

    // filter out stuff after 1st space. trim to be sure. orig thought could use a long but have seen isbn's like this:
    // 123456789X
    public static String getIsbn(String s) {

        String[] tokens = s.split(" ");
        String isbn = tokens[0];
        return isbn.trim();
    }
    
    private MarcAggregationService getMAS() {
		if (mas == null) {
			mas = (MarcAggregationService)config.getBean("MarcAggregationService");
		}
		return mas;
	}

    @Override
    /**
     * when a record is updated/deleted, need to use this to
     */
    public void removeRecordFromMatcher(InputRecord ir) {
        Long id   = new Long(ir.getId());
        List<String> isbns = inputId2isbn.get(id);
        if (isbns != null) {
            for (String isbn: isbns) {
                List<Long> inputIds = isbn2inputIds.get(isbn);
                if (inputIds != null) {
                    inputIds.remove(id);
                    if (inputIds.size() > 0) {
                        isbn2inputIds.put(isbn, inputIds);
                    }
                    else {
                        isbn2inputIds.remove(isbn);
                    }
                }
            }
        }
        inputId2isbn.remove(id);

        // keep database in sync.  Don't worry about the one-off performance hit...yet.
        getMAS().getMarcAggregationServiceDAO().deleteMergeRow(MarcAggregationServiceDAO.matchpoints_020a_table, id);
    }

    @Override
    // return all matching records!!! a match means the same int part of isbn.
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord r) {
        MarcAggregationServiceDAO masDao = getMAS().getMarcAggregationServiceDAO();

        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = r.getDataFields(20);

        final Long id = new Long(r.recordId);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');

            for (String subfield : subfields) {
                String isbn = getIsbn(subfield);
                if (isbn2inputIds.get(isbn) != null) {
                	List<Long> m = isbn2inputIds.get(isbn);
                	if (m != null && m.size() > 0) {
                		results.addAll(m);
                	}
                    
                    if (results.contains(id)) {
                        results.remove(id);
                    }
                }

                //TODO can the below be made faster?  How to limit WHAT is queried?  My idea was to try to cache and prematch some of this but
                //TODO    would then have to take that result set and come up with a query that is faster than this one!

                // now look in the database too!
                //mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_020a where string_id = "24094664" '
                List<Long> records = masDao.getMatchingRecords(MarcAggregationServiceDAO.matchpoints_020a_table, MarcAggregationServiceDAO.input_record_id_field,MarcAggregationServiceDAO.string_id_field,isbn);
                LOG.debug("ISBN, DAO, getMatching records for "+isbn+", numResults="+records.size());
                for (Long record: records) {
                    if (!record.equals(id)) {
                        if (!results.contains(record)) {
                            results.add(record);
                            LOG.debug("**ISBN, DAO,  record id: "+record +" matches id "+id);
                        }
                    }
                }
            }
        }
        LOG.debug("getMatchinginputIds, irId=" + r.recordId + " results.size=" + results.size());
        return results;
    }

    /**
     * quick and dirty test, if 1st char is a digit, let it slide.
     * @param isbn, allegedly
     * @return the trueness of it all
     */
    private boolean isIsbnValid(String isbn) {
        if (StringUtils.isEmpty(isbn)) {
            return false;
        }
        try {
            return Character.isDigit(isbn.charAt(0));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * log invalid ISBN error
     * @param ir - attach error to this input record, viewable via browse records.
     */
    private void attachError104(InputRecord ir) {
        final MarcAggregationService service = getMarcAggregationService();
        if (service != null) {
            service.addMessage(ir, 104, RecordMessage.ERROR);
        }
    }

    @Override
    // TODO refactor out the commonalities!
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir) {
        List<Field> fields = r.getDataFields(20);

        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            for (String subfield : subfields) {
                Long id = r.recordId;   // autoboxing for better or worse...
                LOG.debug("here we go, processing subfield: "+subfield+" recordId:"+id+" numSubfields="+subfields.size()+ "numFields="+fields.size());
                if (debug) {
                    Util.getUtil().printStackTrace("who got me here?");
                }
                String isbn = getIsbn(subfield);
                if (!isIsbnValid(isbn)) {
                    LOG.error("** problem with 020$a ISBN in: " + r.recordId);
                    attachError104(ir);
                    continue;   // bad data will cause trouble up the road.
                }

                //note, originally saved orig string too, but I don't see the need
                //final String[] isbnArray = new String[] {isbn, subfield};  // its a pair of strings
                //List<String[]> isbnList = inputId2isbn.get(id);
                List<String> isbnList = inputId2isbn.get(id);

                if (isbnList == null) {
                    //isbnList = new ArrayList<String[]>();
                    isbnList = new ArrayList<String>();
                    isbnList.add(isbn);
                    inputId2isbn.put(id, isbnList);
                    LOG.debug("*** 1.adding to inputId2isbn, for id: " + id + " for isbn: " + isbn);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else if (isbnList.contains(isbn)) {
                    LOG.debug("** We have already seen isbn " + isbn + " for recordId: " + r.recordId);
                }
                else {
                    isbnList.add(isbn);
                    inputId2isbn.put(id, isbnList);
                    LOG.debug("*** 2.adding to inputId2isbn, for id: " + id + " for isbn: " + isbn);
                }

                List<Long> ids = isbn2inputIds.get(isbn);
                if (ids == null) {
                    ids = new ArrayList<Long>();
                }
                if (!ids.contains(r.recordId)) {
                    ids.add(r.recordId);
                    isbn2inputIds.put(isbn, ids);
                    LOG.debug("*** adding to isbn2inputIds, for isbn: " + isbn);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else {
                    LOG.debug("** We have already seen recordId: " + r.recordId);
                }
            }
        }
    }
    
    
    public boolean matchpointsHaveChanged(SaxMarcXmlRecord r, InputRecord ir) {
    	LOG.debug("isbn matchpointsHaveChanged? ID: " + ir.getId()); 
    	Map<Long, List<String>> cachedListId2isbn = getMAS().getMarcAggregationServiceDAO().get1StrMatchpointsRecordsCache(Long.valueOf(ir.getId()), MarcAggregationServiceDAO.matchpoints_020a_table);
    	LOG.debug("cachedListId2isbn: " + cachedListId2isbn);    	
    	    	
    	List<String> cachedId2isbn = new ArrayList<String>();
    	if (cachedListId2isbn.containsKey(ir.getId())) {
    		cachedId2isbn = cachedListId2isbn.get(ir.getId());
    		LOG.debug("cachedId2isbn: " + cachedId2isbn);  
    	}
    	
        List<String> thisId2isbn = new ArrayList<String>();
        
        List<Field> fields = r.getDataFields(20);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            for (String subfield : subfields) {
                String isbn = getIsbn(subfield);
                if (!isIsbnValid(isbn)) {
                    continue;   // bad data will cause trouble up the road.
                }

                LOG.debug("adding thisId2isbn: " + isbn);  
                thisId2isbn.add(isbn);
            }
        }
        LOG.error("gonna compare cachedId2isbn: " + cachedId2isbn + "  ...with... thisId2isbn: " + thisId2isbn);
        
        Set<String> setA = new HashSet<String>(cachedId2isbn);
        Set<String> setB = new HashSet<String>(thisId2isbn);
        boolean same = setA.containsAll(thisId2isbn) && setB.containsAll(cachedId2isbn);
	        
       	return (! same);   
    }
    

    @Override
    public void load(boolean firstTime) {
        // TODO Auto-generated method stub

    }

    // TODO this can fail.  so to sanitize the table?
    @Override
    public void flush(boolean freeUpMemory) {
        getMAS().getMarcAggregationServiceDAO().persist1StrMatchpointMaps(inputId2isbn, MarcAggregationServiceDAO.matchpoints_020a_table);
        inputId2isbn.clear();
        isbn2inputIds.clear();
    }

    /**
     * For testing.
     * @return the number of unique record ids the matcher holds
     */
    public int getNumRecordIdsInMatcher() {
        //return inputId2isbn.size();

        MarcAggregationService s = getMAS();
        LOG.debug("** 020 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_020a_table)+ " unique records in dB & "+inputId2isbn.size() +" records in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_020a_table);
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2isbn.keySet();
    }

    /**
     * For testing.
     * @return  the number of strings the matcher holds (matchpoints)
     */
    public int getNumMatchPointsInMatcher() {
        //return isbn2inputIds.size();

        MarcAggregationService s = getMAS();
        LOG.debug("** 020 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueStringIds(MarcAggregationServiceDAO.matchpoints_020a_table)+ " unique strings in dB & "+isbn2inputIds.size() +" strs in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueStringIds(MarcAggregationServiceDAO.matchpoints_020a_table);
    }
}
