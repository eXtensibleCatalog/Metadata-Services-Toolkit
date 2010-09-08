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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	Map<Long, java.util.Set<Long>> predSuccMap = new HashMap<Long, java.util.Set<Long>>();
	
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
					if (previousVersion == null && "0.3.0".equals(currentVersion)) {
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
		this.name = name.toLowerCase().replaceAll(" ", "_");
	}
	
	public int getSize() {
		return getRepositoryDAO().getSize(name);
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
		if (getRepositoryDAO().addRecord(name, record)) {
			predSuccMap.clear();	
		}
	}
	
	public void addRecords(List<Record> records) {
		getRepositoryDAO().addRecords(name, records);
	}
	
	public void beginBatch() {
		getRepositoryDAO().beginBatch();
	}
	
	public void endBatch() {
		getRepositoryDAO().endBatch(name);
		predSuccMap.clear();
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
		return getRepositoryDAO().getRecord(name, id);
	}

	public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
		LOG.debug("from:"+from+" until:"+until+ " startingId:"+startingId+" inputFormat:"+inputFormat+" inputSet:"+inputSet);
		List<Record> records = getRepositoryDAO().getRecords(name, from, until, startingId, inputFormat, inputSet);
		if (records == null) {
			LOG.debug("no records found");
		} else { 
			LOG.debug("records.size(): "+records.size());
		}
		return records;
	}
	
	public long getRecordCount(Date from, Date until, Format inputFormat, Set inputSet) {
		LOG.debug("from:"+from+" until:"+until+ " inputFormat:"+inputFormat+" inputSet:"+inputSet);
		long recordCount = getRepositoryDAO().getRecordCount(name, from, until, inputFormat, inputSet);
		LOG.debug("recordCount:"+recordCount);
		return recordCount;
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
		return getRepositoryDAO().getRecordsWSets(name, from, until, startingId);
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
		if (succIds != null) {
			for (Long succId : succIds) {
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
		Date lm = getLastModified();
	    while (lm != null && new Date().before(lm)) {
	    	try {
	    		Thread.sleep(500);
	    	} catch (Throwable t) {
	    		getUtil().throwIt(t);
	    	}
	    	lm = getLastModified();
	    }
	}
	
	public void deleteAllData() {
		getRepositoryDAO().deleteAllData(this.name);
	}

}