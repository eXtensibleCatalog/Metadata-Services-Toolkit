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
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.user.UserDAO;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.UserService;

/**
 * Displays all the members of a particular group of users
 *
 * @author Tejaswi Haramurali
 */
public class ShowGroupMembers extends ActionSupport
{
    /** The group whose members are to be displayed */
    private String groupId;

    /** The column on which the rows are sorted*/
    private String columnSorted = "UserName";

    /** determines whether the rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder = true;

    /** The list of users who are members of the specified group */
    private List<User> membershipList;

    /** The group service object */
    private GroupService groupService = new DefaultGroupService();

    /** The service object for users */
    private UserService userService = new DefaultUserService();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Sets the group ID
     *
     * @param groupId group ID
     */
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    /**
     * Returns the group ID
     *
     * @return group ID
     */
    public String getGroupId()
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
            Group tempGroup = groupService.getGroupById(Integer.parseInt(groupId));
            if(tempGroup!=null)
            {
                if(columnSorted.equalsIgnoreCase("UserName")||columnSorted.equalsIgnoreCase("FirstName")||columnSorted.equalsIgnoreCase("LastName"))
                {
                    if(columnSorted.equalsIgnoreCase("UserName"))
                    {
                         membershipList = userService.getUsersForGroupSorted(tempGroup.getId(),isAscendingOrder,UserDAO.COL_USERNAME);
                    }
                    else if(columnSorted.equalsIgnoreCase("FirstName"))
                    {
                         membershipList = userService.getUsersForGroupSorted(tempGroup.getId(),isAscendingOrder,UserDAO.COL_FIRST_NAME);
                    }
                    else
                    {
                         membershipList = userService.getUsersForGroupSorted(tempGroup.getId(),isAscendingOrder,UserDAO.COL_LAST_NAME);
                    }
                   
                    setMembershipList(membershipList);
                    setGroupId(this.groupId);
                    System.out.println("Setting group ID to "+groupId);
                    System.out.println("Returning success");
                    return SUCCESS;
                }
                else
                {
                    this.addFieldError("showGroupsMembersError", "ERROR : The column "+columnSorted+" does not exist");
                    return INPUT;
                }
            }
            else
            {
                
                this.addFieldError("showGroupsMembersError", "ERROR : There is no group with the group ID specified");
                return INPUT;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("showGroupMembersError", "ERROR : There was a problem displaying the page");
            return INPUT;
        }
    }
}
