package xc.mst.services.marcaggregation.matcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;

/**
 *
 * The Library of Congress Control Number corresponds to the
 * <a href="http://www.loc.gov/marc/bibliographic/bd028.html">MARC 028 field</a>
 *
 * 028$ab
 * 028 can repeat, $a, $b subfields do not.
 * concat it all together?
 * remember, needs to work via solr, assoc. $a, $b with id via solr.
 *
 * @author JohnB
 *
 */
public class x028abMatcher extends FieldMatcherService {

    protected Map<Long, List<String>> inputId2x028ab = new HashMap<Long, List<String>>();  // this might be a temp hold place.

    // fuzzy see x130
    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        // TODO Auto-generated method stub
        return null;
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return null;
        //return recordId2x024a.keySet();
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir) {
        List<String> subfields = r.getSubfield(28, 'a');  // need 'b' subfields too...
        if (subfields != null) {
            for (String sf : subfields) {
//                inputId2x028ab.put(r.getRecordId(), sf);
            }
        }
    }

    @Override
    public void load() {
        // nothing to do here?  well sure there is some is persisted, should  it be?

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
        return 0;
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return 0;
    }

}
