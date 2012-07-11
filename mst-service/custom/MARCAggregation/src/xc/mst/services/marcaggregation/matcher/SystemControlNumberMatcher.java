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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;

/**
 * The System control number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd035.html">MARC 035 field</a>
 *
 * OCLC Number, i.e. 035a, when the prefix= (OCoLC) (The service must match on both the numeric identifier AND the prefix.
 *
 * Note, the original requirement was to only accept this as a matchpoint if (OCoLC) was the prefix.  I never implemented it
 * that way, and now it turns out that the requirement has changed.  Now, just make sure you have a valid 035a with a well-formed
 * prefix followed by an identifier, and save it as a matchpoint.
 *
 * A later requirement may be to modify to accept the matchpoint with NO prefix.  Not yet implemented!
 * This requirement bounces back and forth, since:
 * 3/9/12 This just in, ignore the field if there is no prefix.
 *
 * Note that the XC MARC Normalization Service has steps to ensure that these identifiers are in a consistent format.
 * The prefix is defined as the characters within the parentheses.
 * OCLC numbers may also contain other letters BETWEEN the prefix and the prefix and the number itself.
 * These should be ignored in matching, as all OCLC numeric values are unique without the numbers.
 * E.g. (OCoLC)ocm12345 should match with (OCoLC)12345 but NOT with (NRU)12345.
 *
 * We save the entire string in the db, i.e. (OCoLC)ocm12345.
 *
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
    // thus use a list of SCNData, the data values are the original string
    //    and the normalized string parts - the numeric id and the prefix
    protected Map<Long, List<SCNData>> inputId2scn = new HashMap<Long, List<SCNData>>();

    // multiple records might have the same normalized 035$a, this would be an indication of a match
    protected Map<SCNData, List<Long>> scn2inputIds = new HashMap<SCNData, List<Long>>();

    protected List<String> prefixList = new ArrayList<String>();

    private static final Logger LOG = Logger.getLogger(SystemControlNumberMatcher.class);

    // as a side effect populates prefix list, for now, only if it finds a non-blank prefix.
    protected String getPrefix(String s) {
        int start, end;
        if (s.contains("(")) {
            start = s.indexOf("(");
            if (s.contains(")")) {
                end = s.indexOf(")");
                LOG.debug(s);
                final String prefix = s.substring(start + 1, end);
                LOG.debug("found a prefix of " + prefix);
                if (!prefixList.contains(prefix)) {
                    prefixList.add(prefix);
                }
                return prefix;
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

    protected SCNData getMapId(String s) {
        // return (getNumericId(s)*1000)+getPrefixId(s);
        final String prefix = getPrefix(s);
        LOG.debug("mapID:" + prefix + getNumericId(s));
        return new SCNData(prefix ,prefixList.indexOf(prefix) ,getNumericId(s), s);
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
                SCNData goods = getMapId(subfield);

                // for now don't consider 035$a if no prefix.
                if (!goods.prefix.equals("")) {
                    // look in memory
                    if (scn2inputIds.get(goods) != null) {
                        results.addAll(scn2inputIds.get(goods));
                        if (results.contains(id)) {
                            results.remove(id);
                        }
                    }

                    // now look in the database too!
                    List<Long> records = masDao.getMatchingSCCNRecords(MarcAggregationServiceDAO.matchpoints_035a_table,
                            MarcAggregationServiceDAO.input_record_id_field,
                            MarcAggregationServiceDAO.numeric_id_field,
                            MarcAggregationServiceDAO.prefix_id_field, goods);

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
        }
        LOG.debug("getMatchingInputIds, irId=" + ir.recordId + " results.size=" + results.size());
        return results;
    }

    /**
     * when a record is updated/deleted, need to use this to
     */
    @Override
    public void removeRecordFromMatcher(InputRecord r) {
        Long id = new Long(r.getId());
        List<SCNData> goodsList = inputId2scn.get(id);
        if (goodsList != null) {
            for (SCNData goodsFields: goodsList) {
                List<Long> idsList = scn2inputIds.get(goodsFields);
                if (idsList != null) {
                    idsList.remove(id);
                    if (idsList.size() > 0) {
                        scn2inputIds.put(goodsFields, idsList);
                    }
                    else {
                        scn2inputIds.remove(goodsFields);
                    }
                }
            }
        }
        inputId2scn.remove(id);

        // keep database in sync.  Don't worry about the one-off performance hit...yet.
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().deleteMergeRow(MarcAggregationServiceDAO.matchpoints_035a_table, id);
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
                String prefix = getPrefix(subfield);
                if (prefix.equals("")) {
                    // must have a prefix to use as a match point.
                    // TODO MST-503
                    break;
                }
                else if (prefix.equals(oclc)) {
                    if (haveSeenOCoLC) {
                        LOG.error("ERROR: 035$a prefix (OCoLC) seen > 1 time for recordId: " + r.recordId);
//
//                      For now, log only, don't add to error facet
//                        final MarcAggregationService service = getMarcAggregationService();
//                        if (service != null) {
//                            service.addMessage(ir, 101, RecordMessage.ERROR);
//                        }
                    }
                    haveSeenOCoLC = true;
                }
                SCNData goods = getMapId(subfield);
                List<SCNData> goodsList = inputId2scn.get(id);
                if (goodsList == null || goodsList.size() == 0) {
                    goodsList = new ArrayList<SCNData>();
                    goodsList.add(goods);
                    inputId2scn.put(id, goodsList);
                }
                else if (!goodsList.contains(goods)) {
                    goodsList.add(goods);
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
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        Map<Integer,String> map = s.getMarcAggregationServiceDAO().getPrefixes();
        Collection<String> c = map.values();

        //obtain an Iterator for Collection
        Iterator<String> itr = c.iterator();

        //iterate through TreeMap values iterator
        while(itr.hasNext()) {
            prefixList.add((String) itr.next());
        }
        // TODO
        // May also want to retrieve all matchpoint int data into memory,
    }

    // into db
    @Override
    public void flush(boolean force) {
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().persistPrefixList(prefixList, MarcAggregationServiceDAO.prefixes_035a_table);
        s.getMarcAggregationServiceDAO().persistSCNMatchpointMaps(inputId2scn, MarcAggregationServiceDAO.matchpoints_035a_table);
        // allow prefix list to grow - should not get too big?
        inputId2scn.clear();
        scn2inputIds.clear();
    }


    /**
     * For testing.  (for my tests, the more reliable number was out of the db)
     */
    public int getNumRecordIdsInMatcher() {
        //return inputId2scn.size();

        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        LOG.debug("** 035 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_035a_table)+ " unique records in dB & "+inputId2scn.size() +" records in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_035a_table);
    }

    //TODO check in dB too?
    // - seems to be unused, and I already don't recall what I used if for or thought I wanted it for.
    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2scn.keySet();
    }

    /**
     * For testing.
     */
    public int getNumMatchPointsInMatcher() {
        //return scn2inputIds.size();

        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        LOG.debug("** 035 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueNumericIds(MarcAggregationServiceDAO.matchpoints_035a_table)+ " unique strings in dB & "+inputId2scn.size() +" strs in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueNumericIds(MarcAggregationServiceDAO.matchpoints_035a_table);
    }
}
