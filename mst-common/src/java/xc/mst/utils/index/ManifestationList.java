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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;

import xc.mst.bo.record.Manifestation;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.ManifestationService;
import xc.mst.utils.MSTConfiguration;

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
	 * The maximum number of results to 
	 */
	private static final int MAX_RESULTS = 2048;
	
	/**
	 * An Object which manages the Solr index
	 */
	protected SolrIndexManager indexMgr = (SolrIndexManager)MSTConfiguration.getInstance().getBean("SolrIndexManager");
	
	/**
	 * The current offset into the results of the query that are in the document list
	 */
	private int currentOffset = 0;
	
	/**
	 * A list of documents from the query between results currentOffset and currentOffset+MAX_RESULTS
	 */
	private SolrDocumentList docs = null;
	
	/**
	 * The query for which the ManifestationList was built
	 */
	private SolrQuery query = null;

	/**
	 * The service used to get a record from a Lucene document
	 */
	private static ManifestationService service = (ManifestationService)MSTConfiguration.getInstance().getBean("ManifestationService");
	
	/**
	 * The number of elements in the list
	 */
	private int size = -1;

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Constructs a ManifestationList around a Solr query.  The docs returned by the query
	 * are assumed to all be Manifestation Objects
	 *
	 * @param query The Solr query for which the ManifestationList was built
	 */
	public ManifestationList(SolrQuery query) throws IndexException
	{
		this.query = query;
		query.setRows(MAX_RESULTS);
		query.setStart(currentOffset);
		docs = indexMgr.getDocumentList(query);
	}

	/**
	 * Gets the record at a given index
	 *
	 * @param index The index of the Record to get
	 * @return The record at the specified index
	 */
	public Manifestation get(int index)
	{
		try
		{
			if(query == null)
				return null;
			
			if(currentOffset < index && currentOffset + MAX_RESULTS > index)
			{
				if(docs == null)
					return null;
				
				return (docs.size() > (index-currentOffset) ? service.getManifestationFromDocument(docs.get(index-currentOffset)) : null);
			}
			
			// Truncation will make this the largest multiple of MAX_RESULTS which comes before the requested index
			currentOffset = (index/MAX_RESULTS)*MAX_RESULTS;
			
			query.setRows(MAX_RESULTS);
			query.setStart(currentOffset);
			docs = indexMgr.getDocumentList(query);
			
			if(docs == null)
				return null;
			
			return (docs.size() > (index-currentOffset) ? service.getManifestationFromDocument(docs.get(index-currentOffset)) : null);
		}
		catch(DatabaseConfigException e)
		{
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			return null;
		} 
		catch(IndexException ie) 
		{
			log.error("Cannot connect to Solr Server. Check the port in configuration file.", ie);
			
			return null;
		}
	}

	/**
	 * The set method is not used because ManifestationLists are read only.  It is
	 * only included because it is required to extend the AbstractList class.
	 *
	 * @throws UnsupportedOperationException Whenever this method is called
	 */
	public Manifestation set(int index, Record element)
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
		if(size >= 0)
			return size;
		
		if(query == null)
		{
			size = 0;
			return size;
		}
		
		// Binary search to find the size of the list
		int low = 0;
		int high = Integer.MAX_VALUE;
		int mid;
		
		while(low <= high)
		{
			mid = (low + high) / 2;
			
			if(mid == 0)
				return 0;
			
			if(get(mid-1) != null)
			{
				if(get(mid) == null)
				{
					size = mid;
					return size;
				}
				else
					low = mid+1;
			}
			else
			{
				if(mid == 1)
					return 0;
				
				if(get(mid-2) != null)
				{
					size = mid-1;
					return size;
				}
				else
					high = mid;
			}
		}
		
		return -1;
	}
} // end class ManifestationList
