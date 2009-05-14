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
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.constants.Constants;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * Displays the OAI request that was actually sent
 *
 * @author Tejaswi Haramurali
 */
public class DisplayOAIRequest extends ActionSupport
{
    /** The ID of the provider whose related OAI request is being displayed */
    private int providerId;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The string that that depicts the OAI requests*/
    private String oaiRequest;

    /**Boolean value which determines whether a harvest has been set up for the provider in question */
    private boolean noHarvestString;
   

    /**
     * Overrides default implementation to return the OAI PMH request information pertaining to a repository.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            ScheduleService scheduleService = new DefaultScheduleService();
            ProviderService providerService = new DefaultProviderService();
            HarvestSchedule schedule = scheduleService.getScheduleForProvider(providerService.getProviderById(providerId));

            if(schedule!=null)
            {
                setOaiRequest(schedule.getRequest());
            }
            else
            {
                setNoHarvestString(true);
                log.error("No Harvest has been set up for the provider");
            }
            /*
            List<HarvestSchedule> allHarvestSchedules = scheduleService.getAllSchedules();
            Iterator<HarvestSchedule> harvestIter = allHarvestSchedules.iterator();
            while(harvestIter.hasNext())
            {
                HarvestSchedule schedule = (HarvestSchedule)harvestIter.next();
                if(schedule.getProvider().getId()== providerId)
                {
                    setRequestString(schedule.getRequest());
                    break;
                }
            }
            */
        }
        /*
        catch(NullPointerException e)
        {
            setNoHarvestString(true);
            log.error("No Harvest has been set up for the provider",e);
        }*/
        catch(Exception e)
        {
            log.error("Unable to Display the request String",e);
        }
        return SUCCESS;
    }

     /**
     * Sets the request String to be displayed in the JSP
     *
     * @param requestString request String
     */
    public void setOaiRequest(String requestString)
    {
        this.oaiRequest = requestString;
    }

    /**
     * Returns the request string to be displayed in the JSP
     *
     * @return request String
     */
    public String getOaiRequest()
    {
        return this.oaiRequest;
    }

    /**
     * Sets the ID of the provider whose OAI PMH request
     *
     * @param providerId provider ID
     */
    public void setProviderId(int providerId)
    {
        this.providerId = providerId;
    }

    /**
     * Returns the provider whose OAI PMH request details should be displayed
     *
     * @return provider ID
     */
    public int getProviderId()
    {
        return this.providerId;
    }

    /**
     * Sets the noHarvestString boolean variable
     *
     * @param noHarvestString boolean variable
     */
    public void setNoHarvestString(boolean noHarvestString)
    {
        this.noHarvestString = noHarvestString;
    }

    /**
     * Returns the boolean value which determines whether a harvest has been set up for a given provider
     *
     * @return boolean value
     */
    public boolean getNoHarvestString()
    {
        return this.noHarvestString;
    }
}
