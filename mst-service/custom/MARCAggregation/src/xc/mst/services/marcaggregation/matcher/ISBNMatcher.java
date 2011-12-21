package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;

/**
 *
 * The Library of Congress Control Number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd020.html">MARC 020 field</a>
 *
 * 020$a
 *
 * Step 2. Exact match on multiple identifiers.
This step allows the service to consider multiple combinations of identifiers,
when each of the identifiers on its own is not considered reliable enough to be used alone.
If the content of ALL of the fields listed within one group of identifiers in an incoming record
(as defined in the Configuration File) matches the content of the same fields in an existing record,
then the records are to be considered a MATCH. If a match is not found for Step A,
the service would attempt Step B, and so forth. The default setting would use the following steps:

    Step 2A:
        LCCN: 010a (current LCCN) AND
        ISBN: 020a (current ISBN) – Note: number of digits must match for ISBN-10 vs ISBN-13.
        Match only up to the first blank space, ignoring any characters after the blank.

        020$a:
        It should be ignored as part of the comparison, but retained.
        We have had a Normalization Service step that would strip out the extra data,
        but Kyushu has requested that it NOT be stripped out since they want to display it to users.
        So just ignore it in the matching but keep it.

         020  (R)   $a (NR) - ISBN, 10 or 13 digit, note, number of digits must match for ISBN-10 vs ISBN-13.
 *
 * @author JohnB
 *
 */
public class ISBNMatcher extends FieldMatcherService {

	// you can have multiple 020$a fields within a record  (mult 020, each w/1 $a)
    protected Map<Long, List<String>> recordId2isbnStr = new HashMap<Long, List<String>>();
    protected Map<Long, List<String>> recordId2isbn = new HashMap<Long, List<String>>();

    // multiple records might have the same normalized 020$a, this would be an indication of a match
    protected Map<String, List<Long>> isbn2recordIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(ISBNMatcher.class);

    //filter out stuff after 1st space.  trim to be sure.  orig thought could use a long but have seen isbn's like this:
    //   123456789X
    protected String getIsbn(String s) {

    	String[] tokens = s.split(" ");
    	String isbn = tokens[0];
    	return isbn.trim();
    }

    @Override
    // return all matching records!!!  a match means the same int part of isbn.
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
    	ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(20);

        for (Field field: fields) {
        	List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size>1) {
            	LOG.error("ERROR: Multiple $a subfields in 020 in record! "+ir.recordId);
            }
            for (String subfield : subfields) {
            	String isbn = getIsbn(subfield);
            	if (isbn2recordIds.get(isbn) != null) {
                	results.addAll(isbn2recordIds.get(isbn));
            	}
            }
        }
        LOG.debug("getMatchingOutputIds, irId="+ ir.recordId+" results.size="+results.size());
        return results;
    }

    @Override
    //TODO refactor out the commonalities!
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        List<Field> fields = r.getDataFields(20);
        for (Field field: fields) {
        	List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size>1) {
            	LOG.error("** ERROR: Multiple $a subfields in 020 in record! "+r.recordId);
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
            	String isbn = getIsbn(subfield);
            	List<String> isbnList = recordId2isbn.get(id);

            	if (isbnList == null) {
            		isbnList = new ArrayList<String>();
            		isbnList.add(isbn);
            	}
            	else if (isbnList.contains(isbn)) {  //TODO error?
                	LOG.error("** ERROR: we have already seen isbn "+ isbn +" for recordId: "+r.recordId);
            	}
            	else {
            		isbnList.add(isbn);
            	}
        		recordId2isbn.put(id, isbnList);
        		LOG.debug("*** adding to recordId2isbn, for id: "+id+ " for isbn: "+isbn);

                List<String> isbnStrList = recordId2isbnStr.get(id);
            	if (isbnStrList == null) {
            		isbnStrList = new ArrayList<String>();
            		isbnStrList.add(subfield);
            	}
            	else if (isbnStrList.contains(subfield)) {  //TODO error?
                	LOG.error("** ERROR: we have already seen isbnStr "+ subfield +" for recordId: "+r.recordId);
            	}
            	else {
            		isbnStrList.add(subfield);
            	}
        		recordId2isbnStr.put(id, isbnStrList);
        		LOG.debug("*** adding to recordId2isbnStr, for id: "+id+ " for isbnStr: "+subfield);

        		List<Long> ids = isbn2recordIds.get(isbn);
        		if (ids == null) {
            		ids = new ArrayList<Long>();
        		}
        		if (!ids.contains(r.recordId)) {
        			ids.add(r.recordId);
        			isbn2recordIds.put(isbn, ids);
        			LOG.debug("*** adding to isbn2recordIds, for isbn: "+isbn);
        		}
            	else {  //TODO error?
                	LOG.error("** ERROR: we have already seen recordId: "+r.recordId);
            	}
            }
        }
    }

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    public void flush(boolean freeUpMemory) {
        // TODO Auto-generated method stub
    }

}
