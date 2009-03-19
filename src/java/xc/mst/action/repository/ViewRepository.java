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
import xc.mst.action.UserAware;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.repository.DefaultProviderService;

/**
 * This method is used to View the properties of a Repository in the system
 *
 * @author Tejaswi Haramurali
 */
public class ViewRepository extends ActionSupport implements UserAware
{
      static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

      /** The ID of the repository to be viewed */
      private int repositoryId;

      /**The User who is logged in */
      private User user;

      /**The repository to be viewed */
      private Provider provider;

      /**Boolean value that denotes success or failure */
      private String listSets;

      /**Boolean value that denotes success or failure */
      private String listFormats;

      /**Boolean value that denotes success or failure */
      private String Identify;

      /**
       * returns the ID of the repository to be viewed
       * @return The ID of the repository
       */
      public int getRepositoryId()
      {
          return repositoryId;
      }

      /**
       * sets the ID of the Repository to be viewed
       * @param repoId The ID of the repository to be viewed
       */
      public void setRepositoryId(String repoId)
      {

          this.repositoryId = Integer.parseInt(repoId);
      }

      public ViewRepository()
      {
          repositoryId = 0;
      }

      /**
       * sets the User who is currently logged in
       * @param user The User who is currently logged in
       */
      public void setUser(User user)
      {
          this.user = user;
      }

      /**
       * gets the User who is currently logged in
       * @return returns the User object
       */
      public User getUser()
      {
          return user;
      }

      /**
       * sets the Repository Object to be viewed
       * @param provider The Repository object
       */
      public void setProvider(Provider provider)
      {
          this.provider = provider;
      }

      /**
       * Returns the repository object to be viewed
       * @return retuns the repository object
       */
      public Provider getProvider()
      {
          return provider;
      }

      /**
       * A boolean value that is associated with success/failure of Validation of a repository
       * @param listSets
       */
      public void setListSets(String listSets)
      {
          this.listSets = listSets;
      }


      /**
       * return list sets boolean value
       *
       * @return listSets boolean value
       */
      public String getListSets()
      {
          return listSets;
      }

       /**
       * A boolean value that is associated with success/failure of Validation of a repository
       * @param listSets
       */
      public void setListFormats(String listFormats)
      {
          this.listFormats = listFormats;
      }

      /**
       * returns the boolean value for listFormats
       * @return listFormats boolean value
       */
      public String getListFormats()
      {
          return listFormats;
      }

       /**
       * A boolean value that is associated with success/failure of Validation of a repository
       * @param listSets
       */
      public void setIdentify(String Identify)
      {
          this.Identify = Identify;
      }

      /**
       * returns the boolean value for identify
       * @return identify boolean value
       */
      public String getIdentify()
      {
          return Identify;
      }
     

     /**
     * Overrides default implementation to view the details of a repository.
     * @return {@link #SUCCESS}
     */
      @Override
      public String execute()
      {
          try
          {

                provider = new DefaultProviderService().getProviderById(repositoryId);
                user = provider.getUser();
                setUser(user);
                setProvider(provider);
                boolean boollistsets = provider.getListSets();
                boolean boolidentify = provider.getIdentify();
                boolean boollistformats = provider.getListFormats();
                listSets = "Fail";
                listFormats = "Fail";
                Identify = "Fail";

                if (boollistsets == true) {
                    listSets = "Success";
                }

                if (boolidentify == true) {
                    Identify = "Success";
                }
                if (boollistformats == true) {
                    listFormats = "Success";
                }

                return SUCCESS;
          }
          catch(Exception e)
          {
              log.debug(e);
              e.printStackTrace();
              this.addFieldError("allRepositoryError", "Repository details cannot be displayed");
              return SUCCESS;
          }


      }

      /**
       * This method validates a Repository/Provider
       * 
       * @return returns the status of the operation and re-directs accordingly
       */
      public String ValidateRepository()
      {
          try
          {
                ValidateRepository vr = new ValidateRepository();
                vr.validate(getRepositoryId());
                this.addFieldError("viewRepositoryError", "SUCCESSFUL REVALIDATION (The format for this alert should be changed as per the new design)");               
                return SUCCESS;
          }
          catch(Exception e)
          {
              log.debug(e);
              e.printStackTrace();
              this.addFieldError("viewRepositoryError", "ERROR : Revalidation unsuccessful");
              return SUCCESS;
          }
      }
}
