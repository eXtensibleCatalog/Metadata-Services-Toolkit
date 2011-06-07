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
import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.ExpressionList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * Service class to query, add, update and delete records from an index.
 * Records the ExpressionService interacts with belong to the "Expression" bucket used
 * by the Aggregation Service
 *
 * @author Eric Osisek
 */
public abstract class ExpressionService extends BaseService
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
	 * Gets the Expression that matches the passed XC expression ID
	 *
	 * @param expressionId The XC expression ID of the target expression element
	 * @throws DatabaseConfigException
	 */
	public abstract Expression getByXcExpressionId(long expressionId) throws DatabaseConfigException, IndexException;

	/**
	 * Gets a list of all Expressions linked to the passed work
	 *
	 * @param work The work whose linked Expressions should be returned
	 */
	public abstract ExpressionList getByLinkedWork(Work work) throws IndexException;

	/**
	 * Gets all expressions from the index which have been processed from the specified record
	 *
	 * @param processedFrom The ID of the original record whose processed Records we're getting
	 * @return A list of all records in the index which have been processed from the specified record
	 */
	public abstract ExpressionList getByProcessedFrom(Record processedFrom) throws IndexException;
	
	/**
	 * Inserts a expression into the index
	 *
	 * @param expression The expression to insert
	 * @return true on success, false on failure
	 */
	public boolean insert(Expression expression) throws DataException, IndexException
	{
		// Check that the non-ID fields on the expression are valid
		validateFields(expression, false, true);

		if(log.isDebugEnabled())
			log.debug("Inserting a new " + expression.getType());

		expression.setCreatedAt(new Date());

		// Create a Document object and set it's type field
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, expression.getType());

		// Set up the fields for the specific type of indexed object
		doc = setFieldsOnDocument(expression, doc, true);

		SolrIndexManager sim = (SolrIndexManager)config.getBean("SolrIndexManager");
		return sim.addDoc(doc);
	} // end method insert(Expression)

	/**
	 * Updates a expression in the index
	 *
	 * @param expression The expression to update
	 * @return true on success, false on failure
	 */
	public boolean update(Expression expression) throws DataException, IndexException
	{
		// Check that the fields on the expression are valid
		validateFields(expression, true, true);

		if(log.isDebugEnabled())
			log.debug("Updating the expression with ID " + expression.getId());

		// Set the updated at timestamp to now
		expression.setUpdatedAt(new Date());

		// Set up a Document Object to insert the updated set into the Lucene index
		// Create a Document object and set it's type field

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_INDEXED_OBJECT_TYPE, expression.getType());

		// Set up the fields for the Expression
		doc = setFieldsOnDocument(expression, doc, false);

		SolrIndexManager sim = (SolrIndexManager)config.getBean("SolrIndexManager");
		return sim.addDoc(doc);
	} // end method update(Expression)

	/**
	 * Deletes a expression from the index
	 *
	 * @param expression The expression to delete
	 * @return true on success, false on failure
	 */
	public boolean delete(Expression expression) throws DataException
	{
		// Check that the ID field on the expression are valid
		validateFields(expression, true, false);

		if(log.isDebugEnabled())
			log.debug("Deleting the expression with ID " + expression.getId());

		// TODO delete implementation
		// Delete all expressions with the matching expression ID
		boolean result = false;
		//boolean result = indexMgr.deleteDoc(FIELD_RECORD_ID, Long.toString(expression.getId()));

		// Return the result of the delete
		return result;
	} // end method delete(Expression)

	/**
	 * Parses a Expression from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Expression.
	 * @return The expression which was contained in the passed Document.
	 * @throws DatabaseConfigException
	 */
	public abstract Expression getExpressionFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException;

	/**
	 * Parses a Expression from the fields in a Document from the index.
	 *
	 * @param doc The document containing information on the Expression.
	 * @return The expression which was contained in the passed Document.
	 */
	public abstract Expression getBasicExpressionFromDocument(SolrDocument doc);

	/**
	 * Sets the fields on the document which need to be stored in the
	 * index.
	 *
	 * @param expression The expression to use to set the fields on the document
	 * @param doc The document whose fields need to be set.
	 * @param generateNewId True to generate a new record ID for the expression, false to use the expression's current ID
	 * @return A reference to the Document after its fields have been set
	 * @throws DatabaseConfigException
	 */
	protected abstract SolrInputDocument setFieldsOnDocument(Expression expression, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException;

	/**
	 * Validates the fields on the passed Expression Object
	 *
	 * @param expression The expression to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed expression were invalid
	 */
	protected void validateFields(Expression expression, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(expression.getId() < 0)
				errorMessage.append("The expression's id is invalid. ");
		} // end if(we should check the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(expression.getFormat() == null)
				errorMessage.append("The expression's format is invalid. ");

		} // end if(we should check the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(we found an error)
	} // end method validateFields(Expression, boolean, boolean)
} // end class ExpressionService
