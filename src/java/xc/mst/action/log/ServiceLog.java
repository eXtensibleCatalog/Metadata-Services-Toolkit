
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.log;

import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;

import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * This action method is used to display the Service logs
 *
 * @author Tejaswi Haramurali
 */
public class ServiceLog extends ActionSupport
{
    /**The coulmn on which the rows are to be sorted */
    private String columnSorted;
    
    /**Boolena parameter determines if the rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder=true;

    /** Creates a service object for Services */
    private ServicesService servicesService = new DefaultServicesService();

    /**Sets the list of all services */
    private List<Service> serviceList;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    public ServiceLog()
    {
        serviceList = new ArrayList();
    }

    /**
     * sets the list of all services
     * @param serviceList list of services
     */
    public void setServiceList(List<Service> serviceList)
    {
        this.serviceList = serviceList;
    }

    /**
     * returns the list of all services
     * @return list of services
     */
    public List<Service> getServiceList()
    {
        return serviceList;
    }

     /**
     * sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        this.isAscendingOrder = isAscendingOrder;
    }

    /**
     * sgets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

     /**
     * sets the name of the column on which the sorting should be performed
     * @param columnSorted name of the column
     */
    public void setColumnSorted(String columnSorted)
    {
        System.out.println("Setting column sorted as "+columnSorted);
        this.columnSorted = columnSorted;
    }

    /**
     * returns the name of the column on which sorting should be performed
     * @return column name
     */
    public String getColumnSorted()
    {
        return this.columnSorted;
    }

     /**
     * Overrides default implementation to view the Service Logs Page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {            
            List<Service> servicesList = servicesService.getAllServicesSorted(isAscendingOrder,columnSorted);
            setServiceList(servicesList);
            return SUCCESS;

        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("serviceLogError", "Error : There was a problem in loading the Page");
            return SUCCESS;
        }
    }
}
