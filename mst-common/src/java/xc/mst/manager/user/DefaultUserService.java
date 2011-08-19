/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.manager.user;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.email.Emailer;
import xc.mst.manager.BaseService;
import xc.mst.utils.MSTConfiguration;

/**
 * Service class for User to deal with creating, updating
 * and deleting Users
 * 
 * @author Sharmila Ranganathan
 * 
 */
public class DefaultUserService extends BaseService implements UserService {

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Get User having the specified user id
     * 
     * @throws DatabaseConfigException
     * 
     * @see xc.mst.manager.user.UserService#getUserById(Long)
     */
    public User getUserById(int userId) throws DatabaseConfigException {
        return userDAO.getById(userId);
    }

    /**
     * Get User having the specified user name in the given server id
     * 
     * @throws DatabaseConfigException
     * 
     * @see xc.mst.manager.user.UserService#getUser(String, int)
     */
    public User getUserByUserName(String userName, Server server) throws DatabaseConfigException {
        return userDAO.getUserByUserName(userName, server);

    }

    /**
     * Delete User
     * 
     * @see xc.mst.manager.user.UserService#deleteUser(User)
     */
    public void deleteUser(User user) throws DataException {
        userDAO.delete(user);

    }

    /**
     * Insert User
     * 
     * @param user
     */
    public void insertUser(User user) throws DataException {

        if (!user.getServer().getName().equalsIgnoreCase("Local")) {
            user.setPassword("");
        } else {
            user.setPassword(encryptPassword(user.getPassword()));
        }

        user.setAccountCreated(new Date());

        userDAO.insert(user);

    }

    /**
     * Update User
     * 
     * @param user
     */
    public void updateUser(User user) throws DataException {
        userDAO.update(user);
    }

    /**
     * Creates a new temporary password for the User
     * 
     * @return returns the newly generated temporary password
     */
    public String createRandomPassword() {

        char[] pw = new char[8];
        int c = 'A';
        int r1 = 0;
        for (int i = 0; i < 8; i++) {
            r1 = (int) (Math.random() * 3);
            switch (r1) {
            case 0:
                c = '0' + (int) (Math.random() * 10);
                break;
            case 1:
                c = 'a' + (int) (Math.random() * 26);
                break;
            case 2:
                c = 'A' + (int) (Math.random() * 26);
                break;
            }
            pw[i] = (char) c;
        }
        return new String(pw);

    }

    /**
     * This method is used for encryption of plaintext Passwords
     * 
     * @param plaintext
     * @return
     * @throws java.lang.Exception
     */
    public synchronized String encryptPassword(String plaintext) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA"); // step 2
        } catch (NoSuchAlgorithmException e) {
            log.error("Exception occured while encrypting the password.", e);
        }
        try {
            md.update(plaintext.getBytes("UTF-8")); // step 3
        } catch (UnsupportedEncodingException e) {
            log.error("Exception occured while encrypting the password.", e);
        }

        byte raw[] = md.digest(); // step 4
        raw = (new Base64()).encode(raw);
        String hash = new String(raw, 0, raw.length - 2); // step 5
        return hash; // step 6
    }

    /**
     * Authenticate user
     * 
     * @param user
     *            user to be authenticated
     * @param password
     *            to be checked
     * 
     * @return True if authentication is success else false
     */
    public boolean authenticateUser(User user, String password) {
        boolean authenticationStatus = false;
        String encodedPassword = encryptPassword(password);

        if (user.getPassword().equals(encodedPassword)) {
            authenticationStatus = true;
        } else {
            authenticationStatus = false;
        }

        return authenticationStatus;
    }

    /**
     * Authenticate LDAP user
     * 
     * @param user
     *            user to be authenticated
     * @param password
     *            to be checked
     * 
     * @return True if authentication is success else false
     */
    public boolean authenticateLDAPUser(User user, String password, Server server) {

        log.debug("authenticateLDAPUser::" + user + " " + server.getName());

        DirContext dcon = getLDAPConnection(user.getUsername(), password, server);

        if (dcon == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Creates a connection to the LDAP server based on values defined in the configuration file.
     * This method logs into the server with a specified username and password
     * 
     * @param username
     *            The username to log into the LDAP server
     * @param password
     *            The password to log into the LDAP server
     * @return A connection to the LDAP server defined in the configuration file.
     * @throws ILSException
     *             if the username and password were wrong or we couldn't find the LDAP server
     */
    private static DirContext getLDAPConnection(String username, String password, Server loginserver) {

        Properties ldapProperties = getGenericLDAPProperties(loginserver);
        try {
            // Set up the environment for creating the initial context

            // Get the username attribute and start location on the LDAP server from the configuration file
            String usernameAttribute = loginserver.getUserNameAttribute();
            String startLocation = loginserver.getStartLocation();

            // Set up the properties to authenticate with the correct username and password
            // The username passed to this function will be something like "jsmith", but we
            // need to authenticate to the correct LDAP location using the provided parameter.
            // For this reason we pull the username attribute at start locations from the
            // configuration file. The result will be setting the SECURITY_PRINCIPAL (LDAP username)
            // to something like "uid=jsmith, ou=people, dc=rochester, dc=edu"
            ldapProperties.setProperty(Context.SECURITY_AUTHENTICATION, "simple"); // Set this property because we will be authenticating
            ldapProperties.setProperty(Context.SECURITY_PRINCIPAL, usernameAttribute + "=" + username + ", " + startLocation);
            ldapProperties.setProperty(Context.SECURITY_CREDENTIALS, password);

            // Get the environment properties (props) for creating initial
            // context and specifying LDAP service provider parameters.
            return new InitialDirContext(ldapProperties);
        }
        // catch(MalformedURLException e1) {} // not thrown in above, but I thought I saw this in the log with misconfigured ldap?
        // catch(AuthenticationException e2) {} // more specific then below, I think this is the one thrown if invalid password.
        catch (NamingException e) {
            // If the exception was an error code 49, the username or password was incorrect.
            log.error("Exception occured while authenticating user against LDAP server.If the exception was an error code 49, the username or password was incorrect", e);
            InitialDirContext in = null;
            return in;
        }
    }

    /**
     * Sets up the Properties used to create an LDAP connection
     * 
     * @return The Properties for an LDAP connection
     */
    private static Properties getGenericLDAPProperties(Server loginserver) {

        // Get important values from the configuration file for connecting to the LDAP server.
        String url = loginserver.getUrl();
        int port = loginserver.getPort();

        // Set up the environment for creating the initial context
        Properties ldapProperties = new Properties();
        ldapProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapProperties.setProperty(Context.PROVIDER_URL, url + ":" + port);

        return ldapProperties;
    }

    /**
     * User with group and permission data loaded
     * 
     * @param userId
     *            Id of user
     * 
     * @return User
     */
    public User getUserWithPermissions(int userId) throws DataException {
        User user = userDAO.getById(userId);

        return user;
    }

    /**
     * Sends email to the user with new password
     * 
     * @param newPassword
     *            New password
     * @param user
     *            user requesting reset password
     */
    public boolean sendEmailForForgotPassword(String newPassword, User user) {

        Emailer emailer = (Emailer) MSTConfiguration.getInstance().getBean("Emailer");

        boolean emailSent = emailer.sendEmail(user.getEmail(), "New password", "Password has been reset. \nUser name is : " + user.getUsername() + "\nNew password is : " + newPassword);

        return emailSent;
    }

    /**
     * Get user having specified email
     * 
     * @param email
     *            Email to get the user
     * @param server
     *            Login Server
     * @return User having specified email
     * @throws DatabaseConfigException
     */
    public User getUserByEmail(String email, Server server) throws DatabaseConfigException {
        return userDAO.getUserByEmail(email, server);
    }

    /**
     * Returns a sorted list of users
     * 
     * @param sort
     *            determines whether the list of users is sorted in ascending or descending order
     * @param columnSorted
     *            the column on which the rows of users are sorted
     * @return list of users
     * @throws DatabaseConfigException
     */
    public List<User> getAllUsersSorted(boolean sort, String columnSorted) throws DatabaseConfigException {
        return userDAO.getSorted(sort, columnSorted);
    }

    /**
     * Returns a list of user that are associated with a group
     * 
     * @param group
     *            group to get the users
     * @return list of user
     * @throws DatabaseConfigException
     */
    public List<User> getUsersForGroupSorted(int groupId, boolean sort, String columnSorted) throws DatabaseConfigException {
        return userGroupUtilDAO.getUsersForGroupSorted(groupId, sort, columnSorted);
    }

    /**
     * Returns a list of users associated with the group
     * 
     * @param groupId
     *            group ID
     * @return list of users
     * @throws DatabaseConfigException
     */
    public List<User> getUsersForGroup(int groupId) throws DatabaseConfigException {
        return userGroupUtilDAO.getUsersForGroup(groupId);
    }

    /**
     * Retrieves the number of users in a group
     * 
     * @param groupId
     *            group ID
     * @return member count
     */
    public int getUserCountForGroup(int groupId) {
        return userGroupUtilDAO.getUserCountForGroup(groupId);
    }

    /**
     * returns the number of LDAP users in the system
     * 
     * @return number of LDAP users
     * @throws DatabaseConfigException
     */
    public int getLDAPUserCount() throws DatabaseConfigException {
        return userDAO.getLDAPUserCount();
    }

    /**
     * Sends email to all admins to give permission for a new user
     * 
     * @param userName
     *            User name of new user
     * @param comments
     *            Comments to get access to the system
     * @throws DatabaseConfigException
     */
    public boolean sendEmailForUserPermission(String userName, String comments) throws DatabaseConfigException {

        Emailer emailer = (Emailer) MSTConfiguration.getInstance().getBean("Emailer");

        // Email the admin to assign permissions for new user
        StringBuffer adminMessageBody = new StringBuffer();
        adminMessageBody.append("New account created in Metadata Services Toolkit with user name : " + userName);
        adminMessageBody.append("\nComments from the user : " + comments);
        adminMessageBody.append("\nPlease login into the system and assign appropriate permissions for the user.");
        String adminSubject = "Assign permission to new User";

        GroupService groupService = (GroupService) config.getBean("GroupService");
        List<User> admins = getUsersForGroup(groupService.getGroupByName(Group.ADMINISTRATOR).getId());

        boolean emailConfigured = emailer.isConfigured();

        if (!emailConfigured) {
            log.debug("*** DefaultUserService: e-mail is not configured! ");

            return false;
        }

        for (User admin : admins) {
            emailer.sendEmail(admin.getEmail(), adminSubject, adminMessageBody.toString());
        }

        return true;
    }

    /**
     * Sends email to user to inform that following permissions has been assigned.
     * 
     * @param user
     *            User whose permissions has beedn changed/added/removed
     * @throws DatabaseConfigException
     */
    public boolean sendEmailToUserWithPermissions(User user) throws DatabaseConfigException {

        Emailer emailer = (Emailer) MSTConfiguration.getInstance().getBean("Emailer");

        // Email the user to inform that following permissions has been assigned.
        StringBuffer messageBody = new StringBuffer();
        messageBody.append("Hello " + user.getUsername() + ",");
        messageBody.append("\nYou have been asssigned permissions to access following tabs in Metadata Services Toolkit.");
        for (Permission permission : getPermissionsForUserByTabOrderAsc(user)) {
            messageBody.append("\n\t" + permission.getTabName());
        }

        String subject = "Permissions assigned to access Metadata Services Toolkit";

        boolean emailConfigured = new Emailer().isConfigured();

        if (!emailConfigured) {
            return false;
        }

        emailer.sendEmail(user.getEmail(), subject, messageBody.toString());

        return true;
    }

    /**
     * Sends email to all admins regarding a failure condition in the MST GUI
     * 
     * @param message
     *            The message which describes where the error took place.
     * @throws DatabaseConfigException
     */
    @SuppressWarnings("unchecked")
    public boolean sendEmailErrorReport() {

        try {
            String MESSAGE = "An error has occurred with the Metadata Services Toolkit.  Please submit this error here: http://code.google.com/p/xcmetadataservicestoolkit/issues/entry \n\n Please include the following information:\n Template: Defect report from user \n Summary: <error summary> \n Description: <copy paste the relevant error message from the attached log file here> \n";

            Emailer emailer = (Emailer) MSTConfiguration.getInstance().getBean("Emailer");

            String adminSubject = "MST error";

            GroupService groupService = (GroupService) config.getBean("GroupService");
            List<User> admins = getUsersForGroup(groupService.getGroupByName(Group.ADMINISTRATOR).getId());

            boolean emailConfigured = new Emailer().isConfigured();

            if (!emailConfigured) {
                return false;
            }

            // Get the log file path
            String filename = null;
            Enumeration<Appender> root = Logger.getRootLogger().getAllAppenders();

            while (root.hasMoreElements()) {
                Appender app = (Appender) root.nextElement();

                if (app instanceof RollingFileAppender) {
                    filename = ((RollingFileAppender) app).getFile();
                }
            }

            for (User admin : admins) {
                emailer.sendEmail(admin.getEmail(), adminSubject, MESSAGE, filename);
            }

            return true;
        } catch (DatabaseConfigException dce) {
            log.error("Error occured while emailing the error log file to admin.", dce);
            return false;
        }

    }

    /**
     * Get permissions for user ordered by tab order
     * 
     * @param user
     *            User to get permissions
     * @return user permissions
     * @throws DatabaseConfigException
     */
    public List<Permission> getPermissionsForUserByTabOrderAsc(User user) throws DatabaseConfigException {
        return permissionDAO.getPermissionsForUserByTabOrderAsc(user.getId());
    }
}
