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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;

/**
 * Initialize log
 *
 * @author Sharmila Ranganathan
 *
 */
public class InitializeLog  extends HttpServlet {

	/**
	 * Eclipse generated id
	 */
	private static final long serialVersionUID = 6847591197004656298L;

	/**
	 * Initialize logging
	 */
	public void init() {

	    String prefix =  getServletContext().getRealPath("/");
	    String file = getInitParameter("log4j-init-file");

	    // if the log4j-init-file is not set, then no point in trying
	    if(file != null) {
	      PropertyConfigurator.configure(prefix+file);
	    }
	  }

	  public void doGet(HttpServletRequest req, HttpServletResponse res) {
	  }

}
