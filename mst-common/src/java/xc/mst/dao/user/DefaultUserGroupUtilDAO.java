/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.user.User;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.user.UserService;

/**
 * MySQL implementation of the utility class for manipulating the groups assigned to a user
 *
 * @author Eric Osisek
 */
public class DefaultUserGroupUtilDAO extends UserGroupUtilDAO
{
    /**
     * A PreparedStatement to add a group for a user into the database
     */
    private static PreparedStatement psInsert = null;

    /**
     * A PreparedStatement to remove a group from a user into the database
     */
    private static PreparedStatement psDelete = null;

    /**
     * A PreparedStatement to get the group IDs of the groups that a user belongs to
     */
    private static PreparedStatement psGetGroupsForUser = null;

    /**
     * A PreparedStatement to remove all groups for a user
     */
    private static PreparedStatement psDeleteGroupForUser = null;

    /**
     * A PreparedStatement to list all users for a group
     */
    private static PreparedStatement psGetUsersForGroup = null;

    /**
     * A PreparedStatement to get number of users in a group
     */
    private static PreparedStatement psGetUserCountForGroup = null;

    /**
     * A PreparedStatement to get all users in a group
     */
    private static PreparedStatement psGetUsersForGroupSorted = null;

    /**
     * Lock to prevent concurrent access of the prepared statement to add a group for a user
     */
    private static Object psInsertLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to remove a group from a user
     */
    private static Object psDeleteLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to get all groups for a user
     */
    private static Object psGetGroupsForUserLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to remove all groups from a user
     */
    private static Object psDeleteGroupsForUserLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to list all users associated with a group
     */
    private static Object psGetUsersForGroupLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to get number of users associated with a group
     */
    private static Object psGetUserCountForGroupLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to list all users associated with a group sorted
     */
    private static Object psGetUsersForGroupSortedLock = new Object();

    @Override
    public boolean insert(int userId, int groupId)
    {
        synchronized(psInsertLock)
        {
            if(log.isDebugEnabled())
                log.debug("Adding the group with ID " + groupId + " for the user with ID " + userId + ".");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to add a user to a group is not defined, create it
                if(psInsert == null || dbConnectionManager.isClosed(psInsert))
                {
                    // SQL to insert the new row
                    String insertSql = "INSERT INTO " + USERS_TO_GROUPS_TABLE_NAME +
                                                        " (" + COL_USER_ID + ", " +
                                                               COL_GROUP_ID + ") " +
                                       "VALUES (?, ?)";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"add group for a user\" PreparedStatement from the SQL " + insertSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
                } // end if (insert prepared statement is null)

                // Set the parameters on the insert statement
                psInsert.setInt(1, userId);
                psInsert.setInt(2, groupId);

                // Execute the insert statement and return the result
                return dbConnectionManager.executeUpdate(psInsert) > 0;
            } // end try (insert the group)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while adding the group with ID " + groupId + " for the user with ID " + userId + ".", e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return insert(userId, groupId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method insert(int, int)

    @Override
    public boolean delete(int userId, int groupId)
    {
        synchronized(psDeleteLock)
        {
            if(log.isDebugEnabled())
                log.debug("Removing the group with ID " + groupId + " from the user with ID " + userId + ".");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to remove a user from a group is not defined, create it
                if(psDelete == null || dbConnectionManager.isClosed(psDelete))
                {
                    // SQL to insert the new row
                    String deleteSql = "DELETE FROM " + USERS_TO_GROUPS_TABLE_NAME + " " +
                                       "WHERE " + COL_USER_ID + "=? " +
                                       "AND " + COL_GROUP_ID + "=? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"remove group from a user\" PreparedStatement from the SQL " + deleteSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
                } // end if (insert prepared statement is null)

                // Set the parameters on the insert statement
                psDelete.setInt(1, userId);
                psDelete.setInt(2, groupId);

                // Execute the delete statement and return the result
                return dbConnectionManager.executeUpdate(psDelete) > 0;
            } // end try (delete the group)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while removing the group with ID " + groupId + " from the user with ID " + userId + ".", e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return delete(userId, groupId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method delete(int, int)

    @Override
    public List<Integer> getGroupsForUser(int userId)
    {
        synchronized(psGetGroupsForUserLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the groups for the user with user ID " + userId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of groups for the user with the passed ID
            List<Integer> groupIds = new ArrayList<Integer>();

            try
            {
                // If the PreparedStatement to get groups by user ID wasn't defined, create it
                if(psGetGroupsForUser == null || dbConnectionManager.isClosed(psGetGroupsForUser))
                {
                    // SQL to get the rows
                    String selectSql = "SELECT " + COL_GROUP_ID + " " +
                                       "FROM " + USERS_TO_GROUPS_TABLE_NAME + " " +
                                       "WHERE " + COL_USER_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get groups for user\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetGroupsForUser = dbConnectionManager.prepareStatement(selectSql, psGetGroupsForUser);
                }

                // Set the parameters on the select statement
                psGetGroupsForUser.setInt(1, userId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetGroupsForUser);

                // For each result returned, add the group ID object to the list with the returned data
                while(results.next())
                    groupIds.add(new Integer(results.getInt(1)));

                if(log.isDebugEnabled())
                    log.debug("Found " + groupIds.size() + " group IDs that the user with user ID " + userId + " belongs to.");

                return groupIds;
            } // end try (get and return the group IDs which the user belongs to)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the groups for the user with user ID " + userId, e);

                return groupIds;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getGroupsForUser(userId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        } // end synchronized
    } // end method getGroupsForUser(int)

    @Override
    public boolean deleteGroupsForUser(int userId)
    {
        synchronized(psDeleteGroupsForUserLock)
        {
            if(log.isDebugEnabled())
                log.debug("Removing the groups for the user with user ID " + userId);

            try
            {
                // If the PreparedStatement to delete groups by user ID wasn't defined, create it
                if(psDeleteGroupForUser == null || dbConnectionManager.isClosed(psDeleteGroupForUser))
                {
                    // SQL to get the rows
                    String selectSql = "DELETE FROM " + USERS_TO_GROUPS_TABLE_NAME + " " +
                                       "WHERE " + COL_USER_ID + "=? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"remove groups for user\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDeleteGroupForUser = dbConnectionManager.prepareStatement(selectSql, psDeleteGroupForUser);
                }

                // Set the parameters on the select statement
                psDeleteGroupForUser.setInt(1, userId);

                // Get the result of the SELECT statement

                // Execute the insert statement and return the result
                return dbConnectionManager.executeUpdate(psDeleteGroupForUser) > 0;
            } // end try (remove all groups from the user)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while deleting the groups for the user with user ID " + userId, e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return deleteGroupsForUser(userId);
            }
        } // end synchronized
    } // end method deleteGroupsForUser(int)

    /**
     * returns a list of all the users associated with a group
     *
     * @param groupId group ID
     * @return Lis of users
     * @throws DatabaseConfigException
     */
    public List<User> getUsersForGroup(int groupId) throws DatabaseConfigException
    {
        synchronized(psGetUsersForGroupLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the users for the group with group ID " + groupId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of users for the group with the passed ID
            List<User> users = new ArrayList<User>();

            try
            {
                // If the PreparedStatement to get users by group ID wasn't defined, create it
                if(psGetUsersForGroup == null || dbConnectionManager.isClosed(psGetUsersForGroup))
                {
                    // SQL to get the rows
                    String selectSql = "SELECT " + COL_USER_ID + " " +
                                       "FROM " + USERS_TO_GROUPS_TABLE_NAME + " " +
                                       "WHERE " + COL_GROUP_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get users for group\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetUsersForGroup = dbConnectionManager.prepareStatement(selectSql, psGetUsersForGroup);
                }

                // Set the parameters on the select statement
                psGetUsersForGroup.setInt(1, groupId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetUsersForGroup);

                UserService userService = (UserService)config.getBean("UserService");
                // For each result returned, add the group ID object to the list with the returned data
                while(results.next())
                    users.add(userService.getUserById(results.getInt(1)));

                if(log.isDebugEnabled())
                    log.debug("Found " + users.size() + " user IDs that the group with group ID " + groupId + " contains.");

                return users;
            } // end try (get and return the group IDs which the user belongs to)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the users for the group with group ID " + groupId, e);

                return users;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getUsersForGroup(groupId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        }
    }

    /**
     * Returns the number of users who are members of the groups
     *
     * @param groupId ID of the group
     * @return member count
     */
    public int getUserCountForGroup(int groupId)
    {
         synchronized(psGetUserCountForGroupLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the users for the group with group ID " + groupId);

            // The ResultSet from the SQL query
            ResultSet results = null;


            try
            {
                // If the PreparedStatement to get users by group ID wasn't defined, create it
                if(psGetUserCountForGroup == null || dbConnectionManager.isClosed(psGetUserCountForGroup))
                {
                    // SQL to get the rows
                    String selectSql = "SELECT COUNT(" + COL_USER_ID + ") " +
                                       "FROM " + USERS_TO_GROUPS_TABLE_NAME + " " +
                                       "WHERE " + COL_GROUP_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get users for group\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetUserCountForGroup = dbConnectionManager.prepareStatement(selectSql, psGetUserCountForGroup);
                }

                // Set the parameters on the select statement
                psGetUserCountForGroup.setInt(1, groupId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetUserCountForGroup);

                // Return the result
                if(results.next())
                    return results.getInt(1);

                if(log.isDebugEnabled())
                    log.debug("Did not find count of " + " user IDs that the group with group ID " + groupId + " contains.");

                return 0;

            } // end try (get and return the group IDs which the user belongs to)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the users for the group with group ID " + groupId, e);

                return 0;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getUserCountForGroup(groupId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        }
    }

    /**
     * Get users in a specified group sorted by given sort column
     *
     * @param groupId Id of group
     * @param sort ascending or descending
     * @param columnSorted Column name to be sorted
     *
     * @return Users in a group
     */
    public List<User> getUsersForGroupSorted(int groupId,boolean sort,String columnSorted) throws DatabaseConfigException
    {
        synchronized(psGetUsersForGroupSortedLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the users for the group with group ID " + groupId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of users for the group with the passed ID
            List<User> users = new ArrayList<User>();

            try
            {

                    // SQL to get the rows
                    String selectSql = "SELECT " + USERS_TABLE_NAME + "." + COL_USER_ID + ", " + USERS_TABLE_NAME + "." +COL_USERNAME + ", " + USERS_TABLE_NAME + "." + COL_FIRST_NAME + ", " + USERS_TABLE_NAME + "." + COL_LAST_NAME + " " +
                                       "FROM " + USERS_TO_GROUPS_TABLE_NAME + ", " + USERS_TABLE_NAME + " " +
                                       "WHERE " + USERS_TABLE_NAME + "." + COL_USER_ID + "=" + USERS_TO_GROUPS_TABLE_NAME + "." + COL_USER_ID + " AND " + USERS_TO_GROUPS_TABLE_NAME + "."+ COL_GROUP_ID + "=?" + " ORDER BY " + USERS_TABLE_NAME + "." + columnSorted + (sort ? " ASC" : " DESC");

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get users for group\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetUsersForGroupSorted = dbConnectionManager.prepareStatement(selectSql, null);

                // Set the parameters on the select statement
                psGetUsersForGroupSorted.setInt(1, groupId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetUsersForGroupSorted);

                UserService userService = (UserService)config.getBean("UserService");
                // For each result returned, add the group ID object to the list with the returned data
                while(results.next())
                    users.add(userService.getUserById(results.getInt(1)));

                if(log.isDebugEnabled())
                    log.debug("Found " + users.size() + " user IDs that the group with group ID " + groupId + " contains.");

                return users;
            } // end try (get and return the group IDs which the user belongs to)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the users for the group with group ID " + groupId, e);

                return users;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getUsersForGroupSorted(groupId, sort, columnSorted);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        }

    }
} // end class DefaultUserGroupUtilDAO
