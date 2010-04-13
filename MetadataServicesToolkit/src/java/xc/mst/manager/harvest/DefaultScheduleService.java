/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.harvest;

import java.sql.Timestamp;
import java.util.List;

import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.provider.Provider;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.harvest.DefaultHarvestDAO;
import xc.mst.dao.harvest.DefaultHarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;

/**
 * Service to access Schedules
 *
 * @author Sharmila Ranganathan
 *
 */
public class DefaultScheduleService implements ScheduleService {

    /** DAO object for harvest schedules*/
	private HarvestScheduleDAO harvestScheduleDAO = new DefaultHarvestScheduleDAO();

    /** DAO object for harvests */
	private HarvestDAO harvestDAO = new DefaultHarvestDAO();


	/**
	 * Get schedule having the specified schedule id
	 *
	 * @param scheduleId Id of the schedule
	 * @return Schedule if exist else null
	 * @throws DatabaseConfigException 
	 */
	public HarvestSchedule getScheduleById(int scheduleId) throws DatabaseConfigException {
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
    	harvestScheduleDAO.update(harvestSchedule, true);
    }

    /**
     * Get all schedules
     *
     * @return all schedules
     * @throws DatabaseConfigException 
     */
    public List<HarvestSchedule> getAllSchedules() throws DatabaseConfigException  {
    	return harvestScheduleDAO.getAll();
    }

    /**
     * Get a schedule by name
     *
     * @param name Name of the schedule
     */
    public HarvestSchedule getScheduleByName(String name) throws DatabaseConfigException {
    	return harvestScheduleDAO.getByName(name);
    }

    /**
	 * Gets all schedules in the database sorted by their names
	 *
	 * @param asc True to sort in ascending order, false to sort in descending order
	 * @return A list containing all schedules in the database sorted by their names
     * @throws DatabaseConfigException 
	 */
	public List<HarvestSchedule> getAllSchedulesSorted(boolean sort,String columnSorted) throws DatabaseConfigException
    {
        return harvestScheduleDAO.getSorted(sort,columnSorted);
    }

    /**
	 * Gets harvest schedule for a provider
	 *
	 * @param provider Provider to get the harvest schedule
	 * @return Harvest schedule found
     * @throws DatabaseConfigException 
	 */
	public HarvestSchedule getScheduleForProvider(Provider provider) throws DatabaseConfigException
    {
        return harvestScheduleDAO.getHarvestScheduleForProvider(provider.getId());
    }

    /**
	 * Gets harvests for a  schedule 
	 *
	 * @param harvestSchedule harvest schedule to get the harvests
	 * @return List of Harvest found
     * @throws DatabaseConfigException 
	 */
	public List<Harvest> getHarvestsForSchedule(HarvestSchedule harvestSchedule) throws DatabaseConfigException
    {
        return harvestDAO.getHarvestsForSchedule(harvestSchedule.getId());
    }
	
	/**
	 * Get latest harvest end time for given harvest schedule
	 * 
	 * @param harvestSchedule harvest schedule
	 * @return latest harvest end time if found otherwise null
	 * @throws DatabaseConfigException 
	 */
	public Timestamp getLatestHarvestEndTime(HarvestSchedule harvestSchedule) throws DatabaseConfigException
    {
        return harvestDAO.getLatestHarvestEndTimeForSchedule(harvestSchedule.getId());
    }
}
