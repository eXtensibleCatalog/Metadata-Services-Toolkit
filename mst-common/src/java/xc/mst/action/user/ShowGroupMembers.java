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

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.user.UserDAO;

/**
 * Displays all the members of a particular group of users
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class ShowGroupMembers extends BaseActionSupport
{
    /** The group whose members are to be displayed */
    private int groupId;

    /** The column on which the rows are sorted*/
    private String columnSorted = "UserName";

    /** determines whether the rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder = true;

    /** The list of users who are members of the specified group */
    private List<User> membershipList;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

     /**
     * Overrides default implementation to view the page which displays all the members of a group.
      * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Group tempGroup = getGroupService().getGroupById(groupId);
            if(tempGroup!=null)
            {
                if(columnSorted.equalsIgnoreCase("UserName")||columnSorted.equalsIgnoreCase("FirstName")||columnSorted.equalsIgnoreCase("LastName"))
                {
                    if(columnSorted.equalsIgnoreCase("UserName"))
                    {
                         membershipList = getUserService().getUsersForGroupSorted(tempGroup.getId(),isAscendingOrder,UserDAO.COL_USERNAME);
                    }
                    else if(columnSorted.equalsIgnoreCase("FirstName"))
                    {
                         membershipList = getUserService().getUsersForGroupSorted(tempGroup.getId(),isAscendingOrder,UserDAO.COL_FIRST_NAME);
                    }
                    else
                    {
                         membershipList = getUserService().getUsersForGroupSorted(tempGroup.getId(),isAscendingOrder,UserDAO.COL_LAST_NAME);
                    }
                   
                    setMembershipList(membershipList);
                    setGroupId(this.groupId);
                    return SUCCESS;
                }
                else
                {
                    this.addFieldError("showGroupsMembersError", "The column "+columnSorted+" does not exist");
                    return INPUT;
                }
            }
            else
            {
                
                this.addFieldError("showGroupsMembersError", "There is no group with the group ID specified");
                return INPUT;
            }
        }
        catch(Exception e)
        {
            log.debug("There was a problem displaying members of the group",e);
            this.addFieldError("showGroupMembersError", "There was a problem displaying the page");
            return INPUT;
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
     * Returns the group ID
     *
     * @return group ID
     */
    public int getGroupId()
    {
        return this.groupId;
    }

    /**
     * Sets the members of the group
     *
     * @param membershipList list of users
     */
    public void setMembershipList(List<User> membershipList)
    {
        this.membershipList = membershipList;
    }

    /**
     * Returns a list of users who are members of the group
     *
     * @return user list
     */
    public List<User> getMembershipList()
    {
        return this.membershipList;
    }

    /**
     * Sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        this.isAscendingOrder = isAscendingOrder;
    }

    /**
     * Gets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

    /**
     * Sets the name of the column on which the sorting should be performed
     *
     * @param columnSorted name of the column
     */
    public void setColumnSorted(String columnSorted)
    {
        this.columnSorted = columnSorted;
    }

    /**
     * Returns the name of the column on which sorting should be performed
     *
     * @return column name
     */
    public String getColumnSorted()
    {
        return this.columnSorted;
    }

}
