/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This action is used to display all the services that are part of the MST
 *
 * @author Tejaswi Haramurali
 */

public class ListServices extends ActionSupport implements ServletRequestAware
{
       
    /** Serial id */
	private static final long serialVersionUID = 5867719363631588555L;

	/** Determines whether the rows are to be sorted in ascending or descending order*/
    private boolean isAscendingOrder = true;

    /** The coumn on which the rows are sorted*/
    private String columnSorted = "ServiceName";
    
    /** The list of services that are part of the MST */
    private List<Service> ServicesList;

    /** error type */
    private String errorType;

    /** Base URL of the system **/
    private String baseURL;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
    /** Http servlet request */
    private HttpServletRequest servletRequest;

    /**
     * Overrides default implementation to list all services
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
           baseURL = "http://" + servletRequest.getServerName() + ":" + "SERVICE_PORT" + servletRequest.getContextPath() + "/oaiRepositoryServlet";
           ServicesService servService = new DefaultServicesService();
           if(columnSorted.equalsIgnoreCase("ServiceName")||(columnSorted.equalsIgnoreCase("Port")))
            {
                if(columnSorted.equalsIgnoreCase("ServiceName"))
                {
                    ServicesList = servService.getAllServicesSorted(isAscendingOrder, ServiceDAO.COL_SERVICE_NAME);
                }
                else
                {
                    ServicesList = servService.getAllServicesSorted(isAscendingOrder, ServiceDAO.COL_PORT);
                }
               
            }
            else
            {
               ServicesList = servService.getAllServicesSorted(isAscendingOrder, ServiceDAO.COL_SERVICE_NAME);
            }
            setServices(ServicesList);
            setIsAscendingOrder(isAscendingOrder);
            setColumnSorted(columnSorted);
            return SUCCESS;
                      
        }
        catch(DatabaseConfigException dce)
        {
            errorType = "error";
            log.error(dce.getMessage(),dce);
            this.addFieldError("listServicesError", "Unable to connect to the database. Database configuration may be incorrect");
            return INPUT;
        }
        
    }

    /**
     * Sets the List of services that are part of the MST
     *
     * @param ServicesList list of services
     */
    public void setServices(List<Service> ServicesList)
    {
        this.ServicesList = ServicesList;
    }

    /**
     * Returns the list of services that are part of the MST
     *
     * @return list of services
     */
    public List<Service> getServices()
    {
        return ServicesList;
    }

     /**
     * Sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        this.isAscendingOrder = isAscendingOrder;
    }

    /**
     * Gets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

    /**
     * Sets the name of the column on which the sorting should be performed
     *
     * @param columnSorted name of the column
     */
    public void setColumnSorted(String columnSorted)
    {
        this.columnSorted = columnSorted;
    }

    /**
     * Returns the name of the column on which sorting should be performed
     * 
     * @return column name
     */
    public String getColumnSorted()
    {
        return this.columnSorted;
    }

    /**
     * Sets the base URL
     *
     * @param Base URL
     */
    public void setBaseURL(String baseURL)
    {
        this.baseURL = baseURL;
    }

    /**
     * Returns base URL
     *
     * @return base URL
     */
    public String getBaseURL()
    {
        return this.baseURL;
    }

    /**
     * Returns error type
      *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets error type
     *
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}
    
}
