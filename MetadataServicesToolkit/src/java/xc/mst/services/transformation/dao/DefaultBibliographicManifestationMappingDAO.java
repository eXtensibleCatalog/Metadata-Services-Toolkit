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
import xc.mst.services.transformation.bo.BibliographicManifestationMapping;

/**
 * MySQL implementation of the data access object for the BibliographicManifestationMapping table
 *
 * @author Sharmila Ranganathan
 */
public class DefaultBibliographicManifestationMappingDAO extends BibliographicManifestationMappingDAO
{

	/**
	 * A PreparedStatement to get a BibliographicManifestationMapping from the database by its ID
	 */
	private static PreparedStatement psGetByBibliographic001Field = null;

	/**
	 * A PreparedStatement to get a BibliographicManifestationMapping from the database by its bib OAI id
	 */
	private static PreparedStatement psGetByBibliographicOAIId = null;
	
	/**
	 * A PreparedStatement to insert a BibliographicManifestationMapping into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a BibliographicManifestationMapping in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a BibliographicManifestationMapping from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the PreparedStatement to get a BibliographicManifestationMappings from the database by ID
	 */
	private static Object psGetByBibliographic001FieldLock = new Object();
	
	/**
	 * Lock to synchronize access to the PreparedStatement to get a BibliographicManifestationMappings from the database by oai id
	 */
	private static Object psGetByBibliographicOAIIdLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement insert a BibliographicManifestationMapping into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to update a BibliographicManifestationMapping in the database
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to delete a BibliographicManifestationMapping from the database
	 */
	private static Object psDeleteLock = new Object();

	@Override
	public List<BibliographicManifestationMapping> getByBibliographic001Field(String field001) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByBibliographic001FieldLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the BibliographicManifestationMapping with field 001 " + field001);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a bibliographicManifestationMapping by field 001 if it hasn't already been created
				if(psGetByBibliographic001Field == null || dbConnectionManager.isClosed(psGetByBibliographic001Field))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_BIBLIOGRAPHIC_MANIFESTATION_ID + ", " +
												   COL_BIBLIOGRAPHIC_OAI_ID + ", " +
				                                   COL_MANIFESTATION_OAI_ID + ", " +
				                                   COL_BIBLIOGRAPHIC_001_FIELD + " " +
				                                  
	                                   "FROM " + MARC_BIBLIOGRAPHIC_TO_XC_MANIFESTATION_MAPPING_TABLE_NAME + " " +
	                                   "WHERE " + COL_BIBLIOGRAPHIC_001_FIELD + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get BibliographicManifestationMapping by field 001\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByBibliographic001Field = dbConnectionManager.prepareStatement(selectSql, psGetByBibliographic001Field);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByBibliographic001Field.setString(1, field001);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByBibliographic001Field);
				
				List<BibliographicManifestationMapping> bibliographicManifestationMappings = new ArrayList<BibliographicManifestationMapping>();

				// If any results were returned
				while(results.next())
				{
					BibliographicManifestationMapping bibliographicManifestationMapping = new BibliographicManifestationMapping();
					bibliographicManifestationMapping.setId(results.getInt(1));
					bibliographicManifestationMapping.setBibliographicRecordOAIId(results.getString(2));
					bibliographicManifestationMapping.setManifestationRecordOAIId(results.getString(3));
					bibliographicManifestationMapping.setBibliographicRecord001Field(results.getString(4));
					
					bibliographicManifestationMappings.add(bibliographicManifestationMapping);
					
				} // end if(result found)


				// Return the bibliographicManifestationMapping
				return bibliographicManifestationMappings;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the BibliographicManifestationMapping with field 001 " + field001, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByBibliographic001Field(field001);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByBibliographic001Field(String)
	
	@Override
	public BibliographicManifestationMapping getByBibliographicOAIId(String bibliographicOAIId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByBibliographicOAIIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the BibliographicManifestationMapping with OAI id " + bibliographicOAIId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a bibliographicManifestationMapping by OAI id if it hasn't already been created
				if(psGetByBibliographicOAIId == null || dbConnectionManager.isClosed(psGetByBibliographicOAIId))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_BIBLIOGRAPHIC_MANIFESTATION_ID + ", " +
												   COL_BIBLIOGRAPHIC_OAI_ID + ", " +
				                                   COL_MANIFESTATION_OAI_ID + ", " +
				                                   COL_BIBLIOGRAPHIC_001_FIELD + " " +
				                                  
	                                   "FROM " + MARC_BIBLIOGRAPHIC_TO_XC_MANIFESTATION_MAPPING_TABLE_NAME + " " +
	                                   "WHERE " + COL_BIBLIOGRAPHIC_OAI_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get BibliographicManifestationMapping by OAI id\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByBibliographicOAIId = dbConnectionManager.prepareStatement(selectSql, psGetByBibliographicOAIId);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetByBibliographicOAIId.setString(1, bibliographicOAIId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByBibliographicOAIId);

				// If any results were returned
				if(results.next())
				{
					BibliographicManifestationMapping bibliographicManifestationMapping = new BibliographicManifestationMapping();
					bibliographicManifestationMapping.setId(results.getInt(1));
					bibliographicManifestationMapping.setBibliographicRecordOAIId(results.getString(2));
					bibliographicManifestationMapping.setManifestationRecordOAIId(results.getString(3));
					bibliographicManifestationMapping.setBibliographicRecord001Field(results.getString(4));
					
					// Return the bibliographicManifestationMapping
					return bibliographicManifestationMapping;
				} // end if(result found)


				return null;
			} 
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the BibliographicManifestationMapping with OAI Id " + bibliographicOAIId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed " + e.getMessage());
				return getByBibliographicOAIId(bibliographicOAIId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByBibliographicOAIId(String)

	@Override
	public boolean insert(BibliographicManifestationMapping bibliographicManifestationMapping) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the BibliographicManifestationMapping are valid
		validateFields(bibliographicManifestationMapping, false, true);

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
					String insertSql = "INSERT INTO " + MARC_BIBLIOGRAPHIC_TO_XC_MANIFESTATION_MAPPING_TABLE_NAME + " (" +  COL_BIBLIOGRAPHIC_OAI_ID + ", " +
	            	      													        COL_MANIFESTATION_OAI_ID + ", " +
	            	      													        COL_BIBLIOGRAPHIC_001_FIELD + ") " +
	            				       "VALUES (?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert BibliographicManifestationMapping\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, bibliographicManifestationMapping.getBibliographicRecordOAIId());
				psInsert.setString(2, bibliographicManifestationMapping.getManifestationRecordOAIId());
				psInsert.setString(3, bibliographicManifestationMapping.getBibliographicRecord001Field());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Group Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	bibliographicManifestationMapping.setId(rs.getInt(1));

				    return true;
				} // end if(insert succeeded)
				else
					return false;

				
			} // end try(insert the BibliographicManifestationMapping)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new bibliographicManifestationMapping with the OAI id " + bibliographicManifestationMapping.getBibliographicRecordOAIId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(bibliographicManifestationMapping);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close the ResultSet)
		} // end synchronized
	} // end insert(BibliographicManifestationMapping)

	@Override
	public boolean update(BibliographicManifestationMapping bibliographicManifestationMapping) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the BibliographicManifestationMapping are valid
		validateFields(bibliographicManifestationMapping, false, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the BibliographicManifestationMapping with ID " + bibliographicManifestationMapping.getBibliographicRecordOAIId());

			try
			{
				// Create a PreparedStatement to update a BibliographicManifestationMapping if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + MARC_BIBLIOGRAPHIC_TO_XC_MANIFESTATION_MAPPING_TABLE_NAME + " SET " + COL_BIBLIOGRAPHIC_OAI_ID + "=?, " +
				                                                          COL_MANIFESTATION_OAI_ID + "=?, " +
				                                                          COL_BIBLIOGRAPHIC_001_FIELD + "=? " +
	                                   "WHERE " + COL_BIBLIOGRAPHIC_MANIFESTATION_ID+ "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update BibliographicManifestationMapping\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, bibliographicManifestationMapping.getBibliographicRecordOAIId());
				psUpdate.setString(2, bibliographicManifestationMapping.getManifestationRecordOAIId());
				psUpdate.setString(3, bibliographicManifestationMapping.getBibliographicRecord001Field());
				psUpdate.setInt(4, bibliographicManifestationMapping.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the bibliographicManifestationMapping)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the bibliographicManifestationMapping with ID " + bibliographicManifestationMapping.getBibliographicRecordOAIId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(bibliographicManifestationMapping);
			}
		} // end synchronized
	} // end update(bibliographicManifestationMapping)

	@Override
	public boolean delete(BibliographicManifestationMapping bibliographicManifestationMapping) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the bibliographicManifestationMapping with ID " + bibliographicManifestationMapping.getBibliographicRecordOAIId());

			try
			{
				// Create the PreparedStatement to delete a bibliographicManifestationMapping if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ MARC_BIBLIOGRAPHIC_TO_XC_MANIFESTATION_MAPPING_TABLE_NAME + " " +
		                               "WHERE " + COL_BIBLIOGRAPHIC_MANIFESTATION_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete BibliographicManifestationMapping\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, bibliographicManifestationMapping.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the BibliographicManifestationMapping with OAI ID " + bibliographicManifestationMapping.getBibliographicRecordOAIId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(bibliographicManifestationMapping);
			}
		} // end synchronized
	} // end method delete(BibliographicManifestationMapping)
} // end class DefaultBibliographicManifestationMappingDAO
