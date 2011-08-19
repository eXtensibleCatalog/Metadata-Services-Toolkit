/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.tags;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.utils.MSTConfiguration;

/**
 * Tag to get latest harvest end time
 * 
 * @author Sharmila Ranganathan
 */
public class HarvestUtil {

    /**
     * Returns the latest harvest end time for the given harvest schedule
     * 
     * @param harvestSchedule
     *            - Harvest schedule to get the latest harvest end time
     * @return Returns the latest harvest end time
     */
    public static String latestHarvest(HarvestSchedule harvestSchedule) throws Exception {
        ScheduleService scheduleService = (ScheduleService) MSTConfiguration.getInstance().getBean("ScheduleService");

        Timestamp latestRun = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        latestRun = scheduleService.getLatestHarvestEndTime(harvestSchedule);
        format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String output = "Not yet harvested";
        if (latestRun != null) {
            output = "last run completed on " + format.format(latestRun);
        }

        return output;

    }

    public static String simpleDateFormat(Timestamp timeStamp) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return simpledateformat.format((java.util.Date) timeStamp);
    }
}
