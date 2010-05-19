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
import xc.mst.manager.services.ServicesManager;
import xc.mst.services.MetadataServiceFactory;
import xc.mst.services.MetadataService;
import xc.mst.utils.MSTConfiguration;

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
	
	/** Type of thread */
	public static final String type = Constants.THREAD_SERVICE;
	
	protected ServicesManager servicesManager = (ServicesManager)MSTConfiguration.getBean("ServicesManager");

	/**
	 * The ID of the service to run
	 */
	private int serviceId = -1;

	/**
	 * The ID of the service to run
	 */
	private int outputSetId = -1;

	/**
	 * Reference to the running service
	 */
	private static MetadataService runningService;
	
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
		try {
			
			if(log.isDebugEnabled())
				log.debug("Invoking the service with ID " + serviceId + " and adding the results to the set with ID " + outputSetId + ".");
			
			runningService = MetadataServiceFactory.getService(serviceId);
			runningService.runService(serviceId, outputSetId);
		} // end try(run the service)
		catch(Exception e){
			log.error("An error occurred while running the service with ID " + serviceId, e);
			runningService.setStatus(Constants.STATUS_SERVICE_ERROR);
			runningService.sendReportEmail("An error occurred while running the service with ID " + serviceId);
		} // end catch(Exception)
		finally {
			Scheduler.setJobCompletion();
		}
	} // end method run()

	/**
	 * Cancels the currently running service
	 */
	public void cancel() {
		
		log.info("Canceling service with id:" + serviceId);
		runningService.setCanceled(true);
	}

	/**
	 * Pauses the currently running service
	 */
	public void pause() {
		log.info("Pausing service with id:" + serviceId);
		runningService.setPaused(true);
		
	}

	/**
	 * Resumes the currently paused service
	 */
	public void proceed() {
		
		log.info("Resuming service with id:" + serviceId);
		runningService.setPaused(false);
		
	}

	/**
	 * Gets the name for the job
	 */
	public String getJobName() {
	
		return runningService.getServiceName();
	}

	/**
	 * Gets the status of the job
	 */
	public String getJobStatus() {

		if (runningService != null)
			return runningService.getServiceStatus();
		else
			return Constants.STATUS_SERVICE_NOT_RUNNING;

	}
	
	/**
	 * Gets the thread type
	 * @return
	 */
	public String getType() {
		return type;
	}

	@Override
	public int getProcessedRecordCount() {
	
		return runningService.getProcessedRecordCount();
	}

	@Override
	public int getTotalRecordCount() {
		
		return runningService.getTotalRecordCount();
	}	
	
	public static MetadataService getRunningService(){
		
		return runningService;
	}
} // end class ServiceWorkerThread
