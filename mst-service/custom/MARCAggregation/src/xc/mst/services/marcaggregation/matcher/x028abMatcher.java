package xc.mst.services.marcaggregation.matcher;

import java.util.List;

import xc.mst.bo.record.SaxMarcXmlRecord;

/**
 *
 * The Library of Congress Control Number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd028.html">MARC 028 field</a>
 *
 * 028$ab
 *
 * @author JohnB
 *
 */
public class x028abMatcher extends FieldMatcherService {

    @Override
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r) {
        // TODO Auto-generated method stub

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
