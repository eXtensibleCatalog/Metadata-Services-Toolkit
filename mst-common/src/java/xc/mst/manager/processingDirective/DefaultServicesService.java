/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.processingDirective;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.service.ErrorCode;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;
import xc.mst.manager.IndexException;
import xc.mst.services.MetadataService;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.ServiceUtil;
import xc.mst.utils.Util;

/**
 * Provides implementation for service methods to interact with the services in the MST
 *
 * @author Tejaswi Haramurali
 */
public class DefaultServicesService extends BaseService 
	implements ServicesService, ApplicationListener<ApplicationEvent>, ApplicationContextAware {

	protected static Logger LOG = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Constant signifying the parser is parsing the service's input formats
     */
    private static final String FILE_OUTPUT_FORMATS = "OUTPUT FORMATS";

    /**
     * Constant signifying the parser is parsing the service's output formats
     */
    private static final String FILE_INPUT_FORMATS = "INPUT FORMATS";

    /**
     * Constant signifying the parser is parsing the service's error messages
     */
    private static final String FILE_ERROR_MESSAGES = "ERROR MESSAGES";

    /**
     * Constant signifying the parser is parsing the service specific configuration
     */
    private static final String FILE_SERVICE_SPECIFIC = "SERVICE CONFIG";
    
	protected Map<String, ServiceEntry> serviceEntries = new HashMap<String, ServiceEntry>();
	protected Semaphore semaphore = new Semaphore(1);
	protected ApplicationContext applicationContext = null;
	protected static int loopCount = 0;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public boolean isRootAC() {
		boolean acquired = this.semaphore.tryAcquire();
		if (acquired) {
			this.semaphore.release();
		}
		return acquired;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		try {
	        if (event instanceof ContextRefreshedEvent) {
	        	boolean b = isRootAC();
	        	if (b) {
	        		// do nothing
	        	} else {
	        		this.semaphore.release();
	        	}
	        }
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
    }
	
	public ServiceEntry getServiceEntry(String name) {
		synchronized (this) {
			LOG.debug("entering getMetadataService "+name+" "+System.currentTimeMillis());
			if (!serviceEntries.containsKey(name)) {
				new ServiceEntry(name).start();	
			}
			ServiceEntry se = serviceEntries.get(name);
			return se;	
		}
	}
	
	protected MetadataService getMetadataService(Service s) {
		MetadataService ms = (MetadataService)getServiceEntry(s.getName()).ac.getBean("Service");
		if (ms.getRepository().getName() == null) {
			ms.getRepository().setName(s.getName());
			ms.getRepository().setService(s);
		}
		return ms;
	}
	
	public Object getBean(String serviceName, String beanName) {
		return getServiceEntry(serviceName).ac.getBean(beanName);
	}
	
	class ServiceEntry {
		public Thread t = null;
		public ClassPathXmlApplicationContext ac = null;
		public String id = null;
		public ServiceEntry thisthis = null;
		
		public ServiceEntry(String id) {
			this.id = id;
		}
		
		public void start() {
			thisthis = this;
			try {
				this.t = new Thread() {
					public void run() {
						try {
			        		String serviceFolder = MSTConfiguration.getUrlPath()+"/services/"+id;
			        		List<URL> urls = new ArrayList<URL>();
			        		String metaInfFolderStr = serviceFolder+"/META-INF";
			        		File libFolder = new File(metaInfFolderStr+"/lib");
			        		String classesFolderStr = metaInfFolderStr+"/classes/";
			        		if (libFolder != null) {
			        			if (libFolder.listFiles() != null) {
					        		for (File f : libFolder.listFiles()) {
					        			if (f.getName().endsWith(".jar")) {
					        				urls.add(f.toURI().toURL());
					        			}
					        		}
			        			}
			        			File f = new File(classesFolderStr);
			        			URI uri = f.toURI();
			        			URL url = uri.toURL();
			        			url = new URL(url.toString()+"/");
				        		urls.add(url);
				        		URL[] urlsArr = urls.toArray(new URL[]{});
				        		URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
				        		ac = new ClassPathXmlApplicationContext();
				        		ac.setClassLoader(loader);
				        		ac.setConfigLocation("xc/mst/services/spring-service.xml");
				        		ac.setParent(applicationContext);
				        		BufferedReader br = null;
				        		try {
				        			br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(
				        					"xc/mst/services/spring-service.xml")));
				        		} catch (Throwable t) {
				        			semaphore.release();
				        			return;
				        		}
				        		StringBuilder sb = new StringBuilder();
				        		String line = null;
				        		while ((line = br.readLine()) != null) {
				        		sb.append(line + "\n");
				        		}
				        		br.close();
				        		serviceEntries.put(id, thisthis);
				        		Util util = (Util)config.getBean("Util");
				        		util.setClassLoader(loader);
				        		ac.refresh();
				        		util.setClassLoader(null);
			        		}
						} catch (Throwable t) {
							semaphore.release();
							t.printStackTrace(System.out);
						}
						LOG.debug("done initting ac");
					}
				};
				semaphore.acquire();
				t.start();
				semaphore.acquire();
				semaphore.release();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
	
	protected void injectMetadataServices(List<Service> services) {
    	for (Service service : services) {
    		injectMetadataService(service);
    	}
	}
	
	protected void injectMetadataService(Service service) {
		if (service.getMetadataService() == null) {
			service.setMetadataService(getMetadataService(service));
		}
	}

    /**
     * Returns a list of all services
     *
     * @return List of Services
     * @throws DatabaseConfigException
     */
    public List<Service> getAllServices() throws DatabaseConfigException
    {
    	List<Service> services = getServiceDAO().getAll();
    	injectMetadataServices(services);
        return services;
    }

    /**
     * Returns a sorted list of services
     *
     * @param sort determines whether the list of services is sorted in ascending or descending order
     * @param columnSorted column on which the rows are sorted
     * @return list of services
     * @throws DatabaseConfigException
     */
    public List<Service> getAllServicesSorted(boolean sort,String columnSorted) throws DatabaseConfigException
    {
        List<Service> services = getServiceDAO().getSorted(sort, columnSorted);
    	injectMetadataServices(services);
        return services;
    }

    /**
     * Adds a new Service whose configuration details are present in the file
     *
     * @param name
     * @throws xc.mst.dao.DataException
     * @throws java.io.IOException
     * @throws xc.mst.manager.processingDirective.ConfigFileException
     */
    public void addNewService(String name) throws DataException, IOException, ConfigFileException {
    	PropertiesConfiguration props = null; 
    	
    	try
    	{
    		props = new PropertiesConfiguration(MSTConfiguration.getUrlPath()+"/services/"+name+
    				"/META-INF/classes/xc/mst/services/custom.properties");

    		String logFileName = getLogDAO().getById(Constants.LOG_ID_SERVICE_MANAGEMENT).getLogFileLocation();

    		// The version of the service, which must appear in the second line of the configuration file
    		String version = props.getString("service.version");
    		if (!name.equals(props.getString("service.name"))) {
    			throw new RuntimeException("service folder name must match the name in the properties file.");
    		}
    		
    		if(version == null || version.length() == 0)
    		{
    			LogWriter.addError(logFileName, "Error adding a new service: The second line of the service configuration file must be the service's version.");
    			throw new ConfigFileException("The second line of the service configuration file must be the service's version.");
    		}

    		// The name of the service's class, which must appear in the fourth line of the configuration file
    		String className = props.getString("service.classname");
    		if(className == null || className.length() == 0)
    		{
    			LogWriter.addError(logFileName, "Error adding a new service: The fourth line of the service configuration file must be the service's class name.");
    			throw new ConfigFileException("The fourth line of the service configuration file must be the service's class name.");
    		}
    		
	    	// Populate the service BO
    		Service service = new Service();
    		service.setName(name);
    		service.setVersion(version);
    		service.setClassName(className);
    		service.setHarvestOutLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "harvestOut" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
    		service.setServicesLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "service" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
    		service.setStatus(Status.NOT_RUNNING);

    		String[] temp = new String[] {"input", "output"};
    		for (int j=0; j<temp.length; j++) {
        		
        		String[] formatNames = props.getStringArray(temp[j]+".format.name");
        		String[] formatSchemas = props.getStringArray(temp[j]+".format.schemaLocation");
        		String[] formatNamespaces = props.getStringArray(temp[j]+".format.namespace");
        		
        		if (getUtil().arraysEqualInLength(formatNames, formatSchemas, formatNames)) {
    				for (int i=0; i<formatNames.length; i++) {
        				Format format = getFormatDAO().getByName(formatNames[i]);

        				// If the format was not in the database, get it from the configuration file
        				if(format == null)
        				{
        					format = new Format();
        					format.setName(formatNames[i]);
        					format.setSchemaLocation(formatSchemas[i]);
        					format.setNamespace(formatNamespaces[i]);
        					getFormatDAO().insert(format);
        				}
        				// Otherwise check whether or not the configuration file provided the same schema location and namespace for the format.
        				// Log a warning if not
        				else
        				{
        					if(!format.getSchemaLocation().equals(formatSchemas[i]))
        						LogWriter.addWarning(logFileName, "The configuration file specified a schema location for the " +
        								formatNames[i] + " format that differed from the one in the database. " +
    									"The current schema location of " + format.getSchemaLocation() + " will be used " +
    									"and the schema location " + formatSchemas[i] + " from the configuration file will be ignored.");

        					if(!format.getNamespace().equals(formatNamespaces[i]))
        						LogWriter.addWarning(logFileName, "The configuration file specified a namespace for the " +
        								formatNames[i] + " format that differed from the one in the database. " +
        								"The current namespace of " + format.getNamespace() + " will be used " +
        								"and the namespace " + formatNamespaces[i] + " from the configuration file will be ignored.");
        				}

        				// Add the format we just parsed as an input format for the new service
        				if (temp[j].equals("input")) {
        					service.addInputFormat(format);	
        				} else if (temp[j].equals("output")) {
        					service.addOutputFormat(format);
        				}
    				}
        		} else {
        			throw new ConfigFileException(temp[j]+".formats don't match in length");
        		}   						
    		}

    		// Insert the service
    		getServiceDAO().insert(service);
    		
    		// TODO : Error need not be stored in db. This code can be removed. It is never read from db. 
    		String[] errorCodes = props.getStringArray("error.code");
    		String[] errorDescriptionFiles = props.getStringArray("error.descriptionFile");
    		
    		if (getUtil().arraysEqualInLength(errorCodes, errorDescriptionFiles)) {
    			for (int i=0; i<errorCodes.length; i++) {
    				ErrorCode errorCode = new ErrorCode();
    				errorCode.setErrorCode(errorCodes[i]);
    				errorCode.setErrorDescriptionFile(errorDescriptionFiles[i]);
    				errorCode.setService(service);

    				getErrorCodeDAO().insert(errorCode);
    			}
    		} else {
    			throw new ConfigFileException("error codes don't match in length");
    		}

    		getRepositoryDAO().createRepository(service);
    		MetadataService ms = getMetadataService(service);
    		ms.getRepository().installOrUpdateIfNecessary(null, version);
    		ms.install();
    	}
    	catch(DataException e)
    	{
    		LOG.error("DataException while adding a service: ", e);
    		throw e;
    	}
    	catch(ConfigFileException e)
    	{
    		LOG.error("ConfigFileException while adding a service: ", e);
    		throw e;
    	}
    	catch(Exception e)
    	{
    		LOG.error("Exception while adding a service: ", e);
    	}
    }


    public void updateService(String name, Service service, boolean reprocessingRequired) throws DataException, IndexException, IOException, ConfigFileException
    {
    	// Reload the service and confirm that it's not currently running.
    	// Throw an error if it is
    	service = getServiceDAO().getById(service.getId());
    	if(service.getStatus().equals(Status.RUNNING) || service.getStatus().equals(Status.PAUSED)) {
    		throw new DataException("Cannot update a service while it is running.");
    		// TODO propagate to UI
    	}
    	
    	String perviousVersion = service.getVersion();
    	
    	PropertiesConfiguration props = null;
    	
    	try
    	{
    		
        	props = new PropertiesConfiguration(MSTConfiguration.getUrlPath()+"/services/"+name+
    		"/META-INF/classes/xc/mst/services/custom.properties");

    		String logFileName = getLogDAO().getById(Constants.LOG_ID_SERVICE_MANAGEMENT).getLogFileLocation();

    		// The name of the service, which must appear in the first line of the configuration file
    		String serviceName = props.getString("service.name");
    		if(serviceName == null || serviceName.length() == 0)
    		{
    			LogWriter.addError(logFileName, "Error adding a new service: The first line of the service configuration file must be the service's name.");
    			throw new ConfigFileException("The first line of the service configuration file must be the service's name.");
    		}

    		// The version of the service, which must appear in the second line of the configuration file
    		String version = props.getString("service.version");
    		if(version == null || version.length() == 0)
    		{
    			LogWriter.addError(logFileName, "Error adding a new service: The second line of the service configuration file must be the service's version.");
    			throw new ConfigFileException("The second line of the service configuration file must be the service's version.");
    		}

    		// The name of the service's class, which must appear in the fourth line of the configuration file
    		String className = props.getString("service.classname");
    		if(className == null || className.length() == 0)
    		{
    			LogWriter.addError(logFileName, "Error adding a new service: The fourth line of the service configuration file must be the service's class name.");
    			throw new ConfigFileException("The fourth line of the service configuration file must be the service's class name.");
    		}

	    	// Populate the service BO
    		service.setName(name);
    		service.setVersion(version);
    		service.setClassName(className);
    		service.setHarvestOutLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "harvestOut" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
    		service.setServicesLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "service" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
    		service.getInputFormats().clear();
    		service.getOutputFormats().clear();

    		String[] temp = new String[] {"input", "output"};
    		for (int j=0; j<temp.length; j++) {
        		
        		String[] formatNames = props.getStringArray(temp[j]+".format.name");
        		String[] formatSchemas = props.getStringArray(temp[j]+".format.schemaLocation");
        		String[] formatNamespaces = props.getStringArray(temp[j]+".format.namespace");
        		
        		if (getUtil().arraysEqualInLength(formatNames, formatSchemas, formatNames)) {
    				for (int i=0; i<formatNames.length; i++) {
        				Format format = getFormatDAO().getByName(formatNames[i]);

        				// If the format was not in the database, get it from the configuration file
        				if(format == null)
        				{
        					format = new Format();
        					format.setName(formatNames[i]);
        					format.setSchemaLocation(formatSchemas[i]);
        					format.setNamespace(formatNamespaces[i]);
        					getFormatDAO().insert(format);
        				}
        				// Otherwise check whether or not the configuration file provided the same schema location and namespace for the format.
        				// Log a warning if not
        				else
        				{
        					if(!format.getSchemaLocation().equals(formatSchemas[i]))
        						LogWriter.addWarning(logFileName, "The configuration file specified a schema location for the " +
        								formatNames[i] + " format that differed from the one in the database. " +
    									"The current schema location of " + format.getSchemaLocation() + " will be used " +
    									"and the schema location " + formatSchemas[i] + " from the configuration file will be ignored.");

        					if(!format.getNamespace().equals(formatNamespaces[i]))
        						LogWriter.addWarning(logFileName, "The configuration file specified a namespace for the " +
        								formatNames[i] + " format that differed from the one in the database. " +
        								"The current namespace of " + format.getNamespace() + " will be used " +
        								"and the namespace " + formatNamespaces[i] + " from the configuration file will be ignored.");
        				}

        				// Add the format we just parsed as an input format for the new service
        				if (temp[j].equals("input")) {
        					service.addInputFormat(format);	
        				} else if (temp[j].equals("output")) {
        					service.addOutputFormat(format);
        				}
    				}
        		} else {
        			throw new ConfigFileException(temp[j]+".formats don't match in length");
        		}   						
    		}
    		
    		// Insert the service
    		getServiceDAO().update(service);
    		
    		String[] errorCodes = props.getStringArray("error.code");
    		String[] errorDescriptionFiles = props.getStringArray("error.descriptionFile");
    		
    		if (getUtil().arraysEqualInLength(errorCodes, errorDescriptionFiles)) {
    			for (int i=0; i<errorCodes.length; i++) {
    				
    				
    				ErrorCode errorCode = getErrorCodeDAO().getByErrorCodeAndService(errorCodes[i], service);
    				if(errorCode == null)
    				{
    					errorCode = new ErrorCode();
        				errorCode.setErrorCode(errorCodes[i]);
        				errorCode.setErrorDescriptionFile(errorDescriptionFiles[i]);
        				errorCode.setService(service);
    					getErrorCodeDAO().insert(errorCode);
    				}
    				else
    				{
    					errorCode.setErrorDescriptionFile(errorDescriptionFiles[i]);
    					getErrorCodeDAO().update(errorCode);
    				}
    				
    			}
    		} else {
    			throw new ConfigFileException("error codes don't match in length");
    		}
    		
//    		getRepositoryDAO().createRepository(service);
    		MetadataService ms = getMetadataService(service);
    		ms.getRepository().installOrUpdateIfNecessary(perviousVersion, version);
    		ms.install();
 
    		// TODO what does below line do? Is it necessary? Should it be here or moved to Service Reprocess thread?
    		ServiceUtil.getInstance().checkService(service.getId(), Status.NOT_RUNNING, true);

    		// Schedule a job to reprocess records through new service
    		if(reprocessingRequired)
    		try {
				Job job = new Job(service, 0, Constants.THREAD_SERVICE_REPROCESS);
				JobService jobService = (JobService)config.getBean("JobService");
				job.setOrder(jobService.getMaxOrder() + 1); 
				jobService.insertJob(job);
			} catch (DatabaseConfigException dce) {
				LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
			}
    	}
    	catch(ConfigurationException e)
    	{
    		LOG.error("ConfigFileException while adding a service: ", e);
    		throw new ConfigFileException(e.getMessage());
    	}
    }

    /**
     * Inserts a service into the MST
     *
     * @param service service object
     * @throws xc.mst.dao.DataException
     */
    public void insertService(Service service) throws DataException
    {
    	getServiceDAO().insert(service);
    }

    /**
     * Deletes a service from the MST. This method has to be used when the service has no processed records.
     *
     * @param service service to be deleted
     * @throws xc.mst.dao.DataException
     */
    public void deleteService(Service service) throws DataException
    {
    	// Get all Processing Directives that has this service as source service
    	List<ProcessingDirective> processingDirectives = getProcessingDirectiveService().getBySourceServiceId(service.getId());
    	
    	// Delete Processing Directive that has this service as source service
    	for (ProcessingDirective processingDirective : processingDirectives) {
    		getProcessingDirectiveService().deleteProcessingDirective(processingDirective);
    	}
    	
    	// Delete service
    	getServiceDAO().delete(service);
    	ServiceEntry se = serviceEntries.get(service.getName());
    	se.ac.destroy();
    	serviceEntries.remove(service.getName());
    	getRepositoryDAO().deleteSchema(service.getName());
    }
    
    /**
     * Deletes a service and its records by scheduling a job
     *
     * @param service service to be deleted
     */
    public void deleteServiceAndRecordsByJob(Service service) throws DataException
    {
    	service.setDeleted(true);
    	getServiceDAO().update(service);
    	try {
			Job job = new Job(service, 0, Constants.THREAD_DELETE_SERVICE);
			JobService jobService = (JobService)config.getBean("JobService");
			job.setOrder(jobService.getMaxOrder() + 1); 
			jobService.insertJob(job);
		} catch (DatabaseConfigException dce) {
			LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
		}
    	
    }

    /**
     * Updates the details related to a service
     *
     * @param service service object
     * @throws xc.mst.dao.DataException
     */
    public void updateService(Service service) throws DataException
    {
    	getServiceDAO().update(service);
    }

    /**
     * Returns a service by ID
     *
     * @param serviceId service ID
     * @return Service object
     * @throws DatabaseConfigException
     */
    public Service getServiceById(int serviceId) throws DatabaseConfigException
    {
        Service s = getServiceDAO().getById(serviceId);
        injectMetadataService(s);
        return s;
    }

    /**
     * Returns a service by name
     *
     * @param serviceName name of the service
     * @return service object
     * @throws DatabaseConfigException
     */
    public Service getServiceByName(String serviceName) throws DatabaseConfigException
    {
        Service s = getServiceDAO().getByServiceName(serviceName);
        if (s != null) {
        	s.setMetadataService(getMetadataService(s));
            s.getMetadataService().setService(s);	
        }
        return s;
    }

    /**
     * Given a BufferedReader for a File, skip to the next line in the file
     * which is neither a comment nor whitespace
     *
     * @param in The BufferedReader for the file
     * @return The first non-comment non-whitespace line in the file, or null if we reached the end of the file
     * @throws IOException If an error occurred while reading the file
     */
    private String consumeCommentsAndWhitespace(BufferedReader in) throws IOException
    {
    	while(in.ready())
		{
			String line = in.readLine(); // A line from the configuration file

			// If the line is a valid line, return it
			if(!line.startsWith("#") && line.trim().length() > 0)
				return line.trim();
		}

    	// If we got here we reached the end of the file, so return null
    	return null;
    }
    
    public Collection<String> getServicesAvailableForInstall() {
    	List<String> availableServices = new ArrayList<String>();
    	File dir = new File(MSTConfiguration.getUrlPath() + "/services");
    	File[] fileList = dir.listFiles();
    	
    	Set<String> allServices = new HashSet<String>();
    	try {
    		List<Service> services = getAllServices();
    		for (Service s : services) {
    			allServices.add(s.getName());
    		}
    	} catch (Throwable t) {
    		LOG.error("", t);
    	}

    	for(File file : fileList) {
    		if (file.isDirectory() && !allServices.contains(file.getName())) {
 	    		String serviceName = file.getName();
	    		availableServices.add(serviceName);
    		}
    	}
    	
    	return availableServices;
    }

	/**
	 * Get error text for given error code
	 * 
	 * @param errorCode Error code
	 * @return Corresponding error text
	 */
	public String getError(int serviceId, String errorCode) {
		 
    	String errorMessage = null;

    	try {
    		Service service = getServiceById(serviceId);
    		PropertiesConfiguration props = new PropertiesConfiguration(MSTConfiguration.getUrlPath()+"/services/"+service.getName()+
				"/META-INF/classes/xc/mst/services/custom.properties");
			String[] errorCodes = props.getStringArray("error.code");
			String[] errorText = props.getStringArray("error.text");

			for (int i=0; i<errorCodes.length; i++) {
				if (errorCodes[i].equalsIgnoreCase(errorCode)) {
					errorMessage =  errorText[i];
					break;
				}
			}
    	} catch (ConfigurationException ce) {
    		LOG.error("Exception occured when reading properties file");
    	} catch (DatabaseConfigException dce) {
    		LOG.error("Exception occured when accessing database with provided parameters");
    	}
		
		return errorMessage;
		
	}
}
