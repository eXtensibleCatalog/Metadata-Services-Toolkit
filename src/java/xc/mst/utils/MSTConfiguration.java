/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.utils;

import java.io.File;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;
import org.jconfig.handler.XMLFileHandler;

import xc.mst.constants.Constants;

/**
 * Configuration for Metadata services toolkit 
 * 
 * @author Sharmila Ranganathan
 *
 */
public class MSTConfiguration {


	/*  The instance of the MST configuration	 */
	private static MSTConfiguration instance = null;
	
	/* The instance of the configuration */
	private static Configuration configuration = null;
	
	/** Name of category */
	private static String urlPath;
	
	/** File separator according to OS. \ for windows  / for unix. */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	/** The logger object */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**  Object used to read properties from the default configuration file */
	protected static final Configuration defaultConfiguration = ConfigurationManager.getConfiguration();
	
	/**  Indicates whether MST instance folder exist */
	public static boolean mstInstanceFolderExist = false;

	/**  Name of MST instance */
	public static String instanceName;
	
	/**  Indicates whether instance folder for this instance exist */
	public static boolean currentInstanceFolderExist = false;

	/** Default constructor */
	private MSTConfiguration() {}
	
	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public static MSTConfiguration getInstance(String urlPath)
	{
		if(instance != null) {
			return instance;
		}

		createConfiguration(urlPath);
		instance = new MSTConfiguration();
		return instance;
	}
	
	/*
	 * Creates and initializes configuration for MST
	 */
	private static void createConfiguration(String urlPath) {
		
		instanceName = urlPath;
		
		File mstInstances = new File(System.getProperty("user.dir") + FILE_SEPARATOR + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME));
		if (mstInstances.exists()) {
			mstInstanceFolderExist = true;
		}
		
		File currentInstance = new File(System.getProperty("user.dir") + FILE_SEPARATOR + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) +  FILE_SEPARATOR + urlPath);
		if (currentInstance.exists()) {
			currentInstanceFolderExist = true;
		}
		
		MSTConfiguration.urlPath = defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) +  FILE_SEPARATOR + urlPath;
		String externalConfigurationFilePath = System.getProperty("user.dir") + FILE_SEPARATOR + MSTConfiguration.urlPath + FILE_SEPARATOR+ "MetadataServicesToolkit_config.xml";
		
		if (log.isDebugEnabled()) {
			log.debug("External MST configuration file path : " + externalConfigurationFilePath);
		}
		File file = new File(externalConfigurationFilePath);
	    
	    XMLFileHandler handler = new XMLFileHandler();
        handler.setFile(file);
        
        ConfigurationManager configurationManager =
            ConfigurationManager.getInstance();
        try {
	        configurationManager.load(handler, "myConfig");
	        configuration =  ConfigurationManager.getConfiguration("myConfig");
        } catch (ConfigurationManagerException cme) {
        	log.error("Exception occured while loading the Configuration", cme);
        }
	}
	
	/**
	 * Get value of given property
	 *  
	 * @param name name of property
	 * @return value of property
	 */
	public static String getProperty(String name) {
		return configuration.getProperty(name);
	}

	/**
	 * Get relative path from tomcat working directory to MST configuration folder 
	 * 
	 * @return path to MST configuration folder
	 */
	public  static String getUrlPath() {
		return urlPath;
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(Configuration configuration) {
		MSTConfiguration.configuration = configuration;
	}

	public static void setUrlPath(String urlPath) {
		MSTConfiguration.urlPath = urlPath;
	}

	public static String getMSTInstancesFolderPath(){
	
		return System.getProperty("user.dir") + FILE_SEPARATOR + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME);
	}

	public static String getInstanceName() {
		return instanceName;
	}

	public static void setInstanceName(String instanceName) {
		MSTConfiguration.instanceName = instanceName;
	}


}
