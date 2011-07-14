/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.log;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.ProviderDAO;

/**
 * This action method is used to display Logs associated with providers from which the MST harvests data.
 * In short, these are the logs that are produced by external data sources from which data is pulled in.
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class HarvestInLog extends BaseActionSupport
{
    /** determines the column name on which sorting should be performed */
    private String columnSorted="RepositoryName";

    /** determines if the rows are to be ordered in ascending or descending order */
    private boolean isAscendingOrder=true;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** List of all providers **/
    private List<Provider> providerList;
    
	/** Error type */
	private String errorType; 

    /**
     * Overrides default implementation to view the 'Harvest-In Logs' Page.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            if(columnSorted.equalsIgnoreCase("RepositoryName")||(columnSorted.equalsIgnoreCase("LastHarvestEndTime"))||(columnSorted.equalsIgnoreCase("RecordsAdded"))||(columnSorted.equalsIgnoreCase("RecordsReplaced"))||(columnSorted.equalsIgnoreCase("LastLogReset")))
            {
                if(columnSorted.equalsIgnoreCase("RepositoryName"))
                {
                    setProviderList(getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_NAME));
                }
                else if(columnSorted.equalsIgnoreCase("LastHarvestEndTime"))
                {
                    setProviderList(getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_LAST_HARVEST_END_TIME));
                }
                else if(columnSorted.equalsIgnoreCase("RecordsAdded"))
                {
                    setProviderList(getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_RECORDS_ADDED));
                }
                else if(columnSorted.equalsIgnoreCase("RecordsReplaced"))
                {
                    setProviderList(getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_RECORDS_REPLACED));
                }
                else
                {
                    setProviderList(getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_LAST_LOG_RESET));
                }
               
            }
            else
            {
                setProviderList(getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_NAME));
            }

            setIsAscendingOrder(isAscendingOrder);
            setColumnSorted(columnSorted);
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("generalLogError", "Unable to connect to the database. Database Configuration may be incorrect");
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
     * Sets the list of Providers
     *
     * @param providerList list of providers
     */
    public void setProviderList(List<Provider> providerList)
    {
        this.providerList = providerList;
    }

    /**
     * Returns the list of providers
     *
     * @return list of providers
     */
    public List<Provider> getProviderList()
    {
        return this.providerList;
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
