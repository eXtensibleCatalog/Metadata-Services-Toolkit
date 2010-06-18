package xc.mst.services;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.manager.BaseManager;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkDelegate;

public class MetadataServiceManager extends BaseManager implements WorkDelegate {
	
	protected MetadataService metadataService = null;
	protected Repository incomingRepository = null;
	protected Format format = null;
	protected Set set = null;
	
	public MetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(MetadataService metadataService) {
		this.metadataService = metadataService;
	}
	
	public Repository getIncomingRepository() {
		return incomingRepository;
	}

	public void setIncomingRepository(Repository incomingRepository) {
		this.incomingRepository = incomingRepository;
	}
	
	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public Set getSet() {
		return set;
	}

	public void setSet(Set set) {
		this.set = set;
	}

	public void cancel() {
		metadataService.cancel();
	}

	public boolean doSomeWork() {
		metadataService.process(incomingRepository, format, set);
		return false;
	}

	public void setup() {}
	public void finish() {}

	public String getDetailedStatus() {
		return null;
	}

	public String getName() {
		return metadataService.getService().getName();
	}

	public int getRecordsProcessed() {
		return metadataService.getProcessedRecordCount();
	}

	public int getTotalRecords() {
		return metadataService.getTotalRecordCount();
	}

	public void pause() {
		metadataService.pause();
	}

	public void resume() {
		metadataService.resume();
	}

}
