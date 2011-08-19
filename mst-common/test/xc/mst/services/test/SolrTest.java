package xc.mst.services.test;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrInputDocument;
import org.testng.annotations.Test;

import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.common.test.BaseTest;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.BrowseRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.MSTConfiguration;

public class SolrTest extends BaseTest {

    private static final Logger LOG = Logger.getLogger(SolrTest.class);

    @Test
    public void update() {
        try {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField(RecordService.FIELD_RECORD_ID, 100);
            doc.addField(RecordService.FIELD_ALL, "beluga");

            getSolrIndexManager().addDoc(doc);
            getSolrIndexManager().commitIndex();

            doc = new SolrInputDocument();
            doc.addField(RecordService.FIELD_RECORD_ID, 100);
            doc.addField(RecordService.FIELD_ALL, "steelers");

            /*
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.addFilterQuery("");

            solrQuery.addField(RecordService.FIELD_RECORD_ID);
            solrQuery.addField(RecordService.FIELD_FORMAT_ID);
            solrQuery.addField(RecordService.FIELD_PROVIDER_ID);
            solrQuery.addField(RecordService.FIELD_SERVICE_ID);
            solrQuery.addField(RecordService.FIELD_HARVEST_SCHEDULE_NAME);
            solrQuery.addField(RecordService.FIELD_ERROR);
            solrQuery.addField(RecordService.FIELD_PROCESSED_FROM);
            solrQuery.addField(RecordService.FIELD_SUCCESSOR);
            solrQuery.addField(RecordService.FIELD_OAI_IDENTIFIER);


            BrowseRecordService browseRecordService = (BrowseRecordService)MSTConfiguration.getInstance().getBean("BrowseRecordService");
            SolrBrowseResult result = browseRecordService.search(solrQuery);

            getSolrIndexManager().addDoc(doc);
            getSolrIndexManager().commitIndex();
            */

        } catch (Throwable t) {
            LOG.error("", t);
            throw new RuntimeException(t);
        }
    }

}
