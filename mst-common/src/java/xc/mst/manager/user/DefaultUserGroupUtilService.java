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

import xc.mst.manager.BaseService;

/*
 * This is the service class that is used to associate users with groups
 *
 *
 * @author Tejaswi Haramurali
 */
public class DefaultUserGroupUtilService extends BaseService implements UserGroupUtilService{
	
    /**
     * returns a list of IDs of the groups that are associated with a user
     * @param userId user ID
     * @return list of group IDs
     */
    public List<Integer> getGroupsForUserId(int userId) {
        return getUserGroupUtilDAO().getGroupsForUser(userId);
    }

    /**
     * deletes all the user-group associations for the user with the specified ID
     * @param userId user ID
     */
    public void deleteGroupsForUserId(int userId) {
    	getUserGroupUtilDAO().deleteGroupsForUser(userId);
    }

    /**
     * inserts a user-group association
     * @param userId user ID
     * @param groupId group ID
     */
    public void insertUserGroup(int userId, int groupId) {
    	getUserGroupUtilDAO().insert(userId,groupId);
    }

    /**
     * deletes a user-group association
     * @param userId user ID
     * @param groupId group ID
     */
     public void deleteUserGroup(int userId, int groupId) {
    	 getUserGroupUtilDAO().delete(userId,groupId);
    }

}
