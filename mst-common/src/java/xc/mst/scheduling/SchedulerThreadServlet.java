/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xc.mst.utils.MSTConfiguration;

/**
 * Servlet to start scheduler thread
 *
 * @author Sharmila Ranganathan
 *
 */
public class SchedulerThreadServlet extends HttpServlet {

	/** Eclipse generated id */
	private static final long serialVersionUID = 4129403386738067125L;

	private Thread schedulerThread = null;
	private Scheduler scheduler = null;

	/**
	 * Start scheduler thread
	 */
	public void init() {
		scheduler = (Scheduler)MSTConfiguration.getBean("Scheduler");
		schedulerThread = new Thread(scheduler);
		schedulerThread.start();
	  }

	  public void doGet(HttpServletRequest req, HttpServletResponse res) {
	  }

	  public void destroy()
	  {
		  if(scheduler != null && schedulerThread.isAlive())
			  scheduler.kill();
	  }
}

