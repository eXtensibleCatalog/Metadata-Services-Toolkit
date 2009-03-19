
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

import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

import xc.mst.action.UserAware;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * This method is for adding a new repository to the database.
 *
 * @author  Tejaswi Haramurali
 */

public class AddRepository extends ActionSupport implements UserAware
{

    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Represents the User who is logged in */
    private User user;

    /** Represents the Name of the Repository that should be added */
    private String repositoryName;

    /**Represents the UR of the repository that should be added */
    private String repositoryURL;

    /**The ID of the repository to be added */
    private int repositoryId;

    /**
     * sets the ID of the repository to be added
     * @param repositoryId repository ID
     */
    public void setRepositoryId(int repositoryId)
    {
        this.repositoryId = repositoryId;
    }
    public int getRepositoryId()
    {
        return repositoryId;
    }
    /**
     * sets the Repository Name to the specified value
     * @param repoName The name to be assigned to the repository
     */
    public void setRepositoryName(String repoName)
    {
        repositoryName = repoName.trim();
    }

    /**
     * Gets the name of the repository
     * @return returns the name of the repository
     */
    public String getRepositoryName()
    {
        return repositoryName;
    }

    /**
     * sets the URL of the repository to the specified value
     * @param repoURL The URL value to be assigned to the repository
     */
    public void setRepositoryURL(String repoURL)
    {
        repositoryURL = repoURL.trim();
    }

    /**
     * Gets the URL of the repository
     * @return returns URL of the repository
     */
    public String getRepositoryURL()
    {
        return repositoryURL;
    }

    /**
     * This method is used to set a user
     * @param user The user object to be set
     */
    public void setUser(User user)
    {
        this.user = user;
    }
    

    /**
     * The method is used to add a repository
     * @return returns the status of the add operation
     */
    public String addRepository()
    {
         Provider pr = new Provider();
         try
            {
                ProviderService providerService = new DefaultProviderService();

                Provider repositorySameName = providerService.getProviderByName(repositoryName);
                Provider repositorySameURL = providerService.getProviderByURL(repositoryURL);
                if(repositorySameName!=null)
                {

                   this.addFieldError("addRepositoryError", "Repository with name '"+repositoryName+"' already exists");
                   return INPUT;
                }
                else if(repositorySameURL!=null)
                {
                   this.addFieldError("addRepositoryError", "Repository with URL '"+repositoryURL+"' already exists");
                   return INPUT;
                }
                else
                {

                    java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());

                    pr.setName(getRepositoryName());
                    pr.setCreatedAt(new Date());
                    pr.setUpdatedAt( new Timestamp(new Date().getTime()));
                    pr.setLastValidationDate(new Date());
                    pr.setOaiProviderUrl(getRepositoryURL());
                    pr.setLogFileName("C:/logs/"+getRepositoryName()+".txt");

                    pr.setUser(user);

                    providerService.insertProvider(pr);

                    ValidateRepository vr = new ValidateRepository();
                    vr.validate(pr.getId());

                }
            }
            catch(Exception e)
            {
                log.debug(e);
                e.printStackTrace();
                this.addFieldError("addRepositoryError", "Error : There was a problem identified in the repository added");
                System.out.println("The error Field has "+this.hasFieldErrors());
               
            }

          setRepositoryId(pr.getId());
          return SUCCESS;
    }
}
