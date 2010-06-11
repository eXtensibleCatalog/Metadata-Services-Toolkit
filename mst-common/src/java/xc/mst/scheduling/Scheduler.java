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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseManager;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;

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
public class Scheduler extends BaseManager implements Runnable {
	
	private final static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	protected boolean killed = false;
	
	protected WorkerThread runningJob;
	protected Job previousJob = null;

	public void init() {
		new Thread(this).start();
	}
	
	public WorkerThread getRunningJob() {
		return runningJob;
	}
	
	public void run() {		
		Map<Integer, String> lastRunDate = new HashMap<Integer, String>();
		
		while(!killed) {
			Calendar now = Calendar.getInstance();

			List<HarvestSchedule> schedulesToRun = null;
			String thisMinute = ""+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.DAY_OF_WEEK)+now.get(Calendar.MINUTE);
			try {
				schedulesToRun = getHarvestScheduleDAO().getSchedulesToRun(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.DAY_OF_WEEK), now.get(Calendar.MINUTE));
			} catch (DatabaseConfigException e1) {
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				schedulesToRun = new ArrayList<HarvestSchedule>();
			}

			for(HarvestSchedule scheduleToRun : schedulesToRun) {
				boolean alreadyRanThisMinute = false;
				if (lastRunDate.containsKey(scheduleToRun.getId())) {
					if (lastRunDate.get(scheduleToRun.getId()).equals(thisMinute)) {
						alreadyRanThisMinute = true;
					}
				}
				// BDA: The Scheduler was tied to looping every 60 seconds.  I like to test faster
				//      than that so I changed it to loop every 3 seconds.  This added check is necessary
				//      because the 60 second loop assured that a job would not be started twice.  Instead
				//      I'll keep track of the last start time for each job.
				if(!alreadyRanThisMinute &&
						!scheduleToRun.getStatus().equals(Status.RUNNING) && 
						!scheduleToRun.getStatus().equals(Status.PAUSED))
				{
					if(log.isDebugEnabled())
						log.debug("Creating a Thread to run HarvestSchedule with id " + scheduleToRun.getId());
	
					// Add job to database queue
					try {
						Job job = new Job(scheduleToRun, Constants.THREAD_REPOSITORY);
						job.setOrder(getJobService().getMaxOrder() + 1); 
						jobService.insertJob(job);
						lastRunDate.put(scheduleToRun.getId(), thisMinute);
					} catch (DatabaseConfigException dce) {
						log.error("DatabaseConfig exception occured when ading jobs to database", dce);
					}
				}
			} // end loop over schedules to be run

			if (runningJob == null || !runningJob.isAlive())
			{
				try {
					if (previousJob != null) {
						TimingLogger.log("finished job: "+previousJob.getJobType());
						TimingLogger.log("runningJob: "+runningJob);
						TimingLogger.reset();
						
						List<ProcessingDirective> processingDirectives = null;
						if (previousJob.getHarvestSchedule() != null) { // was harvest
							processingDirectives = getProcessingDirectiveDAO().getBySourceProviderId(
									previousJob.getHarvestSchedule().getProvider().getId());

						} else if (previousJob.getService() != null) { // was service
							processingDirectives = getProcessingDirectiveDAO().getBySourceServiceId(
									previousJob.getService().getId());
						}
						if (processingDirectives != null) {
							try {
								for (ProcessingDirective pd : processingDirectives) {
									Job job = new Job(pd.getService(), pd.getOutputSet().getId(), Constants.THREAD_SERVICE);
									job.setOrder(jobService.getMaxOrder() + 1); 
									jobService.insertJob(job);	
								}
							} catch (DatabaseConfigException dce) {
								log.error("DatabaseConfig exception occured when ading jobs to database", dce);
							}
						}
					}
					Job jobToStart = jobService.getNextJobToExecute();
					previousJob = jobToStart;

					// If there was a service job in the waiting queue, start it.  Otherwise break from the loop
					if(jobToStart != null) {
						TimingLogger.reset();
						TimingLogger.log("starting job: "+jobToStart.getJobType());
						
						if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
							HarvesterWorkerThread harvestThread = new HarvesterWorkerThread();
							harvestThread.setHarvestScheduleId(jobToStart.getHarvestSchedule().getId());
							harvestThread.start();
							runningJob = harvestThread;
						} else if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_SERVICE)) {
							TimingLogger.log("service : "+jobToStart.getService().getClassName());
							ServiceWorkerThread serviceThread = new ServiceWorkerThread();
							serviceThread.setServiceId(jobToStart.getService().getId());
							serviceThread.setOutputSetId(jobToStart.getOutputSetId());
							serviceThread.start();
							runningJob = serviceThread;
						} else if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_SERVICE_REPROCESS)) {
							ServiceReprocessWorkerThread serviceReprocessWorkerThread = new ServiceReprocessWorkerThread();
							serviceReprocessWorkerThread.setServiceId(jobToStart.getService().getId());
							serviceReprocessWorkerThread.start();
							runningJob = serviceReprocessWorkerThread;
						} else if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_DELETE_SERVICE)) {
							DeleteServiceWorkerThread deleteServiceWorkerThread = new DeleteServiceWorkerThread();
							deleteServiceWorkerThread.setServiceId(jobToStart.getService().getId());
							deleteServiceWorkerThread.start();
							runningJob = deleteServiceWorkerThread;
						}

						// Delete the job from database once its scheduled to run
						// BDA - hmmm... perhaps we shouldn't delete it until it completes?
						jobService.deleteJob(jobToStart);
					} // end if(the service job queue was empty)
				} catch(DatabaseConfigException dce) {
					log.error("DatabaseConfigException occured when getting job from database", dce);
				}
			}

			// Sleep until the next hour begins
			try {
				if(log.isDebugEnabled())
					log.debug("Scheduler Thread sleeping for 1 minute.");
				Thread.sleep(3 * 1000);
			} catch(InterruptedException e) {
				if(log.isDebugEnabled())
					log.debug("Caught InteruptedException while sleeping in Scheduler Thread.");
			} catch(Throwable t) {
				log.error("", t);
			}
		}
	}

	/**
	 * Adds a WorkerThread to the queue of Threads to be run.
	 *
	 * @param scheduleMe The Thread to be run.
	 */
	public static void scheduleThread(WorkerThread scheduleMe)
	{
		waitingJobs.add(scheduleMe);
	} // end method scheduleThread(WorkerThread)
	
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

	/**
	 * Sets the currentJob reference to null after completion of the job.
	 */
	public static void setJobCompletion(){
		TimingLogger.log("setJobCompletion()");
		runningJob = null;
	}

}
