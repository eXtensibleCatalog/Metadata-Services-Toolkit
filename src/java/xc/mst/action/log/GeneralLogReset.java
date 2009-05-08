package xc.mst.action.log;

import com.opensymphony.xwork2.ActionSupport;
import java.io.PrintWriter;
import java.util.*;
import org.apache.log4j.Logger;
import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.manager.logs.DefaultLogService;
import xc.mst.manager.logs.LogService;

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
            logs.setErrors(0);
            logs.setLastLogReset(new Date());
            logs.setWarnings(0);
            logService.update(logs);
            String filename = logs.getLogFileLocation();
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("There was an error reseting the details of the log",e);
            this.addFieldError("generalLogReset", "There was an error reseting the details of the log");
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
        catch(Exception e)
        {
            log.error("There was a problem resetting all the general log files",e);
            this.addFieldError("generalLogResetAll", "There was a problem resetting all the general log files");
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
}
