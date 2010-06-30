/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.constants.Status;

public class WorkerThread extends Thread {

	private static final Logger LOG = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	public static final String type = null;

	protected Status status = null;
	
	protected WorkDelegate workDelegate = null;
	 
	public void setWorkDelegate(WorkDelegate workDelegate) {
		this.workDelegate = workDelegate;
	}

	public void run() {
		try {
			this.status = Status.RUNNING;
			this.workDelegate.setup();
			boolean keepGoing = true;
			while (keepGoing) {
				LOG.debug("workDelegate.getName(): "+workDelegate.getName());
				LOG.debug("status: "+status);
				if (this.status == Status.RUNNING) {
					keepGoing = this.workDelegate.doSomeWork();
				} else if (this.status == Status.CANCELED) {
					keepGoing = false;
				} else if (this.status == Status.PAUSED) {
					sleep(5000);
				}
			}
		} catch(Exception e) {
			LOG.error("", e);
			this.status = Status.ERROR;
		} finally {
			this.workDelegate.finish();
			this.status = Status.NOT_RUNNING;
		}
	}
	
	public void cancel() {
		this.workDelegate.cancel();
		this.status = Status.CANCELED;
	}

	public void pause() {
		this.workDelegate.pause();
		this.status = Status.PAUSED;
	}

	public void proceed() {
		this.workDelegate.resume();
		this.status = Status.RUNNING;
	}

	public String getJobName() {
		if (this.workDelegate != null) {
			return this.workDelegate.getName();
		} else {
			return "";
		}
	}
	
	public int getRecordsProcessed() {
		return this.workDelegate.getRecordsProcessed();
	}

	public int getTotalRecords() {
		return this.workDelegate.getTotalRecords();
	}

	public Status getJobStatus() {
		return status;
	}
	
	public String getDetailedStatus() {
		return this.workDelegate != null ? this.workDelegate.getDetailedStatus() : "";
	}

	public String getType() {
		return type;
	}

}
