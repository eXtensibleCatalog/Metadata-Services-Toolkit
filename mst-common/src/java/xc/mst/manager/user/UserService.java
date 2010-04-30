/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.user;

import java.util.List;

import xc.mst.bo.user.Permission;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Interface for User to deal with creating, updating
 * and deleting Users
 *
 * @author Sharmila Ranganathan
 *
 */
public interface UserService {


	/**
	 * Get User having the specified user id
	 *
	 * @param userId Id of the user
	 * @return User if exist else null
	 * @throws DatabaseConfigException 
	 */
	public User getUserById(int userId) throws DatabaseConfigException;

	/**
	 * Delete User
	 *
	 * @param user user to be deleted
	 * @throws DataException Thrown when problem in deleting the user
	 */
	public void deleteUser(User user) throws DataException;

    /**
     *
     * @param user user to inserted into the database
     */
    public void insertUser(User user) throws DataException;

     /**
     *
     * @param user user whose details should be updated in the database
     */
    public void updateUser(User user) throws DataException;

     /**
     * This method is used to create a new temporary password
      *
     * @return The temporary password that is generated
     */
    public String createRandomPassword();

    /**
     * Authenticate user
     *
     * @param user user to be authenticated
     * @param password to be checked
     *
     * @return True if authentication is success else false
     */
    public boolean  authenticateUser(User user, String password);

    /**
     * User with group and permission data loaded
     *
     * @param userId Id of user
     *
     * @return User
     */
    public User  getUserWithPermissions(int userId) throws DataException;

    /**
     * Encrypts the password
     *
     * @param plaintext plain password
     * @return Encrypted password
     * @throws Exception
     */
    public String encryptPassword(String plaintext) throws DataException;

    /**
     * Sends email to the user with new password
     *
     * @param newPassword New password
     * @param user user requesting reset password
     */
    public boolean sendEmailForForgotPassword(String newPassword, User user);

    /**
     * Sends email to all admins about an error in the MST GUI
     *
     * @param comments message error message which describes where the error took place.
     * @throws DatabaseConfigException
     */
    public boolean sendEmailErrorReport();

    /**
     * Get user having specified email
     *
     * @param email Email to get the user
     * @return User having specified email
     * @throws DatabaseConfigException 
     */
    public User getUserByEmail(String email, Server server) throws DatabaseConfigException ;

    /**
     * Returns a sorted list of users
     *
     * @param sort determines whether the list of users is sorted in ascending or descending order
     * @param columnSorted the column on which the rows of users are sorted
     * @return list of users
     * @throws DatabaseConfigException 
     */
    public List<User> getAllUsersSorted(boolean sort,String columnSorted) throws DatabaseConfigException;

    /**
     * Get user by given name and server
     *
     * @param userName user name of user
     * @param server  login server
     * @return User
     * @throws DatabaseConfigException 
     */
    public User getUserByUserName(String userName, Server server) throws DatabaseConfigException;

    /**
     * Authenticate LDAP user
     *
     * @param user user to be authenticated
     * @param password to be checked
     * @param server Login server
     *
     * @return True if authentication is success else false
     */
    public boolean  authenticateLDAPUser(User user, String password, Server server);

   /**
    * Returns the list of users associated with a group
    *
    * @param groupId group Id
    * @param sort determines if the rows are to be sorted in ascending or descending order
    * @param columnSorted the column on which the rows are to be sorted
    * @return sorted list of users
 * @throws DatabaseConfigException 
    */
    public List<User> getUsersForGroupSorted(int groupId,boolean sort,String columnSorted) throws DatabaseConfigException;

    /**
     * Sends email to all admins to give permission for a new user
     *
     * @param userName User name of new user
     * @param comments Comments to get access to the system
     * @throws DatabaseConfigException 
     */
    public boolean sendEmailForUserPermission(String userName, String comments) throws DatabaseConfigException ;

     /**
    * Returns the list of users associated with a group
      * 
    * @param groupId group ID
    * @return sorted list of users
     * @throws DatabaseConfigException 
    */
    public List<User> getUsersForGroup(int groupId) throws DatabaseConfigException;

    /**
     * Retrieves the number of users in a group
     *
     * @param groupId group ID
     * @return member count
     */
    public int getUserCountForGroup(int groupId);

    /**
     * returns the number of LDAP users in the system
     *
     * @return number of LDAP users
     * @throws DatabaseConfigException 
     */
    public int getLDAPUserCount() throws DatabaseConfigException;

    /**
     * Get permissions for user ordered by tab order
     *  
     * @param user User to get permissions
     * @return user permissions
     * @throws DatabaseConfigException 
     */
    public List<Permission> getPermissionsForUserByTabOrderAsc(User user) throws DatabaseConfigException;
    
    /**
     * Sends email to user to inform that following permissions has been assigned.
     *
     * @param user User whose permissions has beedn changed/added/removed
     * @throws DatabaseConfigException 
     */
    public boolean sendEmailToUserWithPermissions(User user) throws DatabaseConfigException ;
}
