package xc.mst.action.log;

import com.opensymphony.xwork2.ActionSupport;
import java.io.PrintWriter;
import java.util.*;
import xc.mst.bo.log.Log;
import xc.mst.manager.logs.DefaultLogService;
import xc.mst.manager.logs.LogService;

/**
 * Resets the details of the General log files.
 *
 * @author Tejaswi Haramurali
 */
public class GeneralLogReset extends ActionSupport
{
    /**The ID of the general Log file */
    private String logId;

    /**Service object created for interaction with general log files */
    private LogService logService = new DefaultLogService();

    /**
     * sets the log ID
     * @param logId log ID
     */
    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    /**
     * returns the log ID
     * @return log ID
     */
    public String getLogId()
    {
        return this.logId;
    }

    /**
     * resets the general log file corresponding to the given log ID
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            int id = Integer.parseInt(logId);
            Log log = logService.getById(id);
            log.setErrors(0);
            log.setLastLogReset(new Date());
            log.setWarnings(0);
            logService.update(log);
            String filename = log.getLogFileLocation();
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("generalLogReset", "There was an error reseting the details of the log");
            return SUCCESS;
        }
    }

    /**
     * resets all the general log files
     * @return {@link #SUCCESS}
     */
    public String resetAll()
    {
        try
        {
            List logList = logService.getAll();
            Iterator logIter = logList.iterator();
            while(logIter.hasNext())
            {
                Log log = (Log)logIter.next();
                log.setErrors(0);
                log.setLastLogReset(new Date());
                log.setWarnings(0);
                logService.update(log);
                String filename = log.getLogFileLocation();
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("generalLogResetAll", "ERROR : There was a problem resetting all the general log files");
            return SUCCESS;
        }
    }
}
