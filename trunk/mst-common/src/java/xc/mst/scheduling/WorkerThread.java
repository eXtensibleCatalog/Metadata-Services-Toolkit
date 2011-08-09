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
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import xc.mst.bo.record.RecordCounts;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.manager.BaseManager;
import xc.mst.repo.Repository;

public abstract class WorkerThread extends BaseManager implements Runnable {

	private static final Logger LOG = Logger.getLogger(Constants.LOGGER_GENERAL);

	protected ReentrantLock running = new ReentrantLock();
	protected Repository repo = null;

	protected RecordCounts incomingRecordCounts = null;
	protected RecordCounts outgoingRecordCounts = null;

	abstract public void setup();
	abstract public String getName();
	abstract public boolean doSomeWork();
	abstract public String getDetailedStatus();
	abstract public long getRecordsProcessedThisRun();
	abstract public long getRecords2ProcessThisRun();

    // yes, this is asinine, but less so then putting it in the http session (which is where it was).
    public static String serviceBarDisplay = null;

	protected String type = null;

	protected Status status = null;

	protected Semaphore setupComplete = null;

	public WorkerThread() {
		setupComplete = new Semaphore(1);
		setupComplete.acquireUninterruptibly();
	}

	public void waitForSetupCompletion() {
		if (setupComplete != null) {
			setupComplete.acquireUninterruptibly();
			setupComplete.release();
			setupComplete = null;
		}
	}

	// only call after setup has run
	public boolean isSetupHappy() {
		return true;
	}

	public void run() {
		boolean success = true;
		try {
			this.status = Status.RUNNING;
			setup();
			if (setupComplete != null) {
	            setupComplete.release();
			}
			boolean keepGoing = isSetupHappy();
			if (!keepGoing) {
				WorkerThread.serviceBarDisplay = "abort";
				LOG.error("Job aborting, setup did not complete successfully!");
				this.status = Status.ERROR;
				success = false;
			}
			while (keepGoing) {
				LOG.debug("getName(): "+getName());
				LOG.debug("status: "+status);
				if (this.status == Status.RUNNING) {
					serviceBarDisplay = "pause";
					keepGoing = doSomeWork();
				} else if (this.status == Status.CANCELED) {
					keepGoing = false;
				} else if (this.status == Status.PAUSED || this.status == Status.PAUSING) {
					Thread.sleep(5000);
				}
			}
		} catch(Throwable e) {
			LOG.error("", e);
			this.status = Status.ERROR;
			success = false;
		} finally {
			LOG.debug("before finish workDelegate.getName(): "+getName());
			finish(success);
			LOG.debug("after finish workDelegate.getName(): "+getName());
            getProdLogger().debug("after finish workDelegate.getName(): "+getName());
            if (running.isHeldByCurrentThread()) {
                LOG.error("*** Warning:  WorkerThread "+this.getName()+" held the running lock, unlocked it now on exit.");
                running.unlock();
            }
			this.status = Status.NOT_RUNNING;
		}
	}

	public String getJobName() {
		//waitForSetupCompletion();
		return getName();
	}

	public Status getJobStatus() {
		return status;
	}

	public void setJobStatus(Status status) {
		this.status = status;
	}

	public RecordCounts getIncomingRecordCounts() {
		return incomingRecordCounts;
	}
	public void setIncomingRecordCounts(RecordCounts incomingRecordCounts) {
		this.incomingRecordCounts = incomingRecordCounts;
	}

	public RecordCounts getOutgoingRecordCounts() {
		return outgoingRecordCounts;
	}
	public void setOutgoingRecordCounts(RecordCounts outgoingRecordCounts) {
		this.outgoingRecordCounts = outgoingRecordCounts;
	}

	public String getType() {
		return type;
	}

	public final void cancel() {
		waitForSetupCompletion();
		running.lock();
		running.unlock();
		this.status = Status.CANCELED;
	}
	public void cancelInner() {}

	public final void finish(boolean success) {
		LOG.debug("finish-1");
		waitForSetupCompletion();
		LOG.debug("finish-2");
		running.lock();
		LOG.debug("finish-3");
		finishInner(success);
		LOG.debug("finish-4");
		running.unlock();
		LOG.debug("finish-5");
	}
	public void finishInner(boolean success) {
		if (repo != null) {
			repo.commitIfNecessary(true, 0, this.incomingRecordCounts, this.outgoingRecordCounts);
			LOG.debug("before repo.processComplete");
			repo.processComplete();
			LOG.debug("after repo.processComplete");
		}
	}

	public final void pause()  {
		waitForSetupCompletion();
		this.status = Status.PAUSING;
		running.lock();
		pauseInner();
		running.unlock();
		this.status = Status.PAUSED;
	}
	public void pauseInner() {
		if (repo != null) {
			repo.commitIfNecessary(true, 0, this.incomingRecordCounts, this.outgoingRecordCounts);
		}
	}

	public final void proceed() {
		waitForSetupCompletion();
		proceedInner();
		this.status = Status.RUNNING;
	}
	public void proceedInner() {}
}
