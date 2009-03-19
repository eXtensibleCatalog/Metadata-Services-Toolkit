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
import xc.mst.bo.user.Permission;
import xc.mst.dao.user.PermissionDAO;

/**
 *
 * @author tejaswih
 */
public class DefaultPermissionService implements PermissionService
{
    private PermissionDAO permissionDao;
    /**
	 * Gets the permissions belonging to a group.
	 *
	 * @param groupId The ID of the group to get permissions for
	 * @return A list of permissions belonging to the group
	 */
	public List<Permission> getPermissionsForGroup(int groupId)
    {
        return permissionDao.getPermissionsForGroup(groupId);
    }
}
