/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.browse;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.manager.record.MSTSolrServer;

/**
 * A simple servlet that initializes the solr server
 *
 * @author Sharmila Ranganathan
 */
public class SolrServerInitializationServlet extends HttpServlet
{
	/**
	 * Serial Id
	 */
	private static final long serialVersionUID = 7716796826345895487L;

	/** A reference to the logger which writes to the General log file */
	private static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Servlet to initialize SolrServer 
	 * 
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void init() {
	
		if(log.isDebugEnabled()) {
			log.debug("In execute, initialize SolrServer");
		}	
		
		// Get the port on which the request is coming in.  This port
		// is used to create SolrServer
		MSTSolrServer.getInstance();

		if(log.isDebugEnabled()) {
			log.debug("In execute, SolrServer instance : " + MSTSolrServer.getServer());
		}
	    
	}




}
