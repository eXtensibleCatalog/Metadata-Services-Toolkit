/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.log.Log;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data Access Object for the logs table
 *
 * @author Eric Osisek
 */
public abstract class LogDAO extends BaseDAO
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
	public final static String LOGS_TABLE_NAME = "logs";

	/**
	 * The name of the log ID column
	 */
	public final static String COL_LOG_ID = "log_id";

	/**
	 * The name of the warnings column
	 */
	public final static String COL_WARNINGS = "warnings";

	/**
	 * The name of the errors column
	 */
	public final static String COL_ERRORS = "errors";

	/**
	 * The name of the last log reset column
	 */
	public final static String COL_LAST_LOG_RESET = "last_log_reset";

	/**
	 * The name of the log file name column
	 */
	public final static String COL_LOG_FILE_NAME = "log_file_name";

	/**
	 * The name of the log file location column
	 */
	public final static String COL_LOG_FILE_LOCATION = "log_file_location";

	/**
	 * A set of all columns which are valid for sorting
	 */
	protected static Set<String> sortableColumns = new HashSet<String>();
	
	// Initialize the list of sortable columns
	static
	{
		sortableColumns.add(COL_LOG_ID);
		sortableColumns.add(COL_WARNINGS);
		sortableColumns.add(COL_ERRORS);
		sortableColumns.add(COL_LAST_LOG_RESET);
		sortableColumns.add(COL_LOG_FILE_NAME);
		sortableColumns.add(COL_LOG_FILE_LOCATION);
	} // end initialization of sortableColumns
	
	/**
	 * Gets all logs from the database
	 *
	 * @return A list of Log Objects representing all logs in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Log> getAll() throws DatabaseConfigException;

	/**
	 * Gets a sorted list of all the general logs in the database 
	 *
	 * @param asc True to sort in ascending order, false to sort in descending order
     * @param columnName The name of the column on which the rows should be sorted
	 * @return A sorted list containing all the general logs in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Log> getSorted(boolean asc, String columnName) throws DatabaseConfigException;
	
	/**
	 * Gets the log from the database with the passed log ID
	 *
	 * @param id The ID of the log to get
	 * @return A the log with the log ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Log getById(int id) throws DatabaseConfigException;

	/**
	 * Inserts a log into the database
	 *
	 * @param log The log to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed log was not valid for inserting
	 */
	public abstract boolean insert(Log log) throws DataException;

	/**
	 * Updates a log in the database
	 *
	 * @param log The log to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed log was not valid for updating
	 */
	public abstract boolean update(Log log) throws DataException;

	/**
	 * Deletes a log from the database
	 *
	 * @param log The log to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed log was not valid for deleting
	 */
	public abstract boolean delete(Log log) throws DataException;

	/**
	 * Validates the fields on the passed Log Object
	 *
	 * @param logObj The log to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed harvest were invalid
	 */
	protected void validateFields(Log logObj, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(logObj.getId() < 0)
				errorMessage.append("The log ID is invalid. ");
        } // end if(we need to check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(logObj.getWarnings() < 0)
				errorMessage.append("The warnings field is invalid. ");

			if(logObj.getErrors() < 0)
				errorMessage.append("The errors field is invalid. ");

			if(logObj.getLogFileName() == null || logObj.getLogFileName().length() > 255)
				errorMessage.append("The log file name is invalid. ");
			
			if(logObj.getLogFileLocation() == null || logObj.getLogFileLocation().length() > 512)
				errorMessage.append("The log file location is invalid. ");
		} // end if(we need to check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(Log, boolean, boolean)
} // end class LogDAO
