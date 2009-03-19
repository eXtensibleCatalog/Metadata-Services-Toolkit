/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.utils.index;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;

/**
 * This class maintains a Lucene index whose Documents are the records
 * conatained in the Metadata Services Toolkit.
 *
 * Modified by Eric Osisek to be used by the Metadata Services Toolkit
 * (Original version was used by the OAI Toolkit.)
 *
 * @author kiru
 */
public class IndexManager_dummy
{
//	/**
//	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
//	 */
//	protected static final Configuration configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");
//
//	/**
//	 * The logger object
//	 */
//	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
//
//	/**
//	 * Used to add or update records in the Lucene index
//	 */
//	private ThreadedIndexWriter writer;
//
//	/**
//	 * The singleton instance of the LuceneIndexManager
//	 */
//	private static IndexManager instance = null;
//
//	/**
//	 * A map whose keys are IndexSearchers and whose values are the number of references to the
//	 * IndexSearcher.
//	 */
//	private HashMap<IndexSearcher, Integer> searcherRefCount = new HashMap<IndexSearcher, Integer>();
//
//	/**
//	 * The IndexSearcher that is being used for new searches
//	 */
//	private IndexSearcher searcher = null;
//
//	/**
//	 * Gets the singleton instance of the LuceneIndexManager
//	 */
//	public static IndexManager getInstance()
//	{
//		if(instance != null)
//			return instance;
//
//		if(log.isDebugEnabled())
//			log.debug("Initializing the LuceneIndexManager instance.");
//
//		instance = new IndexManager();
//		return instance;
//	}
//
//	/**
//	 * Constructs a LuceneIndexManager.  This opens the Lucene index in the directory defined in the
//	 * configuration file
//	 */
//	private IndexManager_dummy()
//	{
//		if(log.isDebugEnabled())
//			log.debug("Creating a new LuceneIndexManager.");
//
//		// Set the property which tells Lucene to use Native File System Locking.
//		// This will prevent the lock files from remaining if the MST crashes
//		System.setProperty("org.apache.lucene.store.FSDirectoryLockFactoryClass", "org.apache.lucene.store.NativeFSLockFactory");
//
//		openWriter(configuration.getProperty(Constants.CONFIG_LUCENE_INDEX_DIRECTORY));
//		openReader(configuration.getProperty(Constants.CONFIG_LUCENE_INDEX_DIRECTORY));
//	}
//
//	/**
//	 * Opens the Lucene searcher in the directory defined in the configuration file
//	 */
//	public void openReader()
//	{
//		if(log.isDebugEnabled())
//			log.debug("Opening the Reader for the Lucene Index.");
//
//		openReader(configuration.getProperty(Constants.CONFIG_LUCENE_INDEX_DIRECTORY));
//	}
//
//	/**
//	 * Open the index reader to the specified directory
//	 *
//	 * @param indexDir The directory to open the index reader to
//	 */
//	private void openReader(String indexDir)
//	{
//		if(log.isDebugEnabled())
//			log.debug("Opening the Reader for the Lucene Index at " + indexDir + ".");
//
//		try
//		{
//			// If the searcher was already open, call maybeReOpen instead of creating it from scratch.
//			// This will ensure that Threads using the old searcher will still be able to access it.
//			if(searcher != null)
//			{
//				if(log.isDebugEnabled())
//					log.debug("Reopening the searcher.");
//
//				maybeReOpen();
//				return;
//			}
//
//			if(log.isDebugEnabled())
//				log.debug("Setting up the searcher.");
//
//			// Set up the Lucene searcher
//			File dir = new File(indexDir);
//			Directory fsDir = FSDirectory.getDirectory(dir);
//			searcher = new IndexSearcher(fsDir);
//			initRefCount(searcher); // Adds an entry for searcher in searcherRefCount with a count of 1
//		}
//		catch (Exception e)
//		{
//			log.error("An error occurred while opening the Reader for the Lucene index.", e);
//		}
//	}
//
//	/**
//	 * Opens the Lucene writer in the directory defined in the configuration file
//	 */
//	public void openWriter()
//	{
//		if(log.isDebugEnabled())
//			log.debug("Opening the Writer for the Lucene Index.");
//
//		openWriter(configuration.getProperty(Constants.CONFIG_LUCENE_INDEX_DIRECTORY));
//	}
//
//	/**
//	 * Open the index writer to the specified directory
//	 *
//	 * @param indexDir The directory to open the index writer to
//	 */
//	private void openWriter(String indexDir)
//	{
//		if(log.isDebugEnabled())
//			log.debug("Opening the Writer for the Lucene Index at " + indexDir + ".");
//
//		try
//		{
//			if(log.isDebugEnabled())
//				log.debug("Setting up the writer.");
//
//			// Set up the Lucene writer
//			writer = new ThreadedIndexWriter(indexDir, new StandardAnalyzer(), 2, 8);
//
//			// Get the RAM Buffer size from the configuration file
//			String ramBufferSize = configuration.getProperty(Constants.CONFIG_LUCENE_INDEXER_RAM_BUFFER_MB);
//			if(ramBufferSize != null)
//			{
//				try
//				{
//					double ramBufferSizeD = Double.parseDouble(ramBufferSize);
//
//					if(log.isInfoEnabled())
//						log.info("The index will use " + ramBufferSizeD + "MB of RAM.");
//
//					writer.setRAMBufferSizeMB(ramBufferSizeD);
//				}
//				catch(NumberFormatException e)
//				{
//					log.error("Invalid indexer RAM buffer size " + ramBufferSize + ", the RAM buffer size must be an integer.  The default RAM buffer size of 16MB will be used.");
//				}
//			}
//
//			writer.setMaxBufferedDocs(Integer.MAX_VALUE);
//			writer.setMergeFactor(200);
//		}
//		catch (Exception e)
//		{
//			log.error("An error occurred while opening the Writer for the Lucene index.", e);
//		}
//	}
//
//	/**
//	 * Closes the Reader's connection to the Lucene index
//	 */
//	public void closeReader()
//	{
//		if (searcher != null)
//		{
//			try
//			{
//				searcher.close();
//				release(searcher);
//				searcher = null;
//			}
//			catch (IOException e)
//			{
//				log.error("An error occurred while closing the Reader to the Lucene index.", e);
//			}
//		}
//	}
//
//	/**
//	 * Closes the Writer's connection to the Lucene index
//	 */
//	public void closeWriter()
//	{
//		if (writer != null)
//		{
//			try
//			{
//				writer.close();
//			}
//			catch (IOException e)
//			{
//				log.error("An error occurred while closing the Writer to the Lucene index.", e);
//			}
//		}
//	}
//
//	/**
//	 * Optimizes the Lucene index
//	 */
//	public void optimize()
//	{
//		if(log.isDebugEnabled())
//			log.debug("Optimizing the Lucene index.");
//
//		if (writer != null)
//		{
//			try
//			{
//				writer.optimize();
//			}
//			catch (IOException e)
//			{
//				log.error("Error optimizing the Lucene index.", e);
//			}
//		}
//	}
//
//	/**
//	 * Adds a document to the Lucene index
//	 *
//	 * @param doc The document to add
//	 * @return true on success, false on failure
//	 */
//	public boolean addDoc(Document doc)
//	{
//		writer.addDocument(doc);
//		return true;
//	}
//
//	/**
//	 * Deletes documents from the Lucene index which match the passed field, value pair
//	 * All documents where field %fieldName% equals %value% will be deleted.
//	 *
//	 * @param fieldName The name of the field we're checking
//	 * @param value The value of the field to delete.
//	 * @return true on success, false on failure
//	 */
//	public boolean deleteDoc(String fieldName, String value)
//	{
//		try
//		{
//			writer.deleteDocuments(new Term(fieldName, value));
//			return true;
//		}
//		catch (CorruptIndexException e)
//		{
//			log.error("Corrupt Index Error adding a document to the Lucene index.", e);
//			return false;
//		}
//		catch (IOException e)
//		{
//			log.error("Error adding a document to the Lucene index.", e);
//			return false;
//		}
//	}
//
//	/**
//	 * Updates the document from the Lucene index which matches the passed field/value pair,
//	 * and replaces it with the provided document.
//	 *
//	 * @param fieldName The name of the field we're checking
//	 * @param value The value of the field to update.
//	 * @param replacement The document to replace the document to update with
//	 * @return true on success, false on failure.
//	 */
//	public boolean updateDoc(String fieldName, String value, Document replacement)
//	{
//		writer.updateDocument(new Term(fieldName, value), replacement);
//		return true;
//	}
//
//	/**
//	 * Return a field which is tokenized and stored.
//	 * Text fields are stored as full text indexes which may be queried.
//	 *
//	 * @param name The name of the field
//	 * @param content The content of the field
//	 * @return The Field object
//	 */
//	public Field text(String name, String content)
//	{
//		return new Field(name, content, Store.YES, Index.TOKENIZED);
//	}
//
//	/**
//	 * Return a field which is indexed but not tokenized.
//	 * Keyword fields are meant to be queried.
//	 *
//	 * @param name The name of the field
//	 * @param content The content of the field
//	 * @return The Field object
//	 */
//	public Field keyword(String name, String content)
//	{
//		return new Field(name, content, Store.YES, Index.UN_TOKENIZED);
//	}
//
//	/**
//	 * Return a field which is indexed but not tokenized.
//	 * Keyword fields are meant to be queried.
//	 *
//	 * @param name The name of the field
//	 * @param content The content of the field
//	 * @return The Field object
//	 */
//	public Field keyword(String name, Date content)
//	{
//		return new Field(name, DateTools.dateToString(content, DateTools.Resolution.SECOND), Store.YES, Index.UN_TOKENIZED);
//	}
//
//	/**
//	 * Return a field which is stored but not indexed.
//	 *
//	 * @param name The name of the field
//	 * @param content The content of the field
//	 * @return The Field object
//	 */
//	public Field stored(String name, String content)
//	{
//		return new Field(name, content, Store.YES, Index.NO);
//	}
//
//	/**
//	 * Runs the passed query on the Lucene index and returns
//	 * the first hit.
//	 *
//	 * @param query The query to run.
//	 * @return The first hit returned by the query, or null if there were no hits.
//	 * @throws DataException If the index could not be opened
//	 */
//	public Document getDocument(Query query) throws DataException // TODO: Do we need to pass a reference to the searcher to use as well?
//	{
//		if(log.isDebugEnabled())
//			log.debug("Running the query " + query.toString() + ".");
//
//		try
//		{
//			if(searcher == null)
//			{
//				openReader();
//				if(searcher == null)
//					throw new DataException("The index specified in the configuration file could not be opened.");
//			}
//
//			Hits hits = searcher.search(query);
//			return (hits.length() > 0 ? hits.doc(0) : null);
//		}
//		catch(IOException e)
//		{
//			log.error("An error occurred while running the query " + query.toString() + ".", e);
//
//			return null;
//		}
//	}
//
//	/**
//	 * Runs the passed query on the Lucene index and returns
//	 * the list of hits.
//	 *
//	 * @param query The query to run.
//	 * @return The hits returned by the query.
//	 */
//	public Hits getHits(Query query) // TODO: Do we need to pass a reference to the searcher to use as well?
//	{
//		if(log.isDebugEnabled())
//			log.debug("Running the query " + query.toString() + ".");
//
//		try
//		{
//			return searcher.search(query);
//		}
//		catch(IOException e)
//		{
//			log.error("An error occurred while running the query " + query.toString() + ".", e);
//
//			return null;
//		}
//	}
//
//	/**
//	 * Primes a searcher by running a small set of searches hitting a wide variety of
//	 * documents in the index.
//	 *
//	 * This method has not yet been implemented.
//	 *
//	 * @param searcher The IndexSearcher to prime
//	 */
//	public void warm(IndexSearcher searcher)
//	{
//		/*if(log.isDebugEnabled())
//			log.debug("Getting all records to warm the searcher after reopening.");
//
//		Record.getAll();
//
//		if(log.isDebugEnabled())
//			log.debug("Finished warming the searcher.");*/
//	}
//
//	/**
//	 * Reopens the index if any changes were made to it.
//	 *
//	 * @throws IOException If an error occurred while reopening the index
//	 */
//	public void maybeReOpen() throws IOException
//	{
//		writer.flush();
//
//		// Get the current version of the index
//		long currentVersion = searcher.getIndexReader().getVersion();
//
//		// If the current version is different from the version we're using, reopen the index
//		if (IndexReader.getCurrentVersion(configuration.getProperty(Constants.CONFIG_LUCENE_INDEX_DIRECTORY)) != currentVersion)
//		{
//			// Reopen the index
//			IndexReader newReader = searcher.getIndexReader().reopen();
//			assert newReader != searcher.getIndexReader();
//
//			// Set up the searcher to use the new index
//			IndexSearcher newSearcher = new IndexSearcher(newReader);
//
//			// Prime the searcher to improve preformance
//			warm(newSearcher);
//
//			// Replace the current searcher with the new searcher, maintaining a link
//			// to the current searcher if and only if any other Threads are accessing it.
//			swapSearcher(newSearcher);
//		}
//	}
//
//	/**
//	 * Returns a reference to the current searcher, and increments the
//	 * reference count for that searcher.
//	 *
//	 * @return A reference to the current searcher
//	 */
//	public synchronized IndexSearcher get()
//	{
//		int refCount = getRefCount(searcher);
//		searcherRefCount.put(searcher, new Integer(1+refCount));
//		return searcher;
//	}
//
//	/**
//	 * Called by a Thread when it is finished with the IndexSearcher.  This
//	 * reduces the searcher's reference count by 1, and closes it once the last
//	 * reference is removed.
//	 *
//	 * @param searcher The IndexSearcher the caller is finished using
//	 * @throws IOException If an error occurred while closing the searcher
//	 */
//	public synchronized void release(IndexSearcher searcher) throws IOException
//	{
//		int refCount = getRefCount(searcher);
//		if (refCount == 1)
//		{
//			searcher.close();
//			searcherRefCount.remove(searcher);
//		}
//		else
//			searcherRefCount.put(searcher, new Integer(refCount-1));
//	}
//
//	/**
//	 * Gets the number of references to the passed IndexSearcher
//	 *
//	 * @param searcher The IndexSearcher we want the reference count for
//	 * @return The number of references to the passed IndexSearcher
//	 */
//	private synchronized int getRefCount(IndexSearcher searcher)
//	{
//		Integer refCount = (Integer) searcherRefCount.get(searcher);
//		return refCount.intValue();
//	}
//
//	/**
//	 * Adds the passed IndexSearcher to the searcherRefCount map
//	 * with a reference count of 1 (since we're referencing it here.)
//	 *
//	 * @param searcher The searcher whose reference count is to be initialized
//	 */
//	private synchronized void initRefCount(IndexSearcher searcher)
//	{
//		searcherRefCount.put(searcher, new Integer(1));
//	}
//
//	/**
//	 * Switches the current IndexSearcher with the passed searcher, and
//	 * releases our reference to the passed searcher.
//	 *
//	 * @param newSearcher The IndexSearcher which should be used.
//	 * @throws IOException If an error occurred releasing our reference to the old searcher
//	 */
//	private synchronized void swapSearcher(IndexSearcher newSearcher) throws IOException
//	{
//		release(searcher);
//		searcher = newSearcher;
//		initRefCount(searcher);
//	}
//
//	/**
//	 * Optimizes the Lucene index
//	 */
//	public static void optimizeIndex()
//	{
//		// Optimize the index.
//		getInstance().optimize();
//
//		// Close the old connection to the index, then reopen it.
//		try
//		{
//			getInstance().maybeReOpen();
//		}
//		catch(IOException e)
//		{
//			log.error("An error occurred while reopening the Lucene index.", e);
//		}
//	}
//
//	/**
//	 * Closes the Lucene index
//	 */
//	public static void closeIndex()
//	{
//		getInstance().closeWriter();
//		getInstance().closeReader();
//	}
//
//	/**
//	 * Commits the Lucene index
//	 */
//	public static void commitIndex()
//	{
//		try
//		{
//			getInstance().writer.commit();
//		}
//		catch (CorruptIndexException e)
//		{
//			log.error("An corrupt index exception occurred while commiting the Lucene index.", e);
//		}
//		catch (IOException e)
//		{
//			log.error("An error occurred while commiting the Lucene index.", e);
//		}
//	}
//
//	/**
//	 * Closes and reopens the Lucene index writer
//	 */
//	public static void reopenIndexWriter()
//	{
//		getInstance().closeWriter();
//		getInstance().openWriter();
//	}
}
