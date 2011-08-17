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

import java.util.List;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;

/**
 * 
 * The Library of Congress Control Number corresponds to the 
 * <a href="http://www.loc.gov/marc/bibliographic/bd010.html">MARC 010 field</a>
 * 
 * @author Benjamin D. Anderson
 *
 */
public class LccnMatcher extends FieldMatcherService {
    
    protected TLongLongHashMap lccn2outputIds = new TLongLongHashMap();
    
    protected long getUniqueId(String s) {
        // http://www.loc.gov/marc/bibliographic/bd010.html
        // find the first numeric value and return it
        // This was determined by Jennifer in a phone conversation.
        return 0l;
    }


    public List<Long> getMatchingOutputIds(InputRecord ir) {
        // String s = r.getMARC().getDataFields().get(10).get('a');
        // long[] ids = lccn2outputIds.get(getUniqueId(s));
        // return new ArrayList<Long>(ids);

        return null;
    }

    public void addRecordToMatcher(Record r) {
        // String s = r.getMARC().getDataFields().get(10).get('a');
        // lccn2outputIds.add(r.getId(), getUniqueId(s));
    }

    public List<Long> getPreviousMatchPoint(long inputId) {
        // TODO Auto-generated method stub
        return null;
    }

    public void flush() {
        // TODO Auto-generated method stub
    }

    public void loadFromDB() {
        // TODO Auto-generated method stub
    }

    public void unload() {
        // TODO Auto-generated method stub       
    }

}
