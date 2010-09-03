/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.record;

import org.apache.solr.client.solrj.SolrQuery;

import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.bo.service.ErrorCode;
import xc.mst.bo.service.Service;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;

/**
 * Browse for records
 *
 * @author Sharmila Ranganathan
 *
 */
public interface BrowseRecordService {

	/**
	 * Method to search the index for records
     *
	 * @param query Query to perform the search
	 * @return Search results
	 * @throws DatabaseConfigException 
	 */
	public SolrBrowseResult search(SolrQuery query) throws IndexException, DatabaseConfigException;
	
	/**
	 * Get error description for this code and service
	 * 
	 * @param errorCode Error code
	 * @param service Service which generated the error
	 * @return Error if found
	 * @throws DatabaseConfigException 
	 */
	public ErrorCode getError(String errorCode, Service service) throws DatabaseConfigException; 

}
