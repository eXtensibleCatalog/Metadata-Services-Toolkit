
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
import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.log.LogDAO;


/**
 *
 * This is the action method which is used to display the General Logs page.
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class GeneralLog extends BaseActionSupport
{
    /** The column on which the rows are to be sorted */
    private String columnSorted = "LogFileName";

    /** Determines whether the rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder = true;

    /**A List of Log Files **/
    private List<Log> logList;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	

    /**
     * Overrides default implementation to view the General Logs Page.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            List<Log> fullList = new ArrayList<Log>();

            if(columnSorted.equalsIgnoreCase("LogFileName")||(columnSorted.equalsIgnoreCase("Warnings"))||(columnSorted.equalsIgnoreCase("Errors"))||(columnSorted.equalsIgnoreCase("LastLogReset")))
            {
                if(columnSorted.equalsIgnoreCase("LogFileName"))
                {
                    fullList  = getLogService().getSorted(isAscendingOrder, LogDAO.COL_LOG_FILE_NAME);
                }
                else if(columnSorted.equalsIgnoreCase("Warnings"))
                {
                    fullList = getLogService().getSorted(isAscendingOrder, LogDAO.COL_WARNINGS);
                }
                else if(columnSorted.equalsIgnoreCase("Errors"))
                {
                    fullList = getLogService().getSorted(isAscendingOrder, LogDAO.COL_ERRORS);
                }
                else
                {
                    fullList = getLogService().getSorted(isAscendingOrder, LogDAO.COL_LAST_LOG_RESET);
                }
               
            }
            else
            {
               fullList  = getLogService().getSorted(isAscendingOrder, LogDAO.COL_LOG_FILE_NAME);
            }
            setLogList(fullList);
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
     * Sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        this.isAscendingOrder = isAscendingOrder;
    }

    /**
     * Returns the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @return
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

     /**
     * Sets the name of the column on which the sorting should be performed
      *
     * @param columnSorted 
     */
    public void setColumnSorted(String columnSorted)
    {
        this.columnSorted = columnSorted;
    }

    /**
     * Returns the name of the column on which sorting should be performed
     * 
     * @return 
     */
    public String getColumnSorted()
    {
        return this.columnSorted;
    }
    
    /**
     * Sets the list of log files
     *
     * @param logList list of log files
     */
    public void setLogList(List<Log> logList)
    {
        this.logList = logList;
    }

    /**
     * Returns the list of logs files
     *
     * @return list of log files
     */
    public List<Log> getLogList()
    {
        return this.logList;
    }
}
