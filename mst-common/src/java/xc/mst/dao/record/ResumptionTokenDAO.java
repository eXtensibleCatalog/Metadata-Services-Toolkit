/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.record;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.record.ResumptionToken;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data Access Object for the resumption tokens table
 *
 * @author Eric Osisek
 */
public abstract class ResumptionTokenDAO extends BaseDAO
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
	protected final static String RESUMPTION_TOKENS_TABLE_NAME = "resumption_tokens";

	/**
	 * The name of the resumption token ID column
	 */
	protected final static String COL_RESUMPTION_TOKEN_ID = "resumption_token_id";

	/**
	 * The name of the set spec column
	 */
	protected final static String COL_SET_SPEC = "set_spec";

	/**
	 * The name of the metadata format column
	 */
	protected final static String COL_METADATA_FORMAT = "metadata_format";

	/**
	 * The name of the starting_from column
	 */
	protected final static String COL_FROM = "starting_from";

	/**
	 * The name of the until column
	 */
	protected final static String COL_UNTIL = "until";

	/**
	 * The name of the offset column
	 */
	protected final static String COL_OFFSET = "offset";
	
	/**
	 * The name of the token column
	 */
	protected final static String COL_TOKEN = "token";

	/**
	 * Gets all resumption tokens in the database
	 *
	 * @return A list of all resumption tokens in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<ResumptionToken> getAll() throws DatabaseConfigException;

	/**
	 * Gets the resumption token from the database with the passed ID
	 *
	 * @param id The ID of the resumption token to get
	 * @return The resumption token with the passed ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract ResumptionToken getById(long id) throws DatabaseConfigException;
	

	/**
	 * Gets the resumption token from the database with the passed token
	 *
	 * @param token The token of the resumption token to get
	 * @return The resumption token with the passed ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract ResumptionToken getByToken(String token) throws DatabaseConfigException;

	/**
	 * Inserts a resumption token from the database
	 *
	 * @param resumptionToken The resumption token to insert
	 * @return True if the resumption token was inserted successfully, false otherwise
	 * @throws DataException If the fields on the passed resumption token were not valid for inserting
	 */
	public abstract boolean insert(ResumptionToken resumptionToken) throws DataException;

	/**
	 * Updates a resumption token in the database
	 *
	 * @param resumptionToken The resumption token to update
	 * @return True if the resumption token was updated successfully, false otherwise
	 * @throws DataException If the fields on the passed resumption token were not valid for updating
	 */
	public abstract boolean update(ResumptionToken resumptionToken) throws DataException;

	/**
	 * Deletes a resumption token from the database
	 *
	 * @param resumptionToken The resumption token to delete
	 * @return True if the resumption token was deleted successfully, false otherwise
	 * @throws DataException If the fields on the passed resumption token were not valid for deleting
	 */
	public abstract boolean delete(ResumptionToken resumptionToken) throws DataException;

	/**
	 * Validates the fields on the passed Resumption Token Object
	 *
	 * @param resumptionToken The resumption token to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed resumption token were invalid
	 */
	protected void validateFields(ResumptionToken resumptionToken, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(resumptionToken.getId() < 0)
				errorMessage.append("The resumption token ID is invalid. ");
        } // end if(we need to check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(resumptionToken.getSetSpec() != null && resumptionToken.getSetSpec().length() > 255)
				errorMessage.append("The setSpec is invalid. ");

			if(resumptionToken.getMetadataFormat() == null || resumptionToken.getMetadataFormat().length() <= 0 ||  resumptionToken.getMetadataFormat().length() > 511)
				errorMessage.append("The metadata format field is invalid. ");

			if(resumptionToken.getOffset() < 0)
				errorMessage.append("The offset is invalid. ");
		} // end if(we need to check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(ResumptionToken, boolean, boolean)
} // end class ResumptionTokenDAO
