package xc.mst.services.transformation.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
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
			
			String nextWork = null;
			String nextExpression = null;
			for (String fStr : inputFolderFileStrs) {
				LOG.debug("processing: "+fStr);
				if (fStr.contains("svn")) {
					continue;
				}
				boolean manifestationFound = false;
				String actualContents = getUtil().slurp(actualOutputFolderStr+"/"+fStr);
				Element rootEl = (Element)xmlHelper.getJDomDocument(actualContents).detachRootElement();
				Element actRecordEl = (Element)rootEl.getChildren("record", rootEl.getNamespace()).get(0);
				Element actHeaderEl = (Element)actRecordEl.getChild("header", rootEl.getNamespace());
				String status = actHeaderEl.getAttributeValue("status");
				if ("held".equals(status)) {
					recordId++;
					continue;
				}
				/*
				Element actMetadataEl = actRecordEl.getChild("metadata", rootEl.getNamespace());
				Element actFrbrEl = (Element)actMetadataEl.getChildren().get(0);
				Element actEntityEl = actFrbrEl.getChild("entity", actFrbrEl.getNamespace());
				String actType = actEntityEl.getAttributeValue("type");
				*/
				StringBuilder sb = new StringBuilder();
				sb.append("<records xmlns=\"http://www.openarchives.org/OAI/2.0/\">");
				while (true) {
					String contents = null;
					if (nextExpression != null) {
						if (nextWork != null) {
							contents = nextWork;
							nextWork = null;	
						} else {
							contents = nextExpression;
							nextExpression = null;
						}
					} else {
						String oeof = null;
						while (oeof == null || oeof.contains("svn")) {
							LOG.debug("oeofidx: "+oeofidx);
							if (oeofidx < origExpectedOutputFolderFileStrs.length) {
								oeof = origExpectedOutputFolderFileStrs[oeofidx++];	
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
					String type = frbr.getChild("entity", frbr.getNamespace()).getAttributeValue("type");
					//LOG.debug("type: "+type);
					if (manifestationFound && "work".equals(type)) {
						nextWork = contents;
					} else if (manifestationFound && "expression".equals(type)) {
						nextExpression = contents;
						break;
					} else if ("manifestation".equals(type)) {
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
				FileUtils.writeStringToFile(new File(newExpectedOutputFolder+"/"+fStr), sb.toString());
				inputIdx++;
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
