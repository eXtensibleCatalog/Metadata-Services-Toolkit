

package xc.mst.manager.user;

import java.util.List;

import xc.mst.bo.user.Permission;

/**
 * Service class that interacts with the various permissions that are provided as part of the MST
 *
 * @author Tejaswi Haramurali
 */
public interface PermissionService
{
    /**
	 * Gets the permissions belonging to a group.
	 *
	 * @param groupId The ID of the group to get permissions for
	 * @return A list of permissions belonging to the group
	 */
	public abstract List<Permission> getPermissionsForGroup(int groupId);

    /**
     * returns a permission by its ID
     * @param permissionId permission ID
     * @return
     */
    public abstract Permission getPermissionById(int permissionId);
}
