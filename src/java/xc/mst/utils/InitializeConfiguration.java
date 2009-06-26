/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;

/**
 * Initialize configuration
 *
 * @author Sharmila Ranganathan
 *
 */
public class InitializeConfiguration  extends HttpServlet {

	/**  Eclipse generated id */
	private static final long serialVersionUID = 6847591197004656298L;
	
	/** The logger object */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * Initialize configuration
	 */
	public void init() {

		if (log.isDebugEnabled()) {
			log.debug("Initializing Configuration");
		}
	    
		// The path in the URL(Eg: For URL: http://localhost:8080/MetadataServicesToolkit , returns '/MetadataServicesToolkit')
		String path =  getServletContext().getContextPath();
	    path = path.substring(1, path.length());
	    
	    // Initialize MST configuration.
	    MSTConfiguration.getInstance(path);
	    
		
	}

}
