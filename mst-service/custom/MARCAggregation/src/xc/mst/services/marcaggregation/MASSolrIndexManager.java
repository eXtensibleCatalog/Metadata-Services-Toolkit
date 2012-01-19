package xc.mst.services.marcaggregation;

import org.apache.solr.client.solrj.SolrServer;

import xc.mst.utils.index.SolrIndexManager;

/**
 * Solr Index manager for MARC Aggregation Service
 *
 * Allow access to the MAS Solr core, along with reusing the wrapper calls from SolrIndexManager
 *
 * @author John Brand
 *
 */
public class MASSolrIndexManager extends SolrIndexManager {
    public void init() {
        super.init();
        log.info("** MASSolrIndexManager initialized!");
    }
    protected SolrServer getMASSolrServer() {
        MASSolrService mss = (MASSolrService)config.getBean("MASSolrService");
        return mss.getServer();
    }

    protected SolrServer getSolrService() {
        return getMASSolrServer();
    }
}
