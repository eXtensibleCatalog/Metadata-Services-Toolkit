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
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.Harvest;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data Access Object for the harvests table
 *
 * @author Eric Osisek
 */
public abstract class HarvestDAO
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
	public final static String HARVESTS_TABLE_NAME = "harvests";

	/**
	 * The name of the harvest ID column
	 */
	public final static String COL_HARVEST_ID = "harvest_id";

	/**
	 * The name of the start time column
	 */
	public final static String COL_START_TIME = "start_time";

	/**
	 * The name of the end time column
	 */
	public final static String COL_END_TIME = "end_time";

	/**
	 * The name of the request column
	 */
	public final static String COL_REQUEST = "request";

	/**
	 * The name of the result column
	 */
	public final static String COL_RESULT = "result";

	/**
	 * The name of the harvest schedule name column
	 */
	public final static String COL_HARVEST_SCHEDULE_NAME = "harvest_schedule_name";

	/**
	 * Gets all harvest s in the database
	 *
	 * @return A list containing all harvest s in the database
	 */
	public abstract List<Harvest> getAll();

	/**
	 * Gets a harvest by it's ID
	 *
	 * @param harvestId The ID of the harvest to get
	 * @return The harvest with the passed ID, or null if there was no harvest with that ID.
	 */
	public abstract Harvest getById(int harvestId);

	/**
	 * Gets a harvest by it's ID without getting extra information
	 *
	 * @param harvestId The ID of the harvest to get
	 * @return The harvest  with the passed ID, or null if there was no harvest with that ID.
	 */
	public abstract Harvest loadBasicHarvest(int harvestId);

	/**
	 * Gets all harvests which were run by a harvest schedule
	 *
	 * @param harvestScheduleId The ID of the harvest schedule whose harvests we should get
	 * @return A list all harvests which were run by the harvest schedule with the passed ID
	 */
	public abstract List<Harvest> getHarvestsForSchedule(String harvestScheduleName);

	/**
	 * Inserts a harvest into the database
	 *
	 * @param harvest The harvest to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest was not valid for inserting
	 */
	public abstract boolean insert(Harvest harvest) throws DataException;

	/**
	 * Updates a harvest in the database
	 *
	 * @param harvest The harvest to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest was not valid for updating
	 */
	public abstract boolean update(Harvest harvest) throws DataException;

	/**
	 * Deletes a harvest from the database
	 *
	 * @param harvest The harvest to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest was not valid for deleting
	 */
	public abstract boolean delete(Harvest harvest) throws DataException;

	/**
	 * Validates the fields on the passed Harvest Object
	 *
	 * @param harvest The harvest to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed harvest were invalid
	 */
	protected void validateFields(Harvest harvest, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(harvest.getId() < 0)
				errorMessage.append("The harvest ID is invalid. ");
        } // end if(we need to check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(harvest.getStartTime() == null)
				errorMessage.append("The start time field is invalid. ");

			if(harvest.getHarvestScheduleName() == null || harvest.getHarvestScheduleName().length() == 0 || harvest.getHarvestScheduleName().length() > 127)
				errorMessage.append("The harvest schedule name is invalid. ");
		} // end if(we need to check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(Harvest, boolean, boolean)
} // end class HarvestDAO
