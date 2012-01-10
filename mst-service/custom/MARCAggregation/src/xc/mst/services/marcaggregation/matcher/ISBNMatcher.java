package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
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
 * ISBN: 020a (current ISBN) – Note: number of digits must match for ISBN-10 vs ISBN-13.
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
    protected Map<Long, List<String>> inputId2isbnStr = new HashMap<Long, List<String>>();
    protected Map<Long, List<String>> inputId2isbn = new HashMap<Long, List<String>>();

    // multiple records might have the same normalized 020$a, this would be an indication of a match
    protected Map<String, List<Long>> isbn2inputIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(ISBNMatcher.class);

    private boolean debug = false;

    // filter out stuff after 1st space. trim to be sure. orig thought could use a long but have seen isbn's like this:
    // 123456789X
    protected String getIsbn(String s) {

        String[] tokens = s.split(" ");
        String isbn = tokens[0];
        return isbn.trim();
    }

    @Override
    // return all matching records!!! a match means the same int part of isbn.
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(20);

        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 020 in record! " + ir.recordId);
            }
            for (String subfield : subfields) {
                String isbn = getIsbn(subfield);
                if (isbn2inputIds.get(isbn) != null) {
                    results.addAll(isbn2inputIds.get(isbn));
                }
            }
        }
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
        }
        LOG.debug("getMatchinginputIds, irId=" + ir.recordId + " results.size=" + results.size());
        return results;
    }

    @Override
    // TODO refactor out the commonalities!
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        List<Field> fields = r.getDataFields(20);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("** ERROR: Multiple $a subfields in 020 in record! " + r.recordId);
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                LOG.debug("here we go, processing subfield: "+subfield+" recordId:"+id+" numSubfields="+subfields.size()+ "numFields="+fields.size());
                if (debug) {
                    Util.getUtil().printStackTrace("who got me here?");
                }
                String isbn = getIsbn(subfield);
                List<String> isbnList = inputId2isbn.get(id);

                if (isbnList == null) {
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

                List<String> isbnStrList = inputId2isbnStr.get(id);
                if (isbnStrList == null) {
                    isbnStrList = new ArrayList<String>();
                    isbnStrList.add(subfield);
                    inputId2isbnStr.put(id, isbnStrList);
                    LOG.debug("*** 1.adding to inputId2isbnStr, for id: " + id + " for isbnStr: " + subfield);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else if (isbnStrList.contains(subfield)) {
                    LOG.debug("** We have already seen isbnStr " + subfield + " for recordId: " + r.recordId);
                }
                else {
                    isbnStrList.add(subfield);
                    inputId2isbnStr.put(id, isbnStrList);
                    LOG.debug("*** 2.adding to inputId2isbnStr, for id: " + id + " for isbnStr: " + subfield);
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

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    public void flush(boolean freeUpMemory) {
        // TODO Auto-generated method stub
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return inputId2isbn.size();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2isbn.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return isbn2inputIds.size();
    }
}
