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

import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.WorkList;

/**
 * Lucene implementation of the service class to query, add, update and
 * delete records from an index.
 *
 * Records the WorkService interacts with belong to the "Work" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class DefaultWorkService extends WorkService
{
	@Override
	public Work getByXcWorkId(long workId) throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting the record with XC work ID " + workId);

		// Create a query to get the Documents with the requested XC work ID
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_FRBR_LEVEL_ID + ":" + Long.toString(workId) + " AND "
				+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Work.indexedObjectType);

		// Get the result of the query
		SolrDocumentList docs = null;

		docs = indexMgr.getDocumentList(query);

		// Return null if we couldn't find the work with the correct XC work ID
		if(docs == null)
		{
			if(log.isDebugEnabled())
				log.debug("Could not find the work with XC work ID " + workId + ".");

			return null;
		} // end if(no result found)

		if(log.isDebugEnabled())
			log.debug("Parcing the work with XC work ID " + workId + " from the Lucene Document it was stored in.");

		return getWorkFromDocument(docs.get(0));
	} // end method getByXcWorkId(long)

	@Override
	public WorkList getByIdentifierForTheWork(String identifierForTheWork) throws IndexException
	{
		String trait = Work.TRAIT_IDENTIFIER_FOR_THE_WORK + ":" + identifierForTheWork;

		if(log.isDebugEnabled())
			log.debug("Getting all works with trait " + trait);

		// Create a query to get the Documents with the requested trait
		SolrQuery query = new SolrQuery();
		query.setQuery(DefaultRecordService.FIELD_TRAIT + ":" + trait + " AND "
				+  RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Work.indexedObjectType);

		// Return the list of results
		return new WorkList(query);
	} // end method getByIdentifierForTheWork(String)

	@Override
	public WorkList getByProcessedFrom(Record processedFrom) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all records that were processed from the record with ID " + processedFrom.getId());

		// Create a query to get the Documents with the requested input for service IDs
		SolrQuery query = new SolrQuery();
		query.setQuery(RecordService.FIELD_PROCESSED_FROM + ":" + Long.toString(processedFrom.getId()) + " AND "
				    +  RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Work.indexedObjectType);

		// Return the list of results
		return new WorkList(query);
	} // end method getByProcessedFrom(long)
	
	@Override
	public WorkList getUnprocessedWorks(int serviceId) throws IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Getting all unprocessed works.");

		// Create a query to get the Documents with the requested trait
		SolrQuery query = new SolrQuery();
		query.setQuery(FIELD_PROCESSED + ":false AND "
				+ RecordService.FIELD_INDEXED_OBJECT_TYPE + ":" + Work.indexedObjectType);

		// Return the list of results
		return new WorkList(query);
	} // end method getUnprocessendWorks()

	@Override
	public Work getWorkFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException
	{
		// Create a Work object to store the result
		Work work = Work.buildWorkFromRecord(recordService.getRecordFromDocument(doc));

		// Get whether or not the work was processed
		work.setProcessed(Boolean.parseBoolean((String)doc.getFieldValue(FIELD_PROCESSED)));

		// Return the work we parsed from the document
		return work;
	} // end method getWorkFromDocument(Document)

	@Override
	public Work getBasicWorkFromDocument(SolrDocument doc)
	{
		// Create a Work object to store the result
		Work work = Work.buildWorkFromRecord(recordService.getBasicRecordFromDocument(doc));

		// Get whether or not the work was processed
		work.setProcessed(Boolean.parseBoolean((String)doc.getFieldValue(FIELD_PROCESSED)));

		// Return the work we parsed from the document
		return work;
	} // end method getBasicWorkFromDocument(Document)

	@Override
	protected SolrInputDocument setFieldsOnDocument(Work work, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException
	{
		// Set the fields on the record
		doc = recordService.setFieldsOnDocument(work, doc, generateNewId);

		// Set whether or not the work was processed
		doc.addField(FIELD_PROCESSED, Boolean.toString(work.getProcessed()));

		return doc;
	} // end method setFieldsOnDocument(Work, Document, boolean)
	
	
} // end class DefaultWorkService
