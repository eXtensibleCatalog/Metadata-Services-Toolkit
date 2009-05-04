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
    /** Request object */
    private HttpServletRequest request;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** This is the display text that displays the status of the process */
    private String displayText;

    /**
     * Sets the text to be displayed in the JSP
     *
     * @param displayText display text
     */
    public void setDisplayText(String displayText)
    {
        this.displayText = displayText;
    }

    /**
     * Returns the text to be displayed in the JSP
     *
     * @return display text
     */
    public String getDisplaytext()
    {
        return this.displayText;
    }

    /**
     * Overrides default implementation for methods to interact with the services.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            if(Scheduler.getRunningJob()!=null)
            {
                Scheduler.cancelRunningJob();
                request.getSession().setAttribute("serviceBarDisplay", "abort");
            }
            else
            {
                setDisplayText("Already_ended");
                request.getSession().setAttribute("serviceBarDisplay", "abort");
            }
            
        }
        catch(Exception e)
        {
            log.error(e);
            this.addFieldError("serviceAbortError", "ERROR : The process could not be aborted ");            
        }
        return SUCCESS;
    }

    /**
     * Method for pausing a service
     *
     * @return {@link #SUCCESS}
     */
    public String pauseJob()
    {
        try
        {
            if(Scheduler.getRunningJob()!=null)
            {
                Scheduler.pauseRunningJob();
                request.getSession().setAttribute("serviceBarDisplay", "resume");
            }
            else
            {
                setDisplayText("Already_ended");
                request.getSession().setAttribute("serviceBarDisplay", "abort");
            }
           
            
        }
        catch(Exception e)
        {
            log.error(e);
            this.addFieldError("servicePauseError", "ERROR : There was an error pausing the specified service");            
        }
        return SUCCESS;
    }

    /**
     * Method which resumes a service
     *
     * @return {@link #SUCCESS}
     */
    public String resumeJob()
    {
        try
        {
            if(Scheduler.getRunningJob()!=null)
            {
                Scheduler.resumePausedJob();
                request.getSession().setAttribute("serviceBarDisplay", "pause");
            }
            else
            {
                setDisplayText("Already_ended");
                request.getSession().setAttribute("serviceBarDisplay", "abort");
            }
           
        }
        catch(Exception e)
        {
            log.error(e);
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
