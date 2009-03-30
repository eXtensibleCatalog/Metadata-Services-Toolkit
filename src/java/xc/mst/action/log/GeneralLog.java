
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
import java.util.*;
import org.apache.log4j.Logger;
import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.manager.logs.DefaultLogService;
import xc.mst.manager.logs.LogService;


/**
 *
 * This is the action method which is used to display the General Logs page.
 *
 * @author Tejaswi Haramurali
 */
public class GeneralLog extends ActionSupport
{
    /** The column on which the rows are to be sorted */
    private String columnSorted = "log_file_name";

    /** Determines whether the rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder = true;

    /**A List of Log Files **/
    private List<Log> logList;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	
    /**
     * sets the list of log files
     * @param logList list of log files
     */
    public void setLogList(List<Log> logList)
    {
        this.logList = logList;
    }

    /**
     * returns the list of logs files
     * @return list of log files
     */
    public List<Log> getLogList()
    {
        return this.logList;
    }

    /**
     * Overrides default implementation to view the General Logs Page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

            LogService logService = new DefaultLogService();

            List<Log> fullList = logService.getSorted(isAscendingOrder, columnSorted);

            setLogList(fullList);

            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("generalLogError", "ERROR : There was a problem loading ");
            errorType = "error";
            return SUCCESS;
        }
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
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
}
