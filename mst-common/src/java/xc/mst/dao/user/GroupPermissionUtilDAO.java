/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.dao.user;

import java.util.List;

import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the permissions assigned to a group
 * 
 * @author Eric Osisek
 */
public abstract class GroupPermissionUtilDAO extends BaseDAO {
    /**
     * The Object managing the database connection
     */
    protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

    /**
     * The name of the groups to top level tabs database table
     */
    public final static String GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME = "groups_to_top_level_tabs";

    /**
     * The name of the group ID column
     */
    public final static String COL_GROUP_ID = "group_id";

    /**
     * The name of the top level tab ID column
     */
    public final static String COL_TOP_LEVEL_TAB_ID = "top_level_tab_id";

    /**
     * Inserts a row in the database giving a group permission to access a top level tab.
     * 
     * @param groupId
     *            The group to receive the permission
     * @param topLevelTabId
     *            The top level tab to add permission for
     * @return True on success, false on failure
     */
    public abstract boolean insert(int groupId, int topLevelTabId);

    /**
     * Deletes the row in the database giving a group permission to access a top level tab.
     * 
     * @param groupId
     *            The group to remove the permission from
     * @param topLevelTabId
     *            The top level tab to remove permission for
     * @return True on success, false on failure
     */
    public abstract boolean delete(int groupId, int topLevelTabId);

    /**
     * Gets all permissions belonging to a group
     * 
     * @param groupId
     *            The ID of the group whose permissions should be returned
     * @return A list of top level tab IDs for the tabs the group has permission to access
     */
    public abstract List<Integer> getPermissionsForGroup(int groupId);

    /**
     * Deletes all permissions belonging to a group
     * 
     * @param groupId
     *            The ID of the group whose permissions should be removed
     * @return True on success, false on failure
     */
    public abstract boolean deletePermissionsForGroup(int groupId);
} // end class GroupPermissionUtil
