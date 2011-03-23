/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
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
public class MSTConfiguration extends PropertyPlaceholderConfigurer implements ApplicationContextAware {
	
	private static final Logger LOG = Logger.getLogger(MSTConfiguration.class);
	
	protected Properties properties = new Properties();

	protected ApplicationContext applicationContext = null;

	/*  The instance of the MST configuration	 */
	private static MSTConfiguration instance = null;
	
	/** Name of category */
	private static String urlPath;
	
	public static String rootDir;
	
	public static String serviceBeingLoaded = null;
	
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
		if (MSTConfiguration.instance == null) {
			MSTConfiguration.instance = this;
		}
	}
	
	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public static MSTConfiguration getInstance()
	{
		return instance;
	}

	protected boolean resolvePlaceholderVisited = false;
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		if ("normalized.service.name".equals(placeholder)) {
			return getUtil().normalizeName(serviceBeingLoaded);
		}
		String val = super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
		if ("service.name".equals(placeholder)) {
			val = getUtil().normalizeName(val);
		}
		if (!resolvePlaceholderVisited) {
			resolvePlaceholderVisited = true;
			for (Object key : props.keySet()) {
				String keyStr = key.toString();
				String pval = props.getProperty(keyStr);
				if ("service.name".equals(keyStr)) {
					properties.put("normalized.service.name", getUtil().normalizeName(pval));
					properties.put(keyStr, getUtil().normalizeName(pval));
					log.info("key: (normalized.)service.name val: "+getUtil().normalizeName(pval));	
				} else {
					properties.put(keyStr, pval);
					if (!key.toString().contains("Password")) {
						log.info("key: "+key+" val: "+pval);
					}
				}
			}
			init2();
		}
		return val;
	}
	
	/*
	 * Creates and initializes configuration for MST
	 */
	public void init2() {
		if (applicationContext instanceof WebApplicationContext) {
			String urlPath = ((WebApplicationContext)applicationContext).getServletContext().getContextPath();
			// Remove the / in '/MetadataServicesToolkit'
			urlPath = urlPath.substring(1, urlPath.length());
			instanceName = urlPath;
		} else {
			instanceName = "MetadataServicesToolkit";
		}
		
		File mstInstances = new File(rootDir+"/"+getProperty(Constants.INSTANCES_FOLDER_NAME));
		if (mstInstances.exists()) {
			mstInstanceFolderExist = true;
		}
		
		File currentInstance = new File(rootDir+"/"+getProperty(Constants.INSTANCES_FOLDER_NAME) +  FILE_SEPARATOR + instanceName);
		if (currentInstance.exists()) {
			currentInstanceFolderExist = true;
		}
		urlPath = rootDir + getProperty(Constants.INSTANCES_FOLDER_NAME) +  FILE_SEPARATOR + instanceName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Object getBean(String name) {
		try {
			return applicationContext.getBean(name);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	/**
	 * Get value of given property
	 *  
	 * @param name name of property
	 * @return value of property
	 */
	public String getProperty(String name) {
		return getProperty(name, null);
	}
	
	public List<String> getPropertyAsList(String name) {
		List<String> props = new ArrayList<String>();
		String propertiesStr = getProperty(name, null);
		if (properties != null) {
			for (String token : propertiesStr.split(",")) {
				props.add(token);
			}
		}
		return props;
	}
	
	public boolean getPropertyAsBoolean(String name, boolean def) {
		String value = getProperty(name);
		LOG.debug("value: "+value);
		if (value == null) {
			return def;	
		} else {
			value = value.toUpperCase();
			if ("TRUE".equals(value) || "ON".equals(value) || "YES".equals(value)) {
				return true;
			} else if ("FALSE".equals(value) || "OFF".equals(value) || "OFF".equals(value)) {
				return false;
			} else {
				log.error("property '"+name+"' value not understood: "+value);
				return def;
			}
		}
	}
	
	public String getProperty(String name, String def) {
		String value = properties.getProperty(name);
		if (value == null) {
			value = def;
		}
		return value;
	}

	/**
	 * Get property value as int
	 * 
	 * @param name name of property to retrieve
	 * @param def default value
	 * @return Value defined in configuration file
	 */
	public int getPropertyAsInt(String name, int def) {
		String strValue = properties.getProperty(name);
		int value = 0;
		if (strValue != null) {
			value = Integer.parseInt(strValue);
		} else {
			value = def;
		}
		return value;
	}
	
	/**
	 * Get relative path from tomcat working directory to MST configuration folder 
	 * 
	 * @return path to MST configuration folder
	 */
	public static String getUrlPath() {
		return urlPath;
	}

	public static String getInstanceName() {
		return instanceName;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	protected Util getUtil() {
		return (Util)instance.getBean("Util");
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public boolean isPerformanceTestingMode() {
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
	
	public String getServicePath() {
		return getUrlPath()+"/services/";
	}

}
