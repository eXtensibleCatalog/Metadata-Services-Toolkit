/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;

import xc.mst.constants.Constants;

public class TestOAIRepository
{
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static Configuration configuration = null;
	
	/**
	 * A reference to the logger which writes to the HarvestOut log file
	 */
	private static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
/*
	static
	{
		// Load the configuration file
		configuration = ConfigurationManager.getConfiguration();
		
		// Configure the log file location as the value found in the configuration file.
		String logConfigFileLocation = configuration.getProperty(Constants.CONFIG_LOGGER_CONFIG_FILE_LOCATION);
		if(logConfigFileLocation != null)
			PropertyConfigurator.configure(logConfigFileLocation);
		// Abort if we could not find the configuration file
		else
		{
			System.err.println("The configuration file was invalid or did not exist.");
			System.exit(1);
		}
		
		MSTConfiguration.getInstance("MetadataServicesToolkit");

		MSTSolrServer.getInstance();
	} */

	public static void main(String[] args) throws Exception { 

		 try{
			 Calendar now = Calendar.getInstance();
					 System.out.println("cal :"+now);
					 System.out.println("Port  is :"
				      + TimeZone.getDefault());
					 
		    }catch (Exception e){
		      System.out.println("Exception caught ="+e.getMessage());
		    }


		
	}

/*	public static void main(String[] args) throws DataException, IOException, JDOMException, IndexException
	{

		// Bean to manage data for handling the request
		OaiRequestBean bean = new OaiRequestBean();

		// Set parameters on the bean based on the OAI request's parameters
		bean.setVerb("ListRecords");
		bean.setMetadataPrefix("marcxml");
		bean.setServiceId(1);
//		bean.setResumptionToken("2");
//		bean.setFrom("2009-08-20T21:11:20Z");
//		bean.setUntil("2009-09-30T08:10:00Z");

		
		String oaiRepoBaseURL = "http://localhost:8080/MetadataServicesToolkit/oaiRepository";
		
		// Create the Facade Object, which will compute the results of the request and set them on the bean
		Facade facade = new Facade(bean, oaiRepoBaseURL);

		StringBuffer b = new StringBuffer("http://localhost:8080/MetadataServicesToolkit/Normalization Service/oaiRepository");
		// Set the response header on the facade Object
		facade.setResponseHeader(b);

		// Execute the correct request on the Facade Object
		facade.execute();
		
		// Build the OAI response
		StringBuilder oaiResponseElement = new StringBuilder();

		// Append the header
		oaiResponseElement.append(Constants.OAI_RESPONSE_HEADER);

		// Append the response date element
		oaiResponseElement.append(bean.getResponseDateElement()).append("\n");

		// Append the request element
		oaiResponseElement.append(bean.getRequestElement()).append("\n");

		// Append the response itself
		oaiResponseElement.append(bean.getXmlResponse()).append("\n");

		// Append the footer
		oaiResponseElement.append(Constants.OAI_RESPONSE_FOOTER);

		String oaiXMLOutput = oaiResponseElement.toString();

		log.info(oaiXMLOutput);	
		
	}
*/	
}

