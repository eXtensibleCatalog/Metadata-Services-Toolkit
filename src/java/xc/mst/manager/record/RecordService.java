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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.record.Expression;
import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Item;
import xc.mst.bo.record.Manifestation;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.harvest.DefaultHarvestRecordUtilDAO;
import xc.mst.dao.harvest.HarvestRecordUtilDAO;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * Service class to query, add, update and delete records from an index.
 *
 * @author Eric Osisek
 */
public abstract class RecordService
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 *  Data access object for updating harvest to record assignments
	 */
	private HarvestRecordUtilDAO harvestRecordDao = new DefaultHarvestRecordUtilDAO();

	/**
	 * An Object shared by all LuceneObjects which manages the Lucene index
	 */
	protected static SolrIndexManager indexMgr = SolrIndexManager.getInstance();

	/**
	 * The field name for the indexed object type
	 */
	protected static final String FIELD_INDEXED_OBJECT_TYPE = "indexed_object_type";

	/**
	 * The Lucene object term
	 */
	protected static final Term TERM_INDEXED_OBJECT_TYPE = new Term(FIELD_INDEXED_OBJECT_TYPE, "");

	/**
	 * The name of the record ID field
	 */
	protected final static String FIELD_RECORD_ID = "record_id";

	/**
	 * The name of the frbr level ID field
	 */
	protected final static String FIELD_FRBR_LEVEL_ID = "frbr_level_id";

	/**
	 * The name of the up link field
	 */
	protected final static String FIELD_UP_LINK = "up_link";

	/**
	 * The name of the created at field
	 */
	protected final static String FIELD_CREATED_AT = "created_at";

	/**
	 * The name of the updated at field
	 */
	protected final static String FIELD_UPDATED_AT = "updated_at";

	/**
	 * The name of the deleted field
	 */
	protected final static String FIELD_DELETED = "deleted";

	/**
	 * The name of the format ID field
	 */
	protected final static String FIELD_FORMAT_ID = "format_id";

	/**
	 * The name of the provider ID field
	 */
	protected final static String FIELD_PROVIDER_ID = "provider_id";

	/**
	 * The name of the service ID field
	 */
	protected final static String FIELD_SERVICE_ID = "service_id";

	/**
	 * The name of the service field
	 */
	protected final static String FIELD_SERVICE_NAME = "service_name";

	/**
	 * The name of the harvest ID field
	 */
	protected final static String FIELD_HARVEST_ID = "harvest_id";

	/**
	 * The name of the harvest schedule ID field
	 */
	protected final static String FIELD_HARVEST_SCHEDULE_ID = "harvest_schedule_id";

	/**
	 * The name of the harvest schedule field
	 */
	protected final static String FIELD_HARVEST_SCHEDULE_NAME = "harvest_schedule_name";

	/**
	 * The name of the provider name field
	 */
	protected final static String FIELD_PROVIDER_NAME = "provider_name";

	/**
	 * The name of the provider URL field
	 */
	protected final static String FIELD_PROVIDER_URL = "provider_url";
	
	/**
	 * The name of the provider/service and harvest start date and time
	 */
	protected final static String FIELD_HARVEST_START_TIME = "harvest_start_time";

	/**
	 * The name of the format name field
	 */
	protected final static String FIELD_FORMAT_NAME = "format_name";

	/**
	 * The name of the OAI identifier  field
	 */
	protected final static String FIELD_OAI_IDENTIFIER = "oai_identifier";

	/**
	 * The name of the OAI datestamp field
	 */
	protected final static String FIELD_OAI_DATESTAMP = "oai_datestamp";

	/**
	 * The name of the OAI header field
	 */
	protected final static String FIELD_OAI_HEADER = "oai_header";

	/**
	 * The name of the XML field
	 */
	protected final static String FIELD_OAI_XML = "oai_xml";

	/**
	 * The name of the set name field
	 */
	protected final static String FIELD_SET_NAME = "set_name";

	/**
	 * The name of the setSpec field
	 */
	protected final static String FIELD_SET_SPEC = "set_spec";

	/**
	 * The name of the processed from field
	 */
	protected final static String FIELD_PROCESSED_FROM = "processed_from";

	/**
	 * The name of the successor field
	 */
	protected final static String FIELD_SUCCESSOR = "successor";
	
	/**
	 * The name of the input for service IDs field
	 */
	protected final static String FIELD_INPUT_FOR_SERVICE_ID = "input_for_service_id";

	/**
	 * The name of the processed by service IDs field
	 */
	protected final static String FIELD_PROCESSED_BY_SERVICE_ID = "processed_by_service_id";

	/**
	 * The name of the traits field
	 */
	protected final static String FIELD_TRAIT = "trait";

	/**
	 * The name of the errors field
	 */
	protected final static String FIELD_ERROR = "error";
	
	/**
	 * All default search fields
	 */
	protected final static String FIELD_ALL = "all";

	/**
	 * Gets all records from the index
	 *
	 * @return A list of all records in the index
	 */
	public abstract RecordList getAll();

	/**
	 * Gets the record from the index with the passed record ID
	 *
	 * @param id The record's ID
	 * @return The record with the passed record ID
	 */
	public abstract Record getById(long id);

	/**
	 * Gets the basic information for a a record from the index with the passed record ID
	 *
	 * @param id The record's ID
	 * @return The basic information for a record with the passed record ID
	 */
	public abstract Record loadBasicRecord(long id);

	/**
	 * Gets all records from the index that match a Lucene query string
	 *
	 * @param queryString The Lucene query that should be run to get the records
	 * @return A list of all records in the index matching the provided query
	 * @throws ParseException If the Lucene query was invalid
	 */
	public abstract RecordList getByLuceneQuery(String queryString) throws ParseException;

	/**
	 * Gets all records from the index with the passed provider ID
	 *
	 * @param providerId The provider ID of the records to retrieve
	 * @return A list of all records in the index with the passed provider ID
	 */
	public abstract RecordList getByProviderId(int providerId);

	/**
	 * Gets all records from the index with the passed service ID
	 *
	 * @param serviceId The service ID of the records to retrieve
	 * @return A list of all records in the index with the passed provider ID
	 */
	public abstract RecordList getByServiceId(int serviceId);

	/**
	 * Gets number of records from the index with the passed service ID
	 *
	 * @param serviceId The service ID of the records to retrieve
	 * @return Number of records in the index with the passed provider ID
	 */
	public abstract long getNumberOfRecordsByServiceId(int serviceId);
	
	/**
	 * Gets all records from the index with the passed processing service ID
	 *
	 * @param serviceId The service ID of the service that processed records to retrieve
	 * @return A list of all records in the index with the passed processing service ID
	 */
	public abstract RecordList getByProcessingServiceId(int serviceId);
	
	/**
	 * Gets all records from the index with the passed harvest ID
	 *
	 * @param harvestId The harvest ID of the records to retrieve
	 * @return A list of all records in the index with the passed harvest ID
	 */
	public abstract RecordList getByHarvestId(int harvestId);

	/**
	 * Gets all records from the index with the passed harvest schedule ID
	 *
	 * @param harvestScheduleId The harvest schedule ID of the records to retrieve
	 * @return A list of all records in the index with the passed harvest schedule ID
	 */
	public abstract RecordList getByHarvestScheduleId(int harvestScheduleId);

	/**
	 * Gets all records from the index with the passed format ID and service ID
	 *
	 * @param formatId The format ID of the records to retrieve
	 * @param serviceId The service that processed the records to retrieve
	 * @return A list all records in the index with the passed format ID
	 */
	public abstract RecordList getByFormatIdAndServiceId(int formatId, int serviceId);
	
	/**
	 * Gets all records from the index contained in the set with the passed name
	 *
	 * @param setName the name of the set whose records should be returned
	 * @return A list all records in the index contained in the set with the passed name
	 */
	public abstract RecordList getBySetName(String setName);

	/**
	 * Gets all records from the index contained in the set with the passed setSpec
	 *
	 * @param setSpec the setSpec of the set whose records should be returned
	 * @return A list all records in the index contained in the set with the passed setSpec
	 */
	public abstract RecordList getBySetSpec(String setSpec);

	/**
	 * Gets all records from the index harvested from the provider with the passed name
	 *
	 * @param providerName the name of the provider whose records should be returned
	 * @return A list all records in the index harvested from the provider with the passed name
	 */
	public abstract RecordList getByProviderName(String providerName);

	/**
	 * Gets all records from the index harvested from the provider with the passed URL
	 *
	 * @param providerUrl the URL of the provider whose records should be returned
	 * @return A list all records in the index harvested from the provider with the passed URL
	 */
	public abstract RecordList getByProviderUrl(String providerUrl);

	/**
	 * Gets all records from the index with the format with the passed name
	 *
	 * @param formatName the name of the format whose records should be returned
	 * @return A list all records in the index with the format with the passed name
	 */
	public abstract RecordList getByFormatName(String formatName);

	/**
	 * Gets all record inputs that were not processed for a given service
	 *
	 * @return A list of all records that need to be processed for a given service
	 */
	public abstract RecordList getInputForService(int serviceId);

	/**
	 * Gets the record from the index with the passed OAI Identifier
	 *
	 * @param identifier The record's OAI Identifer
	 * @return A Record Object representing the record with the passed OAI Identifier
	 */
	public abstract Record getByOaiIdentifier(String identifier);

	/**
	 * Gets the record from the index with the passed OAI Identifier
	 *
	 * @param identifier The record's OAI Identifer
	 * @param providerId The Id of the provider from which this record was harvested
	 * @return The record with the passed OAI Identifier
	 */
	public abstract Record getByOaiIdentifierAndProvider(String identifier, int providerId);

	/**
	 * Gets the record from the index with the passed OAI Identifier
	 *
	 * @param identifier The record's OAI Identifer
	 * @param serviceId The Id of the service that processed this record
	 * @return The record with the passed OAI Identifier
	 */
	public abstract Record getByOaiIdentifierAndService(String identifier, int serviceId);

	/**
	 * Gets all records from the index which have been processed from the specified record
	 *
	 * @param processedFromId The ID of the original record whose processed Records we're getting
	 * @return A list of all records in the index which have been processed from the specified record
	 */
	public abstract RecordList getByProcessedFrom(long processedFromId);

	/**
	 * Gets all records from the index with the passed trait
	 *
	 * @param trait The trait of the records to retrieve
	 * @return A list of all records in the index with the passed trait
	 */
	public abstract RecordList getByTrait(String trait);

	/**
	 * Gets the record from the index with the earliest datestamp processed by a given service
	 *
	 * @param serviceId The ID of the service whose earliest processed record we're looking for
	 * @return The record with earliest datestamp that was processed by the target service
	 */
	public abstract Record getEarliest(int serviceId);

	/**
	 * Returns the number of processed records between the specified dates contained in the specified set
	 *
	 * @param fromDate The lower bound for the date for the records to count
	 * @param untilDate The upper bound for the date for the records to count
	 * @param setId The setId of the set for the records to count.  If this is null,
	 *            the count will include all sets.
	 * @param formatId The ID of the metadata format of the records to count.  If less than 0,
	 *                 the count will include all metadata types.
	 * @param serviceId The service which processed the outgoing records
	 * @return The number of records matching the parameters queried for
	 */
	public abstract long getCount(Date fromDate, Date untilDate, int setId, int formatId, int serviceId);

	/**
	 * Returns the records between the specified dates within the specified set
	 *
	 * @param fromDate The lower bound for the date for the records to return
	 * @param untilDate The upper bound for the date for the records to return
	 * @param setId The ID of the set for the records to return
     * @param formatId The ID of the format for the records to return
	 * @param offset The offset into the list of matching records representing the first record to return
	 * @param numResults The number of records to return
	 * @param serviceId The service which processed the outgoing records
	 * @return A list of records matching the parameters queried for
	 */
	public abstract List<Record> getOutgoingRecordsInRange(Date fromDate, Date untilDate, int setId, int formatId, int offset, int numResults, int serviceId);

	/**
	 * Inserts a record into the index
	 *
	 * @param record The record to insert
	 * @return true on success, false on failure
	 */
	public boolean insert(Record record) throws DataException
	{
		// Check that the non-ID fields on the record are valid
		validateFields(record, false, true);

		if(log.isDebugEnabled())
			log.debug("Inserting a new " + record.getIndexedObjectType());

		record.setCreatedAt(new Date());

		// Create a Document object and set it's type field
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, record.getIndexedObjectType());

		// Set up the fields for the specific type of indexed object
		if(record instanceof Work)
			doc = new DefaultWorkService().setFieldsOnDocument((Work)record, doc, true);
		else if(record instanceof Expression)
			doc = new DefaultExpressionService().setFieldsOnDocument((Expression)record, doc, true);
		else if(record instanceof Manifestation)
			doc = new DefaultManifestationService().setFieldsOnDocument((Manifestation)record, doc, true);
		else if(record instanceof Holdings)
			doc = new DefaultHoldingsService().setFieldsOnDocument((Holdings)record, doc, true);
		else if(record instanceof Item)
			doc = new DefaultItemService().setFieldsOnDocument((Item)record, doc, true);
		else
			doc = setFieldsOnDocument(record, doc, true);

		return indexMgr.addDoc(doc);
	} // end method insert(Record)

	/**
	 * Updates a record in the index
	 *
	 * @param record The record to update
	 * @return true on success, false on failure
	 */
	public boolean update(Record record) throws DataException
	{
		// Check that the fields on the record are valid
		validateFields(record, true, true);

		if(log.isDebugEnabled())
			log.debug("Updating the record with ID " + record.getId());

		// Set the updated at timestamp to now
		record.setUpdatedAt(new Date());

		// Set up a Document Object to insert the updated set into the Lucene index
		// Create a Document object and set it's type field

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, record.getIndexedObjectType());

		// Set up the fields for the Record
		doc = setFieldsOnDocument(record, doc, false);

		return indexMgr.addDoc(doc);
	} // end method update(Record)

	/**
	 * Deletes a record from the index
	 *
	 * @param record The record to delete
	 * @return true on success, false on failure
	 */
	public boolean delete(Record record) throws DataException
	{
		// Check that the ID field on the record are valid
		validateFields(record, true, false);

		if(log.isDebugEnabled())
			log.debug("Deleting the record with ID " + record.getId());

		String deleteQuery = FIELD_RECORD_ID + ":" + Long.toString(record.getId()) + "  AND "
		                     + FIELD_INDEXED_OBJECT_TYPE + ":" + Record.indexedObjectType;
		
		// Delete all records with the matching record ID
		boolean result = indexMgr.deleteByQuery(deleteQuery);

		// If the delete was successful, also delete rows in the MySQL tables which reference it
		if(result)
			harvestRecordDao.deleteForRecord(record.getId());

		// Return the result of the delete
		return result;
	} // end method delete(Record)

	/**
	 * Parses a Record from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Record.
	 * @return The record which was contained in the passed Document.
	 */
	public abstract Record getRecordFromDocument(SolrDocument doc);

	/**
	 * Parses a Record from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Record.
	 * @return The record which was contained in the passed Document.
	 */
	public abstract Record getBasicRecordFromDocument(SolrDocument doc);

	/**
	 * Sets the fields on the document which need to be stored in the
	 * index.
	 *
	 * @param record The record to use to set the fields on the document
	 * @param doc The document whose fields need to be set.
	 * @param generateNewId True to generate a new ID for the record, false to use the record's current ID
	 * @return A reference to the Document after its fields have been set
	 */
	protected abstract SolrInputDocument setFieldsOnDocument(Record record, SolrInputDocument doc, boolean generateNewId);

	/**
	 * Validates the fields on the passed Record Object
	 *
	 * @param record The record to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed record were invalid
	 */
	protected void validateFields(Record record, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(record.getId() < 0)
				errorMessage.append("The record's id is invalid. ");
		} // end if(we should check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(record.getFormat() == null)
				errorMessage.append("The record's format is invalid. ");

		} // end if(we should check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(we found an error)
	} // end method validateFields(Record, boolean, boolean)
} // end class RecordService
