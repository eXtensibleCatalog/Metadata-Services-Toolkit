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
import xc.mst.services.transformation.bo.BibliographicManifestationMapping;

/**
 * Accesses Bibliographic Manifestation Mapping in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class BibliographicManifestationMappingDAO
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
	public final static String MARC_BIBLIOGRAPHIC_TO_XC_MANIFESTATION_MAPPING_TABLE_NAME = "marc_bibliographic_to_xc_manifestation";

	/**
	 * The name of the ID column
	 */
	public final static String COL_BIBLIOGRAPHIC_MANIFESTATION_ID = "bibliographic_manifestation_id";
	
	/**
	 * The name of the bib oai ID column
	 */
	public final static String COL_BIBLIOGRAPHIC_OAI_ID = "bibliographic_oai_id";

	/**
	 * The name of the manifestation oai id column
	 */
	public final static String COL_MANIFESTATION_OAI_ID = "manifestation_oai_id";

	/**
	 * The name of the bib 001 field column
	 */
	public final static String COL_BIBLIOGRAPHIC_001_FIELD = "bibliographic_001_field";

	/**
	 * Gets a bibliographic Manifestation Mapping by 001 field
	 *
	 * @param field001 The 001 field
	 * @return The bibliographic Manifestation Mapping with the passed field001, or null if there was no mapping with that 001 field.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<BibliographicManifestationMapping> getByBibliographic001Field(String field001) throws DatabaseConfigException;

	/**
	 * Gets a marc bibliographic Manifestation Mapping by bib OAI id
	 *
	 * @param bibliographicOAIId bibliographic OAI id
	 * @return The bibliographic Manifestation Mapping with the passed bibliographic OAI id, or null if there was no mapping.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract BibliographicManifestationMapping getByBibliographicOAIId(String bibliographicOAIId) throws DatabaseConfigException;

	/**
	 * Inserts a bibliographic Manifestation Mapping into the database
	 *
	 * @param bibliographicManifestationMapping The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed BibliographicManifestationMapping was not valid for inserting
	 */
	public abstract boolean insert(BibliographicManifestationMapping bibliographicManifestationMapping) throws DataException;

	/**
	 * Updates a BibliographicManifestationMapping in the database
	 *
	 * @param bibliographicManifestationMapping The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed BibliographicManifestationMapping was not valid for updating
	 */
	public abstract boolean update(BibliographicManifestationMapping bibliographicManifestationMapping) throws DataException;

	/**
	 * Deletes a BibliographicManifestationMapping from the database
	 *
	 * @param bibliographicManifestationMapping The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed BibliographicManifestationMapping was not valid for deleting
	 */
	public abstract boolean delete(BibliographicManifestationMapping bibliographicManifestationMapping) throws DataException;

	/**
	 * Validates the fields on the passed BibliographicManifestationMapping Object
	 *
	 * @param bibliographicManifestationMapping The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(BibliographicManifestationMapping bibliographicManifestationMapping, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(bibliographicManifestationMapping.getBibliographicRecord001Field() == null || bibliographicManifestationMapping.getBibliographicRecord001Field().length() <= 0 || bibliographicManifestationMapping.getBibliographicRecord001Field().length() > 500)
				errorMessage.append("The Bib field 001 is invalid. ");

			if(bibliographicManifestationMapping.getBibliographicRecordOAIId() == null || bibliographicManifestationMapping.getBibliographicRecordOAIId().length() <= 0 || bibliographicManifestationMapping.getBibliographicRecordOAIId().length() > 500)
				errorMessage.append("The Bib OAI id is invalid. ");

			if(bibliographicManifestationMapping.getManifestationRecordOAIId() == null || bibliographicManifestationMapping.getManifestationRecordOAIId().length() <= 0 || bibliographicManifestationMapping.getManifestationRecordOAIId().length() > 500)
				errorMessage.append("The manifestation OAI id is invalid. ");
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(BibliographicManifestationMapping, boolean, boolean)
} // end class BibliographicManifestationMappingDAO
