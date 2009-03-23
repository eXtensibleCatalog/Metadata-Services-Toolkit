
package xc.mst.manager.processingDirective;

import java.util.List;
import xc.mst.bo.service.*;
import xc.mst.dao.DataException;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;

/**
 * Provides implementation for service methods to interact with the services in the MST
 *
 * @author Tejaswi Haramurali
 */
public class DefaultServicesService implements ServicesService
{
    /** DAO object for services in the MST */
    private ServiceDAO servicesDao;
    
    public DefaultServicesService()
    {
        servicesDao = new DefaultServiceDAO();
    }

    /**
     * returns a list of all services
     *
     * @return List of Services
     */
    public List<Service> getAllServices()
    {
        return servicesDao.getAll();
    }

    /**
     * returns a sorted list of services
     * @param sort determines whether the list of services is sorted in ascending or descending order
     * @param columnSorted column on which the rows are sorted
     * @return list of services
     */
    public List<Service> getAllServicesSorted(boolean sort,String columnSorted)
    {
        return servicesDao.getSorted(sort, columnSorted);
    }
    /**
     * inserts a service into the MST
     * @param service service object
     * @throws xc.mst.dao.DataException
     */
    public void insertService(Service service) throws DataException
    {
        servicesDao.insert(service);
    }

    /**
     * deletes a service from the MST
     * @param service service object
     * @throws xc.mst.dao.DataException
     */
    public void deleteService(Service service) throws DataException
    {
        servicesDao.delete(service);
    }

    /**
     * updates the details related to a service
     * @param service service object
     * @throws xc.mst.dao.DataException
     */
    public void updateService(Service service) throws DataException
    {
        servicesDao.update(service);
    }

    /**
     * returns a service by ID
     * @param serviceId service ID
     * @return Service object
     */
    public Service getServiceById(int serviceId)
    {
        return servicesDao.getById(serviceId);
    }

    /**
     * returns a service by name
     * @param serviceName name of the service
     * @return service object
     */
    public Service getServiceByName(String serviceName)
    {
        return servicesDao.getByServiceName(serviceName);
    }

    /**
     * returns service by port number
     * @param servicePort service port
     * @return service object
     */
    public Service getServiceByPort(int servicePort)
    {
        return servicesDao.getByPort(servicePort);
    }
}
