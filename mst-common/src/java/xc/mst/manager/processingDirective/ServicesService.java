/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.processingDirective;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import xc.mst.bo.service.Service;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;

/**
 * Service class for interacting with the services in the MST
 *
 * @author Tejaswi Haramurali
 */
public interface ServicesService
{
    /**
     * Returns a list of all the services
     *
     * @return returns the list containing all the services
     * @throws DatabaseConfigException 
     */
    public List<Service> getAllServices() throws DatabaseConfigException;

    /**
     * Parses a configuration file with information on the service to add
     * and adds the new service, loading any new .jar files if necessary.
     * 
     * @param name The name of the service
     * @throws DataException If an error occurred while interacting with the database
     * @throws IOException If an error occurred while reading the configuration file
     * @throws ConfigFileException If the configuration file was invalid
     */
    public void addNewService(String name) throws DataException, IOException, ConfigFileException;
    
    /**
	 * Parses a configuration file with information on the service to update
     * and updates the service, loading any new .jar files if necessary.
	 * 
	 * @param name Service folder name
	 * @param service The service to update
	 * @throws DataException If an error occurred while interacting with the database
     * @throws IOException If an error occurred while reading the configuration file
     * @throws ConfigFileException If the configuration file was invalid
	 */
	void updateService(String name, Service service, boolean reprocessingRequired) throws DataException, IndexException, IOException, ConfigFileException;

	/**
     * Adds a new Service
     *
     * @param service The service to be added
     */
    public void insertService(Service service) throws DataException;
    
    /**
     * Deletes a service from the MST. This method has to be used when the service has no processed records.
     *
     * @param service service to be deleted
     * @throws xc.mst.dao.DataException
     */
    public void deleteService(Service service) throws DataException;
    
    /**
     * Deletes a service and its records by scheduling a job
     *
     * @param service service to be deleted
     */
    public void deleteServiceAndRecordsByJob(Service service) throws DataException;
    
    /**
     * Updates the details of a Service
     *
     * @param service The service to be updated.
     */
    public void updateService(Service service) throws DataException;

    /**
     * Retrieves a service object based on the ID passed to it
     *
     * @param serviceId The Id based on which a service object is retrieved.
     * @return
     * @throws DatabaseConfigException 
     */
    public Service getServiceById(int serviceId) throws DatabaseConfigException;

    /**
     * Retrieves a service object based on the name of the service
     *
     * @param serviceName The name of the service
     * @return
     * @throws DatabaseConfigException 
     */
    public Service getServiceByName(String serviceName) throws DatabaseConfigException;
    
    public Object getBean(String serviceName, String beanName);

    /**
     * returns a list of soervices sorted by name
     * 
     * @param sort boolean parameter that determines of the services are to be sorted in ascending/descending order
     * @return list of services
     * @throws DatabaseConfigException 
     */
    public List<Service> getAllServicesSorted(boolean sort,String columnSorted) throws DatabaseConfigException;
    
    public Collection<String> getServicesAvailableForInstall();
    
    /**
     * If file time needs update, it follows that the service must reprocess.
     */
    public boolean doesServiceFileTimeNeedUpdate(Service service);

    public void reprocessService(Service service);

}
