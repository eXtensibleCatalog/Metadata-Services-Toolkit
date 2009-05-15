/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.user.DefaultServerDAO;
import xc.mst.dao.user.DefaultUserDAO;
import xc.mst.dao.user.ServerDAO;
import xc.mst.dao.user.UserDAO;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.harvester.HarvestRunner;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.scheduling.SchedulingException;
import xc.mst.services.MetadataService;
import xc.mst.utils.LogWriter;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

public class EOTests
{
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration;

	/**
	 * Used to format timestamps for the results of the tests
	 */
	private static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	static
	{
		// Load the configuration file
		configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

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
		
		MSTSolrServer.getInstance("8080");
	}

	/**
	 * Tests the classes in the data project
	 *
	 * @param args Ignored
	 */
	public static void main(String[] args) throws DataException, SchedulingException
	{
		try
		{	
			RecordList test = new DefaultRecordService().getAll();
			System.out.println("size: " + test.size());
			
			java.util.Set<String> set = new HashSet<String>();
			for(Record record : test)
				if(!set.contains(record.getOaiIdentifier()))
					set.add(record.getOaiIdentifier());
			
			System.out.println("size: " + test.size());
			
			int i = 0;
			for(Record record : test)
				i++;
			
			//System.out.println(test.get(5) == null ? "null" : "not null");
			System.out.println("size: " + test.size());
			System.out.println("set size: " + set.size());

			if(true) return;
			
			//File file = new File("C:\\AllXcProjects\\MetadataServicesToolkit\\serviceConfig\\DefaultNormalizationServiceConfig.xccfg");
			//new DefaultServicesService().addNewService(file);
			
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//SolrIndexManager.getInstance().commitIndex();
		}
	}
}
