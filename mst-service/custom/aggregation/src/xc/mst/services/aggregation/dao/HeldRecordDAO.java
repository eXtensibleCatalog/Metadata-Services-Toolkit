/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.aggregation.dao;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.services.aggregation.bo.HeldRecord;

/**
 * Accesses held record in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class HeldRecordDAO
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
	public final static String HELD_RECORD_TABLE_NAME = "held_records";
	
	/**
	 * The name of the ID column
	 */
	public final static String HELD_RECORD_ID = "held_records_id";

	/**
	 * The name of the held oai ID column
	 */
	public final static String COL_OAI_ID = "held_oai_id";

	/**
	 * The name of the parent oai id column
	 */
	public final static String COL_PARENT_OAI_ID = "parent_oai_id";

	/**
	 * Gets list of held OAI identifiers that match the given parent OAI id
	 *
	 * @param parentOaiId The parent OAI Id
	 * @return list of held records that match the given OAI identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<HeldRecord> getByParentOaiId(String parentOaiId) throws DatabaseConfigException;
	
	/**
	 * Inserts held record into the database
	 *
	 * @param outputRecord The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for inserting
	 */
	public abstract boolean insert(HeldRecord outputRecord) throws DataException;

	/**
	 * Updates held record in the database
	 *
	 * @param outputRecord The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for updating
	 */
	public abstract boolean update(HeldRecord outputRecord) throws DataException;

	/**
	 * Deletes held record from the database
	 *
	 * @param outputRecord The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for deleting
	 */
	public abstract boolean delete(HeldRecord outputRecord) throws DataException;

	/**
	 * Deletes held record from the database for given OAI id
	 *
	 * @param oaiId The OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for deleting
	 */
	public abstract boolean deleteByOAIId(String oaiId) throws DataException;

	/**
	 * Validates the fields on the passed held record Object
	 *
	 * @param heldRecord The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(HeldRecord heldRecord, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(heldRecord.getOaiId() == null || heldRecord.getOaiId().length() <= 0 || heldRecord.getOaiId().length() > 500)
				errorMessage.append("The OAI id is invalid. ");

			
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(HeldRecord, boolean, boolean)
} 
