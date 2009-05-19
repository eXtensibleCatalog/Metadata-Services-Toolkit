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

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;

/**
 * Tag to get latest harvest end time
 *
 * @author Sharmila Ranganathan
 */
public class HarvestUtil {
		
	/** A reference to the logger for this class */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * Returns the latest harvest end time for the given harvest schedule 
	 * 
	 * @param harvestSchedule - Harvest schedule to get the latest harvest end time
	 * @return Returns the latest harvest end time 
	 * @throws DatabaseConfigException 
	 */
	public static String latestHarvest(HarvestSchedule harvestSchedule) throws DatabaseConfigException
	{

		ScheduleService scheduleService = new DefaultScheduleService();
		
		Timestamp latestRun = scheduleService.getLatestHarvestEndTime(harvestSchedule); 
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm");
		String output = "Not yet harvested";
		if (latestRun != null) {
			output = "Success last run completed " + format.format(latestRun);
		}
		
		return output;
		
	}
}
