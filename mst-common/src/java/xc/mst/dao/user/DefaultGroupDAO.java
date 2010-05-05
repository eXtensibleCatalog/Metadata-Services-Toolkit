/**
  * Copyright (c) 2009 University of Rochester
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

import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

public class DefaultGroupDAO extends GroupDAO
{
	/**
	 * A PreparedStatement to get all groups in the database
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * A PreparedStatement to get all groups sorted in the database
	 */
	private static PreparedStatement psGetAllSorted = null;

	/**
	 * A PreparedStatement to get a group from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get a group from the database by its name
	 */
	private static PreparedStatement psGetByName = null;

	/**
	 * A PreparedStatement to insert a group into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a group in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a group from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the get all PreparedStatement
	 */
	private static Object psGetAllLock = new Object();
	
	/**
	 * Lock to synchronize access to the get all sorted PreparedStatement
	 */
	private static Object psGetAllSortedLock = new Object();

	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetByIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by name PreparedStatement
	 */
	private static Object psGetByNameLock = new Object();
	
	/**
	 * Lock to synchronize access to the insert PreparedStatement
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the update PreparedStatement
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the delete PreparedStatement
	 */
	private static Object psDeleteLock = new Object();

	@Override
	public List<Group> getAll() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all groups");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// A list to hold the results of the query
			List<Group> groups = new ArrayList<Group>();

			try
			{
				// If the PreparedStatement to get all groups was not defined, create it
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_GROUP_ID + ", " +
					                               COL_NAME + ", " +
				                                   COL_DESCRIPTION + " " +
	                                   "FROM " + GROUPS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all groups\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				}

				// Get the results of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// If any results were returned
				while(results.next())
				{
					// The Object which will contain data on the group
					Group group = new Group();

					// Set the fields on the group
					group.setId(results.getInt(1));
					group.setName(results.getString(2));
					group.setDescription(results.getString(3));

					// Set the correct permissions on the group
					group.setPermissions(getPermissionDAO().getPermissionsForGroup(group.getId()));

					// Return the group
					groups.add(group);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + groups.size() + " groups in the database.");

				return groups;
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the groups", e);

				return groups;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getAll();
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getAll()

    @Override
    public List<Group> getAllSorted(boolean isAscendingOrder,String columnSorted) throws DatabaseConfigException
	{
    	// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllSortedLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all groups");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// A list to hold the results of the query
			List<Group> groups = new ArrayList<Group>();

			try
			{
				
					// SQL to get the rows
					String selectSql = "SELECT " + COL_GROUP_ID + ", " +
					                               COL_NAME + ", " +
				                                   COL_DESCRIPTION + " " +
	                                   "FROM " + GROUPS_TABLE_NAME + " " + " ORDER BY " + columnSorted + (isAscendingOrder ? " ASC" : " DESC");

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all groups\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAllSorted = dbConnectionManager.prepareStatement(selectSql, null);
				

				// Get the results of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAllSorted);

				// If any results were returned
				while(results.next())
				{
					// The Object which will contain data on the group
					Group group = new Group();

					// Set the fields on the group
					group.setId(results.getInt(1));
					group.setName(results.getString(2));
					group.setDescription(results.getString(3));

					// Set the correct permissions on the group
					group.setPermissions(getPermissionDAO().getPermissionsForGroup(group.getId()));

					// Return the group
					groups.add(group);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + groups.size() + " groups in the database.");

				return groups;
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the groups", e);

				return groups;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getAllSorted( isAscendingOrder, columnSorted);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getAllSorted()

	@Override
	public Group getById(int groupId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Get the group with the passed ID
		Group group = loadBasicGroup(groupId);

		// If we found the group, set up its permissions
		if(group != null)
			group.setPermissions(getPermissionDAO().getPermissionsForGroup(group.getId()));

		// return the result
		return group;
	} // end method getById(int)

	@Override
	public Group getByName(String groupName) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByNameLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the group with name " + groupName);
			
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			try
			{
				// If the PreparedStatement to get a group by name was not defined, create it
				if(psGetByName == null || dbConnectionManager.isClosed(psGetByName))
				{			
					// SQL to get the row
					String selectSql = "SELECT " + COL_GROUP_ID + ", " +
					                    COL_NAME + ", " +
					                    COL_DESCRIPTION + " " +
								        "FROM " + GROUPS_TABLE_NAME + " " +
								        "WHERE " + COL_NAME + "=?";
									
					if(log.isDebugEnabled())
						log.debug("Creating the \"get group by name\" PreparedSatement from the SQL " + selectSql);
				
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByName = dbConnectionManager.prepareStatement(selectSql, psGetByName);
				} // end if(get by name PreparedStatement not defined)
						
				// Set the parameters on the select statement
				psGetByName.setString(1, groupName);
			
				// Get the result of the SELECT statement			
			
				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByName);
				
				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the group
					Group group = new Group();

					// Set the fields on the group
					group.setId(results.getInt(1));
					group.setName(results.getString(2));
					group.setDescription(results.getString(3));

					if(log.isDebugEnabled())
						log.debug("Found the group with name " + groupName + " in the database.");

					// set up its permissions
					group.setPermissions(getPermissionDAO().getPermissionsForGroup(group.getId()));

					// Return the page
					return group;
				} // end if(the group was found)
				else // There were no rows in the database, the group could not be found
				{
					if(log.isDebugEnabled())
						log.debug("The group with name " + groupName + " was not found in the database.");

					return null;
				} // end else
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the group with name " + groupName, e);
				
				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByName(groupName);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getGroupByName(int)
	
	@Override
	public Group loadBasicGroup(int groupId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the group with ID " + groupId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a group by ID was not defined, create it
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_GROUP_ID + ", " +
					                               COL_NAME + ", " +
				                                   COL_DESCRIPTION + " " +
	                                   "FROM " + GROUPS_TABLE_NAME + " " +
	                                   "WHERE " + COL_GROUP_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get group by ID\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(the get by ID PreparedStatement wasn't defined)

				// Set the parameters on the update statement
				psGetById.setInt(1, groupId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the group
					Group group = new Group();

					// Set the fields on the group
					group.setId(results.getInt(1));
					group.setName(results.getString(2));
					group.setDescription(results.getString(3));

					if(log.isDebugEnabled())
						log.debug("Found the group with ID " + groupId + " in the database.");

					// Return the page
					return group;
				} // end if(the group was found)
				else // There were no rows in the database, the group could not be found
				{
					if(log.isDebugEnabled())
						log.debug("The group with ID " + groupId + " was not found in the database.");

					return null;
				} // end else
			} // end try (get and return the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the group with ID " + groupId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return loadBasicGroup(groupId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method loadGroupBasic(int)

	@Override
	public boolean insert(Group group) throws DataException
	{
		// Check that the non-ID fields on the group are valid
		validateFields(group, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new group with name " + group.getName());

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a group was not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + GROUPS_TABLE_NAME + " (" + COL_NAME + ", " +
	                                                                               COL_DESCRIPTION + ") " +
	                                   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert group\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, group.getName());
				psInsert.setString(2, group.getDescription());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Group Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        group.setId(rs.getInt(1));

				    boolean success = true;

				    // Add the permissions to the group
				    for(Permission permission : group.getPermissions())
                    {
                        
				    	success = getGroupPermissionUtilDAO().insert(group.getId(), permission.getTabId()) && success;
                    }

					return success;
				} // end if(insert succeeded)
				else
					return false;
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new group with name " + group.getName(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(group);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method insert(Group)

	@Override
	public boolean update(Group group) throws DataException
	{
		// Check that the fields on the group are valid
		validateFields(group, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the group with ID " + group.getId());

			try
			{
				// If the PreparedStatement to update a group was not defined, create it
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + GROUPS_TABLE_NAME + " SET " + COL_NAME + "=?, " +
																		  COL_DESCRIPTION + "=? " +
	                                   "WHERE " + COL_GROUP_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update group\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, group.getName());
				psUpdate.setString(2, group.getDescription());
				psUpdate.setInt(3, group.getId());

				// Execute the update statement and return the result
				if(dbConnectionManager.executeUpdate(psUpdate) > 0)
				{
					// Remove the old permissions for the group
					boolean success = getGroupPermissionUtilDAO().deletePermissionsForGroup(group.getId());

				    // Add the permissions to the group
				    for(Permission permission : group.getPermissions())
				    	success = getGroupPermissionUtilDAO().insert(group.getId(), permission.getTabId()) && success;

					return success;
				} // end if(update successful)
				else
					return false;
			} // end try (update group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the group with ID " + group.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(group);
			}
		} // end synchronized
	} // end method update(Group)

	@Override
	public boolean delete(Group group) throws DataException
	{
		// Check that the ID field on the group are valid
		validateFields(group, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the group with ID " + group.getId());

			try
			{
				// If the PreparedStatement to delete a group was not defined, create it
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM " + GROUPS_TABLE_NAME + " " +
									   "WHERE " + COL_GROUP_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete group\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, group.getId());

				// Execute the delete statement and return the result
				// Permissions will delete automatically because of a
				// Foreign Key which cascades deletes
				return dbConnectionManager.execute(psDelete);
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the group with ID " + group.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(group);
			}
		} // end synchronized
	} // end method delete(Group)
} // end class DefaultGroupDAO
