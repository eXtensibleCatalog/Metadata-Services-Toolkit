/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.harvest;

import java.util.List;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.dao.DataException;
import xc.mst.dao.harvest.DefaultHarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;

/**
 * Service to access Schedules
 *
 * @author Sharmila Ranganathan
 *
 */
public class DefaultScheduleService implements ScheduleService {

	private HarvestScheduleDAO harvestScheduleDAO = new DefaultHarvestScheduleDAO();


	/**
	 * Get schedule having the specified schedule id
	 *
	 * @param scheduleId Id of the schedule
	 * @return Schedule if exist else null
	 */
	public HarvestSchedule getScheduleById(int scheduleId) {
		return harvestScheduleDAO.getById(scheduleId);
	}

	/**
	 * Delete Schedule
	 *
	 * @param schedule schedule to be deleted
	 * @throws DataException Thrown when problem in deleting the schedule
	 */
	public void deleteSchedule(HarvestSchedule harvestSchedule) throws DataException {
		harvestScheduleDAO.delete(harvestSchedule);
	}

    /**
     *
     * @param schedule schedule to inserted into the database
     */
    public void insertSchedule(HarvestSchedule harvestSchedule) throws DataException {
    	harvestScheduleDAO.insert(harvestSchedule);
    }

     /**
     *
     * @param schedule schedule whose details should be updated in the database
     */
    public void updateSchedule(HarvestSchedule harvestSchedule) throws DataException {
    	harvestScheduleDAO.update(harvestSchedule);
    }

    /**
     * Get all schedules
     *
     * @return all schedules
     */
    public List<HarvestSchedule> getAllSchedules()  {
    	return harvestScheduleDAO.getAll();
    }

    /**
     * Get a schedule by name
     *
     * @param name Name of the schedule
     */
    public HarvestSchedule getScheduleByName(String name) throws DataException {
    	return harvestScheduleDAO.getByName(name);
    }

    /**
	 * Gets all schedules in the database sorted by their names
	 *
	 * @param asc True to sort in ascending order, false to sort in descending order
	 * @return A list containing all schedules in the database sorted by their names
	 */
	public List<HarvestSchedule> getAllSchedulesSorted(boolean sort,String columnSorted)
    {
        return harvestScheduleDAO.getSorted(sort,columnSorted);
    }
}
