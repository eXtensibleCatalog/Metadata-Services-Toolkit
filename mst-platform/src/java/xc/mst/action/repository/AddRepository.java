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

/**
 * This method is for adding a new repository to the database.
 * 
 * @author Tejaswi Haramurali
 */

public class AddRepository extends BaseActionSupport {

    /** Serial id */
    private static final long serialVersionUID = -2353352472168477475L;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Represents the Name of the Repository that should be added */
    private String repositoryName;

    /** Represents the UR of the repository that should be added */
    private String repositoryURL;

    /** The ID of the repository to be added */
    private int repositoryId;

    /** Number of records to harvest */
    private int numberOfRecordsToHarvest;

    /** Error type */
    private String errorType;

    /**
     * The method is used to add a repository
     * 
     * @return returns the status of the add operation
     */
    public String addRepository() {
        Provider p = new Provider();
        String error = getRepositoryService().save(repositoryName, repositoryURL, p, numberOfRecordsToHarvest);
        if (error != null) {
            errorType = "error";
            this.addFieldError("editRepository", error);
            return INPUT;
        } else {
            setRepositoryId(p.getId());
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
     * @param errorType
     *            error type
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * Sets the ID of the repository to be added
     * 
     * @param repositoryId
     *            repository ID
     */
    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    /**
     * Returns the ID of the repository to be added
     * 
     * @return ID of the repository
     */
    public int getRepositoryId() {
        return repositoryId;
    }

    /**
     * Sets the Repository Name to the specified value
     * 
     * @param repoName
     *            The name to be assigned to the repository
     */
    public void setRepositoryName(String repoName) {
        repositoryName = repoName.trim();
    }

    /**
     * Gets the name of the repository
     * 
     * @return returns the name of the repository
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * Sets the URL of the repository to the specified value
     * 
     * @param repoURL
     *            The URL value to be assigned to the repository
     */
    public void setRepositoryURL(String repoURL) {
        repositoryURL = repoURL.trim();
    }

    /**
     * Gets the URL of the repository
     * 
     * @return returns URL of the repository
     */
    public String getRepositoryURL() {
        return repositoryURL;
    }

    /**
     * Gets the number of records to harvest
     * 
     * @return number of records to harvest
     */
    public int getNumberOfRecordsToHarvest() {
        return numberOfRecordsToHarvest;
    }

    /**
     * Sets the number of records to harvest
     * 
     * @param numberOfRecordsToHarvest
     *            number of records to harvest
     */
    public void setNumberOfRecordsToHarvest(int numberOfRecordsToHarvest) {
        this.numberOfRecordsToHarvest = numberOfRecordsToHarvest;
    }

}
