/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.helper;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import xc.mst.bo.log.Log;
import xc.mst.dao.DataException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
 

/**
 * This class helps to initialize database, solr, logs for test
 * 
 * @author Sharmila Ranganathan
 *
 */
public class TestHelper {
	

	/**
	 * The singleton instance 
	 */
	private static TestHelper instance = null;
	
	protected static ApplicationContext applicationContext = null;
	
	/** Constructor */
	private TestHelper() {}

	
	public static TestHelper getInstance() {
		if(instance != null) {
			
			return instance;
		}

		loadConfiguration();
		initializeLog();
		initializeSolr();
		
		instance = new TestHelper();
		return instance;		

	}

	/**
	 * Initialize database connection.
	 * Loads the MetadataServicesToolkit_config.xml in workspace\MetadataServicesToolkit\MetadataServicesToolkit_config.xml
	 */
	public static void loadConfiguration() {
		applicationContext = new ClassPathXmlApplicationContext(new String[] {"spring-mst.xml"});
	}
	
	/**
	 * Initialize log
	 * 
	 * Loads the log4j.config.txt in workspace\MetadataServicesToolkit\src\java\log4j.config.txt
	 */
	public  static void initializeLog() {

		PropertyConfigurator.configure(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "log4j.config.txt");
		
	    // Initialize the general MST logs
	    LogDAO logDao = new DefaultLogDAO();
	    List<Log> logs = null;
		try 
		{
			logs = logDao.getAll();
			// Update log file path
			for(Log log : logs) {
				// add the path if its not added previously. In case of server restart, the path need not be added since it would have been
				// addeed the fisrt time server was restarted.
				/* BDA 2010-05-07 I have no idea why you would want to do this
				if (log.getLogFileLocation().indexOf(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR) == -1) {
					log.setLogFileLocation(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + log.getLogFileLocation());
			    	logDao.update(log);
				}
				*/
			}
			
			logs = logDao.getAll();
		} 
		catch (DataException e) 
		{
			return;
		}
		
	
	    for(Log log : logs) {
	    	LogWriter.addInfo(log.getLogFileLocation(), "Beginning logging for " + log.getLogFileName() + ".");
	    }

		
	}
	
	/**
	 * Initialize Solr server
	 * 
	 * Loads the solr in  in workspace\MetadataServicesToolkit\MST-instances\MetadataServicesToolkit\solr.
	 * Data(index) will be created under workspace\MetadataServicesToolkit\MST-instances\MetadataServicesToolkit\solr\data
	 */
	public static void initializeSolr() {
		
		/*
		 * Loads the solr in  in workspace\MetadataServicesToolkit\MST-instances\MetadataServicesToolkit\solr.
		 * Data(index) will be created under workspace\MetadataServicesToolkit\MST-instances\MetadataServicesToolkit\solr\data
		 */
		String solrHome = MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "solr";

		try
		{
			CoreContainer container = new CoreContainer();
			CoreDescriptor descriptor = new CoreDescriptor(container, "core1", solrHome);
			SolrCore core = container.create(descriptor);
			container.register("core1", core, false);

			((MSTSolrServer)MSTConfiguration.getBean("MSTSolrServer")).setServer(new EmbeddedSolrServer(container, "core1"));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch (SAXException se)
		{
			se.printStackTrace();
		}
		catch (ParserConfigurationException pe)
		{
			pe.printStackTrace();

		}
	}

}
