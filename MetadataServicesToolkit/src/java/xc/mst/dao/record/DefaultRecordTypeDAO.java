package xc.mst.dao.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.record.RecordType;
import xc.mst.bo.record.ResumptionToken;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * MySQL implementation of the Data Access Object for the record type table
 *
 * @author Vinaykumar Bangera
 */

public class DefaultRecordTypeDAO extends RecordTypeDAO {
	
	/**
	 * A PreparedStatement to get all resumption tokens in the database
	 */
	private static PreparedStatement psGetAll = null;

	/**
	 * A PreparedStatement to get a resumption token from the database by its ID
	 */
	private static PreparedStatement psGetById = null;
	
	/**
	 * A PreparedStatement to get a resumption token from the database by its token
	 */
	private static PreparedStatement psGetByProcessingOrder = null;

	/**
	 * A PreparedStatement to insert a resumption token into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a resumption token in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a resumption token from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the PreparedStatement to get all resumption tokens in the database
	 */
	private static Object psGetAllLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to get a resumption token from the database by its ID
	 */
	private static Object psGetByIdLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to get a resumption token from the database by its token
	 */
	private static Object psGetByProcessingOrderLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to insert a resumption token into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to update a resumption token in the database
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to delete a resumption token from the database
	 */
	private static Object psDeleteLock = new Object();


	@Override
	public List<RecordType> getAll() throws DatabaseConfigException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all resumption tokens");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all resumption tokens
			List<RecordType> recordTypes = new ArrayList<RecordType>();

			try
			{
				// If the PreparedStatement to get all resumption tokens was not defined, create it
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_ID + ", " +
				                                   COL_NAME + ", " +
				                                   COL_PROCESSING_ORDER + " " +
	                                   "FROM " + RECORD_TYPES_TABLE_NAME +
	                                   " ORDER BY " + COL_PROCESSING_ORDER;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all record types\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement
				
				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a ResumptionToken object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the resumption token
					RecordType recordType = new RecordType();

					// Set the fields on the resumption token
					recordType.setId(results.getInt(1));
					recordType.setName(results.getString(2));
					recordType.setProcessingOrder(results.getInt(3));

					// Add the resumption tokens to the list
					recordTypes.add(recordType);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + recordTypes.size() + " resumption tokens in the database.");

				return recordTypes;
			} // end try(get the records
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the resumption tokens.", e);

				return recordTypes;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed in getAll() method of RecordTypeDAO ");
				return getAll();
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public RecordType getById(long id) throws DatabaseConfigException {
		

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the resumption token with ID " + id);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a resumption token by ID was not defined, create it
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_ID + ", " +
                    					COL_NAME + ", " +
                    					COL_PROCESSING_ORDER + " " +
                    					"FROM " + RECORD_TYPES_TABLE_NAME +
	                                   "WHERE " + COL_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get resumption token by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				
				psGetById.setLong(1, id);
				

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the resumption token
					RecordType recordType = new RecordType();

					// Set the fields on the resumption token
					recordType.setId(results.getInt(1));
					recordType.setName(results.getString(2));
					recordType.setProcessingOrder(results.getInt(3));

					if(log.isDebugEnabled())
						log.debug("Found the resumption token with ID " + id + " in the database.");

					// Return the resumption token
					return recordType;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The resumption token with ID " + id + " was not found in the database.");

				return null;
			} // end try(get the resumption token)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the resumption token with ID " + id, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getById(id);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public RecordType getByPorcessingOrder(int processingOrder) throws DatabaseConfigException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByProcessingOrderLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the record type with processing Order " + processingOrder);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a resumption token by token was not defined, create it
				if(psGetByProcessingOrder == null || dbConnectionManager.isClosed(psGetByProcessingOrder))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_ID + ", " +
										COL_NAME + ", " +
										COL_PROCESSING_ORDER + " " +
										"FROM " + RECORD_TYPES_TABLE_NAME +
										"WHERE " + COL_PROCESSING_ORDER + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get resumption token by toeken\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByProcessingOrder = dbConnectionManager.prepareStatement(selectSql, psGetByProcessingOrder);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByProcessingOrder.setInt(1, processingOrder);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByProcessingOrder);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the resumption token
					RecordType recordType = new RecordType();

					// Set the fields on the resumption token
					recordType.setId(results.getInt(1));
					recordType.setName(results.getString(2));
					recordType.setProcessingOrder(results.getInt(3));

					if(log.isDebugEnabled())
						log.debug("Found the resumption token with token " + processingOrder + " in the database.");

					// Return the resumption token
					return recordType;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The resumption token with token " + processingOrder + " was not found in the database.");

				return null;
			} // end try(get the resumption token)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the resumption token with token " + processingOrder, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed in getByPorcessingOrder() method of RecordTypeDAO.");
				return getByPorcessingOrder(processingOrder);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public boolean insert(RecordType recordType) throws DataException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new resumption token with name " + recordType.getName());

			// The ResultSet returned by the query
			ResultSet rs = null;
			try
			{
				// If the PreparedStatement to insert a resumption token was not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + RECORD_TYPES_TABLE_NAME + " (" + COL_NAME + ", " +
																						COL_PROCESSING_ORDER + ") " +
																						"VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert resumption token\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, recordType.getName());
				psInsert.setInt(2, recordType.getProcessingOrder());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated resource identifier ID and set it correctly on this ResumptionToken Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	recordType.setId(rs.getInt(1));

					return true;
				} // end if(insert succeeded)

				return false;
			} // end try(insert the resumption token)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new resumption token with offset " + recordType.getName(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(recordType);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	}

	@Override
	public boolean update(RecordType recordType) throws DataException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
	
		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the resumption token with ID " + recordType.getName());

			try
			{
				// If the PreparedStatement to update a resumption token was not defined, create it
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + RECORD_TYPES_TABLE_NAME + " SET " + COL_NAME + "=?, " +
				                                                          COL_PROCESSING_ORDER + "=? " +
	                                   "WHERE " + COL_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update resumption token\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement was not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, recordType.getName());
				psUpdate.setInt(2, recordType.getProcessingOrder());
				psUpdate.setLong(3, recordType.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the record)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the resumption token with ID " + recordType.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(recordType);
			}
		} // end synchronized
	}

	@Override
	public boolean delete(RecordType recordType) throws DataException {
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the resumption token with ID " + recordType.getId());

			try
			{
				// If the PreparedStatement to delete a resumption token was not defined, create it
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ RECORD_TYPES_TABLE_NAME + " " +
									   "WHERE " + COL_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete resumption token\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setLong(1, recordType.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the record)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the resumption token with ID " + recordType.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(recordType);
			}
		} // end synchronized
	}

}
