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

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordIfc;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.utils.MSTConfiguration;

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
		getRecords();
	}

	public void install() {
		try {
			repositoryDAO.deleteSchema(repoName);
			repositoryDAO.createSchema(repoName);
			repo.installOrUpdateIfNecessary(null, MSTConfiguration.getInstance().getProperty("version"));
			
			Format f = new Format();
			f.setNamespace("marcxml");
			f.setSchemaLocation("http://www.extensiblecatalog.org/support");
			f.setName("http://www.loc.gov/MARC21/slim");
			getFormatDAO().insert(f);
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
			
	public void addRecords() {
		try {
			List<Record> records = new ArrayList<Record>();
			Format f = getFormatDAO().getById(1);
			Set set = getSetDAO().getById(1);
			RecordIfc previousRecord = recordService.createRecord();
			Service s = new Service();
			s.setId(1);
			for (int i=0; i<10; i++) {
				/*
				Record record = recordService.createSuccessor(previousRecord, s);
				record.setMode(Record.STRING_MODE);
				record.setOaiXml("<hello>"+i+"</hello>");
				record.setFormat(f);
				record.addSet(set);
				records.add(record);
				//record.setXML();
				lastRecordId = record.getId();
				*/
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
	
	public void getRecords() {
		try {
			assert getRepositoryDAO().getRecords(repoName, null, null, null, null, null).size() == 10;
			Format f = getFormatDAO().getById(1);
			assert getRepositoryDAO().getRecords(repoName, null, null, null, f, null).size() == 10;
			Set set = getSetDAO().getById(1);
			assert getRepositoryDAO().getRecords(repoName, null, null, null, f, set).size() == 10;
			List<Record> records = getRepositoryDAO().getRecordsWSets(repoName, null, null, null);
			assert records.size() == 10;
			for (Record r : records) {
				assert r.getSets().get(0).getId() == set.getId();
			}
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
