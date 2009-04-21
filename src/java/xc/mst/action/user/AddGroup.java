
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
import xc.mst.constants.Constants;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultPermissionService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.PermissionService;


/**
 * This action method is used to add a new group of users
 *
 * @author Tejaswi Haramurali
 */
public class AddGroup extends ActionSupport
{
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
    List tabNames;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType; 
	


    public AddGroup()
    {
        tabNames = new ArrayList();
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
    public void setTabNames(List tabNames)
    {
        this.tabNames = tabNames;
    }

    /**
     * Returns a list of all the tab names in the system
     *
     * @return list of tab names
     */
    public List getTabNames()
    {
        return tabNames;
    }

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
            String[] tabs = {"Repositories","Harvest","Services","Browse Records","Logs","Users/Groups","Configuration","Search Index"};
            for(int i=0;i<tabs.length;i++)
            {
                tabNames.add(tabs[i]);
            }
            setTabNames(tabNames);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            this.addFieldError("addGroupError", "The Page could not be displayed correctly");
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
            GroupService groupService = new DefaultGroupService();
            PermissionService permissionService = new DefaultPermissionService();

            Group group = new Group();
            group.setName(getGroupName());
            group.setDescription(getGroupDescription());

            List<Group> tempGrpList = groupService.getAllGroups();
            Iterator<Group> iter = tempGrpList.iterator();

            while(iter.hasNext())
            {
                Group tempGroup = (Group)iter.next();
                if(tempGroup.getName().equalsIgnoreCase(groupName))
                {
                    setTemporaryGroup(group);
                    String[] tabs = {"Repositories","Harvest","Services","Browse Records","Logs","Users/Groups","Configuration","Search Index"};
                    for(int i=0;i<tabs.length;i++)
                    {
                        tabNames.add(tabs[i]);
                    }
                    setTabNames(tabNames);
                    setSelectedPermissions(permissionsSelected);
                    this.addFieldError("addGroupError", "A group with the same name already exists");
                    errorType = "error";
                    return INPUT;
                }
            }
            

            for(int i=0;i<permissionsSelected.length;i++)
            {
                int permissionId = Integer.parseInt(permissionsSelected[i]);
                group.addPermission(permissionService.getPermissionById(permissionId));
            }

            groupService.insertGroup(group);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            this.addFieldError("addGroupError", "Group could not be added properly");
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
}
