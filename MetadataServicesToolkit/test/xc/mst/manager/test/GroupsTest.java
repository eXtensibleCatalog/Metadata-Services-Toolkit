/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.manager.test;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.GroupService;
import xc.mst.helper.TestHelper;

/**
 * Tests for Groups
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class GroupsTest
{
    /**
     * Method which tests all the functionality related to Groups
     *
     */
    public void addGroup()
    {
      	 // Initialize Solr, database, log before testing
      	 TestHelper helper = TestHelper.getInstance();
        try
        {
            GroupService groupService = new DefaultGroupService();
            Group group = new Group();
            group.setDescription("New Group Description");
            group.setName("New Group Name");
            List permissionsList = new ArrayList();
            Permission permission = new Permission();
            permission.setTabId(1);
            permission.setTabName("Repositories");
            permissionsList.add(permission);
            group.setPermissions(permissionsList);

            groupService.insertGroup(group);
            Group anotherGroup = groupService.getGroupById(group.getId());
            assert (anotherGroup.getId()==group.getId()): "";
            assert (anotherGroup.getDescription().equalsIgnoreCase(group.getDescription())): "The group descriptions should match";
            assert (anotherGroup.getName().equalsIgnoreCase(group.getName())): "The group names should match";
            assert (anotherGroup.getPermissions().containsAll(group.getPermissions())): "The permissions that both groups have, should match";

            groupService.deleteGroup(group);
            /*
            while(groupIter.hasNext())
            {
                Permission tempPermission = (Permission)groupIter.next();
                while(anotherGroupIter.hasNext())
                {
                    Permission temp = (Permission)anotherGroupIter.next();

                }
            }
            * */
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}
