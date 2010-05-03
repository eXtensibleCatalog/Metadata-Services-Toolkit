/**
  * Copyright (c) 2009 University of Rochester
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
import java.util.ArrayList;
import java.util.List;

import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.services.aggregation.bo.OutputRecord;

/**
 * Accesses output record in the database
 *
 * @author Sharmila Ranganathan
 */
public class DefaultOutputRecordDAO extends OutputRecordDAO
{
	
	/**
	 * A PreparedStatement to get output record by OAI Id
	 */
	private static PreparedStatement psGetByOAIId = null;
	
	/**
	 * A PreparedStatement to get successor OAI ids by input OAI Id
	 */
	private static PreparedStatement psGetSuccesorByOAIId = null;

	/**
	 * A PreparedStatement to insert identifiers
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update identifiers in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete identifiers from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A PreparedStatement to delete identifiers by OAI id from the database
	 */
	private static PreparedStatement psDeleteByOAIId = null;

	/**
	 * Lock to synchronize access to the PreparedStatement to get output record from the database
	 */
	private static Object psGetByOAIIdLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to get output record from the database
	 */
	private static Object psGetSuccessorByOAIIdLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement insert a identifiers into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to update a identifiers in the database
	 */
	private static Object psUpdateLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to delete a identifiers by OAI id  from the database
	 */
	private static Object psDeleteByOAIIdLock = new Object();
	
	/**
	 * Predecessor util DAO
	 */
	private PredecessorUtilDAO predecessorUtilDAO = new DefaultPredecessorUtilDAO();

	/**
	 * Gets the successor record for the given OAI id
	 *
	 * @param oaiId The OAI Id
	 * @return list of successor OAI ids that match the given OAI identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getSuccessorByOaiId(String oaiId) throws DatabaseConfigException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetSuccessorByOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the succesor OAI ids for OAI Id value " + oaiId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a output record by OAI Id if it hasn't already been created
				if(psGetSuccesorByOAIId == null || dbConnectionManager.isClosed(psGetSuccesorByOAIId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + OUTPUT_RECORD_TABLE_NAME + "." + COL_OAI_ID +  
	                                   " FROM " + OUTPUT_RECORD_TABLE_NAME + ", " + PredecessorUtilDAO.PREDECESSOR_RECORD_TABLE_NAME +  " " +
	                                   " WHERE " + OUTPUT_RECORD_TABLE_NAME + "." +OUTPUT_RECORD_ID + "=" + PredecessorUtilDAO.PREDECESSOR_RECORD_TABLE_NAME + "." + PredecessorUtilDAO.COL_OUTPUT_RECORD_ID + 
	                                   " AND " + PredecessorUtilDAO.PREDECESSOR_RECORD_TABLE_NAME + "." + PredecessorUtilDAO.COL_PREDECESSOR_OAI_ID + "=?";
					log.info("selectSql="+selectSql);
					if(log.isDebugEnabled())
						log.debug("Creating the \"get successor by OAI id \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetSuccesorByOAIId = dbConnectionManager.prepareStatement(selectSql, psGetSuccesorByOAIId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetSuccesorByOAIId.setString(1, oaiId);

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetSuccesorByOAIId);
				
				List<String> sucessorOAIIds = new ArrayList<String>();
				
				while(results.next()) {
					sucessorOAIIds.add(results.getString(1));
				}

				// Return the output records
				return sucessorOAIIds;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the successors of OAI id " + oaiId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getSuccessorByOaiId(oaiId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}

	/**
	 * Gets list of OAI identifiers that match the given OAI id
	 *
	 * @param oaiId The OAI Id
	 * @return list of records that match the given OAI identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public OutputRecord getByOaiId(String oaiId) throws DatabaseConfigException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the output record with OAI Id value " + oaiId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a output record by OAI Id if it hasn't already been created
				if(psGetByOAIId == null || dbConnectionManager.isClosed(psGetByOAIId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + OUTPUT_RECORD_ID +
												   COL_OAI_ID  +
												   COL_UPDATED +
												   COL_XML +
	                                   "FROM " + OUTPUT_RECORD_TABLE_NAME + " " +
	                                   "WHERE " + COL_OAI_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get output record by OAI id \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByOAIId = dbConnectionManager.prepareStatement(selectSql, psGetByOAIId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByOAIId.setString(1, oaiId);

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByOAIId);

				OutputRecord outputRecord = new OutputRecord();
				
				while(results.next()) {
					
					outputRecord.setId(results.getInt(1));
					outputRecord.setOaiId(results.getString(2));
					outputRecord.setUpdated(results.getBoolean(3));
					outputRecord.setXml(results.getString(4));
					
					outputRecord.setPredecessorOaiId(predecessorUtilDAO.getByOutputRecordId(outputRecord.getId()));
			
				}

				// Return the output records
				return outputRecord;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the output record with OAI id " + oaiId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByOaiId(oaiId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}
	
	/**
	 * Gets list of output records that match the given uplink OAI id
	 *
	 * @param uplinkOaiId Uplink OAI Id
	 * @return list of records that match the given uplink
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getByUplink(String uplinkOaiId) throws DatabaseConfigException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the output record with Uplink value " + uplinkOaiId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a output record by Uplink if it hasn't already been created
				if(psGetByOAIId == null || dbConnectionManager.isClosed(psGetByOAIId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + OUTPUT_RECORD_TABLE_NAME + "." + COL_OAI_ID +  
					                    " FROM " + OUTPUT_RECORD_TABLE_NAME + ", " + UplinksUtilDAO.UPLINK_TABLE_NAME +  " " +
					                    " WHERE " + OUTPUT_RECORD_TABLE_NAME + "." +OUTPUT_RECORD_ID + "=" + UplinksUtilDAO.UPLINK_TABLE_NAME + "." + UplinksUtilDAO.COL_OUTPUT_RECORD_ID + 
					                    " AND " + UplinksUtilDAO.UPLINK_TABLE_NAME + "." + UplinksUtilDAO.COL_UPLINK_OAI_ID+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get output record by Uplink \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByOAIId = dbConnectionManager.prepareStatement(selectSql, psGetByOAIId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByOAIId.setString(1, uplinkOaiId);

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByOAIId);
				
				List<String> oaiIds = new ArrayList<String>();
				
				while(results.next()) {
					oaiIds.add(results.getString(1));
				}


				// Return the output records
				return oaiIds;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the record with Uplink " + uplinkOaiId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByUplink(uplinkOaiId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}
	
	/**
	 * Inserts output record into the database
	 *
	 * @param outputRecord The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed output record was not valid for inserting
	 */
	public boolean insert(OutputRecord outputRecord) throws DataException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the output record are valid
		validateFields(outputRecord, false, true);

		synchronized(psInsertLock)
		{
			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a outputRecord if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + OUTPUT_RECORD_TABLE_NAME  + " (" +  COL_OAI_ID + ", " +
	            	      													        COL_UPDATED + "," +
	            	      													        COL_XML + ") " +
	            				       "VALUES (?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert outputRecord\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, outputRecord.getOaiId());
				psInsert.setBoolean(2, outputRecord.isUpdated());
				psInsert.setString(3, outputRecord.getXml());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Group Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	outputRecord.setId(rs.getInt(1));

				    boolean success = true;

				    // Add the predecessors
				    for(String predecessorOAIId : outputRecord.getPredecessorOaiIds())
                    {
				    	success = predecessorUtilDAO.insert(outputRecord.getId(), predecessorOAIId);
                    }

					return success;

				} // end if(insert succeeded)
				else
					return false;

				
			} // end try(insert the BibliographicManifestationMapping)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new outputRecord with the OAI id " + outputRecord.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(outputRecord);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
	}

	/**
	 * Updates output record in the database
	 *
	 * @param outputRecord The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed output record was not valid for updating
	 */
	public boolean update(OutputRecord outputRecord) throws DataException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the outputRecord are valid
		validateFields(outputRecord, false, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the outputRecord with ID " + outputRecord.getOaiId());

			try
			{
				// Create a PreparedStatement to update a outputRecord if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + OUTPUT_RECORD_TABLE_NAME + " SET " + COL_OAI_ID + "=?, " +
				                                                          COL_UPDATED + "=?, " +
				                                                          COL_XML + "=? " +
				                                                          
	                                   "WHERE " + COL_OAI_ID+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update Identifiers\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, outputRecord.getOaiId());
				psUpdate.setBoolean(2, outputRecord.isUpdated());
				psUpdate.setString(3, outputRecord.getXml());

				// Execute the update statement and return the result
				boolean success = dbConnectionManager.executeUpdate(psUpdate) > 0;
				
				// Delete the old predecessors for output record
				predecessorUtilDAO.deletePredecessorsForOutputRecordId(outputRecord.getId());
				
			    // Add the predecessors
			    for(String predecessorOAIId : outputRecord.getPredecessorOaiIds())
                {
			    	success = predecessorUtilDAO.insert(outputRecord.getId(), predecessorOAIId);
                }

				return success;

			} // end try(update the outputRecord)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the identifiers with ID " + outputRecord.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(outputRecord);
			}
		} // end synchronized
	}

	/**
	 * Deletes output record from the database for given OAI id
	 *
	 * @param oaiId The OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed output record was not valid for deleting
	 */
	public boolean deleteByOAIId(String oaiId) throws DataException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteByOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the output record with OAI ID " + oaiId);

			try
			{
				// Create the PreparedStatement to delete a match identifiers if it wasn't already defined
				if(psDeleteByOAIId == null || dbConnectionManager.isClosed(psDeleteByOAIId))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ OUTPUT_RECORD_TABLE_NAME + " " +
		                               "WHERE " + COL_OAI_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete output record\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDeleteByOAIId = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDeleteByOAIId.setString(1, oaiId);

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDeleteByOAIId);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the identifiers with OAI ID " + oaiId, e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return deleteByOAIId(oaiId);
			}
		} // end synchronized

	}

	/**
	 * Validates the fields on the passed output record Object
	 *
	 * @param outputRecord The mapping to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed user were invalid
	 */
	protected void validateFields(OutputRecord outputRecord, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(outputRecord.getOaiId() == null || outputRecord.getOaiId().length() <= 0 || outputRecord.getOaiId().length() > 500)
				errorMessage.append("The OAI id is invalid. ");

			
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method validateFields(OutputRecord, boolean, boolean)
} 
