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
import xc.mst.manager.BaseService;

/*
 * This is the Service Class for adding, deleting and updating groups
 *
 *
 * @author Tejaswi Haramurali
 */
public class DefaultGroupService extends BaseService implements GroupService {

    /**
     * Returns a group object based on the group ID
     * 
     * @param groupId
     *            the ID of the group to be returned
     * @return group object
     * @throws DatabaseConfigException
     */
    public Group getGroupById(int groupId) throws DatabaseConfigException {
        return getGroupDAO().getById(groupId);
    }

    /**
     * Inserts a group
     * 
     * @param group
     *            The group to be inserted
     */
    public void insertGroup(Group group) throws DataException {

        getGroupDAO().insert(group);
    }

    /**
     * Deletes a group
     * 
     * @param group
     *            The group to be deleted
     */
    public void deleteGroup(Group group) throws DataException {

        getGroupDAO().delete(group);
    }

    /**
     * Updates the details relating to a group.
     * 
     * @param group
     *            The group whose details are to be updates.
     */
    public void updateGroup(Group group) throws DataException {
        getGroupDAO().update(group);
    }

    /**
     * Returns a list of all groups
     * 
     * @return list of groups
     * @throws DatabaseConfigException
     */
    public List<Group> getAllGroups() throws DatabaseConfigException {
        return getGroupDAO().getAll();
    }

    /**
     * Returns a sorted list of all the groups
     * 
     * @param isAscendingOrder
     *            determines if the rows are to be sorted in ascending or descending order
     * @param columnSorted
     *            the column on which the rows are to be sorted
     * @return list of groups
     * @throws DatabaseConfigException
     */
    public List<Group> getAllGroupsSorted(boolean isAscendingOrder, String columnSorted) throws DatabaseConfigException {
        return getGroupDAO().getAllSorted(isAscendingOrder, columnSorted);
    }

    /**
     * Return the group with the specided Group name
     * 
     * @param groupName
     *            Specifies the name of the group object to be returned
     * @return Group if exists otherwise null
     * @throws DatabaseConfigException
     */
    public Group getGroupByName(String groupName) throws DatabaseConfigException {
        return getGroupDAO().getByName(groupName);
    }

}
