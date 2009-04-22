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
 * Tag to check user permissions
 *
 * @author Sharmila Ranganathan
 */
public class HarvestUtil {
		
		/**
		 * Determine if the contents can be moved into the specified location.  This is true 
		 * if the destination is not equal to the current destination.  A collection cannot be 
		 * moved into itself;
		 * 
		 * @param objectsToMove - set of information to be moved
		 * @param destination - destination  to move to
		 * @return true if the set of information can be moved into the specified location
		 */
		public static String latestHarvest(HarvestSchedule harvestSchedule)
		{
			ScheduleService scheduleService = new DefaultScheduleService();
			
			List<Harvest> harvests = scheduleService.getHarvestForSchedule(harvestSchedule); 
			
			Timestamp latestRun = null;
			for(Harvest harvest:harvests) {
				latestRun = harvest.getEndTime();
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
