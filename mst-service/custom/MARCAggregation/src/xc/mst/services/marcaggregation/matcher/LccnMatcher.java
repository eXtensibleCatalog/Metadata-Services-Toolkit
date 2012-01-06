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

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
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
    protected Map<Long, List<Long>> lccn2recordIds = new HashMap<Long, List<Long>>();

    // you can have exactly 1 010$a fields within a record  (1 010, w/1 $a)
    // will this field datastructure be needed? (or do we need recordid to 010$a string, or both?)
    protected TLongLongHashMap recordId2lccn = new TLongLongHashMap();

    protected Map<Long, String> recordId2lccnStr = new HashMap<Long, String>();

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
    protected long getUniqueId(String s) {
        String stripped=null;
        long strippedL=0l;
        // I have to learn to use this right.  I want the string tokenizer effect, so I will use that,
        //  this gave me extra empty tokens, needed to chomp whitespace better.
        //
//        String[] tokens = s.split(" ");
//        String candidate = tokens[0];
        StringTokenizer st = new StringTokenizer(s);
        String candidate = st.nextToken();
        if (StringUtils.isNotEmpty(candidate) && StringUtils.isNumeric(candidate)) {
            return Long.parseLong(candidate);
        }
        else if (candidate.toCharArray().length <=3) {
//            candidate = tokens[1];
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
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
        ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(10);
        if (fields.size()>1) {
            LOG.error("ERROR: Multiple 010 fields in record! "+ir.recordId);
        }

        for (Field field: fields) {
                List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size>1) {
                LOG.error("ERROR: Multiple $a subfields in 010 in record! "+ir.recordId);
            }
            // TODO don't return the original record itself as a match, adding record to matcher AFTER this step?, BUT
            //       should we verify the record is not matching itself?
            // there will be only 1 subfield, but this won't hurt...
            for (String subfield : subfields) {
                Long goods = new Long(getUniqueId(subfield));
                if (lccn2recordIds.get(goods) != null) {
                        results.addAll(lccn2recordIds.get(goods));
                }
            }
        }
        LOG.debug("getMatchingOutputIds, irId="+ ir.recordId+" results.size="+results.size());
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
        }
        return results;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        // String s = r.getMARC().getDataFields().get(10).get('a');
        // lccn2outputIds.add(r.getId(), getUniqueId(s));

        List<Field> fields = r.getDataFields(10);
        if (fields.size()>1) {
            LOG.error("ERROR: Multiple 010 fields in record! "+r.recordId);
        }
        for (Field field: fields) {
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            final int size = subfields.size();
            if (size>1) {
                LOG.error("ERROR: Multiple $a subfields in 010 in record! "+r.recordId);
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
                Long oldGoods = recordId2lccn.get(id);
                // but if the item is not in the longlong map, a 0 is returned???
                if (oldGoods == null || oldGoods == 0l) {
                    recordId2lccn.put(id, goods);
                }
                else {
                    if (goods != oldGoods) {
                        recordId2lccn.put(id, goods);
                        LOG.debug("we have already seen a different 010 entry ("+oldGoods+") for recordId: "+r.recordId);
                        //LOG.info("we have already seen a different 010 entry ("+oldGoods+") for recordId: "+r.recordId);
                    }
                    LOG.debug("we have already seen "+ goods +" for recordId: "+r.recordId);
                    //LOG.info("we have already seen "+ goods +" for recordId: "+r.recordId);
                }
                recordId2lccnStr.put(id, subfield);

                List<Long> idsList = lccn2recordIds.get(goods);
                if (idsList == null || idsList.size() == 0) {
                        idsList = new ArrayList<Long>();
                        idsList.add(id);
                        lccn2recordIds.put(goods, idsList);
                }
                else if (!idsList.contains(id)){
                        idsList.add(id);
                        lccn2recordIds.put(goods, idsList);
                }
                else {  //error?
                        LOG.debug("we have already seen "+ id +" for recordId: "+r.recordId);
                }
            }
        }
    }

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    // at commit time put stuff into db,
    public void flush(boolean freeUpMemory) {
        // TODO Auto-generated method stub
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return recordId2lccn.size();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        List<Long> results = new ArrayList<Long>();
        for (Long record: recordId2lccn.keys()) {
            results.add(record);
        }
        return results;
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return lccn2recordIds.size();
    }

}
