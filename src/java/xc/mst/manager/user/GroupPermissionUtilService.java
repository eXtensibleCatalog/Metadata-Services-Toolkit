
package xc.mst.manager.user;

import java.util.List;

/**
 * This service class is used to provide methods which associate a group with certain permissions
 *
 * @author Tejaswi Haramurali
 */
public interface GroupPermissionUtilService
{
    /**
	 * Inserts a row in the database giving a group permission to access a top level tab.
	 *
	 * @param groupId The group to receive the permission
	 * @param topLevelTabId The top level tab to add permission for
	 * @return True on success, false on failure
	 */
	public void insertGroupPermission(int groupId, int topLevelTabId);

	/**
	 * Deletes the row in the database giving a group permission to access a top level tab.
	 *
	 * @param groupId The group to remove the permission from
	 * @param topLevelTabId The top level tab to remove permission for
	 * @return True on success, false on failure
	 */
	public void deleteGroupPermission(int groupId, int topLevelTabId);

	/**
	 * Gets all permissions belonging to a group
	 *
	 * @param groupId The ID of the group whose permissions should be returned
	 * @return A list of top level tab IDs for the tabs the group has permission to access
	 */
	public List getPermissionsForGroup(int groupId);

	/**
	 * Deletes all permissions belonging to a group
	 *
	 * @param groupId The ID of the group whose permissions should be removed
	 * @return True on success, false on failure
	 */
	public void deletePermissionsForGroup(int groupId);
}
