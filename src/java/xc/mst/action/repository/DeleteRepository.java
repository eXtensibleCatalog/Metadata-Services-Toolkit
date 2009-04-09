
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

import com.opensymphony.xwork2.ActionSupport;

/**
 *This class is used to delete a repository from the database
 *
 * @author Tejaswi Haramurali
 */
public class DeleteRepository extends ActionSupport
{
        
    /**
	 * 
	 */
	private static final long serialVersionUID = 4498437059514909755L;

	/** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The ID of the repository to be deleted */
    private int repositoryId;
    
	/** Error type */
	private String errorType; 
	
	/** Message explaining why the repository cannot be deleted */
	private String message;

	/** Provider service */
    private ProviderService providerService = new DefaultProviderService();
    
    /** Schedule service */
    private ScheduleService scheduleService = new DefaultScheduleService();

    /** Determines whether repository is deleted */
	private boolean deleted;
    
    /**
     * set the ID of the repository to be deleted
     * @param repoId The ID of the repository to be deleted
     */
    public void setRepositoryId(String repoId)
    {
        repositoryId = Integer.parseInt(repoId);
    }

    /**
     * gets the ID of the repository to be deleted
     * @return The ID of the repository to be deleted
     */
    public int getRepositoryId()
    {
        return repositoryId;
    }

    /**
     * Overrides default implementation to delete a repository.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            
            Provider provider = providerService.getProviderById(repositoryId);
            // Check if harvest has just begun
//            HarvestSchedule harvestSchedule = scheduleService.getScheduleForProvider(provider);
//            harvestSchedule.get
            
            // Delete provider if not yet harvested.
            if (provider.getLastHarvestEndTime() != null) {
                message = "Repository has harvested data and cannot be deleted";
                deleted = false;
            }

            //            List<ProcessingDirective> processingDirectivesList = PDService.getBySourceProviderId(repositoryId);
//            Iterator iter = processingDirectivesList.iterator();
//            while(iter.hasNext())
//            {
//                ProcessingDirective processingDirective = (ProcessingDirective)iter.next();
//                PDIFService.deleteInputFormatsForProcessingDirective(processingDirective.getId());
//                PDISService.deleteInputSetsForProcessingDirective(processingDirective.getId());
//                PDService.deleteProcessingDirective(processingDirective);
//            }
//            providerService.deleteProvider(provider);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            this.addFieldError("viewRepositoryError", "Repository cannot be deleted");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Delete repository
     * 
     * @return
     */
    public String delete() {
     	
    	try {
	    	Provider provider = providerService.getProviderById(repositoryId);
	    	providerService.deleteProvider(provider);
    	}
    	catch(DataException de)
        {
            log.debug(de);
            this.addFieldError("viewRepositoryError", "Repository cannot be deleted");
            errorType = "error";
            return INPUT;
        }     	

    	return SUCCESS;
    }
    
	/**
     * returns error type
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * sets error type
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getMessage() {
		return message;
	}

	public boolean isDeleted() {
		return deleted;
	}

}
