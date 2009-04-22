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

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.processing.DefaultProcessingDirectiveDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.scheduling.Scheduler;
import xc.mst.scheduling.ServiceWorkerThread;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

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
     */
    public ProcessingDirective getByProcessingDirectiveId(int processingDirectiveId) {
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
			
			runProcessingDirective(processingDirective);
		} catch (DataException e) {
			log.error("Data Exception", e);
		}
    }

    /**
     * Returns a list of all processing directives
     *
     * @return list of processing directives
     */
    public List<ProcessingDirective> getAllProcessingDirectives()
    {
        return processingDirectiveDao.getAll();
    }

    /**
     * Returns a processing directives associated with a provider
     *
     * @param providerId provider ID
     * @return list of processing directives
     */
    public List getBySourceProviderId(int providerId)
    {
        return processingDirectiveDao.getBySourceProviderId(providerId);
    }

    /**
     * Returns a list of processing directives associated with a service
     *
     * @param serviceId service ID
     * @return list of processing directives
     */
    public List getBySourceServiceId(int serviceId)
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
    	// If the processing directive's source is a provider then we should check all
    	// records harvested from that provider.  Otherwise we should check all records
    	// processed by the processing directive's source service.
    	RecordList recordsToCheck = (pd.getSourceProvider() != null ?
    			                     recordService.getByProviderId(pd.getSourceProvider().getId()) :
    			                	 recordService.getByServiceId(pd.getSourceService().getId()));

    	// Check each record against the new processing directive. Mark the matched
    	// records as input for the processing directive's service
    	for(Record record : recordsToCheck)
    	{
    		// Check if the record matches any of the metadata formats for the new processing directive
			if(pd.getTriggeringFormats().contains(record.getFormat()))
			{
				record.addInputForService(pd.getService());

				try
				{
					recordService.update(record);
				} // end try(update the record)
				catch (DataException e)
				{
					log.error("Data Exception", e);
				} // end catch(DataException)
			} // end if(format matched)

			// If the metadata format didn't match, check if the record is in any of the sets for the current processing directive
			else
			{
				for(Set set : record.getSets())
				{
					if(pd.getTriggeringSets().contains(set))
					{
						record.addInputForService(pd.getService());

						try
						{
							recordService.update(record);
						} // end try(update the record)
						catch (DataException e)
						{
							log.error("Data Exception", e);
						} // end catch(DataException)

						break;
					} // end if(the set matched the processing directive)
				} // end loop over the record's sets
			} // end else(the format did not trigger the processing directive)
    	} // end loop over potentially matching records

    	// Reopen the reader so it can see the changes made by running the service

		try
		{
			SolrIndexManager.getInstance().commitIndex();
		} // end try(reopen the reader)
		catch(Exception e)
		{
			log.error("An error occurred while reopening the index");
		} // end catch(Exception)

    	// Run the service that is the processing directive's target
    	try
		{
			ServiceWorkerThread serviceThread = new ServiceWorkerThread();
			serviceThread.setServiceId(pd.getService().getId());
			if(pd.getOutputSet() != null)
				serviceThread.setOutputSetId(pd.getOutputSet().getId());
			Scheduler.scheduleThread(serviceThread);
		} // end try(start the service)
		catch(Exception e)
		{
			log.error("An error occurred while running the service with ID " + pd.getService().getId() + ".", e);
		} // end catch(Exception)
    } // end method runNewProcessingDirective(ProcessingDirective)
}
