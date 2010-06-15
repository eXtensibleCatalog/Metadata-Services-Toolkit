/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;


/**
 * A Thread which runs a harvest
 *
 * @author Eric Osisek
 */
public class HarvesterWorkerThread extends WorkerThread {

	/*
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	public static final String type = Constants.THREAD_REPOSITORY;
	
	protected HarvestManager harvestManager = null;

	private int harvestScheduleId = -1;

	public void setHarvestScheduleId(int newId) {
		harvestScheduleId = newId;
	}

	public void run() {
		try {
			if(log.isDebugEnabled())
				log.debug("Invoking the harvester on harvest schedule with ID " + harvestScheduleId + ".");

			// Construct the XC_Harvester object.  This will automatically run the harvester
			harvestManager = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager"); 
			harvestManager.runHarvest(harvestScheduleId);
		} catch(Exception e) {
			log.error("An error occurred while running the harvest schedule with ID " + harvestScheduleId, e);
		} finally {
			Scheduler.setJobCompletion();
		}
	}
	
	public void cancel() {
		harvestManager.kill();
		log.debug("Cancelling the Harvest" );
	}

	public void pause() {
		harvestManager.pause();
		log.debug("Pausing the Harvest" );
	}

	public void proceed() {
		harvestManager.proceed();
		log.debug("Resuming the Harvest" );
	}

	public String getJobName() {
		return harvestManager.getProvider().getName();
	}

	public String getJobStatus() {
		if (Harvester.getRunningHarvester() != null)
			return Harvester.getRunningHarvester().getHarvesterStatus();
		else
			return Constants.STATUS_SERVICE_NOT_RUNNING;
	}

	public String getType() {
		return type;
	}

	@Override
	public int getProcessedRecordCount() {
		return Harvester.getRunningHarvester().getProcessedRecordCount();		
		
	}

	@Override
	public int getTotalRecordCount() {
		
		return Harvester.getRunningHarvester().getTotalRecordCount();
		 
	}
	*/

}
