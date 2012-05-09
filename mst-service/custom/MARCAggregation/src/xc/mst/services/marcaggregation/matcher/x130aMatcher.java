package xc.mst.services.marcaggregation.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.RecordService;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.utils.TimingLogger;

public class x130aMatcher extends FieldMatcherService {

    protected Map<Long, String> inputId2x130a = new HashMap<Long, String>();  // this might be a temp hold place

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir) {
        /*
         // no fuzzy for now so lets not waste cycles doing anything...

        ArrayList<Long> results = new ArrayList<Long>();
        // query db for records with specific ids;
        final Long id = new Long(ir.recordId);
        if (results.contains(id)) {
            results.remove(id);
        }
*/
        return null;
    }

    @Override
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir, List<Long> filterBy) {
/*
         // no fuzzy for now so lets not waste cycles doing anything...

        SolrServer s = getMASSolrServer();
//        s.
        Map<Long, String> filterByFields = null; // query db for records with specific ids;
        //LuceneIndex tempIndex = new MemoryIndex();
        // add tokenizer
        for (Map.Entry<Long, String> fbField : filterByFields.entrySet()) {
            SolrInputDocument doc = new SolrInputDocument();
            //doc.addField("id", fbField.getKey());
            //doc.addField("field", fbField.getValue());
            //tempIndex.addDoc(doc);
        }
        //get scores tempIndex.search(ir.getSubField(130, 'a');
        // for results
        //    if score above x, then consider a match

         */
        return null;
    }

    @Override
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir2) {
        /*
         // no fuzzy for now so lets not waste cycles doing anything...

        List<String> subfields = r.getSubfield(130, 'a');
        if (subfields != null) {
            SolrServer s = getMASSolrServer();
            //LuceneIndex tempIndex = new MemoryIndex();
            //tempIndex.addDoc(doc);
            SolrInputDocument doc = null;
            for (String sf : subfields) {
                inputId2x130a.put(r.getRecordId(), sf);  //TODO elim use of this struct
//                doc = new SolrInputDocument();
//                doc.addField("field_key", sf);          // TODO make the field name correct for dynamic, i.e. _l
            }
            if (doc != null) {
                doc.addField(RecordService.FIELD_RECORD_ID, r.getRecordId());

                //TODO add to the index is not working - trying to use same index.  Separate directories?
                //  see - http://www.mattfitz.info/library/article/364
                //      though still not clear to me from this that it will address how indexes in data directory
                //      are used.
                try {
                    TimingLogger.start("SolrIndexService.process add doc");
                    getMASSolrIndexManager().addDoc(doc);
                    TimingLogger.stop("SolrIndexService.process add doc");
                } catch (IndexException ie) {
                    throw new RuntimeException(ie);
                }
            }
        }
        */
    }

    @Override
    public void load() {
        // nothing to do here;
        // nothing to do here?  well sure there is some is persisted, should  it be?
    }

    @Override
    public void flush(boolean freeUpMemory) {
        /*
         // no fuzzy for now so lets not waste cycles doing anything...

        MarcAggregationService s = (MarcAggregationService)config.getBean("MarcAggregationService");
        s.getMarcAggregationServiceDAO().persistOneStrMatchpointMaps(inputId2x130a, MarcAggregationServiceDAO.matchpoints_130a_table);
        inputId2x130a.clear();
        // the above might be wrong, persist to solr only?

         */
    }

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher() {
        return 0;//inputId2x130a.size();
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

    @Override
    public void removeRecordFromMatcher(InputRecord ir) {
        // TODO Auto-generated method stub
    }
}
