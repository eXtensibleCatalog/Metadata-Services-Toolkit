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
 * This class is used to return the latest status of the processes running in the MST
 *
 * @author Tejaswi Haramurali
 */
public class RefreshServiceBar extends ActionSupport implements ServletRequestAware
{
     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Servlet request object */
    private HttpServletRequest request;

    /** The current process being executed by the MST */
    private String currentProcess;

    /** Determines how the HTML should be displayed in the view page for this action */
    private String displayType;

    /**
     * Overrides default implementation to refresh the contents of the service status bar.
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
                    if(Scheduler.getRunningJob().getJobStatus().equalsIgnoreCase("CANCELED"))
                    {
                        currentProcess = "Aborting " + Scheduler.getRunningJob().getJobName() + "...";
                        setCurrentProcess(currentProcess);
                    }
                    else
                    {
                        currentProcess = Scheduler.getRunningJob().getJobName();
                        setCurrentProcess(currentProcess);
                    }
                }
                else
                {
                    request.getSession().setAttribute("serviceBarDisplay", null);
                }
                displayType = (String)request.getSession().getAttribute("serviceBarDisplay");
                setDisplayType(displayType);
                return SUCCESS;
            }
            catch(Exception e)
            {
                log.error("The status of the services running in the MST , could not be displayed correctly",e);
                this.addFieldError("refreshServiceBar", "The status of the services running in the MST , could not be displayed correctly");
                return INPUT;
            }
        

    }

    /**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

    /**
     * Sets the name of the surrent process being executed
     *
     * @param currentProcess name of the process
     */
    public void setCurrentProcess(String currentProcess)
    {
        this.currentProcess = currentProcess;
    }

    /**
     * Returns the current process being executed by the MST
     *
     * @return current process
     */
    public String getCurrentProcess()
    {
        return this.currentProcess;
    }

    /**
     * Sets the type of display depending on the buttons to be displayed in the JSP
     *
     * @param displayType display type
     */
    public void setDisplayType(String displayType)
    {
        this.displayType = displayType;
    }

    /**
     * Returns the type of display depending on the buttons to be displayed in the JSP
     *
     * @return display type
     */
    public String getDisplayType()
    {
        return this.displayType;
    }
}


   