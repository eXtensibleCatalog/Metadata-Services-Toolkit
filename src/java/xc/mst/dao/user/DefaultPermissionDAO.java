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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.user.Permission;
import xc.mst.constants.Constants;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Default data access object for permissions belonging to a group.  These are taken from the
 * top_level_tab and group_to_top_level_tab tables.  This connects to a MySQL database which
 * is the default database provider used by the MST.
 *
 * @author Eric Osisek
 */
public class DefaultPermissionDAO extends PermissionDAO
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The connection to the database
	 */
	private final static Connection dbConnection = MySqlConnectionManager.getDbConnection();

	/**
	 * Prepared statement to get all possible permissions
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * Prepared statement to get the permissions for a group
	 */
	private static PreparedStatement psGetPermissionsForGroup = null;

    /**
	 * Prepared statement to get the permission for a given ID
	 */
	private static PreparedStatement psGetPermissionById = null;
	
    /**
	 * Prepared statement to get the permission for a given user ID
	 */
	private static PreparedStatement psGetPermissionByUserId = null;

	/**
	 * Lock to prevent concurrent access of the prepared statement to get all possible permissions.
	 */
	private static Object psGetAllLock = new Object();
	
	/**
	 * Lock to prevent concurrent access of the prepared statement to get all permissions for a group
	 */
	private static Object psGetPermissionsForGroupLock = new Object();

    /**
	 * Lock to prevent concurrent access of the prepared statement to get permission for a given ID
	 */
    private static Object psGetPermissionByIdLock = new Object();
    
    /**
	 * Lock to prevent concurrent access of the prepared statement to get permission for a given user ID
	 */
    private static Object psGetPermissionByUserIdLock = new Object();

    @Override
	public List<Permission> getAll()
	{
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all permissions.");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a groups to Top Level Tab by ID wasn't defined, create it
				if(psGetAll == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_TOP_LEVEL_TAB_ID + ", " +
				                                   COL_TAB_NAME + ", " + COL_TAB_ORDER + " " +
				                       "FROM " + TOP_LEVEL_TABS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all permissions\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnection.prepareStatement(selectSql);
				} // end if (PreparedStatement to get permissions for a group is null)

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetAll.executeQuery();

				// A list of the permissions found in the database
				List<Permission> permissions = new ArrayList<Permission>();

				// Loop over the results
				while(results.next())
				{
					// The current Permission
					Permission permission = new Permission();

					// Set the fields on the permission
					permission.setTabId(results.getInt(1));
					permission.setTabName(results.getString(2));
					permission.setTabOrder(results.getInt(3));

					// Add the permission to the list of returned permissions
					permissions.add(permission);
				} // end loop over permissions returned

				if(log.isDebugEnabled())
					log.debug("Found " + permissions.size() + " permissions.");

				return permissions;
			} // end try (get and return the permissions for the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting all possible permissions", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getAll()
    
	@Override
	public List<Permission> getPermissionsForGroup(int groupId)
	{
		synchronized(psGetPermissionsForGroupLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the permissions for the group with ID " + groupId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a groups to Top Level Tab by ID wasn't defined, create it
				if(psGetPermissionsForGroup == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + TOP_LEVEL_TABS_TABLE_NAME + "." + COL_TOP_LEVEL_TAB_ID + ", " +
				                                   COL_TAB_NAME + ", " + COL_TAB_ORDER + " " +
				                       "FROM " + GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME + " " +
				                       "INNER JOIN " + TOP_LEVEL_TABS_TABLE_NAME + " ON " +
				                                 GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME + "." + COL_TOP_LEVEL_TAB_ID + "=" +
				                                 TOP_LEVEL_TABS_TABLE_NAME + "." + COL_TOP_LEVEL_TAB_ID + " " +
				                       "WHERE " + COL_GROUP_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get the permissions for a group\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetPermissionsForGroup = dbConnection.prepareStatement(selectSql);
				} // end if (PreparedStatement to get permissions for a group is null)

				// Set the parameters on the update statement
				psGetPermissionsForGroup.setInt(1, groupId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetPermissionsForGroup.executeQuery();

				// A list of the permissions found in the database
				List<Permission> permissions = new ArrayList<Permission>();

				// Loop over the results
				while(results.next())
				{
					// The current Permission
					Permission permission = new Permission();

					// Set the fields on the permission
					permission.setTabId(results.getInt(1));
					permission.setTabName(results.getString(2));
					permission.setTabOrder(results.getInt(3));

					// Add the permission to the list of returned permissions
					permissions.add(permission);
				} // end loop over permissions returned

				if(log.isDebugEnabled())
					log.debug("Found " + permissions.size() + " permissions for the group with ID " + groupId + ".");

				return permissions;
			} // end try (get and return the permissions for the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the permissions for the group with ID " + groupId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getPermissionsForGroup(int)



    @Override
	public Permission getPermissionById(int permissionId)
	{
		synchronized(psGetPermissionByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the permission with ID " + permissionId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a permission by ID doesn't exist, create it
				if(psGetPermissionById == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_TOP_LEVEL_TAB_ID + ", " +
				                                   COL_TAB_NAME + ", " + COL_TAB_ORDER + " " +
				                       "FROM " + TOP_LEVEL_TABS_TABLE_NAME + " " +
				                       "WHERE " + COL_TOP_LEVEL_TAB_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get the permission for an ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetPermissionById = dbConnection.prepareStatement(selectSql);
				} // end if (PreparedStatement to get permission for an ID is null)

				// Set the parameters on the update statement
				psGetPermissionById.setInt(1, permissionId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetPermissionById.executeQuery();

                // The current Permission
			    Permission permission = new Permission();


				if(results.next())
				{

					// Set the fields on the permission
					permission.setTabId(results.getInt(1));
					permission.setTabName(results.getString(2));
					permission.setTabOrder(results.getInt(3));

				}

				if(log.isDebugEnabled())
					log.debug("Found the permission for the ID " + permissionId + ".");

				return permission;
			} // end try (get and return the permission for the ID)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the permission for the ID " + permissionId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getPermissionById(int)

    @Override
    public List<Permission> getPermissionsForUserByTabOrderAsc(int userId) {
		synchronized(psGetPermissionByUserIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the permission for user with ID " + userId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a permission by user ID doesn't exist, create it
				if(psGetPermissionByUserId == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_TOP_LEVEL_TAB_ID + ", " +
				                                   COL_TAB_NAME + ", " + COL_TAB_ORDER + " " +
		                                   "FROM " + TOP_LEVEL_TABS_TABLE_NAME + " " +
					                       "WHERE " + TOP_LEVEL_TABS_TABLE_NAME + "." + COL_TOP_LEVEL_TAB_ID + " IN " +
					                       			" ( " + " SELECT " + COL_TOP_LEVEL_TAB_ID + " FROM " + GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME + " WHERE " + COL_GROUP_ID + " IN " +
					                       				" ( " + " SELECT " + COL_GROUP_ID + " FROM " + UserGroupUtilDAO.USERS_TO_GROUPS_TABLE_NAME + " WHERE " + UserGroupUtilDAO.COL_USER_ID + " =? " +
					                       				" ) " +
					                       			" ) " + 
					                       	" ORDER BY ASC " + TOP_LEVEL_TABS_TABLE_NAME + "." + COL_TAB_ORDER;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get the permission for an user ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetPermissionByUserId = dbConnection.prepareStatement(selectSql);
				} // end if (PreparedStatement to get permission for an ID is null)

				// Set the parameters on the update statement
				psGetPermissionByUserId.setInt(1, userId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetPermissionByUserId.executeQuery();

                // The user Permissions
			    List<Permission> permissions = new ArrayList<Permission>();


				if(results.next())
				{
					Permission permission = new Permission();

					// Set the fields on the permission
					permission.setTabId(results.getInt(1));
					permission.setTabName(results.getString(2));
					permission.setTabOrder(results.getInt(3));


				}

				if(log.isDebugEnabled())
					log.debug("Found " + permissions.size() + " permissions for the user ID " + userId + ".");

				return permissions;
			} // end try (get and return the permission for the user ID)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the permission for the user ID " + userId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
		
    }

} // end class DefaultPermissionDAO
