/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Format;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.utils.LogWriter;

public class DefaultServiceDAO extends ServiceDAO
{

	/**
	 * The repository management log file name
	 */
	private static Log logObj = null;
	
	/**
	 * A PreparedStatement to get all services in the database
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * A PreparedStatement to get a service from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get a service from the database by its name
	 */
	private static PreparedStatement psGetByName = null;

	/**
	 * A PreparedStatement to insert a service into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a service in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a service from the database
	 */
	private static PreparedStatement psDelete = null;
	
	/**
	 * A PreparedStatement to get a basic service from the database
	 */
	private static PreparedStatement psLoadBasicService = null;
	
	
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
	 * Lock to synchronize access to the loadBasicService PreparedStatement
	 */
	private static Object psLoadBasicServiceLock = new Object();
	
	public DefaultServiceDAO()
	{
		try 
		{
			logObj = getLogDAO().getById(Constants.LOG_ID_SERVICE_MANAGEMENT);
		} catch (DatabaseConfigException e) 
		{
			log.error("Unable to connect to the database using the parameters from the configuration file.", e);
		}
	}
	
	@Override
	public ArrayList<Service> getAll() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all services");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all services
			ArrayList<Service> services = new ArrayList<Service>();

			try
			{
				// Create the PreparedStatment to get all services if it hasn't already been created
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SERVICE_ID + ", " +
												   COL_SERVICE_NAME + ", " +
												   COL_CLASS_NAME + ", " +
												   COL_WARNINGS + ", " +
												   COL_ERRORS + ", " +
												   COL_INPUT_RECORD_COUNT + ", " +
												   COL_OUTPUT_RECORD_COUNT + ", " +
												   COL_LAST_LOG_RESET + ", " +
												   COL_LOG_FILE_NAME + ", " +
												   COL_HARVEST_OUT_WARNINGS + ", " +
												   COL_HARVEST_OUT_ERRORS + ", " +
												   COL_HARVEST_OUT_RECORDS_AVAILABLE + ", " +
												   COL_HARVEST_OUT_RECORDS_HARVESTED + ", " +
												   COL_HARVEST_OUT_LAST_LOG_RESET + ", " +
												   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
												   COL_STATUS + " " +
												   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
												   COL_VERSION + ", " +
												   COL_DELETED + " " +
								       "FROM " + SERVICES_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all services\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a Service object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the service
					Service service = new Service();

					int i=1;
					// Set the fields on the service
					service.setId(results.getInt(i++));
					service.setName(results.getString(i++));
					service.setClassName(results.getString(i++));
					service.setServicesWarnings(results.getInt(i++));
					service.setServicesErrors(results.getInt(i++));
					service.setInputRecordCount(results.getInt(i++));
					service.setOutputRecordCount(results.getInt(i++));
					service.setServicesLastLogReset(results.getDate(i++));
					service.setServicesLogFileName(results.getString(i++));
					service.setHarvestOutWarnings(results.getInt(i++));
					service.setHarvestOutErrors(results.getInt(i++));
					service.setHarvestOutRecordsAvailable(results.getLong(i++));
					service.setNumberOfHarvests(results.getLong(i++));
					service.setHarvestOutLastLogReset(results.getDate(i++));
					service.setHarvestOutLogFileName(results.getString(i++));
					service.setStatus(results.getString(i++));
					service.setVersion(results.getString(i++));
					service.setDeleted(results.getBoolean(i++));

					for(Integer inputFormatId : getServiceInputFormatUtilDAO().getInputFormatsForService(service.getId()))
						service.addInputFormat(getFormatDAO().getById(inputFormatId));

					for(Integer inputSetId : getServiceOutputSetUtilDAO().getOutputSetsForService(service.getId()))
						service.addOutputSet(getSetDAO().getById(inputSetId));
					
					for(Integer outputFormatId : getServiceOutputFormatUtilDAO().getOutputFormatsForService(service.getId()))
						service.addOutputFormat(getFormatDAO().getById(outputFormatId));

					// Add the service to the list
					services.add(service);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + services.size() + " services in the database.");

				return services;
			} // end try(get services)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the services.", e);

				return services;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getAll();
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getAll()

    /**
     * returns a sorted list of services
     * @param asc determines whether the rows are sorted in ascending or descending order
     * @param columnSorted the coulmn on which rows are sorted
     * @return list of services
     */
	@Override
	public List<Service> getSorted(boolean asc,String columnSorted) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		if(log.isDebugEnabled())
			log.debug("Getting all services sorted in " + (asc ? "ascending" : "descending") + " order by " + columnSorted);

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
		
		// The list of all services
		ArrayList<Service> services = new ArrayList<Service>();

		try
		{			
			// SQL to get the rows
			String selectSql = "SELECT " + COL_SERVICE_ID + ", " +
										   COL_SERVICE_NAME + ", " +
										   COL_CLASS_NAME + ", " +
										   COL_WARNINGS + ", " +
										   COL_ERRORS + ", " +
										   COL_INPUT_RECORD_COUNT + ", " +
										   COL_OUTPUT_RECORD_COUNT + ", " +
										   COL_LAST_LOG_RESET + ", " +
										   COL_LOG_FILE_NAME + ", " +
										   COL_HARVEST_OUT_WARNINGS + ", " +
										   COL_HARVEST_OUT_ERRORS + ", " +
										   COL_HARVEST_OUT_RECORDS_AVAILABLE + ", " +
										   COL_HARVEST_OUT_RECORDS_HARVESTED + ", " +
										   COL_HARVEST_OUT_LAST_LOG_RESET + ", " +
										   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
										   COL_STATUS + " " +
										   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
										   COL_VERSION + ", " +
										   COL_DELETED + " " +
						       "FROM " + SERVICES_TABLE_NAME + " " +
						       "ORDER BY " + columnSorted + (asc ? " ASC" : " DESC");
	
			if(log.isDebugEnabled())
				log.debug("Creating the \"get all services\" PreparedStatement from the SQL " + selectSql);

			// A prepared statement to run the select SQL
			// This should sanitize the SQL and prevent SQL injection
			getSorted = dbConnectionManager.createStatement();
			
			// Get the results of the SELECT statement			
			
			// Execute the query
			results = getSorted.executeQuery(selectSql);

			// For each result returned, add a Service object to the list with the returned data
			while(results.next())
			{
				// The Object which will contain data on the service
				Service service = new Service();

				int i=1;
				// Set the fields on the service
				service.setId(results.getInt(i++));
				service.setName(results.getString(i++));
				service.setClassName(results.getString(i++));
				service.setServicesWarnings(results.getInt(i++));
				service.setServicesErrors(results.getInt(i++));
				service.setInputRecordCount(results.getInt(i++));
				service.setOutputRecordCount(results.getInt(i++));
				service.setServicesLastLogReset(results.getDate(i++));
				service.setServicesLogFileName(results.getString(i++));
				service.setHarvestOutWarnings(results.getInt(i++));
				service.setHarvestOutErrors(results.getInt(i++));
				service.setHarvestOutRecordsAvailable(results.getLong(i++));
				service.setNumberOfHarvests(results.getLong(i++));
				service.setHarvestOutLastLogReset(results.getDate(i++));
				service.setHarvestOutLogFileName(results.getString(i++));
				service.setStatus(results.getString(i++));
				service.setVersion(results.getString(i++));
				service.setDeleted(results.getBoolean(i++));

				for(Integer inputFormatId : getServiceInputFormatUtilDAO().getInputFormatsForService(service.getId()))
					service.addInputFormat(getFormatDAO().getById(inputFormatId));

				for(Integer inputSetId : getServiceOutputSetUtilDAO().getOutputSetsForService(service.getId()))
					service.addOutputSet(getSetDAO().getById(inputSetId));

				for(Integer outputFormatId : getServiceOutputFormatUtilDAO().getOutputFormatsForService(service.getId()))
					service.addOutputFormat(getFormatDAO().getById(outputFormatId));

				// Add the service to the list
				services.add(service);
			} // end loop over results

			if(log.isDebugEnabled())
				log.debug("Found " + services.size() + " services in the database.");

			return services;
		} // end try(get services)
		catch(SQLException e)
		{
			log.error("A SQLException occurred while getting the services.", e);

			return services;
		} // end catch(SQLException)
		finally
		{
			dbConnectionManager.closeResultSet(results);
			
			try
			{
				if(getSorted != null)
					getSorted.close();
			} // end try(close the Statement)
			catch(SQLException e)
			{
				log.error("An error occurred while trying to close the \"get processing directives sorted\" Statement");
			} // end catch(DataException)
		} // end finally(close ResultSet)
	} // end method getSortedByUserName(boolean)
	
	@Override
	public Service getById(int serviceId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the service with ID " + serviceId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a service by ID if it hasn't already been created
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_SERVICE_ID + ", " +
				                                   COL_SERVICE_NAME + ", " +
				                                   COL_CLASS_NAME + ", " +
												   COL_WARNINGS + ", " +
												   COL_ERRORS+ ", " +
												   COL_INPUT_RECORD_COUNT + ", " +
												   COL_OUTPUT_RECORD_COUNT + ", " +
												   COL_LAST_LOG_RESET + ", " +
												   COL_LOG_FILE_NAME + ", " +
												   COL_HARVEST_OUT_WARNINGS + ", " +
												   COL_HARVEST_OUT_ERRORS + ", " +
												   COL_HARVEST_OUT_RECORDS_AVAILABLE + ", " +
												   COL_HARVEST_OUT_RECORDS_HARVESTED + ", " +
												   COL_HARVEST_OUT_LAST_LOG_RESET + ", " +
												   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
												   COL_STATUS + ", " +
												   COL_VERSION + ", " +
												   COL_DELETED + " " +
	                                   "FROM " + SERVICES_TABLE_NAME + " " +
	                                   "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get service by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the select statement
				psGetById.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the service
					Service service = new Service();

					int i=1;
					// Set the fields on the service
					service.setId(results.getInt(i++));
					service.setName(results.getString(i++));
					service.setClassName(results.getString(i++));
					service.setServicesWarnings(results.getInt(i++));
					service.setServicesErrors(results.getInt(i++));
					service.setInputRecordCount(results.getInt(i++));
					service.setOutputRecordCount(results.getInt(i++));
					service.setServicesLastLogReset(results.getDate(i++));
					service.setServicesLogFileName(results.getString(i++));
					service.setHarvestOutWarnings(results.getInt(i++));
					service.setHarvestOutErrors(results.getInt(i++));
					service.setHarvestOutRecordsAvailable(results.getLong(i++));
					service.setNumberOfHarvests(results.getLong(i++));
					service.setHarvestOutLastLogReset(results.getDate(i++));
					service.setHarvestOutLogFileName(results.getString(i++));
					service.setStatus(results.getString(i++));
					service.setVersion(results.getString(i++));
					service.setDeleted(results.getBoolean(i++));

					for(Integer inputFormatId : getServiceInputFormatUtilDAO().getInputFormatsForService(service.getId()))
						service.addInputFormat(getFormatDAO().getById(inputFormatId));

					for(Integer inputSetId : getServiceOutputSetUtilDAO().getOutputSetsForService(service.getId()))
						service.addOutputSet(getSetDAO().getById(inputSetId));

					for(Integer outputFormatId : getServiceOutputFormatUtilDAO().getOutputFormatsForService(service.getId()))
						service.addOutputFormat(getFormatDAO().getById(outputFormatId));

					if(log.isDebugEnabled())
						log.debug("Found the service with ID " + serviceId + " in the database.");

					// Return the service
					return service;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The service with ID " + serviceId + " was not found in the database.");

				return null;
			} // end try(get the service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the service with ID " + serviceId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getById(serviceId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public Service loadBasicService(int serviceId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psLoadBasicServiceLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the service with ID " + serviceId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a service by ID if it hasn't already been created
				if(psLoadBasicService == null || dbConnectionManager.isClosed(psLoadBasicService))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_SERVICE_ID + ", " +
				                                   COL_SERVICE_NAME + ", " +
				                                   COL_CLASS_NAME + ", " +
												   COL_WARNINGS + ", " +
												   COL_ERRORS+ ", " +
												   COL_INPUT_RECORD_COUNT + ", " +
												   COL_OUTPUT_RECORD_COUNT + ", " +
												   COL_LAST_LOG_RESET + ", " +
												   COL_LOG_FILE_NAME + ", " +
												   COL_HARVEST_OUT_WARNINGS + ", " +
												   COL_HARVEST_OUT_ERRORS + ", " +
												   COL_HARVEST_OUT_RECORDS_AVAILABLE + ", " +
												   COL_HARVEST_OUT_RECORDS_HARVESTED + ", " +
												   COL_HARVEST_OUT_LAST_LOG_RESET + ", " +
												   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
												   COL_STATUS + ", " +
												   COL_VERSION + ", " +
												   COL_DELETED + " " +
	                                   "FROM " + SERVICES_TABLE_NAME + " " +
	                                   "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get service by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psLoadBasicService = dbConnectionManager.prepareStatement(selectSql, psLoadBasicService);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psLoadBasicService.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psLoadBasicService);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the service
					Service service = new Service();

					int i=1;
					// Set the fields on the service
					service.setId(results.getInt(i++));
					service.setName(results.getString(i++));
					service.setClassName(results.getString(i++));
					service.setServicesWarnings(results.getInt(i++));
					service.setServicesErrors(results.getInt(i++));
					service.setInputRecordCount(results.getInt(i++));
					service.setOutputRecordCount(results.getInt(i++));
					service.setServicesLastLogReset(results.getDate(i++));
					service.setServicesLogFileName(results.getString(i++));
					service.setHarvestOutWarnings(results.getInt(i++));
					service.setHarvestOutErrors(results.getInt(i++));
					service.setHarvestOutRecordsAvailable(results.getLong(i++));
					service.setNumberOfHarvests(results.getLong(i++));
					service.setHarvestOutLastLogReset(results.getDate(i++));
					service.setHarvestOutLogFileName(results.getString(i++));
					service.setStatus(results.getString(i++));
					service.setVersion(results.getString(i++));
					service.setDeleted(results.getBoolean(i++));

					if(log.isDebugEnabled())
						log.debug("Found the service with ID " + serviceId + " in the database.");

					// Return the service
					return service;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The service with ID " + serviceId + " was not found in the database.");

				return null;
			} // end try(get the service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the service with ID " + serviceId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return loadBasicService(serviceId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method loadBasicService(int)

	@Override
	public Service getByServiceName(String name) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByNameLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the service with the name " + name);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// Create the PreparedStatment to get a service by name if it hasn't already been created
				if(psGetByName == null || dbConnectionManager.isClosed(psGetByName))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_SERVICE_ID + ", " +
				                                   COL_SERVICE_NAME + ", " +
				                                   COL_CLASS_NAME + ", " +
												   COL_WARNINGS + ", " +
												   COL_ERRORS+ ", " +
												   COL_INPUT_RECORD_COUNT + ", " +
												   COL_OUTPUT_RECORD_COUNT + ", " +
												   COL_LAST_LOG_RESET + ", " +
												   COL_LOG_FILE_NAME + ", " +
												   COL_HARVEST_OUT_WARNINGS + ", " +
												   COL_HARVEST_OUT_ERRORS + ", " +
												   COL_HARVEST_OUT_RECORDS_AVAILABLE + ", " +
												   COL_HARVEST_OUT_RECORDS_HARVESTED + ", " +
												   COL_HARVEST_OUT_LAST_LOG_RESET + ", " +
												   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
												   COL_STATUS + ", " +
												   COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
												   COL_VERSION + ", " +
												   COL_DELETED + " " +
	                                   "FROM " + SERVICES_TABLE_NAME + " " +
	                                   "WHERE " + COL_SERVICE_NAME + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get service by name\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByName = dbConnectionManager.prepareStatement(selectSql, psGetByName);
				} // end if(get by name PreparedStatement not defined)

				// Set the parameters on the select statement
				psGetByName.setString(1, name);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByName);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the service
					Service service = new Service();

					int i=0;
					// Set the fields on the service
					service.setId(results.getInt(i++));
					service.setName(results.getString(i++));
					service.setClassName(results.getString(i++));
					service.setServicesWarnings(results.getInt(i++));
					service.setServicesErrors(results.getInt(i++));
					service.setInputRecordCount(results.getInt(i++));
					service.setOutputRecordCount(results.getInt(i++));
					service.setServicesLastLogReset(results.getDate(i++));
					service.setServicesLogFileName(results.getString(i++));
					service.setHarvestOutWarnings(results.getInt(i++));
					service.setHarvestOutErrors(results.getInt(i++));
					service.setHarvestOutRecordsAvailable(results.getLong(i++));
					service.setNumberOfHarvests(results.getLong(i++));
					service.setHarvestOutLastLogReset(results.getDate(i++));
					service.setHarvestOutLogFileName(results.getString(i++));
					service.setStatus(results.getString(i++));
					service.setVersion(results.getString(i++));
					service.setDeleted(results.getBoolean(i++));

					for(Integer inputFormatId : getServiceInputFormatUtilDAO().getInputFormatsForService(service.getId()))
						service.addInputFormat(getFormatDAO().getById(inputFormatId));

					for(Integer inputSetId : getServiceOutputSetUtilDAO().getOutputSetsForService(service.getId()))
						service.addOutputSet(getSetDAO().getById(inputSetId));

					for(Integer outputFormatId : getServiceOutputFormatUtilDAO().getOutputFormatsForService(service.getId()))
						service.addOutputFormat(getFormatDAO().getById(outputFormatId));

					if(log.isDebugEnabled())
						log.debug("Found the service with the name " + name + " in the database.");

					// Return the service
					return service;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The service with the name " + name + " was not found in the database.");

				return null;
			} // end try(get the service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the service with the name " + name, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByServiceName(name);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByServiceName(String)

	@Override
	public boolean insert(Service service) throws DataException
	{
		// Check that the non-ID fields on the service are valid
		validateFields(service, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new service with the name " + service.getName());

			// The result set returned by the query
			ResultSet rs = null;

			try
			{
				// Build the PreparedStatement to insert a service if it wasn't already created
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + SERVICES_TABLE_NAME + " (" + COL_SERVICE_NAME + ", " +
	            	      													         COL_CLASS_NAME + ", " +
	            	      													         COL_WARNINGS + ", " +
	            	      													         COL_ERRORS + ", " +
	            	      													         COL_INPUT_RECORD_COUNT + ", " +
	            	      													         COL_OUTPUT_RECORD_COUNT + ", " +
	            	      													         COL_LAST_LOG_RESET + ", " +
	            	      													         COL_LOG_FILE_NAME + ", " +
	            	      													         COL_HARVEST_OUT_WARNINGS + ", " +
	            	      													         COL_HARVEST_OUT_ERRORS + ", " +
	            	      													         COL_HARVEST_OUT_RECORDS_AVAILABLE + ", " +
	            	      													         COL_HARVEST_OUT_RECORDS_HARVESTED + ", " +
	            	      													         COL_HARVEST_OUT_LAST_LOG_RESET + ", " +
	            	      													         COL_HARVEST_OUT_LOG_FILE_NAME + ", " +
	            	      													         COL_STATUS + ", " +
	            	      													         COL_VERSION + ", " +
	            	      													         COL_DELETED + ") " +
	            				       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert service\" PreparedStatemnt from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)
				
				int i=1;

				// Set the parameters on the insert statement
				psInsert.setString(i++, service.getName());
				psInsert.setString(i++, service.getClassName());
				psInsert.setInt(i++, service.getServicesWarnings());
				psInsert.setInt(i++, service.getServicesErrors());
				psInsert.setInt(i++, service.getInputRecordCount());
				psInsert.setInt(i++, service.getOutputRecordCount());
				psInsert.setDate(i++, service.getServicesLastLogReset());
				psInsert.setString(i++, service.getServicesLogFileName());
				psInsert.setInt(i++, service.getHarvestOutWarnings());
				psInsert.setInt(i++, service.getHarvestOutErrors());
				psInsert.setLong(i++, service.getHarvestOutRecordsAvailable());
				psInsert.setLong(i++, service.getNumberOfHarvests());
				psInsert.setDate(i++, service.getHarvestOutLastLogReset());
				psInsert.setString(i++, service.getHarvestOutLogFileName());
				psInsert.setString(i++, service.getStatus());
				psInsert.setString(i++, service.getVersion());
				psInsert.setBoolean(i++, service.isDeleted());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated resource identifier ID and set it correctly on this Service Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        service.setId(rs.getInt(1));

				    boolean success = true;

				    // Insert the input format assignments
				    for(Format inputFormat : service.getInputFormats())
				    	success = getServiceInputFormatUtilDAO().insert(service.getId(), inputFormat.getId()) && success;

				    // Insert the input set assignments
				    for(xc.mst.bo.provider.Set inputSet : service.getOutputSets())
				    	success = getServiceOutputSetUtilDAO().insert(service.getId(), inputSet.getId()) && success;
				    
				    // Insert the output format assignments
				    for(Format outputFormat : service.getOutputFormats())
				    	success = getServiceOutputFormatUtilDAO().insert(service.getId(), outputFormat.getId()) && success;

				    if(success)
				    {
				    	LogWriter.addInfo(logObj.getLogFileLocation(), "Added a new service with the name " + service.getName());
				    	
				    	LogWriter.addInfo(service.getServicesLogFileName(), "Beginning logging for " + service.getName());
				    	LogWriter.addInfo(service.getHarvestOutLogFileName(), "Beginning logging for " + service.getName() + "'s OAI repository");
				    }
				    else
				    {
				    	LogWriter.addWarning(logObj.getLogFileLocation(), "Added a new service with the name " + service.getName() + ", but failed to mark which formats it inputs and outputs");
				    	
				    	logObj.setWarnings(logObj.getWarnings() + 1);
				    	getLogDAO().update(logObj);
				    }
				    
					return success;
				} // end if(insert succeeded)
				else
				{
					LogWriter.addError(logObj.getLogFileLocation(), "Failed to add a new service with the name " + service.getName());
					
					logObj.setErrors(logObj.getErrors() + 1);
					getLogDAO().update(logObj);
			    	
					return false;
				}
			}
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new service with the name " + service.getName(), e);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to add a new service with the name " + service.getName());
				
				logObj.setErrors(logObj.getErrors() + 1);
				getLogDAO().update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ", e);
				//return insert(service);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
				return true;
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(Service)

	@Override
	public boolean update(Service service) throws DataException
	{
		// Check that the fields on the service are valid
		validateFields(service, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the service with ID " + service.getId());

			try
			{
				// Create a PreparedStatement to update a service if it wasn't already created
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + SERVICES_TABLE_NAME + " SET " + COL_SERVICE_NAME + "=?, " +
				                                                          COL_CLASS_NAME + "=?, " +
				                                                          COL_WARNINGS + "=?, " +
				                                                          COL_ERRORS + "=?, " +
				                                                          COL_INPUT_RECORD_COUNT + "=?, " +
				                                                          COL_OUTPUT_RECORD_COUNT + "=?, " +
				                                                          COL_LAST_LOG_RESET + "=?, " +
				                                                          COL_LOG_FILE_NAME + "=?, " +
				                                                          COL_HARVEST_OUT_WARNINGS + "=?, " +
				                                                          COL_HARVEST_OUT_ERRORS + "=?, " +
				                                                          COL_HARVEST_OUT_RECORDS_AVAILABLE + "=?, " +
				                                                          COL_HARVEST_OUT_RECORDS_HARVESTED + "=?, " +
				                                                          COL_HARVEST_OUT_LAST_LOG_RESET + "=?, " +
				                                                          COL_HARVEST_OUT_LOG_FILE_NAME + "=?, " +
				                                                          COL_STATUS + "=?, " +
				                                                          COL_VERSION + "=?, " +
				                                                          COL_DELETED + "=? " +
	                                   "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update service\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				int i=1;
				// Set the parameters on the update statement
				psUpdate.setString(i++, service.getName());
				psUpdate.setString(i++, service.getClassName());
				psUpdate.setInt(i++, service.getServicesWarnings());
				psUpdate.setInt(i++, service.getServicesErrors());
				psUpdate.setInt(i++, service.getInputRecordCount());
				psUpdate.setInt(i++, service.getOutputRecordCount());
				psUpdate.setDate(i++, service.getServicesLastLogReset());
				psUpdate.setString(i++, service.getServicesLogFileName());
				psUpdate.setInt(i++, service.getHarvestOutWarnings());
				psUpdate.setInt(i++, service.getHarvestOutErrors());
				psUpdate.setLong(i++, service.getHarvestOutRecordsAvailable());
				psUpdate.setLong(i++, service.getNumberOfHarvests());
				psUpdate.setDate(i++, service.getHarvestOutLastLogReset());
				psUpdate.setString(i++, service.getHarvestOutLogFileName());
				psUpdate.setString(i++, service.getStatus());
				psUpdate.setString(i++, service.getVersion());
				psUpdate.setBoolean(i++, service.isDeleted());
				psUpdate.setInt(i++, service.getId());
				
				// Execute the update statement and return the result
				if(dbConnectionManager.executeUpdate(psUpdate) > 0)
				{
					boolean success = true;

					// Delete the old input and output format assignments for the service
					getServiceInputFormatUtilDAO().deleteInputFormatsForService(service.getId());
					getServiceOutputSetUtilDAO().deleteOutputSetsForService(service.getId());
					getServiceOutputFormatUtilDAO().deleteOutputFormatsForService(service.getId());

					// Insert the input format assignments
				    for(Format inputFormat : service.getInputFormats())
				    	success = getServiceInputFormatUtilDAO().insert(service.getId(), inputFormat.getId()) && success;

				    // Insert the output format assignments
				    for(Format outputFormat : service.getOutputFormats())
				    	success = getServiceOutputFormatUtilDAO().insert(service.getId(), outputFormat.getId()) && success;

				    // Insert the input set assignments
				    for(xc.mst.bo.provider.Set inputSet : service.getOutputSets())
				    	if(inputSet != null)
				    		success = getServiceOutputSetUtilDAO().insert(service.getId(), inputSet.getId()) && success;
				    
				    if(success)
				    	LogWriter.addInfo(logObj.getLogFileLocation(), "Updated the service with the name " + service.getName());
				    else
				    {
				    	LogWriter.addWarning(logObj.getLogFileLocation(), "Updated the service with the name " + service.getName() + ", but failed to update the formats it inputs and outputs");
				    
				    	logObj.setWarnings(logObj.getWarnings() + 1);
				    	getLogDAO().update(logObj);
				    }
				    
					return success;
				} // end if(the update succeeded)

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to update the service with the name " + service.getName());
				
				logObj.setErrors(logObj.getErrors() + 1);
				getLogDAO().update(logObj);
		    	
				// If we got here, the update failed
				return false;
			} // end try(update service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the service with ID " + service.getId(), e);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to update the service with the name " + service.getName());
				
				logObj.setErrors(logObj.getErrors() + 1);
				getLogDAO().update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(service);
			}
		} // end synchronized
	} // end method update(Service)

	@Override
	public boolean delete(Service service) throws DataException
	{
		// Check that the ID field on the service are valid
		validateFields(service, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the service with ID " + service.getId());

			try
			{
				// Create the PreparedStatement to delete a service if it wasn't already defined
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM " + SERVICES_TABLE_NAME + " " +
		                               "WHERE " + COL_SERVICE_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete service\" PreparedStatement the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, service.getId());

				// Execute the delete statement and return the result
				boolean success = dbConnectionManager.execute(psDelete);
				
				return success;
			} // end try(delete the service)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the service with ID " + service.getId(), e);

				LogWriter.addError(logObj.getLogFileLocation(), "Failed to delete the service with the name " + service.getName());
				
				logObj.setErrors(logObj.getErrors() + 1);
				getLogDAO().update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(service);
			}
		} // end synchronized
	} // end method delete(Service)
} // end class DefaultServiceDAO