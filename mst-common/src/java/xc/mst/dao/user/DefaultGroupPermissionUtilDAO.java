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

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.DBConnectionResetException;

/**
 * MySQL implementation of the utility class for manipulating the permissions assigned to a group
 *
 * @author Eric Osisek
 */
public class DefaultGroupPermissionUtilDAO extends GroupPermissionUtilDAO
{
    /**
     * A reference to the logger for this class
     */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * A PreparedStatement to add a permission for a group into the database
     */
    private static PreparedStatement psInsert = null;

    /**
     * A PreparedStatement to remove a permission from a group into the database
     */
    private static PreparedStatement psDelete = null;

    /**
     * A PreparedStatement to get the top level tab IDs of the tabs that a group has permission to access
     */
    private static PreparedStatement psGetPermissionsForGroup = null;

    /**
     * A PreparedStatement to remove all permissions for a group
     */
    private static PreparedStatement psDeletePermissionsForGroup = null;

    /**
     * Lock to prevent concurrent access of the prepared statement to add a permission for a group
     */
    private static Object psInsertLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to remove a permission from a group
     */
    private static Object psDeleteLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to get all permissions for a group
     */
    private static Object psGetPermissionsForGroupLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to remove all permissions from a group
     */
    private static Object psDeletePermissionsForGroupLock = new Object();

    @Override
    public boolean insert(int groupId, int topLevelTabId)
    {
        synchronized(psInsertLock)
        {
            if(log.isDebugEnabled())
                log.debug("Adding permission for the group with ID " + groupId + " to access the top level tab with ID " + topLevelTabId);

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to insert a group to Top Level Tab is not defined, create it
                if(psInsert == null || dbConnectionManager.isClosed(psInsert))
                {
                    // SQL to insert the new row
                    String insertSql = "INSERT INTO " + GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME +
                                                        " (" + COL_GROUP_ID + ", " +
                                                               COL_TOP_LEVEL_TAB_ID + ") " +
                                       "VALUES (?, ?)";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"add permission for a group\" PreparedStatement from the SQL " + insertSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
                } // end if (insert prepared statement is null)

                // Set the parameters on the insert statement
                psInsert.setInt(1, groupId);
                psInsert.setInt(2, topLevelTabId);

                // Execute the insert statement and return the result
                return dbConnectionManager.executeUpdate(psInsert) > 0;
            } // end try (insert the permission)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while adding permission for the group with ID " + groupId + "to access the top Level tab with ID " + topLevelTabId + ".", e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return insert(groupId, topLevelTabId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method insert(int, int)

    @Override
    public boolean delete(int groupId, int topLevelTabId)
    {
        synchronized(psDeleteLock)
        {
            if(log.isDebugEnabled())
                log.debug("Removing permission to access the top level tab with ID " + topLevelTabId + "from the group with ID " + groupId + ".");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to insert a group to Top Level Tab is not defined, create it
                if(psDelete == null || dbConnectionManager.isClosed(psDelete))
                {
                    // SQL to insert the new row
                    String deleteSql = "DELETE FROM " + GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME +
                                       "WHERE " + COL_GROUP_ID + "=? " +
                                       "AND " + COL_TOP_LEVEL_TAB_ID + "=? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"remove permission from a group\" PreparedStatement from the SQL " + deleteSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
                } // end if (insert prepared statement is null)

                // Set the parameters on the insert statement
                psDelete.setInt(1, groupId);
                psDelete.setInt(2, topLevelTabId);

                // Execute the delete statement and return the result
                return dbConnectionManager.executeUpdate(psDelete) > 0;
            } // end try (delete the permission)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while removing permission to access the top level tab with ID " + topLevelTabId + "from the group with ID " + groupId + ".");

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return delete(groupId, topLevelTabId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method delete(int, int)

    @Override
    public List<Integer> getPermissionsForGroup(int groupId)
    {
        synchronized(psGetPermissionsForGroupLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the permissions for the group with group ID " + groupId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of permissions for the requested group
            List<Integer> topLevelTabIds = new ArrayList<Integer>();

            try
            {
                // If the PreparedStatement to get permissions by group ID wasn't defined, create it
                if(psGetPermissionsForGroup == null || dbConnectionManager.isClosed(psGetPermissionsForGroup))
                {
                    // SQL to get the rows
                    String selectSql = "SELECT " + COL_TOP_LEVEL_TAB_ID + " " +
                                       "FROM " + GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME + " " +
                                       "WHERE " + COL_GROUP_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get permissions for group\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetPermissionsForGroup = dbConnectionManager.prepareStatement(selectSql, psGetPermissionsForGroup);
                }

                // Set the parameters on the select statement
                psGetPermissionsForGroup.setInt(1, groupId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetPermissionsForGroup);

                // For each result returned, add a group to Top Level Tab object to the list with the returned data
                while(results.next())
                    topLevelTabIds.add(new Integer(results.getInt(1)));

                if(log.isDebugEnabled())
                    log.debug("Found " + topLevelTabIds.size() + " top level tab IDs that the group with group ID " + groupId + " has permission to access.");

                return topLevelTabIds;
            } // end try (get and return the top level tab IDs which the group has permission to access)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the permissions for the group with group ID " + groupId, e);

                return topLevelTabIds;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getPermissionsForGroup(groupId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        } // end synchronized
    } // end method getPermissionsForGroup(int)

    @Override
    public boolean deletePermissionsForGroup(int groupId)
    {
        synchronized(psDeletePermissionsForGroupLock)
        {
            if(log.isDebugEnabled())
                log.debug("Removing the permissions for the group with group ID " + groupId);

            try
            {
                // If the PreparedStatement to delete permissions by group ID wasn't defined, create it
                if(psDeletePermissionsForGroup == null || dbConnectionManager.isClosed(psDeletePermissionsForGroup))
                {
                    // SQL to get the rows
                    String selectSql = "DELETE FROM " + GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME + " " +
                                       "WHERE " + COL_GROUP_ID + "=? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"remove permissions for group\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDeletePermissionsForGroup = dbConnectionManager.prepareStatement(selectSql, psDeletePermissionsForGroup);
                }

                // Set the parameters on the select statement
                psDeletePermissionsForGroup.setInt(1, groupId);

                // Get the result of the SELECT statement

                // Execute the insert statement and return the result
                return dbConnectionManager.executeUpdate(psDeletePermissionsForGroup) > 0;
            } // end try (remove all permissions from the user
            catch(SQLException e)
            {
                log.error("A SQLException occurred while deleting the permissions for the group with group ID " + groupId, e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return deletePermissionsForGroup(groupId);
            }
        } // end synchronized
    } // end method deletePermissionsForGroup(int)
} // end class DefaultGroupPerissionUtil
