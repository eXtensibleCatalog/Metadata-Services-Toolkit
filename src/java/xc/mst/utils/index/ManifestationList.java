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

import xc.mst.bo.record.Manifestation;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.record.DefaultManifestationService;
import xc.mst.manager.record.ManifestationService;

/**
 * A list of Records resulting from a Lucene query.  This class maps Lucene's
 * Hits Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 *
 * Records in a ManifestationList are all contained in the "Manifestation" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class ManifestationList extends AbstractList<Manifestation>
{
	/**
	 * The hits around which the ManifestationList was built
	 */
	private SolrDocumentList docs = null;

	/**
	 * The service used to get a manifestation from a Lucene document
	 */
	private static ManifestationService service = new DefaultManifestationService();

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a ManifestationList around the results of a Lucene query.  The docs
	 * are assumed to all be Manifestation Objects
	 *
	 * @param docs The docs returned by a Lucene query
	 */
	public ManifestationList(SolrDocumentList docs)
	{
		this.docs = docs;
	}

	/**
	 * Gets the manifestation at a given index
	 *
	 * @param index The index of the Manifestation to get
	 * @return The manifestation at the specified index
	 */
	public Manifestation get(int index)
	{
		try 
		{
			return (docs != null ? service.getManifestationFromDocument(docs.get(index)) : null);
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			return null;
		}
	}

	/**
	 * The set method is not used because ManifestationLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Manifestation set(int index, Manifestation element)
	{
		throw new UnsupportedOperationException("An attempt was made to set an element on a ManifestationList.  ManifestationLists are read only.");
	}

	/**
	 * Returns the size of the ManifestationList
	 *
	 * @return The size of the ManifestationList
	 */
	public int size()
	{
		return (docs != null ? docs.size() : 0);
	}
} // end class ManifestationList
