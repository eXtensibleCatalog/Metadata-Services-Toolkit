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
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * This action method is used to display Logs associated with providers from which the MST harvests data.
 * In short, these are the logs that are produced by external data sources from which data is pulled in.
 *
 * @author Tejaswi Haramurali
 */
public class HarvestInLog extends ActionSupport
{
    /** determines the column name on which sorting should be performed */
    private String columnSorted;

    /** determines if the rows are to be ordered in ascending or descending order */
    private boolean isAscendingOrder;

    /**Creates a service object for providers */
    private ProviderService providerService = new DefaultProviderService();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** List of all providers **/
    private List<Provider> providerList;

    /**
     * sets the list of Providers
     * @param providerList list of providers
     */
    public void setProviderList(List<Provider> providerList)
    {
        this.providerList = providerList;
    }

    /**
     * returns the list of providers
     * @return list of providers
     */
    public List<Provider> getProviderList()
    {
        return this.providerList;
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
     * Overrides default implementation to view the 'Harvest-In Logs' Page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            setProviderList(providerService.getAllProvidersSorted(isAscendingOrder,columnSorted));
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("harvestInLogError", "Error : There was a problem loading the page");
            return SUCCESS;
        }
    }
}
