package xc.mst.services.marcaggregation.matcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xc.mst.bo.record.SaxMarcXmlRecord;

public class x130aMatcher extends FieldMatcherService {

    protected Map<Long, String> tempTable = new HashMap<Long, String>();

    @Override
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir) {
        // query db for records with specific ids;
        return null;
    }

    @Override
    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir, List<Long> filterBy) {
        Map<Long, String> filterByFields = null; // query db for records with specific ids;
        //LuceneIndex tempIndex = new MemoryIndex();
        // add tokenizer
        for (Map.Entry<Long, String> fbField : filterByFields.entrySet()) {
            //Doc doc = new Doc();
            //doc.addField("id", fbField.getKey());
            //doc.addField("field", fbField.getValue());
            //tempIndex.addDoc(doc);
        }
        //get scores tempIndex.search(ir.getSubField(130, 'a');
        // for results
        //    if score above x, then consider a match
        return null;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord ir) {
        List<String> subfields = ir.getSubfield(130, 'a');
        if (subfields != null) {
            for (String sf : subfields) {
                tempTable.put(ir.getRecordId(), sf);
            }
        }
    }

    @Override
    public void load() {
        // nothing to do here;
    }

    @Override
    public void flush(boolean freeUpMemory) {
        // TODO Auto-generated method stub
        // write to db
        tempTable.clear();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return tempTable.size();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return tempTable.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return 0;
    }
}
