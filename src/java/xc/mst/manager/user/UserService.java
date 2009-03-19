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

import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.dao.DataException;

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
	 */
	public User getUserById(int userId);

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
    public void insertUser(User user) throws Exception;

     /**
     *
     * @param user user whose details should be updated in the database
     */
    public void updateUser(User user) throws Exception;

     /**
     * This method is used to create a new temporary password
     * @return The temporary password that is generated
     */
    public String createRandomPassword();

//    /**
//     * This method is used to send the newly generated password to the User specified by the userName.
//     * @param userName The username of the user to whom the password should be sent.
//     */
//    public void sendPassword(String userName) throws Exception;

    /**
     * Authenticate user
     *
     * @param user user to be authenticated
     * @param password to be checked
     *
     * @return True if authentication is success else false
     */
    public boolean  authenticateUser(User user, String password) throws Exception;

    /**
     * User with group and permission data loaded
     *
     * @param userId Id of user
     *
     * @return User
     */
    public User  getUserWithPermissions(int userId) throws Exception;

    /**
     * Encrypts the password
     *
     * @param plaintext plain password
     * @return Encrypted password
     * @throws Exception
     */
    public String encryptPassword(String plaintext) throws Exception;

    /**
     * Sends email to the user with new password
     *
     * @param newPassword New password
     * @param user user requesting reset password
     */
    public void sendEmailForForgotPassword(String newPassword, User user);

    /**
     * Get user having specified email
     *
     * @param email Email to get the user
     * @return User having specified email
     */
    public User getUserByEmail(String email, Server server) ;

    /**
     * returns a sorted list of users
     * @param sort determines whether the list of users is sorted in ascending or descending order
     * @param columnSorted the column on which the rows of users are sorted
     * @return list of users
     */
    public List<User> getAllUsersSorted(boolean sort,String columnSorted);

    /**
     * Get user by given name and server
     *
     * @param userName user name of user
     * @param server  login server
     * @return User
     */
    public User getUserByUserName(String userName, Server server);

    /**
     * Authenticate LDAP user
     *
     * @param user user to be authenticated
     * @param password to be checked
     * @param server Login server
     *
     * @return True if authentication is success else false
     */
    public boolean  authenticateLDAPUser(User user, String password, Server server) throws Exception;
}
