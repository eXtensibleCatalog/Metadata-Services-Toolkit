
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import xc.mst.constants.Constants;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 *This class is used to delete a repository from the database
 *
 * @author Tejaswi Haramurali
 */
public class DeleteRepository extends ActionSupport
{
        
    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The ID of the repository to be deleted */
    private int repositoryId;
    
	/** Error type */
	private String errorType; 

    /**
     * Set the ID of the repository to be deleted
     *
     * @param repoId The ID of the repository to be deleted
     */
    public void setRepositoryId(String repoId)
    {
        repositoryId = Integer.parseInt(repoId);
    }

    /**
     * Gets the ID of the repository to be deleted
     *
     * @return The ID of the repository to be deleted
     */
    public int getRepositoryId()
    {
        return repositoryId;
    }

    /**
     * Overrides default implementation to delete a repository.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            ProviderService providerService = new DefaultProviderService();
            providerService.deleteProvider(providerService.getProviderById(repositoryId));
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
     * Returns error type
     *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets error type
     * 
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
