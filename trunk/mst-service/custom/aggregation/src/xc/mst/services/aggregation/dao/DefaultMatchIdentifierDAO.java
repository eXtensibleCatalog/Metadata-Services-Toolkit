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
import java.util.ArrayList;
import java.util.List;

import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.services.aggregation.bo.MatchIdentifiers;

/**
 * MySQL implementation of the data access object for the match identifiers table
 * 
 * @author Sharmila Ranganathan
 *
 */
public class DefaultMatchIdentifierDAO extends MatchIdentifierDAO {
	
	/**
	 * A PreparedStatement to get OAI id by OCLC value
	 */
	private static PreparedStatement psGetByOCLCValue = null;

	/**
	 * A PreparedStatement to get  OAI id by ISBN value
	 */
	private static PreparedStatement psGetByIsbnValue = null;
	
	/**
	 * A PreparedStatement to get OAI id by ISSN value
	 */
	private static PreparedStatement psGetByIssnValue = null;
	
	/**
	 * A PreparedStatement to get OAI id by LCCN value
	 */
	private static PreparedStatement psGetByLccnValue = null;

	/**
	 * A PreparedStatement to get OAI id 
	 */
	private static PreparedStatement psGetByOAIId = null;
	
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
	 * Lock to synchronize access to the PreparedStatement to get OAI id from the database by ID
	 */
	private static Object psGetByOCLCValueLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to get OAI id from the database by ID
	 */
	private static Object psGetByOAIIdLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to get OAI id from the database by oai id
	 */
	private static Object psGetByISSNValueLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to get OAI id from the database by oai id
	 */
	private static Object psGetByISBNValueLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to get OAI id from the database by oai id
	 */
	private static Object psGetByLCCNValueLock = new Object();
	
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
	 * Gets list of OAI identifiers that match the given OCLC value
	 *
	 * @param oclcValue The oclc Value
	 * @return list of OAI identifiers that match the given OCLC value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getByOCLCValue(String oclcValue) throws DatabaseConfigException {
		
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByOCLCValueLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the Oai ids with OCLC Value" + oclcValue);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a bibliographicManifestationMapping by field 001 if it hasn't already been created
				if(psGetByOCLCValue == null || dbConnectionManager.isClosed(psGetByOCLCValue))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_OAI_ID  + " " +
	                                   "FROM " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_OCLC_VALUE + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get OAI id by  OCLC value \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByOCLCValue = dbConnectionManager.prepareStatement(selectSql, psGetByOCLCValue);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByOCLCValue.setString(1, oclcValue);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByOCLCValue);
				
				List<String> oclcValues = new ArrayList<String>();

				// If any results were returned
				while(results.next())
				{
					oclcValues.add(results.getString(1));
				} // end if(result found)

				// Return the bibliographicManifestationMapping
				return oclcValues;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the OAI id with oclc value " + oclcValue, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByOCLCValue(oclcValue);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	}
	
	/**
	 * Gets list of OAI identifiers that match the given LCCN value
	 *
	 * @param lccnValue The LCCN Value
	 * @return list of OAI identifiers that match the given LCCN value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getByLCCNValue(String lccnValue) throws DatabaseConfigException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByLCCNValueLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the Oai ids with LCCN Value" + lccnValue);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get OAI id by lccn value if it hasn't already been created
				if(psGetByLccnValue == null || dbConnectionManager.isClosed(psGetByLccnValue))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_OAI_ID  + " " +
				                                  
	                                   "FROM " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_LCCN_VALUE+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get OAI id by  LCCN value \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByLccnValue = dbConnectionManager.prepareStatement(selectSql, psGetByLccnValue);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByLccnValue.setString(1, lccnValue);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByLccnValue);
				
				List<String> lccnValues = new ArrayList<String>();

				// If any results were returned
				while(results.next())
				{
					lccnValues.add(results.getString(1));
				} // end if(result found)

				// Return the OAI id
				return lccnValues;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the OAI id with oclc value " + lccnValue, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByLCCNValue(lccnValue);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}
	
	/**
	 * Gets list of OAI identifiers that match the given ISBN value
	 *
	 * @param isbnValue The ISBN Value
	 * @return list of OAI identifiers that match the given ISBN value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getByISBNValue(String isbnValue) throws DatabaseConfigException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByISBNValueLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the Oai ids with ISBN Value" + isbnValue);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get OAI id by ISBN value if it hasn't already been created
				if(psGetByIsbnValue == null || dbConnectionManager.isClosed(psGetByIsbnValue))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_OAI_ID  + " " +
	                                   "FROM " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_ISBN_VALUE+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get OAI id by ISBN value \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByIsbnValue = dbConnectionManager.prepareStatement(selectSql, psGetByIsbnValue);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByIsbnValue.setString(1, isbnValue);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByIsbnValue);
				
				List<String> isbnValues = new ArrayList<String>();

				// If any results were returned
				while(results.next())
				{
					isbnValues.add(results.getString(1));
				} // end if(result found)

				// Return the OAI id
				return isbnValues;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the OAI id with ISBN value " + isbnValue, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByLCCNValue(isbnValue);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}
	
	/**
	 * Gets list of OAI identifiers that match the given ISSN value
	 *
	 * @param issnValue The issn Value
	 * @return list of OAI identifiers that match the given ISSN value
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public List<String> getByISSNValue(String issnValue) throws DatabaseConfigException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByISSNValueLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the Oai ids with ISSN Value" + issnValue);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get OAI id by ISSN value if it hasn't already been created
				if(psGetByIssnValue == null || dbConnectionManager.isClosed(psGetByIssnValue))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_OAI_ID  + " " +
				                                  
	                                   "FROM " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_ISSN_VALUE+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get OAI id by ISSN value \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByIssnValue = dbConnectionManager.prepareStatement(selectSql, psGetByIssnValue);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByIssnValue.setString(1, issnValue);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByIssnValue);
				
				List<String> issnValues = new ArrayList<String>();

				// If any results were returned
				while(results.next())
				{
					issnValues.add(results.getString(1));
				} // end if(result found)

				// Return the OAI id
				return issnValues;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the OAI id with ISBN value " + issnValue, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByLCCNValue(issnValue);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized

	}
	
	/**
	 * Inserts match identifiers for a record into the database
	 *
	 * @param matchIdentifiers The mapping to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for inserting
	 */
	public boolean insert(MatchIdentifiers matchIdentifiers) throws DataException {
		
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the match idenifiers are valid
		validateFields(matchIdentifiers, false, true);

		synchronized(psInsertLock)
		{
			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a BibliographicManifestationMapping if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " (" +  COL_OAI_ID + ", " +
	            	      													        COL_ISBN_VALUE + ", " +
	            	      													        COL_LCCN_VALUE + ", " +
	            	      													        COL_OCLC_VALUE + ", " +
	            	      													        COL_ISSN_VALUE + ") " +
	            				       "VALUES (?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert matchIdentifiers\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, matchIdentifiers.getOaiId());
				psInsert.setString(2, matchIdentifiers.getIsbnValue());
				psInsert.setString(3, matchIdentifiers.getLccnValue());
				psInsert.setString(4, matchIdentifiers.getOclcValue());
				psInsert.setString(5, matchIdentifiers.getIsbnValue());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Group Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	matchIdentifiers.setId(rs.getInt(1));

				    return true;
				} // end if(insert succeeded)
				else
					return false;

				
			} // end try(insert the BibliographicManifestationMapping)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new identifiers with the OAI id " + matchIdentifiers.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(matchIdentifiers);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
		
	}

	/**
	 * Updates match identifiers for a record in the database
	 *
	 * @param matchIdentifiers The mapping to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for updating
	 */
	public boolean update(MatchIdentifiers matchIdentifiers) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the matchIdentifiers are valid
		validateFields(matchIdentifiers, false, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the matchIdentifiers with ID " + matchIdentifiers.getOaiId());

			try
			{
				// Create a PreparedStatement to update a matchIdentifiers if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " SET " + COL_OAI_ID + "=?, " +
				                                                          COL_OCLC_VALUE + "=?, " +
				                                                          COL_LCCN_VALUE + "=? " +
				                                                          COL_ISSN_VALUE + "=? " +
				                                                          COL_ISBN_VALUE + "=? " +
				                                                          
	                                   "WHERE " + COL_OAI_ID+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update Identifiers\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, matchIdentifiers.getOaiId());
				psUpdate.setString(2, matchIdentifiers.getOclcValue());
				psUpdate.setString(3, matchIdentifiers.getLccnValue());
				psUpdate.setString(4, matchIdentifiers.getIssnValue());
				psUpdate.setString(5, matchIdentifiers.getIsbnValue());
				psUpdate.setString(6, matchIdentifiers.getOaiId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the matchIdentifiers)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the identifiers with ID " + matchIdentifiers.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(matchIdentifiers);
			}
		} // end synchronized

		
		
	}

	/**
	 * Deletes match identifiers for a record from the database
	 *
	 * @param matchIdentifiers The mapping to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for deleting
	 */
	public boolean delete(MatchIdentifiers matchIdentifiers) throws DataException {

		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the match identifiers with ID " + matchIdentifiers.getOaiId());

			try
			{
				// Create the PreparedStatement to delete a match identifiers if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
		                               "WHERE " + COL_OAI_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete identifiers\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, matchIdentifiers.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the BibliographicManifestationMapping with OAI ID " + matchIdentifiers.getOaiId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(matchIdentifiers);
			}
		} // end synchronized

	}

	/**
	 * Deletes a match identifiers for a record from the database for given OAI id
	 *
	 * @param oaiId The OAI id to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed match identifiers was not valid for deleting
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
					String deleteSql = "DELETE FROM "+ IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
		                               "WHERE " + COL_OAI_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete identifiers\" PreparedStatement the SQL " + deleteSql);

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
	 * Get the match identifiers by given Oai id
	 *
	 * @param oaiId The OAI id
	 * @return Match identifier
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public MatchIdentifiers getByOaiId(String oaiId) throws DatabaseConfigException {
		
			// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the match identifiers with OAI id Value" + oaiId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get OAI id by lccn value if it hasn't already been created
				if(psGetByOAIId == null || dbConnectionManager.isClosed(psGetByOAIId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + IN_PROCESSED_IDENTIFIERS_ID + "," 
												 + COL_OAI_ID + ", " 
												 + COL_OCLC_VALUE + ", " 
												 + COL_ISBN_VALUE + ", "
												 + COL_ISSN_VALUE  + ", " 
												 + COL_LCCN_VALUE + " " +
	                                   "FROM " + IN_PROCESSED_IDENTIFIERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_OAI_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get OAI id by  LCCN value \" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByOAIId = dbConnectionManager.prepareStatement(selectSql, psGetByOAIId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByOAIId.setString(1, oaiId);

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByOAIId);
				MatchIdentifiers matchIdentifiers = new MatchIdentifiers();
				while(results.next()) {
					// If any results were returned
					matchIdentifiers.setId(results.getInt(1));
					matchIdentifiers.setOaiId(results.getString(2));
					matchIdentifiers.setOclcValue(results.getString(3));
					matchIdentifiers.setIsbnValue(results.getString(4));
					matchIdentifiers.setIssnValue(results.getString(5));
					matchIdentifiers.setLccnValue(results.getString(6));
				}

				// Return the OAI id
				return matchIdentifiers;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the OAI id with oclc value " + oaiId, e);

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
}
