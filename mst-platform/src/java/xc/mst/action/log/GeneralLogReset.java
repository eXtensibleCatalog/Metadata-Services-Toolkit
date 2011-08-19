/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Resets the details of the General log files.
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class GeneralLogReset extends BaseActionSupport
{
    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**The ID of the general Log file */
    private int logId;

    /** Error Type */
    private String errorType;

    /**
     * Resets the general log file corresponding to the given log ID
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Log logs = getLogService().getById(logId);
            if(logs==null)
            {
                this.addFieldError("generalLogReset", "Error Occurred while resetting general log. An email has been sent to the administrator");
                getUserService().sendEmailErrorReport();
                errorType = "error";
                return SUCCESS;
            }
            logs.setErrors(0);
            logs.setLastLogReset(new Date());
            logs.setWarnings(0);
            getLogService().update(logs);
            String filename = logs.getLogFileLocation(true);
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("generalLogReset", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return SUCCESS;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("generalLogReset", "Error Occurred while resetting general log. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
        catch(FileNotFoundException fe)
        {
            log.error(fe.getMessage(),fe);
            this.addFieldError("generalLogReset", "Error Occurred while resetting general log. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * Resets all the general log files
     *
     * @return {@link #SUCCESS}
     */
    public String resetAll()
    {
        try
        {
            List<Log> logList = getLogService().getAll();
            Iterator<Log> logIter = logList.iterator();
            while(logIter.hasNext())
            {
                Log logs = (Log)logIter.next();
                logs.setErrors(0);
                logs.setLastLogReset(new Date());
                logs.setWarnings(0);
                getLogService().update(logs);
                String filename = logs.getLogFileLocation(true);
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("generalLogReset", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return SUCCESS;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("generalLogReset", "Error Occurred while resetting all general logs. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
        catch(FileNotFoundException fe)
        {
            log.error(fe.getMessage(),fe);
            this.addFieldError("generalLogReset", "Error Occurred while resetting all general logs. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
    }

     /**
     * Sets the log ID
     *
     * @param logId ID of the log
     */
    public void setLogId(int logId)
    {
        this.logId = logId;
    }

    /**
     * Returns the log ID
     *
     * @return log ID
     */
    public int getLogId()
    {
        return this.logId;
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
