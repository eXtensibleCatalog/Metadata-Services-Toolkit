
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
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * Resets all the 'Service log' files relating to a Service
 *
 * @author Tejaswi Haramurali
 */
public class ServiceReset extends ActionSupport
{
    /**Creates a service object for Services */
    private ServicesService servicesService = new DefaultServicesService();

    /**The name of the log file which needs to be reset **/
    private String serviceLogFileName;

    /**ID of the service to be reset */
    private String serviceId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Error type */
	private String errorType; 

    /**
     * Sets the Service ID of the Service whose Service Logs need to be reset
     *
     * @param serviceId service ID
     */
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    /**
     * Returns the Service ID of the Service whose Service Logs need to be reset
     *
     * @return service ID
     */
    public String getServiceId()
    {
        return this.serviceId;
    }

    /**
     * Sets the name of the service
     *
     * @param serviceName log file name
     */
    public void setServiceLogFileName(String serviceLogFileName)
    {
        this.serviceLogFileName = serviceLogFileName;
    }

    /**
     * Returns the name of the Log file for the Service
     *
     * @return log file name
     */
    public String getServiceLogFileName()
    {
        return this.serviceLogFileName;
    }

    /**
     * Overrides default implementation to reset the 'Service Logs' for a service.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            
            Service tempService = servicesService.getServiceById(Integer.parseInt(serviceId));
            tempService.setServicesLastLogReset(new Date());
            tempService.setServicesWarnings(0);
            tempService.setServicesErrors(0);
            tempService.setOutputRecordCount(0);
            tempService.setInputRecordCount(0);
            servicesService.updateService(tempService);
            String filename = serviceLogFileName;
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();

            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error(e);
            this.addFieldError("serviceResetError", "Error : There was error resetting the log files");
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * Resets all the log files related to services
     *
     * @return {@link #SUCCESS}
     */
    public String resetAll()
    {
        try
        {
            List<Service> servicesList = servicesService.getAllServices();
            Iterator<Service> servIter = servicesList.iterator();
            while(servIter.hasNext())
            {
                Service tempService = (Service)servIter.next();
                tempService.setServicesLastLogReset(new Date());
                tempService.setServicesWarnings(0);
                tempService.setServicesErrors(0);
                tempService.setOutputRecordCount(0);
                tempService.setInputRecordCount(0);
                servicesService.updateService(tempService);
                String filename = tempService.getServicesLogFileName();
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error(e);
            this.addFieldError("serviceResetError", "Error : There was error resetting the log files");
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * Returns the error type
     *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets the error type
     *
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
