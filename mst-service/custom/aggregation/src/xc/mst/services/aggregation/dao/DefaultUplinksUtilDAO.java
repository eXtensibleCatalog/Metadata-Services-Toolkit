/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.aggregation.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Data access methods for record's uplink
 *  
 * @author Sharmila Ranganathan
 *
 */
public class DefaultUplinksUtilDAO extends UplinksUtilDAO {
	
	/**
	 * A PreparedStatement to insert uplinks
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to delete uplinks from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the PreparedStatement insert a uplinks into the database
	 */
	private static Object psInsertLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to delete a uplinks from the database
	 */
	private static Object psDeleteLock = new Object();

	/**
	 * Inserts uplinks for a record into the database
	 *
	 * @param outputRecordId The output record id
	 * @param uplinkOAIId Uplink OAI id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for inserting
	 */
	public boolean insert(int outputRecordId, String uplinkOAIId) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psInsertLock)
		{
			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a heldRecord if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + UPLINK_TABLE_NAME + " (" +  COL_OUTPUT_RECORD_ID + ", " +
	            	      													        COL_UPLINK_OAI_ID + ") " +
	            				       "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert uplinkss\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setInt(1, outputRecordId);
				psInsert.setString(2, uplinkOAIId);

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
				    return true;
				} // end if(insert succeeded)
				else
					return false;

				
			} // end try(insert the BibliographicManifestationMapping)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a uplinks with the OAI id " + uplinkOAIId, e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(outputRecordId, uplinkOAIId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
		

	}

	/**
	 * Deletes the uplinks of given output record id 
	 *
	 * @param outputRecordId The output record id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for deleting
	 */
	public boolean deleteUplinksByOutputRecordId(int outputRecordId) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the uplinkss for output record with ID " + outputRecordId);

			try
			{
				// Create the PreparedStatement to delete a match identifiers if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ UPLINK_TABLE_NAME + " " +
		                               "WHERE " + COL_OUTPUT_RECORD_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete uplinks for output record\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, outputRecordId);

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the uplinks for output record ID " + outputRecordId, e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return deleteUplinksByOutputRecordId(outputRecordId);
			}
		} // end synchronized

	}
	
}
