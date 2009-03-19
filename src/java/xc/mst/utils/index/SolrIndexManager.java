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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;

public class SolrIndexManager {

	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Solr server */
	private static SolrServer server = null;

	/**
	 * The singleton instance of the LuceneIndexManager
	 */
	private static SolrIndexManager instance = null;

	/**
	 * Gets the singleton instance of the LuceneIndexManager
	 */
	public static SolrIndexManager getInstance()
	{
		if(instance != null)
			return instance;

		if(log.isDebugEnabled())
			log.debug("Initializing the LuceneIndexManager instance.");
		getSolrServerInstance();
		instance = new SolrIndexManager();
		return instance;
	}

	/**
	 * Get Solr server instance
	 *
	 * @return
	 */
	public static SolrServer getSolrServerInstance() {

		if (server == null) {
			try {
				String url = "http://localhost:8085/solr/";
				server = new CommonsHttpSolrServer( url );
				log.debug("Solar Server::"+ server);
			} catch (MalformedURLException me) {
				log.debug(me);
				//throw new DataException(me.getMessage());

			}
		}
		return server;
	}

	/**
	 * Adds a document to the Lucene index
	 *
	 * @param doc The document to add
	 * @return true on success, false on failure
	 */
	public boolean addDoc(SolrInputDocument doc) throws DataException
	{
		log.debug("Add index to Solr - begin");
		try {
			server.add(doc);
		} catch (SolrServerException se) {
			log.debug(se);
			throw new DataException(se.getMessage());
		} catch (IOException ioe) {
			log.debug(ioe);
			throw new DataException(ioe.getMessage());
		}
		log.debug("Add index to Solr - end");
		return true;
	}

	/**
	 * Adds a document to the Lucene index
	 *
	 * @param doc The document to add
	 * @return true on success, false on failure
	 */
	public boolean commitIndex() throws DataException
	{
		log.debug("Commit index to Solr - begin");
		try {
			server.commit();
		} catch (SolrServerException se) {
			log.debug(se);
			throw new DataException(se.getMessage());
		} catch (IOException ioe) {
			log.debug(ioe);
			throw new DataException(ioe.getMessage());
		}
		log.debug("Commit index to Solr - end");
		return true;
	}

	/**
	 * Method to get search rsult documentss
     *
	 * @param query Query to perform the search
	 * @return Search results
	 */
	public SolrDocumentList getDocumentList(SolrQuery query) {

		SolrDocumentList docs = null;
		try {
		    QueryResponse rsp = server.query( query );

		    docs = rsp.getResults();

		} catch (SolrServerException e) {
				log.debug(e);
		}

		return docs;

	}
}
