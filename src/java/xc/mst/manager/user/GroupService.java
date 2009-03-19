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
import xc.mst.bo.user.Group;
import xc.mst.dao.DataException;

/*
 * This interface is used for adding new groups,
 * editing already existing groups and deleting groups
 * of users
 *
 * @author Tejaswi Haramurali
 *
 */

public interface GroupService {

    /**
     * Return the group with the specided Group Id
     * @param groupId Specifies the ID of the group object to be returned
     * @return Group if exists otherwise null
     */
    public Group getGroupById(int groupId);

    /**
     * Inserts a group
     * @param group Group object to inserted
     */
    public void insertGroup(Group group) throws DataException;

    /**
     * Deletes a group
     * @param group Group object to deleted
     */
    public void deleteGroup(Group group) throws DataException;

    /**
     * updates the details pertaining to a group
     * @param group Group object to updated
     */
    public void updateGroup(Group group) throws DataException;
    /**
     * returns a list of all the groups the user can be a member of
     * @return list of groups
     */
    public List<Group> getAllGroups();
}
