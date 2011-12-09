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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;

/**
 * The System control number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd035.html">MARC 035 field</a>
 *
 * 035$a
 *
 * @author Benjamin D. Anderson
 *
 */
public class SystemControlNumberMatcher extends FieldMatcherService {

    protected Map<String, Long> prefixIds = new HashMap<String, Long>();
    protected TLongLongHashMap scn2outputIds = new TLongLongHashMap();

    protected long getPrefixId(String s) {
        // return the prefix String
        return 0l;
    }

    protected long getNumericId(String s) {
        // return the numeric portion
        return 0l;
    }

    protected long getMapId(String s) {
        return (getNumericId(s)*1000)+getPrefixId(s);
    }

    @Override
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
        List<Field> fields = ir.getDataFields(35);
        //String s = ir.getMARC().getDataFields().get(35).get('a');
        //return lccn2outputIds.get(getMapId(s));
        return null;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        List<Field> fields = r.getDataFields(35);
        // String s = r.getMARC().getDataFields().get(35).get('a');
        // lccn2outputIds.add(r.getId(), getMapId(s));
    }

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    public void flush(boolean freeUpMemory) {
        // TODO Auto-generated method stub

    }


}
