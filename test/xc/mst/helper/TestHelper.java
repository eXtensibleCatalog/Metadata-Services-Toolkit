package xc.mst.helper;

import xc.mst.manager.record.MSTSolrServer;
import xc.mst.utils.MSTConfiguration;


/**
 * This class helps to initialize database, solr, logs for test
 * 
 * @author Sharmila Ranganathan
 *
 */
public class TestHelper {
	
	public TestHelper() {
		loadConfiguration();
		initializeSolr();
	}

	/**
	 * Initialize database connection
	 */
	public void loadConfiguration() {
	    // Initialize MST configuration.
	    MSTConfiguration.getInstance("MetadataServicesToolkit");
	}
	
	/**
	 * Initialize Solr server
	 */
	public void initializeSolr() {
		MSTSolrServer.getInstance();
	}	
}
