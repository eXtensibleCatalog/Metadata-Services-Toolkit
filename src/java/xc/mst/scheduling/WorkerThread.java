/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

/**
 * An abstract Thread class representing a Job that the Scheduler can run
 *
 * @author Eric Osisek
 */
public abstract class WorkerThread extends Thread
{
	public abstract void run();
	
	public abstract void cancel();
	
	public abstract void pause();
	
	public abstract void proceed();
	
	public abstract String getJobName();
	
	public abstract String getJobStatus();
} // end class WorkerThread
