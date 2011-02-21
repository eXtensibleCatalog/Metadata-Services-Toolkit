/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.constants.Status;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;

public class SolrWorkDelegate extends WorkerThread {
	
	private static final Logger LOG = Logger.getLogger(SolrWorkDelegate.class);
	
	protected MetadataService solrIndexService = null;
	//protected ReentrantLock lock = new ReentrantLock();
	protected Semaphore lock = new Semaphore(1);
	
	public SolrWorkDelegate() {
		lock.acquireUninterruptibly();
	}

	public void setup() {
		LOG.debug("setup");
		solrIndexService = (MetadataService)config.getBean("SolrIndexService");
		Service s2 = new Service();
		s2.setName("solr-indexer");
		solrIndexService.setService(s2);
		LOG.debug("about to release");
		lock.release();
		LOG.debug("released");
	}
	
	public void finishInner() {
		solrIndexService.finish();
		super.finishInner();
	}

	public String getDetailedStatus() {
		return null;
	}

	public String getName() {
		return solrIndexService.getService().getName();
	}

	public void pauseInner() {
		solrIndexService.pause();
		super.pauseInner();
	}

	public void proceedInner() {
		wait4availability();
		solrIndexService.resume();
		super.proceedInner();
	}

	public boolean doSomeWork() {
		setJobStatus(Status.IDLE);
		wait4availability();
		setJobStatus(Status.RUNNING);
		LOG.debug("doSomeWork");
		try {
			List<Provider> providers = getProviderDAO().getAll();
			if (providers != null) {
				for (Provider p : providers) {
					Repository repo = getRepositoryService().getRepository(p);
					if (repo.ready4harvest()) {
						solrIndexService.process(repo, null, null, null);
					}
				}
			}
			List<Service> services = getServicesService().getAllServices();
			if (services != null) {
				for (Service s : services) {
					Repository repo = s.getMetadataService().getRepository();
					if (repo.ready4harvest()) {
						solrIndexService.process(repo, null, null, null);
					}
				}
			}
		} catch (Throwable t) {
			LOG.error("", t);
		}
		if (getJobStatus().equals(Status.RUNNING)) {
			setJobStatus(Status.IDLE);
			try {Thread.sleep(10000);} catch (Throwable t) {}
			if (getJobStatus().equals(Status.IDLE)) {
				setJobStatus(Status.RUNNING);
			}
		}
		return true;
	}
	
	protected void wait4availability() {
		LOG.debug("about to release");
		lock.acquireUninterruptibly();
		lock.release();
		LOG.debug("released");
	}

	public long getRecords2ProcessThisRun() {
		return solrIndexService.getTotalRecordCount();
	}
	
	public long getRecordsProcessedThisRun() {
		return solrIndexService.getProcessedRecordCount();
	}

}
