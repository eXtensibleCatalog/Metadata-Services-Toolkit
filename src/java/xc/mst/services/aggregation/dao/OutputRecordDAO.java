/**
  * Copyright (c) 2009 University of Rochester
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
import xc.mst.services.aggregation.bo.OutputRecord;

/**
 * Accesses output record in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class OutputRecordDAO
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
	public final static String OUTPUT_RECORD_TABLE_NAME = "output_record";
	
	/**
	 * The name of the ID column
	 */
	public final static String OUTPUT_RECORD_ID = "output_record_id";

	/**
	 * The name of the oai ID column
	 */
	public final static String COL_OAI_ID = "oai_id";

	/**
	 * The name of the xml column
	 */
	public final static String COL_XML = "xml";

	/**
	 * The name of the updated column
	 */
	public final static String COL_UPDATED = "updated";

	/**
	 * Gets list of OAI identifiers that match the given OAI id
	 *
	 * @param oaiId The OAI Id
	 * @return list of records that match the given OAI identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<OutputRecord> getByOaiId(String oaiId) throws DatabaseConfigException;
	
	/**
	 * Inserts output record into the database
	 *
	 * @param outputRecord The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed output record was not valid for inserting
	 */
	public abstract boolean insert(OutputRecord outputRecord) throws DataException;

	/**
	 * Updates output record in the database
	 *
	 * @param outputRecord The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed output record was not valid for updating
	 */
	public abstract boolean update(OutputRecord outputRecord) throws DataException;

	/**
	 * Deletes output record from the database for given OAI id
	 *
	 * @param oaiId The OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed output record was not valid for deleting
	 */
	public abstract boolean deleteByOAIId(String oaiId) throws DataException;

	/**
	 * Gets the successor record for the given OAI id
	 *
	 * @param oaiId The OAI Id
	 * @return list of successor OAI ids that match the given OAI identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<String> getSuccessorByOaiId(String oaiId) throws DatabaseConfigException;
	
	/**
	 * Validates the fields on the passed output record Object
	 *
	 * @param outputRecord The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(OutputRecord outputRecord, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(outputRecord.getOaiId() == null || outputRecord.getOaiId().length() <= 0 || outputRecord.getOaiId().length() > 500)
				errorMessage.append("The OAI id is invalid. ");

			
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(OutputRecord, boolean, boolean)
} 
