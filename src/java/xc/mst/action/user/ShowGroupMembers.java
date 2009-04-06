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
import xc.mst.bo.user.Group;
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
    private String groupId;
    private String columnSorted = "UserName";
    private boolean isAscendingOrder = true;
    private List membershipList;
    private GroupService groupService = new DefaultGroupService();
    private UserService userService = new DefaultUserService();

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupId()
    {
        return this.groupId;
    }

    public void setMembershipList(List membershipList)
    {
        this.membershipList = membershipList;
    }

    public List getMembershipList()
    {
        return this.membershipList;
    }

    /**
     * sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        this.isAscendingOrder = isAscendingOrder;
    }

    /**
     * sgets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

    /**
     * sets the name of the column on which the sorting should be performed
     * @param columnSorted name of the column
     */
    public void setColumnSorted(String columnSorted)
    {
        this.columnSorted = columnSorted;
    }

    /**
     * returns the name of the column on which sorting should be performed
     * @return column name
     */
    public String getColumnSorted()
    {
        return this.columnSorted;
    }

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
                    System.out.println("There is no group with the ID of "+groupId);
                    this.addFieldError("showGroupsMembersError", "ERROR : The column "+columnSorted+" does not exist");
                    return INPUT;
                }
            }
            else
            {
                System.out.println("There is no group with the ID of "+groupId);
                this.addFieldError("showGroupsMembersError", "ERROR : There is no group with the group ID specified");
                return INPUT;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("showGroupMembersError", "ERROR : There was a problem displaying the page");
            return INPUT;
        }
    }
}
