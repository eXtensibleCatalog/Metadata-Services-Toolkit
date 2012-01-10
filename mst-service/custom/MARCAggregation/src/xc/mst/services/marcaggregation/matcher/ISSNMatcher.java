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
 * <a href="http://www.loc.gov/marc/bibliographic/bd022.html">MARC 022 field</a>
 *
 * 022$a
 *
 * Step 2. Exact match on multiple identifiers.
 * This step allows the service to consider multiple combinations of identifiers,
 * when each of the identifiers on its own is not considered reliable enough to be used alone.
 * If the content of ALL of the fields listed within one group of identifiers in an incoming record
 * (as defined in the Configuration File) matches the content of the same fields in an existing record,
 * then the records are to be considered a MATCH. If a match is not found for Step A,
 * the service would attempt Step B, and so forth. The default setting would use the following steps:
 *
    Step 2B:
        LCCN: 010a (current LCCN) AND
        ISSN: 022a (current ISSN)
 *
 *022  (R)    $a (NR) - International Standard Serial Number
           Example: 022      ##$a0376-4583
           Note the '-' between the groups of digits.  All examples showed this,
           will it always be there?  Answer:  It should always be there, unless someone enters the data incorrectly.
           However, the eight numbers are always considered as a single identifier, never as two sets of four.
           The dash is just a display convention to make the number easier to read.

 * For the matching itself it would be okay to strip extraneous characters like dashes,
 * and just do an integer comparison, but if it used as a record of source, it would need to be kept intact,
 * so must store it as a string if it is a string for use as source record data.
 *
 * but, note, the int can contain the char value 'X' so don't bother:
 *           <marc:datafield tag="022" ind1="0" ind2=" ">
            <marc:subfield code="a">0738-100X</marc:subfield>

 *
 * @author JohnB
 *
 */
public class ISSNMatcher extends FieldMatcherService {

    // you can have multiple 022$a fields within a record (mult 022, each w/1 $a)
    protected Map<Long, List<String>> inputId2issnStr = new HashMap<Long, List<String>>();

    // 8 digit number so Integer will cover it.  But can include 'X' at end so must use String.
    protected Map<Long, List<String>> inputId2issn = new HashMap<Long, List<String>>();

    // multiple records might have the same normalized 022$a, this would be an indication of a match
    protected Map<String, List<Long>> issn2inputIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(ISSNMatcher.class);

    private boolean debug = false;

    protected String getAllButDash(final String s) {
        String stripped = s.replaceAll("[-]", "");
        return stripped;
    }

    @Override
    // return all matching records!!! a match means the same int part of issn.
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(22);

        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 022 in record! " + ir.recordId);
            }
            for (String subfield : subfields) {
                String issn = getAllButDash(subfield);
                if (issn2inputIds.get(issn) != null) {
                    results.addAll(issn2inputIds.get(issn));
                }
            }
        }
        LOG.debug("getMatchingInputIds, irId=" + ir.recordId + " results.size=" + results.size());
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
        }
        return results;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        List<Field> fields = r.getDataFields(22);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("** ERROR: Multiple $a subfields in 022 in record! " + r.recordId);
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                LOG.debug("here we go, processing subfield: "+subfield+" recordId:"+id+" numSubfields="+subfields.size()+ "numFields="+fields.size());
                if (debug) {
                    Util.getUtil().printStackTrace("who got me here?");
                }
                String issn = getAllButDash(subfield);
                List<String> issnList = inputId2issn.get(id);

                if (issnList == null) {
                    issnList = new ArrayList<String>();
                    issnList.add(issn);
                    inputId2issn.put(id, issnList);
                    LOG.debug("*** 1.adding to inputId2issn, for id: " + id + " for issn: " + issn);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else if (issnList.contains(issn)) {
                    LOG.debug("** We have already seen issn " + issn + " for recordId: " + r.recordId);
                }
                else {
                    issnList.add(issn);
                    inputId2issn.put(id, issnList);
                    LOG.debug("*** 2.adding to inputId2issn, for id: " + id + " for issn: " + issn);
                }

                List<String> issnStrList = inputId2issnStr.get(id);
                if (issnStrList == null) {
                    issnStrList = new ArrayList<String>();
                    issnStrList.add(subfield);
                    inputId2issnStr.put(id, issnStrList);
                    LOG.debug("*** 1.adding to inputId2issnStr, for id: " + id + " for issnStr: " + subfield);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else if (issnStrList.contains(subfield)) {
                    LOG.debug("** We have already seen issnStr " + subfield + " for recordId: " + r.recordId);
                }
                else {
                    issnStrList.add(subfield);
                    inputId2issnStr.put(id, issnStrList);
                    LOG.debug("*** 2.adding to inputId2issnStr, for id: " + id + " for issnStr: " + subfield);
                }

                List<Long> ids = issn2inputIds.get(issn);
                if (ids == null) {
                    ids = new ArrayList<Long>();
                }
                if (!ids.contains(r.recordId)) {
                    ids.add(r.recordId);
                    issn2inputIds.put(issn, ids);
                    LOG.debug("*** adding to issn2recordIds, for issn: " + issn);
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
        return inputId2issn.size();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2issn.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return issn2inputIds.size();
    }

}
