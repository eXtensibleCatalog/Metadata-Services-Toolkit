
/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.service.ServiceDAO;

/**
 * This action method is used to display the Service logs
 *
 * @author Tejaswi Haramurali
 */
public class HarvestOutLog extends BaseActionSupport
{
    /** Serial id */
	private static final long serialVersionUID = -3510624101788171450L;

	/**The column on which the rows are to be sorted */
    private String columnSorted="ServiceName";

    /** boolean parameter determines if the rows are to sorted in ascending or descending order */
    private boolean isAscendingOrder=true;

    /** Sets the list of all services */
    private List<Service> services = new ArrayList<Service>();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Error type */
	private String errorType; 

     /**
     * Overrides default implementation to view the Service Logs Page.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            if(columnSorted.equalsIgnoreCase("ServiceName")||(columnSorted.equalsIgnoreCase("RecordsAvailable"))||(columnSorted.equalsIgnoreCase("RecordsHarvested"))||(columnSorted.equalsIgnoreCase("Warnings"))||(columnSorted.equalsIgnoreCase("Errors"))||(columnSorted.equalsIgnoreCase("LastLogReset")))
            {
                if(columnSorted.equalsIgnoreCase("ServiceName"))
                {
                	services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_SERVICE_NAME);
                }
                else if(columnSorted.equalsIgnoreCase("Warnings"))
                {
                	services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_HARVEST_OUT_WARNINGS);
                }
                else if(columnSorted.equalsIgnoreCase("Errors"))
                {
                	services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_HARVEST_OUT_ERRORS);
                }
                else if(columnSorted.equalsIgnoreCase("RecordsAvailable"))
                {
                	services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_HARVEST_OUT_RECORDS_AVAILABLE);
                }
                else if(columnSorted.equalsIgnoreCase("RecordsHarvested"))
                {
                	services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_HARVEST_OUT_RECORDS_HARVESTED);
                }
                else
                {
                	services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_HARVEST_OUT_LAST_LOG_RESET);
                }

            }
            else
            {
                services = getServicesService().getAllServicesSorted(isAscendingOrder,ServiceDAO.COL_SERVICE_NAME);
            }
             setIsAscendingOrder(isAscendingOrder);
             setColumnSorted(columnSorted);
             return SUCCESS;
            
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("HarvestOutLogError", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * Returns the error type
     *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets the error type
     *
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

     /**
     * Sets the list of all services
     *
     * @param serviceList list of all services
     */
    public void setServices(List<Service> services)
    {
        this.services = services;
    }

    /**
     * Returns the list of all services
     *
     * @return list of all services
     */
    public List<Service> getServices()
    {
        return services;
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
     * return
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

     /**
     * Sets the name of the column on which the sorting should be performed
      *
     * @param columnSorted column name
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
}
