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

import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Accesses sets in the database
 *
 * @author Eric Osisek
 */
public abstract class SetDAO
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
	protected final static String SETS_TABLE_NAME = "sets";

	/**
	 * The name of the set ID column
	 */
	protected final static String COL_SET_ID = "set_id";

	/**
	 * The name of the display name column
	 */
	protected final static String COL_DISPLAY_NAME = "display_name";

	/**
	 * The name of the description column
	 */
	protected final static String COL_DESCRIPTION = "description";

	/**
	 * The name of the setSpec column
	 */
	protected final static String COL_SET_SPEC = "set_spec";

	/**
	 * The name of the provider set column
	 */
	protected final static String COL_PROVIDER_SET = "is_provider_set";

	/**
	 * The name of the record set column
	 */
	protected final static String COL_RECORD_SET = "is_record_set";

	/**
	 * The name of the record set column
	 */
	protected final static String COL_PROVIDER_ID = "provider_id";

	/**
	 * Gets all sets in the database
	 *
	 * @return A list containing all sets in the database
	 */
	public abstract List<Set> getAll();

	/**
	 * Gets a set by it's ID
	 *
	 * @param setId The ID of the set to get
	 * @return The set with the passed ID, or null if there was no set with that ID.
	 */
	public abstract Set getById(int setId);

	/**
	 * Gets a set by it's ID without getting extra information
	 *
	 * @param setId The ID of the set to get
	 * @return The set with the passed ID, or null if there was no set with that ID.
	 */
	public abstract Set loadBasicSet(int setId);

	/**
	 * Gets a set by it's setSpec
	 *
	 * @param setSpec The setSpec of the set to get
	 * @return The set with the passed setSpec, or null if there was no set with that setSpec.
	 */
	public abstract Set getBySetSpec(String setSpec);

	/**
	 * Gets all sets which belong to the provider with the passed ID
	 *
	 * @param providerId The ID of the provider whose sets we should get
	 * @return A list all sets which belong to the provider with the passed ID
	 */
	public abstract List<Set> getSetsForProvider(int providerId);

	/**
	 * Inserts a set into the database
	 *
	 * @param set The set to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed set was not valid for inserting
	 */
	public abstract boolean insert(Set set) throws DataException;

	/**
	 * Inserts a set into the database
	 *
	 * @param set The set to insert
	 * @param providerId The ID of the provider to which the set belongs
	 * @return True on success, false on failure
	 * @throws DataException if the passed set was not valid for inserting
	 */
	public abstract boolean insertForProvider(Set set, int providerId) throws DataException;

	/**
	 * Removes a set from a provider into the database
	 *
	 * @param set The set to insert
	 * @param providerId The ID of the provider to which the set no longer belongs
	 * @return True on success, false on failure
	 * @throws DataException if the passed set was not valid for inserting
	 */
	public abstract boolean addToProvider(Set set, int providerId) throws DataException;

	/**
	 * Inserts a set into the database
	 *
	 * @param set The set to insert
	 * @param providerId The ID of the provider to which the set belongs
	 * @return True on success, false on failure
	 * @throws DataException if the passed set was not valid for inserting
	 */
	public abstract boolean removeFromProvider(Set set, int providerId) throws DataException;


	/**
	 * Updates a set in the database
	 *
	 * @param set The set to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed set was not valid for updating
	 */
	public abstract boolean update(Set set) throws DataException;

	/**
	 * Deletes a set from the database
	 *
	 * @param set The set to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed set was not valid for deleting
	 */
	public abstract boolean delete(Set set) throws DataException;

	/**
	 * Validates the fields on the passed Set Object
	 *
	 * @param set The set to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed set were invalid
	 */
	protected void validateFields(Set set, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(set.getId() < 0)
				errorMessage.append("The setId is invalid. ");
        } // end if(we need to check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(set.getDisplayName() != null && set.getDisplayName().length() > 127)
				errorMessage.append("The display_name is invalid. ");

			if(set.getSetSpec() == null || set.getSetSpec().length() <= 0 || set.getSetSpec().length() > 127)
				errorMessage.append("The set_spec is invalid. ");

			if(set.getDescription() != null && set.getDescription().length() >= 255)
				errorMessage.append("The description is invalid. ");
		} // end if(we need to check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(Set, boolean, boolean)
} // end class SetDAO
