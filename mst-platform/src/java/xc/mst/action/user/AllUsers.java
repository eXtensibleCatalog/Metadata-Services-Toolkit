/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.user.UserDAO;

/**
 *  This action method is user to display all the users in the system
 *
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class AllUsers extends BaseActionSupport
{
    /** The column on which the rows are to be sorted */
    private String columnSorted = "UserName";

    /** boolean value which determines of the rows have to be sorted in ascending order */
    private boolean isAscendingOrder=true;

    /**stores the list of users */
    private List<User> userList;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
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
                    setUserList(getUserService().getAllUsersSorted(isAscendingOrder, UserDAO.COL_USERNAME));
                }
                else if(columnSorted.equalsIgnoreCase("LastLogin"))
                {
                    setUserList(getUserService().getAllUsersSorted(isAscendingOrder,UserDAO.COL_LAST_LOGIN));
                }
                else if(columnSorted.equalsIgnoreCase("FirstName"))
                {
                    setUserList(getUserService().getAllUsersSorted(isAscendingOrder,UserDAO.COL_FIRST_NAME));
                }
                else
                {
                    setUserList(getUserService().getAllUsersSorted(isAscendingOrder,UserDAO.COL_LAST_NAME));
                }
               
            }
            else
            {
                setUserList(getUserService().getAllUsersSorted(isAscendingOrder, UserDAO.COL_USERNAME));
            }
            setIsAscendingOrder(isAscendingOrder);
            setColumnSorted(columnSorted);
            
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("allUsersError", "Unable to connect to the database. Database Configuration may be incorrect");
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

}
