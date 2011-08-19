/**
 * Copyright (c) 2009 eXtensible Catalog Organization
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import xc.mst.utils.AutoServiceReprocessingFileFilter;
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
        LOG.info("ac-event: " + event);
        /*
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
        */
    }

    public ServiceEntry getServiceEntry(String name) {
        synchronized (this) {
            LOG.debug("entering getMetadataService " + name + " " + System.currentTimeMillis() + " " + this);
            if (!serviceEntries.containsKey(name)) {
                new ServiceEntry(name).start();
            }
            ServiceEntry se = serviceEntries.get(name);
            LOG.debug("exiting  getMetadataService " + name + " " + System.currentTimeMillis() + " " + this);
            return se;
        }
    }

    protected MetadataService getMetadataService(Service s) {
        MetadataService ms = (MetadataService) getServiceEntry(s.getName()).ac.getBean("Service");
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
                            String serviceFolder = MSTConfiguration.getUrlPath() + "/services/" + id;
                            List<URL> urls = new ArrayList<URL>();
                            String metaInfFolderStr = serviceFolder + "/META-INF";
                            File libFolder = new File(metaInfFolderStr + "/lib");
                            String classesFolderStr = metaInfFolderStr + "/classes/";
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
                                url = new URL(url.toString() + "/");
                                urls.add(url);
                                URL[] urlsArr = urls.toArray(new URL[] {});
                                URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
                                MSTConfiguration.serviceBeingLoaded = id;
                                ac = new ClassPathXmlApplicationContext();
                                ac.setClassLoader(loader);
                                ac.setConfigLocation("xc/mst/services/spring-service.xml");
                                ac.setParent(applicationContext);
                                BufferedReader br = null;
                                try {
                                    br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(
                                            "xc/mst/services/spring-service.xml")));
                                } catch (Throwable t) {
                                    LOG.debug("", t);
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
                                Util util = (Util) config.getBean("Util");
                                util.setClassLoader(loader);
                                ac.refresh();
                                util.setClassLoader(null);
                            }
                        } catch (Throwable t) {
                            LOG.debug("", t);
                        } finally {
                            semaphore.release();
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
    public List<Service> getAllServices() throws DatabaseConfigException {
        List<Service> services = getServiceDAO().getAll();
        injectMetadataServices(services);
        return services;
    }

    /**
     * Returns a sorted list of services
     * 
     * @param sort
     *            determines whether the list of services is sorted in ascending or descending order
     * @param columnSorted
     *            column on which the rows are sorted
     * @return list of services
     * @throws DatabaseConfigException
     */
    public List<Service> getAllServicesSorted(boolean sort, String columnSorted) throws DatabaseConfigException {
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

        try {
            props = new PropertiesConfiguration(MSTConfiguration.getUrlPath() + "/services/" + getUtil().normalizeName(name) +
                    "/META-INF/classes/xc/mst/services/custom.properties");

            String logFileName = getLogDAO().getById(Constants.LOG_ID_SERVICE_MANAGEMENT).getLogFileLocation();

            // The version of the service, which must appear in the second line of the configuration file
            String version = props.getString("service.version");
            /*
             * BDA 2010-12-06: This check adds a level of difficult and doesn't provide any rewards?
            if (!name.equals(getUtil().normalizeName(props.getString("service.name")))) {
                LOG.error("name: "+name);
                LOG.error("props.getString(service.name): "+getUtil().normalizeName(props.getString("service.name")));
                throw new RuntimeException("service folder name must match the name in the properties file.");
            }
            */

            if (version == null || version.length() == 0) {
                LogWriter.addError(logFileName, "Error adding a new service: The second line of the service configuration file must be the service's version.");
                throw new ConfigFileException("The second line of the service configuration file must be the service's version.");
            }

            // The name of the service's class, which must appear in the fourth line of the configuration file
            String className = props.getString("service.classname");
            if (className == null || className.length() == 0) {
                LogWriter.addError(logFileName, "Error adding a new service: The fourth line of the service configuration file must be the service's class name.");
                throw new ConfigFileException("The fourth line of the service configuration file must be the service's class name.");
            }

            // Populate the service BO
            Service service = new Service();
            service.setName(name);
            service.setVersion(version);
            service.setClassName(className);
            long latest = getLatestServiceFileTime(name);
            service.setServicesServiceLastModified(new Timestamp(latest));
            service.setHarvestOutLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "harvestOut" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
            service.setServicesLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "service" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
            service.setStatus(Status.NOT_RUNNING);

            String[] temp = new String[] { "input", "output" };
            for (int j = 0; j < temp.length; j++) {

                String[] formatNames = props.getStringArray(temp[j] + ".format.name");
                String[] formatSchemas = props.getStringArray(temp[j] + ".format.schemaLocation");
                String[] formatNamespaces = props.getStringArray(temp[j] + ".format.namespace");

                if (getUtil().arraysEqualInLength(formatNames, formatSchemas, formatNames)) {
                    for (int i = 0; i < formatNames.length; i++) {
                        Format format = getFormatDAO().getByName(formatNames[i]);

                        // If the format was not in the database, get it from the configuration file
                        if (format == null) {
                            format = new Format();
                            format.setName(formatNames[i]);
                            format.setSchemaLocation(formatSchemas[i]);
                            format.setNamespace(formatNamespaces[i]);
                            getFormatDAO().insert(format);
                        }
                        // Otherwise check whether or not the configuration file provided the same schema location and namespace for the format.
                        // Log a warning if not
                        else {
                            if (!format.getSchemaLocation().equals(formatSchemas[i]))
                                LogWriter.addWarning(logFileName, "The configuration file specified a schema location for the " +
                                        formatNames[i] + " format that differed from the one in the database. " +
                                        "The current schema location of " + format.getSchemaLocation() + " will be used " +
                                        "and the schema location " + formatSchemas[i] + " from the configuration file will be ignored.");

                            if (!format.getNamespace().equals(formatNamespaces[i]))
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
                    throw new ConfigFileException(temp[j] + ".formats don't match in length");
                }
            }

            // Insert the service
            getServiceDAO().insert(service);

            // TODO : Error need not be stored in db. This code can be removed. It is never read from db.
            String[] errorCodes = props.getStringArray("error.code");
            String[] errorDescriptionFiles = props.getStringArray("error.descriptionFile");

            if (getUtil().arraysEqualInLength(errorCodes, errorDescriptionFiles)) {
                for (int i = 0; i < errorCodes.length; i++) {
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
            ms.setService(service);
            ms.install();
        } catch (DataException e) {
            LOG.error("DataException while adding a service: ", e);
            throw e;
        } catch (ConfigFileException e) {
            LOG.error("ConfigFileException while adding a service: ", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Exception while adding a service: ", e);
        }
    }

    // called when GUI update made to service
    public void updateService(String name, Service service, boolean reprocessingRequired) throws DataException, IndexException, IOException, ConfigFileException {
        // Reload the service and confirm that it's not currently running.
        // Throw an error if it is
        service = getServiceDAO().getById(service.getId());
        if (service.getStatus().equals(Status.RUNNING) || service.getStatus().equals(Status.PAUSED)) {
            throw new DataException("Cannot update a service while it is running.");
            // TODO propagate to UI
        }

        String perviousVersion = service.getVersion();

        PropertiesConfiguration props = null;

        try {

            props = new PropertiesConfiguration(MSTConfiguration.getUrlPath() + "/services/" + name +
                    "/META-INF/classes/xc/mst/services/custom.properties");

            String logFileName = getLogDAO().getById(Constants.LOG_ID_SERVICE_MANAGEMENT).getLogFileLocation();

            // The name of the service, which must appear in the first line of the configuration file
            String serviceName = props.getString("service.name");
            if (serviceName == null || serviceName.length() == 0) {
                LogWriter.addError(logFileName, "Error adding a new service: The first line of the service configuration file must be the service's name.");
                throw new ConfigFileException("The first line of the service configuration file must be the service's name.");
            }

            // The version of the service, which must appear in the second line of the configuration file
            String version = props.getString("service.version");
            if (version == null || version.length() == 0) {
                LogWriter.addError(logFileName, "Error adding a new service: The second line of the service configuration file must be the service's version.");
                throw new ConfigFileException("The second line of the service configuration file must be the service's version.");
            }

            // The name of the service's class, which must appear in the fourth line of the configuration file
            String className = props.getString("service.classname");
            if (className == null || className.length() == 0) {
                LogWriter.addError(logFileName, "Error adding a new service: The fourth line of the service configuration file must be the service's class name.");
                throw new ConfigFileException("The fourth line of the service configuration file must be the service's class name.");
            }

            // Populate the service BO
            service.setName(name);
            service.setVersion(version);
            service.setClassName(className);
            LOG.debug("**** DefaultServicesService.updateService, about to see if need to setServicesServiceLastModified!");
            if (doesServiceFileTimeNeedUpdate(service)) {
                updateServiceLastModifiedTime(name, service);
                // TODO SHOULD we force reprocessing?
            }
            service.setHarvestOutLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "harvestOut" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
            service.setServicesLogFileName("logs" + MSTConfiguration.FILE_SEPARATOR + "service" + MSTConfiguration.FILE_SEPARATOR + name + ".txt");
            service.getInputFormats().clear();
            service.getOutputFormats().clear();

            String[] temp = new String[] { "input", "output" };
            for (int j = 0; j < temp.length; j++) {

                String[] formatNames = props.getStringArray(temp[j] + ".format.name");
                String[] formatSchemas = props.getStringArray(temp[j] + ".format.schemaLocation");
                String[] formatNamespaces = props.getStringArray(temp[j] + ".format.namespace");

                if (getUtil().arraysEqualInLength(formatNames, formatSchemas, formatNames)) {
                    for (int i = 0; i < formatNames.length; i++) {
                        Format format = getFormatDAO().getByName(formatNames[i]);

                        // If the format was not in the database, get it from the configuration file
                        if (format == null) {
                            format = new Format();
                            format.setName(formatNames[i]);
                            format.setSchemaLocation(formatSchemas[i]);
                            format.setNamespace(formatNamespaces[i]);
                            getFormatDAO().insert(format);
                        }
                        // Otherwise check whether or not the configuration file provided the same schema location and namespace for the format.
                        // Log a warning if not
                        else {
                            if (!format.getSchemaLocation().equals(formatSchemas[i]))
                                LogWriter.addWarning(logFileName, "The configuration file specified a schema location for the " +
                                        formatNames[i] + " format that differed from the one in the database. " +
                                        "The current schema location of " + format.getSchemaLocation() + " will be used " +
                                        "and the schema location " + formatSchemas[i] + " from the configuration file will be ignored.");

                            if (!format.getNamespace().equals(formatNamespaces[i]))
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
                    throw new ConfigFileException(temp[j] + ".formats don't match in length");
                }
            }

            // Insert the service
            getServiceDAO().update(service);

            String[] errorCodes = props.getStringArray("error.code");
            String[] errorDescriptionFiles = props.getStringArray("error.descriptionFile");

            if (getUtil().arraysEqualInLength(errorCodes, errorDescriptionFiles)) {
                for (int i = 0; i < errorCodes.length; i++) {

                    ErrorCode errorCode = getErrorCodeDAO().getByErrorCodeAndService(errorCodes[i], service);
                    if (errorCode == null) {
                        errorCode = new ErrorCode();
                        errorCode.setErrorCode(errorCodes[i]);
                        errorCode.setErrorDescriptionFile(errorDescriptionFiles[i]);
                        errorCode.setService(service);
                        getErrorCodeDAO().insert(errorCode);
                    } else {
                        errorCode.setErrorDescriptionFile(errorDescriptionFiles[i]);
                        getErrorCodeDAO().update(errorCode);
                    }

                }
            } else {
                throw new ConfigFileException("error codes don't match in length");
            }

            // getRepositoryDAO().createRepository(service);
            MetadataService ms = getMetadataService(service);
            ms.getRepository().installOrUpdateIfNecessary(perviousVersion, version);
            ms.install();

            // TODO what does below line do? Is it necessary? Should it be here or moved to Service Reprocess thread?
            ServiceUtil.getInstance().checkService(service.getId(), Status.NOT_RUNNING, true);

            // Schedule a job to reprocess records through new service
            if (reprocessingRequired)
                reprocessService(service);
        } catch (ConfigurationException e) {
            LOG.error("ConfigFileException while adding a service: ", e);
            throw new ConfigFileException(e.getMessage());
        }
    }

    /**
     * Inserts a service into the MST
     * 
     * @param service
     *            service object
     * @throws xc.mst.dao.DataException
     */
    public void insertService(Service service) throws DataException {
        getServiceDAO().insert(service);
    }

    /**
     * Deletes a service from the MST. This method has to be used when the service has no processed records.
     * 
     * @param service
     *            service to be deleted
     * @throws xc.mst.dao.DataException
     */
    public void deleteService(Service service) throws DataException {
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
     * @param service
     *            service to be deleted
     */
    public void deleteServiceAndRecordsByJob(Service service) throws DataException {
        service.setDeleted(true);
        getServiceDAO().update(service);
        try {
            Job job = new Job(service, 0, Constants.THREAD_DELETE_SERVICE);
            JobService jobService = (JobService) config.getBean("JobService");
            job.setOrder(jobService.getMaxOrder() + 1);
            jobService.insertJob(job);
        } catch (DatabaseConfigException dce) {
            LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
        }

    }

    /**
     * Updates the details related to a service
     * 
     * @param service
     *            service object
     * @throws xc.mst.dao.DataException
     */
    public void updateService(Service service) throws DataException {
        getServiceDAO().update(service);
    }

    /**
     * Returns a service by ID
     * 
     * @param serviceId
     *            service ID
     * @return Service object
     * @throws DatabaseConfigException
     */
    public Service getServiceById(int serviceId) throws DatabaseConfigException {
        Service s = getServiceDAO().getById(serviceId);
        if (s != null) {
            injectMetadataService(s);
            List<Integer> setIds = getRepositoryDAO().getSetIds(s.getName());
            if (setIds != null) {
                for (Integer setId : setIds) {
                    xc.mst.bo.provider.Set set = getSetService().getSetById(setId);
                    if (!s.getOutputSets().contains(set)) {
                        s.getOutputSets().add(set);
                    }
                }
            }
        }
        return s;
    }

    /**
     * Returns a service by name
     * 
     * @param serviceName
     *            name of the service
     * @return service object
     * @throws DatabaseConfigException
     */
    public Service getServiceByName(String serviceName) throws DatabaseConfigException {
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
     * @param in
     *            The BufferedReader for the file
     * @return The first non-comment non-whitespace line in the file, or null if we reached the end of the file
     * @throws IOException
     *             If an error occurred while reading the file
     */
    private String consumeCommentsAndWhitespace(BufferedReader in) throws IOException {
        while (in.ready()) {
            String line = in.readLine(); // A line from the configuration file

            // If the line is a valid line, return it
            if (!line.startsWith("#") && line.trim().length() > 0)
                return line.trim();
        }

        // If we got here we reached the end of the file, so return null
        return null;
    }

    public Collection<String> getServicesAvailableForInstall() {
        List<String> availableServices = new ArrayList<String>();
        File dir = getServiceDir();
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

        for (File file : fileList) {
            if (file.isDirectory() && !allServices.contains(file.getName())) {
                boolean nameIsAcceptable = true;
                for (byte b : file.getName().getBytes()) {
                    if (!(Character.isDigit((char) b) || Character.isLetter((char) b))) {
                        nameIsAcceptable = false;
                    }
                }
                if (nameIsAcceptable) {
                    String serviceName = file.getName();
                    availableServices.add(serviceName);
                }
            }
        }

        return availableServices;
    }

    private static File getServiceDir() {
        // this had a hard-coded '/' file separator in code before so leave it in for now.
        File dir = new File(MSTConfiguration.getUrlPath() + "/services");
        return dir;
    }

    private static Collection<File> getAllServiceFiles(File file) {
        Collection<File> all = new ArrayList<File>();
        all = addFilesToScanRecursively(file, all);
        return all;
    }

    private static Collection<File> addFilesToScanRecursively(File file, Collection<File> all) {
        // AutoServiceReprocessingFileFilter
        final File[] children = file.listFiles(new AutoServiceReprocessingFileFilter());
        if (children != null) {
            for (File child : children) {
                all.add(child);
                addFilesToScanRecursively(child, all);
            }
        }
        return all;
    }

    // want latest date of an actual file within service, disqualify directory timestamps as not being relevant.
    // Also filter based on file types acceptable found in MST config property:
    // (regexpOfFilesToScanForAutoServiceReprocessing)
    //
    private long getLatestServiceFileTime(String name) {
        File dir = new File(getServiceDir(), name);
        long latest = 0l;
        final Collection<File> serviceFiles = getAllServiceFiles(dir);
        for (File f : serviceFiles) {
            if (!f.isDirectory() && f.lastModified() > latest) {
                latest = f.lastModified();
                LOG.debug("*** Latest filesystem service file found! Name=" + f.getName() + " Date=" + new Date(latest));
            }
        }
        return latest;
    }

    // When you grab the time out of the database, it was stored as a Timestamp, and the db driver seems to clip (0) the
    // milliseconds at this point in time, so we need to be careful we compare apples to apples, so zero out the milliseconds
    // from time we receive from file system scan.
    // (Normalize the times by dropping milliseconds.)
    //
    public boolean doesServiceFileTimeNeedUpdate(Service service) {
        Calendar calCurrentLatest = Calendar.getInstance();
        Calendar calNewLatest = Calendar.getInstance();

        final String name = service.getName();
        final long currentLatest = service.getServicesServiceLastModified().getTime();
        calCurrentLatest.setTimeInMillis(currentLatest);
        calCurrentLatest.set(Calendar.MILLISECOND, 0);

        long newLatest = getLatestServiceFileTime(name);
        calNewLatest.setTimeInMillis(newLatest);
        calNewLatest.set(Calendar.MILLISECOND, 0);

        // show latest date we found
        LOG.debug("***** doesServiceFileTimeNeedUpdate? current latest time=" + new Date(currentLatest));

        // show comparison of new and old long date values found, with milliseconds zeroed out.
        LOG.debug("***** USE CAL: doesServiceFileTimeNeedUpdate? current latest=" +
                calCurrentLatest.getTimeInMillis() + " new latest=" + calNewLatest.getTimeInMillis());

        return calNewLatest.getTimeInMillis() > calCurrentLatest.getTimeInMillis();
    }

    public void updateServiceLastModifiedTime(String name, Service service) {
        long latest = getLatestServiceFileTime(name);
        service.setServicesServiceLastModified(new Timestamp(latest));
        LOG.debug("***** latest time returned on a file =" + latest + " date=" + new Date(latest));
        LOG.debug("***** DefaultServicesService.updateServiceLastModifiedTime, just setServicesServiceLastModified!");
    }

    public void reprocessService(Service service) {
        // TODO
        // THREAD_SERVICE_REPROCESS was empty (no assoc. code run in Scheduler)
        // Do you REALLY want to reprocess, if so, how? THREAD_SERVICE ? Set a PD...(see scheduler code that does this)
        try {
            Job job = new Job(service, 0, Constants.THREAD_SERVICE_REPROCESS);
            JobService jobService = (JobService) config.getBean("JobService");
            job.setOrder(jobService.getMaxOrder() + 1);
            jobService.insertJob(job);
        } catch (DatabaseConfigException dce) {
            LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
        }
    }
}
