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
import xc.mst.harvester.Harvester;
import xc.mst.services.MetadataService;

/**
 * A Thread which runs a service
 *
 * @author Eric Osisek
 */
public class ServiceWorkerThread extends WorkerThread
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The ID of the service to run
	 */
	private int serviceId = -1;

	/**
	 * The ID of the service to run
	 */
	private int outputSetId = -1;

	/**
	 * Sets the ID of the harvest schedule to be run
	 *
	 * @param newId The ID of the harvest schedule to be run
	 */
	public void setServiceId(int newId)
	{
		serviceId = newId;
	} // end method setServiceId(int)

	/**
	 * Sets the ID of the service to be run
	 *
	 * @param newId The ID of the service to be run
	 */
	public void setOutputSetId(int newId)
	{
		outputSetId = newId;
	} // end method setOutputSetId(int)

	/**
	 * The Thread's run method.  This runs the Metadata Service whose service ID
	 * matches the service ID set on this ServiceWorkerThread.
	 */
	public void run()
	{
		try
		{
			if(log.isDebugEnabled())
				log.debug("Invoking the service with ID " + serviceId + " and adding the results to the set with ID " + outputSetId + ".");

			MetadataService.runService(serviceId, outputSetId);
		} // end try(run the service)
		catch(Exception e)
		{
			log.error("An error occurred while running the service with ID " + serviceId, e);
		} // end catch(Exception)
		finally{
			Scheduler.setJobCompletion();
		}
	} // end method run()

	/**
	 * Cancels the currently running service
	 */
	public void cancel() {
		
		log.info("Canceling service with id:" + serviceId);
		MetadataService.getRunningService().setCanceled(true);
	}

	/**
	 * Pauses the currently running service
	 */
	public void pause() {
		log.info("Pausing service with id:" + serviceId);
		MetadataService.getRunningService().setPaused(true);
		
	}

	/**
	 * Resumes the currently paused service
	 */
	public void proceed() {
		
		log.info("Resuming service with id:" + serviceId);
		MetadataService.getRunningService().setPaused(false);
		
	}

	/**
	 * Gets the name for the job
	 */
	public String getJobName() {
	
		return MetadataService.getRunningService().getServiceName();
	}

	/**
	 * Gets the status of the job
	 */
	public String getJobStatus() {
		
		String status = MetadataService.getRunningService().getServiceStatus();
		
		if(status.equals(Constants.STATUS_SERVICE_CANCELED) || status.equals(Constants.STATUS_SERVICE_CANCELED)
				|| status.equals(Constants.STATUS_SERVICE_NOT_RUNNING) || status.equals(Constants.STATUS_SERVICE_PAUSED))
				return "No Process Running";
		else
			return "Processing through "+ MetadataService.getRunningService().getServiceName();
		
	}
	
	
} // end class ServiceWorkerThread
