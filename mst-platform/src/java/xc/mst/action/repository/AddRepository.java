
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.harvester.Hexception;
import xc.mst.harvester.ValidateRepository;
import xc.mst.utils.MSTConfiguration;

/**
 * This method is for adding a new repository to the database.
 *
 * @author  Tejaswi Haramurali
 */

public class AddRepository extends BaseActionSupport 
{

    /** Serial id */
	private static final long serialVersionUID = -2353352472168477475L;

	/** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);


    /** Represents the Name of the Repository that should be added */
    private String repositoryName;

    /**Represents the UR of the repository that should be added */
    private String repositoryURL;

    /**The ID of the repository to be added */
    private int repositoryId;

	/** Error type */
	private String errorType;

    /**
     * The method is used to add a repository
     *
     * @return returns the status of the add operation
     */
    public String addRepository()
    {
        try
        {

            Provider pr = new Provider();

            Provider repositorySameName = getProviderService().getProviderByName(repositoryName);
            Provider repositorySameURL = getProviderService().getProviderByURL(repositoryURL);
            if(repositorySameName!=null)
            {

               this.addFieldError("addRepositoryError", "A repository with the name '"+repositoryName+"' already exists.");
               errorType = "error";
               return INPUT;
            }
            else if(repositorySameURL!=null)
            {
               this.addFieldError("addRepositoryError", "A repository with the URL '"+repositoryURL+"' already exists");
               errorType = "error";
               return INPUT;
            }
            else
            {
               
                pr.setName(getRepositoryName());
                pr.setCreatedAt(new Date());
                pr.setUpdatedAt( new Timestamp(new Date().getTime()));
                pr.setLastValidationDate(new Date());
                pr.setOaiProviderUrl(getRepositoryURL());

                getProviderService().insertProvider(pr);

                ValidateRepository vr = (ValidateRepository)MSTConfiguration.getBean("ValidateRepository");
                vr.validate(pr.getId());

            }
            setRepositoryId(pr.getId());
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            errorType = "error";
            this.addFieldError("dbConfigError","Unable to access the database. There may be a problem with database configuration.");
            return INPUT;
        }
        catch(DataException e)
        {
            log.error(e.getMessage(),e);
            this.addFieldError("addRepositoryError", "Error occurred while adding repository. An email has been sent to the administrator");
            errorType = "error";
            getUserService().sendEmailErrorReport();
            return INPUT;
        }
        catch(Hexception e)
        {
            log.error(e.getMessage(),e);
            this.addFieldError("addRepositoryError", "Unable to validate the repository");
            errorType = "error";
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
     * Sets the ID of the repository to be added
     *
     * @param repositoryId repository ID
     */
    public void setRepositoryId(int repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    /**
     * Returns the ID of the repository to be added
     *
     * @return ID of the repository
     */
    public int getRepositoryId()
    {
        return repositoryId;
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

    
}