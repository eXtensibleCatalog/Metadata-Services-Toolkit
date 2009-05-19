/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import xc.mst.dao.MySqlConnectionManager;

/**
 * MySQL implementation of the class to get, cache, and update the next unique XC
 * identifiers for elements at each FRBR level. A Metadata Service can use the
 * methods on this class to maintain the correct values for the next XC identifier
 * for a FRBR level while minimizing the number of SQL queries it makes.
 *
 * @author Eric Osisek
 */
public class DefaultXcIdentifierForFrbrElementDAO extends XcIdentifierForFrbrElementDAO
{
	/**
	 * A PreparedStatement to insert a FRBR level/next XC ID pair into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a FRBR level/next XC ID pair in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to get the next XC identifier
	 * for a FRBR level from the database by the FRBR level's element ID.
	 */
	private static PreparedStatement psGetByElementId = null;

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
	 * the next XC identifier for a FRBR level from the database
	 * by the FRBR level's element ID.
	 */
	private static Object psGetByElementIdLock = new Object();

	@Override
	public long getNextXcIdForFrbrElement(int elementId)
	{
		// Box the integer so we can use it as a key in the cache
		Integer boxedElementId = new Integer(elementId);

		// If the cache did not contain an entry for the FRBR level, get the
		// next XC identifier from the database and store it in the cache
		if(!nextXcIdForFrbrElement.containsKey(boxedElementId))
		{
			// Get the next OAI identifier from the database
			long nextXcId = getByElementId(elementId);

			// If the database didn't have a row for the FRBR level,
			// insert a row using 10000 as the next XC identifier
			if(nextXcId < 0)
			{
				// Insert a new row mapping the service to 10000,
				// which is the smallest OAI identifier we can assign.
				insert(elementId, 10000);

				nextXcId = 10000;
			} // end if(database row for FRBR level didn't exist)

			// Store the next XC identifier for the FRBR level in the cache
			nextXcIdForFrbrElement.put(boxedElementId, new Long(nextXcId+1));

			// Return the next XC identifier for the FRBR level
			return nextXcId;
		} // end if(FRBR level element ID not in cache)

		// Get the next XC identifier for the specified FRBR level.
		long nextXcId = nextXcIdForFrbrElement.get(boxedElementId);

		// Add the next OAI identifier to the cache for the service
		nextXcIdForFrbrElement.put(boxedElementId, new Long(nextXcId+1));

		// Return the next OAI identifier
		return nextXcId;
	} // end method getNextXciIdForFrbrElement(int)

	@Override
	public boolean writeNextXcId(int elementId)
	{
		// Box the integer so we can use it as a key in the cache
		Integer boxedElementId = new Integer(elementId);

		// If the cache contained an entry for the FRBR level, update the
		// database so the entry for the FRBR level element ID has the correct
		// next XC identifier
		if(nextXcIdForFrbrElement.containsKey(boxedElementId))
		{
			// If there was a row with the passed FRBR level element ID, update it to have the
			// correct next XC identifier.  Otherwise insert a new row for the FRBR level element
			// ID/XC identifier pair.
			if(getByElementId(elementId) < 0)
			{
				// Insert a new row mapping the FRBR level to the correct next XC identifier
				// Return the result.
				return insert(elementId, nextXcIdForFrbrElement.get(boxedElementId).longValue());
			} // end if(row for FRBR level didn't exist)

			// There was already a mapping for the FRBR level in the database.  Update it
			// to contain the new next XC identifier for that FRBR level
			update(elementId, nextXcIdForFrbrElement.get(boxedElementId).longValue());
		} // end if(cache contained entry for the FRBR level)

		// Return false because we didn't have a cached XC identifier for the FRBR level
		// that we needed to write to the database
		return false;
	} // end method writeNextXcId(int)

	/**
	 * Gets the next OAI identifier for the FRBR level with the passed element ID
	 * from the database.  If there was no database entry with the passed FRBR level element
	 * ID, returns -1.
	 *
	 * @param elementId The ID of the FRBR level element whose next XC identifier should be returned.
	 * @return The next OAI identifier for the FRBR level, or -1 if there was no row for the
	 *         FRBR level in the database.
	 */
	private long getByElementId(int elementId)
	{
		synchronized(psGetByElementIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the next XC identifier for the FRBR level with elment ID " + elementId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get the next XC identifier by FRBR level element ID wasn't defined, create it
				if(psGetByElementId == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_NEXT_XC_ID + " " +
	                                   "FROM " + XC_ID_FOR_FRBR_ELEMENTS_TABLE_NAME + " " +
	                                   "WHERE " + COL_ELEMENT_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get the next XC identifier by FRBR level element ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByElementId = dbConnection.prepareStatement(selectSql);
				} // end if(get by service ID PreparedStatement was null)

				// Set the parameters on the select statement
				psGetByElementId.setInt(1, elementId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetByElementId.executeQuery();

				// If a row was found, return it's next XC identifier
				if(results.next())
				{
					// The result of the query
					long result = results.getLong(1);

					if(log.isDebugEnabled())
						log.debug("Found the next XC identifier for the FRBR level with element ID " + elementId + " to be " + result);

					// Return the next XC identifier for the FRBR levle
					return result;
				}

				if(log.isDebugEnabled())
					log.debug("Could not find the next XC identifier for the FRBR level with element ID " + elementId);

				return -1;
			} // end try(get the next XC identifier for the FRBR level)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the next XC identifier for the FRBR level with element ID " + elementId, e);

				return -1;
			} // end catch(SQLException)
			catch(NullPointerException e)
			{
				log.error("Unable to connect to the database using the parameters from the configuration file.");
				
				return -1;
			}
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByElementId(int)

	/**
	 * Inserts a row in the database mapping the passed FRBR level element ID with the passed
	 * next XC identifier.
	 *
	 * @param elementId The FRBR level element ID to add the mapping for
	 * @param nextXcId The next XC identifier for the FRBR with the passed element ID
	 * @return True if the insert succeeded, false otherwise
	 */
	private boolean insert(int elementId, long nextXcId)
	{
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new XC identifier for the FRBR level with element ID " + elementId);

			// Try to insert the new row
			try
			{
				// If the PreparedStatement to insert a XC identifier for FRBR level is not defined, create it
				if(psInsert == null)
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + XC_ID_FOR_FRBR_ELEMENTS_TABLE_NAME +
					                                                 " (" + COL_ELEMENT_ID + ", " +
	            	    													COL_NEXT_XC_ID + ") " +
	            		    		   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert XC identifier for FRBR level\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnection.prepareStatement(insertSql);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setInt(1, elementId);
				psInsert.setLong(2, nextXcId);

				// Execute the insert statement and return true iff it succeeded
				return psInsert.executeUpdate() > 0;
			} // end try(insert the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new XC identifier for the FRBR level with element ID " + elementId, e);

				return false;
			} // end catch(SQLException)
			catch(NullPointerException e)
			{
				log.error("Unable to connect to the database using the parameters from the configuration file.");
				
				return false;
			}
		} // end synchronized
	} // end method insert(int, long)

	/**
	 * Updates a row in the database mapping the passed FRBR level element ID with the passed
	 * next XC identifier.
	 *
	 * @param elementId The XC level element ID to update the mapping for
	 * @param nextXcId The next XC identifier for the FRBR level with the passed element ID
	 * @return True if the update succeeded, false otherwise
	 */
	private boolean update(int elementId, long nextXcId)
	{
		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the next XC identifier for the FRBR level with element ID " + elementId);

			try
			{
				// If the PreparedStatement to update the next XC identifier for a FRBR level was not defined, create it
				if(psUpdate == null)
				{
					// SQL to update new row
					String updateSql = "UPDATE " + XC_ID_FOR_FRBR_ELEMENTS_TABLE_NAME +
													" SET " + COL_NEXT_XC_ID + "=? " +
	                                   "WHERE " + COL_ELEMENT_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update the next XC identifier for a service\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnection.prepareStatement(updateSql);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setLong(1, nextXcId);
				psUpdate.setInt(2, elementId);

				// Execute the update statement and return the result
				return psUpdate.executeUpdate() > 0;
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the next XC identifier for the FRBR level with element ID " + elementId, e);

				return false;
			} // end catch(SQLException)
			catch(NullPointerException e)
			{
				log.error("Unable to connect to the database using the parameters from the configuration file.");
				
				return false;
			}
		} // end synchronized
	} // end method update(int, long)
} // end class DefaultXcIdentifierForFrbrElementDAO
