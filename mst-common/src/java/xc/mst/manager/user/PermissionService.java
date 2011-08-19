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

/**
 * Service class that interacts with the various permissions that are provided as part of the MST
 * 
 * @author Tejaswi Haramurali
 */
public interface PermissionService {
    /**
     * Gets the permissions belonging to a group.
     * 
     * @param groupId
     *            The ID of the group to get permissions for
     * @return A list of permissions belonging to the group
     * @throws DatabaseConfigException
     */
    public List<Permission> getPermissionsForGroup(int groupId) throws DatabaseConfigException;

    /**
     * returns a permission by its ID
     * 
     * @param permissionId
     *            permission ID
     * @return
     * @throws DatabaseConfigException
     */
    public Permission getPermissionById(int permissionId) throws DatabaseConfigException;

    /**
     * returns a list of all permissions
     * 
     * @return list of permissions
     * @throws DatabaseConfigException
     */
    public List getAllPermissions() throws DatabaseConfigException;
}
