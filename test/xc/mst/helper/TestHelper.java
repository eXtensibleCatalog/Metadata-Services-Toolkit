package xc.mst.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;
import org.jconfig.handler.XMLFileHandler;
import org.xml.sax.SAXException;

import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
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
	
	/** Constructor */
	private TestHelper() {}

	
	public static TestHelper getInstance() {
		System.out.println("TestHelper getInstance()::" + instance);
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
	    // Initialize MST configuration.
	    MSTConfiguration.getInstance("MetadataServicesToolkit");
	    
		MSTConfiguration.setUrlPath("MetadataServicesToolkit");
		String externalConfigurationFilePath = System.getProperty("user.dir") + MSTConfiguration.FILE_SEPARATOR+ "MetadataServicesToolkit_config.xml";

		File file = new File(externalConfigurationFilePath);
	    
	    XMLFileHandler handler = new XMLFileHandler();
        handler.setFile(file);
        
        ConfigurationManager configurationManager =
            ConfigurationManager.getInstance();
        try {
	        configurationManager.load(handler, "myConfig");
	        MSTConfiguration.setConfiguration(ConfigurationManager.getConfiguration("myConfig"));
        } catch (ConfigurationManagerException cme) {
        	System.out.println("Exception occured while loading the Configuration");
        }
		
	}
	
	/**
	 * Initialize log
	 * 
	 * Loads the log4j.config.txt in workspace\MetadataServicesToolkit\src\java\log4j.config.txt
	 */
	public  static void initializeLog() {

		PropertyConfigurator.configure(System.getProperty("user.dir") + MSTConfiguration.FILE_SEPARATOR + "src" + MSTConfiguration.FILE_SEPARATOR + "java" + MSTConfiguration.FILE_SEPARATOR + "log4j.config.txt");
		
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
				if (log.getLogFileLocation().indexOf(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR) == -1) {
					log.setLogFileLocation(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + log.getLogFileLocation());
			    	logDao.update(log);
				}
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
		
		MSTSolrServer.getInstance();
		
		/*
		 * Loads the solr in  in workspace\MetadataServicesToolkit\MST-instances\MetadataServicesToolkit\solr.
		 * Data(index) will be created under workspace\MetadataServicesToolkit\MST-instances\MetadataServicesToolkit\solr\data
		 */
		String solrHome = System.getProperty("user.dir") + MSTConfiguration.FILE_SEPARATOR + "MST-instances" +  MSTConfiguration.FILE_SEPARATOR + MSTConfiguration.getUrlPath();
		solrHome = solrHome + MSTConfiguration.FILE_SEPARATOR + "solr";

		try
		{
			CoreContainer container = new CoreContainer();
			CoreDescriptor descriptor = new CoreDescriptor(container, "core1", solrHome);
			SolrCore core = container.create(descriptor);
			container.register("core1", core, false);

			MSTSolrServer.setServer(new EmbeddedSolrServer(container, "core1"));
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
