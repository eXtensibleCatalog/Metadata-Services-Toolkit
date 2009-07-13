/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.user.DefaultUserGroupUtilService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.UserGroupUtilService;
import xc.mst.manager.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This action method is used to delete a user from the system
 *
 * @author Tejaswi Haramurali
 */
public class DeleteUser extends ActionSupport
{
    /** Serial id */
	private static final long serialVersionUID = 2276027380221702568L;

	/**The ID of the user to be deleted */
    private int userId;

    /** creates service object for users */
    private UserService userService = new DefaultUserService();


	/** Error type */
	private String errorType;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

     /**
     * Overrides default implementation to delete a user from the system.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

            User tempUser = userService.getUserById(userId);

            userService.deleteUser(tempUser);
            UserGroupUtilService UGUtilService = new DefaultUserGroupUtilService();
            UGUtilService.deleteGroupsForUserId(userId);
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("allGroupsError", "Unable to connect to the database. Database configuration may be incorrect.");
            errorType = "error";
            return INPUT;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("allGroupsError", "Error occurred while deleting user. An email has been sent to the administrator.");
            userService.sendEmailErrorReport();
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

    /**
     * Sets the ID of the user to be deleted
     *
     * @param userId user ID
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * Returns the ID of the user to be deleted
     *
     * @return user ID
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * Returns list of users
     *
     * @return
     */
    public List<User> getUserList() throws DatabaseConfigException
    {
        return userService.getAllUsersSorted(false,"username");
    }


}
