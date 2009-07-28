/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.provider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.provider.Set;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * MySQL implementation of the data access object for the sets table
 *
 * @author Eric Osisek
 */
public class DefaultSetDAO extends SetDAO
{
	/**
	 * A PreparedStatement to get all sets in the database
	 */
	private static PreparedStatement psGetAll = null;

	/**
	 * A PreparedStatement to get a set from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get a set from the database by its setSpec
	 */
	private static PreparedStatement psGetBySetSpec = null;

	/**
	 * A PreparedStatement to get sets from the database by their provider ID
	 */
	private static PreparedStatement psGetByProviderId = null;

	/**
	 * A PreparedStatement to insert a set into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to add a set to a provider in the database
	 */
	private static PreparedStatement psAddToProvider = null;

	/**
	 * A PreparedStatement to remove a set from a provider in the database
	 */
	private static PreparedStatement psRemoveFromProvider = null;

	/**
	 * A PreparedStatement to update a set in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a set from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the get all PreparedStatement
	 */
	private static Object psGetAllLock = new Object();

	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetByIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by setSpec PreparedStatement
	 */
	private static Object psGetBySetSpecLock = new Object();

	/**
	 * Lock to synchronize access to the get by provider ID PreparedStatement
	 */
	private static Object psGetByProviderIdLock = new Object();

	/**
	 * Lock to synchronize access to the insert PreparedStatement
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to add a set to a provider in the database
	 */
	private static Object psAddToProviderLock = new Object();

	/**
	 * Lock to synchronize access to the PreparedStatement to remove a set from a provider in the database
	 */
	private static Object psRemoveFromProviderLock = new Object();

	/**
	 * Lock to synchronize access to the update PreparedStatement
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the delete PreparedStatement
	 */
	private static Object psDeleteLock = new Object();

	@Override
	public List<Set> getAll() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all sets");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all sets
			List<Set> sets = new ArrayList<Set>();

			try
			{
				// Create the PreparedStatment to get all sets if it hasn't already been created
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + ", " +
												   COL_DISPLAY_NAME + ", " +
												   COL_DESCRIPTION + ", " +
												   COL_SET_SPEC + ", " +
												   COL_PROVIDER_SET + ", " +
												   COL_RECORD_SET + ", " +
												   COL_PROVIDER_ID + " " +
								       "FROM " + SETS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all sets\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a Set object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the set
					Set set = new Set();

					// Set the columns on the set
					set.setId(results.getInt(1));
					set.setDisplayName(results.getString(2));
					set.setDescription(results.getString(3));
					set.setSetSpec(results.getString(4));
					set.setIsProviderSet(results.getBoolean(5));
					set.setIsRecordSet(results.getBoolean(6));

					// Add the set to the list
					sets.add(set);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + sets.size() + " sets in the database.");

				return sets;
			} // end try (get sets)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the sets.", e);

				return sets;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getAll()

	@Override
	public Set getById(int setId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the set with ID " + setId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a set by it's ID if it hasn't already been created
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + ", " +
												   COL_DISPLAY_NAME + ", " +
												   COL_DESCRIPTION + ", " +
												   COL_SET_SPEC + ", " +
												   COL_PROVIDER_SET + ", " +
												   COL_RECORD_SET + ", " +
												   COL_PROVIDER_ID + " " +
								       "FROM " + SETS_TABLE_NAME + " " +
								       "WHERE " + COL_SET_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get set by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetById.setInt(1, setId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// For each result returned, add a Set object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the set
					Set set = new Set();

					// Set the columns on the set
					set.setId(results.getInt(1));
					set.setDisplayName(results.getString(2));
					set.setDescription(results.getString(3));
					set.setSetSpec(results.getString(4));
					set.setIsProviderSet(results.getBoolean(5));
					set.setIsRecordSet(results.getBoolean(6));

					if(log.isDebugEnabled())
						log.debug("Found the set in the database with ID " + setId + ".");

					// Add the set to the list
					return set;
				} // end if(set found)

				if(log.isDebugEnabled())
					log.debug("Could not find the set in the database with ID " + setId + ".");

				return null;
			} // end try(get set)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the set with ID " + setId + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public Set loadBasicSet(int setId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the set with ID " + setId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a set by it's ID if it hasn't already been created
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + ", " +
												   COL_DISPLAY_NAME + ", " +
												   COL_DESCRIPTION + ", " +
												   COL_SET_SPEC + ", " +
												   COL_PROVIDER_SET + ", " +
												   COL_RECORD_SET + ", " +
												   COL_PROVIDER_ID + " " +
								       "FROM " + SETS_TABLE_NAME + " " +
								       "WHERE " + COL_SET_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get set by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetById.setInt(1, setId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// For each result returned, add a Set object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the set
					Set set = new Set();

					// Set the columns on the set
					set.setId(results.getInt(1));
					set.setDisplayName(results.getString(2));
					set.setDescription(results.getString(3));
					set.setSetSpec(results.getString(4));
					set.setIsProviderSet(results.getBoolean(5));
					set.setIsRecordSet(results.getBoolean(6));

					if(log.isDebugEnabled())
						log.debug("Found the set in the database with ID " + setId + ".");

					// Add the set to the list
					return set;
				} // end if(set found)

				if(log.isDebugEnabled())
					log.debug("Could not find the set in the database with ID " + setId + ".");

				return null;
			} // end try(get set)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the set with ID " + setId + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method loadBasicSet(int)

	@Override
	public Set getBySetSpec(String setSpec) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetBySetSpecLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the set with setSpec " + setSpec + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a set by it's setSpec if it hasn't already been created
				if(psGetBySetSpec == null || dbConnectionManager.isClosed(psGetBySetSpec))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + ", " +
												   COL_DISPLAY_NAME + ", " +
												   COL_DESCRIPTION + ", " +
												   COL_SET_SPEC + ", " +
												   COL_PROVIDER_SET + ", " +
												   COL_RECORD_SET + ", " +
												   COL_PROVIDER_ID + " " +
								       "FROM " + SETS_TABLE_NAME + " " +
								       "WHERE " + COL_SET_SPEC + " LIKE ?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get set by setSpec\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetBySetSpec = dbConnectionManager.prepareStatement(selectSql, psGetBySetSpec);
				} // end if(get by setSpec PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetBySetSpec.setString(1, setSpec);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetBySetSpec);

				// For each result returned, add a Set object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the set
					Set set = new Set();

					// Set the columns on the set
					set.setId(results.getInt(1));
					set.setDisplayName(results.getString(2));
					set.setDescription(results.getString(3));
					set.setSetSpec(results.getString(4));
					set.setIsProviderSet(results.getBoolean(5));
					set.setIsRecordSet(results.getBoolean(6));

					if(log.isDebugEnabled())
						log.debug("Found the set in the database with setSpec " + setSpec + ".");

					// Add the set to the list
					return set;
				} // end if(set found)

				if(log.isDebugEnabled())
					log.debug("Could not find the set in the database with setSpec " + setSpec + ".");

				return null;
			} // end try(get set)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the set with setSpec " + setSpec + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getBySetSpec(String)

	@Override
	public List<Set> getSetsForProvider(int providerId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByProviderIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the set with provider ID " + providerId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;


			ArrayList<Set> sets = new ArrayList<Set>();

			try
			{
				// Create the PreparedStatment to get a set by it's ID if it hasn't already been created
				if(psGetByProviderId == null || dbConnectionManager.isClosed(psGetByProviderId))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + ", " +
												   COL_DISPLAY_NAME + ", " +
												   COL_DESCRIPTION + ", " +
												   COL_SET_SPEC + ", " +
												   COL_PROVIDER_SET + ", " +
												   COL_RECORD_SET + ", " +
												   COL_PROVIDER_ID + " " +
								       "FROM " + SETS_TABLE_NAME + " " +
								       "WHERE " + COL_PROVIDER_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get sets by provider ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByProviderId = dbConnectionManager.prepareStatement(selectSql, psGetByProviderId);
				} // end if(get by provider ID PreparedStatement wasn't defined)

				// Set the parameters on the PreparedStatement
				psGetByProviderId.setInt(1, providerId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByProviderId);

				// For each result returned, add a Set object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the set
					Set set = new Set();

					// Set the columns on the set
					set.setId(results.getInt(1));
					set.setDisplayName(results.getString(2));
					set.setDescription(results.getString(3));
					set.setSetSpec(results.getString(4));
					set.setIsProviderSet(results.getBoolean(5));
					set.setIsRecordSet(results.getBoolean(6));

					if(log.isDebugEnabled())
						log.debug("Found the set in the database with provider ID " + providerId + ".");

					// Add the set to the list
					sets.add(set);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + sets.size() + " sets in the database with provider ID " + providerId + ".");

				return sets;
			} // end try(get sets)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the set with provider ID " + providerId + ".", e);

				return sets;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getSetsForProvider(int)

	@Override
	public boolean insert(Set set) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the set are valid
		validateFields(set, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new set with the setSpec " + set.getSetSpec());

			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a set if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + SETS_TABLE_NAME + " (" + COL_DISPLAY_NAME + ", " +
	            	      													COL_DESCRIPTION + ", " +
	            	      													COL_SET_SPEC + ", " +
	            	      													COL_PROVIDER_SET + ", " +
	            	      													COL_RECORD_SET + ", " +
	            	      													COL_PROVIDER_ID + ") " +
	            				       "VALUES (?, ?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert set\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, set.getDisplayName());
				psInsert.setString(2, set.getDescription());
				psInsert.setString(3, set.getSetSpec());
				psInsert.setBoolean(4, set.getIsProviderSet());
				psInsert.setBoolean(5, set.getIsRecordSet());
				psInsert.setInt(6, 0);

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated resource identifier ID and set it correctly on this Set Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        set.setId(rs.getInt(1));

					return true;
				} // end if(insert succeeded)
				else
					return false;
			} // end try(insert the set)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new set with the setSpec " + set.getSetSpec(), e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(Set)

	@Override
	public boolean insertForProvider(Set set, int providerId) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the set are valid
		validateFields(set, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new set with the setSpec " + set.getSetSpec());

			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a set if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + SETS_TABLE_NAME + " (" + COL_DISPLAY_NAME + ", " +
	            	      													COL_DESCRIPTION + ", " +
	            	      													COL_SET_SPEC + ", " +
	            	      													COL_PROVIDER_SET + ", " +
	            	      													COL_RECORD_SET + ", " +
	            	      													COL_PROVIDER_ID + ") " +
	            				       "VALUES (?, ?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert set\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setString(1, set.getDisplayName());
				psInsert.setString(2, set.getDescription());
				psInsert.setString(3, set.getSetSpec());
				psInsert.setBoolean(4, set.getIsProviderSet());
				psInsert.setBoolean(5, set.getIsRecordSet());
				psInsert.setInt(6, providerId);

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated resource identifier ID and set it correctly on this Set Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        set.setId(rs.getInt(1));

					return true;
				} // end if(insert succeeded)
				else
					return false;
			} // end try(insert the set)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new set with the setSpec " + set.getSetSpec(), e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(Set)

	@Override
	public boolean addToProvider(Set set, int providerId) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the set are valid
		validateFields(set, true, true);

		synchronized(psAddToProviderLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the set with ID " + set.getId());

			try
			{
				// Create a PreparedStatement to update a set if it wasn't already created
				if(psAddToProvider == null || dbConnectionManager.isClosed(psAddToProvider))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + SETS_TABLE_NAME + " SET " + COL_PROVIDER_ID + "=?, " +
					                                                           COL_PROVIDER_SET + "=?, " +
					                                                           COL_RECORD_SET + "=? " +
	                                   "WHERE " + COL_SET_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"add set to provider\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psAddToProvider = dbConnectionManager.prepareStatement(updateSql, psAddToProvider);
				} // end if(update PreparedStatemnt wasn't defined)

				// Set the parameters on the update statement
				psAddToProvider.setInt(1, providerId);
				psAddToProvider.setBoolean(2, true);
				psAddToProvider.setBoolean(3, false);
				psAddToProvider.setInt(4, set.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psAddToProvider) > 0;
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the set with ID " + set.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method addToProvider(Set, int)

	@Override
	public boolean removeFromProvider(Set set, int providerId) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the set are valid
		validateFields(set, true, true);

		synchronized(psRemoveFromProviderLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the set with ID " + set.getId());

			try
			{
				// Create a PreparedStatement to update a set if it wasn't already created
				if(psRemoveFromProvider == null || dbConnectionManager.isClosed(psRemoveFromProvider))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + SETS_TABLE_NAME + " SET " + COL_PROVIDER_ID + "=0, " +
                                                                               COL_PROVIDER_SET + "=?, " +
                                                                               COL_RECORD_SET + "=? " +
	                                   "WHERE " + COL_SET_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove set from provider\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psRemoveFromProvider = dbConnectionManager.prepareStatement(updateSql, psRemoveFromProvider);
				} // end if(update PreparedStatemnt wasn't defined)

				// Set the parameters on the update statement
				psRemoveFromProvider.setBoolean(1, false);
				psRemoveFromProvider.setBoolean(2, true);
				psRemoveFromProvider.setInt(3, set.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psRemoveFromProvider) > 0;
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the set with ID " + set.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method addToProvider(Set, int)

	@Override
	public boolean update(Set set) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the set are valid
		validateFields(set, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the set with ID " + set.getId());

			try
			{
				// Create a PreparedStatement to update a set if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + SETS_TABLE_NAME + " SET " + COL_DISPLAY_NAME + "=?, " +
				                                                          COL_DESCRIPTION + "=?, " +
				                                                          COL_SET_SPEC + "=?, " +
				                                                          COL_PROVIDER_SET + "=?, " +
				                                                          COL_RECORD_SET + "=? " +
	                                   "WHERE " + COL_SET_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update set\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatemnt wasn't defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, set.getDisplayName());
				psUpdate.setString(2, set.getDescription());
				psUpdate.setString(3, set.getSetSpec());
				psUpdate.setBoolean(4, set.getIsProviderSet());
				psUpdate.setBoolean(5, set.getIsRecordSet());
				psUpdate.setInt(6, set.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the set with ID " + set.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method update(Set)

	@Override
	public boolean delete(Set set) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the ID field on the set are valid
		validateFields(set, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the set with ID " + set.getId());

			try
			{
				// Create the PreparedStatement to delete a set if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ SETS_TABLE_NAME + " " +
		                               "WHERE " + COL_SET_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete set\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, set.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete the set)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the set with ID " + set.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method delete(Set)
} // end class DefaultSetDAO
