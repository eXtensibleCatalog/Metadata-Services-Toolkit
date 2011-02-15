/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.harvester.HarvestManager;
import xc.mst.manager.BaseService;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataServiceManager;
import xc.mst.services.SolrWorkDelegate;
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
public class Scheduler extends BaseService implements Runnable {
	
	private final static Logger LOG = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	protected boolean killed = false;
	
	protected WorkerThread runningJob;
	protected Thread runningThread;
	protected Job previousJob = null;
	protected boolean pausedManually = false;

	public void init() {
		LOG.info("init");
		new Thread(this).start();
	}
	
	public WorkerThread getRunningJob() {
		return runningJob;
	}
	
	public void run() {
		LOG.info("Scheduler.run");
		Map<Integer, String> lastRunDate = new HashMap<Integer, String>();
		WorkerThread solrWorkerThread = null;
		Thread solrThread = null;
		
		try {
			for (Service s : getServiceDAO().getAll()) {
				if (Status.RUNNING.equals(s.getStatus())) {
					s.setStatus(Status.CANCELED);
					getServiceDAO().update(s);
				}
			}
			for (HarvestSchedule hs : getHarvestScheduleDAO().getAll()) {
				if (Status.RUNNING.equals(hs.getStatus())) {
					hs.setStatus(Status.CANCELED);
					getHarvestScheduleDAO().update(hs, false);
				}
			}
		} catch (DataException de) {
			throw new RuntimeException(de);
		}

		boolean solrWorkerThreadStarted = false;
		if (config.getPropertyAsBoolean("solr.index.whenIdle", false)) {
			LOG.info("solr.index.whenIdle is true");
			solrWorkerThread = (SolrWorkDelegate)config.getBean("SolrWorkDelegate");
			solrThread = new Thread(solrWorkerThread);
			LOG.debug("solrWorkerThread: "+solrWorkerThread);
		} else {
			LOG.info("solr.index.whenIdle is false");
		}

		while(!killed) {
			Calendar now = Calendar.getInstance();
			List<HarvestSchedule> schedulesToRun = null;
			String thisMinute = ""+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.DAY_OF_WEEK)+now.get(Calendar.MINUTE);
			try {
				schedulesToRun = getHarvestScheduleDAO().getSchedulesToRun(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.DAY_OF_WEEK), now.get(Calendar.MINUTE));
			} catch (DatabaseConfigException e1) {
				LOG.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

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
				if(!alreadyRanThisMinute) {
					if(LOG.isDebugEnabled())
						LOG.debug("Creating a Thread to run HarvestSchedule with id " + scheduleToRun.getId());
	
					// Add job to database queue
					try {
						Job job = new Job(scheduleToRun, Constants.THREAD_REPOSITORY);
						job.setOrder(getJobService().getMaxOrder() + 1); 
						getJobService().insertJob(job);
						lastRunDate.put(scheduleToRun.getId(), thisMinute);
					} catch (DatabaseConfigException dce) {
						LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
					}
				}
			}

			//LOG.debug("runningJob: "+runningJob);
			if (runningJob != null) {
				LOG.debug("runningJob: "+runningJob);
				LOG.debug("runningJob.getType(): "+runningJob.getType());
				//LOG.debug("runningJob == solrWorkerThread: "+(runningJob == solrWorkerThread));
			}
			if (runningJob == null || !runningThread.isAlive() || runningJob == solrWorkerThread) {
				if (runningJob != null) {
					//LOG.debug("runningJob.getWorkDelegate(): "+runningJob.getWorkDelegate());
				}
				//LOG.debug("previousJob: "+previousJob);
				try {
					if (previousJob != null) {
						getJobService().deleteJob(previousJob);
						
						TimingLogger.reset();
						
						Repository previousRepo = null;
						List<ProcessingDirective> processingDirectives = null;
						if (previousJob.getHarvestSchedule() != null) { // was harvest
							processingDirectives = getProcessingDirectiveDAO().getBySourceProviderId(
									previousJob.getHarvestSchedule().getProvider().getId());
							previousJob.getHarvestSchedule().setStatus(runningJob.getJobStatus());
							getHarvestScheduleDAO().update(previousJob.getHarvestSchedule(), false);
							previousRepo = (Repository)config.getBean("Repository");
							previousRepo.setName(previousJob.getHarvestSchedule().getProvider().getName());
						} else if (previousJob.getService() != null) { // was service
							processingDirectives = getProcessingDirectiveDAO().getBySourceServiceId(
									previousJob.getService().getId());
							// Reload service. It is changed during service processing.
							// TODO check to see if there is better way to do this
							Service service = getServicesService().getServiceById(previousJob.getService().getId());
							//getById(previousJob.getService().getId());
							LOG.debug("service: "+service);
							LOG.debug("service.getName(): "+service.getName());
							LOG.debug("service.getMetadataService(): "+service.getMetadataService());
							previousRepo = service.getMetadataService().getRepository();
							service.setStatus(runningJob.getJobStatus());
							getServiceDAO().update(service);
						}
						LOG.debug("processingDirectives: "+processingDirectives);
						
						if (previousRepo != null) {
							if (previousRepo instanceof DefaultRepository) {
								LOG.debug("sleepUntilReady...start");
								((DefaultRepository) previousRepo).sleepUntilReady();
								LOG.debug("sleepUntilReady...finished");
							}
						}
						
						if (processingDirectives != null) {
							try {
								for (ProcessingDirective pd : processingDirectives) {
									// TODO
									// match by set
									// match by format
									// OR you could run the service and it just won't grab any records
									/*
									boolean matched = false;
									if (pd.getTriggeringFormats() != null) {
										for (Format f : pd.getTriggeringFormats()) {
											if (previousJob.getHarvestSchedule().getFormats() != null) {
												
											}
											if (f.getId().equals(previousJob.getHarvestSchedule().getFormats()))
										}
									}
									*/

									
									//Job job = new Job(pd.getService(), pd.getOutputSet().getId(), Constants.THREAD_SERVICE);
									Job job = new Job();
									job.setService(pd.getService());
									if (pd.getOutputSet() != null)
										job.setOutputSetId(pd.getOutputSet().getId());
									job.setJobType(Constants.THREAD_SERVICE);
									job.setOrder(getJobService().getMaxOrder() + 1);
									job.setProcessingDirective(pd);
									getJobService().insertJob(job);	
								}
							} catch (DatabaseConfigException dce) {
								LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
							}
						}
					}
					
					Job jobToStart = getJobService().getNextJobToExecute();
					if (jobToStart != null)
						LOG.debug("jobToStart: "+jobToStart);
					if (jobToStart == null && runningJob != solrWorkerThread && solrWorkerThread != null) {
						LOG.debug("solrWorkerThead.proceed");
						if (!solrWorkerThreadStarted) {
							solrWorkerThreadStarted = true;
							solrThread.start();
						} else {
							if (!pausedManually)
								solrWorkerThread.proceed();	
						}
						runningJob = solrWorkerThread;
						runningJob.type = Constants.SOLR_INDEXER;
						runningThread = solrThread;
					}
					previousJob = jobToStart;

					//LOG.debug("jobToStart: "+jobToStart);
					// If there was a service job in the waiting queue, start it.  Otherwise break from the loop
					if(jobToStart != null) {
						if (solrWorkerThread != null && solrWorkerThreadStarted) {
							solrWorkerThread.pause();
						}
						runningJob = null;
						runningThread = null;
						TimingLogger.reset();
						TimingLogger.log("starting job: "+jobToStart.getJobType());
						
						if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
							HarvestManager hm = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager");
							hm.setHarvestSchedule(jobToStart.getHarvestSchedule());
							runningJob = hm;
							runningJob.type = Constants.THREAD_REPOSITORY;
						} else if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_SERVICE)) {
							MetadataServiceManager msm = new MetadataServiceManager();
							runningJob = msm;
							Service s = getServicesService().getServiceByName(jobToStart.getService().getName());
							msm.setMetadataService(s.getMetadataService());
							msm.setOutputSet(getSetDAO().getById(jobToStart.getOutputSetId()));
							Repository incomingRepo = null;
							if (jobToStart.getProcessingDirective().getSourceProvider() != null) {
								incomingRepo = 
									getRepositoryService().getRepository(jobToStart.getProcessingDirective().getSourceProvider());
							} else if (jobToStart.getProcessingDirective().getSourceService() != null) {
								Service s2 = getServicesService().getServiceById(jobToStart.getProcessingDirective().getSourceService().getId());
								incomingRepo = s2.getMetadataService().getRepository();
							} else {
								throw new RuntimeException("error");
							}
							LOG.debug("incomingRepo.getName(): "+incomingRepo.getName());
							msm.setIncomingRepository(incomingRepo);
							msm.setTriggeringFormats(jobToStart.getProcessingDirective().getTriggeringFormats());
							msm.setTriggeringSets(jobToStart.getProcessingDirective().getTriggeringSets());
							runningJob.type = Constants.THREAD_SERVICE;
							/*
							TimingLogger.log("service : "+jobToStart.getService().getClassName());
							ServiceWorkerThread serviceThread = new ServiceWorkerThread();
							serviceThread.setServiceId(jobToStart.getService().getId());
							serviceThread.setOutputSetId(jobToStart.getOutputSetId());
							serviceThread.start();
							runningJob = serviceThread;
							*/
						} else if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_SERVICE_REPROCESS)) {
							/*
							ServiceReprocessWorkerThread serviceReprocessWorkerThread = new ServiceReprocessWorkerThread();
							serviceReprocessWorkerThread.setServiceId(jobToStart.getService().getId());
							serviceReprocessWorkerThread.start();
							runningJob = serviceReprocessWorkerThread;
							*/
						} else if (jobToStart.getJobType().equalsIgnoreCase(Constants.THREAD_DELETE_SERVICE)) {
							/*
							DeleteServiceWorkerThread deleteServiceWorkerThread = new DeleteServiceWorkerThread();
							deleteServiceWorkerThread.setServiceId(jobToStart.getService().getId());
							deleteServiceWorkerThread.start();
							runningJob = deleteServiceWorkerThread;
							runningJob.type = Constants.THREAD_DELETE_SERVICE;
							*/
						}

						if (runningJob != null) {
							LOG.debug("runningJob.start()");
							runningThread = new Thread(runningJob);
							runningThread.start();
						}
					}
				} catch(DataException de) {
					LOG.error("DataException occured when getting job from database", de);
				}
			}

			// Sleep until the next hour begins
			try {
				if(LOG.isDebugEnabled()) {
					//LOG.debug("Scheduler Thread sleeping for 1 minute.");
				}
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				if(LOG.isDebugEnabled())
					LOG.debug("Caught InteruptedException while sleeping in Scheduler Thread.");
			} catch(Throwable t) {
				LOG.error("", t);
			}
		}
	}
	
	public void kill() {
		if (runningJob != null) {
			runningJob.cancel();
		}
		killed = true;
	}

	public void cancelRunningJob(){
		runningJob.cancel();
	}
	
	public boolean wasPausedManually() {
		return pausedManually;
	}
	public void setPausedManually(boolean _pausedManually) {
		this.pausedManually = _pausedManually;
	}

	public void pauseRunningJob(){
		pausedManually = true;
		runningJob.pause();
	}
	
	public void resumePausedJob(){
		pausedManually = false;
		runningJob.proceed();
	}
}
