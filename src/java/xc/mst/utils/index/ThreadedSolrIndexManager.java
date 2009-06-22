package xc.mst.utils.index;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.EndianUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.utils.LogWriter;

/**
 * Multi-Threaded extension of SolrIndexManager. Overrides the addDoc method of SolrIndexManager
 * and runs the add operation using one of the threads available in a pool of threads initialized.
 * The size of the pool is hard coded.
 *
 * @author Vinaykumar Bangera
 */
public class ThreadedSolrIndexManager extends SolrIndexManager
{
	/**
	 * Service for pipelining and executing tasks.
	 */
	private ExecutorService threadPool;

	/**
	 * Service for pipelining and executing tasks.
	 */
	private static Configuration configuration = ConfigurationManager.getConfiguration();

	/**
	 * The number of Jobs which have been scheduled to run but have not been completed
	 */
	private int numQueuedJobs = 0;

	/**
	 * Private default constructor
	 */
	private ThreadedSolrIndexManager() {}

	/**
	 * Creates a new instance of ThreadedSolrIndexManager with the given initial parameters.
	 * @param numThreads Size of the pool of threads
	 * @param maxQueueSize Size of the queue for waiting threads
	 */
	private ThreadedSolrIndexManager(int numThreads, int maxQueueSize)
	{

		log.info("SolrIndexManager Thread Pool Initialized");

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
		if(instance != null)
		{
			return instance;
		}

		log.info("Initializing the SolrIndexManager instance.");

		// Get the queue size and pool size for threads
		int poolSize = Integer.parseInt(configuration.getProperty("SOLRIndexerMultiThreadCount")) == 0 ? 20 : Integer.parseInt(configuration.getProperty("SOLRIndexerMultiThreadCount")) ;

		server = MSTSolrServer.getServer();

		instance = new ThreadedSolrIndexManager(poolSize, poolSize);
		return instance;
	}

	/**
	 * Adds a document to the Lucene index
	 *
	 * @param doc The document to add
	 * @return true on success, false on failure
	 */
	public boolean addDoc(SolrInputDocument doc) throws IndexException
	{
		log.debug("Add index to Solr - begin");

		// Check if solr server is null
		if (server == null)
		{
			log.error("Solr server is null");

			return false;
		}
		else
		{
			try
			{
				synchronized(this)
				{
					threadPool.execute(new Job(doc));
					log.info("incrementing numQueuedJobs");
					numQueuedJobs++;
					log.info("incremented numQueuedJobs: " + numQueuedJobs);
				}
			}
			catch(RuntimeException e)
			{	
				throw new IndexException(e.getMessage());
			}

			return true;
		}
	}

	/**
	 * Makes the calling Thread yield until all add/update/delete jobs have completed.
	 */
	public void waitForJobCompletion(long timeout)
	{
		long start = System.currentTimeMillis();
		
		while(numQueuedJobs > 0)
		{
			if(timeout < System.currentTimeMillis() - start)
			{
				log.warn("ThreadedSolrIndexManager's jobs aren't getting marked as finished, timeout of " + timeout + " was reached.");
				break;
			}
				
			Thread.yield();
		}
	}
	
	/**
	 * A task that adds a document to the SOLR Index Manager
	 * @author vinaykumarb
	 *
	 */
	private class Job implements Runnable
	{
		/**
		 * Document to be added to SOLR
		 */
		SolrInputDocument doc;

		/**
		 * Creates a new Job with the given initial parameters.
		 * @param doc Document to be added to SOLR
		 */
		public Job(SolrInputDocument doc)
		{
				this.doc = doc;
		}

		/**
		 * Adds the document to SOLR in a separate thread
		 */
		public void run() throws RuntimeException
		{
			try 
			{
				server.add(doc);
			}
			catch (SolrServerException se)
			{
				log.error("Solr server exception occured when adding document to the index.", se);

				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index: " + se.getMessage());

				logObj.setErrors(logObj.getErrors()+1);

				try
				{
					logDao.update(logObj);
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.");
				}

				throw new RuntimeException(se.getMessage());
			}
			catch (IOException ioe)
			{
				log.error("An IOException occurred", ioe);

				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index: " + ioe.getMessage());

				logObj.setErrors(logObj.getErrors()+1);

				try
				{
					logDao.update(logObj);
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.");
				}

				throw new RuntimeException(ioe.getMessage());
			}
			finally
			{
				synchronized(this)
				{
					log.info("decrementing numQueuedJobs");
					numQueuedJobs--;
					log.info("decremented numQueuedJobs: " + numQueuedJobs);
				}
			}
			
			log.debug("Add index to Solr - end");
		}
	}
}
