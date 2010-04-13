package xc.mst.services.aggregation.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

public class DefaultPredecessorUtilDAO extends PredecessorUtilDAO {
	
	/**
	 * A PreparedStatement to get by output record id from the database
	 */
	private static PreparedStatement psGetByOutputRecordId = null;
	
	/**
	 * A PreparedStatement to insert predecessors
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to delete predecessors from the database
	 */
	private static PreparedStatement psDelete = null;
	
	/**
	 * Lock to synchronize access to the PreparedStatement to get by output record id from the database
	 */
	private static Object psGetByOutputRecordIdLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement insert a predecessors into the database
	 */
	private static Object psInsertLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to delete a predecessors from the database
	 */
	private static Object psDeleteLock = new Object();

	/**
	 * Gets list of predecessor OAI identifiers that match the given output record id
	 *
	 * @param outputRecordId The output record Id
	 * @return list of predecessor OAI identifiers that match the given output record id
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getByOutputRecordId(int outputRecordId) throws DatabaseConfigException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByOutputRecordIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the HeldRecord with OAI Id value " + outputRecordId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a output record by OAI Id if it hasn't already been created
				if(psGetByOutputRecordId == null || dbConnectionManager.isClosed(psGetByOutputRecordId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_PREDECESSOR_OAI_ID +
									   "FROM " + PREDECESSOR_RECORD_TABLE_NAME + " " +
	                                   "WHERE " + COL_OUTPUT_RECORD_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get output record by OAI id \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByOutputRecordId = dbConnectionManager.prepareStatement(selectSql, psGetByOutputRecordId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByOutputRecordId.setInt(1, outputRecordId);

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByOutputRecordId);
				
				List<String> oaiIds = new ArrayList<String>();
				
				while(results.next()) {
					
					oaiIds.add(results.getString(1));
				}

				// Return the output records
				return oaiIds;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the predecessor oai ids for output record id" + outputRecordId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByOutputRecordId(outputRecordId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}

	/**
	 * Inserts predecessor record into the database
	 *
	 * @param outputRecordId The output record id
	 * @param predecessorOAIId Predecessor OAI id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for inserting
	 */
	public boolean insert(int outputRecordId, String predecessorOAIId) throws DataException {

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
					String insertSql = "INSERT INTO " + PREDECESSOR_RECORD_TABLE_NAME + " (" +  COL_OUTPUT_RECORD_ID + ", " +
	            	      													        COL_PREDECESSOR_OAI_ID + ") " +
	            				       "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert predecessors\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setInt(1, outputRecordId);
				psInsert.setString(2, predecessorOAIId);

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
				log.error("A SQLException occurred while inserting a new predecessor with the OAI id " + predecessorOAIId, e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(outputRecordId, predecessorOAIId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
		

	}

	/**
	 * Deletes the predecessors of given output record id 
	 *
	 * @param outputRecordId The output record id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for deleting
	 */
	public boolean deletePredecessorsForOutputRecordId(int outputRecordId) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the predecessors for output record with ID " + outputRecordId);

			try
			{
				// Create the PreparedStatement to delete a match identifiers if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ PREDECESSOR_RECORD_TABLE_NAME + " " +
		                               "WHERE " + COL_OUTPUT_RECORD_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete predecessors for output record\" PreparedStatement the SQL " + deleteSql);

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
				log.error("A SQLException occurred while deleting the predecessors for output record ID " + outputRecordId, e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return deletePredecessorsForOutputRecordId(outputRecordId);
			}
		} // end synchronized

	}
	
}
