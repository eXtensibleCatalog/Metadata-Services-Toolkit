/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * This action method is used to edit the details of a group of users
 * 
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class EditGroup extends BaseActionSupport {
    /** The ID of the group whose details are to be edited */
    private int groupId;

    /** The name of the group */
    private String groupName;

    /** A Description of the group */
    private String groupDescription;

    /** The permissions that have been assigned to the group */
    private String[] permissionsSelected;

    /** A temporary group object that is used to pre-fill fields in a JSP page */
    private Group temporaryGroup;

    /** A List that is used to store permissions that have been selected by the user */
    @SuppressWarnings("unchecked")
    private List selectedPermissions = new ArrayList();

    /** The list of all the tab names */
    private List<Permission> tabNames = new ArrayList<Permission>();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Error type */
    private String errorType;

    /**
     * Overrides default implementation to view the edit group page.
     * 
     * @return {@link #SUCCESS}
     */
    @SuppressWarnings("unchecked")
    @Override
    public String execute() {
        try {

            Group group = getGroupService().getGroupById(groupId);
            if (group == null) {
                this.addFieldError("editGroupError", "Error Occurred while viewing edit group page. An email has been sent to the administrator.");
                getUserService().sendEmailErrorReport();
                errorType = "error";
                return INPUT;
            }

            setTemporaryGroup(group);
            Iterator<Permission> tempIter = group.getPermissions().iterator();
            while (tempIter.hasNext()) {
                Permission permission = (Permission) tempIter.next();
                selectedPermissions.add(permission.getTabId());
            }

            setTabNames(getPermissionService().getAllPermissions());
            return SUCCESS;
        } catch (DatabaseConfigException dce) {
            log.debug(dce.getMessage(), dce);
            this.addFieldError("editGroupError", "Unable to connect to the database. Database configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Method that actually edits the details of the group
     * 
     * @return {@link #SUCCESS}
     */
    @SuppressWarnings("unchecked")
    public String editGroup() {
        try {
            Group group = getGroupService().getGroupById(getGroupId());
            if (group == null) {
                this.addFieldError("editGroupError", "Error Occurred while editing group details. An email has been sent to the administrator.");
                getUserService().sendEmailErrorReport();
                errorType = "error";
                return INPUT;
            }
            group.setDescription(getGroupDescription());
            group.setName(getGroupName());

            Group tempGroup = getGroupService().getGroupByName(groupName);

            if (tempGroup.getId() != groupId) {
                if (tempGroup.getName().equalsIgnoreCase(groupName)) {

                    setTemporaryGroup(group);

                    setTabNames(getPermissionService().getAllPermissions());
                    for (int i = 0; i < permissionsSelected.length; i++) {
                        selectedPermissions.add(permissionsSelected[i]);
                    }
                    setSelectedPermissions(selectedPermissions);
                    this.addFieldError("editGroupError", "Error : A group with the same name already exists");
                    errorType = "error";
                    return INPUT;

                }
            }

            group.removeAllPermissions();
            for (int i = 0; i < permissionsSelected.length; i++) {

                int permissionId = Integer.parseInt(permissionsSelected[i]);
                group.addPermission(getPermissionService().getPermissionById(permissionId));
            }

            getGroupService().updateGroup(group);
            return SUCCESS;
        } catch (DatabaseConfigException dce) {
            log.error(dce.getMessage(), dce);
            this.addFieldError("editGroupError", "Unable to connect to the database. Database Configuration may be incorrect");
            errorType = "error";
            return INPUT;
        } catch (DataException de) {
            log.error(de.getMessage(), de);
            this.addFieldError("editGroupError", "Error Occurred while editing group. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
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
     * @param errorType
     *            error type
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * Sets the group name to the specified value.
     * 
     * @param groupName
     *            The name of the group
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName.trim();
    }

    /**
     * Returns the name of the group
     * 
     * @return group Name
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * Sets the description of the group
     * 
     * @param groupDescription
     *            group description
     */
    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription.trim();
    }

    /**
     * Returns the description of the group
     * 
     * @return group description
     */
    public String getGroupDescription() {
        return this.groupDescription;
    }

    /**
     * Sets the permissions that have been allotted to this group
     * 
     * @param permissionsSelected
     *            permissions selected
     */
    public void setPermissionsSelected(String[] permissionsSelected) {
        this.permissionsSelected = permissionsSelected;
    }

    /**
     * Returns the permissions that have been allotted to the group
     * 
     * @return permissions list
     */
    public String[] getPermissionsSelected() {
        return this.permissionsSelected;
    }

    /**
     * Sets a temporary group object that is used to pre-fill fields in a JSP form
     * 
     * @param group
     *            group Object
     */
    public void setTemporaryGroup(Group group) {
        this.temporaryGroup = group;
    }

    /**
     * Returns the temporary group object
     * 
     * @return group object
     */
    public Group getTemporaryGroup() {
        return temporaryGroup;
    }

    /**
     * Sets the list of permissions that have already been selected by the user
     * 
     * @param selectedPermissions
     *            selected permissions
     */
    public void setSelectedPermissions(List<Permission> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    /**
     * Returns a list of selected permissions
     * 
     * @return selected permissions List
     */
    @SuppressWarnings("unchecked")
    public List<Permission> getSelectedPermissions() {
        return this.selectedPermissions;
    }

    /**
     * Sets the ID of the group whose details are to be edited
     * 
     * @param groupId
     *            group ID
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns the ID of the group whose details are to be edited
     * 
     * @return group ID
     */
    public int getGroupId() {
        return this.groupId;
    }

    /**
     * Sets the List of all tab names in the system.
     * 
     * @param tabNames
     *            tab names
     */
    public void setTabNames(List<Permission> tabNames) {
        this.tabNames = tabNames;
    }

    /**
     * Returns a list of all the tab names in the system
     * 
     * @return list of tab names
     */
    public List<Permission> getTabNames() {
        return tabNames;
    }

}
