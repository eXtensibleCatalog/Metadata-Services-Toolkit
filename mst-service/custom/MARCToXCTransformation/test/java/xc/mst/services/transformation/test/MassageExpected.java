package xc.mst.services.transformation.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.testng.annotations.Test;

import xc.mst.repo.TestRepository;
import xc.mst.test.BaseMetadataServiceTest;
import xc.mst.utils.XmlHelper;

public class MassageExpected  extends BaseMetadataServiceTest {
	
	@Test
	public void massage() {
		try {
			XmlHelper xmlHelper = new XmlHelper();
			
			String inputFolderStr = TestRepository.INPUT_RECORDS_DIR+"/orig-186";
			String origExpectedOutputFolderStr = TestRepository.EXPECTED_OUTPUT_RECORDS+"/orig-186-0.2.9";
			String newExpectedOutputFolderStr = TestRepository.EXPECTED_OUTPUT_RECORDS+"/orig-186";
			String actualOutputFolderStr = TestRepository.ACTUAL_OUTPUT_RECORDS+"/orig-186";
			
			File inputFolder = new File(inputFolderStr);
			File origExpectedOutputFolder = new File(origExpectedOutputFolderStr);
			File newExpectedOutputFolder = new File(newExpectedOutputFolderStr);
			
			String[] inputFolderFileStrs = inputFolder.list();
			String[] origExpectedOutputFolderFileStrs = origExpectedOutputFolder.list();
			
			int oeofidx = 0;
			int inputIdx = 1;
			int recordId = 1;
			
			if (newExpectedOutputFolder.exists()) {
				FileUtils.deleteDirectory(newExpectedOutputFolder);
			}
			newExpectedOutputFolder.mkdir();
			
			
			String nextFile = null;
			//for (String fStr : inputFolderFileStrs) {
			for (int k=0; k<inputFolderFileStrs.length; k++) {
				String previousOrigExpectedManOaiId = null;
				String previousOrigExpected001 = null;
				boolean holdingInOutputFile = false;
				String fStr = inputFolderFileStrs[k];
				LOG.debug("processing: "+fStr);
				if (fStr.contains("svn")) {
					continue;
				}
				boolean manifestationOrHoldingFound = false;
				boolean manifestationFound = false;
				String actualContents = getUtil().slurp(actualOutputFolderStr+"/"+fStr);
				Element rootEl = (Element)xmlHelper.getJDomDocument(actualContents).detachRootElement();
				Element actRecordEl = (Element)rootEl.getChildren("record", rootEl.getNamespace()).get(0);
				Element actHeaderEl = (Element)actRecordEl.getChild("header", rootEl.getNamespace());
				String status = actHeaderEl.getAttributeValue("status");
				if ("held".equals(status) || "replaced".equals(status)) {
					recordId++;
					continue;
				}
				
				String inputContents = getUtil().slurp(inputFolderStr+"/"+fStr);
				Element inputRecordsEl = xmlHelper.getJDomDocument(inputContents).detachRootElement();
				Element inputRecordEl = (Element)inputRecordsEl.getChildren().get(0);
				Element inputMetadataEl = inputRecordEl.getChild("metadata", inputRecordsEl.getNamespace());
				Element inputMarc = (Element)inputMetadataEl.getChildren().get(0);
				String leader = inputMarc.getChildText("leader", inputMarc.getNamespace());
				String the001 = null;
				for (Object cfObj : inputMarc.getChildren("controlfield", inputMarc.getNamespace())) {
					Element cf = (Element)cfObj;
					if ("001".equals(cf.getAttributeValue("tag"))) {
						the001 = cf.getText();
					}	
				}
				LOG.debug("the001: "+the001);
				
				String nextInput004 = null;
				String nextInput001 = null;
				if (k+1 != inputFolderFileStrs.length) {
					String ic = getUtil().slurp(inputFolderStr+"/"+inputFolderFileStrs[k+1]);
					Element irsEl = xmlHelper.getJDomDocument(ic).detachRootElement();
					Element irEl = (Element)irsEl.getChildren().get(0);
					Element imEl = irEl.getChild("metadata", inputRecordsEl.getNamespace());
					Element im = (Element)imEl.getChildren().get(0);
					for (Object cfObj : im.getChildren("controlfield", im.getNamespace())) {
						Element cf = (Element)cfObj;
						if ("004".equals(cf.getAttributeValue("tag"))) {
							nextInput004 = cf.getText();
						} else if ("001".equals(cf.getAttributeValue("tag"))) {
							nextInput001 = cf.getText();
						}		
					}
				}
				LOG.debug("leader: "+leader);
				
				boolean inputIsHolding = false;
				char leader06 = leader.charAt(6);
				if(leader06 == 'u' || leader06 == 'v' || leader06 == 'x' || leader06 == 'y') {
					inputIsHolding = true;
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append("<records xmlns=\"http://www.openarchives.org/OAI/2.0/\">");
				while (true) {
					boolean usingPriorInput = false;
					String contents = null;
					if (nextFile != null) {
						contents = nextFile;
						nextFile = null;
						usingPriorInput = true;
					} else {
						String oeof = null;
						while (oeof == null || oeof.contains("svn")) {
							LOG.debug("oeofidx-1: "+(oeofidx-1));
							if (oeofidx < origExpectedOutputFolderFileStrs.length) {
								oeof = origExpectedOutputFolderFileStrs[oeofidx];
								oeofidx++;
							} else {
								break;
							}
						}
						if (oeof == null) {
							LOG.debug("out of oeofidxes");
							break;
						}
						//LOG.debug("origExpectedOutputFolderStr: "+origExpectedOutputFolderStr);
						//LOG.debug("oeof: "+origExpectedOutputFolderStr+"/"+oeof);
						contents = getUtil().slurp(origExpectedOutputFolderStr+"/"+oeof);
						//LOG.debug("contents: "+contents);
					}

					Element frbr = (Element)xmlHelper.getJDomDocument(contents).getRootElement().detach();
					Element entityEl = (Element)frbr.getChild("entity", frbr.getNamespace());
					String oaiId = entityEl.getAttributeValue("id"); 
						
					String type = frbr.getChild("entity", frbr.getNamespace()).getAttributeValue("type");
					LOG.debug("type: "+type);
					LOG.debug("manifestationFound: "+manifestationOrHoldingFound);
					if (manifestationOrHoldingFound && "work".equals(type)) {
						nextFile = contents;
						break;
					} else if ("holdings".equals(type)) {
						manifestationOrHoldingFound = true;
						LOG.debug("manifestationFound: "+manifestationFound);
						LOG.debug("usingPriorInput: "+usingPriorInput);
						if (!manifestationFound && !usingPriorInput) {
							nextFile = contents;
							break;
						}
						
						String expectedHolding001 = frbr.getChild("entity", frbr.getNamespace()).getChildText("recordID", frbr.getNamespace());
						LOG.debug("expectedHolding001: "+expectedHolding001);
						LOG.debug("nextInput001: "+nextInput001);
						if (expectedHolding001 != null && expectedHolding001.equals(nextInput001)) {
							nextFile = contents;
							break;
						}
					} else if ("manifestation".equals(type)) {
						previousOrigExpected001 = the001;
						previousOrigExpectedManOaiId = oaiId;
						manifestationOrHoldingFound = true;
						manifestationFound = true;
					}

					Element recordEl = new Element("record");
					
					Element headerEl = new Element("header");
					recordEl.addContent(headerEl);
					headerEl.setAttribute("status", "active");
					
					Element identifierEl = new Element("identifier");
					headerEl.addContent(identifierEl);
					identifierEl.setText("oai:mst.rochester.edu:marctoxctransformation/"+recordId++);
					
					Element dateStampEl = new Element("datestamp");
					headerEl.addContent(dateStampEl);
					
					Element predecessorsEl = new Element("predecessors");
					headerEl.addContent(predecessorsEl);
					
					Element predecessorEl = new Element("predecessor");
					predecessorsEl.addContent(predecessorEl);
					predecessorEl.setText("oai:mst.rochester.edu:"+inputIdx);
					
					Element metadataEl = new Element("metadata");
					recordEl.addContent(metadataEl);
					metadataEl.addContent(frbr);
					
					sb.append("\n"+xmlHelper.getStringPretty(recordEl));
					if (oeofidx == origExpectedOutputFolderFileStrs.length) {
						break;
					}
				}
				sb.append("\n</records>");
				FileUtils.writeStringToFile(new File(newExpectedOutputFolder+"/"+fStr), sb.toString(), "UTF-8");
				inputIdx++;
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
