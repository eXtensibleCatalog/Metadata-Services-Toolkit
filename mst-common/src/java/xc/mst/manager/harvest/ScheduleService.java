/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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

/**
 * Service to access the Schedules
 *
 * @author Sharmila Ranganathan
 *
 */
public interface ScheduleService {

    /**
     * Get schedule having the specified schedule id
     *
     * @param scheduleId Id of the schedule
     * @return Schedule if exist else null
     * @throws DatabaseConfigException
     */
    public HarvestSchedule getScheduleById(int scheduleId) throws DatabaseConfigException;

    /**
     * Delete Schedule
     *
     * @param schedule schedule to be deleted
     * @throws DataException Thrown when problem in deleting the schedule
     */
    public void deleteSchedule(HarvestSchedule schedule) throws DataException;

    /**
     * Add a schedule
     *
     * @param schedule schedule to inserted into the database
     */
    public void insertSchedule(HarvestSchedule schedule) throws DataException;

    /**
     * Update a schedule
     *
     * @param schedule schedule whose details should be updated in the database
     */
    public void updateSchedule(HarvestSchedule schedule) throws DataException;

    /**
     * Get a schedule by name
     *
     * @param name Name of the schedule
     */
    public HarvestSchedule getScheduleByName(String name) throws DataException;

    /**
     * Get all schedules
     *
     * @return all schedules
     * @throws DatabaseConfigException
     */
    public List<HarvestSchedule> getAllSchedules() throws DatabaseConfigException;

    /**
     * returns a sorted list of all the schedules
     * @param sort determines if the list is sorted in ascending or descending order
     * @param columnSorted the column on which the rows are sorted.
     * @return list of schedules
     * @throws DatabaseConfigException
     */
    public abstract List<HarvestSchedule> getAllSchedulesSorted(boolean sort,String columnSorted) throws DatabaseConfigException;

     /**
     * Gets harvest schedule for a provider
     *
     * @param provider Provider to get the harvest schedule
     * @return Harvest schedule found
     * @throws DatabaseConfigException
     */
    public HarvestSchedule getScheduleForProvider(Provider provider) throws DatabaseConfigException;

    /**
     * Gets harvests for a  schedule
     *
     * @param harvestSchedule harvest schedule to get the harvests
     * @return List of Harvest found
     * @throws DatabaseConfigException
     */
    public List<Harvest> getHarvestsForSchedule(HarvestSchedule harvestSchedule) throws DatabaseConfigException;

    /**
     * Get latest harvest end time for given harvest schedule
     *
     * @param harvestSchedule harvest schedule
     * @return latest harvest end time if found otherwise null
     * @throws DatabaseConfigException
     */
    public Timestamp getLatestHarvestEndTime(HarvestSchedule harvestSchedule) throws DatabaseConfigException;

    public Harvest getHarvest(HarvestSchedule harvestSchedule);
}
