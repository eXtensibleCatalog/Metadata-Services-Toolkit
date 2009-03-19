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

import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;

/**
 * A list of Records resulting from a Lucene query.  This class maps Lucene's
 * Hits Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 *
 * @author Eric Osisek
 */
public class RecordList extends AbstractList<Record>
{
	/**
	 * The docs around which the RecordList was built
	 */
	private SolrDocumentList docs = null;

	/**
	 * The service used to get a record from a Lucene document
	 */
	private static RecordService service = new DefaultRecordService();

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a RecordList around the results of a Lucene query.  The docs
	 * are assumed to all be Record Objects
	 *
	 * @param docs The docs returned by a Lucene query
	 */
	public RecordList(SolrDocumentList docs)
	{
		this.docs = docs;
	}

	/**
	 * Gets the record at a given index
	 *
	 * @param index The index of the Record to get
	 * @return The record at the specified index
	 */
	public Record get(int index)
	{
		return (docs != null ? service.getRecordFromDocument(docs.get(index)) : null);
	}

	/**
	 * The set method is not used because RecordLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Record set(int index, Record element)
	{
		throw new UnsupportedOperationException("An attempt was made to set an element on a RecordList.  RecordLists are read only.");
	}

	/**
	 * Returns the size of the RecordList
	 *
	 * @return The size of the RecordList
	 */
	public int size()
	{
		return (docs != null ? docs.size() : 0);
	}
}
