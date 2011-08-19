/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action.services;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;

/**
 * Deletes a service from the MST
 * 
 * @author Tejaswi Haramurali
 */
public class DeleteService extends BaseActionSupport {
    /** Serial Id */
    private static final long serialVersionUID = -650419286679050797L;

    /** The ID of the service to be deleted */
    private int serviceId;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Message explaining why the service cannot be deleted */
    private String message;

    /** Determines whether service is deleted */
    private boolean deleted;

    /** Error type */
    private String errorType;

    /** True if service is in a running/pause status and cannot be deleted */
    private boolean invalidServiceDeleteStatus = false;

    /**
     * Overrides default implementation to delete a service.
     * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute() {
        if (log.isDebugEnabled()) {
            log.debug("DeleteService:execute():Service Id to be deleted : " + serviceId);
        }

        Service service = null;
        try {
            service = getServicesService().getServiceById(serviceId);

            long numberOfRecordsHarvested = getRecordService().getNumberOfRecordsByServiceId(serviceId);
            // Delete service only if it is not harvested.
            if (service.getStatus().equals(Status.RUNNING) || service.getStatus().equals(Status.PAUSED)) {
                message = service.getName() + " cannot be deleted when it is currently running or paused.";
                deleted = false;
                invalidServiceDeleteStatus = true;
            } else if (numberOfRecordsHarvested > 0) {
                message = "Deleting the " + service.getName() + " will result in deletion of " + numberOfRecordsHarvested + " records created by the service and the processing rules that deliver records to and from this service.";
                deleted = false;
            } else {
                getServicesService().deleteService(service);
                deleted = true;
            }

            return SUCCESS;
        } catch (DataException e) {
            log.error("Exception occured while deleting the service " + ((service != null) ? service.getName() : ""), e);
            this.addFieldError("viewRepositoryError", "Error occured while deleting service. Email has been sent to the administrator regarding the error.");
            getUserService().sendEmailErrorReport();
            return INPUT;
        } catch (IndexException ie) {
            log.error("Exception occured while deleting the service " + ((service != null) ? service.getName() : "") + " and index. Check the path to solr folder.", ie);
            this.addFieldError("viewRepositoryError", "Error occured while deleting the service " + ((service != null) ? service.getName() : "") + ".Email has been sent to the administrator regarding the error.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Delete service and its harvested records
     * 
     */
    public String deleteServiceAndRecords() {
        if (log.isDebugEnabled()) {
            log.debug("DeleteRepository:deleteServiceAndRecords():Service Id to be deleted : " + serviceId);
        }
        Service service = null;

        try {
            service = getServicesService().getServiceById(serviceId);

            // Delete service
            getServicesService().deleteService(service);
            // BDA - I don't think we need to schedule this anymore - should be a quick operation.
            // getServicesService().deleteServiceAndRecordsByJob(service);
            return SUCCESS;
        } catch (DataException e) {
            log.error("Exception occured while deleting the service " + ((service != null) ? service.getName() : ""), e);
            this.addFieldError("viewRepositoryError", "Error occured while deleting service. Email has been sent to the administrator regarding the error.");
            getUserService().sendEmailErrorReport();
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
     * @param serviceId
     *            service ID
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Returns the ID of the service to be deleted
     * 
     * @return service ID
     */
    public int getServiceId() {
        return this.serviceId;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public boolean isInvalidServiceDeleteStatus() {
        return invalidServiceDeleteStatus;
    }

    public void setInvalidServiceDeleteStatus(boolean invalidServiceDeleteStatus) {
        this.invalidServiceDeleteStatus = invalidServiceDeleteStatus;
    }

}
