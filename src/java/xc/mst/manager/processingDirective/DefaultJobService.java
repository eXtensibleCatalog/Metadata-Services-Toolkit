/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.processingDirective;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.Job;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.SetService;
import xc.mst.utils.MSTConfiguration;

/**
 * Service Class that is used for the creation/deletion/updating of jobs.
 *
 * @author Sharmila Ranganathan
 */
public class DefaultJobService extends BaseService implements JobService {

	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Returns a job with the given ID
     *
     * @param jobId job ID
     * @return job object
     * @throws DatabaseConfigException 
     */
    public Job getByJobId(int jobId) throws DatabaseConfigException {
        return jobDAO.getById(jobId);
    }

    /**
     * Insert a new job
     *
     * @param job Job to insert
     */
    public void insertJob(Job job) {

    	try {
			jobDAO.insert(job);
			
			// Write the current jobs in queue to a file
			writeToFile();
		} catch (DataException e) {
			log.error("Data Exception occured when inserting a Job." , e);
		}
    }

    /**
     * Deletes a job
     *
     * @param job job object
     */
    public void deleteJob(Job job) {
        try {
			jobDAO.delete(job);
			
			// Write the current jobs in queue to a file
			writeToFile();
		} catch (DataException e) {
			log.error("Data Exception occured when deleting a Job." , e);
		}
    }

    /**
     * Updates the details of a job
     *
     * @param job job object
     */
    public void updateJob(Job job) {
        try {
			jobDAO.update(job);
		} catch (DataException e) {
			log.error("Data Exception occured when updating a Job." , e);
		}
    }

    /**
     * Returns a list of all jobs
     *
     * @return list of jobs
     * @throws DatabaseConfigException 
     */
    public List<Job> getAllJobs() throws DatabaseConfigException
    {
        return jobDAO.getAll();
    }

    /**
     * Returns a jobs associated with a schedule
     *
     * @param scheduleId Harvest schedule ID
     * @return list of jobs
     * @throws DatabaseConfigException 
     */
    public List<Job> getByHarvestScheduleId(int scheduleId) throws DatabaseConfigException
    {
        return jobDAO.getByHarvestScheduleId(scheduleId);
    }

    /**
     * Returns a list of jobs associated with a service
     *
     * @param serviceId service ID
     * @return list of jobs
     * @throws DatabaseConfigException 
     */
    public List<Job> getByServiceId(int serviceId) throws DatabaseConfigException
    {
        return jobDAO.getByServiceId(serviceId);
    }

	/**
	 * Get the maximum order
	 *
	 * @return max order
	 * @throws DatabaseConfigException 
	 */
	public int getMaxOrder() throws DatabaseConfigException {
		return jobDAO.getMaxOrder();
	}
	
	/**
	 * Get next job in queue to execute
	 *   
	 * @return Job to execute 
	 * @throws DatabaseConfigException
	 */
	public Job getNextJobToExecute() throws DatabaseConfigException
	{
		return jobDAO.getNextJobToExecute();
	}
  
	/**
	 * Write jobs in database queue to a file
	 *   
	 * @throws DatabaseConfigException
	 */
	public void writeToFile() throws DatabaseConfigException
	{
		List<Job> jobs = getAllJobs();
		SetService setService = new DefaultSetService();
		
		try{
		    // Create file 
		    FileWriter fstream = new FileWriter(MSTConfiguration.getUrlPath() +  MSTConfiguration.FILE_SEPARATOR  + "JobsInQueue.txt");
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    out.write("Order\t\t Job Name\t\t\n");
		    
		    // Write all jobs in queue to a file
		    for (Job job: jobs) {
		    	if (job.getJobType().equalsIgnoreCase(Constants.THREAD_SERVICE)) {
		    		out.write(job.getOrder() + "\t\t" + job.getService().getName());
		    		if (job.getOutputSetId() > 0) { 
		    			Set set = setService.getSetById(job.getOutputSetId());
		    			out.write(", Output Set : " +  (set != null ? set.getDisplayName() : ""));
		    		} else {
		    			out.write(", Output Set : NONE" );
		    		}
		    		out.write("\n");
		    	} else if (job.getJobType().equalsIgnoreCase(Constants.THREAD_PROCESSING_DIRECTIVE)) {
		    		if (job.getProcessingDirective().getSourceProvider() != null) {
		    			out.write(job.getOrder() + "\t\t" + "Processing directive: [Source=" +  job.getProcessingDirective().getSourceProvider().getName() + ", Service=" + job.getProcessingDirective().getService().getName() + "]\n");
		    		} else {
		    			out.write(job.getOrder() + "\t\t" + "Processing directive: [Source=" +  job.getProcessingDirective().getSourceService().getName() + ", Service=" + job.getProcessingDirective().getService().getName() + "]\n");
		    		}
		    	} else if (job.getJobType().equalsIgnoreCase(Constants.THREAD_REPOSITORY)) {
		    		out.write(job.getOrder() + "\t\t" + "Harvest repository: " + job.getHarvestSchedule().getProvider().getName() + "\n");
		    	} else if (job.getJobType().equalsIgnoreCase(Constants.THREAD_SERVICE_REPROCESS)) {
		    		out.write(job.getOrder() + "\t\t" + "Reprocessing records through service: " + job.getService().getName() + "\n");
		    	} else if (job.getJobType().equalsIgnoreCase(Constants.THREAD_DELETE_SERVICE)) {
		    		out.write(job.getOrder() + "\t\t" + "Deleting service " + job.getService().getName() + " and its records. \n");
		    	}
		    }
		    
		    //Close the output stream
		    out.close();
		} catch(IOException ioe) {
			log.error("IOException occured when writing jobs to file", ioe);
		}
		
		
	}
}
