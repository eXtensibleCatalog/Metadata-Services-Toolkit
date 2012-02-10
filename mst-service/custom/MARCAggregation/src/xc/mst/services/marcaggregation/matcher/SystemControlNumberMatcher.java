/**
 * Copyright (c) 2011 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 */
package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;

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
 * It shall be considered an error to have > 1 035$a with prefix (OCoLC), must test for this, and log it.
 *
 * 035$a
 *
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public class SystemControlNumberMatcher extends FieldMatcherService {

    // you can have multiple 035$a fields within a record (mult 035, each w/1 $a)
    // thus use a list of string pairs, the pair values are the original string
    //    and the normalized string
    protected Map<Long, List<String[]>> inputId2scn = new HashMap<Long, List<String[]>>();

    // I wonder if the prefixes will be unique?  TODO  And how do you assoc. ind prefixes, with ints?  don't bother for now
    //protected Map<Long, List<String>> inputId2prefix = new HashMap<Long, List<String>>();

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

        MarcAggregationServiceDAO masDao = (MarcAggregationServiceDAO) config.getApplicationContext().getBean("MarcAggregationServiceDAO");

        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(35);

        final Long id = new Long(ir.recordId);
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 035 in record! " + ir.recordId);
            }
            for (String subfield : subfields) {
                String goods = getMapId(subfield);
                // look in memory
                if (scn2inputIds.get(goods) != null) {
                    results.addAll(scn2inputIds.get(goods));
                    if (results.contains(id)) {
                        results.remove(id);
                    }
                }

                // now look in the database too!
                //mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_035a where string_id = "24094664" '
                List<Long> records = masDao.getMatchingRecords(MarcAggregationServiceDAO.matchpoints_035a_table, MarcAggregationServiceDAO.input_record_id_field,MarcAggregationServiceDAO.string_id_field,goods);
                LOG.debug("SCN, DAO, getMatching records for "+goods+", numResults="+records.size());
                for (Long record: records) {
                    if (!record.equals(id)) {
                        if (!results.contains(record)) {
                            results.add(record);
                            LOG.debug("**SCN, DAO,  record id: "+record +" matches id "+id);
                        }
                    }
                }
            }
        }
        LOG.debug("getMatchingInputIds, irId=" + ir.recordId + " results.size=" + results.size());
        return results;
    }

    // should be a max of 1 field returned.
    // * It shall be considered an error to have > 1 035$a with prefix (OCoLC), must test for this, and log it.
    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir) {
        final String oclc = "OCoLC";
        List<Field> fields = r.getDataFields(35);
        boolean haveSeenOCoLC = false;
        for (Field field : fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size > 1) {
                LOG.error("ERROR: Multiple $a subfields in 035 in record! " + r.recordId);
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                String goods = getMapId(subfield);
                String prefix = getPrefixId(subfield);
                if (prefix.equals(oclc)) {
                    if (haveSeenOCoLC) {
                        LOG.error("ERROR: 035$a prefix (OCoLC) seen > 1 time for recordId: " + r.recordId);
                        final MarcAggregationService service = getMarcAggregationService();
                        if (service != null) {
                            service.addMessage(ir, 101, RecordMessage.ERROR);
                        }
                    }
                    haveSeenOCoLC = true;
                }
                List<String[]> goodsList = inputId2scn.get(id);
                final String[] goodsArray = new String[] {goods, subfield};  // its a pair of strings
                if (goodsList == null || goodsList.size() == 0) {
                    goodsList = new ArrayList<String[]>();
                    goodsList.add(goodsArray);
                    inputId2scn.put(id, goodsList);
                }
                else if (!goodsList.contains(goodsArray)) {
                    goodsList.add(goodsArray);
                    inputId2scn.put(id, goodsList);
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
        // TODO we have string data so will we load some subset?

    }

    // into db
    @Override
    public void flush(boolean freeUpMemory) {
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().persist2StrMatchpointMaps(inputId2scn, MarcAggregationServiceDAO.matchpoints_035a_table);
        inputId2scn.clear();
        scn2inputIds.clear();
    }


    /**
     * For testing.
     * TODO check in dB too?
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return inputId2scn.size();
    }

    //TODO check in dB too?
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
