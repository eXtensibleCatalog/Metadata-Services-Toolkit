/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action.user;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * This action method is used to add a new local user
 * 
 * @author Tejaswi Haramurali
 */
public class AddLocalUser extends BaseActionSupport {
    /** Serial id */
    private static final long serialVersionUID = 8341069230872861667L;

    /** The email ID of the user */
    private String email;

    /** The password of the user */
    private String password;

    /** The username of the user */
    private String userName;

    /** The first Name of the user */
    private String firstName;

    /** The Last Name of the user */
    private String lastName;

    /** The groups that have been assigned to the new user */
    private String[] groupsSelected;

    /** The list of all groups in the system */
    private List<Group> groupList;

    /** This User object is used to pre-fill JSP form fields */
    private User temporaryUser;

    /** Provides a list of selected group IDs which are used to pre-fill JSP form fields */
    private String[] selectedGroups;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Error type */
    private String errorType;

    /**
     * Overrides default implementation to view the add local user page.
     * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute() {
        try {
            setGroupList(getGroupService().getAllGroups());
            return SUCCESS;
        } catch (DatabaseConfigException dce) {
            log.error(dce.getMessage(), dce);
            this.addFieldError("addLocalUserError", "Unable to connect to the database. Database Configuration is incorrect.");
            errorType = "error";
            return ERROR;
        }
    }

    /**
     * The action method that actually does the task of adding a new local user to the system
     * 
     * @return returns status of the add operation
     */
    public String addLocalUser() {
        try {
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            user.setAccountCreated(new Date());
            user.setFailedLoginAttempts(0);
            user.setUsername(userName);
            Server localServer = getServerService().getServerByName("Local");
            user.setServer(localServer);

            User similarUserName = getUserService().getUserByUserName(user.getUsername(), localServer);
            User similarEmail = getUserService().getUserByEmail(email, localServer);
            if (similarUserName != null) {
                if (similarUserName.getServer().getName().equalsIgnoreCase("Local")) {
                    this.addFieldError("addLocalUserError", "Username already exists");
                    errorType = "error";
                    setGroupList(getGroupService().getAllGroups());
                    setTemporaryUser(user);
                    setSelectedGroups(groupsSelected);
                    return INPUT;
                }
            }
            if (similarEmail != null) {
                if (similarEmail.getServer().getName().equalsIgnoreCase("Local")) {
                    this.addFieldError("addLocalUserError", "Email ID already exists");
                    errorType = "error";
                    setGroupList(getGroupService().getAllGroups());
                    setTemporaryUser(user);
                    setSelectedGroups(groupsSelected);
                    return INPUT;
                }
            }

            for (int i = 0; i < groupsSelected.length; i++) {
                Group tempGroup = getGroupService().getGroupById(Integer.parseInt(groupsSelected[i]));
                user.addGroup(tempGroup);
            }
            getUserService().insertUser(user);
            return SUCCESS;
        } catch (DatabaseConfigException dce) {
            log.error(dce.getMessage(), dce);
            this.addFieldError("addLDAPUserError", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return ERROR;
        } catch (DataException de) {
            log.error(de.getMessage(), de);
            this.addFieldError("addLDAPUserError", "Error in adding local user. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return ERROR;
        }

    }

    /**
     * Returns error type
     * 
     * @return error type
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Sets error type
     * 
     * @param errorType
     *            error type
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * Returns the first name of the user
     * 
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user
     * 
     * @param firstName
     *            first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    /**
     * Returns the last name of the user
     * 
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user
     * 
     * @param lastName
     *            last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    /**
     * Sets temporary user object
     * 
     * @param user
     *            temporary user object
     */
    public void setTemporaryUser(User user) {
        this.temporaryUser = user;
    }

    /**
     * Returns temporary user object
     * 
     * @return temporary user
     */
    public User getTemporaryUser() {
        return temporaryUser;
    }

    /**
     * Sets the user name for the local user
     * 
     * @param userName
     *            user Name
     */
    public void setUserName(String userName) {
        this.userName = userName.trim();
    }

    /**
     * Returns the local name for the user
     * 
     * @return user name
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Assigns the list of groups that a user can belong to
     * 
     * @param groupList
     *            list of groups
     */
    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    /**
     * Returns a list of groups that a user can belong to
     * 
     * @return list of groups
     */
    public List<Group> getGroupList() {
        return groupList;
    }

    /**
     * Sets the email ID of the user
     * 
     * @param email
     *            email ID
     */
    public void setEmail(String email) {
        this.email = email.trim();
    }

    /**
     * Returns the email ID of the user
     * 
     * @return email ID
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the password of the user
     * 
     * @param password
     *            The password to be assigned
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the password of the user
     * 
     * @return user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the list of groups that the user has been assigned
     * 
     * @param selectedGroupList
     *            list of selected groups
     */
    public void setGroupsSelected(String[] groupsSelected) {
        this.groupsSelected = groupsSelected;
    }

    /**
     * Returns the list of groups that have been assigned to the user
     * 
     * @return list of selected groups
     */
    public String[] getGroupsSelected() {
        return groupsSelected;
    }

    /**
     * Sets the list of groups that the user has been assigned (used to pre-fill JSP form fields)
     * 
     * @param selectedGroupList
     *            list of selected groups
     */
    public void setSelectedGroups(String[] selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    /**
     * Returns the list of groups that have been assigned to the user (used to pre-fill JSP form fields)
     * 
     * @return list of selected groups
     */
    public String[] getSelectedGroups() {
        return selectedGroups;
    }

}
