/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.user.DefaultUserGroupUtilService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.UserGroupUtilService;
import xc.mst.manager.user.UserService;

/**
 * This action method is used to delete a user from the system
 *
 * @author Tejaswi Haramurali
 */
public class DeleteUser extends ActionSupport
{
    /**The ID of the user to be deleted */
    private int userId;

    /** creates service object for users */
    private UserService userService = new DefaultUserService();

    /** creates service object for providers */
    private ProviderService providerService = new DefaultProviderService();

	/** Error type */
	private String errorType;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * sets the ID of the user to be deleted
     * @param userId user ID
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * returns the ID of the user to be deleted
     * @return user ID
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * returns list of users
     * @return
     */
    public List<User> getUserList()
    {
        return userService.getAllUsersSorted(false,"username");
    }

     /**
     * Overrides default implementation to delete a user from the system.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

            User tempUser = userService.getUserById(userId);
            List<Provider> providerList = providerService.getAllProviders();
            Iterator iter = providerList.iterator();
            boolean deleteFlag = true;
            while(iter.hasNext())
            {
                Provider tempProvider = (Provider)iter.next();
                if(tempProvider.getUser().getId()==userId)
                {
                    deleteFlag = false;
                }
            }

            if(deleteFlag==true)
            {
                userService.deleteUser(tempUser);
                UserGroupUtilService UGUtilService = new DefaultUserGroupUtilService();
                UGUtilService.deleteGroupsForUserId(userId);
            }
            else
            {
                this.addFieldError("deleteUserError", "Error: The user '"+tempUser.getUsername()+"' is associated with a Repository and cannot be deleted");
                errorType = "error";
                return INPUT;
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("deleteUserError", "Error: The user was not deleted");
            errorType = "error";
            return INPUT;
        }
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

}
