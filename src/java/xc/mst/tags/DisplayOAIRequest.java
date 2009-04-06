/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.tags;

import java.util.Iterator;
import java.util.List;
import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.provider.Provider;
import xc.mst.manager.harvest.DefaultHarvestService;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.HarvestService;
import xc.mst.manager.harvest.ScheduleService;

/**
 * Displays the OAI Request for a certain Provider
 *
 * @author Tejaswi Haramurali
 */
public class DisplayOAIRequest
{

    public String displayOAIRequest(Provider provider)
    {
        try
        {
            ScheduleService scheduleService = new DefaultScheduleService();
            HarvestService harvestService = new DefaultHarvestService();
            List allHarvestSchedules = scheduleService.getAllSchedules();
            Iterator harvestIter = allHarvestSchedules.iterator();
            while(harvestIter.hasNext())
            {
                HarvestSchedule schedule = (HarvestSchedule)harvestIter.next();
                if(schedule.getProvider().getId()==provider.getId())
                {
                    List harvestList = harvestService.getHarvestsForSchedule(schedule.getId());
                    if(harvestList.size()!=0)
                    {
                        Iterator iter = harvestList.iterator();
                        Harvest tempHarvest = (Harvest)iter.next();
                        return tempHarvest.getRequest();
                    }
                }
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "Error : Unable to return the harvest request URL";
    }
}
