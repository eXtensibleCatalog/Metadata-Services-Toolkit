
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import java.util.List;


import com.opensymphony.xwork2.ActionSupport;

import org.apache.log4j.Logger;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * This Action Class is used for displaying all repositories
 *
 * @author Tejaswi Haramurali
 */
public class AllRepository extends ActionSupport
{
   
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    /**
	 * Eclipse generated id
	 */
	private static final long serialVersionUID = 6270020684379187321L;
    
    /** boolean value which determines if the rows are to be sorted in ascending order*/
    private boolean isAscendingOrder = true;

    /** determines the name of the column on which the sorting is to be done */
    private String columnSorted="name";

    /**
     * The list of Repositories that is returned
     */
	private List<Provider> Repositories;

    /**
     * Overrides default implementation to view all the repositories.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
           ProviderService providerService = new DefaultProviderService();
           System.out.println("Inside execute, columnSorted is "+columnSorted);
           Repositories = providerService.getAllProvidersSorted(isAscendingOrder,columnSorted);
           return SUCCESS;
        }
        catch(Exception e)
        {
           log.debug(e);
           return INPUT;
        }
    }

    /**
     * Gets all the Repositories in a List
     * @return List of Repositories
     */
    public List<Provider> getRepositories()
    {
        return Repositories;
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

}
