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

import org.apache.log4j.Logger;

import xc.mst.bo.user.Group;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Accesses groups in the database
 * 
 * @author Eric Osisek
 */
public abstract class GroupDAO extends BaseDAO {
    /**
     * A reference to the logger for this class
     */
    protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * The Object managing the database connection
     */
    protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

    /**
     * The name of the groups database table
     */
    public final static String GROUPS_TABLE_NAME = "groups";

    /**
     * The name of the group ID column
     */
    public final static String COL_GROUP_ID = "group_id";

    /**
     * The name of the name column
     */
    public final static String COL_NAME = "name";

    /**
     * The name of the description column
     */
    public final static String COL_DESCRIPTION = "description";

    /**
     * Gets all groups in the database
     * 
     * @return A list containing all groups in the database
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract List<Group> getAll() throws DatabaseConfigException;

    /**
     * Returns a sorted list of all the groups in the system
     * 
     * @return list of groups
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract List<Group> getAllSorted(boolean isAscendingOrder, String columnSorted) throws DatabaseConfigException;

    /**
     * Gets a group by it's ID
     * 
     * @param groupId
     *            The ID of the group to get
     * @return The group with the passed ID, or null if there was no group with that ID.
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract Group getById(int groupId) throws DatabaseConfigException;

    /**
     * Gets a group by name
     * 
     * @param groupName
     *            The name of the group to get
     * @return The group or null if there was no group with that name.
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract Group getByName(String groupName) throws DatabaseConfigException;

    /**
     * Gets a group by it's ID. Does not set the list of permissions on the returned group.
     * 
     * @param groupId
     *            The ID of the group to get
     * @return The group with the passed ID, or null if there was no group with that ID.
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract Group loadBasicGroup(int groupId) throws DatabaseConfigException;

    /**
     * Inserts a group into the database
     * 
     * @param group
     *            The group to insert
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed group was not valid for inserting
     */
    public abstract boolean insert(Group group) throws DataException;

    /**
     * Updates a group in the database
     * 
     * @param group
     *            The group to update
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed group was not valid for updating
     */
    public abstract boolean update(Group group) throws DataException;

    /**
     * Deletes a group from the database
     * 
     * @param group
     *            The group to delete
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed group was not valid for deleting
     */
    public abstract boolean delete(Group group) throws DataException;

    /**
     * Validates the fields on the passed Group Object
     * 
     * @param group
     *            The group to validate
     * @param validateId
     *            true if the ID field should be validated
     * @param validateNonId
     *            true if the non-ID fields should be validated
     * @throws DataException
     *             If one or more of the fields on the passed group were invalid
     */
    protected void validateFields(Group group, boolean validateId, boolean validateNonId) throws DataException {
        StringBuilder errorMessage = new StringBuilder();

        // Check the ID field if we're supposed to
        if (validateId) {
            if (log.isDebugEnabled())
                log.debug("Checking the ID");

            if (group.getId() < 0)
                errorMessage.append("The group_id is invalid. ");
        } // end if(we should check the ID field)

        // Check the non-ID fields if we're supposed to
        if (validateNonId) {
            if (log.isDebugEnabled())
                log.debug("Checking the non-ID fields");

            if (group.getName() == null || group.getName().length() <= 0 || group.getName().length() > 255)
                errorMessage.append("The name is invalid. ");

            if (group.getDescription() != null && group.getDescription().length() > 1023)
                errorMessage.append("The description is invalid. ");
        } // end if(we should check the non-ID fields

        // Log the error and throw the exception if any fields are invalid
        if (errorMessage.length() > 0) {
            String errors = errorMessage.toString();
            log.error("The following errors occurred: " + errors);
            throw new DataException(errors);
        } // end if(error found)
    } // end method validateFields(Group, boolean, boolean)
} // end class GroupDAO
