/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.text.DateFormat;

import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.harvester.HarvestRunner;
import xc.mst.harvester.Hexception;
import xc.mst.harvester.OAIErrorException;
import xc.mst.scheduling.SchedulingException;


/**
 * Times a variety of MST components, including the harvester
 * and a number of services.
 *
 * @author Eric Osisek
 */
public class BenchmarkMST
{
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static Configuration configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

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
	}

	/**
	 * Used to format timestamps for the results of the tests
	 */
	private static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	/**
	 * Runs the benchmarks
	 *
	 * @param args Not used
	 */
	public static void main(String[] args) throws SchedulingException, DataException, Hexception, OAIErrorException
	{
		// Run the harvester on the OAI repository and report how long it took
		benchmarkHarvester();

		// Run the Normalization Service on the harvested records and report how long it took
		benchmarkNormalizationService();
	}

	/**
	 * Runs the harvester on the OAI repository and report how long it took
	 */
	private static void benchmarkHarvester() throws SchedulingException, DataException, Hexception, OAIErrorException
	{
		/*
		// Insert the user to which the provider will be assigned
		User user = new User();
		user.setUsername("Benchmark user");
		user.setPermissions("");

		// Create the provider to register
		Provider provider = new Provider();
		provider.setOaiProviderUrl(OAI_REPO);
		provider.setCreatedAt(new Date());
		provider.setUserId(1);
		provider.setProtocolVersion("1.0");

		// Register the provider
		RepositoryRegisterer.register(provider, "Benchmark Schedule", RepositoryRegisterer.NO_SCHEDULE, -1, -1, -1);
		*/

		// Print the start time
		System.out.println(formatter.format(System.currentTimeMillis()) + " Starting harvester.");

		// Construct the XC_Harvester object.  This will automatically run the harvester
		HarvestRunner harvester = new HarvestRunner(1);//HarvestSchedule.getRunOnceScheduleId(provider.getProviderId()));
		harvester.runHarvest();

		// Print the end time
		System.out.println(formatter.format(System.currentTimeMillis()) + " Harvester finished.");
	}

	/**
	 * Runs the Normalization Service on the harvested records and report how long it took
	 */
	private static void benchmarkNormalizationService()
	{

	}
}
