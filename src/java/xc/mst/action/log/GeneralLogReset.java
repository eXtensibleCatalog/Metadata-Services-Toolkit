package xc.mst.action.log;

import com.opensymphony.xwork2.ActionSupport;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import org.apache.log4j.Logger;
import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.logs.DefaultLogService;
import xc.mst.manager.logs.LogService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.UserService;

/**
 * Resets the details of the General log files.
 *
 * @author Tejaswi Haramurali
 */
public class GeneralLogReset extends ActionSupport
{
    /** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**The ID of the general Log file */
    private int logId;

    /**Service object created for interaction with general log files */
    private LogService logService = new DefaultLogService();

    /** Error Type */
    private String errorType;

    /**User service object */
    private UserService userService = new DefaultUserService();

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
            Log logs = logService.getById(logId);
            if(logs==null)
            {
                this.addFieldError("generalLogReset", "Error Occurred while resetting general log. An email has been sent to the administrator");
                userService.sendEmailErrorReport(userService.MESSAGE,"logs/MST_General_log");
                errorType = "error";
                return SUCCESS;
            }
            logs.setErrors(0);
            logs.setLastLogReset(new Date());
            logs.setWarnings(0);
            logService.update(logs);
            String filename = logs.getLogFileLocation();
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
            userService.sendEmailErrorReport(userService.MESSAGE,"logs/MST_General_log");
            errorType = "error";
            return SUCCESS;
        }
        catch(FileNotFoundException fe)
        {
            log.error(fe.getMessage(),fe);
            this.addFieldError("generalLogReset", "Error Occurred while resetting general log. An email has been sent to the administrator.");
            userService.sendEmailErrorReport(userService.MESSAGE,"logs/MST_General_log");
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
            List<Log> logList = logService.getAll();
            Iterator<Log> logIter = logList.iterator();
            while(logIter.hasNext())
            {
                Log logs = (Log)logIter.next();
                logs.setErrors(0);
                logs.setLastLogReset(new Date());
                logs.setWarnings(0);
                logService.update(logs);
                String filename = logs.getLogFileLocation();
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
            userService.sendEmailErrorReport(userService.MESSAGE,"logs/MST_General_log");
            errorType = "error";
            return SUCCESS;
        }
        catch(FileNotFoundException fe)
        {
            log.error(fe.getMessage(),fe);
            this.addFieldError("generalLogReset", "Error Occurred while resetting all general logs. An email has been sent to the administrator.");
            userService.sendEmailErrorReport(userService.MESSAGE,"logs/MST_General_log");
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
