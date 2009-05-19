/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.provider;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Accesses formats in the database
 *
 * @author Eric Osisek
 */
public abstract class FormatDAO
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
	public final static String FORMATS_TABLE_NAME = "formats";

	/**
	 * The name of the format ID column
	 */
	public final static String COL_FORMAT_ID = "format_id";

	/**
	 * The name of the name column
	 */
	public final static String COL_NAME = "name";

	/**
	 * The name of the namespace column
	 */
	public final static String COL_NAMESPACE = "namespace";

	/**
	 * The name of the schema_location column
	 */
	public final static String COL_SCHEMA_LOCATION = "schema_location";

	/**
	 * Gets all formats in the database
	 *
	 * @return A list containing all formats in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Format> getAll() throws DatabaseConfigException;

	/**
	 * Gets a format by it's ID
	 *
	 * @param formatId The ID of the format to get
	 * @return The format with the passed ID, or null if there was no format with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Format getById(int formatId) throws DatabaseConfigException;

	/**
	 * Gets a format by it's name
	 *
	 * @param name The name of the format to get
	 * @return The format with the passed name, or null if there was no format with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Format getByName(String name) throws DatabaseConfigException;

	/**
	 * Gets all formats in the database which the passed provider supports
	 *
	 * @return A list containing all formats in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Format> getFormatsForProvider(int providerId) throws DatabaseConfigException;

	/**
	 * Inserts a format into the database
	 *
	 * @param format The format to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed format was not valid for inserting
	 */
	public abstract boolean insert(Format format) throws DataException;

	/**
	 * Updates a format in the database
	 *
	 * @param format The format to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed format was not valid for updating
	 */
	public abstract boolean update(Format format) throws DataException;

	/**
	 * Deletes a format from the database
	 *
	 * @param format The format to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed format was not valid for deleting
	 */
	public abstract boolean delete(Format format) throws DataException;

	/**
	 * Validates the fields on the passed Format Object
	 *
	 * @param format The format to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(Format format, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(format.getId() < 0)
				errorMessage.append("The format_id is invalid. ");
		} // end if(we should validate the ID

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(format.getName() == null || format.getName().length() <= 0 || format.getName().length() > 63)
				errorMessage.append("The name is invalid. ");

			if(format.getNamespace() == null || format.getNamespace().length() <= 0 || format.getNamespace().length() > 255)
				errorMessage.append("The namespace is invalid. ");

			if(format.getSchemaLocation() == null || format.getSchemaLocation().length() <= 0 || format.getSchemaLocation().length() > 255)
				errorMessage.append("The schema_location is invalid. ");
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(Format, boolean, boolean)
} // end class FormatDAO
