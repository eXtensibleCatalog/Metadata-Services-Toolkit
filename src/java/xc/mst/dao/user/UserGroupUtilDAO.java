/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.user;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the groups assigned to a user
 *
 * @author Eric Osisek
 */
public abstract class UserGroupUtilDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The connection to the database
	 */
	protected final static Connection dbConnection = MySqlConnectionManager.getDbConnection();

	/**
	 * The name of the user to groups database table
	 */
	public final static String USERS_TO_GROUPS_TABLE_NAME = "users_to_groups";

    /**
	 * The name of the users database table
	 */
	public final static String USERS_TABLE_NAME = "users";

	/**
	 * The name of the user ID column
	 */
	public final static String COL_USER_ID = "user_id";

	/**
	 * The name of the group ID column
	 */
	public final static String COL_GROUP_ID = "group_id";

    /**
	 * The name of the username column
	 */
	public final static String COL_USERNAME = "username";

	/**
	 * The name of the first name column
	 */
	public final static String COL_FIRST_NAME = "first_name";

	/**
	 * The name of the last name column
	 */
	public final static String COL_LAST_NAME = "last_name";

	/**
	 * Inserts a row in the database assigning a user to a group.
	 *
	 * @param userId The user to assign to the group
	 * @param groupId The group to assign the user to
	 * @return True on success, false on failure
	 */
	public abstract boolean insert(int userId, int groupId);

	/**
	 * Deletes the row in the database assigning the user to the group.
	 *
	 * @param userId The user to remove from the group
	 * @param groupId The group to remove the user from
	 * @return True on success, false on failure
	 */
	public abstract boolean delete(int userId, int groupId);

	/**
	 * Gets all groups to which a user belongs
	 *
	 * @param userId The ID of the user whose groups should be returned
	 * @return A list of group IDs for the groups the user belongs to
	 */
	public abstract List<Integer> getGroupsForUser(int userId);

	/**
	 * Deletes all groups assignments for a user
	 *
	 * @param userId The ID of the user whose groups should be removed
	 * @return True on success, false on failure
	 */
	public abstract boolean deleteGroupsForUser(int userId);

    /**
     * returns a list of all the users associated with a group
     *
     * @param groupId group ID
     * @return List of users
     */
    public abstract List<User> getUsersForGroup(int groupId);

    /**
     * Returns the number of users who are members of the groups
     *
     * @param groupId ID of the group
     * @return member count
     */
    public abstract int getUserCountForGroup(int groupId);

    /**
     * returns list of all users associated with a group
     * @param groupId group ID
     * @param sort determines if the rows are to be sorted in ascending or descending order
     * @param columnSorted the column on which the rows are to be sorted
     * @return list of users
     */
    public abstract List<User> getUsersForGroupSorted(int groupId,boolean sort,String columnSorted);
    
} // end class UserGroupUtil

