
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
import java.util.Date;
import org.apache.log4j.Logger;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * This method is used to edit the details of a repository
 *
 * @author Tejaswi Haramurali
 */

public class EditRepository extends ActionSupport
{
    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**The ID of the repository to be edited */
    private int repositoryId;

   /**The Name of the repository to be edited */
    private String repositoryName;

    /**The URL of the repository to be edited */
    private String repositoryURL;
    
	/** Error type */
	private String errorType; 

    /**
     * Sets the Repository Name to the specified value
     *
     * @param repoName The name to be assigned to the repository
     */
    public void setRepositoryName(String repoName)
    {

        repositoryName = repoName.trim();
    }

    /**
     * Gets the name of the repository
     *
     * @return returns the name of the repository
     */
    public String getRepositoryName()
    {
        return repositoryName;
    }

    /**
     * Sets the URL of the repository to the specified value
     *
     * @param repoURL The URL value to be assigned to the repository
     */
    public void setRepositoryURL(String repoURL)
    {

        repositoryURL = repoURL.trim();
    }

    /**
     * Gets the URL of the repository
     *
     * @return returns URL of the repository
     */
    public String getRepositoryURL()
    {
        return repositoryURL;
    }

    /**
     * Sets the ID of the repository to be edited
     *
     * @param RepoId The ID of the repository to be edited
     */
    public void setRepositoryId(String RepoId)
    {
        repositoryId = Integer.parseInt(RepoId);
    }

    /**
     * Gets the ID of the repository to be edited
     *
     * @return returns the ID of the repository to be edited
     */
    public int getRepositoryId()
    {
        return repositoryId;
    }

   /**
     * Overrides default implementation to edit the details of a repository.
    *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

            Provider provider = new DefaultProviderService().getProviderById(repositoryId);
            setRepositoryName(provider.getName());
            setRepositoryURL(provider.getOaiProviderUrl());
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            return INPUT;
        }

    }

    /**
     * This method is used to edit repository information
     *
     * @return returns the status of the edit operation
     */
    public String editRepository()
    {
        try
        {
            ProviderService providerService = new DefaultProviderService();
            Provider provider = providerService.getProviderById(repositoryId);
            provider.setName(getRepositoryName());
            
            boolean urlChanged = false;
            if(provider.getOaiProviderUrl().equalsIgnoreCase(repositoryURL))
            {
                urlChanged = false;
            }
            else
            {
                urlChanged = true;

            }
            provider.setOaiProviderUrl(getRepositoryURL());


            Provider repositorySameName = providerService.getProviderByName(repositoryName);
            Provider repositorySameURL = providerService.getProviderByURL(repositoryURL);
            if(repositorySameName!=null||repositorySameURL!=null)
            {

                if(repositorySameName!=null)
                {

                    if(repositorySameName.getId()!=getRepositoryId())
                    {

                         this.addFieldError("editRepositoryError", "Repository with Name '"+repositoryName+"' already exists");
                         errorType = "error";
                        
                         return INPUT;
                    }
                    else
                    {
                        if(repositorySameURL!=null)
                        {
                            if(repositorySameURL.getId()!=getRepositoryId())
                            {
                                this.addFieldError("editRepositoryError", "Repository with URL '"+repositoryURL+"' already exists");
                                errorType = "error";
                               
                                return INPUT;
                            }
                        }
                        else
                        {
                            if(urlChanged)
                            {

                                provider.setIdentify(false);
                                provider.setListFormats(false);
                                provider.setListSets(false);
                                provider.removeAllFormats();
                                provider.removeAllSets();
                                provider.setLastValidationDate(new Date());
                                providerService.updateProvider(provider);
                                ValidateRepository vr = new ValidateRepository();
                                vr.validate(provider.getId());
                            }
                            else
                            {
                                providerService.updateProvider(provider);
                            }
                        }

                        return SUCCESS;

                    }
                }
                else
                {

                    if(repositorySameURL.getId()!=getRepositoryId())
                    {

                         this.addFieldError("editRepositoryError", "Repository with URL '"+repositoryURL+"' already exists");
                         errorType = "error";
                         return INPUT;
                    }
                    else
                    {

                        if(urlChanged)
                        {

                            provider.setIdentify(false);
                            provider.setListFormats(false);
                            provider.setListSets(false);
                            provider.removeAllFormats();
                            provider.removeAllSets();
                            provider.setLastValidationDate(new Date());
                            providerService.updateProvider(provider);
                            ValidateRepository vr = new ValidateRepository();
                            vr.validate(provider.getId());
                        }
                        else
                        {
                             providerService.updateProvider(provider);
                        }

                        return SUCCESS;
                    }
                }

            }
            else
            {

                if(urlChanged)
                {

                    provider.setIdentify(false);
                    provider.setListFormats(false);
                    provider.setListSets(false);
                    provider.removeAllFormats();
                    provider.removeAllSets();
                    provider.setLastValidationDate(new Date());
                    providerService.updateProvider(provider);
                    ValidateRepository vr = new ValidateRepository();
                    vr.validate(provider.getId());
                }
                else
                {
                     providerService.updateProvider(provider);
                }

                return SUCCESS;
            }
        }
        catch(Exception e)
        {
            log.debug(e);
            return SUCCESS;
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
