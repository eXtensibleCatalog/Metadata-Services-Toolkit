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
import java.util.ArrayList;
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
import xc.mst.manager.user.DefaultUserGroupUtilService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserGroupUtilService;
import xc.mst.manager.user.UserService;

/**
 * This action method is used to add an LDAP User to the system
 *
 * @author Tejaswi Haramurali
 */
public class AddLDAPUser extends ActionSupport
{
    /** creates service object for users  */
    private UserService userService = new DefaultUserService();

    /** creates service object for servers */
    private ServerService serverService = new DefaultServerService();

    /** creates service object for groups */
    private GroupService groupService = new DefaultGroupService();

    /**The username of the user */
    private String userName;

     /**The email ID of the user */
    private String email;

    /**The Full Name of the user which includes First Name and Last Name */
    private String fullName;

    /**Comments that help the administrator recognise the new user */
    private String comments;

    /**The groups that have been assigned to the new user */
    private String[] groupsSelected;

    /**The list of all groups in the system */
    private List<Group> groupList;

    /** List of servers in the system **/
    private List<Server> serversList;

    /** The list of groups that are already associated with the user **/
    private String[] selectedGroups;

    /**The temporary user object that is used to populate JSP form fields **/
    private User temporaryUser;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);


	/** Error type */
	private String errorType; 
	


    /**
     * sets list of all servers
     * @param serversList server list
     */
    public void setServersList(List<Server> serversList)
    {
        this.serversList = serversList;
    }
    /**
     * returns the list of all servers
     * @return server list
     */
    public List<Server> getServersList()
    {
        return serversList;
    }
    /**
     * assigns the list of groups that a user can belong to
     * @param groupList list of groups
     */
    public void setGroupList(List<Group> groupList)
    {
        this.groupList = groupList;
    }

    /**
     * returns a list of groups that a user can belong to
     * @return list of groups
     */
    public List<Group> getGroupList()
    {
        return groupList;
    }

    /**
     * sets the email ID of the user
     * @param email email ID
     */
    public void setEmail(String email)
    {
        this.email = email.trim();
    }

    /**
     * returns the email ID of the user
     * @return email ID
     */
    public String getEmail()
    {
        return email;
    }



    /**
     * sets the Full Name of the user
     * @param fullName user's full name
     */
    public void setFullName(String fullName)
    {
        this.fullName = fullName.trim();
    }

    /**
     * returns the fullname of the user
     * @return user's full name
     */
    public String getFullName()
    {
        return fullName;
    }

    /**
     * sets the username of the LDAP user
     * @param userName user name
     */
    public void setUserName(String userName)
    {
        this.userName = userName.trim();
    }

    /**
     * returns the user name of the user
     * @return user Name
     */
    public String getUserName()
    {
        return userName;
    }
     /**
     * sets the list of groups that the user has been assigned
     * @param selectedGroupList list of selected groups
     */
    public void setGroupsSelected(String[] groupsSelected)
    {
        this.groupsSelected = groupsSelected;
    }

    /**
     * returns the list of groups that have been assigned to the user
     * @return list of selected groups
     */
    public String[] getGroupsSelected()
    {
        return groupsSelected;
    }

    /**
     * sets the list of groups that the user has been assigned (used to pre-fill JSP form fields)
     * @param selectedGroupList list of selected groups
     */
    public void setSelectedGroups(String[] selectedGroups)
    {
        this.selectedGroups = selectedGroups;
    }

    /**
     * returns the list of groups that have been assigned to the user (used to pre-fill JSP form fields)
     * @return list of selected groups
     */
    public String[] getSelectedGroups()
    {
        return selectedGroups;
    }

    /**
     * sets the temporary user object which is used to populate JSP form fields
     * @param user LDAP user object
     */
    public void setTemporaryUser(User user)
    {
        this.temporaryUser = user;
    }
    /**
     * returns the temporary user object
     * @return user object
     */
    public User getTemporaryUser()
    {
        return temporaryUser;
    }


     /**
     * Overrides default implementation to view the 'add NCIP user' page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            setGroupList(groupService.getAllGroups());
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("addLDAPUserError","Error : Page not displayed correctly");
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * This is the action method that actually inserts a new NCIP user into the system
     * @return {@link #SUCCESS}
     */
    public String addLDAPUser()
    {
        try
        {
            
            User user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setGroups(null);
            user.setPassword(null);
            user.setAccountCreated(new Date());
            user.setFailedLoginAttempts(0);
            user.setUsername(userName);

            List<Server> serverList = serverService.getAll();
            boolean serverExists = false;
            Server tempServer = null;
            Iterator iter = serverList.iterator();
            while(iter.hasNext())
            {
                tempServer = (Server)iter.next();
                if(tempServer.getType()!=4)
                {
                    serverExists = true;
                    break;
                }
            }

            if(serverExists==false)
            {
                this.addFieldError("addLDAPUserError","Error : NO LDAP Server has been configured");
                errorType = "error";
                setGroupList(groupService.getAllGroups());
                setTemporaryUser(user);
                setSelectedGroups(groupsSelected);
                return INPUT;
            }
            user.setServer(tempServer);
            user.setLastLogin(new Date());


            User similarUserName = userService.getUserByUserName(user.getUsername(), tempServer);
            User similarEmail = userService.getUserByEmail(email, tempServer);
            if(similarUserName!=null)
            {
                if(!similarUserName.getServer().getName().equalsIgnoreCase("Local"))
                {
                    this.addFieldError("addLDAPUserError","Error : Username already exists");
                    errorType = "error";
                    setGroupList(groupService.getAllGroups());
                    setTemporaryUser(user);
                    setSelectedGroups(groupsSelected);
                    return INPUT;
                }
            }
            if(similarEmail!=null)
            {
                if(!similarEmail.getServer().getName().equalsIgnoreCase("Local"))
                {
                    this.addFieldError("addLDAPUserError","Error : Email ID already exists");
                    errorType = "error";
                    setGroupList(groupService.getAllGroups());
                    setTemporaryUser(user);
                    setSelectedGroups(groupsSelected);
                    return INPUT;
                }
            }
            userService.insertUser(user);
            UserGroupUtilService UGUtilService = new DefaultUserGroupUtilService();
            for(int i=0;i<groupsSelected.length;i++)
            {

                Group tempGroup = groupService.getGroupById(Integer.parseInt(groupsSelected[i]));
                UGUtilService.insertUserGroup(user.getId(), tempGroup.getId());
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("addLDAPUserError","Error : User not Added correctly");
            errorType = "error";
            return ERROR;
        }
    }
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
