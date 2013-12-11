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
import xc.mst.utils.MSTConfiguration;

public class SolrWorkDelegate extends WorkerThread {

    private static final Logger LOG = Logger.getLogger(SolrWorkDelegate.class);

    protected SolrIndexService solrIndexService = null;
    // protected ReentrantLock lock = new ReentrantLock();
    protected Semaphore lock = new Semaphore(1);
    
    // Sometimes it's useful to disable solr indexing,
    // e.g., if you decide to add-a-service-as-you-go-along during initial MST setup (which
    // might be useful if your repo is HUGE) and want to verify each
    // service one-at-a-time. This is necessary because if you do not, every time you add
    // a new service, the previous (incoming) service's solr index gets reprocessed in its
    // entirety.
    // Once all services have processed, one must shutdown MST, then set solr.index.enabled=true,
    // then once MST starts up, the solr indexes will get processed
    protected boolean solrEnabled;

    public SolrWorkDelegate() {
        lock.acquireUninterruptibly();
        solrEnabled = true;
    }

    public void setup() {
        LOG.debug("setup");
        solrIndexService = (SolrIndexService) config.getBean("SolrIndexService");
        Service s2 = new Service();
        s2.setName("solr-indexer");
        solrIndexService.setService(s2);
        LOG.debug("about to release");
        solrEnabled = config.getPropertyAsBoolean("solr.index.enabled", true);
        lock.release();
        LOG.debug("released");
    }

    public void finishInner(boolean success) {
        solrIndexService.finish();
        super.finishInner(success);
    }

    public String getDetailedStatus() {
        return null;
    }

    public String getName() {
        return solrIndexService.getName4progressBar();
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
        if (solrEnabled) {
	        try {
	            List<Provider> providers = getProviderDAO().getAll();
	            if (providers != null) {
	                for (Provider p : providers) {
	                    Repository repo = getRepositoryService().getRepository(p);
	                    if (repo.ready4harvest()) {
	                        LOG.debug("SolrWorkDelegate, repo IS ready4harvest! provider="+p.getName()+" repo="+repo.getName());
	                        solrIndexService.process(repo, null, null, null);
	                    }
	                    else {
	                        LOG.debug("SolrWorkDelegate, repo not ready4harvest! provider="+p.getName()+" repo="+repo.getName());
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
        }
        if (getJobStatus().equals(Status.RUNNING)) {
            setJobStatus(Status.IDLE);
            try {
                Thread.sleep(config.getPropertyAsInt("solr.index.sleepBetweenNewRecordsCheck", 10000));
            } catch (Throwable t) {
            }
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
