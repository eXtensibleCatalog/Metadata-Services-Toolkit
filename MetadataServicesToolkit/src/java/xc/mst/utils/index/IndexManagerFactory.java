package xc.mst.utils.index;

import xc.mst.constants.Constants;

/**
 * Creates instances of SolrIndexManager depending upon the configuration passed to it.
 * 
 * @author Vinaykumar Bangera
 *
 */
public class IndexManagerFactory {

	/**
	 * Gets the instance of SolrIndexManager depending upon the configuration passed to it.
	 * @param threadingModel The string parameter for specifying the threading model
	 */
	public static SolrIndexManager getIndexManager(String threadingModel){
		
		// If the configuration is set for single threading
		if(threadingModel.equals(Constants.SOLR_INDEXER_SINGLE_THREADED))
			return SolrIndexManager.getInstance();
		
		// If the configuration is set for multi-threading		
		else if(threadingModel.equals(Constants.SOLR_INDEXER_MULTI_THREADED))
			return ThreadedSolrIndexManager.getInstance();

		// Default
		return SolrIndexManager.getInstance();
	}
}
