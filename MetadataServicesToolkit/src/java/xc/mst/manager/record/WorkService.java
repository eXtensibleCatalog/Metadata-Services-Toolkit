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
import org.apache.lucene.index.Term;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.IndexManagerFactory;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;
import xc.mst.utils.index.WorkList;

/**
 * Service class to query, add, update and delete records from an index.
 * Records the WorkService interacts with belong to the "Work" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public abstract class WorkService
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
	protected static final Configuration configuration = ConfigurationManager.getConfiguration();

	/**
	 * An Object shared by all LuceneObjects which manages the Lucene index
	 */
	protected static SolrIndexManager indexMgr = IndexManagerFactory.getIndexManager(configuration.getProperty(Constants.CONFIG_SOLR_INDEXER));

	/**
	 * The name of the processed field
	 */
	protected final static String FIELD_PROCESSED = "processed";

	/**
	 * The name of the record ID field
	 */
	protected final static String FIELD_RECORD_ID = "record_id";

	/**
	 * The field name for the indexed object type
	 */
	protected static final String FIELD_INDEXED_OBJECT_TYPE = "indexed_object_type";

	/**
	 * The processed term
	 */
	protected final static Term TERM_PROCESSED = new Term(FIELD_PROCESSED, "");

	/**
	 * Gets a list of Works that match the passed identifierForTheWork
	 *
	 * @param type The type attribute of the identifierForTheWork element
	 * @param value The value of the identifierForTheWork element
	 * @return A list of Works with the requested identifierForTheWork element.
	 */
	public WorkList getByIdentifierForTheWork(String type, String value) throws IndexException
	{
		return getByIdentifierForTheWork("(" + type + ")" + value);
	} // end method getByIdentifierForTheWork(String, String)

	/**
	 * Gets a list of Works that match the passed identifierForTheWork
	 *
	 * @param identifierForTheWork The identifierForTheWork we're querying for in the
	 *                             format (<type>)<value>
	 * @return A list of Works with the requested identifierForTheWork element.
	 */
	public abstract WorkList getByIdentifierForTheWork(String identifierForTheWork) throws IndexException;

	/**
	 * Gets the Work that matches the passed XC work ID
	 *
	 * @param The XC work ID of the target work element
	 * @throws DatabaseConfigException
	 */
	public abstract Work getByXcWorkId(long workId) throws DatabaseConfigException, IndexException;

	/**
	 * Gets all works from the index which have been processed from the specified record
	 *
	 * @param processedFrom The ID of the original record whose processed Records we're getting
	 * @return A list of all records in the index which have been processed from the specified record
	 */
	public abstract WorkList getByProcessedFrom(Record processedFrom) throws IndexException;
	
	/**
	 * Gets a list of Works that have not been processed
	 *
	 * @param serviceId The ID of the service whose unprocessed works to get
	 * @return A list of Works that have not been processed
	 */
	public abstract WorkList getUnprocessedWorks(int serviceId) throws IndexException;
	
	/**
	 * Inserts a work into the index
	 *
	 * @param work The work to insert
	 * @return true on success, false on failure
	 */
	public boolean insert(Work work) throws DataException, IndexException
	{
		// Check that the non-ID fields on the work are valid
		validateFields(work, false, true);

		if(log.isDebugEnabled())
			log.debug("Inserting a new " + work.getIndexedObjectType());

		work.setCreatedAt(new Date());

		// Create a Document object and set it's type field
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, work.getIndexedObjectType());

		// Set up the fields for the specific type of indexed object
		doc = setFieldsOnDocument(work, doc, true);

		return indexMgr.addDoc(doc);
	} // end method insert(Work)

	/**
	 * Updates a work in the index
	 *
	 * @param work The work to update
	 * @return true on success, false on failure
	 */
	public boolean update(Work work) throws DataException, IndexException
	{
		// Check that the fields on the work are valid
		validateFields(work, true, true);

		if(log.isDebugEnabled())
			log.debug("Updating the work with ID " + work.getId());

		// Set the updated at timestamp to now
		work.setUpdatedAt(new Date());

		// Set up a Document Object to insert the updated set into the Lucene index
		// Create a Document object and set it's type field

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, work.getIndexedObjectType());

		// Set up the fields for the Work
		doc = setFieldsOnDocument(work, doc, false);

		return indexMgr.addDoc(doc);
	} // end method update(Work)

	/**
	 * Deletes a work from the index
	 *
	 * @param work The work to delete
	 * @return true on success, false on failure
	 */
	public boolean delete(Work work) throws DataException
	{
		// Check that the ID field on the work are valid
		validateFields(work, true, false);

		if(log.isDebugEnabled())
			log.debug("Deleting the work with ID " + work.getId());

		// TODO delete implementation
		// Delete all works with the matching work ID
		boolean result = false;
		//boolean result = indexMgr.deleteDoc(FIELD_RECORD_ID, Long.toString(work.getId()));
		// Return the result of the delete
		return result;
	} // end method delete(Work)

	/**
	 * Parses a Work from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Work.
	 * @return The work which was contained in the passed Document.
	 * @throws DatabaseConfigException
	 */
	public abstract Work getWorkFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException;

	/**
	 * Parses a Work from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Work.
	 * @return The work which was contained in the passed Document.
	 */
	public abstract Work getBasicWorkFromDocument(SolrDocument doc);

	/**
	 * Sets the fields on the document which need to be stored in the
	 * index.
	 *
	 * @param work The work to use to set the fields on the document
	 * @param doc The document whose fields need to be set.
	 * @param generateNewId True to generate a new record ID for the work, false to use the work's current ID
	 * @return A reference to the Document after its fields have been set
	 * @throws DatabaseConfigException
	 */
	protected abstract SolrInputDocument setFieldsOnDocument(Work work, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException;

	/**
	 * Validates the fields on the passed Work Object
	 *
	 * @param work The work to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed work were invalid
	 */
	protected void validateFields(Work work, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(work.getId() < 0)
				errorMessage.append("The work's id is invalid. ");
		} // end if(we should check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(work.getFormat() == null)
				errorMessage.append("The work's format is invalid. ");

		} // end if(we should check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(we found an error)
	} // end method validateFields(Work, boolean, boolean)
} // end class WorkService
