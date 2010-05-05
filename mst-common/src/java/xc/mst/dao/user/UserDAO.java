/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Accesses users in the database
 *
 * @author Eric Osisek
 */
public abstract class UserDAO extends BaseDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The Object managing the database connection
	 */
	protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();
	
	/**
	 * The name of the users database table
	 */
	public final static String USERS_TABLE_NAME = "users";

	/**
	 * The name of the user ID column
	 */
	public final static String COL_USER_ID = "user_id";

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
	 * The name of the password column
	 */
	public final static String COL_PASSWORD = "password";

	/**
	 * The name of the email column
	 */
	public final static String COL_EMAIL = "email";

	/**
	 * The name of the server_id column
	 */
	public final static String COL_SERVER_ID = "server_id";

    /**
	 * The name of the last_login column
	 */
	public final static String COL_LAST_LOGIN = "last_login";

    /**
	 * The name of the account_created column
	 */
	public final static String COL_ACCOUNT_CREATED = "account_created";

    /**
	 * The name of the failed_login_attempts column
	 */
	public final static String COL_FAILED_LOGIN_ATTEMPTS = "failed_login_attempts";

	/**
	 * A set of all columns which are valid for sorting
	 */
	protected static Set<String> sortableColumns = new HashSet<String>();
	
	// Initialize the list of sortable columns
	static
	{
		sortableColumns.add(COL_USER_ID);
		sortableColumns.add(COL_USERNAME);
		sortableColumns.add(COL_FIRST_NAME);
		sortableColumns.add(COL_LAST_NAME);
		sortableColumns.add(COL_PASSWORD);
		sortableColumns.add(COL_EMAIL);
		sortableColumns.add(COL_SERVER_ID);
		sortableColumns.add(COL_LAST_LOGIN);
		sortableColumns.add(COL_ACCOUNT_CREATED);
		sortableColumns.add(COL_FAILED_LOGIN_ATTEMPTS);
	} // end initialization of sortableColumns

	/**
	 * Gets all users in the database
	 *
	 * @return A list containing all users in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<User> getAll() throws DatabaseConfigException;

	/**
     * Returns a sorted list of all users
     * 
     * @param asc Determines if the rows are sorted in the ascending or descending order
     * @param columnSorted The column on which the rows are sorted
     * @return A sorted list of users
	 * @throws DatabaseConfigException if there was a problem connecting to the database
     */
	public abstract List<User> getSorted(boolean asc, String columnSorted) throws DatabaseConfigException;

	/**
	 * Gets a user by it's ID
	 *
	 * @param userId The ID of the user to get
	 * @return The user with the passed ID, or null if there was no user with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract User getById(int userId) throws DatabaseConfigException;

	/**
	 * Gets a user by it's ID.  Does not set the list of permissions on the returned user.
	 *
	 * @param userId The ID of the user to get
	 * @return The user with the passed ID, or null if there was no user with that ID.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract User loadBasicUser(int userId) throws DatabaseConfigException;

	/**
	 * Gets a user by it's user name
	 *
	 * @param userName The user name of the user to get
	 * @return The user with the passed name, or null if there was no user with that name.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract User getUserByName(String userName) throws DatabaseConfigException;

	/**
	 * Gets a user by their email
	 *
	 * @param email The email of the user to get
	 * @param server Login server
	 * @return The user with the passed email, or null if there was no user with that email.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract User getUserByEmail(String email, Server server) throws DatabaseConfigException;

	/**
	 * Gets a user by it's user name and server id
	 *
	 * @param userName The user name of the user to get
	 * @param serverId Id of the server
	 * @return The user with the passed name and server, or null if there was no user with that name.
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract User getUserByUserName(String userName, Server server) throws DatabaseConfigException;
    
    /**
     * returns the number of LDAP users in the system
     * 
     * @return number of LDAP users
	 * @throws DatabaseConfigException if there was a problem connecting to the database
     */
    public abstract int getLDAPUserCount() throws DatabaseConfigException;

	/**
	 * Inserts a user into the database
	 *
	 * @param user The user to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed user was not valid for inserting
	 */
	public abstract boolean insert(User user) throws DataException;

	/**
	 * Updates a user in the database
	 *
	 * @param user The user to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed user was not valid for updating
	 */
	public abstract boolean update(User user) throws DataException;

	/**
	 * Deletes a user from the database
	 *
	 * @param user The user to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed user was not valid for deleting
	 */
	public abstract boolean delete(User user) throws DataException;

	/**
	 * Validates the fields on the passed User Object
	 *
	 * @param user The user to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(User user, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(user.getId() < 0)
				errorMessage.append("The user_id is invalid. ");
		} // end if(we should validate the ID)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(user.getUsername() == null || user.getUsername().length() <= 0 || user.getUsername().length() > 255)
				errorMessage.append("The username is invalid. ");

			if(user.getFirstName() != null && user.getFirstName().length() > 255)
				errorMessage.append("The fullname is invalid. ");

			if(user.getPassword() != null && user.getPassword().length() > 63)
				errorMessage.append("The password is invalid. ");

			if(user.getEmail() == null || user.getEmail().length() <= 0 || user.getEmail().length() > 255)
				errorMessage.append("The email is invalid.");

			if(user.getServer() == null)
				errorMessage.append("The server is invalid.");
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(User, boolean, boolean)
} // end class UserDAO
