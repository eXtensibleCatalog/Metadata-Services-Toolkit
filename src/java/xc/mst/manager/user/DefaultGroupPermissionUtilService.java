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

import xc.mst.dao.user.DefaultGroupPermissionUtilDAO;
import xc.mst.dao.user.GroupPermissionUtilDAO;

/**
 * Provides the implementation for the methods which associate groups with permissions
 *
 * @author Tejaswi Haramurali
 */
public class DefaultGroupPermissionUtilService implements GroupPermissionUtilService
{
    private GroupPermissionUtilDAO groupPermissionUtilDao;

    public DefaultGroupPermissionUtilService()
    {
        groupPermissionUtilDao = new DefaultGroupPermissionUtilDAO();
    }

    /**
	 * Inserts a row in the database giving a group permission to access a top level tab.
	 *
	 * @param groupId The group to receive the permission
	 * @param topLevelTabId The top level tab to add permission for
	 * @return True on success, false on failure
	 */
	public void insertGroupPermission(int groupId, int topLevelTabId)
    {
        groupPermissionUtilDao.insert(groupId, topLevelTabId);
    }

	/**
	 * Deletes the row in the database giving a group permission to access a top level tab.
	 *
	 * @param groupId The group to remove the permission from
	 * @param topLevelTabId The top level tab to remove permission for
	 * @return True on success, false on failure
	 */
	public void deleteGroupPermission(int groupId, int topLevelTabId)
    {
        groupPermissionUtilDao.delete(groupId, topLevelTabId);
    }

	/**
	 * Gets all permissions belonging to a group
	 *
	 * @param groupId The ID of the group whose permissions should be returned
	 * @return A list of top level tab IDs for the tabs the group has permission to access
	 */
	public List getPermissionsForGroup(int groupId)
    {
        return groupPermissionUtilDao.getPermissionsForGroup(groupId);
    }

	/**
	 * Deletes all permissions belonging to a group
	 *
	 * @param groupId The ID of the group whose permissions should be removed
	 * @return True on success, false on failure
	 */
	public void deletePermissionsForGroup(int groupId)
    {
        groupPermissionUtilDao.deletePermissionsForGroup(groupId);
    }
}
