/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.manager.record;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;

import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.utils.LogWriter;

/**
 * Creates Solr Server instance
 * 
 * @author Sharmila Ranganathan
 *
 */
public class MSTSolrServer {

	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Solr server */
	private static SolrServer server = null;

	/**
	 * The singleton instance of the LuceneIndexManager
	 */
	private static MSTSolrServer instance = null;

	/**
	 * Data access object for managing general logs
	 */
	private static LogDAO logDao = new DefaultLogDAO();

	/**
	 * The repository management log file name
	 */
	private static Log logObj = null;
	
	static
	{
		try 
		{
			logObj = (new DefaultLogDAO()).getById(Constants.LOG_ID_SOLR_INDEX);
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Cannot connect to the database with the parameters in the configuration file.", e);
		}
	}
	
	/**
	 * Default constructor
	 */
	private MSTSolrServer() 
	{
		
	}
	
	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public static MSTSolrServer getInstance(String port)
	{
		if(instance != null) {
			return instance;
		}

		if(log.isDebugEnabled()) {
			log.debug("Initializing the MSTSolrServer instance.");
		}
		server = createSolrServer(port);
		instance = new MSTSolrServer();
		return instance;
	}

	/**
	 * Get Solr server instance
	 *
	 * @return
	 */
	private static SolrServer createSolrServer(String port) {

		if (server == null) {
			String url = "http://localhost:" + port + "/solr/";

			try {				
				server = new CommonsHttpSolrServer( url );
				LogWriter.addInfo(logObj.getLogFileLocation(), "The Solr server instance was successfully created at " + url);
				
				
			} catch (MalformedURLException me) {
				log.error("Failure to create server instance. Solr Server is not created.", me);
				
				LogWriter.addError(logObj.getLogFileLocation(), "Failed to create Solr server instance at " + url);
				
				logObj.setErrors(logObj.getErrors()+1);
				try{
					logDao.update(logObj);
				}catch(DataException e){
					log.error("DataExcepiton while updating the log's error count.");
				}
			}
			
/*			try {
			     SolrPingResponse ping = server.ping();
			     LogWriter.addInfo(logObj.getLogFileLocation(), "Got ping reply in " + ping.getElapsedTime() + "ms");
			} catch (Exception e) {
				log.error("Ping failed on solr server.", e);
			
				LogWriter.addError(logObj.getLogFileLocation(), "Ping failed on solr server.");
				
				logObj.setErrors(logObj.getErrors()+1);
				try{
					logDao.update(logObj);
				}catch(DataException de){
					log.error("DataExcepiton while updating the log's error count.");
				}
				
			 }*/

		}
		return server;
	}

	public  static SolrServer getServer() {
		return server;
	}
}
