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

import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Manifestation;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.HoldingsList;

/**
 * Lucene implementation of the service class to query, add, update and
 * delete records from an index.
 *
 * Records the HoldingsService interacts with belong to the "Holdings" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class DefaultHoldingsService extends HoldingsService
{
	@Override
	public Holdings getByXcHoldingsId(long holdingsId) throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting the record with XC holdings ID " + holdingsId);

		// Create a query to get the Documents with the requested XC holdings ID
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.TERM_FRBR_LEVEL_ID + ":" +  Long.toString(holdingsId) + " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Holdings.indexedObjectType);

		// Get the result of the query
		SolrDocumentList doc = null;
		doc = indexMgr.getDocumentList(query);

		// Return null if we couldn't find the holdings with the correct XC holdings ID
		if(doc == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the holdings with XC holdings ID " + holdingsId + ".");

			return null;
		} // end if(no result found)

		if(log.isDebugEnabled())
			log.debug("Parcing the holdings with XC holdings ID " + holdingsId + " from the Lucene Document it was stored in.");

		return getHoldingsFromDocument(doc.get(0));
	} // end method getByXcHoldingsId(long)

	@Override
	public HoldingsList getByXcRecordId(String recordId) throws IndexException
	{
		String trait = Holdings.TRAIT_RECORD_ID + ":" + recordId;

		if(log.isDebugEnabled())
			log.debug("Getting all holdings with trait " + trait);

		// Create a query to get the Documents with the requested trait
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_TRAIT + ":" + trait + " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Holdings.indexedObjectType);

		// Get the result of the query
		SolrDocumentList docs = indexMgr.getDocumentList(query);

		// Return the empty list if we couldn't find the holdings
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find any holdings with trait " + trait + ".");

			return new HoldingsList(null);
		} // end if(no results found)

		if(log.isDebugEnabled())
			log.debug("Parcing the " + docs.size() + " holdings with trait " + trait + " from the Lucene Documents they were stored in.");

		// Return the list of results
		return new HoldingsList(docs);
	} // end method getByXcRecordId(String)

	@Override
	public HoldingsList getByManifestationHeld(String manifestationHeld) throws IndexException
	{
		String trait = (Holdings.TRAIT_MANIFESTATION_HELD + ":" + manifestationHeld).replaceAll(":", "\\\\:");

		if(log.isDebugEnabled())
			log.debug("Getting all holdings with trait " + trait);

		// Create a query to get the Documents with the requested trait
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_TRAIT + ":" + trait + " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Holdings.indexedObjectType);

		// Get the result of the query
		SolrDocumentList docs = indexMgr.getDocumentList(query);

		// Return the empty list if we couldn't find the holdings
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find any holdings with trait " + trait + ".");

			return new HoldingsList(null);
		} // end if(no results found)

		if(log.isDebugEnabled())
			log.debug("Parcing the " + docs.size() + " holdings with trait " + trait + " from the Lucene Documents they were stored in.");

		// Return the list of results
		return new HoldingsList(docs);
	} // end method getByManifestationHeld(String)

	@Override
	public HoldingsList getByLinkedManifestation(Manifestation manifestation) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all holdings linked to the manifestation with ID " + manifestation.getId());

		// Create a query to get the Documents with the requested requested up link
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_UP_LINK + ":" + Long.toString(manifestation.getId())
				+ " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Holdings.indexedObjectType);


		// Get the result of the query
		SolrDocumentList docs = indexMgr.getDocumentList(query);

		// Return the empty list if we couldn't find any matching holdings
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the any holdings linked to the manifestation with ID " + manifestation.getId() + ".");

			return new HoldingsList(null);
		} // end if(no results found)

		if(log.isDebugEnabled())
			log.debug("Parcing the " + docs.size() + " holdings linked to the manifestation with ID " + manifestation.getId() + " from the Lucene Documents they were stored in.");

		// Return the list of results
		return new HoldingsList(docs);
	} // end method getByLinkedManifestation(Manifestation)

	@Override
	public Holdings getHoldingsFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException
	{
		// Create a Holdings object to store the result
		Holdings holdings = Holdings.buildHoldingsFromRecord(recordService.getRecordFromDocument(doc));

		// Return the holdings we parsed from the document
		return holdings;
	} // end method getHoldingsFromDocument(Document)

	@Override
	public Holdings getBasicHoldingsFromDocument(SolrDocument doc)
	{
		// Create a Holdings object to store the result
		Holdings holdings = Holdings.buildHoldingsFromRecord(recordService.getBasicRecordFromDocument(doc));

		// Return the holdings we parsed from the document
		return holdings;
	} // end method getBasicHoldingsFromDocument(Document)

	@Override
	protected SolrInputDocument setFieldsOnDocument(Holdings holdings, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException
	{
		// Set the fields on the record
		return recordService.setFieldsOnDocument(holdings, doc, generateNewId);
	} // end method setFieldsOnDocument(Holdings, Document, boolean)
} // end class DefaultHoldingsService
