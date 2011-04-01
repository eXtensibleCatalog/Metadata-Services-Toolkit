/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import gnu.trove.TLongHashSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.manager.BaseService;

public class DefaultRepository extends BaseService implements Repository {
	
	private static final Logger LOG = Logger.getLogger(DefaultRepository.class);
	
	// This is meant only to be a cache of what is not yet in the DB.  This will not
	// keep all pred-succs in memory.
	protected Map<Long, java.util.Set<Long>> predSuccMap = new HashMap<Long, java.util.Set<Long>>();
	
	protected List<long[]> uplinks = new ArrayList<long[]>();
	
	protected TLongHashSet recordsToActivate = new TLongHashSet();

	// All of the following counts are reset each time a batch of records is persisted
	protected Map<String, long[]> incomingRecordCountsByType = null;
	
	protected String name = null;
	
	protected Provider provider = null;
	protected Service service = null;

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
	
	public Date getLastModified() {
		return getRepositoryDAO().getLastModified(name);
	}
	
	public int getNumRecords() {
		return getRepositoryDAO().getNumRecords(name);
	}

	public void installOrUpdateIfNecessary(final String previousVersion, final String currentVersion) {
		final Repository thisthis = this;
		this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					LOG.debug("config.getProperty(\"version\"): "+config.getProperty("version"));
					if (previousVersion == null && 
							currentVersion != null && 
							currentVersion.startsWith("0.3.")) {
						getRepositoryDAO().createTables(thisthis);
					}
				} catch (Throwable t) {
					LOG.error("", t);
					status.setRollbackOnly();
				}
			}
		});
	}
	
	public void populatePredecessors(TLongHashSet predecessors) {
		getRepositoryDAO().populatePredecessors(name, predecessors);
	}
	
	protected boolean exists() {
		return false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = getUtil().normalizeName(name);
	}
	
	public int getSize() {
		return getRepositoryDAO().getSize(name);
	}
	
	protected long[] getRecordCounts(String type) {
		if (incomingRecordCountsByType == null) {
			incomingRecordCountsByType = new HashMap<String, long[]>();
		}
		long[] counts = incomingRecordCountsByType.get(type);
		if (counts == null) {
			counts = new long[4];
			incomingRecordCountsByType.put(type, counts);
		}
		return counts;
	}

	public void updateIncomingRecordCounts(String type, boolean update, boolean delete) {
		long newRecordsCount = 0;
		long updatedRecordsCount = 0;
		long deletedRecordsCount = 0;
		if (delete) {
			deletedRecordsCount = 1;
			if (update) {
				newRecordsCount = -1;
			}
		} else {
			if (update) {
				updatedRecordsCount = 1;
			} else {
				newRecordsCount = 1;
			}
		}
		long[] counts = getRecordCounts(type);
		counts[0] += newRecordsCount;
		counts[1] += updatedRecordsCount;
		counts[2] += deletedRecordsCount;
	}
	
	public void incrementUnexpectedProcessingErrors(String type) {
		long[] counts = getRecordCounts(type);
		counts[3]++;
	}
	
	public void addRecord(Record record) {
		if (record.getPredecessors() != null) {
			for (InputRecord ir : record.getPredecessors()) {
				java.util.Set<Long> succIds = predSuccMap.get(ir.getId());
				if (succIds == null) {
					succIds = new HashSet<Long>();
					predSuccMap.put(ir.getId(), succIds);
				}
				succIds.add(record.getId());
			}
		}
		getRepositoryDAO().addRecord(name, record);
	}
	
	public void addRecords(List<Record> records) {
		getRepositoryDAO().addRecords(name, records);
	}

	public boolean commitIfNecessary(boolean force) {
		return commitIfNecessary(force, 0);
	}
	public boolean commitIfNecessary(boolean force, long processedRecordsCount) {
		if (getRepositoryDAO().commitIfNecessary(name, force, processedRecordsCount)) {
			predSuccMap.clear();
			getRepositoryDAO().persistLinkedRecordIds(name, uplinks);
			uplinks.clear();
			getRepositoryDAO().activateRecords(name, recordsToActivate);
			recordsToActivate.clear();
			
			for (Map.Entry<String, long[]> me : this.incomingRecordCountsByType.entrySet()) {
				String type = me.getKey();
				int i=0;
				for (long c : me.getValue()) {
					String key = null;
					if (i == 0) {
						key = "incomingNewRecordsCount";
					} else if (i == 1) {
						key = "incomingUpdatedRecordsCount";
					} else if (i == 2) {
						key = "incomingDeletedRecordsCount";
					} else if (i == 3) {
						key = "incomingProcessingErrorsCount";
					}
					if (type != null) {
						String key2 = key+"-"+type;
						setPersistentProperty(key2, getPersistentPropertyAsLong(key2, 0l)+c);
					} else {
						setPersistentProperty(key, getPersistentPropertyAsLong(key, 0l)+c);
					}
					i++;
				}
			}
			this.incomingRecordCountsByType = null;
			return true;
		} else {
			return force;
		}
	}

	public List<Long> getPredecessorIds(Record r) {
		return getRepositoryDAO().getPredecessors(name, r.getId());
	}

	// TODO: you need to check the cache as well
	public Record getRecord(String oaiId) {
		int indexOfSlash = oaiId.lastIndexOf("/");

		if (indexOfSlash != -1) {
			String id = oaiId.substring(indexOfSlash + 1);
			return getRecord(Long.parseLong(id));
		} else {
			return null;
		}
		
	}

	// TODO: you need to check the cache as well
	public Record getRecord(long id) {	
		Record r = getRepositoryDAO().getRecord(name, id);
		if (r != null)
			r.setSets(getRepositoryDAO().getSets(name, id));
		return r;
	}

	public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
		LOG.debug("from:"+from+" until:"+until+ " startingId:"+startingId+" inputFormat:"+inputFormat+" inputSet:"+inputSet);
		List<Record> records = getRepositoryDAO().getRecordsWSets(name, from, until, startingId, inputFormat, inputSet);
		if (records == null) {
			LOG.debug("no records found");
		} else { 
			LOG.debug("records.size(): "+records.size());
		}
		return records;
	}

	public long getRecordCount(Date from, Date until, Long startingId, Format inputFormat, Set inputSet, long offset) {
		LOG.debug("from:"+from+" until:"+until+ " inputFormat:"+inputFormat+" inputSet:"+inputSet);
		long estimatedRecordsRemaining = getRepositoryDAO().getRecordCount(name, from, until, startingId, inputFormat, inputSet, offset);
		int completeListSizeThreshold = config.getPropertyAsInt("harvestProvider.estimateCompleteListSizeThreshold", 1000000);
		LOG.debug("completeListSizeThreshold: "+completeListSizeThreshold);
		return estimatedRecordsRemaining;
		/*
		// This logic only makes sense if you're taking guesses
		long recordCount = 0;
		if (estimatedRecordsRemaining < -1 && (estimatedRecordsRemaining * -1) < completeListSizeThreshold) {
			recordCount = offset + completeListSizeThreshold;
		}
		LOG.debug("recordCount:"+recordCount);
		// if (recordCount + numAlreadyHarvested) < 
		return recordCount;
		*/
	}
	
	
	public List<Record> getRecordHeader(Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
		LOG.debug("from:"+from+" until:"+until+ " startingId:"+startingId+" inputFormat:"+inputFormat+" inputSet:"+inputSet);
		List<Record> records = getRepositoryDAO().getRecordHeader(name, from, until, startingId, inputFormat, inputSet);
		if (records == null) {
			LOG.debug("no records found");
		} else { 
			LOG.debug("records.size(): "+records.size());
		}
		return records;
	}
	
	public List<Record> getRecordsWSets(Date from, Date until, Long startingId) {
		List<Record> recs = getRepositoryDAO().getRecordsWSets(name, from, until, startingId);
		for (Record r : recs) {
			getMessageService().injectMessageMessage(r);
		}
		return recs;
	}
	
	public void injectSuccessors(Record r) {
		List<Record> succs = getRepositoryDAO().getSuccessors(name, r.getId());
		if (succs != null) {
			r.getSuccessors().addAll(succs);
		}
	}
	
	public void injectSuccessorIds(Record r) {
		java.util.Set<Long> succIds = predSuccMap.get(r.getId());
		if (succIds == null) {
			succIds = getRepositoryDAO().getSuccessorIds(name, r.getId());
			predSuccMap.put(r.getId(), succIds);
		}
		if (succIds != null && succIds.size() > 0) {
			java.util.Set<Long> orderedSuccIds = new TreeSet<Long>();
			orderedSuccIds.addAll(succIds);
			for (Long succId : orderedSuccIds) {
				Record or = new Record();
				or.setId(succId);
				r.getSuccessors().add(or);
			}
		}
	}
	
	/**
	 *  I slightly future dated the timestamp of the records so that a record will always
	 *  have been available from it's update_date forward.  We need to wait for that 
	 *  future dating to become present before moving on here.  Otherwise, the next service
	 *  will not pick up all the records that were just inserted.
	 */
	public void sleepUntilReady() {
		boolean keepSleeping = true;
		boolean repoCreated = false;
		Date lm = null;
		while (keepSleeping) {
			repoCreated = ready4harvest();
			if (repoCreated) {
				lm = getLastModified();
			    keepSleeping = (lm != null && new Date().before(lm));	
			}
	    	try {
	    		Thread.sleep(500);
	    	} catch (Throwable t) {
	    		getUtil().throwIt(t);
	    	}
	    }
	}
	
	public void deleteAllData() {
		getRepositoryDAO().deleteAllData(this.name);
	}

	public void addLink(long fromRecordId, long toRecordId) {
		uplinks.add(new long[] {fromRecordId, toRecordId});
	}

	public List<Long> getLinkedRecordIds(Long toRecordId) {
		return getRepositoryDAO().getLinkedRecordIds(name, toRecordId);
	}

	public void activateRecord(long recordId) {
		recordsToActivate.add(recordId);		
	}
	
	public void processComplete() {
		getRepositoryDAO().createIndiciesIfNecessary(name);
		getRepositoryDAO().updateOutgoingRecordCounts(name);
	}
	
	public boolean ready4harvest() {
		return getRepositoryDAO().ready4harvest(name);
	}
	
	public int getPersistentPropertyAsInt(String key, int def) {
		String val = getRepositoryDAO().getPersistentProperty(name, key);
		if (!StringUtils.isEmpty(val)) {
			try {
				return Integer.parseInt(val);
			} catch (Throwable t) {
				// do nothing
			}	
		}
		return def;
	}
	
	public long getPersistentPropertyAsLong(String key, long def) {
		String val = getRepositoryDAO().getPersistentProperty(name, key);
		if (!StringUtils.isEmpty(val)) {
			try {
				return Long.parseLong(val);
			} catch (Throwable t) {
				// do nothing
			}	
		}
		return def;
	}
	
	public String getPersistentProperty(String key) {
		return getRepositoryDAO().getPersistentProperty(name, key);
	}

	public void setPersistentProperty(String key, int value) {
		getRepositoryDAO().setPersistentProperty(name, key, value+"");
	}
	
	public void setPersistentProperty(String key, long value) {
		getRepositoryDAO().setPersistentProperty(name, key, value+"");
	}
	
	public void setPersistentProperty(String key, String value) {
		getRepositoryDAO().setPersistentProperty(name, key, value);
	}
	
	public void injectHarvestInfo(Record r) {
		getRepositoryDAO().injectHarvestInfo(name, r);
	}
	
}