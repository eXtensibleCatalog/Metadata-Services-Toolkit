package xc.mst.service.impl.test;

import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import xc.mst.bo.record.Record;
import xc.mst.common.test.BaseTest;
import xc.mst.services.impl.GenericMetadataService;

public class BaseMetadataServiceTest extends BaseTest {
	
	public final static String INPUT_RECORDS_DIR = "test/input_records";
	public final static String EXPECTED_OUTPUT_RECORDS = "test/expected_output_records";
	public final static String ACTUAL_OUTPUT_RECORDS = "build/test/actual_output_records";
	
	protected DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	protected void processRecords(GenericMetadataService service) {
		Map<String, List<Record>> outputFiles = new HashMap<String, List<Record>>();
		TLongObjectHashMap repo = new TLongObjectHashMap();
		
		long id = repositoryDAO.restIdSequence(1);
		String inFolderStr = System.getenv("MST_SERVICE_TEST_FOLDER");
		LOG.debug("folderStr: "+inFolderStr);
		
		File inputRecordsDir = new File(INPUT_RECORDS_DIR);
		
		List<String> folderStrs = new ArrayList<String>();
		
		if (!StringUtils.isEmpty(inFolderStr)) {
			folderStrs.add(inFolderStr);
		} else {
			for (String folderStr2 : inputRecordsDir.list()) {
				LOG.debug("folderStr2: "+folderStr2);
				if (!folderStr2.contains(".svn")) {
					folderStr2 = new File(folderStr2).getName();
					folderStrs.add(folderStr2);
				}
			}
		}
		
		Map<String, List<File>> folderFilesMap = new HashMap<String, List<File>>();
		for (String folderStr : folderStrs) {
			List<File> files2process = new ArrayList<File>();
			File folder = new File(INPUT_RECORDS_DIR+"/"+folderStr);
			for (String fileStr2 : folder.list()) {
				if (!fileStr2.contains(".svn")) {
					fileStr2 = new File(fileStr2).getName();
					files2process.add(new File(INPUT_RECORDS_DIR+"/"+folderStr+"/"+fileStr2));
				}
			}
			folderFilesMap.put(folderStr, files2process);
		}
		
		Format format = Format.getPrettyFormat();
		XMLOutputter xmlOutputter = new XMLOutputter(format);
		
		LOG.debug("dbf.getClass(): "+dbf.getClass());
		for (String folderStr2 : folderFilesMap.keySet()) {
			LOG.debug("folderStr2: "+folderStr2);
			for (File file2process : folderFilesMap.get(folderStr2)) {
				LOG.debug("file2process.getName(): "+file2process.getName());
				List<Record> recordsInFile = new ArrayList<Record>();
				outputFiles.put(ACTUAL_OUTPUT_RECORDS+"/"+folderStr2+"/"+file2process.getName(), recordsInFile);
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					DOMBuilder domBuilder = new DOMBuilder();
					Document doc = domBuilder.build(db.parse(file2process));
					
					Element records = doc.getRootElement();
					for (Object recordObj : records.getChildren("record")) {
						Element record = (Element)recordObj;
						Record in = recordService.parse(record);
						
						List<Record> outs = service.process(in);
						for (Record out : outs) {
							recordsInFile.add(out);
							LOG.debug("out: "+out);
						}
					}
				} catch (Throwable t) {
					LOG.error("file failed: "+file2process.getAbsolutePath());
					LOG.error("", t);
				}
			}
			
		}
		
		LOG.debug("dbf.getClass(): "+dbf.getClass());
		for (String folderStr2 : folderFilesMap.keySet()) {
			LOG.debug("folderStr2: "+folderStr2);
			for (File file2process : folderFilesMap.get(folderStr2)) {
				LOG.debug("file2process.getName(): "+file2process.getName());
				File outFile = null;
				PrintWriter pw = null;
				try {
					
					
					
					LOG.debug("outfolder: "+ACTUAL_OUTPUT_RECORDS+"/"+folderStr2);
					File outFolder = new File(ACTUAL_OUTPUT_RECORDS+"/"+folderStr2);
					if (!outFolder.exists()) {
						outFolder.mkdir();
					} else {
						for (String prevOutFile : outFolder.list()) {
							new File(prevOutFile).delete();
						}
					}
					outFile = new File(ACTUAL_OUTPUT_RECORDS+"/"+folderStr2+"/"+file2process.getName());
					pw = new PrintWriter(outFile);
					pw.println("<records>");
					DocumentBuilder db = dbf.newDocumentBuilder();
					DOMBuilder domBuilder = new DOMBuilder();
					LOG.debug("db.getClass(): "+db.getClass());
					
					Document doc = domBuilder.build(db.parse(file2process));
					
					Element records = doc.getRootElement();
					for (Object recordObj : records.getChildren("record")) {
						Element record = (Element)recordObj;
						Record in = recordService.parse(record);
						
						List<Record> outs = service.process(in);
						for (Record out : outs) {
							LOG.debug("out: "+out);
							pw.println(xmlOutputter.outputString(recordService.createJDomElement(out)));
							//pw.println(out.getRecordXml());
						}
					}
					pw.println("</records>");
				} catch (Throwable t) {
					LOG.error("file failed: "+file2process.getAbsolutePath());
					LOG.error("", t);
				} finally {
					try {
						
						pw.close();
					} catch (Throwable t) {
						LOG.error("file close failed: "+file2process.getAbsolutePath());
						LOG.error("", t);	
					}
				}
			}
			
		}
		
		repositoryDAO.restIdSequence(id);
		
		//TODO now check for equality
	}

}
