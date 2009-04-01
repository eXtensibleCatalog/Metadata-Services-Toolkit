
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xc.mst.bo.user.Group;
import xc.mst.bo.user.User;
import xc.mst.manager.user.DefaultGroupPermissionUtilService;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupPermissionUtilService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This action method deletes a group of users
 *
 * @author Tejaswi Haramurali
 */
public class DeleteGroup extends ActionSupport
{
    /** ID of the group to be deleted**/
    private int groupId;

    /** creates a service object for groups */
    private GroupService groupService = new DefaultGroupService();

    /** creates a service object for users */
    private UserService userService = new DefaultUserService();

    /**List of all groups */
    private List<Group> groupList;

	/** Error type */
	private String errorType; 
	

    /**
     * sets the group ID of the group to be deleted.
     * @param groupId group ID
     */
    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }
    /**
     * returns group ID of the group to be deleted
     * @return group ID
     */
    public int getGroupId()
    {
        return groupId;
    }

    /**
     * returns list of all groups
     * @return list of groups
     */
    public List<Group> getGroupList()
    {


        List<Group> tempList = groupService.getAllGroups();
        List<Group> finalList = new ArrayList();

        Iterator iter = tempList.iterator();
        while(iter.hasNext())
        {
            Group group = (Group)iter.next();
            group.setMemberCount(userService.getUsersForGroup(group).size());
            finalList.add(group);
        }
        return finalList;
    }

    /**
     * Overrides default implementation to delete a user.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
                        
            Group tempGroup = groupService.getGroupById(groupId);
            GroupPermissionUtilService GPUtilService = new DefaultGroupPermissionUtilService();            
            boolean flag = true;

            List<User> users = userService.getAllUsersSorted(false,"username");
            Iterator userIter = users.iterator();
            while(userIter.hasNext())
            {
                User user = (User)userIter.next();
                if(!user.getGroups().isEmpty())
                {
                    List<Group> groupList = user.getGroups();
                    Iterator iter = groupList.iterator();
                    while(iter.hasNext())
                    {
                        Group group = (Group)iter.next();
                        if(group.getId()==tempGroup.getId())
                        {
                            flag = false;
                        }
                    }
                }
                if(flag==false)
                {
                    break;
                }

            }
            if(flag==true)
            {
                GPUtilService.deletePermissionsForGroup(groupId);
                groupService.deleteGroup(tempGroup);
            }
            else
            {
                this.addFieldError("allGroupsError", "Error : Users belong to group '"+tempGroup.getName()+"'. So the group cannot be deleted.");
                errorType = "error";
                return INPUT;
            }

            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("allGroupsError", "Error : Problem deleting Group");
            errorType = "error";
            return INPUT;
        }

    }
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
