/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.manager.record;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Manifestation;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.utils.index.HoldingsList;
import xc.mst.utils.index.IndexManagerFactory;
import xc.mst.utils.index.SolrIndexManager;
import xc.mst.utils.index.ThreadedSolrIndexManager;

/**
 * Service class to query, add, update and delete records from an index.
 * Records the HoldingsService interacts with belong to the "Holdings" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public abstract class HoldingsService
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Manager for getting, inserting and updating records
	 */
	protected static RecordService recordService = new DefaultRecordService();

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit.
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

	/**
	 * An Object shared by all LuceneObjects which manages the Lucene index
	 */
	protected static SolrIndexManager indexMgr = IndexManagerFactory.getIndexManager(configuration.getProperty(Constants.CONFIG_SOLR_INDEXER));

	/**
	 * The name of the record ID field
	 */
	protected final static String FIELD_RECORD_ID = "record_id";

	/**
	 * The field name for the indexed object type
	 */
	protected static final String FIELD_INDEXED_OBJECT_TYPE = "indexed_object_type";

	/**
	 * Gets a list of Holdings that match the passed recordID
	 *
	 * @param type The type attribute of the recordID element
	 * @param value The value of the recordID element
	 * @return A list of Holdings with the requested recordID element.
	 */
	public HoldingsList getByXcRecordId(String type, String value)
	{
		return getByXcRecordId("(" + type + ")" + value);
	} // end method getByXcRecordId(String, String)

	/**
	 * Gets a list of Holdings that match the passed recordID
	 *
	 * @param recordId The recordID we're querying for in the
	 *                             format (<type>)<value>
	 * @return A list of Holdings with the requested recordID element.
	 */
	public abstract HoldingsList getByXcRecordId(String recordId);

	/**
	 * Gets a list of Holdings that match the passed manifestationHeld
	 *
	 * @param type The type attribute of the manifestationHeld element
	 * @param value The value of the manifestationHeld element
	 * @return A list of Holdings with the requested manifestationHeld element.
	 */
	public HoldingsList getByManifestationHeld(String type, String value)
	{
		return getByManifestationHeld("(" + type + ")" + value);
	} // end method getByManifestationHeld(String, String)

	/**
	 * Gets a list of Holdings that match the passed manifestationHeld
	 *
	 * @param manifestationHeld The manifestationHeld we're querying for in the
	 *                          format (<type>)<value>
	 * @return A list of Holdings with the requested manifestationHeld element.
	 */
	public abstract HoldingsList getByManifestationHeld(String manifestationHeld);

	/**
	 * Gets the Holdings that matches the passed XC holdings ID
	 *
	 * @param The XC holdings ID of the target holdings element
	 */
	public abstract Holdings getByXcHoldingsId(long holdingsId);

	/**
	 * Gets a list of all Holdings linked to the passed manifestation
	 *
	 * @param manifestation The manifestation whose linked Holdings should be returned
	 */
	public abstract HoldingsList getByLinkedManifestation(Manifestation manifestation);

	/**
	 * Inserts a holdings into the index
	 *
	 * @param holdings The holdings to insert
	 * @return true on success, false on failure
	 */
	public boolean insert(Holdings holdings) throws DataException
	{
		// Check that the non-ID fields on the holdings are valid
		validateFields(holdings, false, true);

		if(log.isDebugEnabled())
			log.debug("Inserting a new " + holdings.getIndexedObjectType());

		holdings.setCreatedAt(new Date());

		// Create a Document object and set it's type field
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, holdings.getIndexedObjectType());

		// Set up the fields for the specific type of indexed object
		doc = setFieldsOnDocument(holdings, doc, true);

		return indexMgr.addDoc(doc);
	} // end method insert(Holdings)

	/**
	 * Updates a holdings in the index
	 *
	 * @param holdings The holdings to update
	 * @return true on success, false on failure
	 */
	public boolean update(Holdings holdings) throws DataException
	{
		// Check that the fields on the holdings are valid
		validateFields(holdings, true, true);

		if(log.isDebugEnabled())
			log.debug("Updating the holdings with ID " + holdings.getId());

		// Set the updated at timestamp to now
		holdings.setUpdatedAt(new Date());

		// Set up a Document Object to insert the updated set into the Lucene index
		// Create a Document object and set it's type field

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, holdings.getIndexedObjectType());

		// Set up the fields for the Holdings
		doc = setFieldsOnDocument(holdings, doc, false);

		return indexMgr.addDoc(doc);
	} // end method update(Holdings)

	/**
	 * Deletes a holdings from the index
	 *
	 * @param holdings The holdings to delete
	 * @return true on success, false on failure
	 */
	public boolean delete(Holdings holdings) throws DataException
	{
		// Check that the ID field on the holdings are valid
		validateFields(holdings, true, false);

		if(log.isDebugEnabled())
			log.debug("Deleting the holdings with ID " + holdings.getId());

		// TODO delete implementation
		// Delete all holdings with the matching holdings ID
		boolean result = false;
//		boolean result = indexMgr.deleteDoc(FIELD_RECORD_ID, Long.toString(holdings.getId()));
		// Return the result of the delete
		return result;
	} // end method delete(Holdings)

	/**
	 * Parses a Holdings from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Holdings.
	 * @return The holdings which was contained in the passed Document.
	 */
	public abstract Holdings getHoldingsFromDocument(SolrDocument doc);

	/**
	 * Parses a Holdings from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Holdings.
	 * @return The holdings which was contained in the passed Document.
	 */
	public abstract Holdings getBasicHoldingsFromDocument(SolrDocument doc);

	/**
	 * Sets the fields on the document which need to be stored in the
	 * index.
	 *
	 * @param holdings The holdings to use to set the fields on the document
	 * @param doc The document whose fields need to be set.
	 * @param generateNewId True to generate a new record ID for the holdings, false to use the holdings's current ID
	 * @return A reference to the Document after its fields have been set
	 */
	protected abstract SolrInputDocument setFieldsOnDocument(Holdings holdings, SolrInputDocument doc, boolean generateNewId);

	/**
	 * Validates the fields on the passed Holdings Object
	 *
	 * @param holdings The holdings to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed holdings were invalid
	 */
	protected void validateFields(Holdings holdings, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(holdings.getId() < 0)
				errorMessage.append("The holdings's id is invalid. ");
		} // end if(we should check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(holdings.getFormat() == null)
				errorMessage.append("The holdings's format is invalid. ");

		} // end if(we should check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(we found an error)
	} // end method validateFields(Holdings, boolean, boolean)
} // end class HoldingsService
