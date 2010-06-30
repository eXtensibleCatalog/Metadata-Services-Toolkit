package xc.mst.service.impl.test;

import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.manager.BaseService;
import xc.mst.repo.Repository;
import xc.mst.utils.Util;

public class TestRepository extends BaseService implements Repository {
	
	private static final Logger LOG = Logger.getLogger(TestRepository.class);
	
	public final static String INPUT_RECORDS_DIR = "test/input_records";
	public final static String EXPECTED_OUTPUT_RECORDS = "test/expected_output_records";
	public final static String ACTUAL_OUTPUT_RECORDS = "build/test/actual_output_records";
	
	protected Set<String> inputFileNames = new TreeSet<String>();
	//protected int inputFilesIterator = 0;
	protected Iterator inputFilesIterator = null;
	//protected Map<Record, String> inputRecordFileNames = new HashMap<Record, String>();
	protected Map<String, List<Record>> outputFiles = new HashMap<String, List<Record>>();
	protected TLongObjectHashMap repo = new TLongObjectHashMap();
	protected Map<String, List<Record>> successorMap = new HashMap<String, List<Record>>();
	protected String folderName = null;
	protected String basePath = null;
	protected String currentFile = null;
	
	protected DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	public String getName() {
		return this.folderName;
	}
	public void setName(String folderName) {
		this.folderName = folderName;
		basePath = new File(".").getAbsolutePath();
		
		try {
			File folder = new File(INPUT_RECORDS_DIR+"/"+folderName);
			for (String fileName : folder.list()) {
				if (!fileName.contains(".svn")) {
					fileName = new File(fileName).getName();
					inputFileNames.add(fileName);
				}
			}
		} catch (Throwable t) {
			Util.getUtil().throwIt(t);
		}
	}
	
	public int getSize() {
		return 0;
	}
	
	public void beginBatch() {
	}
	
	public void endBatch() {
		Format format = Format.getPrettyFormat();
		XMLOutputter xmlOutputter = new XMLOutputter(format);
		File outFolder = new File(ACTUAL_OUTPUT_RECORDS+"/"+folderName);
		if (!outFolder.exists()) {
			outFolder.mkdir();
		} else {
			for (String prevOutFile : outFolder.list()) {
				LOG.debug("deleting file: "+ACTUAL_OUTPUT_RECORDS+"/"+folderName+"/"+prevOutFile);
				new File(ACTUAL_OUTPUT_RECORDS+"/"+folderName+"/"+prevOutFile).delete();
			}
		}
		for (Map.Entry<String, List<Record>> me : outputFiles.entrySet()) {
			String fileName = me.getKey();
			List<Record> records = me.getValue();
			if (records != null) {
				File outFile = null;
				PrintWriter pw = null;
				try {
					outFile = new File(ACTUAL_OUTPUT_RECORDS+"/"+folderName+"/"+fileName);
					LOG.debug("writing outFile: "+outFile);
					pw = new PrintWriter(outFile);
					pw.println("<records xmlns=\"http://www.openarchives.org/OAI/2.0/\">");
					for (Record r : records) {
						pw.println(xmlOutputter.outputString(getRecordService().createJDomElement(r, null)));
					}
					pw.println("</records>");
				} catch (Throwable t) {
					LOG.error("file failed: "+fileName);
					LOG.error("", t);
				} finally {
					try {
						
						pw.close();
					} catch (Throwable t) {
						LOG.error("file close failed: "+fileName);
						LOG.error("", t);
					}
				}
			}
		}
	}
	
	public void installOrUpdateIfNecessary() {
		throw new RuntimeException("not implemented");
	}
	
	public void addRecord(Record r) {
		Record previousOutputRecord = (Record)repo.get(r.getId());
		if (previousOutputRecord != null) {
			if (Record.DELETED == r.getStatus()) {
				previousOutputRecord.setStatus(Record.DELETED);
			} else if (Record.ACTIVE == r.getStatus() || 
					Record.HELD == r.getStatus()) {
				previousOutputRecord.setStatus(Record.UPDATE_REPLACE);
			}
		}
		repo.put(r.getId(), r);
		List<Record> outputRecordsInFile = outputFiles.get(this.currentFile);
		if (outputRecordsInFile == null) {
			outputRecordsInFile = new ArrayList<Record>();
			outputFiles.put(this.currentFile, outputRecordsInFile);
		}
		outputRecordsInFile.add(r);
	}

	public void addRecords(List<Record> records) {
		for (Record r : records) {
			addRecord(r);
		}
	}
	
	public List<Record> getRecords(Date from, Date until, Long startingId, 
			xc.mst.bo.provider.Format inputFormat,  xc.mst.bo.provider.Set inputSet) {
		if (inputFilesIterator == null) {
			inputFilesIterator = inputFileNames.iterator();
		}
		if (inputFilesIterator.hasNext()) {
			String fileName = (String)inputFilesIterator.next();
			List<Record> inputRecords = new ArrayList<Record>();
			try {
				this.currentFile = fileName;
				File file2process = new File(INPUT_RECORDS_DIR+"/"+folderName+"/"+fileName);
				LOG.debug("file2process: "+file2process);
				
				DocumentBuilder db = dbf.newDocumentBuilder();
				DOMBuilder domBuilder = new DOMBuilder();
				Document doc = domBuilder.build(db.parse(file2process));
				
				Element records = doc.getRootElement();
				for (Object recordObj : records.getChildren("record", doc.getRootElement().getNamespace())) {
					Element record = (Element)recordObj;
					Record in = getRecordService().parse(record);
					inputRecords.add(in);
					//this.inputRecordFileNames.put(in, fileName);
				}
			} catch (Throwable t) {
				Util.getUtil().throwIt(t);
			}
			return inputRecords;
		} else {
			return null;
		}

	}
	
	public Record getRecord(String oaiId) {
		return (Record)repo.get(Long.parseLong(oaiId.split(":")[3]));
	}
	
	public Record getRecord(long id) {
		return (Record)repo.get(id);
	}
	
	public List<Record> getPredecessors(Record r) {
		throw new RuntimeException("not implemented");
	}
	
	public void injectSuccessors(Record r) {
		List<Record> succs = successorMap.get(r.getOaiIdentifier());
		if (succs != null) {
			r.getSuccessors().addAll(succs);
		}
	}
	@Override
	public List<Long> getPredecessorIds(Record r) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Provider getProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void installOrUpdateIfNecessary(String previousVersion,
			String currentVersion) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setProvider(Provider p) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setService(Service s) {
		// TODO Auto-generated method stub
		
	}

}
