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
import xc.mst.bo.user.Group;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

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
     *
     * @param groupId Specifies the ID of the group object to be returned
     * @return Group if exists otherwise null
     * @throws DatabaseConfigException
     */
    public Group getGroupById(int groupId) throws DatabaseConfigException;

    /**
     * Return the group with the specided Group name
     *
     * @param groupName Specifies the name of the group object to be returned
     * @return Group if exists otherwise null
     * @throws DatabaseConfigException
     */
    public Group getGroupByName(String groupName) throws DatabaseConfigException;

    /**
     * Inserts a group
     *
     * @param group Group object to inserted
     */
    public void insertGroup(Group group) throws DataException;

    /**
     * Deletes a group
     *
     * @param group Group object to deleted
     */
    public void deleteGroup(Group group) throws DataException;

    /**
     * Updates the details pertaining to a group
     *
     * @param group Group object to updated
     */
    public void updateGroup(Group group) throws DataException;

    /**
     * Returns a list of all the groups the user can be a member of
     *
     * @return list of groups
     * @throws DatabaseConfigException
     */
    public List<Group> getAllGroups() throws DatabaseConfigException;

   /**
    * Returns a sorted list of all the groups
    *
    * @param isAscendingOrder determines whether the list of groups is to be sorted in ascending or descending order
    * @param columnSorted The column on which the rows are sorted
    * @return list of groups
 * @throws DatabaseConfigException
    */
    public List<Group> getAllGroupsSorted(boolean isAscendingOrder,String columnSorted) throws DatabaseConfigException;


}
