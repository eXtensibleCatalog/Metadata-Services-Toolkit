/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
import xc.mst.services.transformation.bo.XCHoldingRecord;

/**
 * MySQL implementation of the data access object for the Holding record table
 *
 * @author Sharmila Ranganathan
 */
public class DefaultXCHoldingDAO extends XCHoldingDAO
{

	/**
	 * A PreparedStatement to get a HoldingRecord from the database by its ID
	 */
	private static PreparedStatement psGetByHolding004Field = null;
	
	/**
	 * A PreparedStatement to get a HoldingRecord from the database by its holding oai ID
	 */
	private static PreparedStatement psGetByHoldingOaiId = null;
	
	/**
	 * A PreparedStatement to get a HoldingRecord from the database by its manifestation Oai id
	 */
	private static PreparedStatement psGetByManifestationOaiId = null;

	/**
	 * A PreparedStatement to insert a HoldingRecord into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a HoldingRecord in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a HoldingRecord from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A PreparedStatement to delete a HoldingRecord from the database
	 */
	private static PreparedStatement psDeleteByHoldingOaiId = null;

	/**
	 * Lock to synchronize access to the PreparedStatement to get a HoldingRecord from the database by field 004
	 */
	private static Object psGetByHolding004FieldLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to get a HoldingRecord from the database by manifestation Oai id
	 */
	private static Object psGetByManifestationOaiIdLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to get a HoldingRecord from the database by holding Oai id
	 */
	private static Object psGetByHoldingOaiIdLock = new Object();
	/**
	 * Lock to synchronize access to the PreparedStatement insert a HoldingRecord into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to update a HoldingRecord in the database
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to delete a HoldingRecord from the database
	 */
	private static Object psDeleteLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to delete a HoldingRecord from the database
	 */
	private static Object psDeleteByHoldingOaiIdLock = new Object();



	@Override
	public List<XCHoldingRecord> getByHolding004Field(String field004) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByHolding004FieldLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the HoldingRecord with field 001 " + field004);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a HoldingRecord by field 004 if it hasn't already been created
				if(psGetByHolding004Field == null || dbConnectionManager.isClosed(psGetByHolding004Field))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_XC_HOLDINGS_ID + ", " +
													COL_HOLDING_OAI_ID + ", " +
				                                   COL_HOLDING_004_FIELD+ ", " +
				                                   COL_MANIFESTATION_OAI_ID + " " +
				                                  
	                                   "FROM " + XC_HOLDINGS_TABLE_NAME + " " +
	                                   "WHERE " + COL_HOLDING_004_FIELD + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get HoldingRecord by field 004\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByHolding004Field = dbConnectionManager.prepareStatement(selectSql, psGetByHolding004Field);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByHolding004Field.setString(1, field004);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByHolding004Field);
				
				List<XCHoldingRecord> records = new ArrayList<XCHoldingRecord>();

				// If any results were returned
				while (results.next())
				{
					XCHoldingRecord holdingRecord = new XCHoldingRecord();
					holdingRecord.setId(results.getInt(1));
					holdingRecord.setHoldingRecordOAIID(results.getString(2));
					holdingRecord.setHolding004Field(results.getString(3));
					holdingRecord.setManifestationOAIId(results.getString(4));
				
					records.add(holdingRecord);
					
				} // end if(result found)


				// Return the holdingRecords
				return records;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the HoldingRecord with field 001 " + field004, e);

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
	public List<XCHoldingRecord> getByHoldingOAIId(String holdingOaiId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByHoldingOaiIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the HoldingRecord with holdoing OAI id " + holdingOaiId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a HoldingRecord by field 004 if it hasn't already been created
				if(psGetByHoldingOaiId == null || dbConnectionManager.isClosed(psGetByHoldingOaiId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_XC_HOLDINGS_ID + ", " +
													COL_HOLDING_OAI_ID + ", " +
				                                   COL_HOLDING_004_FIELD+ ", " +
				                                   COL_MANIFESTATION_OAI_ID + " " +
				                                  
	                                   "FROM " + XC_HOLDINGS_TABLE_NAME + " " +
	                                   "WHERE " + COL_HOLDING_OAI_ID+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get HoldingRecord by holdoing OAI id \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByHoldingOaiId = dbConnectionManager.prepareStatement(selectSql, psGetByHoldingOaiId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByHoldingOaiId.setString(1, holdingOaiId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByHoldingOaiId);
				
				List<XCHoldingRecord> records = new ArrayList<XCHoldingRecord>();

				// If any results were returned
				while (results.next())
				{
					XCHoldingRecord holdingRecord = new XCHoldingRecord();
					holdingRecord.setId(results.getInt(1));
					holdingRecord.setHoldingRecordOAIID(results.getString(2));
					holdingRecord.setHolding004Field(results.getString(3));
					holdingRecord.setManifestationOAIId(results.getString(4));
				
					records.add(holdingRecord);
					
				} // end if(result found)


				// Return the holdingRecords
				return records;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the HoldingRecord with holdoing OAI id  " + holdingOaiId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByHoldingOAIId(holdingOaiId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByHoldingOAIId(String)
	
	@Override
	public List<XCHoldingRecord> getByManifestationOAIId(String manifestationOAIId)  throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByManifestationOaiIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the HoldingRecord with manifestation OAI id " + manifestationOAIId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a HoldingRecord by field 004 if it hasn't already been created
				if(psGetByManifestationOaiId == null || dbConnectionManager.isClosed(psGetByManifestationOaiId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_XC_HOLDINGS_ID + ", " +
													COL_HOLDING_OAI_ID + ", " +
				                                   COL_HOLDING_004_FIELD+ ", " +
				                                   COL_MANIFESTATION_OAI_ID + " " +
				                                  
	                                   "FROM " + XC_HOLDINGS_TABLE_NAME + " " +
	                                   "WHERE " + COL_MANIFESTATION_OAI_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get HoldingRecord by manifestation OAI id\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByManifestationOaiId = dbConnectionManager.prepareStatement(selectSql, psGetByManifestationOaiId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByManifestationOaiId.setString(1, manifestationOAIId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByManifestationOaiId);
				
				List<XCHoldingRecord> records = new ArrayList<XCHoldingRecord>();

				// If any results were returned
				while (results.next())
				{
					XCHoldingRecord holdingRecord = new XCHoldingRecord();
					holdingRecord.setId(results.getInt(1));
					holdingRecord.setHoldingRecordOAIID(results.getString(2));
					holdingRecord.setHolding004Field(results.getString(3));
					holdingRecord.setManifestationOAIId(results.getString(4));
				
					records.add(holdingRecord);
					
				} // end if(result found)


				// Return the holdingRecords
				return records;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the HoldingRecord with manifestation OAI id " + manifestationOAIId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByManifestationOAIId(manifestationOAIId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByManifestationOAIId(String) 

	@Override
	public boolean insert(XCHoldingRecord holdingRecord) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the HoldingRecord are valid
		validateFields(holdingRecord, false, true);

		synchronized(psInsertLock)
		{
			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a HoldingRecord if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + XC_HOLDINGS_TABLE_NAME + " (" + COL_HOLDING_OAI_ID + ", " +
	            	      													        COL_HOLDING_004_FIELD + ", " +
	            	      													      COL_MANIFESTATION_OAI_ID + ") " +
	            				       "VALUES (?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert HoldingRecord\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, holdingRecord.getHoldingRecordOAIID());
				psInsert.setString(2, holdingRecord.getHolding004Field());
				psInsert.setString(3, holdingRecord.getManifestationOAIId());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Group Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	holdingRecord.setId(rs.getInt(1));

				    return true;
				} // end if(insert succeeded)
				else
					return false;
				
			} // end try(insert the HoldingRecord)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new holdingRecord with the OAI id " + holdingRecord.getHoldingRecordOAIID(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(holdingRecord);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
	} // end insert(HoldingRecord)

	@Override
	public boolean update(XCHoldingRecord holdingRecord) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the HoldingRecord are valid
		validateFields(holdingRecord, false, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the HoldingRecord with ID " + holdingRecord.getId());

			try
			{
				// Create a PreparedStatement to update a HoldingRecord if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + XC_HOLDINGS_TABLE_NAME + " SET " + COL_HOLDING_OAI_ID + "=?, " +
				                                                          COL_HOLDING_004_FIELD  + "=?, " +
				                                                          COL_MANIFESTATION_OAI_ID + "=? " +
	                                   "WHERE " + COL_XC_HOLDINGS_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update HoldingRecord\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, holdingRecord.getHoldingRecordOAIID());
				psUpdate.setString(2, holdingRecord.getHolding004Field());
				psUpdate.setString(3, holdingRecord.getManifestationOAIId());
				psUpdate.setInt(4, holdingRecord.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the holdingRecord)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the holdingRecord with ID " + holdingRecord.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(holdingRecord);
			}
		} // end synchronized
	} // end update(holdingRecord)

	@Override
	public boolean delete(XCHoldingRecord holdingRecord) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the holdingRecord with ID " + holdingRecord.getId());

			try
			{
				// Create the PreparedStatement to delete a holdingRecord if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ XC_HOLDINGS_TABLE_NAME + " " +
		                               "WHERE " + COL_XC_HOLDINGS_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete HoldingRecord\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, holdingRecord.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the HoldingRecord with OAI ID " + holdingRecord.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(holdingRecord);
			}
		} // end synchronized
	} // end method delete(HoldingRecord)
	
	@Override
	public boolean deleteByHoldingOAIId(String holdingOAIId) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteByHoldingOaiIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the holdingRecord with ID " + holdingOAIId);

			try
			{
				// Create the PreparedStatement to delete a holdingRecord if it wasn't already defined
				if(psDeleteByHoldingOaiId == null || dbConnectionManager.isClosed(psDeleteByHoldingOaiId))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ XC_HOLDINGS_TABLE_NAME + " " +
		                               "WHERE " + COL_HOLDING_OAI_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete HoldingRecord\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDeleteByHoldingOaiId = dbConnectionManager.prepareStatement(deleteSql, psDeleteByHoldingOaiId);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDeleteByHoldingOaiId.setString(1, holdingOAIId);

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDeleteByHoldingOaiId);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the HoldingRecord with OAI ID " + holdingOAIId, e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return deleteByHoldingOAIId(holdingOAIId);
			}
		} // end synchronized
	} // end method deleteByHoldingOAIId(String)
} // end class DefaultHeldHoldingRecordDAO
