package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;

public class x130aMatcher extends FieldMatcherService {

    protected Map<Long, String> inputId2x130a = new HashMap<Long, String>();  // this might be a temp hold place

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        ArrayList<Long> results = new ArrayList<Long>();
        // query db for records with specific ids;
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
        }

        return null;
    }

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir, List<Long> filterBy) {
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
                inputId2x130a.put(ir.getRecordId(), sf);
            }
        }
    }

    @Override
    public void load() {
        // nothing to do here;
        // nothing to do here?  well sure there is some is persisted, should  it be?
    }

    @Override
    public void flush(boolean freeUpMemory) {
        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().persistOneStrMatchpointMaps(inputId2x130a, MarcAggregationServiceDAO.matchpoints_130a_table);
        inputId2x130a.clear();
        // the above might be wrong, perist to solr only?
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return inputId2x130a.size();
    }
    public Collection<Long> getRecordIdsInMatcher() {
        return inputId2x130a.keySet();
    }

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher() {
        return 0;
    }
}
