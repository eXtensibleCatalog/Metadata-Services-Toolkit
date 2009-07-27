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

public class DefaultServiceOutputSetUtilDAO extends ServiceOutputSetUtilDAO
{
	/**
	 * A PreparedStatement to add a set as output for a service into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to remove a set as output for a service in the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A PreparedStatement to get the IDs of the sets that a service outputs
	 */
	private static PreparedStatement psGetOutputSetsForService = null;

	/**
	 * A PreparedStatement to remove all output set assignments for a service
	 */
	private static PreparedStatement psDeleteOutputSetsForService = null;

	/**
	 * Lock to prevent concurrent access of the prepared statement to
	 * add a set as output for a service into the database.
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to
	 * remove a set as output for a service in the database.
	 */
	private static Object psDeleteLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to
	 * get the IDs of the sets that a service outputs.
	 */
	private static Object psGetOutputSetsForServiceLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to
	 * remove all output set assignments for a service.
	 */
	private static Object psDeleteOutputSetsForServiceLock = new Object();

	@Override
	public boolean insert(int serviceId, int setId)
	{
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Adding the set with ID " + setId + " as output for the service with ID " + serviceId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to add an output set to a service is not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					dbConnectionManager.unregisterStatement(psInsert);
					
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + SERVICES_TO_OUTPUT_SETS_TABLE_NAME +
					                                    " (" + COL_SERVICE_ID + ", " +
	            	    								       COL_SET_ID + ") " +
	            		    		   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"add output set for a service\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql);
				} // end if (insert prepared statement is null)

				// Set the parameters on the insert statement
				psInsert.setInt(1, serviceId);
				psInsert.setInt(2, setId);

				// Execute the insert statement and return the result
				return dbConnectionManager.executeUpdate(psInsert) > 0;
			} // end try (insert the output set to service assignment)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while adding the set with ID " + setId + " as output for the service with ID " + serviceId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method insert(int, int)

	@Override
	public boolean delete(int serviceId, int setId)
	{
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the set with ID " + setId + " as output for the service with ID " + serviceId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to remove an output set from a service is not defined, create it
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					dbConnectionManager.unregisterStatement(psDelete);
					
					// SQL to insert the new row
					String deleteSql = "DELETE FROM " + SERVICES_TO_OUTPUT_SETS_TABLE_NAME + " " +
	            		    		   "WHERE " + COL_SERVICE_ID + "=? " +
	            		    		   "AND " + COL_SET_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove output set from a service\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql);
				} // end if (insert prepared statement is null)

				// Set the parameters on the insert statement
				psDelete.setInt(1, serviceId);
				psDelete.setInt(2, setId);

				// Execute the delete statement and return the result
				return dbConnectionManager.executeUpdate(psDelete) > 0;
			} // end try (delete the output set to service assignment)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while removing the set with ID " + setId + " as output for the service with ID " + serviceId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method delete(int, int)

	@Override
	public List<Integer> getOutputSetsForService(int serviceId)
	{
		synchronized(psGetOutputSetsForServiceLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the output sets for the service with service ID " + serviceId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of set IDs for the output sets for the service with the passed ID
			List<Integer> setIds = new ArrayList<Integer>();

			try
			{
				// If the PreparedStatement to get output set IDs by service ID wasn't defined, create it
				if(psGetOutputSetsForService == null || dbConnectionManager.isClosed(psGetOutputSetsForService))
				{
					dbConnectionManager.unregisterStatement(psGetOutputSetsForService);
					
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + " " +
	                                   "FROM " + SERVICES_TO_OUTPUT_SETS_TABLE_NAME + " " +
	                                   "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get output sets for service\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetOutputSetsForService = dbConnectionManager.prepareStatement(selectSql);
				}

				// Set the parameters on the select statement
				psGetOutputSetsForService.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetOutputSetsForService);

				// For each result returned, add the set ID to the list with the returned data
				while(results.next())
					setIds.add(new Integer(results.getInt(1)));

				if(log.isDebugEnabled())
					log.debug("Found " + setIds.size() + " set IDs that the service with service ID " + serviceId + " outputs.");

				return setIds;
			} // end try (get and return the set IDs which the service outputs)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the output set IDs for the service with service ID " + serviceId, e);

				return setIds;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally
		} // end synchronized
	} // end method getOutputSetsForService(int)

	@Override
	public boolean deleteOutputSetsForService(int serviceId)
	{
		synchronized(psDeleteOutputSetsForServiceLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the output set assignments for the service with service ID " + serviceId);

			try
			{
				// If the PreparedStatement to delete output set assignments by service ID wasn't defined, create it
				if(psDeleteOutputSetsForService == null || dbConnectionManager.isClosed(psDeleteOutputSetsForService))
				{
					dbConnectionManager.unregisterStatement(psDeleteOutputSetsForService);
					
					// SQL to get the rows
					String selectSql = "DELETE FROM " + SERVICES_TO_OUTPUT_SETS_TABLE_NAME + " " +
		    		                   "WHERE " + COL_SERVICE_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove output sets for service\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psDeleteOutputSetsForService = dbConnectionManager.prepareStatement(selectSql);
				}

				// Set the parameters on the select statement
				psDeleteOutputSetsForService.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the insert statement and return the result
				return dbConnectionManager.executeUpdate(psDeleteOutputSetsForService) > 0;
			} // end try (remove all output set assignments from the service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the output set assignments for the service with service ID " + serviceId, e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method deleteOutputSetsForService(int)
} // end class DefaultServiceOutputSetUtilDAO
