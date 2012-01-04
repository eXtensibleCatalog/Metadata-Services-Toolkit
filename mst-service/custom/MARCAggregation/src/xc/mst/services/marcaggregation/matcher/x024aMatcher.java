package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
import xc.mst.utils.Util;

/**
 *
 * The Library of Congress Control Number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd024.html">MARC 024 field</a>
 *
 * Other Standard Identifier: 024a2 (Note: 1st indicator must also match.
 * If 1st indicator value is 7, then contents of Subfield 2 must also match)
 * TODO, do the ABOVE!
 *
 * 024$a
 *
 * @author JohnB
 *
 */
public class x024aMatcher extends FieldMatcherService {

    // you can have multiple 024$a fields within a record (mult 024, each w/1 $a)
    protected Map<Long, List<String>> recordId2x024a = new HashMap<Long, List<String>>();

    // multiple records might have the same normalized 020$a, this would be an indication of a match
    protected Map<String, List<Long>> x024a2recordIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(x024aMatcher.class);
    private boolean debug = false;

    @Override
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(24);

        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 024 in record! " + ir.recordId);
            }
            for (String subfield : subfields) {
                if (StringUtils.isNotEmpty(subfield)) {
                    String s = subfield.trim();
                    if (x024a2recordIds.get(s) != null) {
                        results.addAll(x024a2recordIds.get(s));
                    }
                }
            }
        }
        LOG.debug("getMatchingOutputIds, irId=" + ir.recordId + " results.size=" + results.size());
        return results;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        List<Field> fields = r.getDataFields(24);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("** ERROR: Multiple $a subfields in 024 in record! " + r.recordId);
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                LOG.debug("here we go, processing subfield: "+subfield+" recordId:"+id+" numSubfields="+subfields.size()+ "numFields="+fields.size());
                if (debug) {
                    Util.getUtil().printStackTrace("who got me here?");
                }
                List<String> list = recordId2x024a.get(id);

                if (list == null) {
                    list = new ArrayList<String>();
                    list.add(subfield.trim());
                    recordId2x024a.put(id, list);
                    LOG.debug("*** 1.adding to recordId2isbn, for id: " + id + " for x245$a: " + subfield);
      LOG.info("*** 1.adding to recordId2isbn, for id: " + id + " for x245$a: " + subfield);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                // TODO fix that!
                else if (list.contains(subfield.trim())) {
                    LOG.debug("** We have already seen x024$a " + subfield + " for recordId: " + r.recordId);
        LOG.info("** We have already seen x024$a " + subfield + " for recordId: " + r.recordId);
                }
                else {
                    list.add(subfield.trim());
                    recordId2x024a.put(id, list);
                    LOG.debug("*** 2.adding to recordId2x024a, for id: " + id + " for x024$a: " + subfield);
          LOG.info("*** 2.adding to recordId2x024a, for id: " + id + " for x024$a: " + subfield);
                }

                List<Long> ids = x024a2recordIds.get(subfield.trim());
                if (ids == null) {
                    ids = new ArrayList<Long>();
                }
                if (!ids.contains(r.recordId)) {
                    ids.add(r.recordId);
                    x024a2recordIds.put(subfield.trim(), ids);
                    LOG.debug("*** adding to x024a2recordIds, for x024$a: " + subfield.trim());
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
        return recordId2x024a.size();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return recordId2x024a.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return x024a2recordIds.size();
    }

}
