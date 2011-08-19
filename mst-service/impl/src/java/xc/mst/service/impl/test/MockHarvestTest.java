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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
    protected Map<String, String> testFailures = new HashMap<String, String>();

    public String getRepoName() {
        return getUtil().normalizeName(getFolder());
    }

    public MockHarvestTest() {
        this.shouldValidateRepo = false;
    }

    public String getProviderUrl() {
        return "file://"+INPUT_FOLDER+"/"+getFolder();
    }

    public void incrementalHarvestTest(String lastOaiRequest) {
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        String testFolder = System.getenv("test.folder");
        if (StringUtils.isEmpty(testFolder)) {
            for (File f : new File(INPUT_FOLDER).listFiles()) {
                if (!f.getName().contains(".svn")) {
                    fileStrs.add(f.getName());
                }
            }
        } else {
            fileStrs.add(testFolder);
        }
        LOG.debug("fileStrs: "+fileStrs);
        return fileStrs;
    }

    protected String folderName = null;
    protected final String getFolder() {
        return folderName;
    }

    @Test
    public void startToFinish() throws Exception  {
        LOG.info("in startToFinish");
        for (String folderStr : getFolders()) {
            LOG.info("folderStr: "+folderStr);
            getRepositoryDAO().resetIdSequence(1);
            this.provider = null;
            this.folderName = folderStr;

            dropOldSchemas();
            LOG.info("after dropOldSchemas");

            installService();
            //getServicesService().addNewService(getServiceName());
            LOG.info("after installService");

            installProvider();
            LOG.info("after installProvider");
            LOG.debug("this.folderName: "+this.folderName);

            configureProcessingRules();
            LOG.info("after configureProcessingRules");

            String previousLastOaiRequest = HarvestManager.lastOaiRequest;
            while (true) {
                LOG.debug("createHarvestSchedule()-1");
                createHarvestSchedule();
                LOG.debug("createHarvestSchedule()-2");
                LOG.debug("waitUntilFinished()-1");
                waitUntilFinished();
                LOG.debug("waitUntilFinished()-2");
                LOG.debug("previousLastOaiRequest: "+previousLastOaiRequest);
                LOG.debug("HarvestManager.lastOaiRequest: "+HarvestManager.lastOaiRequest);
                if (HarvestManager.lastOaiRequest == null || (
                        previousLastOaiRequest != null && previousLastOaiRequest.equals(HarvestManager.lastOaiRequest))) {
                    break;
                } else {
                    previousLastOaiRequest = HarvestManager.lastOaiRequest;
                }
                LOG.debug("createHarvestSchedule()-1");
                incrementalHarvestTest(HarvestManager.lastOaiRequest);
                harvestOutRecordsFromMST();
                LOG.debug("createHarvestSchedule()-2");
            }
            compareAgainstExpectedOutput();
            waitUntilFinished();
        }

        finalTest();

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
        String folderStr = getFolder();

        File expectedOutputFolder = new File(EXPECTED_OUTPUT_FOLDER+"/"+folderStr);
        Set<String> expectedOutputFiles = new HashSet<String>();
        if (expectedOutputFolder.list() != null) {
            for (String ef : expectedOutputFolder.list()) {
                if (ef.endsWith(".xml")) {
                    expectedOutputFiles.add(ef);
                }
            }

            File actualOutputFolder  = new File(ACTUAL_OUTPUT_FOLDER+"/"+folderStr);
            if (!actualOutputFolder.exists()) {
                testFailures.put(folderStr, "folder expected, but wasn't produced.");
                return;
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
        }
    }

}
