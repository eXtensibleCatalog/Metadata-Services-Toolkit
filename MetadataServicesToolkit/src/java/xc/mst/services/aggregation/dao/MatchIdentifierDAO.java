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
import xc.mst.services.aggregation.bo.MatchIdentifiers;

/**
 * Accesses match identifiers for a record in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class MatchIdentifierDAO
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
	public final static String IN_PROCESSED_IDENTIFIERS_TABLE_NAME = "in_processed_identifiers";
	
	/**
	 * The name of the ID column
	 */
	public final static String IN_PROCESSED_IDENTIFIERS_ID = "in_processed_identifiers_id";

	/**
	 * The name of the oai ID column
	 */
	public final static String COL_OAI_ID = "oai_id";

	/**
	 * The name of the OCLC value column
	 */
	public final static String COL_OCLC_VALUE = "oclc_value";

	/**
	 * The name of the LCCN value column
	 */
	public final static String COL_LCCN_VALUE = "lccn_value";

	/**
	 * The name of the ISBN value column
	 */
	public final static String COL_ISBN_VALUE = "isbn_value";

	/**
	 * The name of the ISSN value column
	 */
	public final static String COL_ISSN_VALUE = "issn_value";

	/**
	 * Gets list of OAI identifiers that match the given OCLC value
	 *
	 * @param oclcValue The oclc Value
	 * @return list of OAI identifiers that match the given OCLC value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<String> getByOCLCValue(String oclcValue) throws DatabaseConfigException;
	
	/**
	 * Gets list of OAI identifiers that match the given LCCN value
	 *
	 * @param lccnValue The LCCN Value
	 * @return list of OAI identifiers that match the given LCCN value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<String> getByLCCNValue(String lccnValue) throws DatabaseConfigException;
	
	/**
	 * Gets list of OAI identifiers that match the given ISBN value
	 *
	 * @param isbnValue The ISBN Value
	 * @return list of OAI identifiers that match the given ISBN value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<String> getByISBNValue(String isbnValue) throws DatabaseConfigException;
	
	/**
	 * Get the match identifiers by given Oai id
	 *
	 * @param oaiId The OAI id
	 * @return Match identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract MatchIdentifiers getByOaiId(String oaiId) throws DatabaseConfigException;
	
	/**
	 * Gets list of OAI identifiers that match the given ISSN value
	 *
	 * @param issnValue The issn Value
	 * @return list of OAI identifiers that match the given ISSN value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract  List<String> getByISSNValue(String issnValue) throws DatabaseConfigException;
	
	/**
	 * Inserts match identifiers for a record into the database
	 *
	 * @param matchIdentifiers The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for inserting
	 */
	public abstract boolean insert(MatchIdentifiers matchIdentifiers) throws DataException;

	/**
	 * Updates match identifiers for a record in the database
	 *
	 * @param matchIdentifiers The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for updating
	 */
	public abstract boolean update(MatchIdentifiers matchIdentifiers) throws DataException;

	/**
	 * Deletes match identifiers for a record from the database
	 *
	 * @param matchIdentifiers The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for deleting
	 */
	public abstract boolean delete(MatchIdentifiers matchIdentifiers) throws DataException;

	/**
	 * Deletes a match identifiers for a record from the database for given OAI id
	 *
	 * @param oaiId The OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for deleting
	 */
	public abstract boolean deleteByOAIId(String oaiId) throws DataException;

	/**
	 * Validates the fields on the passed identifers Object
	 *
	 * @param matchIdentifiers The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(MatchIdentifiers matchIdentifiers, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(matchIdentifiers.getOaiId() == null || matchIdentifiers.getOaiId().length() <= 0 || matchIdentifiers.getOaiId().length() > 500)
				errorMessage.append("The OAI id is invalid. ");

			
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(MatchIdentifiers, boolean, boolean)
} // end class MatchIdentifierDAO
