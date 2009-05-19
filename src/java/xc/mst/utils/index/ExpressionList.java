/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */



package xc.mst.utils.index;

import java.io.IOException;
import java.util.AbstractList;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.solr.common.SolrDocumentList;

import xc.mst.bo.record.Expression;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.record.DefaultExpressionService;
import xc.mst.manager.record.ExpressionService;

/**
 * A list of Records resulting from a Lucene query.  This class maps Lucene's
 * DocumentList Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 *
 * Records in a ExpressionList are all contained in the "Expression" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class ExpressionList extends AbstractList<Expression>
{
	/**
	 * The hits around which the ExpressionList was built
	 */
	private SolrDocumentList documentList = null;

	/**
	 * The service used to get a expression from a Lucene document
	 */
	private static ExpressionService service = new DefaultExpressionService();

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a ExpressionList around the results of a Lucene query.  The hits
	 * are assumed to all be Expression Objects
	 *
	 * @param hits The hits returned by a Lucene query
	 */
	public ExpressionList(SolrDocumentList documentList)
	{
		this.documentList = documentList;
	}

	/**
	 * Gets the expression at a given index
	 *
	 * @param index The index of the Expression to get
	 * @return The expression at the specified index
	 */
	public Expression get(int index)
	{
		try 
		{
			return (documentList != null ? service.getExpressionFromDocument(documentList.get(index)) : null);
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			return null;
		}
	}

	/**
	 * The set method is not used because ExpressionLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Expression set(int index, Expression element)
	{
		throw new UnsupportedOperationException("An attempt was made to set an element on a ExpressionList.  ExpressionLists are read only.");
	}

	/**
	 * Returns the size of the ExpressionList
	 *
	 * @return The size of the ExpressionList
	 */
	public int size()
	{
		return (documentList != null ? documentList.size() : 0);
	}
} // end class ExpressionList
