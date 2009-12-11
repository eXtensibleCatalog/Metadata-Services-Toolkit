/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.transformation.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.services.transformation.bo.HeldHoldingRecord;

/**
 * MySQL implementation of the data access object for the HeldHolding record table
 *
 * @author Sharmila Ranganathan
 */
public class DefaultHeldHoldingRecordDAO extends HeldHoldingRecordDAO
{

	/**
	 * A PreparedStatement to get a HeldHoldingRecord from the database by its ID
	 */
	private static PreparedStatement psGetByHolding004Field = null;

	/**
	 * A PreparedStatement to insert a HeldHoldingRecord into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a HeldHoldingRecord in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a HeldHoldingRecord from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the PreparedStatement to get a HeldHoldingRecord from the database by field 004
	 */
	private static Object psGetByHolding004FieldLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement insert a HeldHoldingRecord into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to update a HeldHoldingRecord in the database
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to delete a HeldHoldingRecord from the database
	 */
	private static Object psDeleteLock = new Object();

	@Override
	public List<HeldHoldingRecord> getByHolding004Field(String field004) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByHolding004FieldLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the HeldHoldingRecord with field 001 " + field004);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a HeldHoldingRecord by field 004 if it hasn't already been created
				if(psGetByHolding004Field == null || dbConnectionManager.isClosed(psGetByHolding004Field))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HELD_HOLDINGS_ID + ", " +
													COL_HOLDING_OAI_ID + ", " +
				                                   COL_HOLDING_004_FIELD+ " " +
				                                  
	                                   "FROM " + HELD_HOLDINGS_TABLE_NAME + " " +
	                                   "WHERE " + COL_HOLDING_004_FIELD + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get HeldHoldingRecord by field 004\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByHolding004Field = dbConnectionManager.prepareStatement(selectSql, psGetByHolding004Field);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByHolding004Field.setString(1, field004);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByHolding004Field);
				
				List<HeldHoldingRecord> heldRecords = new ArrayList<HeldHoldingRecord>();

				// If any results were returned
				if(results.next())
				{
					HeldHoldingRecord heldHoldingRecord = new HeldHoldingRecord();
					heldHoldingRecord.setId(results.getInt(1));
					heldHoldingRecord.setHoldingRecordOAIID(results.getString(2));
					heldHoldingRecord.setHolding004Field(results.getString(3));
					
					heldRecords.add(heldHoldingRecord);

				} // end if(result found)

				// Return the heldHoldingRecord
				return heldRecords;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the HeldHoldingRecord with field 001 " + field004, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByHolding004Field(field004);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByHolding004Field(String)

	@Override
	public boolean insert(HeldHoldingRecord heldHoldingRecord) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the HeldHoldingRecord are valid
		validateFields(heldHoldingRecord, false, true);

		synchronized(psInsertLock)
		{
			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a HeldHoldingRecord if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + HELD_HOLDINGS_TABLE_NAME + " (" + COL_HOLDING_OAI_ID + ", " +
	            	      													        COL_HOLDING_004_FIELD + ") " +
	            				       "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert HeldHoldingRecord\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, heldHoldingRecord.getHoldingRecordOAIID());
				psInsert.setString(2, heldHoldingRecord.getHolding004Field());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Group Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	heldHoldingRecord.setId(rs.getInt(1));

				    return true;
				} // end if(insert succeeded)
				else
					return false;
				
			} // end try(insert the HeldHoldingRecord)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new heldHoldingRecord with the OAI id " + heldHoldingRecord.getHoldingRecordOAIID(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(heldHoldingRecord);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
	} // end insert(HeldHoldingRecord)

	@Override
	public boolean update(HeldHoldingRecord heldHoldingRecord) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the HeldHoldingRecord are valid
		validateFields(heldHoldingRecord, false, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the HeldHoldingRecord with ID " + heldHoldingRecord.getHoldingRecordOAIID());

			try
			{
				// Create a PreparedStatement to update a HeldHoldingRecord if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + HELD_HOLDINGS_TABLE_NAME + " SET " + COL_HOLDING_OAI_ID + "=?, " +
				                                                          COL_HOLDING_004_FIELD  + "=? " +
	                                   "WHERE " + COL_HELD_HOLDINGS_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update HeldHoldingRecord\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, heldHoldingRecord.getHoldingRecordOAIID());
				psUpdate.setString(2, heldHoldingRecord.getHolding004Field());
				psUpdate.setInt(3, heldHoldingRecord.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the heldHoldingRecord)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the heldHoldingRecord with ID " + heldHoldingRecord.getHoldingRecordOAIID(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(heldHoldingRecord);
			}
		} // end synchronized
	} // end update(heldHoldingRecord)

	@Override
	public boolean delete(HeldHoldingRecord heldHoldingRecord) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the heldHoldingRecord with ID " + heldHoldingRecord.getHoldingRecordOAIID());

			try
			{
				// Create the PreparedStatement to delete a heldHoldingRecord if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ HELD_HOLDINGS_TABLE_NAME + " " +
		                               "WHERE " + COL_HELD_HOLDINGS_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete HeldHoldingRecord\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, heldHoldingRecord.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the HeldHoldingRecord with OAI ID " + heldHoldingRecord.getHoldingRecordOAIID(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(heldHoldingRecord);
			}
		} // end synchronized
	} // end method delete(HeldHoldingRecord)
} // end class DefaultHeldHoldingRecordDAO
