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
import xc.mst.bo.record.Work;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.utils.index.ExpressionList;

/**
 * Lucene implementation of the service class to query, add, update and
 * delete records from an index.
 *
 * Records the ExpressionService interacts with belong to the "Expression" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class DefaultExpressionService extends ExpressionService
{
	@Override
	public Expression getByXcExpressionId(long expressionId) throws DatabaseConfigException
	{
		if(log.isDebugEnabled())
			log.debug("Getting the record with XC expression ID " + expressionId);

		// Create a query to get the Documents with the requested XC expression ID
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_FRBR_LEVEL_ID + ":" + Long.toString(expressionId) + " AND "
			+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Expression.indexedObjectType);

		// Get the result of the query
		SolrDocumentList doc = null;

		doc = indexMgr.getDocumentList(query);

		// Return null if we couldn't find the expression with the correct XC expression ID
		if(doc == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the expression with XC expression ID " + expressionId + ".");

			return null;
		} // end if(no result found)

		if(log.isDebugEnabled())
			log.debug("Parcing the exrpession with XC exrpession ID " + expressionId + " from the Lucene Document it was stored in.");

		return getExpressionFromDocument(doc.get(0));
	} // end method getByXcExpressionId(long)

	@Override
	public ExpressionList getByLinkedWork(Work work)
	{
		if(log.isDebugEnabled())
			log.debug("Getting all expressions linked to the work with ID " + work.getId());

		// Create a query to get the Documents with the requested requested up link
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_UP_LINK + ":" + Long.toString(work.getId()) + " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Expression.indexedObjectType);

		// Get the result of the query
		SolrDocumentList docs = indexMgr.getDocumentList(query);

		// Return the empty list if we couldn't find any matching expressions
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the any expressions linked to the work with ID " + work.getId() + ".");

			return new ExpressionList(null);
		} // end if(no results found)

		if(log.isDebugEnabled())
			log.debug("Parcing the " + docs.size() + " expressions linked to the work with ID " + work.getId() + " from the Lucene Documents they were stored in.");

		// Return the list of results
		return new ExpressionList(docs);
	} // end method getByLinkedManifestation(Manifestation)

	@Override
	public Expression getExpressionFromDocument(SolrDocument doc) throws DatabaseConfigException
	{
		// Create a Expression object to store the result
		Expression expression = Expression.buildExpressionFromRecord(recordService.getRecordFromDocument(doc));

		// Return the expression we parsed from the document
		return expression;
	} // end method getExpressionFromDocument(Document)

	@Override
	public Expression getBasicExpressionFromDocument(SolrDocument doc)
	{
		// Create a Expression object to store the result
		Expression expression = Expression.buildExpressionFromRecord(recordService.getBasicRecordFromDocument(doc));

		// Return the Expression we parsed from the document
		return expression;
	} // end method getBasicExpressionFromDocument(Document)

	@Override
	protected SolrInputDocument setFieldsOnDocument(Expression expression, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException
	{
		// Set the fields on the record and return the results
		return recordService.setFieldsOnDocument(expression, doc, generateNewId);
	} // end method setFieldsOnDocument(Expression, Document, boolean)
} // end class DefaultExpressionService
