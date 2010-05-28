package xc.mst.service.impl.test;

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

import xc.mst.bo.record.Record;
import xc.mst.common.test.BaseTest;
import xc.mst.services.impl.GenericMetadataService;

public class BaseMetadataServiceTest extends BaseTest {
	
	public final static String INPUT_RECORDS_DIR = "test/input_records";
	public final static String EXPECTED_OUTPUT_RECORDS = "test/expected_output_records";
	public final static String ACTUAL_OUTPUT_RECORDS = "build/test/actual_output_records";
	
	protected DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	protected void processRecords(GenericMetadataService service) {
		String folderStr = System.getenv("MST_SERVICE_TEST_FOLDER");
		String fileStr = System.getenv("MST_SERVICE_TEST_FILE");
		
		LOG.debug("folderStr: "+folderStr);
		LOG.debug("fileStr: "+fileStr);
		
		File inputRecordsDir = new File(INPUT_RECORDS_DIR);
		//File expectedOutputRecordsDir = new File(EXPECTED_OUTPUT_RECORDS);
		//File actualOutputRecordsDir = new File(ACTUAL_OUTPUT_RECORDS);
		
		Map<String, List<File>> folderFilesMap = new HashMap<String, List<File>>();
		
		if (!StringUtils.isEmpty(folderStr)) {
			List<File> files2process = new ArrayList<File>();
			if (!StringUtils.isEmpty(fileStr)) {
				files2process.add(new File(INPUT_RECORDS_DIR+"/"+folderStr+"/"+fileStr));
			} else {
				File folder = new File(INPUT_RECORDS_DIR+"/"+folderStr);
				for (String fileStr2 : folder.list()) {
					fileStr2 = new File(fileStr2).getName();
					files2process.add(new File(INPUT_RECORDS_DIR+"/"+folderStr+"/"+fileStr2));	
				}
			}
			folderFilesMap.put(folderStr, files2process);
		} else if (!StringUtils.isEmpty(fileStr)) {
			throw new RuntimeException("you can't specific a file without a folder");
		} else {
			for (String folderStr2 : inputRecordsDir.list()) {
				folderStr2 = new File(folderStr2).getName();
				LOG.debug("folderStr2: "+folderStr2);
				File folder = new File(INPUT_RECORDS_DIR+"/"+folderStr2);
				List<File> files2process = new ArrayList<File>();
				for (String fileStr2 : folder.list()) {
					fileStr2 = new File(fileStr2).getName();
					LOG.debug("fileStr2: "+fileStr2);
					files2process.add(new File(INPUT_RECORDS_DIR+"/"+folderStr2+"/"+fileStr2));	
				}
				folderFilesMap.put(folderStr2, files2process);
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
						Record in = new Record(record);
						
						List<Record> outs = service.process(in);
						for (Record out : outs) {
							pw.println(out.getRecordXml());
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

	}

}
