/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action.user;

import java.util.List;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.email.Emailer;
import xc.mst.utils.MSTConfiguration;

/**
 * Action to register a user
 * 
 * @author Sharmila Ranganathan
 * 
 */
public class UserRegistration extends BaseActionSupport {

    /** Generated id */
    private static final long serialVersionUID = 5946519493525167816L;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** New user registering with the system */
    private User newUser;

    /** List of servers */
    private List<Server> servers;

    /** Comments entered by user */
    private String comments;

    /** Name of server selected */
    private String serverName;

    /** Error type */
    private String errorType;

    /** Object used to read properties from the default configuration file */
    protected static final Configuration defaultConfiguration = ConfigurationManager.getConfiguration();

    /** Indicates if error in configuration */
    public boolean configurationError = false;

    /**
     * To register the user
     */
    @Override
    public String execute() {

        try {
            servers = getServerService().getAll();
        } catch (DatabaseConfigException dce) {
            if (!MSTConfiguration.mstInstanceFolderExist) {
                addFieldError("instancesFolderError", defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + " folder is missing under tomcat working directory. Please refer to MST installation manual for configuring correctly.");
                log.error(defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + " folder is missing under tomcat working directory. Please refer to MST installation manual for configuring correctly.");
            } else if (!MSTConfiguration.currentInstanceFolderExist) {
                int beginIndex = MSTConfiguration.getUrlPath().indexOf(MSTConfiguration.FILE_SEPARATOR);
                String instanceFolderName = MSTConfiguration.getUrlPath().substring(beginIndex + 1);
                addFieldError("currentInstancesFolderError", instanceFolderName + " folder is missing under &lt;tomcat-working-directory&gt;/" + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + ". Please refer to MST installation manual for configuring correctly.");
                log.error(instanceFolderName + " folder is missing under &lt;tomcat-working-directory&gt;/" + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + ". Please refer to MST installation manual for configuring correctly.");
            } else {
                log.error(dce.getMessage(), dce);
                addFieldError("dbConfigError", "Unable to access the database to get Server type information. There may be problem with database configuration.");
            }
            configurationError = true;
            errorType = "error";
            return INPUT;

        }

        return SUCCESS;
    }

    /**
     * Registers the new user
     * 
     * @return
     */
    public String registerUser() {

        try {
            Server server = getServerService().getServerByName(serverName);
            newUser.setServer(server);

            // Check if user name already exist
            User otherUser = getUserService().getUserByUserName(newUser.getUsername().trim(), server);
            if (otherUser == null) {
                // Check if email id already exist
                User otherUserWithSameEmail = getUserService().getUserByEmail(newUser.getEmail().trim(), server);

                if (otherUserWithSameEmail == null) {

                    // Check if user entered password for LDAP server is valid.
                    if (!server.getName().equalsIgnoreCase("local")) {
                        if (!getUserService().authenticateLDAPUser(newUser, newUser.getPassword(), server)) {
                            addFieldError("authenticationError", "Password entered did not match the password in " + server.getName() + " account.");
                            errorType = "error";
                            servers = getServerService().getAll();
                            return INPUT;
                        }
                        // LDAP user's password need not be stored.
                        newUser.setPassword(null);
                    }

                    boolean emailSent = false;

                    // Email the user
                    Emailer emailer = (Emailer) MSTConfiguration.getInstance().getBean("Emailer");
                    StringBuffer messageBody = new StringBuffer();
                    messageBody.append("An account has been created successfully in Metadata Services Toolkit with the user name of \"" + newUser.getUsername().trim() + "\".\n");
                    messageBody.append("You will be able to login once a system admin assigns permissions for your account.");
                    String subject = "Your new Metadata Services Toolkit account";

                    emailSent = emailer.sendEmail(newUser.getEmail().trim(), subject, messageBody.toString());

                    if (!emailSent) {
                        servers = getServerService().getAll();
                        StringBuffer errorMessage = new StringBuffer();
                        errorMessage.append("E-mail is not configured for the application. E-mail should be setup for user registration.");
                        addFieldError("emailError", errorMessage.toString());
                        errorType = "error";
                        return INPUT;
                    }

                    // Insert only after email verification is sent
                    getUserService().insertUser(newUser);

                    // Email the admin to assign permissions for new user
                    boolean permissionEmailSent = getUserService().sendEmailForUserPermission(newUser.getUsername().trim(), comments);
                    if (!permissionEmailSent) {
                        log.error("** Your account has been set up, but failed to mail a message to the admin account!");
                        StringBuffer errorMessage = new StringBuffer();
                        errorMessage.append("Your account has been created successfully in Metadata Services Toolkit but there was a problem e-mailing the admin account.");
                        addFieldError("emailError", errorMessage.toString());
                        errorType = "error";
                        servers = getServerService().getAll();
                        return INPUT;
                    }
                } else {
                    servers = getServerService().getAll();
                    addFieldError("userEmailExist", "This email address already exists in the system.- " + newUser.getEmail().trim());
                    errorType = "error";
                    return INPUT;
                }

            } else {
                servers = getServerService().getAll();
                addFieldError("userNameExist", "User name already exists - " + newUser.getUsername().trim());
                errorType = "error";
                return INPUT;
            }
        } catch (DataException e) {
            log.error("Exception occured while saving user registration information", e);
            addFieldError("dataError", e.getMessage());
            errorType = "error";
            return INPUT;
        }
        return SUCCESS;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public List<Server> getServers() {
        return servers;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public boolean isConfigurationError() {
        return configurationError;
    }

}
