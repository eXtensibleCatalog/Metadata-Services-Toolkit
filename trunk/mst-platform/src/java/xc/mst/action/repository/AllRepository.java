
/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.ProviderDAO;

/**
 * This Action Class is used for displaying all repositories
 *
 * @author Tejaswi Haramurali
 */
public class AllRepository extends BaseActionSupport
{
   
   /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
    /**
	 * Eclipse generated id
	 */
	private static final long serialVersionUID = 6270020684379187321L;
    
    /** boolean value which determines if the rows are to be sorted in ascending order*/
    private boolean isAscendingOrder = true;

    /** determines the name of the column on which the sorting is to be done */
    private String columnSorted="RepositoryName";

    /**
     * The list of Repositories that is returned
     */
	private List<Provider> repositories;

     /** Error type */
      private String errorType;

    /**
     * Overrides default implementation to view all the repositories.
     * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
       try
       {

            if(columnSorted.equalsIgnoreCase("RepositoryName")||(columnSorted.equalsIgnoreCase("RepositoryURL"))||(columnSorted.equalsIgnoreCase("LastHarvestEndTime")))
            {
                if(columnSorted.equalsIgnoreCase("RepositoryName"))
                {
                    repositories = getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_NAME);
                }
                else if(columnSorted.equalsIgnoreCase("RepositoryURL"))
                {
                    repositories = getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_OAI_PROVIDER_URL);
                }
                else
                {
                    repositories = getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_LAST_HARVEST_END_TIME);
                }

               
            }
            else //remove this and do by repo name
            {
                repositories = getProviderService().getAllProvidersSorted(isAscendingOrder,ProviderDAO.COL_NAME);
            }
            setIsAscendingOrder(isAscendingOrder);
            setColumnSorted(columnSorted);
            return SUCCESS;
           
       }
       catch(DatabaseConfigException dce)
       {
           log.error(dce.getMessage(),dce);
           errorType = "error";
           this.addFieldError("dbConfigError","Unable to access the database. There may be a problem with database configuration.");
           return INPUT;
       }
    }

    /**
     * Gets all the Repositories in a List
     *
     * @return List of Repositories
     */
    public List<Provider> getRepositories()
    {
        return repositories;
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

}
