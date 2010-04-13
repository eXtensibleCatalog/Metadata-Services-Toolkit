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
import xc.mst.services.transformation.bo.XCHoldingRecord;

/**
 * Accesses holdings record in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class XCHoldingDAO
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
	public final static String XC_HOLDINGS_TABLE_NAME = "holding_manifestation";
	
	/**
	 * The name of the ID column
	 */
	public final static String COL_XC_HOLDINGS_ID = "holding_manifestation_id";

	/**
	 * The name of the holding oai ID column
	 */
	public final static String COL_HOLDING_OAI_ID = "xc_holding_oai_id";

	/**
	 * The name of the holding 004 field column
	 */
	public final static String COL_HOLDING_004_FIELD = "marcxml_holding_004_field";

	/**
	 * The name of the manifestation oai id column
	 */
	public final static String COL_MANIFESTATION_OAI_ID = "manifestation_oai_id";

	/**
	 * Gets a held holding record by 004 field
	 *
	 * @param field004 The 004 field
	 * @return The held holding record with the passed field004, or null if there was no matching 004 field.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<XCHoldingRecord> getByHolding004Field(String field004) throws DatabaseConfigException;
	
	/**
	 * Gets list of holding OAI Ids by manifestation OAI Id
	 *
	 * @param manifestationOAIId  manifestation OAI Id
	 * @return The list of holding manifestation mapping
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<XCHoldingRecord> getByManifestationOAIId(String manifestationOAIId) throws DatabaseConfigException;

	/**
	 * Gets list of holding manifestation mapping by holding OAI Id
	 *
	 * @param holdingOAIId  holding OAI Id
	 * @return The list of holding manifestation mapping
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<XCHoldingRecord> getByHoldingOAIId(String holdingOaiId) throws DatabaseConfigException;
	
	/**
	 * Inserts a held holding record into the database
	 *
	 * @param holdingRecord The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed HoldingRecord was not valid for inserting
	 */
	public abstract boolean insert(XCHoldingRecord holdingRecord) throws DataException;

	/**
	 * Updates a HoldingRecord in the database
	 *
	 * @param holdingRecord The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed HoldingRecord was not valid for updating
	 */
	public abstract boolean update(XCHoldingRecord holdingRecord) throws DataException;

	/**
	 * Deletes a HoldingRecord from the database
	 *
	 * @param holdingRecord The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed HoldingRecord was not valid for deleting
	 */
	public abstract boolean delete(XCHoldingRecord holdingRecord) throws DataException;

	/**
	 * Deletes a HoldingRecord from the database for given holding OAI id
	 *
	 * @param holdingOAIId The holding OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed HoldingRecord was not valid for deleting
	 */
	public abstract boolean deleteByHoldingOAIId(String holdingOAIId) throws DataException;

	/**
	 * Validates the fields on the passed HoldingRecord Object
	 *
	 * @param holdingRecord The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(XCHoldingRecord holdingRecord, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(holdingRecord.getHoldingRecordOAIID() == null || holdingRecord.getHoldingRecordOAIID().length() <= 0 || holdingRecord.getHoldingRecordOAIID().length() > 500)
				errorMessage.append("The Holding OAI id is invalid. ");

			if(holdingRecord.getHolding004Field() == null || holdingRecord.getHolding004Field().length() <= 0 || holdingRecord.getHolding004Field().length() > 500)
				errorMessage.append("The 004 field is invalid. ");
			
			if(holdingRecord.getManifestationOAIId() == null || holdingRecord.getManifestationOAIId().length() <= 0 || holdingRecord.getManifestationOAIId().length() > 500)
				errorMessage.append("The manifestation oai id invalid. ");

		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(HoldingRecord, boolean, boolean)
} // end class HeldHoldingRecordDAO
