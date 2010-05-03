package xc.mst.services.aggregation.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.services.aggregation.bo.HeldRecord;

/**
 * MySQL implementation of the data access object for the held record table
 * 
 * @author Sharmila Ranganathan
 *
 */
public class DefaultHeldRecordDAO extends HeldRecordDAO {
	
	/**
	 * A PreparedStatement to get held record by parent OAI Id
	 */
	private static PreparedStatement psGetByParentOAIId = null;

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
	 * Lock to synchronize access to the PreparedStatement to get held record from the database
	 */
	private static Object psGetByParentOAIIdLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement insert a identifiers into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to update a identifiers in the database
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to delete a identifiers from the database
	 */
	private static Object psDeleteLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to delete a identifiers by OAI id  from the database
	 */
	private static Object psDeleteByOAIIdLock = new Object();
	
	/**
	 * Gets list of held OAI identifiers that match the given parent OAI id
	 *
	 * @param parentOaiId The parent OAI Id
	 * @return list of held records that match the given OAI identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<HeldRecord> getByParentOaiId(String parentOaiId) throws DatabaseConfigException {		
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByParentOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the HeldRecord with parent OAI Id value " + parentOaiId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a HeldRecord by parent OAI Id if it hasn't already been created
				if(psGetByParentOAIId == null || dbConnectionManager.isClosed(psGetByParentOAIId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + HELD_RECORD_ID +
												   COL_OAI_ID  +
												   COL_PARENT_OAI_ID +
	                                   "FROM " + HELD_RECORD_TABLE_NAME+ " " +
	                                   "WHERE " + COL_PARENT_OAI_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get Held record by parent OAI id \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByParentOAIId = dbConnectionManager.prepareStatement(selectSql, psGetByParentOAIId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByParentOAIId.setString(1, parentOaiId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByParentOAIId);
				
				List<HeldRecord> heldRecords = new ArrayList<HeldRecord>();
				
				while(results.next()) {
					
					HeldRecord heldRecord = new HeldRecord();
					heldRecord.setOaiId(results.getString(2));
					heldRecord.addParentOaiId(results.getString(3));
					heldRecord.setId(results.getInt(1));
					
					heldRecords.add(heldRecord);
				}

				// Return the held records
				return heldRecords;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the OAI id with oclc value " + parentOaiId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByParentOaiId(parentOaiId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}
	
	/**
	 * Inserts held record into the database
	 *
	 * @param heldRecord The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for inserting
	 */
	public boolean insert(HeldRecord heldRecord) throws DataException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the held record are valid
		validateFields(heldRecord, false, true);

		synchronized(psInsertLock)
		{
			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				for (String parentOaiId : heldRecord.getParentOaiIds()) {

					// Build the PreparedStatement to insert a heldRecord if it wasn't already created
					if(psInsert == null || dbConnectionManager.isClosed(psInsert))
					{
						
						// SQL to insert the new row
						String insertSql = "INSERT INTO " + HELD_RECORD_TABLE_NAME + " (" +  COL_OAI_ID + ", " +
		            	      													        COL_PARENT_OAI_ID + ") " +
		            				       "VALUES (?, ?)";
	
						if(log.isDebugEnabled())
							log.debug("Creating the \"insert heldRecord\" PreparedStatemnt from the SQL " + insertSql);
	
						// A prepared statement to run the insert SQL
						// This should sanitize the SQL and prevent SQL injection
						psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
					} // end if(insert PreparedStatement not defined)
	
					// Set the parameters on the insert statement
					psInsert.setString(1, heldRecord.getOaiId());
					psInsert.setString(2, parentOaiId);
	
					// Execute the insert statement and return the result
					if(dbConnectionManager.executeUpdate(psInsert) > 0)
					{
						// Get the auto-generated user ID and set it correctly on this Group Object
						rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
	
					    if (rs.next())
					    	heldRecord.setId(rs.getInt(1));
	
					} // end if(insert succeeded)
				}
				
			    return true;
				
			} // end try(insert the BibliographicManifestationMapping)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new heldRecord with the OAI id " + heldRecord.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(heldRecord);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
		
	}

	/**
	 * Updates held record in the database
	 *
	 * @param heldRecord The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for updating
	 */
	public boolean update(HeldRecord heldRecord) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the heldRecord are valid
		validateFields(heldRecord, false, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the heldRecord with ID " + heldRecord.getOaiId());

			try
			{
				for(String parentOaiId: heldRecord.getParentOaiIds()) {
					// Create a PreparedStatement to update a heldRecord if it wasn't already created
					if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
					{
						// SQL to update new row
						String updateSql = "UPDATE " + HELD_RECORD_TABLE_NAME + " SET " + COL_OAI_ID + "=?, " +
					                                                          COL_PARENT_OAI_ID + "=? " +
					                                                          
		                                   "WHERE " + COL_OAI_ID+ "=?";
	
						if(log.isDebugEnabled())
							log.debug("Creating the \"update Identifiers\" PreparedStatement from the SQL " + updateSql);
	
						// A prepared statement to run the update SQL
						// This should sanitize the SQL and prevent SQL injection
						psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
					} // end if(update PreparedStatement not defined)
	
					// Set the parameters on the update statement
					psUpdate.setString(1, heldRecord.getOaiId());
					psUpdate.setString(2, parentOaiId);
					psUpdate.setString(3, heldRecord.getOaiId());
	
					// Execute the update statement and return the result
					dbConnectionManager.executeUpdate(psUpdate);
				}
				
				return true;
			} // end try(update the heldRecord)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the identifiers with ID " + heldRecord.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(heldRecord);
			}
		} // end synchronized
	}

	/**
	 * Deletes held record from the database
	 *
	 * @param heldRecord The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for deleting
	 */
	public boolean delete(HeldRecord heldRecord) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the held recor with ID " + heldRecord.getOaiId());

			try
			{
				// Create the PreparedStatement to delete a match identifiers if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ HELD_RECORD_TABLE_NAME + " " +
		                               "WHERE " + COL_OAI_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete held record\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, heldRecord.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the held record with OAI ID " + heldRecord.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(heldRecord);
			}
		} // end synchronized

	}

	/**
	 * Deletes held record from the database for given OAI id
	 *
	 * @param oaiId The OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed held record was not valid for deleting
	 */
	public boolean deleteByOAIId(String oaiId) throws DataException {
		 
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteByOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the match identifiers with OAI ID " + oaiId);

			try
			{
				// Create the PreparedStatement to delete a match identifiers if it wasn't already defined
				if(psDeleteByOAIId == null || dbConnectionManager.isClosed(psDeleteByOAIId))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ HELD_RECORD_TABLE_NAME + " " +
		                               "WHERE " + COL_OAI_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete held record\" PreparedStatement the SQL " + deleteSql);

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


}
