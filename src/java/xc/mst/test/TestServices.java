/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
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

public class TestServices
{
	private static File unprocessedRecordsDir = new File("C:\\MetadataServicesToolkit\\MetadataServicesToolkit\\testRecordsForServices\\aggServiceInput");
	private static File processedRecordsDir = new File("C:\\MetadataServicesToolkit\\MetadataServicesToolkit\\testRecordsForServices\\aggServiceOutput");

	private static int serviceId = 3;

	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();

	/**
	 * Data access object for getting and updating providers
	 */
	private static ProviderDAO providerDao = new DefaultProviderDAO();

	/**
	 * Data access object for getting formats
	 */
	private static FormatDAO formatDao = new DefaultFormatDAO();

	/**
	 * Data access object for getting and updating services
	 */
	private static ServiceDAO serviceDao = new DefaultServiceDAO();

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

	// Set the location of the logger's configuration file
	static
	{
		PropertyConfigurator.configure(configuration.getProperty(Constants.CONFIG_LOGGER_CONFIG_FILE_LOCATION));
	}

	/**
	 * Builds the XML Document based on the record's OAI XML
	 */
	private static SAXBuilder builder = new SAXBuilder();

	/**
	 * Used to format timestamps for the results of the tests
	 */
	private static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	public static void main(String[] args) throws DataException, IOException, JDOMException
	{
		try
		{
			addUnprocessedRecordFromFiles(unprocessedRecordsDir);
			Thread.sleep(1000);
			// TODO removed IndexManager
			//IndexManager.getInstance().maybeReOpen();
			Thread.sleep(1000);
			System.out.println(formatter.format(new Date()));
			MetadataService.runService(serviceId, -1);
			System.out.println(formatter.format(new Date()));
			Thread.sleep(1000);
			// TODO removed IndexManager
			//IndexManager.getInstance().maybeReOpen();
			Thread.sleep(1000);
			RecordList records = recordService.getAll();
			for(Record record: records)
			{
				if(record.getService().getId() == serviceId)
					saveRecordToFile(processedRecordsDir, record);
				recordService.delete(record);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

			RecordList records = recordService.getAll();
			for(Record record: records)
			{
				if(record.getService().getId() == serviceId)
					saveRecordToFile(processedRecordsDir, record);
				recordService.delete(record);
			}
		}
		finally
		{
			// TODO removed IndexManager
			// IndexManager.closeIndex();
		}
	}

	public static void addUnprocessedRecordFromFiles(File inputDirectory) throws DataException, IOException
	{
		File[] testRecords = inputDirectory.listFiles();

		for(int counter = 0; counter < testRecords.length; counter++)
		{
			File currentRecord = testRecords[counter];

			Record record = new Record();

			record.setOaiXml(readUnicodeFile(currentRecord));
			record.setFormat(formatDao.getById(2));
			record.setProvider(providerDao.getById(2));
			record.setOaiIdentifierBase("oai:rochester");
			record.setProvider(providerDao.getById(1));
			record.addInputForService(serviceDao.getById(serviceId));
			recordService.insert(record);
		}
	}

	/**
	 * The readInputFile method did not handle unicode correctly.  I found this method at
	 * http://www.devarticles.com/c/a/Java/J2ME-and-Unicode/3/ which should work with unicode
	 * characters.
	 *
	 * Reads the input file into a String and return it
	 *
	 * @param file The file to read
	 * @return The contents of the file
	 */
	private static String readUnicodeFile(File file)
	{
	     StringBuffer buffer = null;

	     InputStreamReader isr = null;
	     try {
	       isr = new InputStreamReader(new FileInputStream(file), "UTF-8");

	       buffer = new StringBuffer();
	       int ch;
	       while ((ch = isr.read()) > -1) {
	         buffer.append((char)ch);
	       }
	       if (isr != null)
	         isr.close();
	     } catch (Exception ex) {
	       System.out.println(ex);
	     }
	     return buffer.toString();
	   }

	static int counter = 0;

	@SuppressWarnings("unchecked")
	private static void saveRecordToFile(File directory, Record record) throws IOException, JDOMException
	{
		String xml = record.getOaiXml();
		OutputStreamWriter writer = null;
		File outfile = null;

		String controlNumber = runRegex(xml, "<marc:controlfield tag=\"001\">(\\d*)</marc:controlfield>", 1);

		if(controlNumber == null)
		{
			// An XPATH expression to get the recordIDs
			XPath xpath = XPath.newInstance("//xc:entity[@type='manifestation']/xc:recordID[@type='NRU']");

			List<Element> elements = xpath.selectNodes(builder.build(new InputSource(new StringReader(record.getOaiXml()))));

			if(elements == null || elements.size() == 0)
				controlNumber = "" + ++counter;
			else
			{
				for(Element element : elements)
	    		{
					if(controlNumber == null)
						controlNumber = element.getText();
					else
						controlNumber = controlNumber + "_" + element.getText();
	    		}
			}
		}

		try
		{
			outfile = new File(directory.getAbsolutePath() + "\\" + controlNumber + ".xml");
			outfile.createNewFile();

			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outfile)),"UTF-8");

			writer.write(xml);
		}
		finally
		{
			if(writer != null)
				writer.close();
		}
	}

	//**********************************************************
	// Private methods to run regular expressions
	//**********************************************************

	/**
	 * A helper method to run a regular expression against a String.  If the
	 * regex matches the text, the content of the requested group will be
	 * returned, otherwise this method will return null.
	 *
	 * @param text The text to run the regex against.
	 * @param regex A regular expression with at least one group.
	 * @param groupNum The group to return.
	 * @return The contents of the first group in the first match, or null if there
	 *         were no matches.
	 */
	private static String runRegex(String text, String regex, int groupNum)
	{
		// Create a Pattern based on the passed regex string
    	Pattern regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    	// Run the Pattern against the passed text string
    	Matcher regexMatcher = regexPattern.matcher(text);

    	StringBuilder result = new StringBuilder();

    	// Return the first group if any matches were found,
    	// otherwise return null
    	while(regexMatcher.find())
    		result.append(regexMatcher.group(groupNum)).append('_');

    	if(result.length() == 0)
    		return null;

    	return(result.substring(0, result.length()-1));
	}
}

