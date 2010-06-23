/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.manager.BaseService;

public class DefaultRepository extends BaseService implements Repository {
	
	private static final Logger LOG = Logger.getLogger(DefaultRepository.class);
	
	protected String name = null;

	public void installOrUpdateIfNecessary() {
		this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					LOG.debug("config.getProperty(\"version\"): "+config.getProperty("version"));
					if ("0.3.0".equals(config.getProperty("version"))) {
						boolean exists = getRepositoryDAO().exists(name);
						LOG.debug("exists: "+exists);
						if (!exists) {
							getRepositoryDAO().createRepo(name);
						}
					}
				} catch (Throwable t) {
					LOG.error("", t);
					status.setRollbackOnly();
				}
			}
		});
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
		getRepositoryDAO().addRecord(name, record);
	}
	
	public void addRecords(List<Record> records) {
		getRepositoryDAO().addRecords(name, records);
	}
	
	public void beginBatch() {
		getRepositoryDAO().beginBatch();
	}
	
	public void endBatch() {
		getRepositoryDAO().endBatch(name);
	}

	public List<Long> getPredecessorIds(Record r) {
		return getRepositoryDAO().getPredecessors(name, r.getId());
	}

	// TODO: you need to check the cache as well
	public Record getRecord(String oaiId) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO: you need to check the cache as well
	public Record getRecord(long id) {
		return getRepositoryDAO().getRecord(name, id);
	}

	public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
		return getRepositoryDAO().getRecords(name, from, until, startingId, inputFormat, inputSet);
	}
	
	public void injectSuccessors(Record r) {
		List<Record> succs = getRepositoryDAO().getSuccessors(name, r.getId());
		if (succs != null) {
			r.getSuccessors().addAll(succs);
		}
	}

}