/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */



package xc.mst.manager.user;

import java.util.List;

import xc.mst.bo.user.Permission;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;

/**
 * Service class that provides implementation for methods which interact with permissions
 *
 * @author Tejaswi Haramurali
 */
public class DefaultPermissionService extends BaseService implements PermissionService {
    
    /**
	 * Gets the permissions belonging to a group.
	 *
	 * @param groupId The ID of the group to get permissions for
	 * @return A list of permissions belonging to the group
     * @throws DatabaseConfigException 
	 */
	public List<Permission> getPermissionsForGroup(int groupId) throws DatabaseConfigException
    {
        return getPermissionDAO().getPermissionsForGroup(groupId);
    }

     /**
     * returns a permission by its ID
     * @param permissionId permission ID
     * @return
     * @throws DatabaseConfigException 
     */
    public Permission getPermissionById(int permissionId) throws DatabaseConfigException
    {
        return getPermissionDAO().getPermissionById(permissionId);
    }

    /**
     * Returns a list of all permissions in the system
     *
     * @return list of permissions
     * @throws DatabaseConfigException 
     */
    public List<Permission> getAllPermissions() throws DatabaseConfigException
    {
        return getPermissionDAO().getAll();
    }
}
