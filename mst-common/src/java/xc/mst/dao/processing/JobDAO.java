/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.processing;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.Job;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data access object for the job table
 *
 * @author Sharmila Ranganathan
 */
public abstract class JobDAO extends BaseDAO
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
	public final static String JOBS_TABLE_NAME = "jobs";

	/**
	 * The name of the job ID column
	 */
	public final static String COL_JOB_ID = "job_id";

	/**
	 * The name of the service column
	 */
	public final static String COL_SERVICE_ID = "service_id";

	/**
	 * The name of the harvest schedule ID column
	 */
	public final static String COL_HARVEST_SCHEDULE_ID = "harvest_schedule_id";

	/**
	 * The name of the processing directive ID column
	 */
	public final static String COL_PROCESSING_DIRECTIVE_ID = "processing_directive_id";

	/**
	 * The name of the output set ID column
	 */
	public final static String COL_OUTPUT_SET_ID = "output_set_id";

	/**
	 * The name of the order column
	 */
	public final static String COL_ORDER = "job_order";
	
	/**
	 * The name of the type column
	 */
	public final static String COL_TYPE = "job_type";


	/**
	 * Gets all jobs from the database
	 *
	 * @return A list containing all jobs in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Job> getAll() throws DatabaseConfigException;
	
	/**
	 * Gets the job from the database with the passed job ID.
	 *
	 * @param jobId The ID of the job to get
	 * @return The job with the passed job ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Job getById(int jobId) throws DatabaseConfigException;

	/**
	 * Gets the job from the database with the passed job ID.
	 * This method does not get the input sets or formats.
	 *
	 * @param jobId The ID of the job to get
	 * @return The job with the passed job ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Job loadBasicJob(int jobId) throws DatabaseConfigException;

	/**
	 * Gets the jobs from the database with the passed service ID
	 *
	 * @param serviceId The service ID of the jobs to get
	 * @return A list containing the jobs with the passed service ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Job> getByServiceId(int serviceId) throws DatabaseConfigException;

	/**
	 * Gets the jobs from the database with the passed harvest schedule ID.
	 *
	 * @param harvestScheduleId The harvest schedule ID of the jobs to get
	 * @return A list containing the jobs with the passed harvest schedule ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Job> getByHarvestScheduleId(int harvestScheduleId) throws DatabaseConfigException;

	/**
	 * Inserts a job into the database.
	 *
	 * @param job The job to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed job was not valid for inserting
	 */
	public abstract boolean insert(Job job) throws DataException;

	/**
	 * Updates a job in the database.
	 *
	 * @param job The job to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed job was not valid for updating
	 */
	public abstract boolean update(Job job) throws DataException;

	/**
	 * Deletes a job from the database.
	 *
	 * @param job The job to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed job was not valid for deleting
	 */
	public abstract boolean delete(Job job) throws DataException;
	
	/**
	 * Get the maximum order
	 *
	 * @return max order
	 * @throws DatabaseConfigException if unable to contact database
	 */
	public abstract int getMaxOrder() throws DatabaseConfigException;
	
	/**
	 * Get the next job in queue
	 *
	 * @return next job to execute
	 * @throws DatabaseConfigException if unable to contact database
	 */
	public abstract Job getNextJobToExecute() throws DatabaseConfigException;

	/**
	 * Validates the fields on the passed Job Object.
	 *
	 * @param schedule The job to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed job were invalid
	 */
	protected void validateFields(Job job, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(job.getId() < 0)
				errorMessage.append("The job ID is invalid. ");
		} // end if(we should validate the ID)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(job.getService() == null && job.getHarvestSchedule() == null && job.getProcessingDirective() == null)
				errorMessage.append("Either the service or harvest schedule or processing directive must be defined.");

			if(job.getOrder() <= 0)
				errorMessage.append("The order is invalid. ");
		} // end if(we should check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(Job, boolean, boolean)
} // end class JobDAO
