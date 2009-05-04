/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.harvest;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data Access Object for the harvest schedules table
 *
 * @author Eric Osisek
 */
public abstract class HarvestScheduleDAO
{
	/**
	 * The connection to the database
	 */
	protected final static Connection dbConnection = MySqlConnectionManager.getDbConnection();

	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The name of the database table we're interacting with
	 */
	public final static String HARVEST_SCHEDULES_TABLE_NAME = "harvest_schedules";

    /**
     * The name of the providers table
     */
    public final static String PROVIDERS_TABLE_NAME = "providers";

	/**
	 * The name of the harvest schedule ID column
	 */
	public final static String COL_HARVEST_SCHEDULE_ID = "harvest_schedule_id";

	/**
	 * The name of the schedule name column
	 */
	public final static String COL_SCHEDULE_NAME = "schedule_name";

	/**
	 * The name of the recurrence column
	 */
	public final static String COL_RECURRENCE = "recurrence";

        /**
	 * The name of the provider ID column
	 */
	public final static String COL_PROVIDER_ID = "provider_id";

     /**
	 * The name of the provider name column
	 */
	public final static String COL_PROVIDER_NAME = "name";

	/**
	 * The name of the start date column
	 */
	public final static String COL_START_DATE = "start_date";

	/**
	 * The name of the end date column
	 */
	public final static String COL_END_DATE = "end_date";

	/**
	 * The name of the minute column
	 */
	public final static String COL_MINUTE = "minute";

	/**
	 * The name of the day of week column
	 */
	public final static String COL_DAY_OF_WEEK = "day_of_week";

	/**
	 * The name of the hour column
	 */
	public final static String COL_HOUR = "hour";

	/**
	 * The name of the notify email column
	 */
	public final static String COL_NOTIFY_EMAIL = "notify_email";

	/**
	 * A set of all columns which are valid for sorting
	 */
	protected static Set<String> sortableColumns = new HashSet<String>();
	
	/**
	 * The current status of the harvest.
	 */
	public final static String COL_STATUS = "status";
	
	/**
	 * The current request of the harvest.
	 */
	public final static String COL_REQUEST = "request";
	
	// Initialize the list of sortable columns
	static
	{
		sortableColumns.add(COL_HARVEST_SCHEDULE_ID);
		sortableColumns.add(COL_SCHEDULE_NAME);
		sortableColumns.add(COL_RECURRENCE);
		sortableColumns.add(COL_PROVIDER_ID);
		sortableColumns.add(COL_START_DATE);
		sortableColumns.add(COL_END_DATE);
		sortableColumns.add(COL_MINUTE);
		sortableColumns.add(COL_DAY_OF_WEEK);
		sortableColumns.add(COL_HOUR);
		sortableColumns.add(COL_NOTIFY_EMAIL);
		sortableColumns.add(COL_STATUS);
		sortableColumns.add(COL_REQUEST);
	} // end initialization of sortableColumns
	
	/**
	 * Gets all harvest schedules in the database
	 *
	 * @return A list containing all harvest schedules in the database
	 */
	public abstract List<HarvestSchedule> getAll();

	/**
     * Returns a sorted list of all the harvest schedules
     * 
     * @param asc Boolean parameter determines if rows are to be sorted in ascending or descending order
     * @param columnSorted The column on which the rows are sorted
     * @return A sorted list of schedules
     */
	public abstract List<HarvestSchedule> getSorted(boolean asc, String columnSorted);
	
	/**
	 * Gets a harvest schedule by it's ID
	 *
	 * @param harvestScheduleId The ID of the harvest schedule to get
	 * @return The harvest schedule with the passed ID, or null if there was no harvest schedule with that ID.
	 */
	public abstract HarvestSchedule getById(int harvestScheduleId);

	/**
	 * Gets a harvest schedule by it's ID without loading the schedule's steps
	 *
	 * @param harvestScheduleId The ID of the harvest schedule to get
	 * @return The harvest schedule with the passed ID, or null if there was no harvest schedule with that ID.
	 */
	public abstract HarvestSchedule loadWithoutSteps(int harvestScheduleId);

	/**
	 * Gets a harvest schedule by it's name
	 *
	 * @param name The name of the harvest schedule to get
	 * @return The harvest schedule with the passed name, or null if there was no harvest schedule with that name.
	 */
	public abstract HarvestSchedule getByName(String name);

	/**
	 * Gets a harvest schedule by it's ID without getting extra information
	 *
	 * @param harvestScheduleId The ID of the harvest schedule to get
	 * @return The harvest schedule with the passed ID, or null if there was no harvest schedule with that ID.
	 */
	public abstract HarvestSchedule loadBasicHarvestSchedule(int harvestScheduleId);

	/**
	 * Gets all harvest schedules which harvest the provider with the passed ID
	 *
	 * @param providerId The ID of the provider whose schedules we should get
	 * @return A list all harvest schedules which harvest the provider with the passed ID
	 */
	public abstract HarvestSchedule getHarvestScheduleForProvider(int providerId);

	/**
	 * Gets all harvest schedules from the database which should be run at the time specified
	 * by the parameters
	 *
	 * @param hour The hour for which to get the harvest schedules to run
	 * @param dayOfWeek The day of the week for which to get the harvest schedules to run
	 * @param minute The minute for which to get the harvest schedules to run
	 * @return A list of all HarvestSchedules to be run at the time specified by the parameters
	 */
	public abstract List<HarvestSchedule> getSchedulesToRun(int hour, int dayOfWeek, int minute);

	/**
	 * Inserts a harvest schedule into the database
	 *
	 * @param harvestSchedule The harvest schedule to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest schedule was not valid for inserting
	 */
	public abstract boolean insert(HarvestSchedule harvestSchedule) throws DataException;

	/**
	 * Updates a harvestSchedule in the database
	 *
	 * @param harvestSchedule The harvest schedule to update
	 * @param updateSteps True to update the steps on the schedule, false to maintain them
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest schedule was not valid for updating
	 */
	public abstract boolean update(HarvestSchedule harvestSchedule, boolean updateSteps) throws DataException;

	/**
	 * Deletes a harvest schedule from the database
	 *
	 * @param harvestSchedule The harvest schedule to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest schedule was not valid for deleting
	 */
	public abstract boolean delete(HarvestSchedule harvestSchedule) throws DataException;

	/**
	 * Validates the fields on the passed HarvestSchedule Object
	 *
	 * @param schedule The harvest schedule to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed harvest schedule were invalid
	 */
	protected void validateFields(HarvestSchedule schedule, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(schedule.getId() < 0)
				errorMessage.append("The harvestScheduleId is invalid. ");
        } // end if(we need to check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(schedule.getMinute() > 60)
				errorMessage.append("The minute is invalid. " );

			if(schedule.getDayOfWeek() > 7)
				errorMessage.append("The day_of_week is invalid. ");

			if(schedule.getHour() > 24)
				errorMessage.append("The hour is invalid. ");

			if(schedule.getProvider() == null)
				errorMessage.append("The provider is invalid. ");

			if(schedule.getNotifyEmail() != null && schedule.getNotifyEmail().length() > 127)
				errorMessage.append("The notify_email is invalid. ");

			if(schedule.getScheduleName() != null && schedule.getScheduleName().length() > 127)
				errorMessage.append("The schedule_name is invalid. ");

			if(schedule.getRecurrence() != null && schedule.getRecurrence().length() > 127)
				errorMessage.append("The recurrence is invalid. ");
		} // end if(we need to check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(HarvestSchedule, boolean, boolean)
} // end class HarvestScheduleDAO
