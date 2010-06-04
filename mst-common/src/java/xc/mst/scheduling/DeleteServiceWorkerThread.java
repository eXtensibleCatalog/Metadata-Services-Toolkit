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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.Job;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.processingDirective.JobService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * A Thread which deletes the service and its records .
 * 
 * @author Sharmila Ranganathan
 */
public class DeleteServiceWorkerThread extends WorkerThread 
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/** 
	 * Type of thread 
	 */
	public static final String type = Constants.THREAD_DELETE_SERVICE;

	/**
	 * The flag for indicating cancel service operation.
	 */
	private boolean isCanceled;

	/**
	 * The flag for indicating service operation pause.
	 */
	private boolean isPaused;
	
	/**
	 * The ID of the service to be deleted
	 */
	private int serviceId;
	
	/**
	 * The service to be deleted
	 */
	private Service service;
	
	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = (RecordService)MSTConfiguration.getInstance().getBean("RecordService");
	
	/**
	 * Manager for getting, inserting and updating jobs
	 */
	private static JobService jobService = (JobService)MSTConfiguration.getInstance().getBean("JobService");
	
	/**
	 * Manager for getting, inserting and updating service
	 */
	private static ServicesService serviceManager = (ServicesService)MSTConfiguration.getInstance().getBean("ServicesService");
	
	@Override
	public void run() 
	{
		try
		{
			
	    	service = serviceManager.getServiceById(serviceId);

	    	log.info("Starting thread to delete service " + service.getName());
	    	
	    	// Delete the records processed by the service and send the deleted
	    	// records to subsequent services so they know about the delete
			RecordList records = recordService.getByServiceId(serviceId);

			// A list of services which must be run after this one
			List<Service> affectedServices = new ArrayList<Service>();
			List<Record> updatedPredecessors = new ArrayList<Record>();
			
			for(Record record : records)
			{
				// set as deleted
				record.setDeleted(true);
				
				// Get all predecessors & remove the current record as successor
				List<Record> predecessors =  record.getProcessedFrom();
				for (Record predecessor : predecessors) {
					
					int index = updatedPredecessors.indexOf(predecessor);
					if (index < 0) {
						predecessor =  recordService.getById(predecessor.getId());
					} else {
						predecessor = updatedPredecessors.get(index);
					}
					
					// Remove errors added to predecessor record 
					List<String> errors =  predecessor.getErrors();
					
					if (errors != null && errors.size() > 0) {
						
						ArrayList<String> errorsToRemove = new ArrayList<String>(); 
					
						for (String error: errors) {
							
							if(error.startsWith(Integer.valueOf(service.getId()).toString() + "-")) {
								errorsToRemove.add(error);
							}
						}
						if (errorsToRemove.size() > 0) {
							predecessor.getErrors().removeAll(errorsToRemove);
						}
					}

					predecessor.removeSucessor(record);

					// Remove the reference for the service which is being deleted from its predecessor
					predecessor.removeProcessedByService(service);
					updatedPredecessors.add(predecessor);
				}
				
				record.setUpdatedAt(new Date());
				for(Service nextService : record.getProcessedByServices())
				{
					record.addInputForService(nextService);
					if(!affectedServices.contains(nextService))
						affectedServices.add(nextService);
				}
				recordService.update(record);
			}
			
			// Update all predecessor records
			for (Record updatedPredecessor:updatedPredecessors) {
				recordService.update(updatedPredecessor);				
			}
			
			((SolrIndexManager)MSTConfiguration.getInstance().getBean("SolrIndexManager")).commitIndex();

			// Schedule subsequent services to process that the record was deleted
			for(Service nextSerivce : affectedServices)
			{
				try {
					Job job = new Job(nextSerivce, 0, Constants.THREAD_SERVICE);
					job.setOrder(jobService.getMaxOrder() + 1); 
					jobService.insertJob(job);
				} catch (DatabaseConfigException dce) {
					log.error("DatabaseConfig exception occured when ading jobs to database", dce);
				}
			}

			serviceManager.deleteService(service);
			
			log.info("Finished deleting service " + service.getName());

		} catch (DataException de) {
			log.error("Exception occured while updating records.", de);
		} catch (IndexException ie) {
			log.error("Exception occured while commiting to Solr index.", ie);
		}
	}

	@Override
	public void cancel() 
	{
		isCanceled = true;
	}

	@Override
	public void pause() 
	{
		isPaused = true;
	}

	@Override
	public void proceed() 
	{
		isPaused = false;
	}

	@Override
	public String getJobName() 
	{
		return "Deleting service and its records";
	}

	@Override
	public String getJobStatus() 
	{
		if(isCanceled)
			return Constants.STATUS_SERVICE_CANCELED;
		else if(isPaused)
			return Constants.STATUS_SERVICE_PAUSED;
		else
			return Constants.STATUS_SERVICE_RUNNING;
	}

	@Override
	public String getType() 
	{
		return type;
	}

	@Override
	public int getProcessedRecordCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalRecordCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Service getService() {
		return service;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
}
