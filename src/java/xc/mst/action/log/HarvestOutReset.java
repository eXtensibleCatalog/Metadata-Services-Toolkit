
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
 * Resets all the 'Harvest-Out log' files relating to a Service
 *
 * @author Tejaswi Haramurali
 */
public class HarvestOutReset extends ActionSupport
{
    /** Creates a service object for Services */
    private ServicesService servicesService = new DefaultServicesService();

    /**The name of the log file which needs to be reset **/
    private String harvestOutLogFileName;

    /**ID of the service to be reset */
    private int serviceId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	
     /**
     * Sets the Service ID of the Service whose Service Logs need to be reset
     *
     * @param serviceId service ID
     */
    public void setServiceId(int serviceId)
    {
        this.serviceId = serviceId;
    }

    /**
     * Returns the Service ID of the Service whose Service Logs need to be reset
     *
     * @return service ID
     */
    public int getServiceId()
    {
        return this.serviceId;
    }

    /**
     * Sets the name of the log file for the service
     *
     * @param serviceName  name of the log file
     */
    public void setHarvestOutLogFileName(String harvestOutLogFileName)
    {
        this.harvestOutLogFileName = harvestOutLogFileName;
    }

    /**
     * Returns the name of the Log file for the Service
     *
     * @return name of the log file
     */
    public String getHarvestOutLogFileName()
    {
        return this.harvestOutLogFileName;
    }

    /**
     * Overrides default implementation to reset the 'Harvest-Out Logs' for a service.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Service tempService = servicesService.getServiceById(serviceId);
            tempService.setHarvestOutLastLogReset(new Date());
            tempService.setHarvestOutWarnings(0);
            tempService.setHarvestOutErrors(0);
            tempService.setHarvestOutRecordsAvailable(0);
            tempService.setHarvestOutRecordsHarvested(0);
            servicesService.updateService(tempService);
            String filename = harvestOutLogFileName;
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("There was an error resetting the Harvest-Out log files",e);
            this.addFieldError("harvestOutResetError", "There was an error resetting the Harvest-Out log files");
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

    /**
     * Resets all the harvest-out log files relating to services
     *
     * @return {@link #SUCCESS}
     */
    public String resetAll()
    {
        try
        {
            List<Service> serviceList = servicesService.getAllServices();
            Iterator<Service> harvIter = serviceList.iterator();
            while(harvIter.hasNext())
            {
                
                Service tempService = (Service)harvIter.next();
                tempService.setHarvestOutLastLogReset(new Date());
                tempService.setHarvestOutWarnings(0);
                tempService.setHarvestOutErrors(0);
                tempService.setHarvestOutRecordsAvailable(0);
                tempService.setHarvestOutRecordsHarvested(0);
                servicesService.updateService(tempService);
                String filename = tempService.getHarvestOutLogFileName();
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
           
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("There was an error resetting the Harvest-Out log files",e);
            this.addFieldError("harvestOutResetError", "There was an error resetting the Harvest-Out log files");
            errorType = "error";
            return SUCCESS;
        }
    }
}
