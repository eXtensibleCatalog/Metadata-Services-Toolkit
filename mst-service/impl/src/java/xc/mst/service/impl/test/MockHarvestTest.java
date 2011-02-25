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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.harvester.HarvestManager;
import xc.mst.oai.Facade;
import xc.mst.oai.OaiRequestBean;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;


public abstract class MockHarvestTest extends StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);
	
	public static final String EXPECTED_OUTPUT_FOLDER = "../test/mock_harvest_expected_output";
	public static final String INPUT_FOLDER = "../test/mock_harvest_input";
	public static final String ACTUAL_OUTPUT_FOLDER = "test/mock_harvest_actual_output";
	
	protected Date harvestOutFrom = null;
	protected Date harvestOutUntil = null;
	protected XmlHelper xmlHelper = new XmlHelper();
	
	public MockHarvestTest() {
		this.shouldValidateRepo = false;
	}

	public abstract String getFolder();
	
	@Test
	public void startToFinish() throws Exception  {
		//HarvestManager harvestManager = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager");

		printClassPath();
		
		dropOldSchemas();
		LOG.info("after dropOldSchemas");
		installProvider();
		LOG.info("after installProvider");
		installService();
		LOG.info("after installService");

		configureProcessingRules();
		LOG.info("after configureProcessingRules");

		Date previousLastModified = null;
		while (true) {	
			createHarvestSchedule();
			waitUntilFinished();
			if (previousLastModified != null && previousLastModified.equals(repo.getLastModified())) {
				break;
			} else {
				previousLastModified = repo.getLastModified();
			}
			harvestOutRecordsFromMST();
		}
		finalTest();
		compareAgainstExpectedOutput();
	}
	
	@Override
	public void harvestOutRecordsFromMST() throws Exception {
		HarvestManager harvestManager = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager");
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
		bean.setFrom(harvestManager.printDateTime(harvestOutFrom));
		bean.setUntil(harvestManager.printDateTime(harvestOutUntil));
		
		Service service = getServicesService().getServiceByName(getServiceName());
		bean.setServiceId(service.getId());
		bean.setRequest("http://localhost:8080/MetadataServicesToolkit/"+getServiceName()+"-Service/oaiRepository?verb=ListRecords");

		Facade facade = (Facade) MSTConfiguration.getInstance().getBean("Facade");
		
		StringBuilder stringBuilder = new StringBuilder();

		LOG.debug("harvestManager: "+harvestManager);
		LOG.debug("harvestManager.lastOaiRequest: "+HarvestManager.lastOaiRequest);
		stringBuilder.append(facade.execute(bean));
		
		harvestOutResponse = stringBuilder.toString();
		Document doc = xmlHelper.getJDomDocument(harvestOutResponse);
		harvestOutResponse = xmlHelper.getStringPretty(doc.getRootElement());
		
		Element oaipmhEl = doc.getRootElement();
		List records = oaipmhEl.getChild("ListRecords", oaipmhEl.getNamespace()).
			getChildren("record", oaipmhEl.getNamespace());
		
		for (String folderStr : new String[] {
				ACTUAL_OUTPUT_FOLDER+"/"+getFolder(),
				ACTUAL_OUTPUT_FOLDER+"/"+getFolder()+"/byRecordIds"}) {
			File outFolder = new File(folderStr);
			if (!outFolder.exists()) {
				outFolder.mkdir();
			}
		}
		
		for (Object rObj : records) {
			Element rEl = (Element)rObj;
			String oaiId = rEl.getChild("header", oaipmhEl.getNamespace())
				.getChildText("identifier", oaipmhEl.getNamespace());
			int lastIndexOf = oaiId.lastIndexOf('/');
			oaiId = oaiId.substring(lastIndexOf+1);
			Util.getUtil().spit(ACTUAL_OUTPUT_FOLDER+"/"+getFolder()+"/byRecordIds/"+
					oaiId+"-"+HarvestManager.lastOaiRequest, xmlHelper.getStringPretty(rEl));
		}
		
		LOG.debug("XML response");
		LOG.debug(harvestOutResponse);
		File outputFolder = new File(ACTUAL_OUTPUT_FOLDER+"/"+getFolder());
		if (!outputFolder.exists()) {
			outputFolder.mkdir();
		}
		Util.getUtil().spit(ACTUAL_OUTPUT_FOLDER+"/"+getFolder()+"/"+
				HarvestManager.lastOaiRequest, harvestOutResponse);
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
					if (af.contains("byRecordIds")) {
						continue;
					}
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
