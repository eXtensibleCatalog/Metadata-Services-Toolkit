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
                    String goods = getFieldDataIntoCorrectFormat(field, subfield);
                    if (x024a2recordIds.get(goods) != null) {
                        results.addAll(x024a2recordIds.get(goods));
                    }
                }
            }
        }
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
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
                String goods = getFieldDataIntoCorrectFormat(field, subfield);

                if (list == null) {
                    list = new ArrayList<String>();
                    list.add(goods);
                    recordId2x024a.put(id, list);
                    LOG.debug("*** 1.adding to recordId2x024a, for id: " + id + " for x024$a: " + goods);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                // TODO fix that!
                else if (list.contains(goods)) {
                    LOG.debug("** We have already seen x024$a " + goods + " for recordId: " + r.recordId);
                }
                else {
                    list.add(goods);
                    recordId2x024a.put(id, list);
                    LOG.debug("*** 2.adding to recordId2x024a, for id: " + id + " for x024$a: " + goods);
                }

                List<Long> ids = x024a2recordIds.get(goods);
                if (ids == null) {
                    ids = new ArrayList<Long>();
                }
                if (!ids.contains(r.recordId)) {
                    ids.add(r.recordId);
                    x024a2recordIds.put(goods, ids);
                    LOG.debug("*** adding to x024a2recordIds, for x024$a: " + goods);
                }
                // Just because we have seen it, it is not an error, it just means multiple match rules use this matcher.
                else {
                    LOG.debug("** We have already seen recordId: " + r.recordId);
                }
            }
        }
    }

    private String getFieldDataIntoCorrectFormat(Field field, String goods) {
        goods = goods.trim();
        char indicator = SaxMarcXmlRecord.getIndicatorOfField(field, 1);
        goods = goods + indicator;  // thank you java
        if (indicator == '7') {
            goods += SaxMarcXmlRecord.getSubfieldOfField(field, '2'); //thanks again!
        }
        return goods;
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
