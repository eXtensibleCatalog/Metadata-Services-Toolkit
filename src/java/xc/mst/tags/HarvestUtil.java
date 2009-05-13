/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.tags;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;

/**
 * Tag to get latest harvest end time
 *
 * @author Sharmila Ranganathan
 */
public class HarvestUtil {
		
		/**
		 * Returns the latest harvest end time for the given harvest schedule 
		 * 
		 * @param harvestSchedule - Harvest schedule to get the latest harvest end time
		 * @return Returns the latest harvest end time 
		 */
		public static String latestHarvest(HarvestSchedule harvestSchedule)
		{
			ScheduleService scheduleService = new DefaultScheduleService();
			
			List<Harvest> harvests = scheduleService.getHarvestsForSchedule(harvestSchedule); 
			Timestamp latestRun = null;
			for(Harvest harvest:harvests) {
				if(latestRun == null) {
					latestRun = harvest.getEndTime();
					continue;
				}
				
				if (harvest.getEndTime().after(latestRun)) {
					latestRun = harvest.getEndTime();
				}
				
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm");

			String output = "Not yet harvested";
			if (latestRun != null) {
				output = "Success last run completed " + format.format(latestRun);
			}
			
			return output;
			
		}
}
