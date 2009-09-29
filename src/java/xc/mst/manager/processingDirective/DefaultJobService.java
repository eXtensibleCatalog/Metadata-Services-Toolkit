/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.processingDirective;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.Job;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.processing.DefaultJobDAO;
import xc.mst.dao.processing.JobDAO;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;

/**
 * Service Class that is used for the creation/deletion/updating of jobs.
 *
 * @author Sharmila Ranganathan
 */
public class DefaultJobService implements JobService {

	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** DAO for jobs */
    private JobDAO jobDAO = new DefaultJobDAO();
    
    /**
	 * Manager for getting, inserting and updating records
	 */
	protected static RecordService recordService = new DefaultRecordService();

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
    
}
