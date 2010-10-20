/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.service.impl.test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.harvester.HarvestManager;
import xc.mst.oai.Facade;
import xc.mst.oai.OaiRequestBean;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;
import xc.mst.services.GenericMetadataService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;


public abstract class MockHarvestTest extends StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);
	
	public static final String EXPECTED_OUTPUT_FOLDER = "../test/mock_harvest_expected_output";
	public static final String INPUT_FOLDER = "../test/mock_harvest_input";
	public static final String ACTUAL_OUTPUT_FOLDER = "test/mock_harvest_actual_output";
	
	protected HarvestManager harvestManager = null;
	protected Date harvestOutFrom = null;
	protected Date harvestOutUntil = null;
	protected XmlHelper xmlHelper = new XmlHelper();

	@Test
	public void startToFinish() throws Exception  {
		try {
			dropOldSchemas();
			LOG.debug("after dropOldSchemas");
			installProvider();
			LOG.debug("after installProvider");
			installService();
			LOG.debug("after installService");

			createHarvestSchedule();
			LOG.debug("after createHarvestSchedule");
			
			Date previousLastModified = null;
			while (true) {
				WorkerThread wt = new WorkerThread();
				wt.setWorkDelegate(harvestManager);
				wt.run();
				LOG.debug("previousFileLastModified: "+previousLastModified);
				LOG.debug("repo.getLastModified(): "+repo.getLastModified());
				
				if (previousLastModified != null && previousLastModified.equals(repo.getLastModified())) {
					break;
				} else {
					previousLastModified = repo.getLastModified();
				}
				
				Service s = getServicesService().getServiceByName(getServiceName());
				GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
				
				if (repo instanceof DefaultRepository) {
					((DefaultRepository)repo).sleepUntilReady();
				}
				
				ms.process(repo, null, null, null);
				
				if (ms.getRepository() instanceof DefaultRepository) {
					((DefaultRepository)ms.getRepository()).sleepUntilReady();
				}
				
				harvestOutRecordsFromMST();
			}
				
			finalTest();
			
			compareAgainstExpectedOutput();
			
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
	}
	
	public abstract String getFolder();
	
	public void installProvider() throws Exception {
		provider = new Provider();

		provider.setName(getRepoName());
		provider.setDescription("Repository used in TestNG tests");
		provider.setOaiProviderUrl(getProviderUrl());
		provider.setCreatedAt(new java.util.Date());
		getProviderService().insertProvider(provider);
		
		repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(provider.getName());
		repo.setProvider(provider);
	}
	
	public void createHarvestSchedule() throws Exception {
		HarvestSchedule schedule = new HarvestSchedule();
		Calendar nowCal = Calendar.getInstance();
        schedule.setScheduleName("Test Schedule Name");
        schedule.setDayOfWeek(nowCal.get(Calendar.DAY_OF_WEEK));

        schedule.addFormat(null);
        schedule.setHour(nowCal.get(Calendar.HOUR_OF_DAY));
        schedule.setId(111);
        schedule.setMinute(nowCal.get(Calendar.MINUTE));
        schedule.setProvider(provider);
        schedule.setRecurrence("Daily");
        schedule.setStartDate(java.sql.Date.valueOf("2009-05-01"));
		
		harvestManager = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager");
		HarvestScheduleStep hss = new HarvestScheduleStep();
		hss.setFormat(null);
		hss.setSet(null);
		hss.setSchedule(schedule);
		schedule.addStep(hss);
		harvestManager.setHarvestSchedule(schedule);
	}
	
	public void harvestOutRecordsFromMST() throws Exception {
		if (harvestOutUntil != null) {
			harvestOutFrom = harvestOutUntil;
		} else {
			harvestOutFrom = new Date();
			harvestOutFrom.setYear(60);
		}
		harvestOutUntil = new Date();
		
		LOG.debug("harvestOutFrom: "+harvestOutFrom);
		LOG.debug("harvestOutUntil: "+harvestOutUntil);

		OaiRequestBean bean = new OaiRequestBean();

		// Set parameters on the bean based on the OAI request's parameters
		bean.setVerb("ListRecords");
		bean.setMetadataPrefix(getHarvestOutFormat().getName());
		bean.setFrom(harvestManager.printDate(harvestOutFrom));
		bean.setUntil(harvestManager.printDate(harvestOutUntil));
		
		Service service = getServicesService().getServiceByName(getServiceName());
		bean.setServiceId(service.getId());
		bean.setRequest("http://localhost:8080/MetadataServicesToolkit/"+getServiceName()+"-Service/oaiRepository?verb=ListRecords");

		Facade facade = (Facade) MSTConfiguration.getInstance().getBean("Facade");
		
		StringBuilder stringBuilder = new StringBuilder();
		
		LOG.debug("provider.getLastOaiRequest(): "+provider.getLastOaiRequest());
		stringBuilder.append(facade.execute(bean));
		
		harvestOutResponse = stringBuilder.toString();
		Document doc = xmlHelper.getJDomDocument(harvestOutResponse);
		harvestOutResponse = xmlHelper.getStringPretty(doc.getRootElement());
		
		LOG.debug("XML response");
		LOG.debug(harvestOutResponse);
		File outputFolder = new File(ACTUAL_OUTPUT_FOLDER+"/"+getFolder());
		if (!outputFolder.exists()) {
			outputFolder.mkdir();
		}
		Util.getUtil().spit(ACTUAL_OUTPUT_FOLDER+"/"+getFolder()+"/"+provider.getLastOaiRequest(), harvestOutResponse);
	}
	
	public void compareAgainstExpectedOutput() {
		Map<String, String> testFailures = new HashMap<String, String>();
		File expectedOutputContainingFolder = new File(EXPECTED_OUTPUT_FOLDER);
		String[] expectedOutputFolders = expectedOutputContainingFolder.list();
		if (expectedOutputFolders != null) {
			for (String folderStr : expectedOutputFolders) {
				if (folderStr.contains(".svn")) {
					continue;
				}
				File expectedOutputFolder = new File(EXPECTED_OUTPUT_FOLDER+"/"+folderStr);
				Set<String> expectedOutputFiles = new HashSet<String>();
				for (String ef : expectedOutputFolder.list()) {
					if (ef.endsWith(".xml")) {
						expectedOutputFiles.add(ef);
					}
				}
				
				File actualOutputFolder  = new File(ACTUAL_OUTPUT_FOLDER+"/"+folderStr);
				if (!actualOutputFolder.exists()) {
					testFailures.put(folderStr, "folder expected, but wasn't produced.");
					continue;
				}
				for (String af : actualOutputFolder.list()) {
					LOG.debug("af: "+af);
					if (expectedOutputFiles.contains(af)) {
						expectedOutputFiles.remove(af);
						if (new XmlHelper().diffXmlFiles(
								ACTUAL_OUTPUT_FOLDER+"/"+folderStr+"/"+af, 
								EXPECTED_OUTPUT_FOLDER+"/"+folderStr+"/"+af)) {
							testFailures.put(folderStr+"/"+af, "files differ");
						}
					} else {
						testFailures.put(folderStr+"/"+af, "file exists in actual, but not expected.");
					}
				}
				for (String ef : expectedOutputFiles) {
					testFailures.put(folderStr+"/"+ef, "file expected, but wasn't produced.");
				}
				
				StringBuilder sb = new StringBuilder();
				for (String key : testFailures.keySet()) {
					String value = testFailures.get(key);
					String s2 = "\n"+key+": "+value;

					sb.append(s2);
				}
				
				if (sb.length() > 0) {
					LOG.error(sb.toString());
					throw new RuntimeException(sb.toString());
				}
			}		
		}

	}

}
