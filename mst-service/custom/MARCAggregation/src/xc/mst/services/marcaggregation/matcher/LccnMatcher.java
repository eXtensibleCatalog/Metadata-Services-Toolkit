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

import gnu.trove.TLongLongHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
 * <a href="http://www.loc.gov/marc/bibliographic/bd010.html">MARC 010 field</a>
 *
 * 010$a
 *
 * The examples at the above site show alphanumeric data is possible, correct?   Yes - JBB
           (I have this note in my source though, is it correct ?) :
             //  find the first numeric value and return it
        //    This was determined by Jennifer in a phone conversation.
           If so, must modify our schema accordingly. (string field in addition to numeric.)
           Example: 010      ##$anuc76039265#
           Example: 010     ##$a##2001627090
    If no alphabetic prefix is present, then there should be either 2 or 3 blanks, as in the example above.

    Note, 010 does not repeat, nor does $a, so only 1 of these max per record.
 *
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public class LccnMatcher extends FieldMatcherService {

    private boolean debug = false;

    private static final Logger LOG = Logger.getLogger(LccnMatcher.class);

    // multiple records might have the same normalized 010$a, this would be an indication of a match
    protected Map<Long, List<Long>> lccn2inputIds = new HashMap<Long, List<Long>>();

    // you can have exactly 1 010$a fields within a record  (1 010, w/1 $a)
    // will this field data structure be needed? (or do we need record id to 010$a string, or both?)
    protected TLongLongHashMap inputId2lccn = new TLongLongHashMap();

    //don't need to save this
    //protected Map<Long, String> inputId2lccnStr = new HashMap<Long, String>();

    // am not sure how much I can count on the int starting at char 3 (0,1,2,3)
    // there is a prefix or spaces before the int, and the prefix can run right
    // to the int without a space between.
    // <== update - prefix not always there.
    //
    // http://www.loc.gov/marc/bibliographic/bd010.html
    // find the first numeric value and return it
    // This was determined by Jennifer in a phone conversation.
    //
    //  Note that I have seen prefix and suffix data, i.e.:
    //   <marc:datafield tag="010" ind1=" " ind2=" ">
    //   <marc:subfield code="a">m  61000295 /M/r86</marc:subfield>
    //
    //  Seems like trimming 1st 3 chars off will work,
    //  then tokenize around spaces, return the numeric portion that
    //  remains (or could test 1st. to verify it is numeric)
    //
    //TODO
    //change the matching algorithm for Aggregation to ignore a forward slash and any characters that follow it in matching 010 fields
    //
    public static long getUniqueId(String s) {
        String stripped=null;
        long strippedL=0l;
        StringTokenizer st = new StringTokenizer(s);
        String candidate = st.nextToken();
        if (StringUtils.isNotEmpty(candidate) && StringUtils.isNumeric(candidate)) {
            return Long.parseLong(candidate);
        }
        else if (candidate.toCharArray().length <=3) {
            candidate = st.nextToken();
            if (StringUtils.isNotEmpty(candidate) && StringUtils.isNumeric(candidate)) {
                return Long.parseLong(candidate);
            }
            else {
                try {
                    stripped = candidate.replaceAll("[^\\d]", "");
                    strippedL = Long.parseLong(stripped);
                    LOG.debug("numericID:"+strippedL);
                }catch(NumberFormatException e) {
                    LOG.error("** Problem with stripped string, not numeric, original="+s+" all_data="+ " stripped="+stripped);
                    stripped=null;
                }
                if (stripped == null) {
                    return 0l;
                }
                return strippedL;
            }
        }
        else {  // one long str including prefix.
            try {
                stripped = candidate.replaceAll("[^\\d]", "");
                strippedL = Long.parseLong(stripped);
                LOG.debug("numericID:"+strippedL);
            }catch(NumberFormatException e) {
                LOG.error("** Problem with stripped string, not numeric, original="+s+" all_data="+ " stripped="+stripped);
                stripped=null;
            }
            if (stripped == null) {
                return 0l;
            }
            return strippedL;
        }
    }

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {

        MarcAggregationServiceDAO masDao = (MarcAggregationServiceDAO) config.getApplicationContext().getBean("MarcAggregationServiceDAO");

        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(10);
        if (fields.size()>1) {
            LOG.error("ERROR: Multiple 010 fields in record! "+ir.recordId);
        }

        final Long id = new Long(ir.recordId);
        for (Field field: fields) {
                List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size>1) {
                LOG.error("ERROR: Multiple $a subfields in 010 in record! "+ir.recordId);
            }
            // there will be only 1 subfield, but this won't hurt...
            for (String subfield : subfields) {
                Long goods = new Long(getUniqueId(subfield));
                if (lccn2inputIds.get(goods) != null) {
                    results.addAll(lccn2inputIds.get(goods));
                    if (results.contains(id)) {
                        results.remove(id);
                    }
                }

                // now look in the database too!
                //mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_010a where string_id = "24094664" '
                List<Long> records = masDao.getMatchingRecords(MarcAggregationServiceDAO.matchpoints_010a_table, MarcAggregationServiceDAO.input_record_id_field,MarcAggregationServiceDAO.numeric_id_field,goods);
                LOG.debug("LCCN, DAO, getMatching records for "+goods+", numResults="+records.size());
                for (Long record: records) {
                    if (!record.equals(id)) {
                        if (!results.contains(record)) {
                            results.add(record);
                            LOG.debug("**LCCN, DAO,  record id: "+record +" matches id "+id);
                        }
                    }
                }
            }
        }
        LOG.debug("getMatchinginputIds, irId="+ ir.recordId+" results.size="+results.size());
        return results;
    }

    /**
     * when a record is updated/deleted, need to use this to
     */
    @Override
    public void removeRecordFromMatcher(InputRecord ir) {
        Long id   = new Long(ir.getId());
        Long lccn = inputId2lccn.get(id);
        List<Long> inputIds = null;

        if (lccn != null) {
            inputIds = lccn2inputIds.get(lccn);
        }
        if (inputIds != null) {
            inputIds.remove(id);
            if (inputIds.size() > 0) {
                lccn2inputIds.put(lccn, inputIds);
            }
            else {
                lccn2inputIds.remove(lccn);
            }
        }
        inputId2lccn.remove(id);

        // keep database in sync.  Don't worry about the one-off performance hit...yet.
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().deleteMergeRow(MarcAggregationServiceDAO.matchpoints_010a_table, id);
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir) {
        // String s = r.getMARC().getDataFields().get(10).get('a');
        // lccn2inputIds.add(r.getId(), getUniqueId(s));

        List<Field> fields = r.getDataFields(10);
        if (fields.size()>1) {
            LOG.error("ERROR: Multiple 010 fields in record! "+r.recordId);
        }
        for (Field field: fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size>1) {
                LOG.error("*ERROR: Multiple $a subfields in 010 in record! "+r.recordId);
                final MarcAggregationService service = getMarcAggregationService();
                if (service != null) {
                    service.addMessage(ir, 102, RecordMessage.ERROR);
                }
            }
            for (String subfield : subfields) {
                Long id = new Long(r.recordId);
                Long goods = new Long(getUniqueId(subfield));
                if (debug) {
                    LOG.info("LccnMatcher, numeric Lccn="+goods+" for record "+r.recordId);
                    if (r.recordId==2) {   // currently, record 2 gets hit 2x, once for 2a matcher, once for 2c matcher.  really a bug?
                        Util.getUtil().printStackTrace("who got me here?");
                    }
                }
                Long oldGoods = inputId2lccn.get(id);
                // but if the item is not in the longlong map, a 0 is returned???
                //TODO somewhere in the below code is a logic error that causes an exception with, not enough data, or something, when putting into row.
                if (oldGoods == null || oldGoods == 0l) {
                    inputId2lccn.put(id, goods);
                    //inputId2lccnStr.put(id, subfield);
                }
                else {
                    if (!goods.equals(oldGoods)) {
                        inputId2lccn.put(id, goods);
                        //inputId2lccnStr.put(id, subfield);
                        LOG.debug("we have already seen a different 010 entry ("+oldGoods+") for recordId: "+r.recordId+ " this 010: "+goods);
                        //LOG.info("we have already seen a different 010 entry ("+oldGoods+") for recordId: "+r.recordId);
                    }
                    else {
                        LOG.debug("we have already seen "+ goods +" for recordId: "+r.recordId);
                        //LOG.info("we have already seen "+ goods +" for recordId: "+r.recordId);
                    }
                }

                List<Long> idsList = lccn2inputIds.get(goods);
                if (idsList == null || idsList.size() == 0) {
                    idsList = new ArrayList<Long>();
                    idsList.add(id);
                    lccn2inputIds.put(goods, idsList);
                }
                else if (!idsList.contains(id)){
                    idsList.add(id);
                    lccn2inputIds.put(goods, idsList);
                }
                else {  //error?
                    LOG.debug("we have already seen "+ id +" for recordId: "+r.recordId);
                }
            }
        }
    }

    @Override
    public void load() {
        // TODO for this one, because the data is Long, may want to load all from dB to memory.

    }

    @Override
    // at commit time put stuff into db,
    public void flush(boolean freeUpMemory) {
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().persistLongMatchpointMaps(inputId2lccn, MarcAggregationServiceDAO.matchpoints_010a_table, true);
        //inputId2lccnStr.clear();
        inputId2lccn.clear();    //TODO may want to keep these in memory!
        lccn2inputIds.clear();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        List<Long> results = new ArrayList<Long>();
        for (Long record: inputId2lccn.keys()) {
            results.add(record);
        }
        return results;
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        //return inputId2lccn.size();

        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        LOG.debug("** 010 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_010a_table)+ " unique records in dB & "+inputId2lccn.size() +" records in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueRecordIds(MarcAggregationServiceDAO.matchpoints_010a_table);
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        //return lccn2inputIds.size();

        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        LOG.debug("** 010 matcher contains "+s.getMarcAggregationServiceDAO().getNumUniqueNumericIds(MarcAggregationServiceDAO.matchpoints_010a_table)+ " unique strings in dB & "+lccn2inputIds.size() +" strs in mem.");
        return s.getMarcAggregationServiceDAO().getNumUniqueNumericIds(MarcAggregationServiceDAO.matchpoints_010a_table);
    }

}
