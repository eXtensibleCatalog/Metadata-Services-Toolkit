package xc.mst.services.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.services.MetadataService;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

public class TestTransformation {

	
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
	
	/**
	 * List of Input Records as XML String
	 */
	public HashMap<String,String> inputRecords = new HashMap<String, String>();
	
	/**
	 * List of Output Records as XML String
	 */
	public HashMap<String,String> processedRecords = new HashMap<String, String>();
	
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
			
			// Initialize Record Service
			recordService = new DefaultRecordService();

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
			
			// Read input and expected output files from package
			readRecordsFromPackage();
			
			// Add the MARC Records to be processed
			addUnprocessedRecordFromFiles();
			
			Thread.sleep(1000);
			
			// Commit the records
			SolrIndexManager.getInstance().commitIndex();
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
			MetadataService.runService(transformationServiceId, -1);
			SolrIndexManager.getInstance().commitIndex();
			Thread.sleep(1000);
			
			// Get the transformed records
			RecordList records = recordService.getAll();
			

			// Prepare a map of input vs output records
			for(Record record: records)
			{
				
				
				if(record.getProcessedFrom().size() != 0)
				{
					// Get the record ID
					String startTag = "<marc:controlfield tag=\"001\">";
					String endTag = "</marc:controlfield>";
					String xml = record.getProcessedFrom().get(0).getOaiXml();
					
					String recordID = xml.substring(xml.indexOf(startTag)+startTag.length(),xml.indexOf(endTag) );
					
					if(recordList.contains(recordID)){
						// Add to processed records
						processedRecords.put(recordID, record.getOaiXml());
						//Delete the record
						recordService.delete(record);
					}
				}
			
			}
			
			// Compare the results with the baseline results
			for (String record : recordList) {
				
				// Assert true if the records are equal
				Assert.assertEquals(removeTrailingNewLine(processedRecords.get(record)), removeTrailingNewLine(baseProcessedRecords.get(record)));

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
	 */
	public void addUnprocessedRecordFromFiles() throws DataException, IOException
	{
		ProviderDAO providerDao = new DefaultProviderDAO();
		FormatDAO formatDao = new DefaultFormatDAO();
		ServiceDAO serviceDao = new DefaultServiceDAO();
		RecordService recordService = new DefaultRecordService();

		for (String file : inputRecords.keySet()) {

			Record record = new Record();
			record.setOaiXml(inputRecords.get(file));
			record.setFormat(formatDao.getById(1));
			record.setProvider(providerDao.getById(1));
			record.setOaiIdentifierBase("oai:rochester");
			record.setProvider(providerDao.getById(1));
			record.addInputForService(serviceDao
					.getById(transformationServiceId));
			// Add unprocessed records to index manager
			recordService.insert(record);

		}
	}
	
	/**
	 * Reads the input and expected output records from the files in the packages into appropriate lists
	 * @throws IOException
	 */
	private void readRecordsFromPackage() throws IOException{
		
		for (String file : recordList) {

		// Read the input records from package
		inputRecords.put(file, readUnicodeFile(inputDirPath+"/"+file+".xml"));
		
		// Read expected output records from package
		baseProcessedRecords.put(file, readUnicodeFile(outputDirPath+"/"+file+".xml"));
		
		}
	}
	
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
	       
	     }
	     String ret = buffer.toString();
	    
	     return ret;
	   }
		
	
	private String removeTrailingNewLine(String line){
		
		 while(line.endsWith("\n"))
			 line = line.substring(0,line.length() - 1 );
		 
		 return line;
	}
}
