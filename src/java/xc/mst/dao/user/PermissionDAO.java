/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.user;

import java.util.List;

import xc.mst.bo.user.Permission;

/**
 * Data access object for permissions belonging to a group.  These are taken from the
 * top_level_tab and group_to_top_level_tab tables.
 *
 * @author Eric Osisek
 */
public abstract class PermissionDAO
{
	/**
	 * The name of the groups to top level tabs database table
	 */
	public final static String GROUPS_TO_TOP_LEVEL_TABS_TABLE_NAME = "groups_to_top_level_tabs";

	/**
	 * The name of the top level tabs database table
	 */
	public final static String TOP_LEVEL_TABS_TABLE_NAME = "top_level_tabs";

	/**
	 * The name of the Group to Top Level Tabs ID column
	 */
	public final static String COL_GROUP_TO_TOP_LEVEL_TAB_ID = "group_to_top_level_tab_id";

	/**
	 * The name of the group ID column
	 */
	public final static String COL_GROUP_ID = "group_id";

	/**
	 * The name of the top level tab ID column
	 */
	public final static String COL_TOP_LEVEL_TAB_ID = "top_level_tab_id";

	/**
	 * The name of the Tab Name column
	 */
	public final static String COL_TAB_NAME = "tab_name";

	/**
	 * The name of the Tab Order column
	 */
	public final static String COL_TAB_ORDER = "tab_order";

	/**
	 * Gets the permissions belonging to a group.
	 *
	 * @param groupId The ID of the group to get permissions for
	 * @return A list of permissions belonging to the group
	 */
	public abstract List<Permission> getPermissionsForGroup(int groupId);

    /**
     * returns a permission object based on the ID passed
     * @param permissionId permission ID
     * @return
     */
    public abstract Permission getPermissionById(int permissionId);
} // end class PermissionDAO
