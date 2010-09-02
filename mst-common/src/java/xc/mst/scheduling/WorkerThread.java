/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.constants.Status;

public class WorkerThread extends Thread {

	private static final Logger LOG = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	protected String type = null;

	protected Status status = null;
	
	protected WorkDelegate workDelegate = null;
	
	protected Semaphore setupComplete = null;
	
	public WorkerThread() {
		setupComplete = new Semaphore(1);
		setupComplete.acquireUninterruptibly();
	}
	
	public void waitForSetupCompletion() {
		if (setupComplete != null) {
			setupComplete.acquireUninterruptibly();
			setupComplete = null;
		}
	}
	 
	public void setWorkDelegate(WorkDelegate workDelegate) {
		this.workDelegate = workDelegate;
		this.workDelegate.setWorkerThread(this);
	}
	
	public WorkDelegate getWorkDelegate() {
		return this.workDelegate;
	}

	public void run() {
		try {
			this.status = Status.RUNNING;
			this.workDelegate.setup();
			setupComplete.release();
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
			LOG.debug("before finish workDelegate.getName(): "+workDelegate.getName());
			this.workDelegate.finish();
			LOG.debug("after finish workDelegate.getName(): "+workDelegate.getName());
			this.status = Status.NOT_RUNNING;
		}
	}
	
	public void cancel() {
		waitForSetupCompletion();
		this.workDelegate.cancel();
		this.status = Status.CANCELED;
	}

	public void pause() {
		waitForSetupCompletion();
		this.workDelegate.pause();
		this.status = Status.PAUSED;
	}

	public void proceed() {
		waitForSetupCompletion();
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
	
	public void setJobStatus(Status status) {
		this.status = status;
	}
	
	public String getDetailedStatus() {
		return this.workDelegate != null ? this.workDelegate.getDetailedStatus() : "";
	}

	public String getType() {
		return type;
	}

}
