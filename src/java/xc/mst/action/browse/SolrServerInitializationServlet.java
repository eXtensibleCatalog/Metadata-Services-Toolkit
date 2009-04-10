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
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.constants.Constants;
import xc.mst.manager.record.MSTSolrServer;

import com.opensymphony.xwork2.ActionSupport;

/**
 * A simple servlet that initializes the solr server
 *
 * @author Sharmila Ranganathan
 */
public class SolrServerInitializationServlet extends ActionSupport implements ServletRequestAware
{
	/**
	 * Serial Id
	 */
	private static final long serialVersionUID = 7716796826345895487L;

	/** Request */
	private HttpServletRequest request;

	/** A reference to the logger which writes to the General log file */
	private static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Servlet to initialize SolrServer 
	 * 
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public String execute() throws ServletException, IOException
	{
		if(log.isDebugEnabled()) {
			log.debug("In execute, initialize SolrServer");
		}	
		
		// Get the port on which the request is coming in.  This port
		// is used to create SolrServer
		int port = request.getLocalPort();
		
		MSTSolrServer.getInstance(port);

		if(log.isDebugEnabled()) {
			log.debug("In execute, SolrServer instance : " + MSTSolrServer.getServer());
		}
		
	    return SUCCESS;
	}

	public HttpServletRequest getServletRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.request = servletRequest;
	}


}
