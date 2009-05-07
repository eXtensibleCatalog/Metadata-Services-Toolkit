/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import org.apache.log4j.Logger;

import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Deletes a service from the MST
 *
 * @author Tejaswi Haramurali
 */
public class DeleteService extends ActionSupport
{
    /** Serial Id	 */
	private static final long serialVersionUID = -650419286679050797L;

	/** The ID of the service to be deleted */
    private int serviceId;

    /** The service object for services */
    private ServicesService serviceService = new DefaultServicesService();
    
    /** The record service */
    private RecordService recordService = new DefaultRecordService();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Message explaining why the service cannot be deleted */
	private String message;

    /** Determines whether service is deleted */
	private boolean deleted;

	/** Error type */
	private String errorType; 
	
    /**
     * Overrides default implementation to delete a service.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            log.debug("DeleteService:execute():Service Id to be deleted : " + serviceId);
            Service service = serviceService.getServiceById(serviceId);
            
            // Delete service only if it is not harvested.
            if (recordService.getNumberOfRecordsByServiceId(serviceId) > 0) {
                message = "Service has harvested data.";
                deleted = false;
            } else {
    	    	serviceService.deleteService(service);
            	deleted = true;
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e, e.fillInStackTrace());
            this.addFieldError("viewRepositoryError", "Service cannot be deleted");
            errorType = "error";
            return INPUT;
        }
    }
    
    /**
     * Delete service and its harvested records
     * 
     */
    public String deleteServiceAndRecords()
    {
        try
        {
            log.debug("DeleteRepository:deleteServiceAndRecords():Service Id to be deleted : " + serviceId);
            Service service = serviceService.getServiceById(serviceId);
            
            // Delete service
   	    	serviceService.deleteService(service);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("Exception occured while deleting Service.", e);
            this.addFieldError("viewRepositoryError", "Service cannot be deleted");
            errorType = "error";
            return INPUT;
        }
    }
    

	/**
	 * Returns the error message
	 * 
	 * @return error message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns true if service deleted, else false
	 * 
	 * @return Returns true if service deleted, else false
	 */
	public boolean isDeleted() {
		return deleted;
	}


    /**
     * Sets the ID of the service to be deleted
     *
     * @param serviceId service ID
     */
    public void setServiceId(int serviceId)
    {
        this.serviceId = serviceId;
    }
    
    /**
     * Returns the ID of the service to be deleted
     *
     * @return service ID
     */
    public int getServiceId()
    {
        return this.serviceId;
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
