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

import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Item;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.ItemList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * Service class to query, add, update and delete records from an index.
 * Records the ItemService interacts with belong to the "Item" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public abstract class ItemService extends BaseService
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
     * Gets a list of Items that match the passed holdingsExemplified
     *
     * @param type The type attribute of the holdingsExemplified element
     * @param value The value of the holdingsExemplified element
     * @return A list of Item with the requested holdingsExemplified element.
     */
    public ItemList getByHoldingsExemplified(String type, String value) throws IndexException
    {
        return getByHoldingsExemplified("(" + type + ")" + value);
    } // end method getByHoldingsExemplified(String, String)

    /**
     * Gets a list of Items that match the passed holdingsExemplified
     *
     * @param holdingsExemplified The holdingsExemplified we're querying for in the
     *                            format (<type>)<value>
     * @return A list of Items with the requested manifestationHeld element.
     */
    public abstract ItemList getByHoldingsExemplified(String holdingsExemplified) throws IndexException;

    /**
     * Gets the Item that matches the passed XC item ID
     *
     * @param The XC item ID of the target item element
     * @throws DatabaseConfigException
     */
    public abstract Item getByXcItemId(long itemId) throws DatabaseConfigException, IndexException;

    /**
     * Gets a list of all Items linked to the passed holdings
     *
     * @param holdings The holdings whose linked Items should be returned
     */
    public abstract ItemList getByLinkedHoldings(Holdings holdings) throws IndexException;

    /**
     * Gets all items from the index which have been processed from the specified record
     *
     * @param processedFrom The ID of the original record whose processed Records we're getting
     * @return A list of all records in the index which have been processed from the specified record
     */
    public abstract ItemList getByProcessedFrom(Record processedFrom) throws IndexException;

    /**
     * Inserts an item into the index
     *
     * @param item The item to insert
     * @return true on success, false on failure
     */
    public boolean insert(Item item) throws DataException, IndexException
    {
        // Check that the non-ID fields on the item are valid
        validateFields(item, false, true);

        if(log.isDebugEnabled())
            log.debug("Inserting a new " + item.getType());

        item.setCreatedAt(new Date());

        // Create a Document object and set it's type field
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(FIELD_INDEXED_OBJECT_TYPE, item.getType());

        // Set up the fields for the specific type of indexed object
        doc = setFieldsOnDocument(item, doc, true);

        SolrIndexManager sim = (SolrIndexManager)config.getBean("SolrIndexManager");
        return sim.addDoc(doc);
    } // end method insert(Item)

    /**
     * Updates a item in the index
     *
     * @param item The item to update
     * @return true on success, false on failure
     */
    public boolean update(Item item) throws DataException, IndexException
    {
        // Check that the fields on the item are valid
        validateFields(item, true, true);

        if(log.isDebugEnabled())
            log.debug("Updating the item with ID " + item.getId());

        // Set the updated at timestamp to now
        item.setUpdatedAt(new Date());

        // Set up a Document Object to insert the updated set into the Lucene index
        // Create a Document object and set it's type field

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(FIELD_INDEXED_OBJECT_TYPE, item.getType());

        // Set up the fields for the Item
        doc = setFieldsOnDocument(item, doc, false);

        SolrIndexManager sim = (SolrIndexManager)config.getBean("SolrIndexManager");
        return sim.addDoc(doc);
    } // end method update(Item)

    /**
     * Deletes a item from the index
     *
     * @param item The item to delete
     * @return true on success, false on failure
     */
    public boolean delete(Item item) throws DataException
    {
        // Check that the ID field on the item are valid
        validateFields(item, true, false);

        if(log.isDebugEnabled())
            log.debug("Deleting the item with ID " + item.getId());

        // Delete all items with the matching item ID
        // TODO delete implementation
        boolean result = false;
        //boolean result = indexMgr.deleteDoc(FIELD_RECORD_ID, Long.toString(item.getId()));

        // Return the result of the delete
        return result;
    } // end method delete(Item)

    /**
     * Parses a Item from the fields in a Document from the index.
     *
     * @param doc The document containing information on the Item.
     * @return The item which was contained in the passed Document.
     * @throws DatabaseConfigException
     */
    public abstract Item getItemFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException;

    /**
     * Parses a Item from the fields in a Document from the index.
     *
     * @param doc The document containing information on the Item.
     * @return The item which was contained in the passed Document.
     */
    public abstract Item getBasicItemFromDocument(SolrDocument doc);

    /**
     * Sets the fields on the document which need to be stored in the
     * index.
     *
     * @param item The item to use to set the fields on the document
     * @param doc The document whose fields need to be set.
     * @param generateNewId True to generate a new record ID for the item, false to use the item's current ID
     * @return A reference to the Document after its fields have been set
     * @throws DatabaseConfigException
     */
    protected abstract SolrInputDocument setFieldsOnDocument(Item item, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException;

    /**
     * Validates the fields on the passed Item Object
     *
     * @param item The item to validate
     * @param validateId true if the ID field should be validated
     * @param validateNonId true if the non-ID fields should be validated
     * @throws DataException If one or more of the fields on the passed item were invalid
     */
    protected void validateFields(Item item, boolean validateId, boolean validateNonId) throws DataException
    {
        StringBuilder errorMessage = new StringBuilder();

        // Check the ID field if we're supposed to
        if(validateId)
        {
            if(log.isDebugEnabled())
                log.debug("Checking the ID");

            if(item.getId() < 0)
                errorMessage.append("The item's id is invalid. ");
        } // end if(we should check the ID field)

        // Check the non-ID fields if we're supposed to
        if(validateNonId)
        {
            if(log.isDebugEnabled())
                log.debug("Checking the non-ID fields");

            if(item.getFormat() == null)
                errorMessage.append("The item's format is invalid. ");

        } // end if(we should check the non-ID fields)

        // Log the error and throw the exception if any fields are invalid
        if(errorMessage.length() > 0)
        {
            String errors = errorMessage.toString();
            log.error("The following errors occurred: " + errors);
            throw new DataException(errors);
        } // end if(we found an error)
    } // end method validateFields(Item, boolean, boolean)
} // end class ItemService
