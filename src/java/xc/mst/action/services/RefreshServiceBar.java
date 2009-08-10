/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.services;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.constants.Constants;
import xc.mst.scheduling.Scheduler;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This class is used to return the latest status of the processes running in the MST
 *
 * @author Tejaswi Haramurali
 */
public class RefreshServiceBar extends ActionSupport implements ServletRequestAware
{
     /** Serial id */
	private static final long serialVersionUID = -3395960723337222914L;

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
                	if(!Scheduler.getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_PROCESSING_DIRECTIVE)) {
	                    if(Scheduler.getRunningJob().getJobStatus().equalsIgnoreCase(Constants.STATUS_SERVICE_CANCELED))
	                    {
	                    	if (Scheduler.getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
	                        	currentProcess = "Aborting harvest of provider " + Scheduler.getRunningJob().getJobName();
	                        } else {
	                        	currentProcess = "Aborting process " + Scheduler.getRunningJob().getJobName();
	                        }
	                    }
	                    else if (Scheduler.getRunningJob().getJobStatus().equalsIgnoreCase(Constants.STATUS_SERVICE_PAUSED))
	                    {
	                        if (Scheduler.getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
	                        	currentProcess = "Paused harvesting from provider " + Scheduler.getRunningJob().getJobName();
	                        } else {
	                        	currentProcess = "Paused processing through " + Scheduler.getRunningJob().getJobName();
	                        }
	                    }  
	                    else if (Scheduler.getRunningJob().getJobStatus().equalsIgnoreCase(Constants.STATUS_SERVICE_NOT_RUNNING))
	                    {
	                        	currentProcess = null;
	                    } else {
	                    	 if (Scheduler.getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
	                         	currentProcess = "Harvested " +  Scheduler.getRunningJob().getProcessedRecordCount() + " records out of " + Scheduler.getRunningJob().getTotalRecordCount() + " from provider " + Scheduler.getRunningJob().getJobName();
	                         } else {
	                         	currentProcess = "Processed " +  Scheduler.getRunningJob().getProcessedRecordCount() + " records out of " + Scheduler.getRunningJob().getTotalRecordCount() + " through " + Scheduler.getRunningJob().getJobName();
	                         }
	                    }
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


   