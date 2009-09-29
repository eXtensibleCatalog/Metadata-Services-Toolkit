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
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.processing.DefaultProcessingDirectiveDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.scheduling.ProcessingDirectiveWorkerThread;
import xc.mst.scheduling.Scheduler;

/**
 * Service Class that is used for the creation/deletion/updating of Processing Directives.
 *
 * @author Tejaswi Haramurali
 */
public class DefaultProcessingDirectiveService implements ProcessingDirectiveService{

	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** DAO for processing directives */
    private ProcessingDirectiveDAO processingDirectiveDao = new DefaultProcessingDirectiveDAO();

    /** DAO for services */
    private ServiceDAO serviceDao = new DefaultServiceDAO();
    
    /**
	 * Manager for getting, inserting and updating records
	 */
	protected static RecordService recordService = new DefaultRecordService();

    /**
     * Returns a procesisng directive  with the given ID
     *
     * @param processingDirectiveId Processing Directive ID
     * @return processing directive object
     * @throws DatabaseConfigException 
     */
    public ProcessingDirective getByProcessingDirectiveId(int processingDirectiveId) throws DatabaseConfigException {
        return processingDirectiveDao.getById(processingDirectiveId);
    }

    /**
     * Insert a new processing directive
     *
     * @param processingDirective Processing Directive
     */
    public void insertProcessingDirective(ProcessingDirective processingDirective) {
        try {
			processingDirectiveDao.insert(processingDirective);
			
			// If the processing directive has an output set that is not
			// already in the target service, we need to add it as an input set
			// to that service
			if(processingDirective.getOutputSet() != null)
			{
				processingDirective.getService().addOutputSet(processingDirective.getOutputSet());
				serviceDao.update(processingDirective.getService());
			}
			
			runProcessingDirective(processingDirective);
		} catch (DataException e) {
			log.error("Data Exception", e);
		}
    }

    /**
     * Deletes a processing directive
     *
     * @param processingDirective processing directive object
     */
    public void deleteProcessingDirective(ProcessingDirective processingDirective) {
        try {
			processingDirectiveDao.delete(processingDirective);
		} catch (DataException e) {
			log.error("Data Exception", e);
		}
    }

    /**
     * Updates the details of a procesisng directive
     *
     * @param processingDirective processing directive object
     */
    public void updateProcessingDirective(ProcessingDirective processingDirective) {
        try {
			processingDirectiveDao.update(processingDirective);
			
			// If the processing directive has an output set that is not
			// already in the target service, we need to add it as an input set
			// to that service
			if(processingDirective.getOutputSet() != null)
			{
				processingDirective.getService().addOutputSet(processingDirective.getOutputSet());
				serviceDao.update(processingDirective.getService());
			}
			
//			runProcessingDirective(processingDirective);
		} catch (DataException e) {
			log.error("Data Exception", e);
		}
    }

    /**
     * Returns a list of all processing directives
     *
     * @return list of processing directives
     * @throws DatabaseConfigException 
     */
    public List<ProcessingDirective> getAllProcessingDirectives() throws DatabaseConfigException
    {
        return processingDirectiveDao.getAll();
    }

    /**
     * Returns a processing directives associated with a provider
     *
     * @param providerId provider ID
     * @return list of processing directives
     * @throws DatabaseConfigException 
     */
    public List<ProcessingDirective> getBySourceProviderId(int providerId) throws DatabaseConfigException
    {
        return processingDirectiveDao.getBySourceProviderId(providerId);
    }

    /**
     * Returns a list of processing directives associated with a service
     *
     * @param serviceId service ID
     * @return list of processing directives
     * @throws DatabaseConfigException 
     */
    public List<ProcessingDirective> getBySourceServiceId(int serviceId) throws DatabaseConfigException
    {
        return processingDirectiveDao.getBySourceServiceId(serviceId);
    }

    /**
     * Checks a processing directive against all records from its source.
     * If any records match, they are marked as input for the target service and
     * that service is scheduled to be run.
     *
     * @param pd  The processing directive to check
     */
    private void runProcessingDirective(ProcessingDirective pd)
    {
    	JobService jobService = new DefaultJobService();
    	
    	// Add job to database queue
		try {
			Job job = new Job(pd);
			job.setOrder(jobService.getMaxOrder() + 1); 
			jobService.insertJob(job);
		} catch (DatabaseConfigException dce) {
			log.error("DatabaseConfig exception occured when ading jobs to database", dce);
		}
    } // end method runNewProcessingDirective(ProcessingDirective)
}
