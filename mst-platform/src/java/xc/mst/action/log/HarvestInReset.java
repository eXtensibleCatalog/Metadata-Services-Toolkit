
/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Resets all the 'Harvest-In log' files relating to a Provider
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class HarvestInReset extends BaseActionSupport {

    /**The name of the log file which needs to be reset **/
    private String harvestInLogFileName;

    /**ID of the service to be reset */
    private int providerId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Error type */
    private String errorType;

    /**
     * Overrides default implementation to reset the 'Harvest-In Logs' for a service.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Provider provider = getProviderService().getProviderById(providerId);
            if(provider==null)
            {
                this.addFieldError("HarvestInLogReset", "Error Occurred while resetting harvest-in log. An email has been sent to the administrator.");
                getUserService().sendEmailErrorReport();
                errorType = "error";
                return SUCCESS;
            }
            provider.setErrors(0);
            provider.setWarnings(0);
            provider.setRecordsAdded(0);
            provider.setRecordsReplaced(0);
            provider.setLastLogReset(new Date());
            getProviderService().updateProvider(provider);
            String filename = provider.getLogFileName(true);
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.close();

            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("HarvestInLogReset", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return SUCCESS;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("HarvestInLogReset", "Error Occurred while resetting harvest-in log. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
        catch(FileNotFoundException fe)
        {
            log.error(fe.getMessage(),fe);
            this.addFieldError("HarvestInLogReset", "Error Occurred while resetting harvest-in log. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
    }



    /**
     * Resets all the logs that are related to the providers
     *
     * @return {@link #SUCCESS}
     */
    public String resetAll()
    {
        try
        {
            List<Provider> providerList = getProviderService().getAllProviders();
            Iterator<Provider> provIter = providerList.iterator();
            while(provIter.hasNext())
            {
                Provider provider = (Provider)provIter.next();
                provider.setErrors(0);
                provider.setWarnings(0);
                provider.setRecordsAdded(0);
                provider.setRecordsReplaced(0);
                provider.setLastLogReset(new Date());
                getProviderService().updateProvider(provider);
                String filename = provider.getLogFileName(true);
                PrintWriter printWriter = new PrintWriter(filename);
                printWriter.close();
            }
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("HarvestInLogReset", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return SUCCESS;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("HarvestInLogReset", "Error Occurred while resetting all harvest-in logs. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
        catch(FileNotFoundException fe)
        {
            log.error(fe.getMessage(),fe);
            this.addFieldError("HarvestInLogReset", "Error Occurred while resetting all harvest-in logs. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return SUCCESS;
        }
    }

     /**
     * Returns error type
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
     * Sets the Provider ID of the provider whose logs are to be reset
      *
     * @param providerId provider ID
     */
    public void setProviderId(int providerId)
    {
        this.providerId = providerId;
    }

    /**
     * Returns the Provider ID of the provider whose logs are reset
     *
     * @return ID of the provider
     */
    public int getProviderId()
    {
        return this.providerId;
    }

    /**
     * Sets the name of the log file for the Provider
     *
     * @param harvestInLogFileName filename for the log
     */
    public void setHarvestInLogFileName(String harvestInLogFileName)
    {
        this.harvestInLogFileName = harvestInLogFileName;
    }

    /**
     * Returns the name of the Log file for the Provider
     *
     * @return filename for the log
     */
    public String getHarvestInLogFileName()
    {
        return this.harvestInLogFileName;
    }
}
