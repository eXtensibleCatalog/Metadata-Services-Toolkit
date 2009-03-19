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
import xc.mst.bo.record.Manifestation;
import xc.mst.utils.index.ManifestationList;

/**
 * Lucene implementation of the service class to query, add, update and
 * delete records from an index.
 *
 * Records the ManifestationService interacts with belong to the "Manifestation" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class DefaultManifestationService extends ManifestationService
{
	@Override
	public Manifestation getByXcManifestationId(long manifestationId)
	{
		if(log.isDebugEnabled())
			log.debug("Getting the record with XC manifestation ID " + manifestationId);

		// Create a query to get the Documents with the requested XC work ID
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_FRBR_LEVEL_ID + ":" + Long.toString(manifestationId) + " AND "
				+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":"+ Manifestation.indexedObjectType);

		// Get the result of the query
		SolrDocumentList docs = null;

		docs = indexMgr.getDocumentList(query);

		// Return null if we couldn't find the work with the correct XC manifestation ID
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the work with XC manifestation ID " + manifestationId + ".");

			return null;
		} // end if(no result found)

		if(log.isDebugEnabled())
			log.debug("Parcing the work with XC manifestation ID " + manifestationId + " from the Lucene Document it was stored in.");

		return getManifestationFromDocument(docs.get(0));
	} // end method getByXcManifestationId(long)

	@Override
	public ManifestationList getByXcRecordId(String xcRecordId)
	{
		String trait = Manifestation.TRAIT_RECORD_ID + ":" + xcRecordId;

		if(log.isDebugEnabled())
			log.debug("Getting all manifestations with trait " + trait);

		// Create a query to get the Documents with the requested trait
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_TRAIT + ":" + trait + " AND "
				+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Manifestation.indexedObjectType);

		// Get the result of the query
		SolrDocumentList docs = indexMgr.getDocumentList(query);

		// Return the empty list if we couldn't find the manifestations
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find any manifestations with trait " + trait + ".");

			return new ManifestationList(null);
		} // end if(no results found)

		if(log.isDebugEnabled())
			log.debug("Parcing the " + docs.size() + " manifestations with trait " + trait + " from the Lucene Documents they were stored in.");

		// Return the list of results
		return new ManifestationList(docs);
	} // end method getByXcRecordId(String)

	@Override
	public ManifestationList getByLinkedExpression(Expression expression)
	{
		if(log.isDebugEnabled())
			log.debug("Getting all manifestations linked to the expression with ID " + expression.getId());

		// Create a query to get the Documents with the requested requested up link
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_UP_LINK + ":" + Long.toString(expression.getId()) + " AND "
				+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Holdings.indexedObjectType );

		// Get the result of the query
		SolrDocumentList docs = indexMgr.getDocumentList(query);

		// Return the empty list if we couldn't find any matching manifestations
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the any manifestations linked to the expression with ID " + expression.getId() + ".");

			return new ManifestationList(null);
		} // end if(no results found)

		if(log.isDebugEnabled())
			log.debug("Parcing the " + docs.size() + " manifestations linked to the expression with ID " + expression.getId() + " from the Lucene Documents they were stored in.");

		// Return the list of results
		return new ManifestationList(docs);
	} // end method getByLinkedExpression(Expression)

	@Override
	public Manifestation getManifestationFromDocument(SolrDocument doc)
	{
		// Return the Record in the document as a Manifestation
		return Manifestation.buildManifestationFromRecord(recordService.getRecordFromDocument(doc));
	} // end method getManifestationFromDocument(Document)

	@Override
	public Manifestation getBasicManifestationFromDocument(SolrDocument doc)
	{
		// Return the Record in the document as a Manifestation
		return Manifestation.buildManifestationFromRecord(recordService.getRecordFromDocument(doc));
	} // end method getBasicManifestationFromDocument(Document)

	@Override
	protected SolrInputDocument setFieldsOnDocument(Manifestation manifestation, SolrInputDocument doc, boolean generateNewId)
	{
		// Set the fields on the record
		return recordService.setFieldsOnDocument(manifestation, doc, generateNewId);
	} // end method setFieldsOnDocument(Manifestation, Document, boolean)
}// end class DefaultManifestationService
