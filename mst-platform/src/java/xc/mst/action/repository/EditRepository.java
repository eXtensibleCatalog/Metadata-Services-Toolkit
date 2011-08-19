
/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;

/**
 * This method is used to edit the details of a repository
 *
 * @author Tejaswi Haramurali
 */

@SuppressWarnings("serial")
public class EditRepository extends BaseActionSupport
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

    /** Number of records to harvest */
    private long numberOfRecordsToHarvest;

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

            Provider provider = getProviderService().getProviderById(repositoryId);

            if(provider==null)
            {
                errorType = "error";
                getUserService().sendEmailErrorReport();
                this.addFieldError("viewRepositoryError","There was a problem displaying the edit Repository page. An email has been sent to the administrator.");
                return INPUT;
            }
            setRepositoryName(provider.getName());
            setRepositoryURL(provider.getOaiProviderUrl());
            setNumberOfRecordsToHarvest(provider.getNumberOfRecordsToHarvest());
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            errorType = "error";
            this.addFieldError("dbConfigError","Unable to access the database. There may be a problem with database configuration.");
            return SUCCESS;
        }

    }

    /**
     * This method is used to edit repository information
     *
     * @return returns the status of the edit operation
     */
    public String editRepository()
    {
        Provider p = new Provider();
        p.setId(repositoryId);
        String error = getRepositoryService().save(repositoryName, repositoryURL, p, numberOfRecordsToHarvest);
        if (error != null) {
            errorType = "error";
            this.addFieldError("editRepository",error);
            return INPUT;
        } else {
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
    public void setRepositoryId(int repositoryId)
    {
        this.repositoryId = repositoryId;
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
     * Get number of records to harvest
     */
    public long getNumberOfRecordsToHarvest() {
        return numberOfRecordsToHarvest;
    }

    /**
     * Set number of records to harvest
     */
    public void setNumberOfRecordsToHarvest(long numberOfRecordsToHarvest) {
        this.numberOfRecordsToHarvest = numberOfRecordsToHarvest;
    }
}
