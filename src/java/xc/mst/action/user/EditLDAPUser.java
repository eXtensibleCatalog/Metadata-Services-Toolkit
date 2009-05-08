
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;

/**
 * This action method is used to edit the details of an LDAP user
 *
 * @author Tejaswi Haramurali
 */
public class EditLDAPUser extends ActionSupport
{
    /** creates service object for servers */
    private  ServerService serverService = new DefaultServerService();

    /** creates service object for groups */
    private GroupService groupService = new DefaultGroupService();

    /** creates service object for users */
    private UserService userService = new DefaultUserService();

    /**ID of the LDAP User to be edited */
    private int userId;

     /**The email ID of the user */
    private String email;

    /**The first Name of the user  */
    private String firstName;

    /**The last Name of the user  */
    private String lastName;

    /**The groups that have been assigned to the new user */
    private String[] groupsSelected;

    /**The list of all groups in the system */
    private List<Group> groupList;

    /** The list of groups that are already associated with the user **/
    private String[] selectedGroups;

    /**The temporary user object that is used to populate JSP form fields **/
    private User temporaryUser;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	
     /**
     * Overrides default implementation to view the 'add NCIP user' page.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            User user = userService.getUserById(userId);
            setTemporaryUser(user);
            setGroupList(groupService.getAllGroups());            
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug("Edit LDAP User Page not displayed correctly",e);
            this.addFieldError("addLDAPUserError","Page not displayed correctly");
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * Method that edits the details of the LDAP user
     *
     * @return {@link #SUCCESS}
     */
    public String editLDAPUser()
    {
        try
        {            
            User user = userService.getUserById(userId);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(null);
            user.setAccountCreated(new Date());
            user.setFailedLoginAttempts(0);

            List<Server> serverList = serverService.getAll();
            boolean serverExists = false;
            Server tempServer = null;
            Iterator<Server> iter = serverList.iterator();
            while(iter.hasNext())
            {
                tempServer = (Server)iter.next();
                if(tempServer.getType()!=Server.ServerType.LOCAL)
                {
                    serverExists = true;
                    break;
                }
            }

            if(serverExists==false)
            {
                this.addFieldError("addLDAPUserError","Error : NO LDAP Server has been configured");
                errorType = "error";
                return ERROR;
            }
            user.setServer(tempServer);
            user.setLastLogin(new Date());

            user.removeAllGroups();
            for(int i=0;i<groupsSelected.length;i++)
            {
                Group group = groupService.getGroupById(Integer.parseInt(groupsSelected[i]));
                user.addGroup(group);
            }

            User similarEmail = userService.getUserByEmail(email, tempServer);
            if(similarEmail!=null)
            {
                if(similarEmail.getId()!=userId)
                {
                    if(!similarEmail.getServer().getName().equalsIgnoreCase("Local"))
                    {
                        this.addFieldError("editLDAPUserError","Error : Email ID already exists");
                        errorType = "error";
                        setGroupList(groupService.getAllGroups());
                        setTemporaryUser(user);
                        setSelectedGroups(groupsSelected);
                        return INPUT;
                    }
                }
            }
            userService.updateUser(user);


            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug("User not Added correctly",e);
            this.addFieldError("editLDAPUserError","User not Added correctly");
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
     * @param errorType error type
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
     * @param firstName first name
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
     * @param lastName last name
     */
	public void setLastName(String lastName) {
		this.lastName = lastName.trim();
	}

     /**
     * Assigns the list of groups that a user can belong to
     *
     * @param groupList list of groups
     */
    public void setGroupList(List<Group> groupList)
    {
        this.groupList = groupList;
    }

    /**
     * Returns a list of groups that a user can belong to
     *
     * @return list of groups
     */
    public List<Group> getGroupList()
    {
        return groupList;
    }

    /**
     * Sets the email ID of the user
     *
     * @param email email ID
     */
    public void setEmail(String email)
    {
        this.email = email.trim();
    }

    /**
     * Returns the email ID of the user
     *
     * @return email ID
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the list of groups that the user has been assigned
     *
     * @param selectedGroupList list of selected groups
     */
    public void setGroupsSelected(String[] groupsSelected)
    {
        this.groupsSelected = groupsSelected;
    }

    /**
     * Returns the list of groups that have been assigned to the user
     *
     * @return list of selected groups
     */
    public String[] getGroupsSelected()
    {
        return groupsSelected;
    }

    /**
     * Sets the list of groups that the user has been assigned (used to pre-fill JSP form fields)
     *
     * @param selectedGroupList list of selected groups
     */
    public void setSelectedGroups(String[] selectedGroups)
    {
        this.selectedGroups = selectedGroups;
    }

    /**
     * Returns the list of groups that have been assigned to the user (used to pre-fill JSP form fields)
     *
     * @return list of selected groups
     */
    public String[] getSelectedGroups()
    {
        return selectedGroups;
    }

    /**
     * Sets the temporary user object which is used to populate JSP fields
     *
     * @param user temporary user object
     */
    public void setTemporaryUser(User user)
    {
        this.temporaryUser = user;
    }

    /**
     * Returns the temporary user object
     *
     * @return user object
     */
    public User getTemporaryUser()
    {
        return temporaryUser;
    }

    /**
     * Sets the user ID of the user whose details should be edited
     *
     * @param userId user ID
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * Returns user ID of the user whose details are to be edited
     *
     * @return user ID
     */
    public int getUserId()
    {
        return userId;
    }

}
