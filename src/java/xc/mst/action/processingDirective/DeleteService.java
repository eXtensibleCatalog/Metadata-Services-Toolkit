/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * Deletes a service from the MST
 *
 * @author Tejaswi Haramurali
 */
public class DeleteService extends ActionSupport
{
    /** The ID of the service to be deleted */
    String serviceId;

    /** The service object for services */
    private ServicesService servicesService = new DefaultServicesService();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * sets the ID of the service to be deleted
     *
     * @param serviceId service ID
     */
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }
    
    /**
     * returns the ID of the service to be deleted
     *
     * @return service ID
     */
    public String getServiceId()
    {
        return this.serviceId;
    }

    /**
     * Overrides default implementation to delete a service.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Service tempService = servicesService.getServiceById(Integer.parseInt(serviceId));
            servicesService.deleteService(tempService);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("deleteServiceError", "ERROR : There was an error deleting the service");
            return SUCCESS;
        }
    }
}
