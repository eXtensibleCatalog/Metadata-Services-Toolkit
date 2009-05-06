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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.log.Log;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.utils.LogWriter;

/**
 * MySQL implementation of the Data Access Object for the users table
 * 
 * @author Eric Osisek
 */
public class DefaultUserDAO extends UserDAO
{
	/**
	 * Data access object to get servers from the database
	 */
	private ServerDAO serverDao = new DefaultServerDAO();
	
	/**
	 * Data access object to get groups from the database
	 */
	private GroupDAO groupDao = new DefaultGroupDAO();
	
	/**
	 * Data access object to get groups for a user from the database
	 */
	private UserGroupUtilDAO userGroupDao = new DefaultUserGroupUtilDAO();
	
	/**
	 * Data access object for managing general logs
	 */
	private LogDAO logDao = new DefaultLogDAO();

	/**
	 * The repository management log file name
	 */
	private static Log logObj = (new DefaultLogDAO()).getById(Constants.LOG_ID_USER_MANAGEMENT);	
	
	/**
	 * A PreparedStatement to get all users in the database
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * A PreparedStatement to get a user from the database by its ID
	 */
	private static PreparedStatement psGetById = null;
	
	/**
	 * A PreparedStatement to get a user from the database by its user name
	 */
	private static PreparedStatement psGetByUserName = null;
	
	/**
	 * A PreparedStatement to get a user from the database by its user name
	 */
	private static PreparedStatement psGetByUserNameAndServer = null;
	
	/**
	 * A PreparedStatement to get a user from the database by their email
	 */
	private static PreparedStatement psGetByEmail = null;
	
	/**
	 * A PreparedStatement to insert a user into the database
	 */
	private static PreparedStatement psInsert = null;
	
	/**
	 * A PreparedStatement to update a user in the database
	 */
	private static PreparedStatement psUpdate = null;
	
	/**
	 * A PreparedStatement to delete a user from the database
	 */
	private static PreparedStatement psDelete = null;

    /**
	 * A PreparedStatement to get count of LDAP users in the system
	 */
	private static PreparedStatement psLDAPUserCount = null;

	/**
	 * Lock to synchronize access to the get all PreparedStatement
	 */
	private static Object psGetAllLock = new Object();

	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetByIdLock = new Object();
	
	/**
	 * Lock to synchronize access to the get by user name PreparedStatement
	 */
	private static Object psGetByUserNameLock = new Object();

    /**
	 * Lock to synchronize access to the LDAP user count PreparedStatement
	 */
	private static Object psLDAPUserCountLock = new Object();
	
	/**
	 * Lock to synchronize access to the get by user name PreparedStatement
	 */
	private static Object psGetByUserNameAndServerLock = new Object();
	
	/**
	 * Lock to synchronize access to the get by email PreparedStatement
	 */
	private static Object psGetByEmailLock = new Object();
	
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
	public List<User> getAll() 
	{
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all users");
		
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			// A list to hold the results of the query
			ArrayList<User> users = new ArrayList<User>();
			
			try
			{
				// If the PreparedStatement to get all users was not defined, create it
				if(psGetAll == null)
				{			
					// SQL to get the rows
					String selectSql = "SELECT " + COL_USER_ID + ", " +
				                                   COL_USERNAME + ", " +
				                                   COL_FIRST_NAME + ", " +
				                                   COL_LAST_NAME + ", " +
				                                   COL_PASSWORD + ", " +
				                                   COL_EMAIL + ", " +
				                                   COL_SERVER_ID + ", " +
				                                   COL_LAST_LOGIN + ", " +
				                                   COL_ACCOUNT_CREATED + ", " +
				                                   COL_FAILED_LOGIN_ATTEMPTS + " " +  
	                                   "FROM " + USERS_TABLE_NAME;
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"get all users\" PreparedStatement from the SQL " + selectSql);
				
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnection.prepareStatement(selectSql);
				} // end if(get all PreparedStatement not defined)
			
			
				// Get the results of the SELECT statement			
			
				// Execute the query
				results = psGetAll.executeQuery();
			
				// If any results were returned
				while(results.next())
				{
					// The Object which will contain data on the user
					User user = new User();
					
					// Set the fields on the user
					user.setId(results.getInt(1));
					user.setUsername(results.getString(2));
					user.setFirstName(results.getString(3));
					user.setLastName(results.getString(4));
					user.setPassword(results.getString(5));
					user.setEmail(results.getString(6));
					user.setServer(serverDao.getById(results.getInt(7)));
					user.setLastLogin(results.getDate(8));
					user.setAccountCreated(results.getDate(9));
					user.setFailedLoginAttempts(results.getInt(10));
					
					// Get the groups for the user
					for(Integer groupId : userGroupDao.getGroupsForUser(user.getId()))
						user.addGroup(groupDao.getById(groupId));
									
					// Return the user
					users.add(user);
				} // end loop over results
				
				if(log.isDebugEnabled())
					log.debug("Found " + users.size() + " users in the database.");
				
				return users;
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the users", e);
				return users;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getAll()

    /**
     * returns the number of LDAP users in the system
     *
     * @return number of LDAP users
     */
    public int getLDAPUserCount()
    {
        synchronized(psLDAPUserCountLock)
		{
            if(log.isDebugEnabled())
				log.debug("Getting the count for all LDAP members ");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get users by group ID wasn't defined, create it
				if(psLDAPUserCount == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT COUNT(" + COL_USER_ID + ") " +
	                                   "FROM " + USERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_SERVER_ID + "<> 1";

					if(log.isDebugEnabled())
						log.debug("Creating the \"LDAP user count\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psLDAPUserCount = dbConnection.prepareStatement(selectSql);
				}


				// Get the result of the SELECT statement

				// Execute the query
				results = psLDAPUserCount.executeQuery();

				// Return the result
				if(results.next())
					return results.getInt(1);

				if(log.isDebugEnabled())
					log.debug("Did not find count of " + " number of LDAP users in the system.");

				return 0;

			} // end try (get and return the group IDs which the user belongs to)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the number of LDAP users in the system", e);

				return 0;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally
        }
    }
	@Override
	public List<User> getSorted(boolean asc,String columnSorted)
	{
		if(log.isDebugEnabled())
			log.debug("Getting all users sorted in " + (asc ? "ascending" : "descending") + " order on the column " + columnSorted);
	
		// Validate the column we're trying to sort on
		if(!sortableColumns.contains(columnSorted))
		{
			log.error("An attempt was made to sort on the invalid column " + columnSorted);
			return getAll();
		} // end if(sort column invalid)
		
		// The ResultSet from the SQL query
		ResultSet results = null;
		
		// The Statement for getting the rows
		Statement getSorted = null;
		
		// A list to hold the results of the query
		List<User> users = new ArrayList<User>();
		
		try
		{
					
			// SQL to get the rows
			String selectSql = "SELECT " + COL_USER_ID + ", " +
		                                   COL_USERNAME + ", " +
		                                   COL_FIRST_NAME + ", " +
		                                   COL_LAST_NAME + ", " +
		                                   COL_PASSWORD + ", " +
		                                   COL_EMAIL + ", " +
		                                   COL_SERVER_ID + ", " +
		                                   COL_LAST_LOGIN + ", " +
		                                   COL_ACCOUNT_CREATED + ", " +
		                                   COL_FAILED_LOGIN_ATTEMPTS + " " +  
                               "FROM " + USERS_TABLE_NAME + " " +
                               "ORDER BY " + columnSorted + (asc ? " ASC" : " DESC");
		
			if(log.isDebugEnabled())
				log.debug("Creating the \"get all users sorted\" Statement from the SQL " + selectSql);
		
			// A statement to run the select SQL
			getSorted = dbConnection.createStatement();
			
			// Get the results of the SELECT statement			
			
			// Execute the query
			results = getSorted.executeQuery(selectSql);
		
			// If any results were returned
			while(results.next())
			{
				// The Object which will contain data on the user
				User user = new User();
				
				// Set the fields on the user
				user.setId(results.getInt(1));
				user.setUsername(results.getString(2));
				user.setFirstName(results.getString(3));
				user.setLastName(results.getString(4));
				user.setPassword(results.getString(5));
				user.setEmail(results.getString(6));
				user.setServer(serverDao.getById(results.getInt(7)));
				user.setLastLogin(results.getDate(8));
				user.setAccountCreated(results.getDate(9));
				user.setFailedLoginAttempts(results.getInt(10));
				
				// Get the groups for the user
				for(Integer groupId : userGroupDao.getGroupsForUser(user.getId()))
					user.addGroup(groupDao.getById(groupId));
								
				// Return the user
				users.add(user);
			} // end loop over results
			
			if(log.isDebugEnabled())
				log.debug("Found " + users.size() + " users in the database.");
			
			return users;
		} // end try
		catch(SQLException e)
		{
			log.error("A SQLException occurred while getting the users sorted in ascending order.", e);
			
			return users;
		} // end catch(SQLException)
		finally
		{
			MySqlConnectionManager.closeResultSet(results);
			
			try
			{
				getSorted.close();
			} // end try(close the Statement)
			catch(SQLException e)
			{
				log.error("An error occurred while trying to close the \"get processing directives sorted\" Statement");
			} // end catch(DataException)
		} // end finally(close ResultSet)
	} // end method getSorted(boolean, String)
	
	@Override
	public User getById(int userId) 
	{
		// Get the basic user
		User user = loadBasicUser(userId);
		
		// Get the groups for the user
		for(Integer groupId : userGroupDao.getGroupsForUser(user.getId()))
			user.addGroup(groupDao.getById(groupId));
		
		// Return the result
		return user;
	} // end method getById(int)

	@Override
	public User loadBasicUser(int userId) 
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the user with ID " + userId);
			
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			try
			{
				// If the PreparedStatement to get a user by ID was not defined, create it
				if(psGetById == null)
				{			
					// SQL to get the row
					String selectSql = "SELECT " + COL_USER_ID + ", " +
				                                   COL_USERNAME + ", " +
				                                   COL_FIRST_NAME + ", " +
				                                   COL_LAST_NAME + ", " +
				                                   COL_PASSWORD + ", " +
				                                   COL_EMAIL + ", " +
				                                   COL_SERVER_ID + ", " +
				                                   COL_LAST_LOGIN + ", " +
				                                   COL_ACCOUNT_CREATED + ", " +
				                                   COL_FAILED_LOGIN_ATTEMPTS + " " +
	                                   "FROM " + USERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_USER_ID + "=?";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"get user by ID\" PreparedSatement from the SQL " + selectSql);
				
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement not defined)
						
				// Set the parameters on the select statement
				psGetById.setInt(1, userId);
			
				// Get the result of the SELECT statement			
			
				// Execute the query
				results = psGetById.executeQuery();
				
				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the user
					User user = new User();
					
					// Set the fields on the user
					user.setId(results.getInt(1));
					user.setUsername(results.getString(2));
					user.setFirstName(results.getString(3));
					user.setLastName(results.getString(4));
					user.setPassword(results.getString(5));
					user.setEmail(results.getString(6));
					user.setServer(serverDao.getById(results.getInt(7)));
					user.setLastLogin(results.getDate(8));
					user.setAccountCreated(results.getDate(9));
					user.setFailedLoginAttempts(results.getInt(10));
					
					if(log.isDebugEnabled())
						log.debug("Found the user with ID " + userId + " in the database.");
					
					// Return the user
					return user;
				} // end if(user found)
				else // There were no rows in the database, the user could not be found
				{	
					if(log.isDebugEnabled())
						log.debug("The user with ID " + userId + " was not found in the database.");
					
					return null;
				} // end else
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the user with ID " + userId, e);
				
				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method loadBasicUser(int)

	@Override
	public User getUserByName(String userName) 
	{
		synchronized(psGetByUserNameLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the user with user name " + userName);
			
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			try
			{
				// If the PreparedStatement to get a user by user name was not defined, create it
				if(psGetByUserName == null)
				{			
					// SQL to get the row
					String selectSql = "SELECT " + COL_USER_ID + ", " +
				                                   COL_USERNAME + ", " +
				                                   COL_FIRST_NAME + ", " +
				                                   COL_LAST_NAME + ", " +
				                                   COL_PASSWORD + ", " +
				                                   COL_EMAIL + ", " +
				                                   COL_SERVER_ID + ", " +
				                                   COL_LAST_LOGIN + ", " +
				                                   COL_ACCOUNT_CREATED + ", " +
				                                   COL_FAILED_LOGIN_ATTEMPTS + " " +
	                                   "FROM " + USERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_USERNAME + "=?";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"get user by user name\" PreparedSatement from the SQL " + selectSql);
				
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByUserName = dbConnection.prepareStatement(selectSql);
				} // end if(get by user name PreparedStatement not defined)
						
				// Set the parameters on the select statement
				psGetByUserName.setString(1, userName);
			
				// Get the result of the SELECT statement			
			
				// Execute the query
				results = psGetByUserName.executeQuery();
				
				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the user
					User user = new User();
					
					// Set the fields on the user
					user.setId(results.getInt(1));
					user.setUsername(results.getString(2));
					user.setFirstName(results.getString(3));
					user.setLastName(results.getString(4));
					user.setPassword(results.getString(5));
					user.setEmail(results.getString(6));
					user.setServer(serverDao.getById(results.getInt(7)));
					user.setLastLogin(results.getDate(8));
					user.setAccountCreated(results.getDate(9));
					user.setFailedLoginAttempts(results.getInt(10));
					
					// Get the groups for the user
					for(Integer groupId : userGroupDao.getGroupsForUser(user.getId()))
						user.addGroup(groupDao.getById(groupId));
					
					if(log.isDebugEnabled())
						log.debug("Found the user with name " + userName + " in the database.");
					
					// Return the user
					return user;
				} // end if(user found)
				else // There were no rows in the database, the user could not be found
				{	
					if(log.isDebugEnabled())
						log.debug("The user with name " + userName + " was not found in the database.");
					
					return null;
				} // end else
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the user with name " + userName, e);
				
				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getUserByName(int)
	
	@Override
	public User getUserByUserName(String userName, Server server) 
	{
		synchronized(psGetByUserNameAndServerLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the user with user name " + userName + " and server id : " + server.getId());
			
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			try
			{
				// If the PreparedStatement to get a user by user name was not defined, create it
				if(psGetByUserNameAndServer == null)
				{			
					// SQL to get the row
					String selectSql = "SELECT " + COL_USER_ID + ", " +
				                                   COL_USERNAME + ", " +
				                                   COL_FIRST_NAME + ", " +
				                                   COL_LAST_NAME + ", " +
				                                   COL_PASSWORD + ", " +
				                                   COL_EMAIL + ", " +
				                                   COL_SERVER_ID + ", " +
				                                   COL_LAST_LOGIN + ", " +
				                                   COL_ACCOUNT_CREATED + ", " +
				                                   COL_FAILED_LOGIN_ATTEMPTS + " " +
	                                   "FROM " + USERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_USERNAME + "=?" + " AND " + COL_SERVER_ID + "=?";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"get user by user name\" PreparedSatement from the SQL " + selectSql);
				
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByUserNameAndServer = dbConnection.prepareStatement(selectSql);
				} // end if(get by user name PreparedStatement not defined)
						
				// Set the parameters on the select statement
				psGetByUserNameAndServer.setString(1, userName);
				psGetByUserNameAndServer.setInt(2, server.getId());
				
				// Get the result of the SELECT statement			
			
				// Execute the query
				results = psGetByUserNameAndServer.executeQuery();
				
				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the user
					User user = new User();
					
					// Set the fields on the user
					user.setId(results.getInt(1));
					user.setUsername(results.getString(2));
					user.setFirstName(results.getString(3));
					user.setLastName(results.getString(4));
					user.setPassword(results.getString(5));
					user.setEmail(results.getString(6));
					user.setServer(serverDao.getById(results.getInt(7)));
					user.setLastLogin(results.getDate(8));
					user.setAccountCreated(results.getDate(9));
					user.setFailedLoginAttempts(results.getInt(10));
					
					// Get the groups for the user
					for(Integer groupId : userGroupDao.getGroupsForUser(user.getId()))
						user.addGroup(groupDao.getById(groupId));
					
					if(log.isDebugEnabled())
						log.debug("Found the user with name " + userName + " in the database.");
					
					// Return the user
					return user;
				} // end if(user found)
				else // There were no rows in the database, the user could not be found
				{	
					if(log.isDebugEnabled())
						log.debug("The user with name " + userName + " was not found in the database.");
					
					return null;
				} // end else
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the user with name " + userName, e);
				
				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getUserByName(int)
	
	@Override
	public User getUserByEmail(String email, Server server) 
	{
		synchronized(psGetByEmailLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the user with email " + email);
			
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			try
			{
				// If the PreparedStatement to get a user by email was not defined, create it
				if(psGetByEmail == null)
				{			
					// SQL to get the row
					String selectSql = "SELECT " + COL_USER_ID + ", " +
				                                   COL_USERNAME + ", " +
				                                   COL_FIRST_NAME + ", " +
				                                   COL_LAST_NAME + ", " +
				                                   COL_PASSWORD + ", " +
				                                   COL_EMAIL + ", " +
				                                   COL_SERVER_ID + ", " +
				                                   COL_LAST_LOGIN + ", " +
				                                   COL_ACCOUNT_CREATED + ", " +
				                                   COL_FAILED_LOGIN_ATTEMPTS + " " +
	                                   "FROM " + USERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_EMAIL + "=?" + " AND " + COL_SERVER_ID + "=?";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"get user by email\" PreparedSatement from the SQL " + selectSql);
				
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByEmail = dbConnection.prepareStatement(selectSql);
				} // end if(get by email PreparedStatement not defined)
						
				// Set the parameters on the select statement
				psGetByEmail.setString(1, email);
				psGetByEmail.setInt(2, server.getId());
			
				// Get the result of the SELECT statement			
			
				// Execute the query
				results = psGetByEmail.executeQuery();
				
				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the user
					User user = new User();
					
					// Set the fields on the user
					user.setId(results.getInt(1));
					user.setUsername(results.getString(2));
					user.setFirstName(results.getString(3));
					user.setLastName(results.getString(4));
					user.setPassword(results.getString(5));
					user.setEmail(results.getString(6));
					user.setServer(serverDao.getById(results.getInt(7)));
					user.setLastLogin(results.getDate(8));
					user.setAccountCreated(results.getDate(9));
					user.setFailedLoginAttempts(results.getInt(10));
					
					if(log.isDebugEnabled())
						log.debug("Found the user with email " + email + " in the database.");
					
					// Return the user
					return user;
				} // end if(user found)
				else // There were no rows in the database, the user could not be found
				{	
					if(log.isDebugEnabled())
						log.debug("The user with email " + email + " was not found in the database.");
					
					return null;
				} // end else
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the user with email " + email, e);
				
				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getUserByEmail(int)
	
	@Override
	public boolean insert(User user) throws DataException 
	{
		// Check that the non-ID fields on the user are valid
		validateFields(user, false, true);
		
		// insert the server if it hasn't already been inserted
		if(user.getServer().getId() < 0)
			serverDao.insert(user.getServer());
		
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new user with username " + user.getUsername());
			
			// The ResultSet returned by the query
			ResultSet rs = null;
			
			try
			{
				// If the PreparedStatement to insert a user was not defined, create it
				if(psInsert == null)
				{			
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + USERS_TABLE_NAME + " (" + COL_USERNAME + ", " +
					                                                        COL_FIRST_NAME + ", " +
					                                                        COL_LAST_NAME + ", " +
					                                                        COL_PASSWORD + ", " +
					                                                        COL_EMAIL + ", " +
					                                                        COL_SERVER_ID + ", " +
					                                                        COL_LAST_LOGIN + ", " +
					                                                        COL_ACCOUNT_CREATED + ", " +
					                                                        COL_FAILED_LOGIN_ATTEMPTS + ") " +
		                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"insert user\" PreparedStatement from the SQL " + insertSql);
								
					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnection.prepareStatement(insertSql);
				}
				
				// Set the parameters on the insert statement
				psInsert.setString(1, user.getUsername().trim());
				psInsert.setString(2, user.getFirstName().trim());
				psInsert.setString(3, user.getLastName().trim());
				psInsert.setString(4, user.getPassword());
				psInsert.setString(5, user.getEmail().trim());
				psInsert.setInt(6, user.getServer().getId());
				psInsert.setDate(7, user.getLastLogin());
				psInsert.setDate(8, user.getAccountCreated());
				psInsert.setInt(9, user.getFailedLoginAttempts());
		                    
				// Execute the insert statement and return the result
				if(psInsert.executeUpdate() > 0)
				{
					// Get the auto-generated user ID and set it correctly on this User Object
					rs = dbConnection.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
		
					if (rs.next())
						user.setId(rs.getInt(1));
					
					// Add the user to the correct groups
					for(Group group : user.getGroups())
						userGroupDao.insert(user.getId(), group.getId());
					
					LogWriter.addInfo(logObj.getLogFileLocation(), "Added a new user with the username " + user.getUsername());
				    
					return true;
				} // end if(insert succeeded)
				else
				{
					LogWriter.addError(logObj.getLogFileLocation(), "An error occurrred while adding a new user with the username " + user.getUsername());
					
					logObj.setErrors(logObj.getErrors() + 1);
			    	logDao.update(logObj);
			    	
					return false;
				}
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new user with username " + user.getUsername(), e);
				
				LogWriter.addError(logObj.getLogFileLocation(), "An error occurrred while adding a new user with the username " + user.getUsername());
				
				logObj.setErrors(logObj.getErrors() + 1);
		    	logDao.update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method insert(User)

	@Override
	public boolean update(User user) throws DataException 
	{
		// Check that the fields on the user are valid
		validateFields(user, true, true);
		
		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the user with ID " + user.getId());
			
			try
			{
				// If the PreparedStatement to update a user was not defined, create it
				if(psUpdate == null)
				{
					// SQL to update new row
					String updateSql = "UPDATE " + USERS_TABLE_NAME + " SET " + COL_USERNAME + "=?, " +
																		  COL_FIRST_NAME + "=?, " +
																		  COL_LAST_NAME + "=?, " +
																		  COL_PASSWORD + "=?, " +
																		  COL_EMAIL + "=?, " +
																		  COL_SERVER_ID + "=?, " +
																		  COL_LAST_LOGIN + "=?, " +
																		  COL_ACCOUNT_CREATED + "=?, " +
																		  COL_FAILED_LOGIN_ATTEMPTS + "=? " +
	                                   "WHERE " + COL_USER_ID + "=?";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"update user\" PreparedStatement from the SQL " + updateSql);
				
					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnection.prepareStatement(updateSql);
				} // end if(update PreparedStatement not defined)
					
				// Set the parameters on the update statement
				psUpdate.setString(1, user.getUsername().trim());
				psUpdate.setString(2, user.getFirstName().trim());
				psUpdate.setString(3, user.getLastName().trim());
				psUpdate.setString(4, user.getPassword());
				psUpdate.setString(5, user.getEmail().trim());
				psUpdate.setInt(6, user.getServer().getId());
				psUpdate.setDate(7, user.getLastLogin());
				psUpdate.setDate(8, user.getAccountCreated());
				psUpdate.setInt(9, user.getFailedLoginAttempts());
				psUpdate.setInt(10,user.getId());
				
				// Execute the update statement and return the result
				boolean success = psUpdate.executeUpdate() > 0;
				
				// Delete the old groups for the user
				userGroupDao.deleteGroupsForUser(user.getId());
				
				// Add the user to the correct groups
				for(Group group : user.getGroups())
					success = userGroupDao.insert(user.getId(), group.getId()) && success;
				
				if(success)
					LogWriter.addInfo(logObj.getLogFileLocation(), "Updated the user with the username " + user.getUsername());
				else 
				{
					LogWriter.addError(logObj.getLogFileLocation(), "An error occurrred while updating the user with the username " + user.getUsername());
					
					logObj.setErrors(logObj.getErrors() + 1);
			    	logDao.update(logObj);
				}
				
				return success;
			} // end try
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the user with ID " + user.getId(), e);
				
				LogWriter.addError(logObj.getLogFileLocation(), "An error occurrred while updating the user with the username " + user.getUsername());
				
				logObj.setErrors(logObj.getErrors() + 1);
		    	logDao.update(logObj);
		    	
				return false;
			} // end catch(SQLException
		} // end synchronized
	} // end method update(User)
	
	@Override
	public boolean delete(User user) throws DataException 
	{
		// Check that the ID field on the user are valid
		validateFields(user, true, false);
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the user with ID " + user.getId());
			
			try
			{
				// If the PreparedStatement to delete a user was not defined, create it
				if(psDelete == null)
				{			
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM " + USERS_TABLE_NAME + " " +
									   "WHERE " + COL_USER_ID + " = ? ";
				
					if(log.isDebugEnabled())
						log.debug("Creating the \"delete user\" PreparedStatement from the SQL " + deleteSql);
				
					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnection.prepareStatement(deleteSql);
				} // end if(delete PreparedStatement not defined)
				
				// Set the parameters on the delete statement
				psDelete.setInt(1, user.getId());
				
				// Execute the delete statement and return the result
				boolean success = psDelete.execute();
				
				if(success)
					LogWriter.addInfo(logObj.getLogFileLocation(), "Deleted the user with the username " + user.getUsername());
				else
				{
					LogWriter.addError(logObj.getLogFileLocation(), "An error occurrred while deleting the user with the username " + user.getUsername());
				
					logObj.setErrors(logObj.getErrors() + 1);
			    	logDao.update(logObj);
				}
				
				return success;
			} // end try(delete the user)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the user with ID " + user.getId(), e);
				
				LogWriter.addError(logObj.getLogFileLocation(), "An error occurrred while deleting the user with the username " + user.getUsername());
				
				logObj.setErrors(logObj.getErrors() + 1);
		    	logDao.update(logObj);
		    	
				return false;
			} // end catch(SQLException
		} // end synchronized
	} // end method delete(User)
} // end class DefaultUserDAO
