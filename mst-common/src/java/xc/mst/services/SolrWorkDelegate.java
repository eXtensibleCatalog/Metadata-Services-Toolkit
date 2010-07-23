package xc.mst.services;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.constants.Status;
import xc.mst.manager.BaseService;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkDelegate;
import xc.mst.scheduling.WorkerThread;

public class SolrWorkDelegate extends BaseService implements WorkDelegate {
	
	private static final Logger LOG = Logger.getLogger(SolrWorkDelegate.class);
	
	protected WorkerThread workerThread = null;
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
	
	public void finish() {
		solrIndexService.finish();
	}

	public String getDetailedStatus() {
		return null;
	}
	
	public WorkerThread getWorkerThread() {
		return workerThread;
	}

	public void setWorkerThread(WorkerThread workerThread) {
		this.workerThread = workerThread;
	}

	public String getName() {
		return solrIndexService.getService().getName();
	}

	public int getRecordsProcessed() {
		return solrIndexService.getProcessedRecordCount();
	}

	public int getTotalRecords() {
		return solrIndexService.getTotalRecordCount();
	}

	public void pause() {
		solrIndexService.pause();
	}

	public void resume() {
		wait4availability();
		//lock.lock();
		//lock.unlock();
		solrIndexService.resume();
	}

	public void cancel() {
		// TODO Auto-generated method stub
	}

	public boolean doSomeWork() {
		this.workerThread.setJobStatus(Status.PAUSED);
		wait4availability();
		this.workerThread.setJobStatus(Status.RUNNING);
		LOG.debug("doSomeWork");
		try {
			List<Provider> providers = getProviderDAO().getAll();
			if (providers != null) {
				for (Provider p : providers) {
					Repository repo = getRepositoryService().getRepository(p);
					solrIndexService.process(repo, null, null, null);
				}
			}

			List<Service> services = getServicesService().getAllServices();
			if (services != null) {
				for (Service s : services) {
					Repository repo = s.getMetadataService().getRepository();
					solrIndexService.process(repo, null, null, null);
				}
			}
		} catch (Throwable t) {
			LOG.error("", t);
		}
		this.workerThread.setJobStatus(Status.PAUSED);
		try {Thread.sleep(3000);} catch (Throwable t) {}
		this.workerThread.setJobStatus(Status.RUNNING);
		return true;
	}
	
	protected void wait4availability() {
		LOG.debug("about to release");
		lock.acquireUninterruptibly();
		lock.release();
		LOG.debug("released");
	}

}
