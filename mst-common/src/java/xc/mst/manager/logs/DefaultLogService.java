/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.manager.logs;

import java.util.List;

import xc.mst.bo.log.Log;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;

/**
 * 
 * Provides the default implementation for the Logs Service
 * 
 * @author Tejaswi Haramurali
 */
public class DefaultLogService extends BaseService implements LogService {

    /**
     * Gets all logs from the database
     * 
     * @return A list of Log Objects representing all logs in the database
     * @throws DatabaseConfigException
     */
    public List<Log> getAll() throws DatabaseConfigException {
        return getLogDAO().getAll();
    }

    /**
     * Gets the log from the database with the passed log ID
     * 
     * @param id
     *            The ID of the log to get
     * @return A the log with the log ID
     * @throws DatabaseConfigException
     */
    public Log getById(int id) throws DatabaseConfigException {
        return getLogDAO().getById(id);
    }

    /**
     * Inserts a log into the database
     * 
     * @param log
     *            The log to insert
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed log was not valid for inserting
     */
    public void insert(Log log) throws DataException {
        getLogDAO().insert(log);
    }

    /**
     * Updates a log in the database
     * 
     * @param log
     *            The log to update
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed log was not valid for updating
     */
    public void update(Log log) throws DataException {
        getLogDAO().update(log);
    }

    /**
     * Deletes a log from the database
     * 
     * @param log
     *            The log to delete
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed log was not valid for deleting
     */
    public void delete(Log log) throws DataException {
        getLogDAO().delete(log);
    }

    /**
     * Gets a sorted list of all the general logs in the database
     * 
     * @param asc
     *            True to sort in ascending order, false to sort in descending order
     * @param columnName
     *            determines the name of the column on which the rows should be sorted
     * @return A list containing all general logs in the database
     * @throws DatabaseConfigException
     */
    public List<Log> getSorted(boolean asc, String columnName) throws DatabaseConfigException {
        return getLogDAO().getSorted(asc, columnName);
    }
}
