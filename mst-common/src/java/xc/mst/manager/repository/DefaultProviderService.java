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
import xc.mst.manager.BaseService;
import xc.mst.manager.IndexException;
import xc.mst.scheduling.HarvesterWorkerThread;
import xc.mst.scheduling.ProcessingDirectiveWorkerThread;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;

/**
 * Service class that is used to Add/Delete/Update a Provider/Repository
 *
 * @author Tejaswi Haramurali
 */
public class DefaultProviderService extends BaseService implements ProviderService{

    /**
     * Returns a provider by its name
     *
     * @param providerName provider name
     * @return provider object
     * @throws DatabaseConfigException
     */
    public Provider getProviderByName(String providerName) throws DatabaseConfigException{
       return getProviderDAO().getByName(providerName);
    }

    /**
     * Returns a provider by its ID
     *
     * @param providerId provider ID
     * @return provider object
     * @throws DatabaseConfigException
     */
    public Provider getProviderById(int providerId) throws DatabaseConfigException{
       return getProviderDAO().getById(providerId);
    }

    /**
     * Returns a provider by its URL
     *
     * @param providerUrl provider URL
     * @return provider object
     * @throws DatabaseConfigException
     */
    public Provider getProviderByURL(String providerUrl) throws DatabaseConfigException {
        return getProviderDAO().getByURL(providerUrl);
    }

    /**
     * Inserts a provider into the database
     *
     * @param provider provider object
     * @throws xc.mst.dao.DataException
     */
    public void insertProvider(Provider provider) throws DataException{

    	provider.setLogFileName(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "logs" + MSTConfiguration.FILE_SEPARATOR + "harvestIn"+ MSTConfiguration.FILE_SEPARATOR + provider.getName()+".txt");
    	getProviderDAO().insert(provider);
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
        if(getScheduler().getRunningJob()!=null)
        {
        	// Check if this repository is being harvested 
        	if (getScheduler().getRunningJob().getType().equals(Constants.THREAD_REPOSITORY)) {
        		HarvesterWorkerThread harvesterWorkerThread = (HarvesterWorkerThread)getScheduler().getRunningJob();
        		if (harvesterWorkerThread.getJobName().equals(provider.getName())) {
        			getScheduler().cancelRunningJob();
        		}
        	}
        	
        	// Check if this repository is being processed by processing directive
        	if (getScheduler().getRunningJob().getType().equals(Constants.THREAD_PROCESSING_DIRECTIVE)) {
        		ProcessingDirectiveWorkerThread processingDirectiveWorkerThread = (ProcessingDirectiveWorkerThread)getScheduler().getRunningJob();
        		Provider sourceProvider = processingDirectiveWorkerThread.getProcessingDirective().getSourceProvider();
        		if (sourceProvider != null && sourceProvider.getName().equals(provider.getName())) {
        			getScheduler().cancelRunningJob();
        		}
        	}
        }
        
    	HarvestSchedule harvestSchedule = getScheduleService().getScheduleForProvider(provider);
    	if (harvestSchedule != null) {
    		getScheduleService().deleteSchedule(harvestSchedule);
    	}

    	// Delete processing directive for this repository
    	List<ProcessingDirective> directives =  getProcessingDirectiveService().getBySourceProviderId(provider.getId());
    	for (ProcessingDirective directive:directives) {
    		getProcessingDirectiveService().deleteProcessingDirective(directive);
    	}

    	// Delete provider
    	getProviderDAO().delete(provider);
    }

    /**
     * Updates the details of a provider
     *
     * @param provider provider object
     * @throws xc.mst.dao.DataException
     */
    public void updateProvider(Provider provider) throws DataException{
    	provider.setLogFileName(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "logs" + MSTConfiguration.FILE_SEPARATOR + "harvestIn"+ MSTConfiguration.FILE_SEPARATOR + provider.getName()+".txt");
    	getProviderDAO().update(provider);
    }

    /**
     * Returns a list of all the providers
     *
     * @return provider list
     * @throws DatabaseConfigException
     */
    public List<Provider> getAllProviders() throws DatabaseConfigException
    {
        return getProviderDAO().getAll();
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
        return getProviderDAO().getSorted(sort, columnSorted);
    }
}
