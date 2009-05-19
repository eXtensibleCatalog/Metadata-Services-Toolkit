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

import xc.mst.bo.record.Holdings;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.record.DefaultHoldingsService;
import xc.mst.manager.record.HoldingsService;

/**
 * A list of Records resulting from a Lucene query.  This class maps Lucene's
 * Hits Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 *
 * Records in a HoldingsList are all contained in the "Holdings" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class HoldingsList extends AbstractList<Holdings>
{
	/**
	 * The hits around which the HoldingsList was built
	 */
	private SolrDocumentList docs = null;

	/**
	 * The service used to get a holdings from a Lucene document
	 */
	private static HoldingsService service = new DefaultHoldingsService();

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a HoldingsList around the results of a Lucene query.  The hits
	 * are assumed to all be Holdings Objects
	 *
	 * @param hits The hits returned by a Lucene query
	 */
	public HoldingsList(SolrDocumentList docs)
	{
		this.docs = docs;
	}

	/**
	 * Gets the holdings at a given index
	 *
	 * @param index The index of the Holdings to get
	 * @return The holdings at the specified index
	 */
	public Holdings get(int index)
	{
		try 
		{
			return (docs != null ? service.getHoldingsFromDocument(docs.get(index)) : null);
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			return null;
		}
	}

	/**
	 * The set method is not used because HoldingsLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Holdings set(int index, Holdings element)
	{
		throw new UnsupportedOperationException("An attempt was made to set an element on a HoldingsList.  HoldingsLists are read only.");
	}

	/**
	 * Returns the size of the HoldingsList
	 *
	 * @return The size of the HoldingsList
	 */
	public int size()
	{
		return (docs != null ? docs.size() : 0);
	}
} // end class HoldingsList
