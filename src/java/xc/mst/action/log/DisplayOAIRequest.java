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
    private String requestString;

   

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

        }
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
    public void setRequestString(String requestString)
    {
        this.requestString = requestString;
    }

    /**
     * Returns the request string to be displayed in the JSP
     *
     * @return request String
     */
    public String getRequestString()
    {
        return this.requestString;
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
}
