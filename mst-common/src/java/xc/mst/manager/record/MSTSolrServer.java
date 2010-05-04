/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.record;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.xml.sax.SAXException;

import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;

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

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration();

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
	public static MSTSolrServer getInstance()
	{
		if(instance != null) {
			return instance;
		}

		if(log.isDebugEnabled()) {
			log.debug("Initializing the MSTSolrServer instance.");
		}
		server = createSolrServer();
		instance = new MSTSolrServer();
		return instance;
	}

	/**
	 * Get Solr server instance
	 *
	 * @return
	 */
	private static SolrServer createSolrServer() {

		if (server == null)
		{
			String solrHome = MSTConfiguration.getUrlPath();
			solrHome = solrHome + MSTConfiguration.FILE_SEPARATOR + "solr";

			log.info("Opening Solr at 3" + solrHome);
			
			java.util.logging.Level logLevel = getLogLevel(configuration.getProperty(Constants.CONFIG_SOLR_LOG_LEVEL));
			log.info("1");
			
			try
			{
				java.util.logging.Logger logg = java.util.logging.Logger.getLogger("org.apache.solr");
			    logg.setUseParentHandlers(false);
			    logg.log(java.util.logging.Level.INFO, "Changing log level to " + logLevel);
			    logg.setLevel(logLevel);
			    log.info("2");

				CoreContainer container = new CoreContainer();
				CoreDescriptor descriptor = new CoreDescriptor(container, "core1", solrHome);
				log.info("3");
				SolrCore core = container.create(descriptor);
				container.register("core1", core, false);

				server = new EmbeddedSolrServer(container, "core1");
				LogWriter.addInfo(logObj.getLogFileLocation(), "The Solr server instance was successfully using the configuration in " + solrHome);
				log.info("successfully opened solr: "+server);
			}
			catch (IOException ioe)
			{
				log.error("Failure to create server instance. Solr Server is not created.", ioe);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to create Solr server instance using the configuration in " + solrHome);

				logObj.setErrors(logObj.getErrors()+1);
				try
				{
					logDao.update(logObj);
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.");
				}
			}
			catch (SAXException se)
			{
				log.error("Failure to create server instance. Solr Server is not created.", se);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to create Solr server instance using the configuration in " + solrHome);

				logObj.setErrors(logObj.getErrors()+1);
				try
				{
					logDao.update(logObj);
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.");
				}
			}
			catch (ParserConfigurationException pe)
			{
				log.error("Failure to create server instance. Solr Server is not created.", pe);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to create Solr server instance using the configuration in " + solrHome);

				logObj.setErrors(logObj.getErrors()+1);
				try
				{
					logDao.update(logObj);
				}
				catch(DataException e)
				{
					log.error("DataExcepiton while updating the log's error count.");
				}
			}
			catch (Exception e)
			{
				log.error("Failure to create server instance. Solr Server is not created.", e);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to create Solr server instance using the configuration in " + solrHome);

				logObj.setErrors(logObj.getErrors()+1);
				try
				{
					logDao.update(logObj);
				}
				catch(DataException e1)
				{
					log.error("DataExcepiton while updating the log's error count.");
				}
			}
		}

		return server;
	}

	private static java.util.logging.Level getLogLevel(String level)
	{
		if(level.equalsIgnoreCase("DEBUG"))
			return java.util.logging.Level.ALL;
		if(level.equalsIgnoreCase("INFO"))
			return java.util.logging.Level.INFO;
		if(level.equalsIgnoreCase("WARN"))
			return java.util.logging.Level.WARNING;
		if(level.equalsIgnoreCase("WARNING"))
			return java.util.logging.Level.WARNING;
		if(level.equalsIgnoreCase("ERROR"))
			return java.util.logging.Level.SEVERE;
		if(level.equalsIgnoreCase("OFF"))
			return java.util.logging.Level.OFF;
		if(level.equalsIgnoreCase("CONFIG"))
			return java.util.logging.Level.CONFIG;
		if(level.equalsIgnoreCase("ALL"))
			return java.util.logging.Level.ALL;
		if(level.equalsIgnoreCase("FINE"))
			return java.util.logging.Level.FINE;
		if(level.equalsIgnoreCase("FINER"))
			return java.util.logging.Level.FINER;
		if(level.equalsIgnoreCase("FINEST"))
			return java.util.logging.Level.FINEST;
		if(level.equalsIgnoreCase("SEVERE"))
			return java.util.logging.Level.SEVERE;

		return java.util.logging.Level.WARNING;
	}

	public  static SolrServer getServer() {
		return server;
	}

	public static void setServer(SolrServer server) {
		MSTSolrServer.server = server;
	}
}
