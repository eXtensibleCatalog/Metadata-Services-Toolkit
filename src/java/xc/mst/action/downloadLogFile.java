
package xc.mst.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.manager.logs.DefaultLogService;
import xc.mst.manager.logs.LogService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;
import javax.servlet.ServletOutputStream;
import org.apache.struts2.interceptor.*;
import xc.mst.constants.Constants;
import javax.servlet.http.*;
import java.io.*;
/**
 *  Downloads a log file
 *
 * @author Tejaswi Haramurali
 */
public class downloadLogFile extends ActionSupport implements ServletResponseAware
{
    /** Indicates the type of log file being downloaded */
    private String logType;

    /** The ID of the log */
    private int id;

    /** The HTTP repsonse object */
    private HttpServletResponse response;

    /** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    public String execute()
    {
        try
        {
            String filename = "";
            String fullpath = "";

            if(logType.equalsIgnoreCase("Service"))
            {
                ServicesService servicesService = new DefaultServicesService();
                Service service = servicesService.getServiceById(id);
                fullpath = service.getServicesLogFileName();
                filename = service.getName()+"ServiceLog";
            }
            else if(logType.equalsIgnoreCase("HarvestOut"))
            {
                ServicesService servicesService = new DefaultServicesService();
                Service service = servicesService.getServiceById(id);
                fullpath = service.getHarvestOutLogFileName();
                filename = service.getName()+"HarvestOutLog";
            }
            else if(logType.equalsIgnoreCase("HarvestIn"))
            {
                ProviderService providerService = new DefaultProviderService();
                Provider provider = providerService.getProviderById(id);
                fullpath = provider.getLogFileName();
                filename = provider.getName();

            }
            else
            {
                LogService logService = new DefaultLogService();
                Log log = logService.getById(id);
                fullpath = log.getLogFileLocation();
                filename = log.getLogFileName();
            }

            response.setContentType ("application/txt");
            //set the header and also the Name by which user will be prompted to save
            response.setHeader("Content-Disposition","attachment;filename="+filename);
            FileInputStream FIS = new FileInputStream(fullpath);
            ServletOutputStream SOS = response.getOutputStream();
            byte[] b = new byte[1];
            while(FIS.read(b)!=-1)
                {
                    SOS.write(b);
                }
            SOS.flush();
            SOS.close();
            FIS.close();
        }
        catch(Exception e)
        {
            log.debug(e);
        }
        return SUCCESS;
    }

    public void setServletResponse(HttpServletResponse response)
    {
        this.response = response;
    }

    public void setLogType(String logType)
    {
        this.logType = logType;
    }

    public String getLogType()
    {
        return this.logType;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

}

