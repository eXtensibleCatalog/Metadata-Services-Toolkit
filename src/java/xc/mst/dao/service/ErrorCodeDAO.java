/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  */

package xc.mst.dao.service;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.service.ErrorCode;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

public abstract class ErrorCodeDAO
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
	public final static String ERROR_CODES_TABLE_NAME = "error_codes";

	/**
	 * The name of the error code ID column
	 */
	public final static String COL_ERROR_CODE_ID = "error_code_id";

	/**
	 * The name of the error code column
	 */
	public final static String COL_ERROR_CODE = "error_code";

	/**
	 * The name of the error description file column
	 */
	public final static String COL_ERROR_DESCRIPTION_FILE = "error_description_file";

	/**
	 * The name of the service ID column
	 */
	public final static String COL_SERVICE_ID = "service_id";
	
	/**
	 * Gets all error codes from the database
	 *
	 * @return A list containing all error codes in the database
	 */
	public abstract List<ErrorCode> getAll();
	
	/**
	 * Gets the error code from the database with the passed error code ID
	 *
	 * @param id The ID of the error code to get
	 * @return The error code with the passed error code ID
	 */
	public abstract ErrorCode getById(int id);

	/**
	 * Gets the error code from the database with the passed error code ID.
	 * This method does not set the service on the error code.
	 *
	 * @param id The ID of the error code to get
	 * @return The error code with the passed ID
	 */
	public abstract ErrorCode loadBasicErrorCode(int id);

	/**
	 * Gets the error code from the database with the passed error code and Service
	 *
	 * @param errorCode The error code of the error code to get
	 * @param service The service of the error code to get
	 * @return The error code with the passed error code
	 */
	public abstract ErrorCode getByErrorCodeAndService(String errorCode, Service service);

	/**
	 * Inserts an error code into the database
	 *
	 * @param errorCode The error code to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed error code was not valid for inserting
	 */
	public abstract boolean insert(ErrorCode errorCode) throws DataException;

	/**
	 * Updates an error code in the database
	 *
	 * @param errorCode The error code to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed error code was not valid for inserting
	 */
	public abstract boolean update(ErrorCode errorCode) throws DataException;

	/**
	 * Deletes an error code from the database
	 *
	 * @param errorCode The error code to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed error code was not valid for inserting
	 */
	public abstract boolean delete(ErrorCode errorCode) throws DataException;

	/**
	 * Validates the fields on the passed ErrorCode Object
	 *
	 * @param errorCode The error code to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed error code were invalid
	 */
	protected void validateFields(ErrorCode errorCode, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(errorCode.getId() < 0)
				errorMessage.append("The error code id is invalid. ");
		} // end if(we should validate the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(errorCode.getErrorCode() == null || errorCode.getErrorCode().length() <= 0 || errorCode.getErrorCode().length() > 63)
				errorMessage.append("The error code is invalid. ");

			if(errorCode.getErrorDescriptionFile() == null || errorCode.getErrorDescriptionFile().length() <= 0 || errorCode.getErrorDescriptionFile().length() > 511)
				errorMessage.append("The error description file is invalid. ");

			if(errorCode.getService() == null || errorCode.getService().getId() <= 0)
				errorMessage.append("The service is invalid. ");
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(we found an error)
	} // end method validateFields(ErrorCode, boolean, boolean)
} // end class ServiceDAO
