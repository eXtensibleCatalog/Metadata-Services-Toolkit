package xc.mst.services.marcaggregation;

import org.apache.log4j.Logger;

import xc.mst.manager.record.MSTSolrService;
import xc.mst.services.SolrIndexService;


/**
 * Creates Solr Server instance for use by MARC Aggregation Service
 *   Solr set up to run as multicore.
 *
 * @author John Brand
 *
 */
public class MASSolrService extends MSTSolrService {

    private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
    public void init() {
        super.init();
        LOG.info("*** MASSolrService init complete!");
    }

    protected String getSolrCore() {
        return "core2";
    }

}
