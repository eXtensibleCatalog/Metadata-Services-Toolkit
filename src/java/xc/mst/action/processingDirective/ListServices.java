
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

import java.util.List;
import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * This action is used to display all the services that are part of the MST
 *
 * @author Tejaswi Haramurali
 */

public class ListServices extends ActionSupport
{
    /** The list of services that are part of the MST */
    private List<Service> ServicesList;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Overrides default implementation to list all services
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

           ServicesService servService = new DefaultServicesService();
           ServicesList = servService.getAllServices();
           setServices(ServicesList);

           return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            return INPUT;
        }
    }

    /**
     * sets the List of services that are part of the MST
     * @param ServicesList list of services
     */
    public void setServices(List<Service> ServicesList)
    {
        this.ServicesList = ServicesList;
    }

    /**
     * returns the list of services that are part of the MST
     * @return list of services
     */
    public List<Service> getServices()
    {
        return ServicesList;
    }

    /**
     * Retrieves the base URL for a particular service
     * @return retruns the URL value
     */
    public String getBaseURL()
    {
        Configuration mstConfiguration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");
        String baseURL = mstConfiguration.getProperty("OaiRepoBaseUrl");
        return baseURL;
    }
}
