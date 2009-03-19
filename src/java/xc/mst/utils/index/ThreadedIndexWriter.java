/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils.index;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import xc.mst.constants.Constants;

/**
 * This class was taken from Lucene In Action 2nd edition Listing 10.1.
 *
 * It is a wrapper around Lucene's IndexWriter which provides the ability
 * to write to the index using multiple Threads, without the calling application
 * needing to worry about managing these Threads.
 */
public class ThreadedIndexWriter extends IndexWriter
{
	private ExecutorService threadPool;

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	private class Job implements Runnable
	{
		Document doc;
		Term delTerm;

		public Job(Document doc, Term delTerm)
		{
			this.doc = doc;
			this.delTerm = delTerm;
		}

		public void run()
		{
			try
			{
				if (delTerm != null)
					ThreadedIndexWriter.super.updateDocument(delTerm, doc);
				else
					ThreadedIndexWriter.super.addDocument(doc);
			}
			catch (CorruptIndexException e)
			{
				log.error("Corrupted Index Error occurred while writing to the index.", e);
			}
			catch (IOException ioe)
			{
				log.error("Error occurred while writing to the index.", ioe);
			}
		}
	}

	public ThreadedIndexWriter(String dir, Analyzer a, int numThreads, int maxQueueSize) throws CorruptIndexException, IOException
	{
		super(dir, a, new MaxFieldLength(Integer.MAX_VALUE));

		threadPool = new ThreadPoolExecutor(numThreads, numThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(maxQueueSize, false), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void addDocument(Document doc)
	{
		threadPool.execute(new Job(doc, null));
	}

	public void updateDocument(Term term, Document doc)
	{
		threadPool.execute(new Job(doc, term));
	}

	public void close() throws CorruptIndexException, IOException
	{
		finish();
		super.close();
	}

	public void abort() throws CorruptIndexException, IOException
	{
		finish();
		super.abort();
	}

	private void finish()
	{
		threadPool.shutdown();
		while(true)
		{
			try
			{
				if (threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS))
					break;
			}
			catch (InterruptedException ie)
			{
				Thread.currentThread().interrupt();
			}
		}
	}
}
