/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.repository;

import java.util.List;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.scheduling.HarvesterWorkerThread;
import xc.mst.scheduling.ProcessingDirectiveWorkerThread;
import xc.mst.scheduling.Scheduler;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;

/**
 * Service class that is used to Add/Delete/Update a Provider/Repository
 *
 * @author Tejaswi Haramurali
 */
public class DefaultProviderService implements ProviderService{

    /** The provider DAO object */
    ProviderDAO providerDao = new DefaultProviderDAO();

    /**
     * Returns a provider by its name
     *
     * @param providerName provider name
     * @return provider object
     * @throws DatabaseConfigException
     */
    public Provider getProviderByName(String providerName) throws DatabaseConfigException{
       return providerDao.getByName(providerName);
    }

    /**
     * Returns a provider by its ID
     *
     * @param providerId provider ID
     * @return provider object
     * @throws DatabaseConfigException
     */
    public Provider getProviderById(int providerId) throws DatabaseConfigException{
       return providerDao.getById(providerId);
    }

    /**
     * Returns a provider by its URL
     *
     * @param providerUrl provider URL
     * @return provider object
     * @throws DatabaseConfigException
     */
    public Provider getProviderByURL(String providerUrl) throws DatabaseConfigException {
        return providerDao.getByURL(providerUrl);
    }

    /**
     * Inserts a provider into the database
     *
     * @param provider provider object
     * @throws xc.mst.dao.DataException
     */
    public void insertProvider(Provider provider) throws DataException{

    	provider.setLogFileName(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "logs" + MSTConfiguration.FILE_SEPARATOR + "harvestIn"+ MSTConfiguration.FILE_SEPARATOR + provider.getName()+".txt");
        providerDao.insert(provider);
        LogWriter.addInfo(provider.getLogFileName(), "Beginning logging for " + provider.getName());
    }

    /**
     * Deletes provider form the database
     *
     * @param provider provider object
     * @throws xc.mst.dao.DataException
     */
    public void deleteProvider(Provider provider) throws DataException, IndexException{

    	// Delete schedule for this repository
    	
    	// Check if any harvest is running 
        if(Scheduler.getRunningJob()!=null)
        {
        	// Check if this repository is being harvested 
        	if (Scheduler.getRunningJob().getType().equals(Constants.THREAD_REPOSITORY)) {
        		HarvesterWorkerThread harvesterWorkerThread = (HarvesterWorkerThread)Scheduler.getRunningJob();
        		if (harvesterWorkerThread.getJobName().equals(provider.getName())) {
        			Scheduler.cancelRunningJob();
        		}
        	}
        	
        	// Check if this repository is being processed by processing directive
        	if (Scheduler.getRunningJob().getType().equals(Constants.THREAD_PROCESSING_DIRECTIVE)) {
        		ProcessingDirectiveWorkerThread processingDirectiveWorkerThread = (ProcessingDirectiveWorkerThread)Scheduler.getRunningJob();
        		Provider sourceProvider = processingDirectiveWorkerThread.getProcessingDirective().getSourceProvider();
        		if (sourceProvider != null && sourceProvider.getName().equals(provider.getName())) {
        			Scheduler.cancelRunningJob();
        		}
        	}
        }
        
    	ScheduleService scheduleService = new DefaultScheduleService();
    	HarvestSchedule harvestSchedule = scheduleService.getScheduleForProvider(provider);
    	if (harvestSchedule != null) {
    		scheduleService.deleteSchedule(harvestSchedule);
    	}

    	// Delete processing directive for this repository
    	ProcessingDirectiveService processingDirectiveService = new DefaultProcessingDirectiveService();
    	List<ProcessingDirective> directives =  processingDirectiveService.getBySourceProviderId(provider.getId());
    	for (ProcessingDirective directive:directives) {
    		processingDirectiveService.deleteProcessingDirective(directive);
    	}

    	// Delete provider
        providerDao.delete(provider);
    }

    /**
     * Updates the details of a provider
     *
     * @param provider provider object
     * @throws xc.mst.dao.DataException
     */
    public void updateProvider(Provider provider) throws DataException{
    	provider.setLogFileName(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "logs" + MSTConfiguration.FILE_SEPARATOR + "harvestIn"+ MSTConfiguration.FILE_SEPARATOR + provider.getName()+".txt");
        providerDao.update(provider);
    }

    /**
     * Returns a list of all the providers
     *
     * @return provider list
     * @throws DatabaseConfigException
     */
    public List<Provider> getAllProviders() throws DatabaseConfigException
    {
        return providerDao.getAll();
    }

    /**
   * Returns a list of all providers sorted
   *
   * @param sort determines if the rows are to be sorted in ascending or descending order
   * @param columnSorted column on which the sorting is done
   * @return list of providers
     * @throws DatabaseConfigException
   */
    public List<Provider> getAllProvidersSorted(boolean sort,String columnSorted) throws DatabaseConfigException
    {
        return providerDao.getSorted(sort, columnSorted);
    }
}
