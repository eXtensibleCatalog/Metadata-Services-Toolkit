
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
import java.io.FileInputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import xc.mst.bo.service.Service;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 *
 * @author Tejaswi Haramurali
 */
public class downloadServiceLogFile extends ActionSupport
{
    String id;
    String logType;

    public void setId(String id)
    {
        this.id = id;
    }
    public String getId()
    {
        return this.id;
    }

    public void setLogType(String logType)
    {
        this.logType = logType;
    }

    public String getLogType()
    {
        return this.logType;
    }

   @Override
   public String execute()
    {
       try
       {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

                String filename = "";
                String fullpath = "";

                if(logType.equalsIgnoreCase("Service"))
                {
                    ServicesService servicesService = new DefaultServicesService();
                    Service service = servicesService.getServiceById(Integer.parseInt(id));
                    fullpath = "C:/"+service.getServicesLogFileName();
                    filename = service.getName()+"ServiceLog";
                }
                else if(logType.equalsIgnoreCase("HarvestOut"))
                {
                    ServicesService servicesService = new DefaultServicesService();
                    Service service = servicesService.getServiceById(Integer.parseInt(id));
                    fullpath = "C:/"+service.getHarvestOutLogFileName();
                    filename = service.getName()+"HarvestOutLog";
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
                ServletActionContext.setResponse(response);

        }
       catch(Exception e)
       {
       }

        return SUCCESS;
   }
}
