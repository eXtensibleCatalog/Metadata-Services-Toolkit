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

import xc.mst.bo.record.Record;
import xc.mst.manager.BaseService;
import xc.mst.utils.MSTConfiguration;

public class DefaultRepository extends BaseService implements Repository {
	
	protected String name = null;

	public void installOrUpdateIfNecessary() {
		if ("0.3.0".equals(MSTConfiguration.getProperty("version"))) {
			boolean exists = getRepositoryDAO().exists(name);
			System.out.println("exists: "+exists);
			if (!exists) {
				getRepositoryDAO().createTables(name);
			}
		}
	}
	
	protected boolean exists() {
		return false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addRecords(List<Record> records) {
		getRepositoryDAO().addRecords(name, records);
	}
	
	public void beginBatch() {
		getRepositoryDAO().beginBatch();
	}
	
	public void endBatch(String name) {
		getRepositoryDAO().endBatch(name);
	}

	public List<Record> getPredecessors(Record r) {
		// TODO Auto-generated method stub
		return null;
	}

	public Record getRecord(String oaiId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Record getRecord(long id) {
		return getRepositoryDAO().getRecord(name, id);
	}

	public List<Record> getRecords(Date from, Date until, Integer startingId) {
		// TODO Auto-generated method stub
		return null;
	}

}
