/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import xc.mst.constants.Constants;
import xc.mst.scheduling.Scheduler;

/**
 * This action class defines method which are used to abort, pause and resume service processes which are being run by the MST
 *
 * @author Tejaswi Haramurali
 */
public class ServiceInteract extends ActionSupport implements ServletRequestAware
{
    HttpServletRequest request;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    @Override
    public String execute()
    {
        try
        {
            Scheduler.cancelRunningJob(); 
            request.getSession().setAttribute("serviceBarDisplay", null);
            System.out.println("The process was aborted");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("serviceAbortError", "ERROR : The process could not be aborted ");            
        }
        return SUCCESS;
    }

    public String pauseJob()
    {
        try
        {
            Scheduler.pauseRunningJob();
            request.getSession().setAttribute("serviceBarDisplay", "resume");
            System.out.println("The process is paused");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("servicePauseError", "ERROR : There was an error pausing the specified service");            
        }
        return SUCCESS;
    }

    public String resumeJob()
    {
        try
        {
            Scheduler.resumePausedJob();
            request.getSession().setAttribute("serviceBarDisplay", "pause");
            System.out.println("The process is resumed");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("serviceResumeError", "ERROR : There was an error resuming the specified service");
        }
        return SUCCESS;
    }

    /**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
