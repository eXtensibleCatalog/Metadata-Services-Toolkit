package xc.mst.services.marcaggregation.matcher;

import java.util.Collection;
import java.util.List;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;

public class x260abcMatcher extends FieldMatcherService {

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir) {
        // TODO Auto-generated method stub

    }

    @Override
    public void load(boolean firstTime) {
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
        return 0;
    }

    public Collection<Long> getRecordIdsInMatcher() {
        return null;
        //return recordId2x024a.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return 0;
    }

    @Override
    public void removeRecordFromMatcher(InputRecord ir) {
        // TODO Auto-generated method stub

    }
}
