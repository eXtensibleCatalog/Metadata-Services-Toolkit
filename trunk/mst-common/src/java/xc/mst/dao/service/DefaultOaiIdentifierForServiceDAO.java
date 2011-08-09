/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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

import xc.mst.dao.DBConnectionResetException;

/**
 * MySQL implementation of the class to get, cache, and update the next unique OAI
 * identifiers for records output by each service.  Metadata Services can use the
 * methods on this class to maintain the correct values for the next OAI identifier
 * for a service while minimizing the number of SQL queries it makes.
 *
 * @author Eric Osisek
 */
public class DefaultOaiIdentifierForServiceDAO extends OaiIdentifierForServiceDAO
{
	/**
	 * A PreparedStatement to insert a service/next OAI ID pair into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a service/next OAI ID pair in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to get the next OAI identifier
	 * for a service from the database by the service's ID.
	 */
	private static PreparedStatement psGetByServiceId = null;

	/**
	 * Lock to synchronize access to the insert PreparedStatement
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the update PreparedStatement
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to get
	 * the next OAI identifier for a service from the database
	 * by the service's ID.
	 */
	private static Object psGetByServiceIdLock = new Object();

	@Override
	public long getNextOaiIdForService(int serviceId)
	{
		// Box the integer so we can use it as a key in the cache
		Integer boxedServiceId = new Integer(serviceId);

		// If the cache did not contain an entry for the service, get the
		// next OAI identifier from the database and store it in the cache
		if(!nextOaiIdForService.containsKey(boxedServiceId))
		{
			// Get the next OAI identifier from the database
			long nextOaiId = getByServiceId(serviceId);

			// If the database didn't have a row for the service,
			// insert a row using 10000 as the next OAI identifier
			if(nextOaiId < 0)
			{
				// Insert a new row mapping the service to 10000,
				// which is the smallest OAI identifier we can assign.
				insert(serviceId, 10000);

				nextOaiId = 10000;
			} // end if(database row for service didn't exist)

			// Store the next OAI identifier for the service in the cache
			nextOaiIdForService.put(boxedServiceId, new Long(nextOaiId+1));

			// Return the next OAI identifier for the service
			return nextOaiId;
		} // end if(service ID not in cache)

		// Get the next OAI identifier for the specified service.
		long nextOaiId = nextOaiIdForService.get(boxedServiceId);

		// Add the next OAI identifier to the cache for the service
		nextOaiIdForService.put(boxedServiceId, new Long(nextOaiId+1));
		
		// Return the next OAI identifier
		return nextOaiId;
	} // end method getNextOaiIdForService(int)

	@Override
	public boolean writeNextOaiId(int serviceId)
	{
		// Box the integer so we can use it as a key in the cache
		Integer boxedServiceId = new Integer(serviceId);

		// If the cache contained an entry for the service, update the
		// database so the entry for the service ID has the correct
		// next OAI identifier
		if(nextOaiIdForService.containsKey(boxedServiceId))
		{
			// If there was a row with the passed service ID, update it to have the
			// correct next OAI identifier.  Otherwise insert a new row for the service
			// ID/OAI identifier pair.
			if(getByServiceId(serviceId) < 0)
			{
				// Insert a new row mapping the service to the correct next OAI identifier
				// Return the result.
				return insert(serviceId, nextOaiIdForService.get(boxedServiceId).longValue());
			} // end if(row for service didn't exist)

			// There was already a mapping for the service in the database.  Update it
			// to contain the new next OAI identifier for that service
			update(serviceId, nextOaiIdForService.get(boxedServiceId).longValue());
		} // end if(cache contained entry for the service)

		// Return false because we didn't have a cached OAI identifier for the service
		// that we needed to write to the database
		return false;
	} // end method writeNextOaiId(int)

	@Override
	public void writeNextOaiId(int serviceId, long nextOaiId)
	{
		// Box the integer so we can use it as a key in the cache
		Integer boxedServiceId = new Integer(serviceId);
		
		// Store the next XC identifier for the FRBR level in the cache
		nextOaiIdForService.put(boxedServiceId, new Long(nextOaiId+1));

		update(serviceId, nextOaiIdForService.get(boxedServiceId).longValue());
	} // end method writeNextXcId(int)
	
	/**
	 * Gets the next OAI identifier for the service with the passed service ID
	 * from the database.  If there was no database entry with the passed service
	 * ID, returns -1.
	 *
	 * @param serviceId The ID of the service whose next OAI identifier should be returned.
	 * @return The next OAI identifier for the service, or -1 if there was no row for the
	 *         service in the database.
	 */
	private long getByServiceId(int serviceId)
	{
		synchronized(psGetByServiceIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the next oai identifier for the service with ID " + serviceId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get the next oai identifier by service ID wasn't defined, create it
				if(psGetByServiceId == null || dbConnectionManager.isClosed(psGetByServiceId))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_NEXT_OAI_ID + " " +
	                                   "FROM " + OAI_IDENTIFIER_FOR_SERVICES_TABLE_NAME + " " +
	                                   "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get the next oai identifier by service ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByServiceId = dbConnectionManager.prepareStatement(selectSql, psGetByServiceId);
				} // end if(get by service ID PreparedStatement was null)

				// Set the parameters on the select statement
				psGetByServiceId.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByServiceId);

				// If a row was found, return it's next OAI identifier
				if(results.next())
				{
					// The result of the query
					long result = results.getLong(1);

					if(log.isDebugEnabled())
						log.debug("Found the next oai identifier for the service with ID " + serviceId + " to be " + result);

					// Return the next OAI identifier for the service
					return result;
				}

				if(log.isDebugEnabled())
					log.debug("Could not find the next oai identifier for the service with ID " + serviceId);

				return -1;
			} // end try(get the next OAI identifier for the service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the next oai identifier for the service with ID " + serviceId, e);

				return -1;
			} // end catch(SQLException)
			catch(NullPointerException e)
			{
				log.error("Unable to connect to the database using the parameters from the configuration file.");
				
				return -1;
			}
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByServiceId(serviceId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByServiceId(int)

	/**
	 * Inserts a row in the database mapping the passed service ID with the passed
	 * next OAI identifier.
	 *
	 * @param serviceId The service ID to add the mapping for
	 * @param nextOaiId The next OAI identifier for the service with the passed ID
	 * @return True if the insert succeeded, false otherwise
	 */
	private boolean insert(int serviceId, long nextOaiId)
	{
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new oai identifier for the service with service ID " + serviceId);

			// Try to insert the new row
			try
			{
				// If the PreparedStatement to insert a oai identifier for service is not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + OAI_IDENTIFIER_FOR_SERVICES_TABLE_NAME +
					                                                 " (" + COL_SERVICE_ID + ", " +
	            	    													COL_NEXT_OAI_ID + ") " +
	            		    		   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert oai identifier for service\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setInt(1, serviceId);
				psInsert.setLong(2, nextOaiId);

				// Execute the insert statement and return true iff it succeeded
				return dbConnectionManager.executeUpdate(psInsert) > 0;
			} // end try(insert the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new oai identifier for the service with service ID " + serviceId, e);

				return false;
			} // end catch(SQLException)
			catch(NullPointerException e)
			{
				log.error("Unable to connect to the database using the parameters from the configuration file.");
				
				return false;
			}
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(serviceId, nextOaiId);
			}
		} // end synchronized
	} // end method insert(int, long)

	/**
	 * Updates a row in the database mapping the passed service ID with the passed
	 * next OAI identifier.
	 *
	 * @param serviceId The service ID to update the mapping for
	 * @param nextOaiId The next OAI identifier for the service with the passed ID
	 * @return True if the update succeeded, false otherwise
	 */
	private boolean update(int serviceId, long nextOaiId)
	{
		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the next oai identifier for the service with service ID " + serviceId);

			try
			{
				// If the PreparedStatement to update the next oai identifier for a service was not defined, create it
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + OAI_IDENTIFIER_FOR_SERVICES_TABLE_NAME +
													" SET " + COL_NEXT_OAI_ID + "=? " +
	                                   "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update the next oai identifier for a service\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setLong(1, nextOaiId);
				psUpdate.setInt(2, serviceId);

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the next oai identifier for the service with ID " + serviceId, e);

				return false;
			} // end catch(SQLException)
			catch(NullPointerException e)
			{
				log.error("Unable to connect to the database using the parameters from the configuration file.");
				
				return false;
			}
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(serviceId, nextOaiId);
			}
		} // end synchronized
	} // end method update(int, long)
} // end class DefaultOaiIdentiferForServiceDAO
