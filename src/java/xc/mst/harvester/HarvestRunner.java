/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.harvester;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;



import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.harvest.DefaultHarvestDAO;
import xc.mst.dao.harvest.DefaultHarvestScheduleDAO;
import xc.mst.dao.harvest.DefaultHarvestScheduleStepDAO;
import xc.mst.dao.harvest.HarvestDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestScheduleStepDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.utils.LogWriter;

/**
 * This class is an interface to the Harvester class which requires only the ID of the harvest schedule
 * step to run.
 *
 * @author ShreyanshV
 */
public class HarvestRunner
{
	/**
	 * Data access object for getting provider
	 */
	private static ProviderDAO providerDao = new DefaultProviderDAO();

	/**
	 * Data access object for getting harvests
	 */
	private static HarvestDAO harvestDao = new DefaultHarvestDAO();

	/**
	 * Data access object for getting harvest schedules
	 */
	private static HarvestScheduleDAO harvestScheduleDao = new DefaultHarvestScheduleDAO();

	/**
	 * Data access object for getting harvest schedule steps
	 */
	private static HarvestScheduleStepDAO harvestScheduleStepDao = new DefaultHarvestScheduleStepDAO();

	/**
	 * A reference to the logger which writes to the HarvestIn log file
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_HARVEST_IN);

	/**
	 * The metadataPrefix (format) to harvest
	 */
	private String metadataPrefix = "";

	/**
	 * The setSpec of the set to harvest
	 */
	private String setSpec = null;

	/**
	 * The OAI request's from parameter
	 */
	private Date from = null;

	/**
	 * The OAI request's until parameter
	 */
	private Date until = null;

	/**
	 * True to harvest all records, false to use the from and until parameters
	 */
	private boolean harvestAll = true;

	/**
	 * True to harvest all records if deleted records are not supported by the
	 * repository, false to use the from and until parameters.  If false the
	 * MST will also be unable to synch with the repository unless that repository
	 * supports deleted records
	 */
	private boolean harvestAllIfNoDeletedRecord = true;

	/**
	 * How long to wait for a response from the repository we're harvesting before
	 * declaring that the request timed out
	 */
	private int timeOutMilliseconds = 5 * 60 * 1000; // 5 minutes

	/**
	 * The provider to harvest
	 */
	private Provider provider = null;

	/**
	 * The URL of the provider to harvest
	 */
	private String baseURL = null;

	/**
	 * The harvest schedule to run
	 */
	private HarvestSchedule harvestSchedule = null;

	/**
	 * The harvest that is currently being run
	 */
	private Harvest currentHarvest = null;

	/**
	 * The request run by the harvester
	 */
	private String request = null;

	/**
	 * Constructs an XC_Harvester to run the passed harvest schedule step
	 *
	 * @param harvestScheduleId The ID of the harvest schedule to run
	 * @throws OAIErrorException If the OAI provider being harvested returned an OAI error
	 * @throws Hexception If a serious error occurred which prevented the harvest from being completed
	 * @throws DatabaseConfigException
	 */
	public HarvestRunner(int harvestScheduleId) throws OAIErrorException, Hexception, DatabaseConfigException
	{
		// Set the parameters for the harvest based on the harvest schedule step ID
		harvestSchedule = harvestScheduleDao.getById(harvestScheduleId);
		provider = harvestSchedule.getProvider();
		baseURL = provider.getOaiProviderUrl();
	} // end constructor(int)

	public void runHarvest()
	{
		try
		{
			StringBuilder requests = new StringBuilder();

			for(HarvestScheduleStep step : harvestScheduleStepDao.getStepsForSchedule(harvestSchedule.getId()))
			{
				runHarvestStep(step);

				if(requests.length() == 0)
					requests.append(request);
				else
					requests.append("\n").append(request);
			}

			harvestSchedule.setRequest(requests.toString());

			harvestScheduleDao.update(harvestSchedule, false);

			// Set the current harvest's end time
			currentHarvest.setEndTime(new Date());
			harvestDao.update(currentHarvest);

			// Set the provider's last harvest time
			provider = providerDao.getById(provider.getId());
			provider.setLastHarvestEndTime(new Date());
			providerDao.update(provider);

			LogWriter.addInfo(provider.getLogFileName(), "Finished harvest of " + baseURL);
		}
		catch (Hexception e) {
				
			log.info("Harvest Aborted!");
		}
		catch(DatabaseConfigException e)
		{
			log.error("Unable to connect to the database with the parameters defined in the configuration file.", e);
		}
		catch(DataException e)
		{
			log.error("An error occurred while updating the harvest schedule's request field.", e);
		}
	}

	/**
	 * Runs the harvest
	 * @throws Hexception 
	 */
	private void runHarvestStep(HarvestScheduleStep harvestScheduleStep) throws Hexception
	{
		try
		{
			metadataPrefix = harvestScheduleStep.getFormat().getName();

			// If there was a set, set up the setSpec
			if(harvestScheduleStep.getSet() != null)
				setSpec = harvestScheduleStep.getSet().getSetSpec();

			// Set the from field to the time when we last harvested the provider
			from = harvestScheduleStep.getLastRan();

			// Harvest all records if the from parameter was not provided
			if(from != null)
				harvestAll = false;

			// The time when we started the harvest
			Date startTime = new Date();

			// Setup the harvest we're currently running
			currentHarvest = new Harvest();
			currentHarvest.setStartTime(startTime);
			currentHarvest.setProvider(provider);
			currentHarvest.setHarvestScheduleId(harvestScheduleStep.getSchedule().getId());
			harvestDao.insert(currentHarvest);

			// Run the harvest
			Harvester.harvest(
					 baseURL,
					 metadataPrefix,
					 setSpec,
					 from,
					 until,
					 harvestAll,
					 harvestAllIfNoDeletedRecord,
					 timeOutMilliseconds,
					 harvestScheduleStep,
					 currentHarvest);

			// Set the request used to run the harvest
			currentHarvest = harvestDao.getById(currentHarvest.getId());
			request = currentHarvest.getRequest();

			// Set the harvest schedule step's last run date to the time when we started the harvest.
			harvestScheduleStep.setLastRan(startTime);
			harvestScheduleStepDao.update(harvestScheduleStep, harvestScheduleStep.getSchedule().getId());
		} // end try(run the harvest)
		catch (Hexception e) {
				 throw new Hexception("Harvest Step Aborted!");
		}
		catch(Exception e)
		{
			log.error("An error occurred while harvesting " + baseURL, e);
		} // end catch(Exception)
	} // end method runHarvest()

	/**
	 * Gets the request used to start the harvest
	 *
	 * @return The OAI request used to start the harvest
	 */
	public String getRequest()
	{
		return request;
	}
} // end class HarvestRunner





