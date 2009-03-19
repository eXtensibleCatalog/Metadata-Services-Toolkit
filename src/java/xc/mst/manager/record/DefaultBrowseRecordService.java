/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.record;

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

import xc.mst.bo.record.Record;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.constants.Constants;

/**
 * Browse for results using solr
 *
 * @author Sharmila Ranganathan
 *
 */
public class DefaultBrowseRecordService implements BrowseRecordService {

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Solr server */
	private SolrServer server;

	/**
	 * Get server instance
	 */
	private void getServer()  {

		try {
			String url = "http://localhost:8085/solr/";
			server = new CommonsHttpSolrServer( url );
			log.debug("Solar Server::"+ server);
		} catch (MalformedURLException me) {
			log.debug(me);
			//throw new DataException(me.getMessage());

		}
	}

	/**
	 * Method to search the index for records
     *
	 * @param query Query to perform the search
	 * @return Search results
	 */
	public SolrBrowseResult search(SolrQuery query) {

		getServer();
		SolrBrowseResult result = null;

		try {
		    QueryResponse rsp = server.query( query );

		    // Load the records in the SolrBrowseResilt object
		    SolrDocumentList docs = rsp.getResults();

		    RecordService recordService = new DefaultRecordService();
		    Iterator<SolrDocument> iteration = docs.iterator();
		    List<Record> records = new ArrayList<Record>();

		    while(iteration.hasNext()) {

		    	records.add(recordService.getRecordFromSolrDocument(iteration.next()));
		    }

		    // Load the facets in the SolrBrowseResilt object
		    List<FacetField> facets = rsp.getFacetFields();

		    result = new SolrBrowseResult(records, facets);
		    result.setTotalNumberOfResults((int)docs.getNumFound());

		} catch (SolrServerException e) {
				log.debug(e);
		}

		return result;

	}

}
