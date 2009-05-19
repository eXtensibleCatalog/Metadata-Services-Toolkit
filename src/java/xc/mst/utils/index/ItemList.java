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

import xc.mst.bo.record.Item;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.record.DefaultItemService;
import xc.mst.manager.record.ItemService;

/**
 * A list of Records resulting from a Lucene query.  This class maps Lucene's
 * Hits Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 *
 * Records in a ItemList are all contained in the "Item" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class ItemList extends AbstractList<Item>
{
	/**
	 * The docs around which the ItemList was built
	 */
	private SolrDocumentList docs = null;

	/**
	 * The service used to get a item from a Lucene document
	 */
	private static ItemService service = new DefaultItemService();

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a ItemList around the results of a Lucene query.  The docs
	 * are assumed to all be Item Objects
	 *
	 * @param docs The docs returned by a Lucene query
	 */
	public ItemList(SolrDocumentList docs)
	{
		this.docs = docs;
	}

	/**
	 * Gets the item at a given index
	 *
	 * @param index The index of the Item to get
	 * @return The item at the specified index
	 */
	public Item get(int index)
	{
		try 
		{
			return (docs != null ? service.getItemFromDocument(docs.get(index)) : null);
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			return null;
		}
	}

	/**
	 * The set method is not used because ItemLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Item set(int index, Item element)
	{
		throw new UnsupportedOperationException("An attempt was made to set an element on a ItemList.  ItemLists are read only.");
	}

	/**
	 * Returns the size of the ItemList
	 *
	 * @return The size of the ItemList
	 */
	public int size()
	{
		return (docs != null ? docs.size() : 0);
	}
} // end class ItemList
