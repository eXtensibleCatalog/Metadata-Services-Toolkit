
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
     * returns a list of soervices sorted by name
     * @param sort boolean parameter that determines of the services are to be sorted in ascending/descending order
     * @return list of services
     */
    public List<Service> getAllServicesSortedByName(boolean sort)
    {
        return servicesDao.getSortedByName(sort);
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
