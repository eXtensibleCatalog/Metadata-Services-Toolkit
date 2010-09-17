/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.service.impl.test;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.manager.BaseService;
import xc.mst.repo.Repository;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;

public class TestRepository extends BaseService implements Repository {
	
	private static final Logger LOG = Logger.getLogger(TestRepository.class);
	
	public final static String INPUT_RECORDS_DIR = "../test/input_records";
	public final static String EXPECTED_OUTPUT_RECORDS = "../test/expected_output_records";
	public final static String ACTUAL_OUTPUT_RECORDS = "test/actual_output_records";
	
	protected java.util.Set<String> inputFileNames = new TreeSet<String>();
	//protected int inputFilesIterator = 0;
	protected Iterator inputFilesIterator = null;
	//protected Map<Record, String> inputRecordFileNames = new HashMap<Record, String>();
	protected Map<String, List<Record>> outputFiles = new HashMap<String, List<Record>>();
	protected TLongObjectHashMap repo = new TLongObjectHashMap();
	protected Map<String, List<Record>> successorMap = new HashMap<String, List<Record>>();
	protected String folderName = null;
	protected String basePath = null;
	protected String currentFile = null;
	protected XmlHelper xmlHelper = new XmlHelper();
	
	public void populatePredSuccMaps(TLongObjectHashMap predKeyedMap, TLongObjectHashMap succKeyedMap) {}
	
	public Date getLastModified() {
		return null;
	}
	
	public int getNumRecords() {
		return -1;
	}
	
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
		LOG.debug("beginBatch");
	}
	
	public void endBatch() {
		if (!inputFilesIterator.hasNext()) {
			LOG.debug("endBatch");
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
						pw = new PrintWriter(outFile, "UTF-8");
						pw.println("<records xmlns=\"http://www.openarchives.org/OAI/2.0/\">");
						for (Record r : records) {
							LOG.debug("r.getService(): "+r.getService());
							pw.println(xmlHelper.getStringPretty(getRecordService().createJDomElement(r, null)));
						}
						pw.println("</records>");
					} catch (Throwable t) {
						LOG.error("file failed: "+fileName);
						LOG.error("", t);
					} finally {
						try {
							LOG.debug("closing file: "+fileName);
							pw.close();
						} catch (Throwable t) {
							LOG.error("file close failed: "+fileName);
							LOG.error("", t);
						}
					}
				}
			}
		}
	}
	
	public void installOrUpdateIfNecessary() {
		throw new RuntimeException("not implemented");
	}
	
	public void addRecord(Record r) {
		for (InputRecord ir : r.getPredecessors()) {
			LOG.debug("((Record)ir).getOaiIdentifier(): "+((Record)ir).getOaiIdentifier());
			LOG.debug("((Record)ir).getHarvestedOaiIdentifier(): "+((Record)ir).getHarvestedOaiIdentifier());
			List<Record> succs = successorMap.get(((Record)ir).getHarvestedOaiIdentifier());
			if (succs == null) {
				succs = new ArrayList<Record>();
				successorMap.put(((Record)ir).getHarvestedOaiIdentifier(), succs);
			}
			if (!succs.contains(r)) {
				LOG.debug("r.getOaiIdentifier(): "+r.getOaiIdentifier());
				succs.add(r);
			}
		}
		Record previousOutputRecord = (Record)repo.get(r.getId());
		if (previousOutputRecord != null) {
			previousOutputRecord.setStatus(Record.REPLACED);
		}
		repo.put(r.getId(), r);
		LOG.debug("r.getStatus(): "+r.getStatus());
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
			if (!fileName.endsWith(".xml")) {
				return inputRecords;
			}
			try {
				this.currentFile = fileName;
				File file2process = new File(INPUT_RECORDS_DIR+"/"+folderName+"/"+fileName);
				LOG.debug("file2process: "+file2process);
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(new FileInputStream(file2process));
				
				Element records = doc.getRootElement();
				LOG.debug("records: "+records);
				Element listRecords = records.getChild("ListRecords", records.getNamespace());
				LOG.debug("listRecords: "+listRecords);
				if (listRecords != null) {
					records = listRecords;
				}
				for (Object recordObj : records.getChildren("record", records.getNamespace())) {
					Element record = (Element)recordObj;
					Record in = getRecordService().parse(record);
					String oaiId = in.getHarvestedOaiIdentifier();
					LOG.debug("in.getId(): "+in.getId());
					LOG.debug("oaiId: "+oaiId);
					int idx1 = oaiId.lastIndexOf("/");
					if (idx1 == -1) {
						idx1 = oaiId.lastIndexOf(":");
					}
					if (idx1 != -1) {
						LOG.debug("oaiId.substring(idx1+1): "+oaiId.substring(idx1+1));
						in.setId(Long.parseLong(oaiId.substring(idx1+1)));
					}
					LOG.debug("in.getId(): "+in.getId());
					LOG.debug("in.getStatus(): "+in.getStatus());
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

	public List<Long> getPredecessorIds(Record r) {
		// TODO Auto-generated method stub
		return null;
	}

	public Provider getProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}

	public void installOrUpdateIfNecessary(String previousVersion,
			String currentVersion) {
		// TODO Auto-generated method stub
		
	}

	public void setProvider(Provider p) {
		// TODO Auto-generated method stub
		
	}

	public void setService(Service s) {
		// TODO Auto-generated method stub
		
	}

	public List<Record> getRecordHeader(Date from, Date until, Long startingId, Format inputFormat, xc.mst.bo.provider.Set inputSet) {
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get number of records that satisfy the given criteria
	 * 
	 * @param from
	 * @param until
	 * @param startingId
	 * @param inputFormat
	 * @param inputSet
	 * @return
	 */
	public long getRecordCount(Date from, Date until, Format inputFormat, xc.mst.bo.provider.Set inputSet) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void populatePredecessors(TLongHashSet predecessors) {
		//do nothing
	}
	
	public void injectSuccessorIds(Record r) {
		List<Record> succs = successorMap.get(r.getHarvestedOaiIdentifier());
		if (succs != null) {
			for (Record succ : succs) {
				Record out = new Record();
				out.setId(succ.getId());
				r.getSuccessors().add(out);	
			}
		}
	}
}
