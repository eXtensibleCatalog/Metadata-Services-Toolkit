package xc.mst.utils.index;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.scheduling.Scheduler;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;

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
	protected ExecutorService threadPool;

	/**
	 * The number of Jobs which have been scheduled to run but have not been completed
	 */
	protected int numQueuedJobs = 0;
	
	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public void init()
	{
		super.init();
		log.info("Initializing the SolrIndexManager instance.");

		// Get the queue size and pool size for threads
		int poolSize = Integer.parseInt(
				MSTConfiguration.getProperty("SOLRIndexerMultiThreadCount")) == 0 ? 20 : Integer.parseInt(MSTConfiguration.getProperty("SOLRIndexerMultiThreadCount")) ;


		log.info("SolrIndexManager Thread Pool Initialized");

		/* Initialize the thread pool*/
		threadPool = new ThreadPoolExecutor(poolSize,
											poolSize,
											Long.MAX_VALUE,
											TimeUnit.NANOSECONDS,
											new ArrayBlockingQueue<Runnable>(poolSize, false),
											new ThreadPoolExecutor.CallerRunsPolicy());
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
		if (getMstSolrServer().getServer() == null)
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
					log.debug("incrementing numQueuedJobs");
					numQueuedJobs++;
					log.debug("incremented numQueuedJobs: " + numQueuedJobs);
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
				getMstSolrServer().getServer() .add(doc);
			}
			catch (SolrServerException se)
			{
				log.error("Solr server exception occured when adding document to the index.", se);

				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index: " + se.getMessage());

				logObj.setErrors(logObj.getErrors()+1);

				try
				{
					getLogDAO().update(logObj);
					Scheduler.getRunningJob().cancel();
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.", e);
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
					getLogDAO().update(logObj);
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.", e);
				}

				throw new RuntimeException(ioe.getMessage());
			}
			finally
			{
				synchronized(this)
				{
					log.debug("decrementing numQueuedJobs");
					numQueuedJobs--;
					log.debug("decremented numQueuedJobs: " + numQueuedJobs);
				}
			}
			
			log.debug("Add index to Solr - end");
		}
	}
}
