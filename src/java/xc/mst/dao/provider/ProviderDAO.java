/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.provider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.manager.IndexException;

/**
 * Accesses providers in the database
 *
 * @author Eric Osisek
 */
public abstract class ProviderDAO extends BaseDAO
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
	public final static String PROVIDERS_TABLE_NAME = "providers";

	/**
	 * The name of the provider ID column
	 */
	public final static String COL_PROVIDER_ID = "provider_id";

	/**
	 * The name of the created at column
	 */
	public final static String COL_CREATED_AT = "created_at";

	/**
	 * The name of the updated at column
	 */
	public final static String COL_UPDATED_AT = "updated_at";

	/**
	 * The name of the name column
	 */
	public final static String COL_NAME = "name";

	/**
	 * The name of the OAI provider URL column
	 */
	public final static String COL_OAI_PROVIDER_URL = "oai_provider_url";

	/**
	 * The name of the provider ID column
	 */
	public final static String COL_USER_ID = "user_id";

	/**
	 * The name of the title column
	 */
	public final static String COL_TITLE = "title";

	/**
	 * The name of the creator column
	 */
	public final static String COL_CREATOR = "creator";

	/**
	 * The name of the subject column
	 */
	public final static String COL_SUBJECT = "subject";

	/**
	 * The name of the description column
	 */
	public final static String COL_DESCRIPTION = "description";

	/**
	 * The name of the publisher column
	 */
	public final static String COL_PUBLISHER = "publisher";

	/**
	 * The name of the contributors column
	 */
	public final static String COL_CONTRIBUTORS = "contributors";

	/**
	 * The name of the date column
	 */
	public final static String COL_DATE = "date";

	/**
	 * The name of the type column
	 */
	public final static String COL_TYPE = "type";

	/**
	 * The name of the format column
	 */
	public final static String COL_FORMAT = "format";

	/**
	 * The name of the identifier column
	 */
	public final static String COL_IDENTIFIER = "identifier";

	/**
	 * The name of the language column
	 */
	public final static String COL_LANGUAGE = "language";

	/**
	 * The name of the relation column
	 */
	public final static String COL_RELATION = "relation";

	/**
	 * The name of the coverage column
	 */
	public final static String COL_COVERAGE = "coverage";

	/**
	 * The name of the rights column
	 */
	public final static String COL_RIGHTS = "rights";

	/**
	 * The name of the service column
	 */
	public final static String COL_SERVICE = "service";

	/**
	 * The name of the next list sets list formats column
	 */
	public final static String COL_NEXT_LIST_SETS_LIST_FORMATS = "next_list_sets_list_formats";

	/**
	 * The name of the protocol version column
	 */
	public final static String COL_PROTOCOL_VERSION = "protocol_version";

	/**
	 * The name of the last validation date column
	 */
	public final static String COL_LAST_VALIDATION_DATE = "last_validated";

	/**
	 * The name of the identify column
	 */
	public final static String COL_IDENTIFY = "identify";

	/**
	 * The name of the service column
	 */
	public final static String COL_LISTFORMATS = "listformats";

	/**
	 * The name of the service column
	 */
	public final static String COL_LISTSETS = "listsets";

	/**
	 * The name of the warnings column
	 */
	public final static String COL_WARNINGS = "warnings";

	/**
	 * The name of the errors column
	 */
	public final static String COL_ERRORS = "errors";

	/**
	 * The name of the records added column
	 */
	public final static String COL_RECORDS_ADDED = "records_added";

	/**
	 * The name of the records replaced column
	 */
	public final static String COL_RECORDS_REPLACED = "records_replaced";

	/**
	 * The name of the last oai request column
	 */
	public final static String COL_LAST_OAI_REQUEST = "last_oai_request";

	/**
	 * The name of the last harvest end time column
	 */
	public final static String COL_LAST_HARVEST_END_TIME = "last_harvest_end_time";

	/**
	 * The name of the last log reset column
	 */
	public final static String COL_LAST_LOG_RESET = "last_log_reset";

	/**
	 * The name of the log file name column
	 */
	public final static String COL_LOG_FILE_NAME = "log_file_name";

	/**
	 * A set of all columns which are valid for sorting
	 */
	protected static Set<String> sortableColumns = new HashSet<String>();
	
	// Initialize the list of sortable columns
	static
	{	
		sortableColumns.add(COL_PROVIDER_ID);
		sortableColumns.add(COL_CREATED_AT);
		sortableColumns.add(COL_UPDATED_AT);
		sortableColumns.add(COL_NAME);
		sortableColumns.add(COL_OAI_PROVIDER_URL);
		sortableColumns.add(COL_USER_ID);
		sortableColumns.add(COL_TITLE);
		sortableColumns.add(COL_CREATOR);
		sortableColumns.add(COL_SUBJECT);
		sortableColumns.add(COL_DESCRIPTION);
		sortableColumns.add(COL_PUBLISHER);
		sortableColumns.add(COL_CONTRIBUTORS);
		sortableColumns.add(COL_DATE);
		sortableColumns.add(COL_TYPE);
		sortableColumns.add(COL_FORMAT);
		sortableColumns.add(COL_IDENTIFIER);
		sortableColumns.add(COL_LANGUAGE);
		sortableColumns.add(COL_RELATION);
		sortableColumns.add(COL_COVERAGE);
		sortableColumns.add(COL_RIGHTS);
		sortableColumns.add(COL_SERVICE);
		sortableColumns.add(COL_NEXT_LIST_SETS_LIST_FORMATS);
		sortableColumns.add(COL_PROTOCOL_VERSION);
		sortableColumns.add(COL_LAST_VALIDATION_DATE);
		sortableColumns.add(COL_IDENTIFY);
		sortableColumns.add(COL_LISTFORMATS);
		sortableColumns.add(COL_LISTSETS);
		sortableColumns.add(COL_WARNINGS);
		sortableColumns.add(COL_ERRORS);
		sortableColumns.add(COL_RECORDS_ADDED);
		sortableColumns.add(COL_RECORDS_REPLACED);
		sortableColumns.add(COL_LAST_OAI_REQUEST);
		sortableColumns.add(COL_LAST_HARVEST_END_TIME);
		sortableColumns.add(COL_LAST_LOG_RESET);
		sortableColumns.add(COL_LOG_FILE_NAME);
	} // end initialization of sortableColumns

	/**
	 * Gets all providers in the database
	 *
	 * @return A list containing all providers in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Provider> getAll() throws DatabaseConfigException;

	/**
	 * Gets all providers in the database sorted by their names
	 *
	 * @param asc True to sort in ascending order, false to sort in descending order
     * @param columnName determines the name of the column on which the rows should be sorted
	 * @return A list containing all providers in the database sorted by their names
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<Provider> getSorted(boolean asc,String columnName) throws DatabaseConfigException;

	/**
	 * Gets a provider by it's ID
	 *
	 * @param providerId The ID of the provider to get
	 * @return The provider with the passed ID, or null if there was no provider with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Provider getById(int providerId) throws DatabaseConfigException;

	/**
     * Gets a provider by it's name
     *
     * @param name The name of the provider to get
     * @return The provider with the passed name, or null if there is no provider associated with that name.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
     */
    public abstract Provider getByName(String name) throws DatabaseConfigException;

    /**
     * Gets a provider by it's URL
     *
     * @param providerURL The URL of the provider to get
     * @return The provider with the passed URL, or null if there is no provider associated with that URL.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
     */
    public abstract Provider getByURL(String providerURL) throws DatabaseConfigException;
	/**
	 * Gets a provider by it's ID.  Does not set the list of sets or formats on the returned provider.
	 *
	 * @param providerId The ID of the provider to get
	 * @return The provider with the passed ID, or null if there was no provider with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Provider loadBasicProvider(int providerId) throws DatabaseConfigException;

	/**
	 * Inserts a provider into the database
	 *
	 * @param provider The provider to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed provider was not valid for inserting
	 */
	public abstract boolean insert(Provider provider) throws DataException;

	/**
	 * Updates a provider in the database
	 *
	 * @param provider The provider to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed provider was not valid for updating
	 */
	public abstract boolean update(Provider provider) throws DataException;

	/**
	 * Deletes a provider from the database
	 *
	 * @param provider The provider to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed provider was not valid for deleting
	 */
	public abstract boolean delete(Provider provider) throws DataException, IndexException;

	/**
	 * Validates the fields on the passed Provider Object
	 *
	 * @param provider The provider to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed provider were invalid
	 */
	protected void validateFields(Provider provider, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(provider.getId() < 0)
				errorMessage.append("The provider_id is invalid. ");
		} // end if(we should validate the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(provider.getCreatedAt() == null)
				errorMessage.append("The created_at field is invalid. ");

			if(provider.getName() != null && provider.getName().length() > 255)
				errorMessage.append("The name field is invalid. ");

			if(provider.getOaiProviderUrl() == null || provider.getOaiProviderUrl().length() <= 0 || provider.getOaiProviderUrl().length() > 255)
				errorMessage.append("The OAI_provider_url field is invalid. ");

			if(provider.getType() != null && provider.getType().length() > 11)
				errorMessage.append("The type field is invalid. ");

			if(provider.getFormat() != null && provider.getFormat().length() > 63)
				errorMessage.append("The format field is invalid. ");

			if(provider.getLanguage() != null && provider.getLanguage().length() > 15)
				errorMessage.append("The language field is invalid. ");

			if(provider.getTitle() != null && provider.getTitle().length() > 127)
				errorMessage.append("The title field is invalid. ");

			if(provider.getCreator() != null && provider.getCreator().length() > 127)
				errorMessage.append("The creator field is invalid. ");

			if(provider.getSubject() != null && provider.getSubject().length() > 63)
				errorMessage.append("The subject field is invalid. ");

			if(provider.getPublisher() != null && provider.getPublisher().length() > 127)
				errorMessage.append("The publisher field is invalid. ");

			if(provider.getContributors() != null && provider.getContributors().length() > 511)
				errorMessage.append("The contributors field is invalid. ");

			if(provider.getRelation() != null && provider.getRelation().length() > 255)
				errorMessage.append("The relation field is invalid. ");

			if(provider.getCoverage() != null && provider.getCoverage().length() > 255)
				errorMessage.append("The coverage field is invalid. ");

			if(provider.getRights() != null && provider.getRights().length() > 255)
				errorMessage.append("The rights field is invalid. ");

			if(provider.getProtocolVersion() != null && provider.getProtocolVersion().length() > 7)
				errorMessage.append("The protocol_version field is invalid. ");
			
			if(provider.getLogFileName() != null && provider.getLogFileName().length() > 355)
				errorMessage.append("The log file name is invalid. ");			
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(Provider, boolean, boolean)
} // end class ProviderDAO
