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
import xc.mst.manager.user.DefaultUserGroupUtilService;
import xc.mst.manager.user.UserGroupUtilService;

/**
 * Removes the association between a user and a group
 *
 * @author Tejaswi Haramurali
 */
public class RemoveMember extends ActionSupport
{
    private String userId;
    private String groupId;

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupId()
    {
        return this.groupId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return this.userId;
    }

    @Override
    public String execute()
    {
        try
        {
            UserGroupUtilService UGUtilService = new DefaultUserGroupUtilService();
            UGUtilService.deleteUserGroup(Integer.parseInt(userId), Integer.parseInt(groupId));
            setGroupId(groupId);
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("removeMemberError", "ERROR : There was a problem removing the member from the group");
            return INPUT;
        }
    }
}
