/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.transformation.dao;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.services.transformation.bo.HeldHoldingRecord;

/**
 * Accesses held holding in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class HeldHoldingRecordDAO
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
	public final static String HELD_HOLDINGS_TABLE_NAME = "held_marcxml_holding";
	
	/**
	 * The name of the ID column
	 */
	public final static String COL_HELD_HOLDINGS_ID = "held_marcxml_holding_id";

	/**
	 * The name of the holding oai ID column
	 */
	public final static String COL_HOLDING_OAI_ID = "marcxml_holding_oai_id";

	/**
	 * The name of the holding 004 field column
	 */
	public final static String COL_HOLDING_004_FIELD = "marcxml_holding_004_field";


	/**
	 * Gets a held holding record by 004 field
	 *
	 * @param field004 The 004 field
	 * @return The held holding record with the passed field004, or null if there was no matching 004 field.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<HeldHoldingRecord> getByHolding004Field(String field004) throws DatabaseConfigException;

	/**
	 * Inserts a held holding record into the database
	 *
	 * @param heldHoldingRecord The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed HeldHoldingRecord was not valid for inserting
	 */
	public abstract boolean insert(HeldHoldingRecord heldHoldingRecord) throws DataException;

	/**
	 * Updates a HeldHoldingRecord in the database
	 *
	 * @param heldHoldingRecord The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed HeldHoldingRecord was not valid for updating
	 */
	public abstract boolean update(HeldHoldingRecord heldHoldingRecord) throws DataException;

	/**
	 * Deletes a HeldHoldingRecord from the database
	 *
	 * @param heldHoldingRecord The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed HeldHoldingRecord was not valid for deleting
	 */
	public abstract boolean delete(HeldHoldingRecord heldHoldingRecord) throws DataException;

	/**
	 * Validates the fields on the passed HeldHoldingRecord Object
	 *
	 * @param heldHoldingRecord The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(HeldHoldingRecord heldHoldingRecord, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(heldHoldingRecord.getHoldingRecordOAIID() == null || heldHoldingRecord.getHoldingRecordOAIID().length() <= 0 || heldHoldingRecord.getHoldingRecordOAIID().length() > 500)
				errorMessage.append("The Holding OAI id is invalid. ");

			if(heldHoldingRecord.getHolding004Field() == null || heldHoldingRecord.getHolding004Field().length() <= 0 || heldHoldingRecord.getHolding004Field().length() > 500)
				errorMessage.append("The ho004 field is invalid. ");

		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(HeldHoldingRecord, boolean, boolean)
} // end class HeldHoldingRecordDAO
