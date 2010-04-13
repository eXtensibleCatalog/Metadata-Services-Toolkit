
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
import java.util.*;
import org.apache.log4j.Logger;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultPermissionService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.PermissionService;
import xc.mst.manager.user.UserService;


/**
 * This action method is used to add a new group of users
 *
 * @author Tejaswi Haramurali
 */
public class AddGroup extends ActionSupport
{
    /** Serial id */
	private static final long serialVersionUID = -1479234838280649053L;

	/** The name of the group */
    private String groupName;

    /** A Description of the group */
    private String groupDescription;

    /**The permissions that have been assigned to the group */
    private String[] permissionsSelected;

    /**A temporary group object that is used to pre-fill  fields in a JSP page*/
    private Group temporaryGroup;

    /** A String array that is used to store permissions that have been selected by the user*/
    private String[] selectedPermissions;

    /** The list of all the tab names */
    private List<Permission> tabNames;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType;

    /**Group Service object */
    private GroupService groupService = new DefaultGroupService();

    /** Permission Service object */
    private PermissionService permissionService = new DefaultPermissionService();

    /**User Service object */
    private UserService userService = new DefaultUserService();

     /**
     * Overrides default implementation to view the add group page.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            setTabNames(permissionService.getAllPermissions());
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("addGroupError", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Method that actually adds a group to the system
     *
     * @return {@link #SUCCESS}
     */
    public String addGroup()
    {
        try
        {
            
            Group group = new Group();
            group.setName(groupName);
            group.setDescription(groupDescription);
            
            Group tempGroup = groupService.getGroupByName(groupName);
            if(tempGroup!=null)
            {
                
                setTemporaryGroup(group);
                setTabNames(permissionService.getAllPermissions());
                this.addFieldError("addGroupError", "A group with the same name already exists");
                errorType = "error";
                return INPUT;
                
            }
            

            for(int i=0;i<permissionsSelected.length;i++)
            {
                
                int permissionId = Integer.parseInt(permissionsSelected[i]);
                
                Permission tempPermission = permissionService.getPermissionById(permissionId);
                
                group.addPermission(tempPermission);
            }

            groupService.insertGroup(group);
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("addGroupError", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("addGroupError", "Error Occurred while adding group. An email has been sent to the administrator.");
            userService.sendEmailErrorReport();
            errorType = "error";
            return INPUT;
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
     * Sets the group name to the specified value.
     *
     * @param groupName The name of the group
     */
    public void setGroupName(String groupName)
    {
        this.groupName = groupName.trim();
    }

    /**
     * Returns the name of the group
     *
     * @return group Name
     */
    public String getGroupName()
    {
        return this.groupName;
    }

    /**
     * Sets the description of the group
     *
     * @param groupDescription group description
     */
    public void setGroupDescription(String groupDescription)
    {
        this.groupDescription = groupDescription.trim();
    }

    /**
     * Returns the description of the group
     *
     * @return group description
     */
    public String getGroupDescription()
    {
        return this.groupDescription;
    }

    /**
     * Sets the permissions that have been allotted to this group
     *
     * @param permissionsSelected permissions selected
     */
    public void setPermissionsSelected(String[] permissionsSelected)
    {
        this.permissionsSelected = permissionsSelected;
    }

    /**
     * Returns the permissions that have been allotted to the group
     *
     * @return permissions list
     */
    public String[] getPermissionsSelected()
    {
        return this.permissionsSelected;
    }

    /**
     * Sets a temporary group object that is used to pre-fill fields in a JSP form
     *
     * @param group group Object
     */
    public void setTemporaryGroup(Group group)
    {
        this.temporaryGroup = group;
    }
    /**
     * Returns the temporary group object
     *
     * @return group object
     */
    public Group getTemporaryGroup()
    {
        return temporaryGroup;
    }

    /**
     * The list of permissions that have already been selected by the user
     *
     * @param selectedPermissions List of selected permissions
     */
    public void setSelectedPermissions(String[] selectedPermissions)
    {
        this.selectedPermissions = selectedPermissions;
    }

    /**
     * Returns the list of selected permissions
     *
     * @return List of permissions
     */
    public String[] getSelectedPermissions()
    {
        return selectedPermissions;
    }

    /**
     * Sets the List of all tab names in the system.
     *
     * @param tabNames tab names
     */
    public void setTabNames(List<Permission> tabNames)
    {
        this.tabNames = tabNames;
    }

    /**
     * Returns a list of all the tab names in the system
     *
     * @return list of tab names
     */
    public List<Permission> getTabNames()
    {
        return tabNames;
    }
}
