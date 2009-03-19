
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.logs;

import java.util.ArrayList;
import xc.mst.bo.log.Log;
import xc.mst.dao.DataException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;

/**
 *
 * Provides the default implementation for the Logs Service
 *
 * @author Tejaswi Haramurali
 */
public class DefaultLogService implements LogService
{
    /**Log DAO Object */
    private LogDAO logDao;

    public DefaultLogService()
    {
        logDao = new DefaultLogDAO();
    }

    /**
	 * Gets all logs from the database
	 *
	 * @return A list of Log Objects representing all logs in the database
	 */
	public ArrayList<Log> getAll()
    {
        return logDao.getAll();
    }

	/**
	 * Gets the log from the database with the passed log ID
	 *
	 * @param id The ID of the log to get
	 * @return A the log with the log ID
	 */
	public Log getById(int id)
    {
        return logDao.getById(id);
    }

	/**
	 * Inserts a log into the database
	 *
	 * @param log The log to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed log was not valid for inserting
	 */
	public void insert(Log log) throws DataException
    {
        logDao.insert(log);
    }

	/**
	 * Updates a log in the database
	 *
	 * @param log The log to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed log was not valid for updating
	 */
	public void update(Log log) throws DataException
    {
        logDao.update(log);
    }

	/**
	 * Deletes a log from the database
	 *
	 * @param log The log to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed log was not valid for deleting
	 */
	public void delete(Log log) throws DataException
    {
        logDao.delete(log);
    }
}
