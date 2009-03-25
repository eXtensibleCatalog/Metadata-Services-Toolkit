
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
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * Resets all the 'Harvest-In log' files relating to a Provider
 *
 * @author Tejaswi Haramurali
 */
public class HarvestInReset extends ActionSupport
{
    /** Creates service object for providers */
    private ProviderService providerService = new DefaultProviderService();

    /**The name of the log file which needs to be reset **/
    private String harvestInLogFileName;

    /**ID of the service to be reset */
    private String providerId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Error type */
	private String errorType; 

     /**
     * sets the Provider ID of the provider whose logs are to be reset
     * @param providerId provider ID
     */
    public void setProviderId(String providerId)
    {
        this.providerId = providerId;
    }

    /**
     * returns the Provider ID of the provider whose logs are reset
     * @return Provider ID
     */
    public String getProviderId()
    {
        return this.providerId;
    }

    /**
     * sets the name of the log file for the Provider
     * @param harvestInLogFileName log file name
     */
    public void setHarvestInLogFileName(String harvestInLogFileName)
    {
        this.harvestInLogFileName = harvestInLogFileName;
    }

    /**
     * returns the name of the Log file for the Provider
     * @return log file name
     */
    public String getHarvestInLogFileName()
    {
        return this.harvestInLogFileName;
    }

    /**
     * Overrides default implementation to reset the 'Harvest-In Logs' for a service.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Provider provider = providerService.getProviderById(Integer.parseInt(providerId));
            provider.setErrors(0);
            provider.setWarnings(0);
            provider.setLastLogReset(new Date());
            providerService.updateProvider(provider);
            String filename = provider.getLogFileName();
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();

            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("harvestInResetError", "Error : There was an error resetting the log files");
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

    /**
     * resets all the logs that are related to the providers
     * @return {@link #SUCCESS}
     */
    public String resetAll()
    {
        try
        {
            List providerList = providerService.getAllProviders();
            Iterator provIter = providerList.iterator();
            while(provIter.hasNext())
            {
                Provider provider = (Provider)provIter.next();
                provider.setErrors(0);
                provider.setWarnings(0);
                provider.setLastLogReset(new Date());
                providerService.updateProvider(provider);
                String filename = provider.getLogFileName();
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("harvestInResetError", "Error : There was an error resetting the log files");
            errorType = "error";
            return SUCCESS;
        }
    }
}
