/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.record;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.record.Expression;
import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Item;
import xc.mst.bo.record.Record;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.ExpressionList;
import xc.mst.utils.index.ItemList;

/**
 * Lucene implementation of the service class to query, add, update and
 * delete records from an index.
 *
 * Records the ItemService interacts with belong to the "Item" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class DefaultItemService extends ItemService
{
	@Override
	public Item getByXcItemId(long itemId) throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting the record with XC item ID " + itemId);

		// Create a query to get the Documents with the requested XC work ID
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_FRBR_LEVEL_ID + ":" + Long.toString(itemId)
				+ " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Item.indexedObjectType);

		// Get the result of the query
		SolrDocumentList doc = null;
		doc = indexMgr.getDocumentList(query);

		// Return null if we couldn't find the item with the correct XC item ID
		if(doc == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the item with XC item ID " + itemId + ".");

			return null;
		} // end if(no result found)

		if(log.isDebugEnabled())
			log.debug("Parcing the item with XC item ID " + itemId + " from the Lucene Document it was stored in.");

		return getItemFromDocument(doc.get(0));
	} // end method getByXcItemId(long)

	@Override
	public ItemList getByHoldingsExemplified(String holdingsExemplified) throws IndexException
	{
		String trait = recordService.escapeString(Item.TRAIT_HOLDINGS_EXEMPLIFIED + ":" + holdingsExemplified);

		if(log.isDebugEnabled())
			log.debug("Getting all items with trait " + trait);

		// Create a query to get the Documents with the requested trait
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_TRAIT + ":" + trait + " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Item.indexedObjectType);

		// Return the list of results
		return new ItemList(query);
	} // end method getByHoldingsExemplified(String)

	@Override
	public ItemList getByLinkedHoldings(Holdings holdings) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all items linked to the holdings with ID " + holdings.getId());

		// Create a query to get the Documents with the requested requested up link
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_UP_LINK + ":" + Long.toString(holdings.getId())
					+ " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + Item.indexedObjectType);

		// Return the list of results
		return new ItemList(query);
	} // end method getByLinkedHoldings(Holdings)

	@Override
	public ItemList getByProcessedFrom(Record processedFrom) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all records that were processed from the record with ID " + processedFrom.getId());

		// Create a query to get the Documents with the requested input for service IDs
		SolrQuery query = new SolrQuery();
		query.setQuery(RecordService.FIELD_PROCESSED_FROM + ":" + Long.toString(processedFrom.getId()) + " AND "
				    +  RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Item.indexedObjectType);

		// Return the list of results
		return new ItemList(query);
	} // end method getByProcessedFrom(long)
	
	@Override
	public Item getItemFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException
	{
		// Create a Item object to store the result
		Item item = Item.buildItemFromRecord(recordService.getRecordFromDocument(doc));

		// Return the item we parsed from the document
		return item;
	} // end method getItemFromDocument(Document)

	@Override
	public Item getBasicItemFromDocument(SolrDocument doc)
	{
		// Create a Item object to store the result
		Item item = Item.buildItemFromRecord(recordService.getBasicRecordFromDocument(doc));

		// Return the item we parsed from the document
		return item;
	} // end method getBasicItemFromDocument(Document)

	@Override
	protected SolrInputDocument setFieldsOnDocument(Item item, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException
	{
		// Set the fields on the record
		return recordService.setFieldsOnDocument(item, doc, generateNewId);
	} // end method setFieldsOnDocument(Item, Document, boolean)
} // end class DefaultItemService
