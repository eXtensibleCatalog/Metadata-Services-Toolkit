/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils.index;

import java.util.AbstractList;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;

import xc.mst.bo.record.Work;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultWorkService;
import xc.mst.manager.record.WorkService;

/**
 * A list of Records resulting from a Lucene query.  This class maps Lucene's
 * Hits Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 *
 * Records in a WorkList are all contained in the "Work" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class WorkList extends AbstractList<Work>
{
	/**
	 * The docs around which the WorkList was built
	 */
	private SolrDocumentList docs = null;

	/**
	 * The service used to get a work from a Lucene document
	 */
	private static WorkService service = new DefaultWorkService();

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a WorkList around the results of a Lucene query.  The docs
	 * are assumed to all be Work Objects
	 *
	 * @param docs The docs returned by a Lucene query
	 */
	public WorkList(SolrDocumentList docs)
	{
		this.docs = docs;
	}

	/**
	 * Gets the work at a given index
	 *
	 * @param index The index of the Work to get
	 * @return The work at the specified index
	 */
	public Work get(int index)
	{
		try 
		{
			return (docs != null ? service.getWorkFromDocument(docs.get(0)) : null);
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			return null;
		} catch(IndexException ie) {
			log.error("Cannot connect to Solr Server. Check the port in configuration file.", ie);
			return null;
		}
	}

	/**
	 * The set method is not used because WorkLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Work set(int index, Work element)
	{
		throw new UnsupportedOperationException("An attempt was made to set an element on a WorkList.  WorkLists are read only.");
	}

	/**
	 * Returns the size of the WorkList
	 *
	 * @return The size of the WorkList
	 */
	public int size()
	{
		return (docs != null ? docs.size() : 0);
	}
} // end class WorkList
