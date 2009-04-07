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
    private String userId;

    /** The ID of the group form which th euser should be removed */
    private String groupId;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * sets the group ID
     * @param groupId group ID
     */
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    /**
     * returns the ID of the group
     * @return group ID
     */
    public String getGroupId()
    {
        return this.groupId;
    }

    /**
     * sets the user ID
     * @param userId user ID
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * returns the ID of the user
     * @return user ID
     */
    public String getUserId()
    {
        return this.userId;
    }

     /**
     * Overrides default implementation to remove a user-group association.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            UserService userService = new DefaultUserService();
            GroupService groupService = new DefaultGroupService();
            User user = userService.getUserById(Integer.parseInt(userId));
            user.removeGroup(groupService.getGroupById(Integer.parseInt(groupId)));
            userService.updateUser(user);
            setGroupId(groupId);
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("removeMemberError", "ERROR : There was a problem removing the member from the group");
            return INPUT;
        }
    }
}
