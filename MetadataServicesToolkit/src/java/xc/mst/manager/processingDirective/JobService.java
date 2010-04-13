
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

import xc.mst.bo.processing.Job;
import xc.mst.dao.DatabaseConfigException;

/**
 * Interface used for creation/deletion/updating jobs.
 *
 * @author Sharmila Ranganathan
 */
public interface JobService {

    /**
     * Returns a job based on the ID of the schedule
     *
     *@param harvestScheduleId Id of harvest schedule
     * @return job
     * @throws DatabaseConfigException 
     */
    public List<Job> getByHarvestScheduleId(int harvestScheduleId) throws DatabaseConfigException;

    /**
     * Returns a job based on the ID of the service
     *
     * @return job
     * @throws DatabaseConfigException 
     */
    public List<Job> getByServiceId(int serviceId) throws DatabaseConfigException;

    /**
     * Returns a job based on the ID
     *
     * @param jobId The ID based on which the job object is retrieved
     * @return
     * @throws DatabaseConfigException 
     */
    public Job getByJobId(int jobId) throws DatabaseConfigException;

    /**
     * Returns list of jobs
     *
     * @return list of jobs
     * @throws DatabaseConfigException 
     */
    public List<Job> getAllJobs() throws DatabaseConfigException;

    /**
     * Inserts a new job
     *
     * @param job The job Object to be inserted
     */
    public void insertJob(Job job);

    /**
     * Deletes a job
     *
     * @param job The job Object to be deleted
     */
    public void deleteJob(Job job);

    /**
     * Updates a job
     * 
     * @param job The job to be updated
     */
    public void updateJob(Job job);
    
	/**
	 * Get the maximum order
	 *
	 * @return max order
	 * @throws DatabaseConfigException if unable to contact database
	 */
	public int getMaxOrder() throws DatabaseConfigException;
	
	/**
	 * Get next job in queue to execute
	 *   
	 * @return Job to execute 
	 * @throws DatabaseConfigException
	 */
	public Job getNextJobToExecute() throws DatabaseConfigException;
	
	/**
	 * Write jobs to queue
	 *   
	 * @throws DatabaseConfigException
	 */
	public void writeToFile() throws DatabaseConfigException;
}
