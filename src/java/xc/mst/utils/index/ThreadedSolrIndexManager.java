package xc.mst.utils.index;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.dao.DataException;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.utils.LogWriter;

/**
 * Multi-Threaded extension of SolrIndexManager. Overrides the addDoc method of SolrIndexManager 
 * and runs the add operation using one of the threads available in a pool of threads initialized. 
 * The size of the pool is hard coded.
 *
 * @author Vinaykumar Bangera
 */
public class ThreadedSolrIndexManager extends SolrIndexManager {

	
	/**
	 * Service for pipelining and executing tasks.
	 */
	private ExecutorService threadPool;
	
	/*
	 * Private default constructor
	 */
	private ThreadedSolrIndexManager() {}
	
	/**
	 * Creates a new instance of ThreadedSolrIndexManager with the given initial parameters.
	 * @param numThreads Size of the pool of threads
	 * @param maxQueueSize Size of the queue for waiting threads 
	 */
	private ThreadedSolrIndexManager(int numThreads, int maxQueueSize){
		
		/* Initialize the thread pool*/
		threadPool = new ThreadPoolExecutor(numThreads, 
											numThreads, 
											Long.MAX_VALUE, 
											TimeUnit.NANOSECONDS, 
											new ArrayBlockingQueue<Runnable>(maxQueueSize, false), 
											new ThreadPoolExecutor.CallerRunsPolicy());
		
		
	}
	
	
	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public static SolrIndexManager getInstance()
	{
		if(instance != null) {
			return instance;
		}

		if(log.isDebugEnabled()) {
			log.debug("Initializing the SolrIndexManager instance.");
		}
		
		server = MSTSolrServer.getServer();
		instance = new ThreadedSolrIndexManager(20, 20);
		return instance;
	}
	
	/**
	 * Adds a document to the Lucene index
	 *
	 * @param doc The document to add
	 * @return true on success, false on failure
	 */
	public boolean addDoc(SolrInputDocument doc) throws DataException
	{
		//System.out.println("Doc: " +doc.getDocumentBoost());
		log.debug("Add index to Solr - begin");
		
		// Check if solr server is null
		if (server == null) {
			log.error("Solr server is null");
			return false;
		}
		else{

			try{
				threadPool.execute(new Job(doc));
				
			}
			catch(RuntimeException e)
			{
				throw new DataException(e.getMessage());
			}
			return true;
		}
		
	}
	
	/**
	 * A task that adds a document to the SOLR Index Manager
	 * @author vinaykumarb
	 *
	 */
	private class Job implements Runnable{
		
		/**
		 * Document to be added to SOLR
		 */
		SolrInputDocument doc;
	
		/**
		 * Creates a new Job with the given initial parameters.
		 * @param doc Document to be added to SOLR
		 */
		public Job(SolrInputDocument doc){
				this.doc = doc;

	}
	
		/**
		 * Adds the document to SOLR in a separate thread
		*/
		public void run() throws RuntimeException{
		
				try {
					server.add(doc);
				} catch (SolrServerException se) {
					log.error("Solr server exception occured when adding document to the index.", se);
					
					LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index: " + se.getMessage());
					
					logObj.setErrors(logObj.getErrors()+1);
					try{
						logDao.update(logObj);
					}catch(DataException e){
						log.error("DataExcepiton while updating the log's error count.");
					}
					
					throw new RuntimeException(se.getMessage());
				} catch (IOException ioe) {
					log.debug(ioe);
					
					LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index: " + ioe.getMessage());
					
					logObj.setErrors(logObj.getErrors()+1);
					try{
						logDao.update(logObj);
					}catch(DataException e){
						log.error("DataExcepiton while updating the log's error count.");
					}
					
					throw new RuntimeException(ioe.getMessage());
				}
				log.debug("Add index to Solr - end");
			}
		
		}
	
	
	
	

}
