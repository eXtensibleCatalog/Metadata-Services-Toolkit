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
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

import xc.mst.constants.Constants;

/**
 * Configuration for Metadata services toolkit 
 * 
 * @author Sharmila Ranganathan
 *
 */
public class MSTConfiguration implements ApplicationContextAware {
	
	protected Properties properties = new Properties();

	protected ApplicationContext applicationContext = null;

	/*  The instance of the MST configuration	 */
	private static MSTConfiguration instance = null;
	
	/** Name of category */
	private String urlPath;
	
	/** File separator according to OS. \ for windows  / for unix. */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	/** The logger object */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**  Indicates whether MST instance folder exist */
	public static boolean mstInstanceFolderExist = false;

	/**  Name of MST instance */
	public static String instanceName;
	
	/**  Indicates whether instance folder for this instance exist */
	public static  boolean currentInstanceFolderExist = false;

	/** Default constructor */
	public MSTConfiguration() {
		MSTConfiguration.instance = this;
	}
	
	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public static MSTConfiguration getInstance()
	{
		return instance;
	}
	
	/*
	 * Creates and initializes configuration for MST
	 */
	public void init() {
		
		System.out.println("MSTCONfig.init()");
		
		if (applicationContext instanceof WebApplicationContext) {
			String urlPath = ((WebApplicationContext)applicationContext).getServletContext().getContextPath();
			// Remove the / in '/MetadataServicesToolkit'
			urlPath = urlPath.substring(1, urlPath.length());
		
			instanceName = urlPath;
		}
		
		System.out.println("instanceName: "+instanceName);
		
		File mstInstances = new File(getProperty(Constants.INSTANCES_DIR) + FILE_SEPARATOR + getProperty(Constants.INSTANCES_FOLDER_NAME));
		if (mstInstances.exists()) {
			mstInstanceFolderExist = true;
		}
		
		File currentInstance = new File(getProperty(Constants.INSTANCES_DIR) + FILE_SEPARATOR + getProperty(Constants.INSTANCES_FOLDER_NAME) +  FILE_SEPARATOR + urlPath);
		if (currentInstance.exists()) {
			currentInstanceFolderExist = true;
		}
		
		this.urlPath = getProperty(Constants.INSTANCES_FOLDER_NAME) +  FILE_SEPARATOR + instanceName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public static Object getBean(String name) {
		try {
			return MSTConfiguration.instance.applicationContext.getBean(name);
		} catch (Throwable t) {
			log.error("", t);
			throw new RuntimeException(t);
		}
	}
	
	/**
	 * Get value of given property
	 *  
	 * @param name name of property
	 * @return value of property
	 */
	public static String getProperty(String name) {
		return instance.properties.getProperty(name);
	}

	/**
	 * Get relative path from tomcat working directory to MST configuration folder 
	 * 
	 * @return path to MST configuration folder
	 */
	public static String getUrlPath() {
		return instance.urlPath;
	}

	public static String getInstanceName() {
		return instanceName;
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public static boolean isPerformanceTestingMode() {
		try {
			String ptMode = getProperty("PerformanceTestingMode");
			if (ptMode != null && "true".equals(ptMode)) {
				return true;
			}
		} catch (Throwable t) {
			//do nothing
		}
		return false;
	}

}
