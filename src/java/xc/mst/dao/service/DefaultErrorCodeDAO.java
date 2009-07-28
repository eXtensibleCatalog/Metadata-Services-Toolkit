/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.service.ErrorCode;
import xc.mst.bo.service.Service;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

public class DefaultErrorCodeDAO extends ErrorCodeDAO 
{
	/**
	 * Data Access Object for getting error codes
	 */
	private ServiceDAO serviceDao = new DefaultServiceDAO();
	
	/**
	 * A PreparedStatement to get all error codes in the database
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * A PreparedStatement to get an error code from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get an error code from the database by its error code and service
	 */
	private static PreparedStatement psGetByNameAndService = null;
	
	/**
	 * A PreparedStatement to insert an error code into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update an error code in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete an error code from the database
	 */
	private static PreparedStatement psDelete = null;
	
	/**
	 * Lock to synchronize access to the get all PreparedStatement
	 */
	private static Object psGetAllLock = new Object();
	
	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetByIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by name and service PreparedStatement
	 */
	private static Object psGetByNameAndServiceLock = new Object();

	/**
	 * Lock to synchronize access to the insert PreparedStatement
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the update PreparedStatement
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the delete PreparedStatement
	 */
	private static Object psDeleteLock = new Object();
	
	@Override
	public List<ErrorCode> getAll() throws DatabaseConfigException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all error codes");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all services
			List<ErrorCode> errorCodes = new ArrayList<ErrorCode>();

			try
			{
				// Create the PreparedStatment to get all error codes if it hasn't already been created
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_ERROR_CODE_ID + ", " +
												   COL_ERROR_CODE + ", " +
												   COL_ERROR_DESCRIPTION_FILE + ", " +
												   COL_SERVICE_ID + " " +
								       "FROM " + ERROR_CODES_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all error codes\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a Service object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the error code
					ErrorCode errorCode = new ErrorCode();

					// Set the fields on the error code
					errorCode.setId(results.getInt(1));
					errorCode.setErrorCode(results.getString(2));
					errorCode.setErrorDescriptionFile(results.getString(3));
					errorCode.setService(serviceDao.getById(results.getInt(4)));
					
					// Add the service to the list
					errorCodes.add(errorCode);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + errorCodes.size() + " error codes in the database.");

				return errorCodes;
			} // end try(get error codes)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the error codes.", e);

				return errorCodes;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public ErrorCode getById(int id) throws DatabaseConfigException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting an error codes by ID");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get all error codes if it hasn't already been created
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_ERROR_CODE_ID + ", " +
												   COL_ERROR_CODE + ", " +
												   COL_ERROR_DESCRIPTION_FILE + ", " +
												   COL_SERVICE_ID + " " +
								       "FROM " + ERROR_CODES_TABLE_NAME + " " +
								       "WHERE " + COL_ERROR_CODE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get error code by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get all PreparedStatement not defined)

				// Set the parameters on the SELECT statement
				psGetById.setInt(1, id);
				
				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// For each result returned, add a Service object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the error code
					ErrorCode errorCode = new ErrorCode();

					// Set the fields on the error code
					errorCode.setId(results.getInt(1));
					errorCode.setErrorCode(results.getString(2));
					errorCode.setErrorDescriptionFile(results.getString(3));
					errorCode.setService(serviceDao.getById(results.getInt(4)));
				
					if(log.isDebugEnabled())
						log.debug("Found the error code with ID " + id + " in the database.");
					
					// Add the service to the list
					return errorCode;
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Could not find the error code with ID " + id + " in the database.");

				return null;
			} // end try(get error codes)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the error code with ID " + id + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public ErrorCode loadBasicErrorCode(int id) throws DatabaseConfigException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting an error codes by ID");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get all error codes if it hasn't already been created
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_ERROR_CODE_ID + ", " +
												   COL_ERROR_CODE + ", " +
												   COL_ERROR_DESCRIPTION_FILE + ", " +
												   COL_SERVICE_ID + " " +
								       "FROM " + ERROR_CODES_TABLE_NAME + " " +
								       "WHERE " + COL_ERROR_CODE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get error code by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get all PreparedStatement not defined)

				// Set the parameters on the SELECT statement
				psGetById.setInt(1, id);
				
				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// For each result returned, add a Service object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the error code
					ErrorCode errorCode = new ErrorCode();

					// Set the fields on the error code
					errorCode.setId(results.getInt(1));
					errorCode.setErrorCode(results.getString(2));
					errorCode.setErrorDescriptionFile(results.getString(3));
				
					if(log.isDebugEnabled())
						log.debug("Found the error code with ID " + id + " in the database.");
					
					// Add the service to the list
					return errorCode;
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Could not find the error code with ID " + id + " in the database.");

				return null;
			} // end try(get error codes)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the error code with ID " + id + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public ErrorCode getByErrorCodeAndService(String errorCode, Service service) throws DatabaseConfigException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByNameAndServiceLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting an error codes by ID");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get all error codes if it hasn't already been created
				if(psGetByNameAndService == null || dbConnectionManager.isClosed(psGetByNameAndService))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_ERROR_CODE_ID + ", " +
												   COL_ERROR_CODE + ", " +
												   COL_ERROR_DESCRIPTION_FILE + ", " +
												   COL_SERVICE_ID + " " +
								       "FROM " + ERROR_CODES_TABLE_NAME + " " +
								       "WHERE " + COL_ERROR_CODE + "=? " + 
								       "AND " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get error code by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByNameAndService = dbConnectionManager.prepareStatement(selectSql, psGetByNameAndService);
				} // end if(get all PreparedStatement not defined)

				// Set the parameters on the SELECT statement
				psGetByNameAndService.setString(1, errorCode);
				psGetByNameAndService.setInt(2, service.getId());
				
				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByNameAndService);

				// For each result returned, add a Service object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the error code
					ErrorCode errorCodeObj = new ErrorCode();

					// Set the fields on the error code
					errorCodeObj.setId(results.getInt(1));
					errorCodeObj.setErrorCode(results.getString(2));
					errorCodeObj.setErrorDescriptionFile(results.getString(3));
					errorCodeObj.setService(serviceDao.getById(results.getInt(4)));
				
					if(log.isDebugEnabled())
						log.debug("Found the error code with error code " + errorCode + " and service ID" + service.getId() + " in the database.");
					
					// Add the service to the list
					return errorCodeObj;
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Could not find the error code with error code " + errorCode + " and service ID" + service.getId() + " in the database.");

				return null;
			} // end try(get error codes)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the error code with error code " + errorCode + " and service ID" + service.getId() + " in the database.");

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public boolean insert(ErrorCode errorCode) throws DataException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the service are valid
		validateFields(errorCode, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new error code with the error code " + errorCode.getErrorCode());

			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a service if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + ERROR_CODES_TABLE_NAME + " (" + COL_ERROR_CODE + ", " +
	            	      													            COL_ERROR_DESCRIPTION_FILE + ", " +
	            	      													            COL_SERVICE_ID + ") " +
	            				       "VALUES (?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert error code\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, errorCode.getErrorCode());
				psInsert.setString(2, errorCode.getErrorDescriptionFile());
				psInsert.setInt(3, errorCode.getService().getId());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated resource identifier ID and set it correctly on this Service Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        errorCode.setId(rs.getInt(1));

					return true;
				} // end if(insert succeeded)
				else
					return false;
			}
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new error code with the error code " + errorCode.getErrorCode(), e);
		    	
				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public boolean update(ErrorCode errorCode) throws DataException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the error code are valid
		validateFields(errorCode, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the service with ID " + errorCode.getId());

			try
			{
				// Create a PreparedStatement to update a service if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + ERROR_CODES_TABLE_NAME + " SET " + COL_ERROR_CODE + "=?, " +
				                                                          COL_ERROR_DESCRIPTION_FILE + "=?, " +
				                                                          COL_SERVICE_ID + "=? " +
	                                   "WHERE " + COL_ERROR_CODE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update error code\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, errorCode.getErrorCode());
				psUpdate.setString(2, errorCode.getErrorDescriptionFile());
				psUpdate.setInt(3, errorCode.getService().getId());
				psUpdate.setInt(4, errorCode.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update error code)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the error code with ID " + errorCode.getId(), e);
		    	
				return false;
			} // end catch(SQLException)
		} // end synchronized
	}

	@Override
	public boolean delete(ErrorCode errorCode) throws DataException 
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the ID field on the service are valid
		validateFields(errorCode, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the service with ID " + errorCode.getId());

			try
			{
				// Create the PreparedStatement to delete a error code if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM " + ERROR_CODES_TABLE_NAME + " " +
		                               "WHERE " + COL_ERROR_CODE_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete error code\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, errorCode.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the error code)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the error code with ID " + errorCode.getId(), e);
		    	
				return false;
			} // end catch(SQLException)
		} // end synchronized
	}
}
