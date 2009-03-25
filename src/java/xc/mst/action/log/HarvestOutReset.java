
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
    private String serviceId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	
     /**
     * sets the Servic ID of the Service whose Service Logs need to be reset
     * @param serviceId service ID
     */
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    /**
     * returns the Service ID of the Service whose Service Logs need to be reset
     * @return service ID
     */
    public String getServiceId()
    {
        return this.serviceId;
    }

    /**
     * sets the name of the log file for the service
     * @param serviceName service name
     */
    public void setHarvestOutLogFileName(String harvestOutLogFileName)
    {
        this.harvestOutLogFileName = harvestOutLogFileName;
    }

    /**
     * returns the name of the Log file for the Service
     * @return service name
     */
    public String getHarvestOutLogFileName()
    {
        return this.harvestOutLogFileName;
    }

    /**
     * Overrides default implementation to reset the 'Harvest-Out Logs' for a service.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Service tempService = servicesService.getServiceById(Integer.parseInt(serviceId));
            tempService.setHarvestOutLastLogReset(new Date());
            tempService.setHarvestOutWarnings(0);
            tempService.setHarvestOutErrors(0);
            servicesService.updateService(tempService);
            String filename = harvestOutLogFileName;
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();

            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("harvestOutResetError", "Error : There was an error resetting the log files");
            errorType = "error";
            return SUCCESS;
        }
    }


	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

    public String resetAll()
    {
        try
        {
            List serviceList = servicesService.getAllServices();
            Iterator harvIter = serviceList.iterator();
            while(harvIter.hasNext())
            {
                Service tempService = (Service)harvIter.next();
                tempService.setHarvestOutLastLogReset(new Date());
                tempService.setHarvestOutWarnings(0);
                tempService.setHarvestOutErrors(0);
                servicesService.updateService(tempService);
                String filename = tempService.getHarvestOutLogFileName();
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("harvestOutResetError", "Error : There was an error resetting the log files");
            errorType = "error";
            return SUCCESS;
        }
    }
}
