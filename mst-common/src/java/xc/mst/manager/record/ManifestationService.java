/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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

import xc.mst.bo.record.Expression;
import xc.mst.bo.record.Manifestation;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.ManifestationList;
import xc.mst.utils.index.SolrIndexManager;

public abstract class ManifestationService extends BaseService
{
    /**
     * A reference to the logger for this class
     */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * The name of the record ID field
     */
    protected final static String FIELD_RECORD_ID = "record_id";

    /**
     * The field name for the indexed object type
     */
    protected static final String FIELD_INDEXED_OBJECT_TYPE = "indexed_object_type";

    /**
     * Gets a list of Manifestations that match the passed XC record ID
     *
     * @param type The type attribute of the XC record ID element
     * @param value The value of the XC record ID element
     * @return A list of Manifestations with the requested XC record ID element.
     */
    public ManifestationList getByXcRecordId(String type, String value) throws IndexException
    {
        return getByXcRecordId("(" + type + ")" + value);
    } // end method getByXcRecordId(String, String)

    /**
     * Gets a list of Manifestations that match the passed XC record ID
     *
     * @param xcRecordId The XC record ID we're querying for in the
     *                             format (<type>)<value>
     * @return A list of Manifestations with the requested XC record ID element.
     */
    public abstract ManifestationList getByXcRecordId(String xcRecordId) throws IndexException;

    /**
     * Gets the Manifestation that matches the passed XC manifestation ID
     *
     * @param manifestationId The XC manifestation ID of the target manifestation element
     * @throws DatabaseConfigException
     */
    public abstract Manifestation getByXcManifestationId(long manifestationId) throws DatabaseConfigException, IndexException;

    /**
     * Gets a list of all Manifestations linked to the passed expression
     *
     * @param expression The expression whose linked Manifestations should be returned
     */
    public abstract ManifestationList getByLinkedExpression(Expression expression) throws IndexException;

    /**
     * Gets all manifestations from the index which have been processed from the specified record
     *
     * @param processedFrom The ID of the original record whose processed Records we're getting
     * @return A list of all records in the index which have been processed from the specified record
     */
    public abstract ManifestationList getByProcessedFrom(Record processedFrom) throws IndexException;

    /**
     * Inserts a manifestation into the index
     *
     * @param manifestation The manifestation to insert
     * @return true on success, false on failure
     */
    public boolean insert(Manifestation manifestation) throws DataException, IndexException
    {
        // Check that the non-ID fields on the manifestation are valid
        validateFields(manifestation, false, true);

        if(log.isDebugEnabled())
            log.debug("Inserting a new " + manifestation.getType());

        manifestation.setCreatedAt(new Date());

        // Create a Document object and set it's type field
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(FIELD_INDEXED_OBJECT_TYPE, manifestation.getType());

        // Set up the fields for the specific type of indexed object
        doc = setFieldsOnDocument(manifestation, doc, true);

        SolrIndexManager sim = (SolrIndexManager)config.getBean("SolrIndexManager");
        return sim.addDoc(doc);
    } // end method insert(Manifestation)

    /**
     * Updates a manifestation in the index
     *
     * @param manifestation The manifestation to update
     * @return true on success, false on failure
     */
    public boolean update(Manifestation manifestation) throws DataException, IndexException
    {
        // Check that the fields on the manifestation are valid
        validateFields(manifestation, true, true);

        if(log.isDebugEnabled())
            log.debug("Updating the manifestation with ID " + manifestation.getId());

        // Set the updated at timestamp to now
        manifestation.setUpdatedAt(new Date());

        // Set up a Document Object to insert the updated set into the Lucene index
        // Create a Document object and set it's type field

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(FIELD_INDEXED_OBJECT_TYPE, manifestation.getType());

        // Set up the fields for the Manifestation
        doc = setFieldsOnDocument(manifestation, doc, false);

        SolrIndexManager sim = (SolrIndexManager)config.getBean("SolrIndexManager");
        return sim.addDoc(doc);
    } // end method update(Manifestation)

    /**
     * Deletes a manifestation from the index
     *
     * @param manifestation The manifestation to delete
     * @return true on success, false on failure
     */
    public boolean delete(Manifestation manifestation) throws DataException
    {
        // Check that the ID field on the manifestation are valid
        validateFields(manifestation, true, false);

        if(log.isDebugEnabled())
            log.debug("Deleting the manifestation with ID " + manifestation.getId());

        // TODO delete implementation
        // Delete all manifestations with the matching manifestation ID
        boolean result = false;
//		boolean result = indexMgr.deleteDoc(FIELD_RECORD_ID, Long.toString(manifestation.getId()));
        // Return the result of the delete
        return result;
    } // end method delete(Manifestation)

    /**
     * Parses a Manifestation from the fields in a Document from the index.
     *
     * @param doc The document containing information on the Manifestation.
     * @return The manifestation which was contained in the passed Document.
     * @throws DatabaseConfigException
     */
    public abstract Manifestation getManifestationFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException;

    /**
     * Parses a Manifestation from the fields in a Document from the index.
     *
     * @param doc The document containing information on the Manifestation.
     * @return The manifestation which was contained in the passed Document.
     * @throws DatabaseConfigException
     */
    public abstract Manifestation getBasicManifestationFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException;

    /**
     * Sets the fields on the document which need to be stored in the
     * index.
     *
     * @param manifestation The manifestation to use to set the fields on the document
     * @param doc The document whose fields need to be set.
     * @param generateNewId True to generate a new record ID for the manifestation, false to use the manifestation's current ID
     * @return A reference to the Document after its fields have been set
     * @throws DatabaseConfigException
     */
    protected abstract SolrInputDocument setFieldsOnDocument(Manifestation manifestation, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException;

    /**
     * Validates the fields on the passed Manifestation Object
     *
     * @param manifestation The manifestation to validate
     * @param validateId true if the ID field should be validated
     * @param validateNonId true if the non-ID fields should be validated
     * @throws DataException If one or more of the fields on the passed manifestation were invalid
     */
    protected void validateFields(Manifestation manifestation, boolean validateId, boolean validateNonId) throws DataException
    {
        StringBuilder errorMessage = new StringBuilder();

        // Check the ID field if we're supposed to
        if(validateId)
        {
            if(log.isDebugEnabled())
                log.debug("Checking the ID");

            if(manifestation.getId() < 0)
                errorMessage.append("The manifestation's id is invalid. ");
        } // end if(we should check the ID field)

        // Check the non-ID fields if we're supposed to
        if(validateNonId)
        {
            if(log.isDebugEnabled())
                log.debug("Checking the non-ID fields");

            if(manifestation.getFormat() == null)
                errorMessage.append("The manifestation's format is invalid. ");

        } // end if(we should check the non-ID fields)

        // Log the error and throw the exception if any fields are invalid
        if(errorMessage.length() > 0)
        {
            String errors = errorMessage.toString();
            log.error("The following errors occurred: " + errors);
            throw new DataException(errors);
        } // end if(we found an error)
    } // end method validateFields(Manifestation, boolean, boolean)
} // end class ManifestationService
