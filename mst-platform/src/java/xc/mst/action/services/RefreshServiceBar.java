/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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

import xc.mst.action.BaseActionSupport;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.scheduling.WorkerThread;

/**
 * This class is used to return the latest status of the processes running in the MST
 *
 * @author Tejaswi Haramurali
 */
public class RefreshServiceBar extends BaseActionSupport implements ServletRequestAware
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
    
    protected static int consecutiveSolrIndexes = 0;

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
            	boolean solrIndexRunning = false;
            	LOG.debug("getScheduler().getRunningJob(): "+getScheduler().getRunningJob());
                if(getScheduler().getRunningJob()!=null && 
                		getScheduler().getRunningJob().getType() != null && 
                		getScheduler().getRunningJob().getJobStatus() != null) {
                	LOG.debug("getScheduler().getRunningJob().getJobStatus(): "+getScheduler().getRunningJob().getJobStatus());
                	LOG.debug("getScheduler().getRunningJob().getType(): "+getScheduler().getRunningJob().getType());
                	
                	if(!getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_PROCESSING_DIRECTIVE) 
                			&& !getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_SERVICE_REPROCESS)
                			&& !getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_DELETE_SERVICE)) {
	                    if(getScheduler().getRunningJob().getJobStatus().equals(Status.CANCELED))
	                    {
	                    	if (getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
	                        	currentProcess = "Aborting harvest of provider " + getScheduler().getRunningJob().getJobName();
	                        } else {
	                        	currentProcess = "Aborting process " + getScheduler().getRunningJob().getJobName();
	                        }
	                    }
	                    else if (getScheduler().getRunningJob().getJobStatus().equals(Status.PAUSED) || 
	                    		getScheduler().getRunningJob().getJobStatus().equals(Status.IDLE))
	                    {
	                        if (getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
	                        	currentProcess = "Paused harvesting from provider " + getScheduler().getRunningJob().getJobName();
	                        } else if (getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.SOLR_INDEXER)) {
	                        	if (getScheduler().wasPausedManually()) {
	                        		currentProcess = "Paused "+getScheduler().getRunningJob().getJobName();
	                        	} else {
	                        		currentProcess = null;
	                        	}
	                        } else {
	                        	currentProcess = "Paused processing through " + getScheduler().getRunningJob().getJobName();
	                        }
	                    }  
	                    else if (getScheduler().getRunningJob().getJobStatus().equals(Status.NOT_RUNNING))
	                    {
	                        	currentProcess = null;
	                    } else {
	                    	 if (getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
	                    		 if (getScheduler().getRunningJob().getRecordsProcessedThisRun() > 0 && 
	                    				 getScheduler().getRunningJob().getRecords2ProcessThisRun() > 0) {
	                    			 currentProcess = "Harvested " +  getScheduler().getRunningJob().getRecordsProcessedThisRun() + 
	                    			 " records out of " + getScheduler().getRunningJob().getRecords2ProcessThisRun() + 
	                    			 " from repository " + getScheduler().getRunningJob().getJobName();
	                    		 } else {
	                    			 currentProcess = "Harvested " +  getScheduler().getRunningJob().getRecordsProcessedThisRun() + 
	                    			 " records from repository " + getScheduler().getRunningJob().getJobName();
	                    		 }
	                    	 } else if (getScheduler().getRunningJob().getType().equalsIgnoreCase(Constants.SOLR_INDEXER)) {
	                    		 solrIndexRunning = true;
	                    		 if (consecutiveSolrIndexes > 2) {
		                        	 LOG.debug("getScheduler().getRunningJob().getRecordsProcessed(): "+
		                        			 getScheduler().getRunningJob().getRecordsProcessedThisRun());
		                        	 LOG.debug("getScheduler().getRunningJob().getTotalRecords(): "+
		                        			 getScheduler().getRunningJob().getRecords2ProcessThisRun());
		                        	 LOG.debug("getScheduler().getRunningJob().getJobName(): "+getScheduler().getRunningJob().getJobName());
	                    			 currentProcess = "Processed " +  getScheduler().getRunningJob().getRecordsProcessedThisRun() + 
	 	                         		" records out of " + getScheduler().getRunningJob().getRecords2ProcessThisRun() + 
	 	                         		" through " + getScheduler().getRunningJob().getJobName();
	                    		 }
	                         } else {
	                         	currentProcess = "Processed " +  getScheduler().getRunningJob().getRecordsProcessedThisRun() + 
	                         	" records out of " + getScheduler().getRunningJob().getRecords2ProcessThisRun() + 
	                         	" through " + getScheduler().getRunningJob().getJobName();
	                         }
	                    }
                	}
                }
                else
                {
                    WorkerThread.serviceBarDisplay = null;
                }
                displayType = WorkerThread.serviceBarDisplay;
                setDisplayType(displayType);
                LOG.debug("solrIndexRunning: "+solrIndexRunning);
                LOG.debug("consecutiveSolrIndexes: "+consecutiveSolrIndexes);
                if (solrIndexRunning) {
                	consecutiveSolrIndexes++;
                } else {
                	consecutiveSolrIndexes=0;
                }
                return SUCCESS;
            }
            catch(Throwable e)
            {
            	LOG.error("", e);
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


   