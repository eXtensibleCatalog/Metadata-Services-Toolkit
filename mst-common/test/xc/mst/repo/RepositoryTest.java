/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;

public class RepositoryTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(RepositoryTest.class);
	
	protected String repoName = "r1";
	protected long lastRecordId = 0;
	
	@BeforeClass
	public void setup() {
		LOG.debug("setup()");
		repoName = "r1";
		repo.setName(repoName);
	}
	
	@Test
	public void coordinateTest() {
		install();
		addRecords();
		findRecords();
	}

	public void install() {
		try {
			repositoryDAO.dropTables(repoName);
			repo.installOrUpdateIfNecessary();
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
			
	public void addRecords() {
		try {
			List<Record> records = new ArrayList<Record>();
			
			Record previousRecord = recordService.createRecord();
			for (int i=0; i<10; i++) {
				Service norm = getServicesService().getServiceByName("MARCNormalization");
				LOG.debug("norm: "+norm);
				Record record = recordService.createSuccessor(previousRecord, norm);
				record.setOaiXml("<hello>"+i+"</hello>");
				records.add(record);
				//record.setXML();
				lastRecordId = record.getId();
			}
			repo.addRecords(records);
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
	
	public void findRecords() {
		try {
			Record r = repo.getRecord(lastRecordId);
			LOG.debug("r.getId(): "+r.getId());
			//LOG.debug("r.getXml(): "+r.getXml());
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
