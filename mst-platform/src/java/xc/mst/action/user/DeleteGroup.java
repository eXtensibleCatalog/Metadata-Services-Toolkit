
/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * This action method deletes a group of users
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class DeleteGroup extends BaseActionSupport
{
    /** ID of the group to be deleted**/
    private int groupId;

    /** Error type */
    private String errorType;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Overrides default implementation to delete a user.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

            Group tempGroup = getGroupService().getGroupById(groupId);

            boolean flag = true;

            List<User> users = getUserService().getAllUsersSorted(false,"username");
            Iterator<User> userIter = users.iterator();
            while(userIter.hasNext())
            {
                User user = (User)userIter.next();
                if(!user.getGroups().isEmpty())
                {
                    List<Group> groupList = user.getGroups();
                    Iterator<Group> iter = groupList.iterator();
                    while(iter.hasNext())
                    {
                        Group group = (Group)iter.next();
                        if(group.getId()==tempGroup.getId())
                        {
                            flag = false;
                        }
                    }
                }
                if(!flag)
                {
                    break;
                }

            }
            if(flag)
            {
                tempGroup.removeAllPermissions();
                getGroupService().deleteGroup(tempGroup);
            }
            else
            {
                this.addFieldError("allGroupsError", "Users belong to group '"+tempGroup.getName()+"'. So the group cannot be deleted.");
                errorType = "error";
                return INPUT;
            }

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
            this.addFieldError("allGroupsError", "Error occurred while deleting group. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return INPUT;
        }

    }

    /**
     * Returns the error type
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
     * Sets the group ID of the group to be deleted.
     *
     * @param groupId group ID
     */
    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    /**
     * Returns group ID of the group to be deleted
     *
     * @return group ID
     */
    public int getGroupId()
    {
        return groupId;
    }

    /**
     * Returns list of all groups
     *
     * @return list of groups
     */
    public List<Group> getGroupList()
    {
        List<Group> finalList = new ArrayList<Group>();
        try {
            List<Group> tempList = getGroupService().getAllGroups();


            Iterator<Group> iter = tempList.iterator();
            while(iter.hasNext())
            {
                Group group = (Group)iter.next();
                group.setMemberCount(getUserService().getUsersForGroup(group.getId()).size());
                finalList.add(group);
            }
        } catch  (DataException e) {
            log.error("Exception occured while geting group information", e);
        }
            return finalList;
    }

}
