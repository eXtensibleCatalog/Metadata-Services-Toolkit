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

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.utils.LogWriter;

/**
 * Solr Index manager
 * 
 * @author Sharmila Ranganathan
 *
 */
public class SolrIndexManager {

	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Solr server */
	protected static SolrServer server = null;

	/**
	 * The singleton instance of the LuceneIndexManager
	 */
	protected static SolrIndexManager instance = null;

	/**
	 * Data access object for managing general logs
	 */
	protected static LogDAO logDao = new DefaultLogDAO();

	/**
	 * The repository management log file name
	 */
	protected static Log logObj = null;
	
	/*
	 * Private default constructor
	 */
	protected SolrIndexManager() 
	{
		try
		{
			logObj = (new DefaultLogDAO()).getById(Constants.LOG_ID_SOLR_INDEX);
		}
		catch(DatabaseConfigException e)
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
		}
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
		instance = new SolrIndexManager();
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
		if (log.isDebugEnabled()) {
			log.debug("Add index to Solr - begin");
		}
		
		// Check if solr server is null
		if (server == null) {
			log.error("Solr server is null");
			return false;
		}
		
		try {
			server.add(doc);
		} catch (SolrServerException se) {
			log.error("Solr server exception occured when adding document to the index. Check the path to solr folder.", se);
			
			LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index. Check the path to solr folder." 
					+ se.getMessage());
			
			logObj.setErrors(logObj.getErrors()+1);
			try{
				logDao.update(logObj);
			}catch(DataException e){
				log.error("DataExcepiton while updating the log's error count.", e);
			}
			
			throw new IndexException(se.getMessage());
		} catch (IOException ioe) {
			log.debug(ioe);
			
			LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while adding a document to the Solr index. Check the path to solr folder." 
					+ ioe.getMessage());
			
			logObj.setErrors(logObj.getErrors()+1);
			try{
				logDao.update(logObj);
			}catch(DataException e){
				log.error("DataExcepiton while updating the log's error count.", e);
			}
			
			throw new IndexException(ioe.getMessage());
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Add index to Solr - end");
		}
		return true;
	}

	public boolean deleteByQuery(String query)
	{
		try 
		{
			server.deleteByQuery(query);
			return true;
		} 
		catch (SolrServerException se) 
		{
			log.error("Solr server exception occured when deleting document in the index. Check the path to solr folder.", se);
			
			LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while deleting document in the index. Check the path to solr folder." 
					+ se.getMessage());
			
			logObj.setErrors(logObj.getErrors()+1);
			try{
				logDao.update(logObj);
			}catch(DataException e){
				log.error("DataExcepiton while updating the log's error count.", e);
			}
			
			return false;
		} 
		catch (IOException ioe) 
		{
			log.error("IO exception occured when deleting document in the index. Check the path to solr folder.", ioe);
			
			LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while deleting document in the index. Check the path to solr folder." 
					+ ioe.getMessage());
			
			logObj.setErrors(logObj.getErrors()+1);
			try{
				logDao.update(logObj);
			}catch(DataException e){
				log.error("DataExcepiton while updating the log's error count.", e);
			}
			
			return false;
		}
	}
	
	/**
	 * Makes the calling Thread yield until all add/update/delete jobs have completed.
	 * 
	 * Since SolrIndexManager is single Threaded, this method does nothing
	 */
	public void waitForJobCompletion(long timeout)
	{	
	}
	
	/**
	 * Adds a document to the solr index
	 *
	 * @param doc The document to add
	 * @return true on success, false on failure
	 */
	public boolean commitIndex() throws IndexException
	{
		if (log.isDebugEnabled()) {
			log.debug("Commit index to Solr - begin");
		}

		// Check if solr server is null
		if (server == null) {
			log.error("Solr server is null");
			return false;
		}
		try {
			server.commit();
			LogWriter.addInfo(logObj.getLogFileLocation(), "Commited changes to the Solr index");
		} catch (SolrServerException se) {
			log.error("Solr server exception occured when commiting to the index. Check the path to solr folder.", se);
			
			LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while commiting changes to the Solr index. Check the path to solr folder." 
					+ se.getMessage());
			logObj.setErrors(logObj.getErrors()+1);
			try {
				logDao.update(logObj);
			} catch(DataException e){
				log.error("DataExcepiton while updating the log's error count.");
			}
			
			throw new IndexException(se.getMessage());
		} catch (IOException ioe) {
			log.error("IO exception occured when commiting to the index. Check the path to solr foldert." + ioe);
			
			LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while commiting changes to the Solr index. Check the path to solr folder." 
					+ ioe.getMessage());
			
			logObj.setErrors(logObj.getErrors()+1);
			try{
				logDao.update(logObj);
			}catch(DataException e){
				log.error("DataExcepiton while updating the log's error count.");
			}
			
			throw new IndexException(ioe.getMessage());
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Commit index to Solr - end");
		}
		return true;
	}

	/**
	 * Method to get search result documents
     *
	 * @param query Query to perform the search
	 * @return Search results
	 */
	public SolrDocumentList getDocumentList(SolrQuery query) throws IndexException {

		SolrDocumentList docs = null;
		
		// Check if solr server is null
		if (server == null) {
			log.error("Solr server is null");
			return docs;
		}
		
		try {

		    QueryResponse rsp = server.query( query );
		    docs = rsp.getResults();

		} catch (SolrServerException e) {
				log.error("An error occurred while getting documents from the Solr index. Check the path to solr folder.", e);
				
				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while getting documents from the Solr index. Check the path to solr folder." 
						+ e.getMessage());
				
				logObj.setErrors(logObj.getErrors()+1);
				try{
					logDao.update(logObj);
				}catch(DataException e2){
					log.error("DataExcepiton while updating the log's error count.");
				}
				throw new IndexException(e.getMessage());
		}

		return docs;
	}
}
