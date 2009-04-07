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
import xc.mst.dao.user.DefaultGroupDAO;
import xc.mst.dao.user.GroupDAO;

/*
 * This is the Service Class for adding, deleting and updating groups
 *
 *
 * @author Tejaswi Haramurali
 */
public class DefaultGroupService implements GroupService {

    /** Group DAO Object */
    private GroupDAO groupDao = new DefaultGroupDAO();

    /**
     * Returns a group object based on the group ID
     * @param groupId the ID of the group to be returned
     * @return group object
     */
    public Group getGroupById(int groupId) {
        return groupDao.getById(groupId);
    }

    /**
     * Inserts a group
     * @param group The group to be inserted
     */
    public void insertGroup(Group group) throws DataException{

        groupDao.insert(group);
    }

    /**
     * Deletes a group
     * @param group The group to be deleted
     */
    public void deleteGroup(Group group) throws DataException{

        groupDao.delete(group);
    }

    /**
     * Updates the details relating to a group.
     * @param group The group whose details are to be updates.
     */
    public void updateGroup(Group group) throws DataException{
        groupDao.update(group);
    }

    /**
     * returns a list of all groups
     * @return list of groups
     */
    public List<Group> getAllGroups()
    {
        return groupDao.getAll();
    }

    /**
     * returns a sorted list of all the groups
     * @param isAscendingOrder determines if the rows are to be sorted in ascending or descending order
     * @param columnSorted the column on which the rows are to be sorted
     * @return list of groups
     */
    public List<Group> getAllGroupsSorted(boolean isAscendingOrder,String columnSorted)
    {
        return groupDao.getAllSorted(isAscendingOrder, columnSorted);
    }

    /**
     * Return the group with the specided Group name
     * @param groupName Specifies the name of the group object to be returned
     * @return Group if exists otherwise null
     */
    public Group getGroupByName(String groupName) {
    	return groupDao.getByName(groupName);
    }

   
}
