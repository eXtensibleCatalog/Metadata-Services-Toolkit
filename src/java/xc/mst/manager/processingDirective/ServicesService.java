

package xc.mst.manager.processingDirective;

import java.io.File;
import java.io.IOException;
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
     * @param configFile The configuration file defining the service to be added
     * @throws DataException If an error occurred while interacting with the database
     * @throws IOException If an error occurred while reading the configuration file
     * @throws ConfigFileException If the configuration file was invalid
     */
    public void addNewService(File configFile) throws DataException, IOException, ConfigFileException;
    
    /**
	 * Parses a configuration file with information on the service to update
     * and updates the service, loading any new .jar files if necessary.
	 * 
	 * @param configFile The configuration file defining the service
	 * @param service The service to update
	 * @throws DataException If an error occurred while interacting with the database
     * @throws IOException If an error occurred while reading the configuration file
     * @throws ConfigFileException If the configuration file was invalid
	 */
	void updateService(File configFile, Service service) throws DataException, IndexException, IOException, ConfigFileException;

	/**
     * Adds a new Service
     *
     * @param service The service to be added
     */
    public void insertService(Service service) throws DataException;
    
    /**
     * deletes a service
     *
     * @param service The service to be deleted
     */
    public void deleteService(Service service) throws IndexException, DataException;

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

    /**
     * Retrieves a service object based on the port number
     *
     * @param port The port number of the service
     * @return
     * @throws DatabaseConfigException 
     */
    public Service getServiceByPort(int port) throws DatabaseConfigException;

    /**
     * returns a list of soervices sorted by name
     * 
     * @param sort boolean parameter that determines of the services are to be sorted in ascending/descending order
     * @return list of services
     * @throws DatabaseConfigException 
     */
    public List<Service> getAllServicesSorted(boolean sort,String columnSorted) throws DatabaseConfigException;
}
