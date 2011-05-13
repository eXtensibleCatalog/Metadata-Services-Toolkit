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

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;

public class MetadataServiceManager extends WorkerThread {
	
	protected MetadataService metadataService = null;
	protected Repository incomingRepository = null;
	protected List<Format> triggeringFormats = null;
	protected List<Set> triggeringSets = null;
	protected Set outputSet = null;

	public Set getOutputSet() {
		return outputSet;
	}

	public void setOutputSet(Set outputSet) {
		this.outputSet = outputSet;
	}

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

	public void cancelInner() {
		metadataService.cancel();
		super.cancelInner();
	}
	
	public List<Format> getTriggeringFormats() {
		return triggeringFormats;
	}

	public void setTriggeringFormats(List<Format> triggeringFormats) {
		this.triggeringFormats = triggeringFormats;
	}
	
	public List<Set> getTriggeringSets() {
		return triggeringSets;
	}

	public void setTriggeringSets(List<Set> sets) {
		this.triggeringSets = sets;
	}

	public boolean doSomeWork() {
		if (incomingRepository.ready4harvest()) {
			if (triggeringFormats != null) {
				for (Format f : triggeringFormats) {
					if (triggeringSets != null && triggeringSets.size() > 0) {
						for (Set s : triggeringSets) {
							metadataService.process(incomingRepository, f, s, outputSet);
						}
					} else {
						metadataService.process(incomingRepository, f, null, outputSet);
					}
				}
			} else if (triggeringSets != null && triggeringSets.size() > 0) {
				for (Set s : triggeringSets) {
					metadataService.process(incomingRepository, null, s, outputSet);
				}
			} else {
				metadataService.process(incomingRepository, null, null, outputSet);
			}
		}
		return false;
	}

	public void setup() {
		metadataService.setMetadataServiceManager(this);
		metadataService.setup();
	}
	
	public void finishInner() {
		metadataService.finish();
		super.finishInner();
	}

	public String getDetailedStatus() {
		return null;
	}

	public String getName() {
		if (metadataService != null && metadataService.getService() != null) {
			return metadataService.getService().getName();
		} else {
			return "service starting up";
		}
	}

	public void pauseInner() {
		metadataService.pause();
		super.pauseInner();
	}

	public void proceedInner() {
		metadataService.resume();
		super.proceedInner();
	}

	public long getRecords2ProcessThisRun() {
		return metadataService.getTotalRecordCount();
	}

	public long getRecordsProcessedThisRun() {
		return metadataService.getProcessedRecordCount();
	}

}
