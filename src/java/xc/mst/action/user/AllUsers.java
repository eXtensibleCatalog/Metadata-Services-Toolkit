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
import java.util.*;
import org.apache.log4j.Logger;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.UserService;

/**
 *  This action method is user to display all the users in the system
 *
 * @author Tejaswi Haramurali
 */
public class AllUsers extends ActionSupport
{
    /** The column on which the rows are to be sorted */
    private String columnSorted;

    /** boolean value which determines of the rows have to be sorted in ascending order */
    private boolean isAscendingOrder;
    
    /** creates a service object for users  */
    private UserService userService = new DefaultUserService();

    /** creates a service object for groups */
    private GroupService groupService = new DefaultGroupService();

    /**stores the list of users */
    private List<User> userList;

    /**stores the list of groups in the system */
    private List<Group> groupList;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * returns the list of users
     * @return user list
     */
    public List<User> getUserList()
    {
        return userList;
    }

    /**
     * sets the list of users
     * @param userList user list
     */
    public void setUserList(List<User> userList)
    {
        this.userList = userList;
    }

    /**
     * returns the list of all groups in the system
     * @return list of groups
     */
    public List<Group> getGroupList()
    {
        return groupList;
    }

    /**
     * sets the list of all groups in the system
     * @param groupList group list
     */
    public void setGroupList(List<Group> groupList)
    {
        this.groupList = groupList;
    }
     /**
     * sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        System.out.println("Setting ascending order to "+isAscendingOrder);
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
        System.out.println("Setting column sorted as "+columnSorted);
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
    /**
     * Overrides default implementation to list all the users in the system.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            setGroupList(groupService.getAllGroups());
            setUserList(userService.getAllUsersSorted(isAscendingOrder, columnSorted));
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("allUsersError", "Error in displaying the users List");
            return SUCCESS;
        }
    }
}
