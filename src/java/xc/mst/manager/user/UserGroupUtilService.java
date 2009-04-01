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
import xc.mst.bo.user.User;

/*
 * This interface is used for associating a user with a list of groups.
 * These groups in turn have permissions associated with them.
 * (A User can belong to more than one group)
 *
 *
 * @author Tejaswi Haramurali
 */
public interface UserGroupUtilService {

    /**
     * Returns a list of Group IDs that are associated with a User
     * @param userId The User Id for whom a list of Group IDs is returned.
     * @return
     */
    public List<Integer> getGroupsForUserId(int userId);

    /**
     * Delete all the group associations for a User
     * @param userId The UserId for which all associations should be deleted
     */
    public void deleteGroupsForUserId(int userId);

    /**
     * Insert a new User-Group mapping
     * @param userId The user ID to be associated with a Group
     * @param groupId The group ID to be associated with a User
     */
    public void insertUserGroup(int userId,int groupId);

    /**
     * Deletes one single User-Group association
     * @param userId The user ID that is associated with a Group
     * @param groupId The group ID that is associated with a user
     */
    public void deleteUserGroup(int userId,int groupId);


}
