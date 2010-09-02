/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.processing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data access object for the processing directives table
 *
 * @author Eric Osisek
 */
public abstract class ProcessingDirectiveDAO extends BaseDAO
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
	public final static String PROCESSING_DIRECTIVE_TABLE_NAME = "processing_directives";

	/**
	 * The name of the processing directive ID column
	 */
	public final static String COL_PROCESSING_DIRECTIVE_ID = "processing_directive_id";

	/**
	 * The name of the source provider ID column
	 */
	public final static String COL_SOURCE_PROVIDER_ID = "source_provider_id";

	/**
	 * The name of the source service ID column
	 */
	public final static String COL_SOURCE_SERVICE_ID = "source_service_id";

	/**
	 * The name of the service ID column
	 */
	public final static String COL_SERVICE_ID = "service_id";

	/**
	 * The name of the output set ID column
	 */
	public final static String COL_OUTPUT_SET_ID = "output_set_id";

	/**
	 * The name of the maintain source sets column
	 */
	public final static String COL_MAINTAIN_SOURCE_SETS = "maintain_source_sets";

	/**
	 * A set of all columns which are valid for sorting
	 */
	protected static Set<String> sortableColumns = new HashSet<String>();
	
	// Initialize the list of sortable columns
	static
	{
		sortableColumns.add(COL_PROCESSING_DIRECTIVE_ID);
		sortableColumns.add(COL_SOURCE_PROVIDER_ID);
		sortableColumns.add(COL_SOURCE_SERVICE_ID);
		sortableColumns.add(COL_SERVICE_ID);
		sortableColumns.add(COL_OUTPUT_SET_ID);
		sortableColumns.add(COL_MAINTAIN_SOURCE_SETS);
	} // end initialization of sortableColumns
	
	/**
	 * Gets all processing directives from the database
	 *
	 * @return A list containing all processing directives in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<ProcessingDirective> getAll() throws DatabaseConfigException;

	/**
     * Returns a sorted list of processing directives
     * 
     * @param asc Determines whether the list of rows are to be sorted in ascending or descending order
     * @param columnSorted The column on which the rows are to be sorted.
     * @return A sorted list of processing directives
	 * @throws DatabaseConfigException if there was a problem connecting to the database
     */
	public abstract List<ProcessingDirective> getSorted(boolean asc, String columnSorted) throws DatabaseConfigException;
	
	/**
	 * Gets the processing directive from the database with the passed processing directive ID.
	 *
	 * @param processingDirectiveId The ID of the processing directive to get
	 * @return The processing directive with the passed processing directive ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract ProcessingDirective getById(int processingDirectiveId) throws DatabaseConfigException;

	/**
	 * Gets the processing directive from the database with the passed processing directive ID.
	 * This method does not get the input sets or formats.
	 *
	 * @param processingDirectiveId The ID of the processing directive to get
	 * @return The processing directive with the passed processing directive ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract ProcessingDirective loadBasicProcessingDirective(int processingDirectiveId) throws DatabaseConfigException;

	/**
	 * Gets the processing directives from the database with the passed source service ID
	 *
	 * @param serviceId The source service ID of the processing directives to get
	 * @return A list containing the processing directives with the passed source service ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<ProcessingDirective> getBySourceServiceId(int serviceId) throws DatabaseConfigException;

	/**
	 * Gets the processing directives from the database with the passed source provider ID.
	 *
	 * @param providerId The source provider ID of the processing directives to get
	 * @return A list containing the processing directives with the passed source provider ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<ProcessingDirective> getBySourceProviderId(int providerId) throws DatabaseConfigException;

	/**
	 * Inserts a processing directive into the database.
	 *
	 * @param processingDirective The processing directive to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed processing directive was not valid for inserting
	 */
	public abstract boolean insert(ProcessingDirective processingDirective) throws DataException;

	/**
	 * Updates a processing directive in the database.
	 *
	 * @param processingDirective The processing directive to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed processing directive was not valid for updating
	 */
	public abstract boolean update(ProcessingDirective processingDirective) throws DataException;

	/**
	 * Deletes a processing directive from the database.
	 *
	 * @param processingDirective The processing directive to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed processing directive was not valid for deleting
	 */
	public abstract boolean delete(ProcessingDirective processingDirective) throws DataException;

	/**
	 * Validates the fields on the passed ProcessingDirective Object.
	 *
	 * @param schedule The processing directive to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed processing directive were invalid
	 */
	protected void validateFields(ProcessingDirective processingDirective, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(processingDirective.getId() < 0)
				errorMessage.append("The processing directive ID is invalid. ");
		} // end if(we should validate the ID)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(processingDirective.getSourceProvider() == null && processingDirective.getSourceService() == null)
				errorMessage.append("Either the source provider or the source service must be defined.");

			if(processingDirective.getService() == null || processingDirective.getService().getId() <= 0)
				errorMessage.append("The service is invalid. ");
		} // end if(we should check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(ProcessingDirective, boolean, boolean)
} // end class ProcessingDirectiveDAO
