/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.harvest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.harvest.Harvest;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.ProviderDAO;

public class DefaultHarvestDAO extends HarvestDAO
{
	/**
	 * The DAO for getting and inserting harvest schedules
	 */
	HarvestScheduleDAO harvestScheduleDao = new DefaultHarvestScheduleDAO();

	/**
	 * The DAO for getting and inserting providers
	 */
	ProviderDAO providerDao = new DefaultProviderDAO();
	
	/**
	 * A PreparedStatement to get all harvests in the database
	 */
	private static PreparedStatement psGetAll = null;

	/**
	 * A PreparedStatement to get a harvest from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get all harvests in the database for a given harvest schedule
	 */
	private static PreparedStatement psGetByHarvestScheduleId = null;

	/**
	 * A PreparedStatement to insert a harvest into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a harvest in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a harvest from the database
	 */
	private static PreparedStatement psDelete = null;
	
	/**
	 * A PreparedStatement to get latest harvest end time for a schedule
	 */
	private static PreparedStatement psGetLatestHarvestEndTime = null;	

	/**
	 * Lock to synchronize access to the get all PreparedStatement
	 */
	private static Object psGetAllLock = new Object();

	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetByIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by harvest schedule ID PreparedStatement
	 */
	private static Object psGetByHarvestScheduleIdLock = new Object();

	/**
	 * Lock to synchronize access to the insert PreparedStatement
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to synchronize access to the update PreparedStatement
	 */
	private static Object psUpdateLock = new Object();

	/**
	 * Lock to synchronize access to the delete PreparedStatement
	 */
	private static Object psDeleteLock = new Object();
	
	/**
	 * Lock to synchronize access toget latest harvest end time for a schedule
	 */
	private static Object psGetLatestHarvestEndTimeLock = new Object();

	@Override
	public List<Harvest> getAll() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all harvests");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all harvests
			List<Harvest> harvests = new ArrayList<Harvest>();

			try
			{
				// Create the PreparedStatement to get all harvests if it wasn't already created
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_HARVEST_ID + ", " +
				                                   COL_START_TIME + ", " +
				                                   COL_END_TIME + ", " +
				                                   COL_REQUEST + ", " +
				                                   COL_RESULT + ", " +
				                                   COL_HARVEST_SCHEDULE_ID + ", " +
				                                   COL_PROVIDER_ID + " " +
	                                   "FROM " + HARVESTS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all harvests\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a Harvest object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the harvest
					Harvest harvest = new Harvest();

					// Set the fields on the harvest
					harvest.setId(results.getInt(1));
					harvest.setStartTime(results.getTime(2));
					harvest.setEndTime(results.getTime(3));
					harvest.setRequest(results.getString(4));
					harvest.setResult(results.getString(5));
					harvest.setHarvestSchedule(harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(6)));
					harvest.setProvider(providerDao.getById(results.getInt(7)));

					// Add the harvest to the list
					harvests.add(harvest);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + harvests.size() + " harvests in the database.");

				return harvests;
			} // end try(get the harvests)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvests.", e);

				return harvests;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getAll

	@Override
	public Harvest getById(int harvestId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the harvest with ID " + harvestId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatement to get a harvest by its ID if it hasn't already been defined
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_ID + ", " +
	                                               COL_START_TIME + ", " +
	                                               COL_END_TIME + ", " +
	                                               COL_REQUEST + ", " +
	                                               COL_RESULT + ", " +
	                                               COL_HARVEST_SCHEDULE_ID + ", " +
				                                   COL_PROVIDER_ID + " " +
	                                   "FROM " + HARVESTS_TABLE_NAME + " " +
 				                       "WHERE " + COL_HARVEST_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetById.setInt(1, harvestId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the set
					Harvest harvest = new Harvest();

					// Set the fields on the harvest
					harvest.setId(results.getInt(1));
					harvest.setStartTime(results.getTimestamp(2));
					harvest.setEndTime(results.getTimestamp(3));
					harvest.setRequest(results.getString(4));
					harvest.setResult(results.getString(5));
					harvest.setHarvestSchedule(harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(6)));
					harvest.setProvider(providerDao.getById(results.getInt(7)));

					if(log.isDebugEnabled())
						log.debug("Found the harvest with ID " + harvestId + " in the database.");

					// Return the harvest
					return harvest;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The harvest with ID " + harvestId + " was not found in the database.");

				return null;
			} // end try(get the harvest
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest with ID " + harvestId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public Harvest loadBasicHarvest(int harvestId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the harvest with ID " + harvestId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatement to get a harvest by its ID if it hasn't already been defined
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_ID + ", " +
	                                               COL_START_TIME + ", " +
	                                               COL_END_TIME + ", " +
	                                               COL_REQUEST + ", " +
	                                               COL_RESULT + ", " +
	                                               COL_HARVEST_SCHEDULE_ID + ", " +
				                                   COL_PROVIDER_ID + " " +
	                                   "FROM " + HARVESTS_TABLE_NAME + " " +
				                       "WHERE " + COL_HARVEST_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetById.setInt(1, harvestId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the set
					Harvest harvest = new Harvest();

					// Set the fields on the harvest
					harvest.setId(results.getInt(1));
					harvest.setStartTime(results.getTime(2));
					harvest.setEndTime(results.getTime(3));
					harvest.setRequest(results.getString(4));
					harvest.setResult(results.getString(5));
					harvest.setHarvestSchedule(harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(6)));
					harvest.setProvider(providerDao.getById(results.getInt(7)));

					if(log.isDebugEnabled())
						log.debug("Found the harvest with ID " + harvestId + " in the database.");

					// Return the harvest
					return harvest;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The harvest with ID " + harvestId + " was not found in the database.");

				return null;
			} // end try(get the harvest
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest with ID " + harvestId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method loadBasicHarvest(int)

	@Override
	public List<Harvest> getHarvestsForSchedule(int harvestScheduleId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByHarvestScheduleIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all harvests with harvest schedule ID " + harvestScheduleId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all harvests
			List<Harvest> harvests = new ArrayList<Harvest>();

			try
			{
				// Create the PreparedStatement to get all harvests for a given harvest schedule if it hasn't already been defined
				if(psGetByHarvestScheduleId == null || dbConnectionManager.isClosed(psGetByHarvestScheduleId))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_HARVEST_ID + ", " +
				                                   COL_START_TIME + ", " +
				                                   COL_END_TIME + ", " +
				                                   COL_REQUEST + ", " +
				                                   COL_RESULT + ", " +
				                                   COL_HARVEST_SCHEDULE_ID + ", " +
				                                   COL_PROVIDER_ID + " " +
	                                   "FROM " + HARVESTS_TABLE_NAME + " " +
	                                   "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest by harvest schedule ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByHarvestScheduleId = dbConnectionManager.prepareStatement(selectSql, psGetByHarvestScheduleId);
				} // end if(get by harvest schedule ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetByHarvestScheduleId.setInt(1, harvestScheduleId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByHarvestScheduleId);

				// For each result returned, add a Harvest object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the harvest
					Harvest harvest = new Harvest();

					// Set the fields on the harvest
					harvest.setId(results.getInt(1));
					harvest.setStartTime(results.getTime(2));
					harvest.setEndTime(results.getTimestamp(3));
					harvest.setRequest(results.getString(4));
					harvest.setResult(results.getString(5));
					harvest.setHarvestSchedule(harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(6)));
					harvest.setProvider(providerDao.getById(results.getInt(7)));

					// Add the harvest to the list
					harvests.add(harvest);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + harvests.size() + " harvests with harvest schedule ID " + harvestScheduleId);

				return harvests;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvests with harvest schedule ID " + harvestScheduleId, e);

				return harvests;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getHarvestsForSchedule(int)

	@Override
	public Timestamp getLatestHarvestEndTimeForSchedule(int harvestScheduleId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetLatestHarvestEndTimeLock)
		{
			if(log.isDebugEnabled())
				log.debug("Get latest harvest end time with  harvest schedule ID " + harvestScheduleId);
	
			// The ResultSet from the SQL query
			ResultSet results = null;
			
			Timestamp endTime = null;
	
			try
			{
				// Create the PreparedStatement to get latest harvest end time for a given harvest schedule if it hasn't already been defined
				if(psGetLatestHarvestEndTime == null || dbConnectionManager.isClosed(psGetLatestHarvestEndTime))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + "MAX("  + COL_END_TIME + ") " +
	                                   "FROM " + HARVESTS_TABLE_NAME + " " +
	                                   "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";
	
					if(log.isDebugEnabled())
						log.debug("Creating the \"get latest harvest end time by harvest schedule ID\" PreparedStatement from the SQL " + selectSql);
	
					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetLatestHarvestEndTime = dbConnectionManager.prepareStatement(selectSql, psGetLatestHarvestEndTime);
				} // end if(get by harvest schedule ID PreparedStatement not defined)
	
				// Set the parameters on the PreparedStatement
				psGetLatestHarvestEndTime.setInt(1, harvestScheduleId);
	
				// Get the result of the SELECT statement
	
				// Execute the query
				results = dbConnectionManager.executeQuery(psGetLatestHarvestEndTime);
				results.next();
				endTime = results.getTimestamp(1);
	
				if(log.isDebugEnabled())
					log.debug("Found harvests end time " + endTime);
	
				return endTime;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest latest end time with harvest schedule ID " + harvestScheduleId, e);
	
				return endTime;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getHarvestsForSchedule(int)

	@Override
	public boolean insert(Harvest harvest) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the harvest are valid
		validateFields(harvest, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new harvest with request " + harvest.getRequest());

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
					// Create the PreparedStatement to insert a harvest if it hasn't already been defined
					if(psInsert == null || dbConnectionManager.isClosed(psInsert))
					{
						// SQL to insert the new row
						String insertSql = "INSERT INTO " + HARVESTS_TABLE_NAME + " (" + COL_START_TIME + ", " +
		            	    													COL_END_TIME + ", " +
		            	    													COL_REQUEST + ", " +
		            	    													COL_RESULT + ", " +
		            	    													COL_HARVEST_SCHEDULE_ID + ", " +
		            	    													COL_PROVIDER_ID + ") " +
		            				       "VALUES (?, ?, ?, ?, ?, ?)";

						if(log.isDebugEnabled())
							log.debug("Creating the \"insert harvest\" PreparedStatement from the SQL " + insertSql);

						// A prepared statement to run the insert SQL
						// This should sanitize the SQL and prevent SQL injection
						psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
					} // end if(insert PreparedStatement not defined)

					// Set the parameters on the insert statement
					psInsert.setTimestamp(1, harvest.getStartTime());
					psInsert.setTimestamp(2, harvest.getEndTime());
					psInsert.setString(3, harvest.getRequest());
					psInsert.setString(4, harvest.getResult());
					psInsert.setInt(5, harvest.getHarvestSchedule().getId());
					psInsert.setInt(6, harvest.getProvider().getId());

					// Execute the insert statement and return the result
					if(dbConnectionManager.executeUpdate(psInsert) > 0)
					{
						// Get the auto-generated resource identifier ID and set it correctly on this Harvest Object
						rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

					    if (rs.next())
					        harvest.setId(rs.getInt(1));

						return true;
					} // end if(insert succeeded)

					return false;
			} // end try(insert harvest)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new harvest with request " + harvest.getRequest(), e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(Harvest)

	@Override
	public boolean update(Harvest harvest) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the harvest are valid
		validateFields(harvest, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the harvest with ID " + harvest.getId());

			try
			{
				// If the PreparedStatement to update a harvest has not been created, create it
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + HARVESTS_TABLE_NAME + " SET " + COL_START_TIME + "=?, " +
				                                                          COL_END_TIME + "=?, " +
				                                                          COL_REQUEST + "=?, " +
				                                                          COL_RESULT + "=?, " +
				                                                          COL_HARVEST_SCHEDULE_ID + "=?, " +
				                                                          COL_PROVIDER_ID + "=? " +
	                                   "WHERE " + COL_HARVEST_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update harvest\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement is not defined)

				// Set the parameters on the update statement
				psUpdate.setTimestamp(1, harvest.getStartTime());
				psUpdate.setTimestamp(2, harvest.getEndTime());
				psUpdate.setString(3, harvest.getRequest());
				psUpdate.setString(4, harvest.getResult());
				psUpdate.setInt(5, harvest.getHarvestSchedule().getId());
				psUpdate.setInt(6, harvest.getProvider().getId());
				psUpdate.setInt(7, harvest.getId());

				// Execute the update statement and return the result
				return dbConnectionManager.executeUpdate(psUpdate) > 0;
			} // end try(update harvest)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the harvest with ID " + harvest.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method update(Harvest)

	@Override
	public boolean delete(Harvest harvest) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the ID field on the harvest are valid
		validateFields(harvest, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the harvest with ID " + harvest.getId());

			try
			{
				// Create the PreparedStatement to delete a harvest if it hasn't already been created
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ HARVESTS_TABLE_NAME + " " +
		                               "WHERE " + COL_HARVEST_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete harvest\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, harvest.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the harvest with ID " + harvest.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method delete(Harvest)
} // end class DefaultHarvestDAO
