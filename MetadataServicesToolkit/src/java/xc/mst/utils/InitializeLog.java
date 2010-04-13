/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;

import xc.mst.bo.log.Log;
import xc.mst.dao.DataException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;

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

		PropertyConfigurator.configure(System.getProperty("user.dir") + MSTConfiguration.FILE_SEPARATOR + MSTConfiguration.getUrlPath()+ MSTConfiguration.FILE_SEPARATOR + "log4j.config.txt");
		
	    // Initialize the general MST logs
	    LogDAO logDao = new DefaultLogDAO();
	    List<Log> logs = null;
		try 
		{
			logs = logDao.getAll();
			// Update log file path
			for(Log log : logs) {
				// add the path if its not added previously. In case of server restart, the path need not be added since it would have been
				// addeed the fisrt time server was restarted.
				if (log.getLogFileLocation().indexOf(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR) == -1) {
					log.setLogFileLocation(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + log.getLogFileLocation());
			    	logDao.update(log);
				}
			}
			
			logs = logDao.getAll();
		} 
		catch (DataException e) 
		{
			return;
		}
		
	
	    for(Log log : logs) {
	    	LogWriter.addInfo(log.getLogFileLocation(), "Beginning logging for " + log.getLogFileName() + ".");
	    }
	  }

	  public void doGet(HttpServletRequest req, HttpServletResponse res) {
	  }

}
