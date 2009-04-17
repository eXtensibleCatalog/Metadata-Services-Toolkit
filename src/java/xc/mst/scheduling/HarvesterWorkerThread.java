/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.harvester.HarvestRunner;
import xc.mst.harvester.Harvester;

/**
 * A Thread which runs a harvest
 *
 * @author Eric Osisek
 */
public class HarvesterWorkerThread extends WorkerThread
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The ID of the harvest schedule to run
	 */
	private int harvestScheduleStepId = -1;

	/**
	 * Sets the ID of the harvest schedule step to be run
	 *
	 * @param newId The ID of the harvest schedule step to be run
	 */
	public void setHarvestScheduleStepId(int newId)
	{
		harvestScheduleStepId = newId;
	} // end method setHarvestScheduleStepId(int)

	/**
	 * The Thread's run method.  This starts the harvest specified by the harvest schedule step ID
	 */
	public void run()
	{
		try
		{
			if(log.isDebugEnabled())
				log.debug("Invoking the harvester on harvest schedule with ID " + harvestScheduleStepId + ".");

			// Construct the XC_Harvester object.  This will automatically run the harvester
			HarvestRunner harvester = new HarvestRunner(harvestScheduleStepId);
			harvester.runHarvest();
		} // end try(run the harvest)
		catch(Exception e)
		{
			log.error("An error occurred while running the harvest schedule with ID " + harvestScheduleStepId, e);
		} // end catch(Exception)
		finally{
			Scheduler.setJobCompletion();
		}
	} // end method run()

	/**
	 * Cancels the running harvest service
	 */
	public void cancel() {
		
		Harvester.getRunningHarvester().kill();
		log.debug("Cancelling the Harvest" );
	}

	/**
	 * Pauses the execution of the currently running harvest service
	 */
	public void pause() {
		Harvester.getRunningHarvester().setPaused(true);
		log.debug("Pausing the Harvest" );
	}


	/**
	 * Resumes the currently paused harvest service
	 */
	public void proceed() {
		Harvester.getRunningHarvester().setPaused(false);
		log.debug("Resuming the Harvest" );
	}

	/**
	 * Gets the name for the job
	 */
	public String getJobName() {
	
		return Harvester.getRunningHarvester().getProvider().getName();
	}

	/**
	 * Gets the status of the job
	 */
	public String getJobStatus() {
		
		return Harvester.getRunningHarvester().getHarvesterStatus();
	}

} // end class HarvestWorkerThread
