/**
 * Copyright (c) 2011 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 * @author Benjamin D. Anderson
 *
 */
package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;

/**
 * The System control number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd035.html">MARC 035 field</a>
 *
 * OCLC Number, i.e. 035a, when the prefix= (OCoLC) (The service must match on both the numeric identifier AND the prefix.
 * Note that the XC MARC Normalization Service has steps to ensure that these identifiers are in a consistent format).
 * The prefix is defined as the characters within the parentheses.
 * OCLC numbers may also contain other letters BETWEEN the prefix and the prefix and the number itself.
 * These should be ignored in matching, as all OCLC numeric values are unique without the numbers.
 * E.g. (OCoLC)ocm12345 should match with (OCoLC)12345 but NOT with (NRU)12345.
 * TODO do we need to save original format, i.e. (OCoLC)ocm12345 or can we just save (OCoLC)12345 ?
 *
 * 035$a
 *
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public class SystemControlNumberMatcher extends FieldMatcherService {

    // you can have multiple 035$a fields within a record (mult 035, each w/1 $a)
    protected Map<Long, List<String>> inputId2scn = new HashMap<Long, List<String>>();
    protected Map<Long, List<String>> inputId2scnFull = new HashMap<Long, List<String>>();

    //protected Map<Long, List<Long>> inputId2scnNum = new HashMap<Long, List<Long>>();
    // I wonder if the prefixes will be unique?  TODO  And how do you assoc. ind prefixes, with ints?  don't bother for now
    //protected Map<Long, List<String>> inputId2prefix = new HashMap<Long, List<String>>();

    //TODO may need to save the entire existing 035$a also instead of just the normalized version.
    //     (normalized version will not have alpha after prefix)

    // protected TLongLongHashMap scn2outputIds = new TLongLongHashMap();

    // multiple records might have the same normalized 035$a, this would be an indication of a match
    protected Map<String, List<Long>> scn2inputIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(SystemControlNumberMatcher.class);

    protected String getPrefixId(String s) {
        int start, end;
        if (s.contains("(")) {
            start = s.indexOf("(");
            if (s.contains(")")) {
                end = s.indexOf(")");
                LOG.debug(s);
                LOG.debug("found a prefix of " + s.substring(start + 1, end));
                return s.substring(start + 1, end);
            }
        }
        return "";
    }

    protected long getNumericId(final String s) {
        String stripped = null;
        long strippedL = 0l;
        try {
            // return the numeric portion, may be SAFER to return the int AFTER the () part,
            // if it exists (to avoid picking up a number within that)
            stripped = s.replaceAll("[^\\d]", "");
            strippedL = Long.parseLong(stripped);
            LOG.debug("numericID:" + strippedL);
        } catch (NumberFormatException e) {
            LOG.error("** Problem with stripped string, not numeric, original=" + s + " all_data=" + " stripped=" + stripped);
            stripped = null;
        }
        if (stripped == null) {
            return 0l;
        }
        return strippedL;
    }

    protected String getMapId(String s) {
        // return (getNumericId(s)*1000)+getPrefixId(s);
        LOG.debug("mapID:" + getPrefixId(s) + getNumericId(s));
        return getPrefixId(s) + getNumericId(s);
    }

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(35);

        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 035 in record! " + ir.recordId);
            }
            // TODO don't return the original record itself as a match, adding record to matcher AFTER this step?, BUT
            // should we verify the record is not matching itself?
            for (String subfield : subfields) {
                String goods = getMapId(subfield);
                if (scn2inputIds.get(goods) != null) {
                    results.addAll(scn2inputIds.get(goods));
                }
            }
        }
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
        }
        LOG.debug("getMatchingInputIds, irId=" + ir.recordId + " results.size=" + results.size());
        return results;
    }

    @Override
    // should be a max of 1 field returned.
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        List<Field> fields = r.getDataFields(35);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 035 in record! " + r.recordId);
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                String goods = getMapId(subfield);
                List<String> goodsList = inputId2scn.get(id);
                List<String> fullList = inputId2scnFull.get(id);
                if (goodsList == null || goodsList.size() == 0) {
                    goodsList = new ArrayList<String>();
                    fullList = new ArrayList<String>();
                    goodsList.add(goods);
                    fullList.add(subfield);
                    inputId2scn.put(id, goodsList);
                    inputId2scnFull.put(id, fullList);
                }
                else if (!goodsList.contains(goods)) {
                    goodsList.add(goods);
                    inputId2scn.put(id, goodsList);
                    fullList.add(subfield);
                    inputId2scnFull.put(id, fullList);
                }
                else {
                    LOG.debug("we have already seen " + goods + " for recordId: " + r.recordId);
                }

                List<Long> idsList = scn2inputIds.get(goods);
                if (idsList == null || idsList.size() == 0) {
                    idsList = new ArrayList<Long>();
                    idsList.add(id);
                    scn2inputIds.put(goods, idsList);
                }
                else if (!idsList.contains(id)) {
                    idsList.add(id);
                    scn2inputIds.put(goods, idsList);
                }
                else { // error?
                    LOG.debug("we have already seen " + id + " for recordId: " + r.recordId);
                }
            }
        }
    }

    // from db
    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    // into db
    @Override
    public void flush(boolean freeUpMemory) {
        // TODO Auto-generated method stub

    }


    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return inputId2scn.size();
    }

    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2scn.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return scn2inputIds.size();
    }
}
