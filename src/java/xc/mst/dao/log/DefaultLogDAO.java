/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.log.Log;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * MySQL implementation of the Data Access Object for the logs table
 *
 * @author Eric Osisek
 */
public class DefaultLogDAO extends LogDAO
{
	/**
	 * A PreparedStatement to get all logs in the database
	 */
	private static PreparedStatement psGetAll = null;

	/**
	 * A PreparedStatement to get all logs in the database sorted by their full name in ascending order
	 */
	private static PreparedStatement psGetSortedAsc = null;
	
	/**
	 * A PreparedStatement to get all logs in the database sorted by their full name in descending order
	 */
	private static PreparedStatement psGetSortedDesc = null;
	
	/**
	 * A PreparedStatement to get a log from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatemnt to insert a log into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatemnt to update a log in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a log from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A Lock to synchronize access to the PreparedStatement to get all logs in the database
	 */
	private static Object psGetAllLock = new Object();

	/**
	 * Lock to synchronize access to the get sorted in ascending order PreparedStatement
	 */
	private static Object psGetSortedAscLock = new Object();

	/**
	 * Lock to synchronize access to the get sorted in descending order PreparedStatement
	 */
	private static Object psGetSortedDescLock = new Object();
	
	/**
	 * A Lock to synchronize access to the PreparedStatement to get a log from the database by its ID
	 */
	private static Object psGetByIdLock = new Object();

	/**
	 * A Lock to synchronize access to the PreparedStatement to insert a log into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * A Lock to synchronize access to the PreparedStatement to update a log in the database
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * A Lock to synchronize access to the PreparedStatement to delete a log from the database
	 */
	private static Object psDeleteLock = new Object();

	@Override
	public ArrayList<Log> getAll()
	{
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all logs");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all Logs
			ArrayList<Log> logs = new ArrayList<Log>();

			try
			{
				// If the PreparedStatement to get all logs was not defined, create it
				if(psGetAll == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_LOG_ID + ", " +
												   COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + " " +
				                       "FROM " + LOGS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all logs\" PreparedStatement the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnection.prepareStatement(selectSql);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetAll.executeQuery();

				// For each result returned, add a Log object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the log
					Log logObj = new Log();

					// Set the fields on the log
					logObj.setId(results.getInt(1));
					logObj.setWarnings(results.getInt(2));
					logObj.setErrors(results.getInt(3));
					logObj.setLastLogReset(results.getDate(4));
					logObj.setLogFileName(results.getString(5));

					// Add the log to the list
					logs.add(logObj);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + logs.size() + " logs in the database.");

				return logs;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the logs.", e);

				return logs;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // synchronized
	} // end method getAll()

	@Override
	public List<Log> getSortedByFileName(boolean asc) 
	{
		return(asc ? getSortedAsc() : getSortedDesc());
	} // end method getSortedByUserName(boolean)

	/**
	 * Gets all log in the database sorted in ascending order by their file name
	 * 
	 * @return A list of all logs in the database sorted in ascending order by their file name
	 */
	private List<Log> getSortedAsc() 
	{
		synchronized(psGetSortedAscLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all logs");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all Logs
			ArrayList<Log> logs = new ArrayList<Log>();

			try
			{
				// If the PreparedStatement to get all logs was not defined, create it
				if(psGetSortedAsc == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_LOG_ID + ", " +
												   COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + " " +
				                       "FROM " + LOGS_TABLE_NAME + " " +
									   "ORDER BY " + COL_LOG_FILE_NAME + " ASC";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all logs\" PreparedStatement the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetSortedAsc = dbConnection.prepareStatement(selectSql);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetSortedAsc.executeQuery();

				// For each result returned, add a Log object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the log
					Log logObj = new Log();

					// Set the fields on the log
					logObj.setId(results.getInt(1));
					logObj.setWarnings(results.getInt(2));
					logObj.setErrors(results.getInt(3));
					logObj.setLastLogReset(results.getDate(4));
					logObj.setLogFileName(results.getString(5));

					// Add the log to the list
					logs.add(logObj);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + logs.size() + " logs in the database.");

				return logs;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the logs.", e);

				return logs;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // synchronized
	} // end method getSortedAsc()
	
	/**
	 * Gets all logs in the database sorted in descending order by their file name
	 * 
	 * @return A list of all logs in the database sorted in descending order by their file name
	 */
	private List<Log> getSortedDesc() 
	{
		synchronized(psGetSortedDescLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all logs");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all Logs
			ArrayList<Log> logs = new ArrayList<Log>();

			try
			{
				// If the PreparedStatement to get all logs was not defined, create it
				if(psGetSortedDesc == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_LOG_ID + ", " +
												   COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + " " +
				                       "FROM " + LOGS_TABLE_NAME + " " +
									   "ORDER BY " + COL_LOG_FILE_NAME + " DESC";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all logs\" PreparedStatement the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetSortedDesc = dbConnection.prepareStatement(selectSql);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetSortedDesc.executeQuery();

				// For each result returned, add a Log object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the log
					Log logObj = new Log();

					// Set the fields on the log
					logObj.setId(results.getInt(1));
					logObj.setWarnings(results.getInt(2));
					logObj.setErrors(results.getInt(3));
					logObj.setLastLogReset(results.getDate(4));
					logObj.setLogFileName(results.getString(5));

					// Add the log to the list
					logs.add(logObj);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + logs.size() + " logs in the database.");

				return logs;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the logs.", e);

				return logs;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // synchronized
	} // end method getSortedDesc()
	
	@Override
	public Log getById(int id)
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the log with ID " + id);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a log by ID was not defined, create it
				if(psGetById == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_LOG_ID + ", " +
					                               COL_WARNINGS + ", " +
					                               COL_ERRORS + ", " +
					                               COL_LAST_LOG_RESET + ", " +
					                               COL_LOG_FILE_NAME + " " +
	                                   "FROM " + LOGS_TABLE_NAME + " " +
	                                   "WHERE " + COL_LOG_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the PreparedStatement to get a log by ID from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetById.setInt(1, id);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetById.executeQuery();

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the log
					Log logObj = new Log();

					// Set the fields on the log
					logObj.setId(results.getInt(1));
					logObj.setWarnings(results.getInt(2));
					logObj.setErrors(results.getInt(3));
					logObj.setLastLogReset(results.getDate(4));
					logObj.setLogFileName(results.getString(5));

					if(log.isDebugEnabled())
						log.debug("Found the log with ID " + id + " in the database.");

					// Return the log
					return logObj;
				} // end if(log found)

				if(log.isDebugEnabled())
					log.debug("The log with ID " + id + " was not found in the database.");

				return null;
			} // end try(get Log)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the log with ID " + id, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public boolean insert(Log logObj) throws DataException
	{
		// Check that the non-ID fields on the log are valid
		validateFields(logObj, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new log");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
					// Create the PreparedStatement to insert a log if it hasn't already been defined
					if(psInsert == null)
					{
						// SQL to insert the new row
						String insertSql = "INSERT INTO " + LOGS_TABLE_NAME + " (" + COL_WARNINGS + ", " +
		            	    													     COL_ERRORS + ", " +
		            	    													     COL_LAST_LOG_RESET + ", " +
		            	    													     COL_LOG_FILE_NAME + ") " +
		            				       "VALUES (?, ?, ?, ?)";

						if(log.isDebugEnabled())
							log.debug("Creating the \"insert log\" PreparedStatement from the SQL " + insertSql);

						// A prepared statement to run the insert SQL
						// This should sanitize the SQL and prevent SQL injection
						psInsert = dbConnection.prepareStatement(insertSql);
					} // end if(insert PreparedStatement not defined)

					// Set the parameters on the insert statement
					psInsert.setInt(1, logObj.getWarnings());
					psInsert.setInt(2, logObj.getErrors());
					psInsert.setDate(3, logObj.getLastLogReset());
					psInsert.setString(4, logObj.getLogFileName());

					// Execute the insert statement and return the result
					if(psInsert.executeUpdate() > 0)
					{
						// Get the auto-generated resource identifier ID and set it correctly on this log Object
						rs = dbConnection.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

					    if (rs.next())
					        logObj.setId(rs.getInt(1));

						return true;
					} // end if(insert succeeded)

					return false;
			} // end try(insert Log)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new log.", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(log)

	@Override
	public boolean update(Log logObj) throws DataException
	{
		// Check that the fields on the log are valid
		validateFields(logObj, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the log with ID " + logObj.getId());

			try
			{
				// If the PreparedStatement to update a log has not been created, create it
				if(psUpdate == null)
				{
					// SQL to update new row
					String updateSql = "UPDATE " + LOGS_TABLE_NAME + " SET " + COL_WARNINGS + "=?, " +
				                                                               COL_ERRORS + "=?, " +
				                                                               COL_LAST_LOG_RESET + "=?, " +
				                                                               COL_LOG_FILE_NAME + "=? " +
	                                   "WHERE " + COL_LOG_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update log\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnection.prepareStatement(updateSql);
				} // end if(update PreparedStatement is not defined)

				// Set the parameters on the update statement
				psUpdate.setInt(1, logObj.getWarnings());
				psUpdate.setInt(2, logObj.getErrors());
				psUpdate.setDate(3, logObj.getLastLogReset());
				psUpdate.setString(4, logObj.getLogFileName());
				psUpdate.setInt(5, logObj.getId());

				// Execute the update statement and return the result
				return psUpdate.executeUpdate() > 0;
			} // end try(update log)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the log with ID " + logObj.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method update(Log)

	@Override
	public boolean delete(Log logObj) throws DataException
	{
		// Check that the ID field on the log are valid
		validateFields(logObj, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the log with ID " + logObj.getId());

			try
			{
				// If the PreparedStatement to delete a log was not defined, create it
				if(psDelete == null)
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ LOGS_TABLE_NAME + " " +
		                               "WHERE " + COL_LOG_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the PreparedStatement to delete a log from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnection.prepareStatement(deleteSql);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, logObj.getId());

				// Execute the delete statement and return the result
				return psDelete.execute();
			} // end try(delete log)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the log with ID " + logObj.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method delete(Log)
} // end class DefaultLogDAO
