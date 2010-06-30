/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.common.test.BaseTest;
import xc.mst.dao.DataException;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.helper.TestHelper;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.RecordService;
import xc.mst.services.MetadataService;
import xc.mst.services.MetadataServiceFactory;
import xc.mst.utils.index.SolrIndexManager;

public class TestTransformation extends BaseTest {

	
	/**
	 * ID of the Transformation Service 
	 */
	private static int transformationServiceId = 1;

	/**
	 * Package of the input files for the transformation service
	 */
	private String inputDirPath = "/xc/mst/services/test/resources/transformation/input";

	/**
	 * Package of the expected output files for the transformation service
	 */
	private String outputDirPath = "/xc/mst/services/test/resources/transformation/output";
	
	/**
	 * Record ID's of the records to be tested
	 */
	public ArrayList<String> recordList = new ArrayList<String>();
	
	
	public Hashtable<String, Long>  inputRecordIDMap =  new Hashtable<String, Long>();
	
	/**
	 * List of Input Records as XML String
	 */
	public HashMap<String,String> inputRecords = new HashMap<String, String>();
	
	/**
	 * List of Output Records as XML String
	 */
	public HashMap<String,String> processedRecords = new HashMap<String, String>();
	
	
	public Hashtable<String , List<Record>> transformedRecords = new Hashtable<String, List<Record>>();
	
	public Hashtable<String , ArrayList<Document>> baseRecords = new Hashtable<String, ArrayList<Document>>();
	
	
	/**
	 * List of Expected Output Records as XML String
	 */
	public HashMap<String,String> baseProcessedRecords = new HashMap<String, String>();
	
	/**
	 * Record service
	 */
	RecordService recordService;
	
	/**
	 * Executes all the preliminary steps for the test
	 */
	@BeforeClass
	public void beforeClass(){
		
		try {
			
		   	 // Initialize Solr, database, log before testing
		   	 TestHelper helper = TestHelper.getInstance(); 

			// Initialize Record Service
			recordService = (RecordService)getBean("RecordService");

			// List of all test records
			recordList.add("1048559");
			recordList.add("1071900");
			recordList.add("1299843");
			recordList.add("1472600");
			recordList.add("1807351");
			recordList.add("18206");
			recordList.add("2510091");
			recordList.add("2944145");
			recordList.add("3349023");
			recordList.add("3395662");
			recordList.add("3724338");
			recordList.add("37294");
			recordList.add("3768890");
			recordList.add("3949333");
			recordList.add("4164761");
			recordList.add("1177208");
			recordList.add("1586760");
			recordList.add("1714983");
			
			// Read input and expected output files from package
			readRecordsFromPackage();
			
			// Add the MARC Records to be processed
			addUnprocessedRecordFromFiles();
			
			Thread.sleep(1000);
			
			// Commit the records
			((SolrIndexManager)getBean("SolrIndexManager")).commitIndex();
			Thread.sleep(1000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Test to be performed on transformation service. Compares the results from the transformation service 
	 * with the expected results stored in the package. 
	 */
	@Test(groups = { "baseTests" }, enabled = true)
	public void test(){

		try
		{

			// Run Transformation Service
			MetadataService ms  = new MetadataServiceFactory().getService(transformationServiceId);
			ms.runService(transformationServiceId, -1);;
			((SolrIndexManager)getBean("SolrIndexManager")).commitIndex();
			Thread.sleep(1000);
			
			// Prepare the list of transformed records
			for (String recordNRUID : inputRecordIDMap.keySet()) {
				
				Long SOLRID = inputRecordIDMap.get(recordNRUID);
				List<Record> list =  recordService.getByProcessedFrom(SOLRID.longValue());
				transformedRecords.put(recordNRUID, list);
				
			}
			
//			RecordList records = recordService.getAll();

			// Prepare a list of base output records
			for(String recordNRUID: baseRecords.keySet()){
				
				ArrayList<Document> baseRecordList = baseRecords.get(recordNRUID);
				List<Record> transformedRecordList = transformedRecords.get(recordNRUID);
				ArrayList<Document> transformedDocumentList = new ArrayList<Document>();

				StringReader sr = null;
				StringWriter sw = null;
				String transformedXML = null;
				SAXBuilder sb = new SAXBuilder();
				XMLOutputter xOut = new XMLOutputter();
				
				for (Record record : transformedRecordList) {
					sr = new StringReader(record.getOaiXml());
					transformedDocumentList.add(sb.build(sr));
					sr.close();
				}
				
				if(baseRecordList.size() != transformedRecordList.size()){
				
					System.out.println("baseRecordList:"+baseRecordList.size());
					System.out.println("transformedRecordList:"+transformedRecordList.size());
					Assert.assertFalse(true, "Number of frbrized records generated not equal.");
				
				}

				else {
					
					for (Document transformedRecord : transformedDocumentList) {
						
						boolean match = false;
						for (Document baseRecord : baseRecordList) {	

							/*
							 * BDA 2010-05-05 - XCRecord currently only exists in the MARCToXCTransformation project
							 * 
							// Compare frbr levels
							if( !transformedRecord.getRootElement().getChild("entity", XCRecord.XC_NAMESPACE).getAttributeValue("type")
							.equals(baseRecord.getRootElement().getChild("entity", XCRecord.XC_NAMESPACE).getAttributeValue("type")))
							//if(!baseRecordFRBRLevel.equals(transformedRecordFRBRLevel))
								continue;
							
							// Compare content
							else{
								//Remove id
								transformedRecord.getRootElement().getChild("entity", XCRecord.XC_NAMESPACE).removeAttribute("id");
								baseRecord.getRootElement().getChild("entity", XCRecord.XC_NAMESPACE).removeAttribute("id");
								
								// Remove link elements
								transformedRecord.getRootElement().getChild("entity", XCRecord.XC_NAMESPACE).removeChildren("link", XCRecord.XC_NAMESPACE);
								baseRecord.getRootElement().getChild("entity", XCRecord.XC_NAMESPACE).removeChildren("link", XCRecord.XC_NAMESPACE);
								
								sw = new StringWriter();
								xOut.output(baseRecord, sw);
								String baseXML = sw.toString();
								sw.flush();
								sw.close();
								
								sw = new StringWriter();
								xOut.output(transformedRecord, sw);
								transformedXML = sw.toString();
								sw.flush();
								sw.close();
								
								if(baseXML.equals(transformedXML)){
									match = true;
									break;
								}

							}
							*/
						}
						
						if(!match){
							System.out.println(transformedXML);
							Assert.assertFalse(true, "The content of the records do not match.");
							break;
							
							}
						
					}
					
				}
			}
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	@AfterClass
	public void afterClass(){
		// Delete the records added as part of test
	
	}
	
	/**
	 * Add the input records read from the package into the index manager for processing
	 * @throws DataException
	 * @throws IOException
	 * @throws IndexException 
	 */
	public void addUnprocessedRecordFromFiles() throws DataException, IOException, IndexException
	{
		ProviderDAO providerDao = (ProviderDAO)getBean("ProviderDAO");
		FormatDAO formatDao = (FormatDAO)getBean("FormatDAO");
		ServiceDAO serviceDao = (ServiceDAO)getBean("ServiceDAO");
		RecordService recordService = (RecordService)getBean("RecordService");

		for (String file : inputRecords.keySet()) {

			Record record = new Record();
			record.setOaiXml(inputRecords.get(file));
			record.setFormat(formatDao.getById(1));
			record.setProvider(providerDao.getById(1));
			record.setOaiIdentifier("oai:rochester");
			record.setProvider(providerDao.getById(1));
			record.addInputForService(serviceDao.getById(transformationServiceId));
			// Add unprocessed records to index manager
			recordService.insert(record);
			
			inputRecordIDMap.put(file, record.getId());
		}
	}
	
	/**
	 * Reads the input and expected output records from the files in the packages into appropriate lists
	 * @throws IOException
	 * @throws JDOMException 
	 */
	private void readRecordsFromPackage() throws IOException, JDOMException {
		StringReader sr = null;
		SAXBuilder sb = new SAXBuilder();


		for (String file : recordList) {

			// Read the input records from package
			
			inputRecords.put(file, readUnicodeFile(inputDirPath+"/"+file+".xml"));	
			
			
			// Read expected output records from package
			int i=1;
			ArrayList<Document> frbrizedBaseOutputRecords = new ArrayList<Document>();
			while(true){
				
				String baseOutputRecordFile = outputDirPath+"/r"+file+"/"+i+".xml";
				if(readUnicodeFile(baseOutputRecordFile) == null)
					break;
				else{
					sr = new StringReader(readUnicodeFile(baseOutputRecordFile));
					frbrizedBaseOutputRecords.add(sb.build(sr));
				}
				i++;	
			}
			baseRecords.put(file, frbrizedBaseOutputRecords);
		}
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private String readUnicodeFile(String file)
	{
	     StringBuffer buffer = null;

	     InputStreamReader isr = null;
	     try {
	       isr = new InputStreamReader(TestTransformation.class.getResourceAsStream(file), "UTF-8");

	       buffer = new StringBuffer();
	       int ch;
	       while ((ch = isr.read()) > -1) {
	         buffer.append((char)ch);
	       }
	       if (isr != null)
	         isr.close();
	     } catch (Exception ex) {
	       return null;
	     }
	     String ret = buffer.toString();
	    
	     return ret;
	   }
		
	/**
	 * 
	 * @param line
	 * @return
	 */
	private String removeTrailingNewLine(String line){
		
		 while(line.endsWith("\n"))
			 line = line.substring(0,line.length() - 1 );
		 
		 return line;
	}
}
