/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.harvest;

import java.sql.Date;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.ProviderDAO;

/**
 * MySQL implementation of the Data Access Object for the harvest schedules table
 *
 * @author Eric Osisek
 */
public class DefaultHarvestScheduleDAO extends HarvestScheduleDAO
{
	/**
	 * The DAO for getting and inserting providers
	 */
	private static ProviderDAO providerDao = new DefaultProviderDAO();

	/**
	 * The DAO for getting and inserting harvest schedule steps
	 */
	private static HarvestScheduleStepDAO harvestScheduleStepDao = new DefaultHarvestScheduleStepDAO();

	/**
	 * A PreparedStatement to get all harvest schedules in the database
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * A PreparedStatement to get a harvest schedule from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get a harvest schedule from the database by its name
	 */
	private static PreparedStatement psGetByName = null;

    /**
	 * A PreparedStatement to get a harvest schedule from the database by its Provider ID
	 */
	private static PreparedStatement psGetByProviderId = null;

	/**
	 * A PreparedStatement to get harvest schedules from the database by their hour, day of week, and minute
	 */
	private static PreparedStatement psGetByTime = null;

	/**
	 * A PreparedStatement to insert a harvest Schedule into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a harvest schedule in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a harvest schedule from the database
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
	 * Lock to synchronize access to the get by name PreparedStatement
	 */
	private static Object psGetByNameLock = new Object();

	/**
	 * Lock to synchronize access to the get by provider ID PreparedStatement
	 */
	private static Object psGetByProviderIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by time PreparedStatement
	 */
	private static Object psGetByTimeLock = new Object();

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

	@Override
	public List<HarvestSchedule> getAll()
	{
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all harvest schedules");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// A list to hold the results of the query
			ArrayList<HarvestSchedule> harvestSchedules = new ArrayList<HarvestSchedule>();

			try
			{
				// If the PreparedStatement to get all harvest schedules was not defined, create it
				if(psGetAll == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                                                   COL_SCHEDULE_NAME + ", " +
                                                   COL_RECURRENCE + ", " +
                                                   COL_PROVIDER_ID + ", " +
                                                   COL_START_DATE + ", " +
                                                   COL_END_DATE + ", " +
                                                   COL_MINUTE + ", " +
                                                   COL_DAY_OF_WEEK + ", " +
                                                   COL_HOUR + ", " +
                                                   COL_NOTIFY_EMAIL + ", " +
                                                   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all harvest schedules\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnection.prepareStatement(selectSql);
				} // end if(get all PreparedStatement wasn't defined)

				// Get the results of the SELECT statement

				// Execute the query
				results = psGetAll.executeQuery();

				// If any results were returned
				while(results.next())
				{
					// The Object which will contain data on the user
					HarvestSchedule harvestSchedule = new HarvestSchedule();

					// Set the fields on the user
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setProvider(providerDao.loadBasicProvider(results.getInt(4)));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));

					// Return the harvest schedule
					harvestSchedules.add(harvestSchedule);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + harvestSchedules.size() + " harvest schedules in the database.");

				return harvestSchedules;
			} // end try(get the harvest schedules)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest schedules", e);

				return harvestSchedules;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getAll()

	@Override
	public List<HarvestSchedule> getSorted(boolean asc,String columnSorted)
	{
		if(log.isDebugEnabled())
			log.debug("Getting all harvest schedules sorted in " + (asc ? "ascending" : "descending") + " order on the column " + columnSorted);
		
		// Validate the column we're trying to sort on
		if(!sortableColumns.contains(columnSorted))
		{
			log.error("An attempt was made to sort on the invalid column " + columnSorted);
			return getAll();
		} // end if(sort column invalid)
		
		// The ResultSet from the SQL query
		ResultSet results = null;
		
		// The Statement for getting the rows
		Statement getSorted = null;
		
		// A list to hold the results of the query
		List<HarvestSchedule> harvestSchedules = new ArrayList<HarvestSchedule>();
		
		try
		{				
			// SQL to get the rows
			String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                                           COL_SCHEDULE_NAME + ", " +
                                           COL_RECURRENCE + ", " +
                                           COL_PROVIDER_ID + ", " +
                                           COL_START_DATE + ", " +
                                           COL_END_DATE + ", " +
                                           COL_MINUTE + ", " +
                                           COL_DAY_OF_WEEK + ", " +
                                           COL_HOUR + ", " +
                                           COL_NOTIFY_EMAIL + ", " +
                                           COL_STATUS + ", " +
                                           COL_REQUEST + " " +
                               "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " + 
                               "ORDER BY " + columnSorted + (asc ? " ASC" : " DESC");
		
			if(log.isDebugEnabled())
				log.debug("Creating the \"get all harvest schedules\" Statement from the SQL " + selectSql);
		
			// A prepared statement to run the select SQL
			// This should sanitize the SQL and prevent SQL injection
			getSorted = dbConnection.createStatement();
			
			// Get the results of the SELECT statement			
			
			// Execute the query
			results = getSorted.executeQuery(selectSql);
		
			// If any results were returned
			while(results.next())
			{
				// The Object which will contain data on the user
				HarvestSchedule harvestSchedule = new HarvestSchedule();
				
				// Set the fields on the user
				harvestSchedule.setId(results.getInt(1));
				harvestSchedule.setScheduleName(results.getString(2));
				harvestSchedule.setRecurrence(results.getString(3));
				harvestSchedule.setProvider(providerDao.loadBasicProvider(results.getInt(4)));
				harvestSchedule.setStartDate(results.getDate(5));
				harvestSchedule.setEndDate(results.getDate(6));
				harvestSchedule.setMinute(results.getInt(7));
				harvestSchedule.setDayOfWeek(results.getInt(8));
				harvestSchedule.setHour(results.getInt(9));
				harvestSchedule.setNotifyEmail(results.getString(10));
				harvestSchedule.setStatus(results.getString(11));
				harvestSchedule.setRequest(results.getString(12));
				
				// Return the harvest schedule
				harvestSchedules.add(harvestSchedule);
			} // end loop over results
			
			if(log.isDebugEnabled())
				log.debug("Found " + harvestSchedules.size() + " harvest schedules in the database.");
			
			return harvestSchedules;
		} // end try(get the harvest schedules)
		catch(SQLException e)
		{
			log.error("A SQLException occurred while getting the harvest schedules", e);
			
			return harvestSchedules;
		} // end catch(SQLException)
		finally
		{
			MySqlConnectionManager.closeResultSet(results);
			
			try
			{
				getSorted.close();
			} // end try(close the Statement)
			catch(SQLException e)
			{
				log.error("An error occurred while trying to close the \"get harvest schedules sorted ASC\" Statement");
			} // end catch(DataException)
		} // end finally(close ResultSet)
	} // end method getSortedByName(boolean)
	
	@Override
	public HarvestSchedule getById(int harvestScheduleId) 
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the harvest schedule with ID " + harvestScheduleId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a harvest schedule by ID was not defined, create it
				if(psGetById == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                                                   COL_SCHEDULE_NAME + ", " +
                                                   COL_RECURRENCE + ", " +
                                                   COL_PROVIDER_ID + ", " +
                                                   COL_START_DATE + ", " +
                                                   COL_END_DATE + ", " +
                                                   COL_MINUTE + ", " +
                                                   COL_DAY_OF_WEEK + ", " +
                                                   COL_HOUR + ", " +
                                                   COL_NOTIFY_EMAIL + ", " +
                                                   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " +
	                                   "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest schedule by ID\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement wasn't defined)

				// Set the parameters on the SELECT statement
				psGetById.setInt(1, harvestScheduleId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetById.executeQuery();

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the harvest schedule
					HarvestSchedule harvestSchedule = new HarvestSchedule();

					// Set the fields on the harvest schedule
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setProvider(providerDao.getById(results.getInt(4)));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));
					harvestSchedule.setSteps(harvestScheduleStepDao.getStepsForSchedule(harvestSchedule.getId()));

					// Get the sets and formats for the schedule based on its steps
					for(HarvestScheduleStep step : harvestSchedule.getSteps())
					{
						harvestSchedule.addSet(step.getSet());
						harvestSchedule.addFormat(step.getFormat());
					} // end loop over steps

					if(log.isDebugEnabled())
						log.debug("Found the harvest schedule with ID " + harvestScheduleId + " in the database.");

					// Return the harvest schedule
					return harvestSchedule;
				} // end if(we found a harvest schedule)
				else // There were no rows in the database, the harvest schedule could not be found
				{
					if(log.isDebugEnabled())
						log.debug("The harvest schedule with ID " + harvestScheduleId + " was not found in the database.");

					return null;
				} // end else
			} // end try(get the harvest schedule)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest schedule with ID " + harvestScheduleId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(closeResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public HarvestSchedule loadWithoutSteps(int harvestScheduleId)
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the harvest schedule with ID " + harvestScheduleId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a harvest schedule by ID was not defined, create it
				if(psGetById == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                                                   COL_SCHEDULE_NAME + ", " +
                                                   COL_RECURRENCE + ", " +
                                                   COL_PROVIDER_ID + ", " +
                                                   COL_START_DATE + ", " +
                                                   COL_END_DATE + ", " +
                                                   COL_MINUTE + ", " +
                                                   COL_DAY_OF_WEEK + ", " +
                                                   COL_HOUR + ", " +
                                                   COL_NOTIFY_EMAIL + ", " +
                                                   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " +
	                                   "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest schedule by ID\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement wasn't defined)

				// Set the parameters on the SELECT statement
				psGetById.setInt(1, harvestScheduleId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetById.executeQuery();

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the harvest schedule
					HarvestSchedule harvestSchedule = new HarvestSchedule();

					// Set the fields on the harvest schedule
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setProvider(providerDao.getById(results.getInt(4)));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));

					// Get the sets and formats for the schedule based on its steps
					for(HarvestScheduleStep step : harvestSchedule.getSteps())
					{
						harvestSchedule.addSet(step.getSet());
						harvestSchedule.addFormat(step.getFormat());
					} // end loop over steps

					if(log.isDebugEnabled())
						log.debug("Found the harvest schedule with ID " + harvestScheduleId + " in the database.");

					// Return the harvest schedule
					return harvestSchedule;
				} // end if(we found a harvest schedule)
				else // There were no rows in the database, the harvest schedule could not be found
				{
					if(log.isDebugEnabled())
						log.debug("The harvest schedule with ID " + harvestScheduleId + " was not found in the database.");

					return null;
				} // end else
			} // end try(get the harvest schedule)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest schedule with ID " + harvestScheduleId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(closeResultSet)
		} // end synchronized
	} // end method loadWithoutSteps(int)

	@Override
	public HarvestSchedule getByName(String name)
	{
		synchronized(psGetByNameLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the harvest schedule with name " + name);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a harvest schedule by ID was not defined, create it
				if(psGetByName == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                                                   COL_SCHEDULE_NAME + ", " +
                                                   COL_RECURRENCE + ", " +
                                                   COL_PROVIDER_ID + ", " +
                                                   COL_START_DATE + ", " +
                                                   COL_END_DATE + ", " +
                                                   COL_MINUTE + ", " +
                                                   COL_DAY_OF_WEEK + ", " +
                                                   COL_HOUR + ", " +
                                                   COL_NOTIFY_EMAIL + ", " +
                                                   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " +
	                                   "WHERE " + COL_SCHEDULE_NAME + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest schedule by name\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByName = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement wasn't defined)

				// Set the parameters on the SELECT statement
				psGetByName.setString(1, name);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetByName.executeQuery();

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the harvest schedule
					HarvestSchedule harvestSchedule = new HarvestSchedule();

					// Set the fields on the harvest schedule
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setProvider(providerDao.getById(results.getInt(4)));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));
					harvestSchedule.setSteps(harvestScheduleStepDao.getStepsForSchedule(harvestSchedule.getId()));

					// Get the sets and formats for the schedule based on its steps
					for(HarvestScheduleStep step : harvestSchedule.getSteps())
					{
						harvestSchedule.addSet(step.getSet());
						harvestSchedule.addFormat(step.getFormat());
					} // end loop over steps

					if(log.isDebugEnabled())
						log.debug("Found the harvest schedule with name " + name + " in the database.");

					// Return the harvest schedule
					return harvestSchedule;
				} // end if(we found a harvest schedule)
				else // There were no rows in the database, the harvest schedule could not be found
				{
					if(log.isDebugEnabled())
						log.debug("The harvest schedule with name " + name + " was not found in the database.");

					return null;
				} // end else
			} // end try(get the harvest schedule)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest schedule with name " + name, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(closeResultSet)
		} // end synchronized
	} // end method getByName(String)

	@Override
	public HarvestSchedule loadBasicHarvestSchedule(int harvestScheduleId)
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the basic harvest schedule with ID " + harvestScheduleId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a harvest schedule by ID was not defined, create it
				if(psGetById == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                                                   COL_SCHEDULE_NAME + ", " +
                                                   COL_RECURRENCE + ", " +
                                                   COL_PROVIDER_ID + ", " +
                                                   COL_START_DATE + ", " +
                                                   COL_END_DATE + ", " +
                                                   COL_MINUTE + ", " +
                                                   COL_DAY_OF_WEEK + ", " +
                                                   COL_HOUR + ", " +
                                                   COL_NOTIFY_EMAIL + ", " +
                                                   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " +
	                                   "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest schedule by ID\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement wasn't defined)

				// Set the parameters on the SELECT statement
				psGetById.setInt(1, harvestScheduleId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetById.executeQuery();

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the harvest schedule
					HarvestSchedule harvestSchedule = new HarvestSchedule();

					// Set the fields on the harvest schedule
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));

					if(log.isDebugEnabled())
						log.debug("Found the harvest schedule with ID " + harvestScheduleId + " in the database.");

					// Return the harvest schedule
					return harvestSchedule;
				} // end if(we found a harvest schedule)
				else // There were no rows in the database, the harvest schedule could not be found
				{
					if(log.isDebugEnabled())
						log.debug("The harvest schedule with ID " + harvestScheduleId + " was not found in the database.");

					return null;
				} // end else
			} // end try(get the harvest schedule)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the basic harvest schedule with ID " + harvestScheduleId, e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(closeResultSet)
		} // end synchronized
	} // end method loadBasicHarvestSchedule(int)

	@Override
	public HarvestSchedule getHarvestScheduleForProvider(int providerId)
	{
		synchronized(psGetByProviderIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the harvest schedules with provider ID " + providerId);

			HarvestSchedule harvestSchedule = null;

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a harvest schedule by ID was not defined, create it
				if(psGetByProviderId == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                    							   COL_SCHEDULE_NAME + ", " +
                    							   COL_RECURRENCE + ", " +
                    							   COL_PROVIDER_ID + ", " +
                    							   COL_START_DATE + ", " +
                    							   COL_END_DATE + ", " +
                    							   COL_MINUTE + ", " +
                    							   COL_DAY_OF_WEEK + ", " +
                    							   COL_HOUR + ", " +
                    							   COL_NOTIFY_EMAIL + ", " +
                    							   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " +
	                                   "WHERE " + COL_PROVIDER_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get harvest schedule by  provider ID\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByProviderId = dbConnection.prepareStatement(selectSql);
				} // end if(get by provider ID PreparedStatement not defined)

				// Set the parameters on the SELECT statement
				psGetByProviderId.setInt(1, providerId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetByProviderId.executeQuery();

				// If any results were returned
				if (results.next())
				{
					// The Object which will contain data on the harvest schedule
					harvestSchedule = new HarvestSchedule();

					// Set the fields on the harvest schedule
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));

					if(log.isDebugEnabled()) {
						log.debug("Found harvest schedule with provider ID " + providerId + ".");
					}

				} else {
					if(log.isDebugEnabled())
						log.debug("Harvest schedule with provider ID " + providerId + " was not found.");

				} // end loop over results}


				return harvestSchedule;
			} // end try(get the schedules)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest schedules with provider ID " + providerId, e);

				return harvestSchedule;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getHarvestSchedulesForProvider(int)

	@Override
	public List<HarvestSchedule> getSchedulesToRun(int hour, int dayOfWeek,	int minute)
	{
		synchronized(psGetByTimeLock)
		{
			// The list of results to return
			ArrayList<HarvestSchedule> harvestSchedules = new ArrayList<HarvestSchedule>();

			if(log.isDebugEnabled())
				log.debug("Getting all harvest schedules with hour " + hour + " or day of the week " + dayOfWeek + " or minute " + minute);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get harvest schedules by their time was not defined, create it
				if(psGetByTime == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_HARVEST_SCHEDULE_ID + ", " +
                    							   COL_SCHEDULE_NAME + ", " +
                    							   COL_RECURRENCE + ", " +
                    							   COL_PROVIDER_ID + ", " +
                    							   COL_START_DATE + ", " +
                    							   COL_END_DATE + ", " +
                    							   COL_MINUTE + ", " +
                    							   COL_DAY_OF_WEEK + ", " +
                    							   COL_HOUR + ", " +
                    							   COL_NOTIFY_EMAIL + ", " +
                    							   COL_STATUS + ", " +
                                                   COL_REQUEST + " " +
	                                   "FROM " + HARVEST_SCHEDULES_TABLE_NAME + " " +
	                                   "WHERE (" + COL_START_DATE + " IS NULL OR " + COL_START_DATE + "<=?) " +
	                                   "AND (" + COL_END_DATE + " IS NULL OR " + COL_END_DATE + ">=?) " +
	                                   "AND " + "((0=? AND " + COL_HOUR + "=? AND " + COL_DAY_OF_WEEK + "=?)" +
	                                             "OR (0=? AND " + COL_HOUR + "=? AND " + COL_DAY_OF_WEEK + "=0)" +
	                                             "OR " + COL_MINUTE + "=?)";

					if(log.isDebugEnabled())
						log.debug("Creating the PreparedStatement to get a harvest schedule by its time the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByTime = dbConnection.prepareStatement(selectSql);
				} // end if(get by time PreparedStatement wasn't defined)

				// Set the parameters on the update statement
				Date now = new Date((new java.util.Date()).getTime());
				psGetByTime.setDate(1, now);
				psGetByTime.setDate(2, now);
				psGetByTime.setInt(3, minute);
				psGetByTime.setInt(4, hour);
				psGetByTime.setInt(5, dayOfWeek);
				psGetByTime.setInt(6, minute);
				psGetByTime.setInt(7, hour);
				psGetByTime.setInt(8, minute);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetByTime.executeQuery();

				// Add each returned result to the list of harvest schedules which match the requested parameters
				while(results.next())
				{
					// The Object which will contain data on the harvest schedule
					HarvestSchedule harvestSchedule = new HarvestSchedule();

					// Set the fields on the harvest schedule
					harvestSchedule.setId(results.getInt(1));
					harvestSchedule.setScheduleName(results.getString(2));
					harvestSchedule.setRecurrence(results.getString(3));
					harvestSchedule.setProvider(providerDao.getById(results.getInt(4)));
					harvestSchedule.setStartDate(results.getDate(5));
					harvestSchedule.setEndDate(results.getDate(6));
					harvestSchedule.setMinute(results.getInt(7));
					harvestSchedule.setDayOfWeek(results.getInt(8));
					harvestSchedule.setHour(results.getInt(9));
					harvestSchedule.setNotifyEmail(results.getString(10));
					harvestSchedule.setStatus(results.getString(11));
					harvestSchedule.setRequest(results.getString(12));
					harvestSchedule.setSteps(harvestScheduleStepDao.getStepsForSchedule(harvestSchedule.getId()));

					// Get the sets and formats for the schedule based on its steps
					for(HarvestScheduleStep step : harvestSchedule.getSteps())
					{
						harvestSchedule.addSet(step.getSet());
						harvestSchedule.addFormat(step.getFormat());
					} // end loop over steps

					// Add the harvest schedule to the list
					harvestSchedules.add(harvestSchedule);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + harvestSchedules.size() + " harvest schedules with hour " + hour + " or day of the week " + dayOfWeek + " or minute " + minute);

				return harvestSchedules;
			} // end try(get the schedules)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the harvest schedules with hour " + hour + " or day of the week " + dayOfWeek + " or minute " + minute, e);

				return harvestSchedules;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally (close ResultSet)
		} // end synchronized
	} // end method getSchedulesToRun(int, int, int)

	@Override
	public boolean insert(HarvestSchedule harvestSchedule) throws DataException
	{
		// Check that the non-ID fields on the harvest schedule are valid
		validateFields(harvestSchedule, false, true);

		// Insert the provider if it hasn't already been inserted
		if(harvestSchedule.getProvider().getId() <= 0)
			if(!providerDao.insert(harvestSchedule.getProvider()))
				return false;

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new harvest schedule with name " + harvestSchedule.getScheduleName());

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a harvest schedule was not defined, create it
				if(psInsert == null)
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + HARVEST_SCHEDULES_TABLE_NAME + " (" + COL_SCHEDULE_NAME + ", " +
	                                                                        COL_RECURRENCE + ", " +
	                                                                        COL_PROVIDER_ID + ", " +
                                                                            COL_START_DATE + ", " +
				                                                            COL_END_DATE + ", " +
				                                                            COL_MINUTE + ", " +
				                                                            COL_DAY_OF_WEEK + ", " +
				                                                            COL_HOUR + ", " +
				                                                            COL_NOTIFY_EMAIL + ", " +
				                                                            COL_STATUS + ", " +
				                                                            COL_REQUEST + ") " +
	                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert harvest schedule\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnection.prepareStatement(insertSql);
				} // end if(insert PreparedStatement wasn't defined)

				// Set the parameters on the insert statement

				psInsert.setString(1, harvestSchedule.getScheduleName());
				psInsert.setString(2, harvestSchedule.getRecurrence());
				psInsert.setInt(3, harvestSchedule.getProvider().getId());
				psInsert.setDate(4, harvestSchedule.getStartDate());
				psInsert.setDate(5, harvestSchedule.getEndDate());
				psInsert.setInt(6, harvestSchedule.getMinute());
				psInsert.setInt(7, harvestSchedule.getDayOfWeek());
				psInsert.setInt(8, harvestSchedule.getHour());
				psInsert.setString(9, harvestSchedule.getNotifyEmail());
				psInsert.setString(10, Constants.STATUS_SERVICE_NOT_RUNNING);
				psInsert.setString(11, harvestSchedule.getRequest());

				// Execute the insert statement and return the result
				if(psInsert.executeUpdate() > 0)
				{
					// Get the auto-generated user ID and set it correctly on this Harvest Schedule Object
					rs = dbConnection.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				    	harvestSchedule.setId(rs.getInt(1));

				    boolean success = true;

				    // A step in the schedule we just inserted
				    HarvestScheduleStep step = new HarvestScheduleStep();

				    // If there are no sets, insert a step for each format to be harvested
				    if(harvestSchedule.getSets().size() <= 0)
				    {
				    	for(Format format : harvestSchedule.getFormats())
				    	{
				    		step.setFormat(format);
				    		success = harvestScheduleStepDao.insert(step, harvestSchedule.getId()) && success;
				    	} // end loop over formats
				    } // end if(the schedule had no sets)

				    // Otherwise insert a step for each format/set pair
				    else
				    {
				    	for(Format format : harvestSchedule.getFormats())
				    	{
				    		step.setFormat(format);

				    		for(Set set : harvestSchedule.getSets())
				    		{
				    			step.setSet(set);
				    			success = harvestScheduleStepDao.insert(step, harvestSchedule.getId()) && success;
				    		}
				    	} // end loop over formats
				    } // end else (the schedule had steps)

					return success;
				} // end if(insert succeeded)
				else
					return false;
			} // end try(insert the schedule)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new harvest schedule with the name " + harvestSchedule.getScheduleName(), e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(HarvestSchedule)

	@Override
	public boolean update(HarvestSchedule harvestSchedule) throws DataException
	{
		// Check that the fields on the harvest schedule are valid
		validateFields(harvestSchedule, true, true);

		// Insert the provider if it hasn't already been inserted
		if(harvestSchedule.getProvider().getId() <= 0)
			if(!providerDao.insert(harvestSchedule.getProvider()))
				return false;

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the harvest schedule with id " + harvestSchedule.getId());

			try
			{
				// If the PreparedStatement to update a harvest schedule was not defined, create it
				if(psUpdate == null)
				{
					// SQL to update new row
					String updateSql = "UPDATE " + HARVEST_SCHEDULES_TABLE_NAME + " SET " + COL_SCHEDULE_NAME + "=?, " +
	                                                                      COL_RECURRENCE + "=?, " +
	                                                                      COL_PROVIDER_ID + "=?, " +
                                                                          COL_START_DATE + "=?, " +
                                                                          COL_END_DATE + "=?, " +
                                                                          COL_MINUTE + "=?, " +
                                                                          COL_DAY_OF_WEEK + "=?, " +
                                                                          COL_HOUR + "=?, " +
				                                                          COL_NOTIFY_EMAIL + "=?, " +
				                                                          COL_STATUS + "=?, " +
				                                                          COL_REQUEST + "=? " +
	                                   "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update harvest schedule\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnection.prepareStatement(updateSql);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setString(1, harvestSchedule.getScheduleName());
				psUpdate.setString(2, harvestSchedule.getRecurrence());
				psUpdate.setInt(3, harvestSchedule.getProvider().getId());
				psUpdate.setDate(4, harvestSchedule.getStartDate());
				psUpdate.setDate(5, harvestSchedule.getEndDate());
				psUpdate.setInt(6, harvestSchedule.getMinute());
				psUpdate.setInt(7, harvestSchedule.getDayOfWeek());
				psUpdate.setInt(8, harvestSchedule.getHour());
				psUpdate.setString(9, harvestSchedule.getNotifyEmail());
				psUpdate.setString(10, harvestSchedule.getStatus());
				psUpdate.setString(11, harvestSchedule.getRequest());
				psUpdate.setInt(12, harvestSchedule.getId());

				// Execute the update statement and return the result
				boolean success = psUpdate.executeUpdate() > 0;

				// Delete all steps from before we updated the schedule
				harvestScheduleStepDao.deleteStepsForSchedule(harvestSchedule.getId());

				// A step in the schedule we just inserted
			    HarvestScheduleStep step = new HarvestScheduleStep();

			    // If there are no sets, insert a step for each format to be harvested
			    if(harvestSchedule.getSets().size() <= 0)
			    {
			    	for(Format format : harvestSchedule.getFormats())
			    	{
			    		step.setFormat(format);
			    		success = harvestScheduleStepDao.insert(step, harvestSchedule.getId()) && success;
			    	} // end loop over formats
			    } // end if(the schedule had no sets)

			    // Otherwise insert a step for each format/set pair
			    else
			    {
			    	for(Format format : harvestSchedule.getFormats())
			    	{
			    		step.setFormat(format);

			    		for(Set set : harvestSchedule.getSets())
			    		{
			    			step.setSet(set);
			    			success = harvestScheduleStepDao.insert(step, harvestSchedule.getId()) && success;
			    		}
			    	} // end loop over formats
			    } // end else (the schedule had steps)

				return success;
			} // end try(update schedule)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the harvest schedule with ID " + harvestSchedule.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method update(HarvestSchedule)

	@Override
	public boolean delete(HarvestSchedule harvestSchedule) throws DataException
	{
		// Check that the ID field on the harvest schedule are valid
		validateFields(harvestSchedule, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the harvest schedule with ID " + harvestSchedule.getId());

			try
			{
				// If the PreparedStatement to delete a harvest schedule was not defined, create it
				if(psDelete == null)
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ HARVEST_SCHEDULES_TABLE_NAME + " " +
									   "WHERE " + COL_HARVEST_SCHEDULE_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete harvest schedule\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnection.prepareStatement(deleteSql);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, harvestSchedule.getId());

				// Execute the delete statement and return the result
				return psDelete.execute();
			} // end try(delete the schedule
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the harvest schedule with ID " + harvestSchedule.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method delete(HarvestSchedule)
} // end class DefaultHarvestScheduleDAO
