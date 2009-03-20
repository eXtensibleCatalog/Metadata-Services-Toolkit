

package xc.mst.manager.processingDirective;

import java.util.List;
import xc.mst.bo.service.*;
import xc.mst.dao.DataException;

/**
 * Service class for interacting with the services in the MST
 *
 * @author Tejaswi Haramurali
 */
public interface ServicesService
{
    /**
     * returns a list of all the services
     * @return returns the list containing all the services
     */
    public List<Service> getAllServices();

    /**
     * Adds a new Service
     * @param service The service to be added
     */
    public void insertService(Service service) throws DataException;
    
    /**
     * deletes a service
     * @param service The service to be deleted
     */
    public void deleteService(Service service) throws DataException;

    /**
     * Updates the details of a Service
     * @param service The service to be updated.
     */
    public void updateService(Service service) throws DataException;

    /**
     * Retrieves a service object based on the ID passed to it
     * @param serviceId The Id based on which a service object is retrieved.
     * @return
     */
    public Service getServiceById(int serviceId);

    /**
     * Retrieves a service object based on the name of the service
     * @param serviceName The name of the service
     * @return
     */
    public Service getServiceByName(String serviceName);

    /**
     * Retrieves a service object based on the port number
     * @param port The port number of the service
     * @return
     */
    public Service getServiceByPort(int port);

    /**
     * returns a list of soervices sorted by name
     * @param sort boolean parameter that determines of the services are to be sorted in ascending/descending order
     * @return list of services
     */
    public List<Service> getAllServicesSorted(boolean sort,String columnSorted);
}
