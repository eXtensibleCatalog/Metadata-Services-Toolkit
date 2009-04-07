/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.constants.Constants;
import xc.mst.dao.harvest.DefaultHarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;

/**
 * A Thread which runs in the background and checks every minute to see
 * if any harvests are scheduled to run.  If they are, the Harvester
 * will be invoked for all steps of the harvest schedule which was due to be invoked
 * This class also runs MetadataServices when the Harvester or another Service
 * matches a processing directive requiring the service to be run.
 *
 * This Thread maintains a queue of jobs to be run.  A job may be
 * either a harvest or a service.
 *
 * @author Eric Osisek
 */
public class Scheduler extends Thread
{
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration;

	/**
	 * The DAO for getting and inserting harvest schedules
	 */
	HarvestScheduleDAO harvestScheduleDao = new DefaultHarvestScheduleDAO();

	/**
	 * A queue of WorkerThreads that are waiting to run harvests/services
	 */
	private static Queue<WorkerThread> waitingJobs = new LinkedList<WorkerThread>();

	/**
	 * The WorkerThread that is currently running harvests/services
	 */
	private static WorkerThread runningJob;

	/**
	 * Gets the currently running job
	 */
	public static WorkerThread getRunningJob() {
		return runningJob;
	}

	/**
	 * Whether or not the scheduler has been killed
	 */
	boolean killed = false;

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
		} // end else
	} // end static initializer

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The Thread's run method.  This checks the database every minute
	 * for harvests which are scheduled to be run, and invokes the
	 * harvester on all steps for that schedule.  Steps invoked in this
	 * manner are queued such that we don't have too many simultaniously
	 * running harvests or services.
	 *
	 * This method also runs MetadataServices when the Harvester or another Service
	 * matches a processing directive requiring the service to be run.
	 */
	public void run()
	{

		while(!killed)
		{
			// Get the current time
			Calendar now = Calendar.getInstance();

			if(log.isDebugEnabled())
				log.debug("Harvest Scheduler checking for harvests scheduled to run now.  The time is " +
						  now.get(Calendar.MINUTE) + " minutes, " + now.get(Calendar.HOUR_OF_DAY) + " hours, and " +
						  now.get(Calendar.DAY_OF_WEEK) + " day of the week.");

			// Get a list of harvest schedules which need to be run now
			List<HarvestSchedule> schedulesToRun = null;

			// Get the schedules to run
			schedulesToRun = harvestScheduleDao.getSchedulesToRun(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.DAY_OF_WEEK), now.get(Calendar.MINUTE));

			// Run each scheduled harvest
			for(HarvestSchedule scheduleToRun : schedulesToRun)
			{
				for(HarvestScheduleStep scheduleStepToRun : scheduleToRun.getSteps())
				{
					if(log.isDebugEnabled())
						log.debug("Creating a Thread to run HarvestScheduleStep with id " + scheduleStepToRun.getId());

					// Start a new Thread to run the Harvester component for the schedule
					HarvesterWorkerThread harvestThread = new HarvesterWorkerThread();
					harvestThread.setHarvestScheduleStepId(scheduleStepToRun.getId());
					scheduleThread(harvestThread);
				} // end loop over the schedule's steps
			} // end loop over schedules to be run

		
			if(runningJob == null)
			{
				WorkerThread jobToStart = waitingJobs.poll();
				
					// If there was a service job in the waiting queue, start it.  Otherwise break from the loop
					if(jobToStart != null)
					{
						jobToStart.start();
						runningJob = jobToStart;
					} // end if(the service job queue was empty)
				
			}
			else {
				if(!runningJob.isAlive()){
					WorkerThread jobToStart = waitingJobs.poll();
					
					// If there was a service job in the waiting queue, start it.  Otherwise break from the loop
					if(jobToStart != null)
					{
						jobToStart.start();
						runningJob = jobToStart;
					} // end if(the service job queue was empty)
				}
			}

			// Sleep until the next hour begins
			try
			{
				if(log.isDebugEnabled())
					log.debug("Scheduler Thread sleeping for 1 minute.");

				Thread.sleep(60 * 1000);
			} // end try(sleep for 1 minute)
			catch(InterruptedException e)
			{
				if(log.isDebugEnabled())
					log.debug("Caught InteruptedException while sleeping in Scheduler Thread.");
			} // end catch(InterruptedException)
		} // end main loop
	} // end method run()

	/**
	 * Adds a WorkerThread to the queue of Threads to be run.
	 *
	 * @param scheduleMe The Thread to be run.
	 */
	public static void scheduleThread(HarvesterWorkerThread scheduleMe)
	{
		waitingJobs.add(scheduleMe);
	} // end method scheduleThread(HarvesterWorkerThread)

	/**
	 * Adds a WorkerThread to the queue of Threads to be run.
	 *
	 * @param scheduleMe The Thread to be run.
	 */
	public static void scheduleThread(ServiceWorkerThread scheduleMe)
	{
		waitingJobs.add(scheduleMe);
	} // end method scheduleThread(ServiceWorkerThread)

	/**
	 * Kills the Scheduling Thread
	 */
	public void kill()
	{
		killed = true;
	} // end method kill()
	
	/**
	 * Cancels the currently running service / harvest
	 */
	public static void cancelRunningJob(){
		
		runningJob.cancel();
	}
	
	/**
	 * Pauses the currently running service / harvest
	 */
	public static void pauseRunningJob(){
		
		runningJob.pause();
	}

	/**
	 * Resumes the currently running service / harvest
	 */
	public static void resumePausedJob(){
		
		runningJob.proceed();
	}

	
} // end class Scheduler
