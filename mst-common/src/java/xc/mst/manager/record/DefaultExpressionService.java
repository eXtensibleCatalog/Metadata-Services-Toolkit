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
import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.index.ExpressionList;
import xc.mst.utils.index.SolrIndexManager;

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
	public Expression getByXcExpressionId(long expressionId) throws DatabaseConfigException, IndexException
	{
		SolrIndexManager sim = (SolrIndexManager)MSTConfiguration.getBean("SolrIndexManager");
		
		if(log.isDebugEnabled())
			log.debug("Getting the record with XC expression ID " + expressionId);

		// Create a query to get the Documents with the requested XC expression ID
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_FRBR_LEVEL_ID + ":" + Long.toString(expressionId) + " AND "
			+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Expression.indexedObjectType);

		// Get the result of the query
		SolrDocumentList doc = null;

		doc = sim.getDocumentList(query);

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
	public ExpressionList getByLinkedWork(Work work) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all expressions linked to the work with ID " + work.getId());

		// Create a query to get the Documents with the requested requested up link
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_UP_LINK + ":" + Long.toString(work.getId()) + " AND " + RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Expression.indexedObjectType);

		// Return the list of results
		return new ExpressionList(query);
	} // end method getByLinkedManifestation(Manifestation)

	@Override
	public ExpressionList getByProcessedFrom(Record processedFrom) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all records that were processed from the record with ID " + processedFrom.getId());

		// Create a query to get the Documents with the requested input for service IDs
		SolrQuery query = new SolrQuery();
		query.setQuery(RecordService.FIELD_PROCESSED_FROM + ":" + Long.toString(processedFrom.getId()) + " AND "
				    +  RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Expression.indexedObjectType);

		// Return the list of results
		return new ExpressionList(query);
	} // end method getByProcessedFrom(long)
	
	@Override
	public Expression getExpressionFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException
	{
		// Create a Expression object to store the result
		RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");
		Expression expression = Expression.buildExpressionFromRecord(recordService.getRecordFromDocument(doc));

		// Return the expression we parsed from the document
		return expression;
	} // end method getExpressionFromDocument(Document)

	@Override
	public Expression getBasicExpressionFromDocument(SolrDocument doc)
	{
		// Create a Expression object to store the result
		RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");
		Expression expression = Expression.buildExpressionFromRecord(recordService.getBasicRecordFromDocument(doc));

		// Return the Expression we parsed from the document
		return expression;
	} // end method getBasicExpressionFromDocument(Document)

	@Override
	protected SolrInputDocument setFieldsOnDocument(Expression expression, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException
	{
		// Set the fields on the record and return the results
		RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");
		return recordService.setFieldsOnDocument(expression, doc, generateNewId);
	} // end method setFieldsOnDocument(Expression, Document, boolean)
} // end class DefaultExpressionService
