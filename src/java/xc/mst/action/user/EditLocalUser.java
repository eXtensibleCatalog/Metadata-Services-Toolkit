
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
 * This action method is used to edit the details of a user
 *
 * @author Tejaswi Haramurali
 */
public class EditLocalUser extends ActionSupport
{
     /** creates service object for users */
     private UserService userService = new DefaultUserService();

      /** creates service object for groups */
     private GroupService groupService = new DefaultGroupService();

      /** creates service object for servers */
     private ServerService serverService = new DefaultServerService();

    /**The ID of the user whose details are to be edited */
    private String userId;

    /**A user object that is used to pre-fill JSP form fields. */
    private User temporaryUser;

    /**List of all the groups in the system */
    private List<Group> groupList;

    /**The username of the user */
    private String userName;

    /**The email ID of the user */
    private String email;

    /** The password of the user */
    private String password;

    /**The Full Name of the user which includes First Name and Last Name */
    private String fullName;

    /**The groups that have been assigned to the new user */
    private String[] groupsSelected;


     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	

    /**
     * sets the user name for the local user
     * @param userName user Name
     */
    public void setUserName(String userName)
    {
        this.userName = userName.trim();
    }

    /**
     * returns the local name for the user
     * @return user name
     */
    public String getUserName()
    {
        return this.userName;
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
     * sets the password of the user
     * @param password The password to be assigned
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * returns the password of the user
     * @return user's password
     */
    public String getPassword()
    {
        return password;
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
     * sets the list of groups that the new user has been assigned
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
     * sets the user whose details are to be edited
     * @param user user object
     */
    public void setTemporaryUser(User user)
    {
        this.temporaryUser = user;
    }

    /**
     * returns the user whose details are to be edited.
     * @return user object
     */
    public User getTemporaryUser()
    {
        return temporaryUser;
    }

    /**
     * sets the ID of the user whose details are to be edited.
     * @param userId user ID
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * returns the ID of the user whose details are to be edited.
     * @return user ID
     */
    public String getUserId()
    {
        return userId;
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
     * Overrides default implementation to view the edit local user page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            setGroupList(groupService.getAllGroups());
            User user = userService.getUserById(Integer.parseInt(userId));
            setTemporaryUser(user);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("editLocalUserError", "Error in displaying the user's details");
            errorType = "error";
            return ERROR;
        }
    }

    /**
     * The action method that actually does the task of editing the details of a local user
     * @return returns status of the edit operation
     */
    public String editLocalUser()
    {
        try
        {
           
            setGroupList(groupService.getAllGroups());
            User user = userService.getUserById(Integer.parseInt(userId));
            user.setUsername(userName);
            user.setServer(serverService.getServerByName("Local"));
            user.setEmail(email);
            user.setFailedLoginAttempts(0);
            user.setFullName(fullName);

            if (!user.getPassword().equals(password)) {
            	user.setPassword(userService.encryptPassword(password));
            } else {
            	user.setPassword(password);
            }

            List<Group> tempGrpList = new ArrayList();
            for(int i=0;i<groupsSelected.length;i++)
            {
                Group group = groupService.getGroupById(Integer.parseInt(groupsSelected[i]));
                tempGrpList.add(group);
            }
            user.setGroups(tempGrpList);

            User similarUserName = userService.getUserByUserName(user.getUsername(), serverService.getServerByName("Local"));
            User similarEmail = userService.getUserByEmail(email, serverService.getServerByName("Local"));
            if(similarUserName!=null)
            {
                if(similarUserName.getId()!=Integer.parseInt(userId))
                {
                    if(similarUserName.getServer().getName().equalsIgnoreCase("Local"))
                    {
                        this.addFieldError("editLocalUserError","Error : Username already exists");
                        errorType = "error";
                        setGroupList(groupService.getAllGroups());
                        setTemporaryUser(user);
                        return INPUT;
                    }
                }
            }
            if(similarEmail!=null)
            {
                if(similarEmail.getId()!=Integer.parseInt(userId))
                {
                    if(similarEmail.getServer().getName().equalsIgnoreCase("Local"))
                    {
                        this.addFieldError("editLocalUserError","Error : Email ID already exists");
                        errorType = "error";
                        setGroupList(groupService.getAllGroups());
                        setTemporaryUser(user);
                        return INPUT;
                    }
                }
            }

            userService.updateUser(user);

            UserGroupUtilService UGUtilService = new DefaultUserGroupUtilService();
            UGUtilService.deleteGroupsForUserId(Integer.parseInt(userId));

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
            this.addFieldError("editLocalUserError","Error : User details not updated correctly");
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
