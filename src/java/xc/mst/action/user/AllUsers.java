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
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.user.UserDAO;
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
    private String columnSorted = "UserName";

    /** boolean value which determines of the rows have to be sorted in ascending order */
    private boolean isAscendingOrder=true;
    
    /** creates a service object for users  */
    private UserService userService = new DefaultUserService();

    /**stores the list of users */
    private List<User> userList;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Error type */
	private String errorType; 
	

    /**
     * Returns the list of users
     *
     * @return user list
     */
    public List<User> getUserList()
    {
        return userList;
    }

    /**
     * Sets the list of users
     *
     * @param userList user list
     */
    public void setUserList(List<User> userList)
    {
        this.userList = userList;
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
     * Overrides default implementation to list all the users in the system.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            if(columnSorted.equalsIgnoreCase("UserName")||(columnSorted.equalsIgnoreCase("LastLogin"))||(columnSorted.equalsIgnoreCase("FirstName"))||(columnSorted.equalsIgnoreCase("LastName")))
            {
                if(columnSorted.equalsIgnoreCase("UserName"))
                {
                    setUserList(userService.getAllUsersSorted(isAscendingOrder, UserDAO.COL_USERNAME));
                }
                else if(columnSorted.equalsIgnoreCase("LastLogin"))
                {
                    setUserList(userService.getAllUsersSorted(isAscendingOrder,UserDAO.COL_LAST_LOGIN));
                }
                else if(columnSorted.equalsIgnoreCase("FirstName"))
                {
                    setUserList(userService.getAllUsersSorted(isAscendingOrder,UserDAO.COL_FIRST_NAME));
                }
                else
                {
                    setUserList(userService.getAllUsersSorted(isAscendingOrder,UserDAO.COL_LAST_NAME));
                }
                setIsAscendingOrder(isAscendingOrder);
                setColumnSorted(columnSorted);
            }
            else
            {
                this.addFieldError("generalLogError", "ERROR : The specified column does not exist");
            }
            
        }
        catch(Exception e)
        {
            log.debug(e);
            this.addFieldError("allUsersError", "Error in displaying the users List");
            errorType = "error";
        }
        return SUCCESS;

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
