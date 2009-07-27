/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.harvest;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data Access Object for the harvest schedule steps table
 *
 * @author Eric Osisek
 */
public abstract class HarvestScheduleStepDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * The Object managing the database connection
	 */
	protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

	/**
	 * The name of the database table we're interacting with
	 */
	public final static String HARVEST_SCHEDULE_STEP_TABLE_NAME = "harvest_schedule_steps";

	/**
	 * The name of the harvest schedule step ID column
	 */
	public final static String COL_HARVEST_SCHEDULE_STEP_ID = "harvest_schedule_step_id";

	/**
	 * The name of the harvest schedule ID column
	 */
	public final static String COL_HARVEST_SCHEDULE_ID = "harvest_schedule_id";

	/**
	 * The name of the format id column
	 */
	public final static String COL_FORMAT_ID = "format_id";

	/**
	 * The name of the set id column
	 */
	public final static String COL_SET_ID = "set_id";

	/**
	 * The name of the last ran column
	 */
	public final static String COL_LAST_RAN = "last_ran";

	/**
	 * Gets all harvest schedule steps in the database
	 *
	 * @return A list containing all harvest schedule steps in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<HarvestScheduleStep> getAll() throws DatabaseConfigException;

	/**
	 * Gets a harvest schedule step by it's ID
	 *
	 * @param harvestScheduleStepId The ID of the harvest schedule step to get
	 * @return The harvest schedule step with the passed ID, or null if there
	 *         was no harvest schedule step with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract HarvestScheduleStep getById(int harvestScheduleStepId) throws DatabaseConfigException;

	/**
	 * Gets all harvest schedule steps which belong to the harvest schedule with the passed ID
	 *
	 * @param harvestScheduleId The ID of the harvest schedule whose steps we should get
	 * @return A list all harvest schedule steps which belong to the harvest schedule with the passed ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<HarvestScheduleStep> getStepsForSchedule(int harvestSchedlueId) throws DatabaseConfigException;

	/**
	 * Inserts a harvest schedule step into the database
	 *
	 * @param harvestScheduleStep The harvest schedule step to insert
	 * @param harvestScheduleId The ID of the harvest schedule to insert the step for
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest schedule step was not valid for inserting
	 */
	public abstract boolean insert(HarvestScheduleStep harvestScheduleStep, int harvestScheduleId) throws DataException;

	/**
	 * Updates a harvestScheduleStep in the database
	 *
	 * @param harvestScheduleStep The harvest schedule step to update
	 * @param harvestScheduleId The ID of the harvest schedule to insert the step for
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest schedule step was not valid for updating
	 */
	public abstract boolean update(HarvestScheduleStep harvestScheduleStep, int harvestScheduleId) throws DataException;

	/**
	 * Deletes a harvest schedule step from the database
	 *
	 * @param harvestScheduleStep The harvest schedule step to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed harvest schedule step was not valid for deleting
	 */
	public abstract boolean delete(HarvestScheduleStep harvestScheduleStep) throws DataException;

	/**
	 * Deletes all harvest schedule steps for a given schedule from the database
	 *
	 * @param harvestScheduleId The ID of the harvest schedule whose steps should be deleted
	 * @return True on success, false on failure
	 */
	public abstract boolean deleteStepsForSchedule(int harvestScheduleId);

	/**
	 * Validates the fields on the passed HarvestScheduleStep Object
	 *
	 * @param step The harvest schedule step to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed harvest schedule step were invalid
	 */
	protected void validateFields(HarvestScheduleStep step, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(step.getId() < 0)
				errorMessage.append("The harvestScheduleStepId is invalid. ");

        } // end if(we need to check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(step.getFormat() == null)
				errorMessage.append("The format is invalid.");
		} // end if(we need to check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(HarvestScheduleStep, boolean, boolean
} // end class HarvestScheduleStepDAO
