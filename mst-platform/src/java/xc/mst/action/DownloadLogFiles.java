/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;

import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.logs.LogService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;
/**
 *  Downloads a log file
 *
 * @author Tejaswi Haramurali
 */
public class DownloadLogFiles extends ActionSupport implements ServletResponseAware
{
    /** Serial id*/
	private static final long serialVersionUID = -2716024718026119266L;

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
        	ServicesService servicesService = (ServicesService)MSTConfiguration.getBean("ServicesService");
        	
            String filename = "";
            String fullpath = "";

            if(logType.equalsIgnoreCase("Service"))
            {
                
                Service service = servicesService.getServiceById(id);
                fullpath = service.getServicesLogFileName(true);
                filename = service.getName()+"ServiceLog";
            }
            else if(logType.equalsIgnoreCase("HarvestOut"))
            {
                Service service = servicesService.getServiceById(id);
                fullpath = service.getHarvestOutLogFileName(true);
                filename = service.getName()+"HarvestOutLog";
            }
            else if(logType.equalsIgnoreCase("HarvestIn"))
            {
                ProviderService providerService = (ProviderService)MSTConfiguration.getBean("ProviderService");
                Provider provider = providerService.getProviderById(id);
                fullpath = provider.getLogFileName(true);
                filename = provider.getName();

            }
            else
            {
                LogService logService = (LogService)MSTConfiguration.getBean("LogService");
                Log log = logService.getById(id);
                fullpath = log.getLogFileLocation(true);
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

