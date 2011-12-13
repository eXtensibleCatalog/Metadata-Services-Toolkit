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
 * 035$a
 *
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public class SystemControlNumberMatcher extends FieldMatcherService {

//    protected Map<String, Long> prefixIds = new HashMap<String, Long>();

	// you can have multiple 035$a fields within a record  (mult 035, each w/1 $a)
    protected Map<Long, List<String>> outputId2scn = new HashMap<Long, List<String>>();

    //    protected TLongLongHashMap scn2outputIds = new TLongLongHashMap();

    // multiple records might have the same normalized 035$a, this would be an indication of a match
    protected Map<String, List<Long>> scn2outputIds = new HashMap<String, List<Long>>();

    private static final Logger LOG = Logger.getLogger(SystemControlNumberMatcher.class);

    protected String getPrefixId(String s) {
    	int start, end;
    	if (s.contains("(")) {
    		start = s.indexOf("(");
    		if (s.contains(")")) {
    			end = s.indexOf(")");
                LOG.debug(s);
                LOG.debug("found a prefix of "+s.substring(start+1,end));
    			return s.substring(start+1,end);
    		}
    	}
        return "";
    }

    protected long getNumericId(final String s) {
    	String stripped=null;
    	long strippedL=0l;
        try {
            // return the numeric portion, may be SAFER to return the int AFTER the () part,
        	//     if it exists (to avoid picking up a number within that)
            stripped = s.replaceAll("[^\\d]", "");
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

    protected String getMapId(String s) {
//        return (getNumericId(s)*1000)+getPrefixId(s);
        LOG.debug("mapID:"+getPrefixId(s) + getNumericId(s));
        return getPrefixId(s) + getNumericId(s);
    }

    @Override
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
    	//ir.recordId;
    	ArrayList<Long> results = new ArrayList<Long>();
        List<Field> fields = ir.getDataFields(35);
        //String s = ir.getMARC().getDataFields().get(35).get('a');
        //return lccn2outputIds.get(getMapId(s));

        for (Field field: fields) {
        	List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(fields.get(0), 'a');
            final int size = subfields.size();
            if (size>1) {
            	LOG.error("ERROR: Multiple $a subfields in 035 in record! "+ir.recordId);
            }
            for (String subfield : subfields) {
            	String goods = getMapId(subfield);
            	if (scn2outputIds.get(goods) != null) {
                	results.addAll(scn2outputIds.get(goods));
            	}
            }
        }
        /*
        if (size >0) {
        	List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(fields.get(0), 'a');
            for (String subfield : subfields) {
            	String goods = getMapId(subfield);
            	results.addAll(scn2outputIds.get(goods));
            }
        }
        */
        return results;
    }

    @Override
    // should be a max of 1 field returned.
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
    	//ir.recordId;
        List<Field> fields = r.getDataFields(35);
        final int size = fields.size();
        if (size>1) {
        	LOG.error("ERROR: Multiple 035 fields in record! "+r.recordId);
        }
        if (size >0) {
        	List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(fields.get(0), 'a');
            for (String subfield : subfields) {
            	Long id = new Long(r.recordId);
            	String goods = getMapId(subfield);
            	List<String> goodsList = outputId2scn.get(id);
            	if (goodsList == null || goodsList.size() == 0) {
            		goodsList = new ArrayList<String>();
            		goodsList.add(goods);
                	outputId2scn.put(id, goodsList);
            	}
            	else if (!goodsList.contains(goods)){
            		goodsList.add(goods);
                	outputId2scn.put(id, goodsList);
            	}
            	else {
                	LOG.debug("we have already seen "+ goods +" for recordId: "+r.recordId);
            	}

            	List<Long> idsList = scn2outputIds.get(goods);
            	if (idsList == null || idsList.size() == 0) {
            		idsList = new ArrayList<Long>();
            		idsList.add(id);
            		scn2outputIds.put(goods, idsList);
            	}
            	else if (!idsList.contains(id)){
            		idsList.add(id);
            		scn2outputIds.put(goods, idsList);
            	}
            	else {  //error?
                	LOG.debug("we have already seen "+ id +" for recordId: "+r.recordId);
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

}
