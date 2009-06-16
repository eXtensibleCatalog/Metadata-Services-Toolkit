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
import org.apache.log4j.Logger;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.UserService;

/**
 * Removes the association between a user and a group
 *
 * @author Tejaswi Haramurali
 */
public class RemoveMember extends ActionSupport
{
    /** The ID of the user who has to be removed from the group */
    private int userId;

    /** The ID of the group form which th euser should be removed */
    private int groupId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** User Service Object */
    private UserService userService = new DefaultUserService();

    /** Group Service Object */
    private GroupService groupService = new DefaultGroupService();

    /**Error Type */
    private String errorType;

     /**
     * Overrides default implementation to remove a user-group association.
      * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            
            User user = userService.getUserById(userId);
            user.removeGroup(groupService.getGroupById(groupId));
            userService.updateUser(user);
            setGroupId(groupId);
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("RemoveMemberError","Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return ERROR;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("removeMemberError","Error in removing a member from a group. An email has been sent to the administrator");
            userService.sendEmailErrorReport(userService.MESSAGE,"logs/MST_General_log");
            errorType = "error";
            return ERROR;
        }
    }

     /**
     * Sets the group ID
     *
     * @param groupId group ID
     */
    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    /**
     * Returns the ID of the group
     *
     * @return group ID
     */
    public int getGroupId()
    {
        return this.groupId;
    }

    /**
     * Sets the user ID
     *
     * @param userId user ID
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * Returns the ID of the user
     *
     * @return user ID
     */
    public int getUserId()
    {
        return this.userId;
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
