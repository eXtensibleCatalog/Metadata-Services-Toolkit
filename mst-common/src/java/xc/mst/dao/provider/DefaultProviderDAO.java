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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.utils.LogWriter;
import xc.mst.utils.index.SolrIndexManager;

/**
 * MySQL implementation of the Data Access Object for the providers table
 *
 * @author Eric Osisek
 */
public class DefaultProviderDAO extends ProviderDAO
{

	/**
	 * The repository management log file name
	 */
	private static Log logObj = null;
	
	/**
	 * A PreparedStatement to get all providers in the database
	 */
	private static PreparedStatement psGetAll = null;

	/**
	 * A PreparedStatement to get a provider from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get a provider from the database by its URL
	 */
	private static PreparedStatement psGetByUrl = null;

	/**
	 * A PreparedStatement to get a provider from the database by its name
	 */
	private static PreparedStatement psGetByName = null;

	/**
	 * A PreparedStatement to insert a provider into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a provider in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a provider from the database
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
	 * Lock to synchronize access to the get by URL PreparedStatement
	 */
	private static Object psGetByUrlLock = new Object();

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

	public void init()
	{
		super.init();
		
		try
		{
			logObj = getLogDAO().getById(Constants.LOG_ID_REPOSITORY_MANAGEMENT);
		}
		catch(DatabaseConfigException e)
		{
			log.error("Could not get the log file location for the repository management log.", e);
		}
	}
	
	@Override
	public List<Provider> getAll() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all providers");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all providers
			List<Provider> providers = new ArrayList<Provider>();

			try
			{
				// If the PreparedStatement to get all providers was not defined, create it
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_PROVIDER_ID + ", " +
				                                   COL_CREATED_AT + ", " +
				                                   COL_UPDATED_AT + ", " +
				                                   COL_NAME + ", " +
				                                   COL_OAI_PROVIDER_URL + ", " +
				                                   COL_TITLE + ", " +
				                                   COL_CREATOR + ", " +
				                                   COL_SUBJECT + ", " +
				                                   COL_DESCRIPTION + ", " +
				                                   COL_PUBLISHER + ", " +
				                                   COL_CONTRIBUTORS + ", " +
				                                   COL_DATE + ", " +
				                                   COL_TYPE + ", " +
				                                   COL_FORMAT + ", " +
				                                   COL_IDENTIFIER + ", " +
				                                   COL_LANGUAGE + ", " +
				                                   COL_RELATION + ", " +
				                                   COL_COVERAGE + ", " +
				                                   COL_RIGHTS + ", " +
				                                   COL_SERVICE + ", " +
				                                   COL_NEXT_LIST_SETS_LIST_FORMATS + ", " +
				                                   COL_PROTOCOL_VERSION + ", " +
				                                   COL_LAST_VALIDATION_DATE + ", " +
				                                   COL_IDENTIFY + ", " +
				                                   COL_LISTFORMATS + ", " +
				                                   COL_LISTSETS + ", " +
				                                   COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_RECORDS_ADDED + ", " +
				                                   COL_RECORDS_REPLACED + ", " +
				                                   COL_LAST_OAI_REQUEST + ", " +
				                                   COL_LAST_HARVEST_END_TIME + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + ", " +
				                                   COL_RECORDS_TO_HARVEST + " " +
	                                   "FROM " + PROVIDERS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all providers\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a Provider object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the provider
					Provider provider = new Provider();

					// Set the fields on the provider
					provider.setId(results.getInt(1));
					provider.setCreatedAt(results.getDate(2));
					provider.setUpdatedAt(results.getTimestamp(3));
					provider.setName(results.getString(4));
					provider.setOaiProviderUrl(results.getString(5));
					provider.setTitle(results.getString(6));
					provider.setCreator(results.getString(7));
					provider.setSubject(results.getString(8));
					provider.setDescription(results.getString(9));
					provider.setPublisher(results.getString(10));
					provider.setContributors(results.getString(11));
					provider.setDate(results.getDate(12));
					provider.setType(results.getString(13));
					provider.setFormat(results.getString(14));
					provider.setIdentifier(results.getInt(15));
					provider.setLanguage(results.getString(16));
					provider.setRelation(results.getString(17));
					provider.setCoverage(results.getString(18));
					provider.setRights(results.getString(19));
					provider.setService(results.getBoolean(20));
					provider.setNextListSetsListFormats(results.getDate(21));
					provider.setProtocolVersion(results.getString(22));
					provider.setLastValidationDate(results.getDate(23));
					provider.setIdentify(results.getBoolean(24));
					provider.setListFormats(results.getBoolean(25));
					provider.setListSets(results.getBoolean(26));
					provider.setWarnings(results.getInt(27));
					provider.setErrors(results.getInt(28));
					provider.setRecordsAdded(results.getInt(29));
					provider.setRecordsReplaced(results.getInt(30));
					provider.setLastOaiRequest(results.getString(31));
					provider.setLastHarvestEndTime(results.getDate(32));
					provider.setLastLogReset(results.getDate(33));
					provider.setLogFileName(results.getString(34));
					provider.setNumberOfRecordsToHarvest(results.getLong(35));

					provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));

					provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
					provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));

					// Add the provider to the list
					providers.add(provider);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + providers.size() + " providers in the database.");

				return providers;
			} // end try(get the providers)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the providers.", e);

				return providers;
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

	@Override
	public List<Provider> getSorted(boolean asc, String columnName) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		if(log.isDebugEnabled())
			log.debug("Getting all providers sorted in " + (asc ? "ascending" : "descending") + " order on the column " + columnName);

		// Validate the column we're trying to sort on
		if(!sortableColumns.contains(columnName))
		{
			log.error("An attempt was made to sort on the invalid column " + columnName);
			return getAll();
		} // end if(sort column invalid)
		
		// The ResultSet from the SQL query
		ResultSet results = null;

		// The Statement for getting the rows
		Statement getSorted = null;
		
		// The list of all providers
		List<Provider> providers = new ArrayList<Provider>();

		try
		{
			// If the PreparedStatement to get all providers was not defined, create it
			
			// SQL to get the rows
			String selectSql = "SELECT " + COL_PROVIDER_ID + ", " +
		                                   COL_CREATED_AT + ", " +
		                                   COL_UPDATED_AT + ", " +
		                                   COL_NAME + ", " +
		                                   COL_OAI_PROVIDER_URL + ", " +
		                                   COL_TITLE + ", " +
		                                   COL_CREATOR + ", " +
		                                   COL_SUBJECT + ", " +
		                                   COL_DESCRIPTION + ", " +
		                                   COL_PUBLISHER + ", " +
		                                   COL_CONTRIBUTORS + ", " +
		                                   COL_DATE + ", " +
		                                   COL_TYPE + ", " +
		                                   COL_FORMAT + ", " +
		                                   COL_IDENTIFIER + ", " +
		                                   COL_LANGUAGE + ", " +
		                                   COL_RELATION + ", " +
		                                   COL_COVERAGE + ", " +
		                                   COL_RIGHTS + ", " +
		                                   COL_SERVICE + ", " +
		                                   COL_NEXT_LIST_SETS_LIST_FORMATS + ", " +
		                                   COL_PROTOCOL_VERSION + ", " +
		                                   COL_LAST_VALIDATION_DATE + ", " +
		                                   COL_IDENTIFY + ", " +
		                                   COL_LISTFORMATS + ", " +
		                                   COL_LISTSETS + ", " +
		                                   COL_WARNINGS + ", " +
		                                   COL_ERRORS + ", " +
		                                   COL_RECORDS_ADDED + ", " +
		                                   COL_RECORDS_REPLACED + ", " +
		                                   COL_LAST_OAI_REQUEST + ", " +
		                                   COL_LAST_HARVEST_END_TIME + ", " +
		                                   COL_LAST_LOG_RESET + ", " +
		                                   COL_LOG_FILE_NAME + ", " +
		                                   COL_RECORDS_TO_HARVEST + " " +
                               "FROM " + PROVIDERS_TABLE_NAME + " " +
                               "ORDER BY " + columnName + (asc ? " ASC" : " DESC");

			if(log.isDebugEnabled())
				log.debug("Creating the \"get all providers sorted\" PreparedStatement from the SQL " + selectSql);

			// A statement to run the select SQL
			getSorted = dbConnectionManager.createStatement();
			
			// Get the results of the SELECT statement			
			
			// Execute the query
			results = getSorted.executeQuery(selectSql);

			// For each result returned, add a Provider object to the list with the returned data
			while(results.next())
			{
				// The Object which will contain data on the provider
				Provider provider = new Provider();

				// Set the fields on the provider
				provider.setId(results.getInt(1));
				provider.setCreatedAt(results.getDate(2));
				provider.setUpdatedAt(results.getTimestamp(3));
				provider.setName(results.getString(4));
				provider.setOaiProviderUrl(results.getString(5));
				provider.setTitle(results.getString(6));
				provider.setCreator(results.getString(7));
				provider.setSubject(results.getString(8));
				provider.setDescription(results.getString(9));
				provider.setPublisher(results.getString(10));
				provider.setContributors(results.getString(11));
				provider.setDate(results.getDate(12));
				provider.setType(results.getString(13));
				provider.setFormat(results.getString(14));
				provider.setIdentifier(results.getInt(15));
				provider.setLanguage(results.getString(16));
				provider.setRelation(results.getString(17));
				provider.setCoverage(results.getString(18));
				provider.setRights(results.getString(19));
				provider.setService(results.getBoolean(20));
				provider.setNextListSetsListFormats(results.getDate(21));
				provider.setProtocolVersion(results.getString(22));
				provider.setLastValidationDate(results.getDate(23));
				provider.setIdentify(results.getBoolean(24));
				provider.setListFormats(results.getBoolean(25));
				provider.setListSets(results.getBoolean(26));
				provider.setWarnings(results.getInt(27));
				provider.setErrors(results.getInt(28));
				provider.setRecordsAdded(results.getInt(29));
				provider.setRecordsReplaced(results.getInt(30));
				provider.setLastOaiRequest(results.getString(31));
				provider.setLastHarvestEndTime(results.getDate(32));
				provider.setLastLogReset(results.getDate(33));
				provider.setLogFileName(results.getString(34));
				provider.setNumberOfRecordsToHarvest(results.getLong(35));

				provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));

				provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
				provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));

				// Add the provider to the list
				providers.add(provider);
			} // end loop over results

			if(log.isDebugEnabled())
				log.debug("Found " + providers.size() + " providers in the database.");

			return providers;
		} // end try(get the providers)
		catch(SQLException e)
		{
			log.error("A SQLException occurred while getting the providers sorted by their name in ascending order.", e);
          
			return providers;
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
			} // end catch(SQLException)
		} // end finally(close ResultSet)
	} // end method getSorted(boolean, String)
	
	@Override
	public Provider getById(int providerId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		Provider provider = loadBasicProvider(providerId);

		// If we found the provider, set up its sets and formats
		if(provider != null)
		{
			provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
			provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));
			provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));
		} // end if(provider found)

		return provider;
	} // end method getById(int)

    @Override
    public Provider getByURL(String providerURL) throws DatabaseConfigException
    {
    	// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
    	synchronized(psGetByUrlLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the provider with URL " + providerURL);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a provider by URL was not defined, create it
				if(psGetByUrl == null || dbConnectionManager.isClosed(psGetByUrl))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_PROVIDER_ID + ", " +
					    				           COL_CREATED_AT + ", " +
					    				           COL_UPDATED_AT + ", " +
					    				           COL_NAME + ", " +
					    				           COL_OAI_PROVIDER_URL + ", " +
					    				           COL_TITLE + ", " +
					    				           COL_CREATOR + ", " +
					    				           COL_SUBJECT + ", " +
					    				           COL_DESCRIPTION + ", " +
					    				           COL_PUBLISHER + ", " +
					    				           COL_CONTRIBUTORS + ", " +
					    				           COL_DATE + ", " +
					    				           COL_TYPE + ", " +
					    				           COL_FORMAT + ", " +
					    				           COL_IDENTIFIER + ", " +
					    				           COL_LANGUAGE + ", " +
					    				           COL_RELATION + ", " +
					    				           COL_COVERAGE + ", " +
					    				           COL_RIGHTS + ", " +
					    				           COL_SERVICE + ", " +
					    				           COL_NEXT_LIST_SETS_LIST_FORMATS + ", " +
				                                   COL_PROTOCOL_VERSION + ", " +
				                                   COL_LAST_VALIDATION_DATE + ", " +
				                                   COL_IDENTIFY + ", " +
				                                   COL_LISTFORMATS + ", " +
				                                   COL_LISTSETS + ", " +
				                                   COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_RECORDS_ADDED + ", " +
				                                   COL_RECORDS_REPLACED + ", " +
				                                   COL_LAST_OAI_REQUEST + ", " +
				                                   COL_LAST_HARVEST_END_TIME + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + ", " +
				                                   COL_RECORDS_TO_HARVEST + " " +
				                                   
	                                   "FROM " + PROVIDERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_OAI_PROVIDER_URL + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get provider by URL\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByUrl = dbConnectionManager.prepareStatement(selectSql, psGetByUrl);
				} // end if(get by URL PreparedStatement not defined)

				// Set the parameters on the select statement
				psGetByUrl.setString(1, providerURL);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByUrl);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the provider
					Provider provider = new Provider();

					// Set the fields on the provider
					provider.setId(results.getInt(1));
					provider.setCreatedAt(results.getDate(2));
					provider.setUpdatedAt(results.getTimestamp(3));
					provider.setName(results.getString(4));
					provider.setOaiProviderUrl(results.getString(5));
					provider.setTitle(results.getString(6));
					provider.setCreator(results.getString(7));
					provider.setSubject(results.getString(8));
					provider.setDescription(results.getString(9));
					provider.setPublisher(results.getString(10));
					provider.setContributors(results.getString(11));
					provider.setDate(results.getDate(12));
					provider.setType(results.getString(13));
					provider.setFormat(results.getString(14));
					provider.setIdentifier(results.getInt(15));
					provider.setLanguage(results.getString(16));
					provider.setRelation(results.getString(17));
					provider.setCoverage(results.getString(18));
					provider.setRights(results.getString(19));
					provider.setService(results.getBoolean(20));
					provider.setNextListSetsListFormats(results.getDate(21));
					provider.setProtocolVersion(results.getString(22));
					provider.setLastValidationDate(results.getDate(23));
					provider.setIdentify(results.getBoolean(24));
					provider.setListFormats(results.getBoolean(25));
					provider.setListSets(results.getBoolean(26));
					provider.setWarnings(results.getInt(27));
					provider.setErrors(results.getInt(28));
					provider.setRecordsAdded(results.getInt(29));
					provider.setRecordsReplaced(results.getInt(30));
					provider.setLastOaiRequest(results.getString(31));
					provider.setLastHarvestEndTime(results.getDate(32));
					provider.setLastLogReset(results.getDate(33));
					provider.setLogFileName(results.getString(34));
					provider.setNumberOfRecordsToHarvest(results.getLong(35));

					provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
					provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));
					provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));
					if(log.isDebugEnabled())
						log.debug("Found the provider with URL " + providerURL + " in the database.");

					// Return the provider
					return provider;
				} // end if(provider found)

				if(log.isDebugEnabled())
					log.debug("The provider with URL " + providerURL + " was not found in the database.");

				return null;
			} // end synchronized
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the provider with URL " + providerURL, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByURL(providerURL);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally
		} // end synchronized
    } // end method getByURL(String)

    @Override
    public Provider getByName(String name) throws DatabaseConfigException
    {
    	// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
    	synchronized(psGetByNameLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the provider with the name " + name);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a provider by ID was not defined, create it
				if(psGetByName == null || dbConnectionManager.isClosed(psGetByName))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_PROVIDER_ID + ", " +
					    				           COL_CREATED_AT + ", " +
					    				           COL_UPDATED_AT + ", " +
					    				           COL_NAME + ", " +
					    				           COL_OAI_PROVIDER_URL + ", " +
					    				           COL_TITLE + ", " +
					    				           COL_CREATOR + ", " +
					    				           COL_SUBJECT + ", " +
					    				           COL_DESCRIPTION + ", " +
					    				           COL_PUBLISHER + ", " +
					    				           COL_CONTRIBUTORS + ", " +
					    				           COL_DATE + ", " +
					    				           COL_TYPE + ", " +
					    				           COL_FORMAT + ", " +
					    				           COL_IDENTIFIER + ", " +
					    				           COL_LANGUAGE + ", " +
					    				           COL_RELATION + ", " +
					    				           COL_COVERAGE + ", " +
					    				           COL_RIGHTS + ", " +
					    				           COL_SERVICE + ", " +
					    				           COL_NEXT_LIST_SETS_LIST_FORMATS + ", " +
					    				           COL_PROTOCOL_VERSION + ", " +
					    				           COL_LAST_VALIDATION_DATE + ", " +
					    				           COL_IDENTIFY + ", " +
					    				           COL_LISTFORMATS + ", " +
					    				           COL_LISTSETS + ", " +
					    				           COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_RECORDS_ADDED + ", " +
				                                   COL_RECORDS_REPLACED + ", " +
				                                   COL_LAST_OAI_REQUEST + ", " +
				                                   COL_LAST_HARVEST_END_TIME + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + ", " +
				                                   COL_RECORDS_TO_HARVEST + " " +
	                                   "FROM " + PROVIDERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_NAME + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get provider by name\" PreparedStatement from the SQL " + selectSql);

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
					// The Object which will contain data on the provider
					Provider provider = new Provider();

					// Set the fields on the provider
					provider.setId(results.getInt(1));
					provider.setCreatedAt(results.getDate(2));
					provider.setUpdatedAt(results.getTimestamp(3));
					provider.setName(results.getString(4));
					provider.setOaiProviderUrl(results.getString(5));
					provider.setTitle(results.getString(6));
					provider.setCreator(results.getString(7));
					provider.setSubject(results.getString(8));
					provider.setDescription(results.getString(9));
					provider.setPublisher(results.getString(10));
					provider.setContributors(results.getString(11));
					provider.setDate(results.getDate(12));
					provider.setType(results.getString(13));
					provider.setFormat(results.getString(14));
					provider.setIdentifier(results.getInt(15));
					provider.setLanguage(results.getString(16));
					provider.setRelation(results.getString(17));
					provider.setCoverage(results.getString(18));
					provider.setRights(results.getString(19));
					provider.setService(results.getBoolean(20));
					provider.setNextListSetsListFormats(results.getDate(21));
					provider.setProtocolVersion(results.getString(22));
					provider.setLastValidationDate(results.getDate(23));
					provider.setIdentify(results.getBoolean(24));
					provider.setListFormats(results.getBoolean(25));
					provider.setListSets(results.getBoolean(26));
					provider.setWarnings(results.getInt(27));
					provider.setErrors(results.getInt(28));
					provider.setRecordsAdded(results.getInt(29));
					provider.setRecordsReplaced(results.getInt(30));
					provider.setLastOaiRequest(results.getString(31));
					provider.setLastHarvestEndTime(results.getDate(32));
					provider.setLastLogReset(results.getDate(33));
					provider.setLogFileName(results.getString(34));
					provider.setNumberOfRecordsToHarvest(results.getLong(35));

					provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
					provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));
					provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));

					if(log.isDebugEnabled())
						log.debug("Found the provider with the name " + name + " in the database.");

					// Return the provider
					return provider;
				} // end if(provider found)

				if(log.isDebugEnabled())
					log.debug("The provider with the name " + name + " was not found in the database.");

				return null;
			} // end try(get provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the provider with the name " + name, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByName(name);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
    } // end method getByName(String)

	@Override
	public Provider loadBasicProvider(int providerId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the provider with ID " + providerId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get a provider by ID was not defined, create it
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_PROVIDER_ID + ", " +
					    				           COL_CREATED_AT + ", " +
					    				           COL_UPDATED_AT + ", " +
					    				           COL_NAME + ", " +
					    				           COL_OAI_PROVIDER_URL + ", " +
					    				           COL_TITLE + ", " +
					    				           COL_CREATOR + ", " +
					    				           COL_SUBJECT + ", " +
					    				           COL_DESCRIPTION + ", " +
					    				           COL_PUBLISHER + ", " +
					    				           COL_CONTRIBUTORS + ", " +
					    				           COL_DATE + ", " +
					    				           COL_TYPE + ", " +
					    				           COL_FORMAT + ", " +
					    				           COL_IDENTIFIER + ", " +
					    				           COL_LANGUAGE + ", " +
					    				           COL_RELATION + ", " +
					    				           COL_COVERAGE + ", " +
					    				           COL_RIGHTS + ", " +
					    				           COL_SERVICE + ", " +
					    				           COL_NEXT_LIST_SETS_LIST_FORMATS + ", " +
					    				           COL_PROTOCOL_VERSION + ", " +
					    				           COL_LAST_VALIDATION_DATE + ", " +
					    				           COL_IDENTIFY + ", " +
					    				           COL_LISTFORMATS + ", " +
					    				           COL_LISTSETS + ", " +
					    				           COL_WARNINGS + ", " +
				                                   COL_ERRORS + ", " +
				                                   COL_RECORDS_ADDED + ", " +
				                                   COL_RECORDS_REPLACED + ", " +
				                                   COL_LAST_OAI_REQUEST + ", " +
				                                   COL_LAST_HARVEST_END_TIME + ", " +
				                                   COL_LAST_LOG_RESET + ", " +
				                                   COL_LOG_FILE_NAME + ", " +
				                                   COL_RECORDS_TO_HARVEST + " " +
	                                   "FROM " + PROVIDERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_PROVIDER_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get provider by ID\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the update statement
				psGetById.setInt(1, providerId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the provider
					Provider provider = new Provider();

					// Set the fields on the provider
					provider.setId(results.getInt(1));
					provider.setCreatedAt(results.getDate(2));
					provider.setUpdatedAt(results.getTimestamp(3));
					provider.setName(results.getString(4));
					provider.setOaiProviderUrl(results.getString(5));
					provider.setTitle(results.getString(6));
					provider.setCreator(results.getString(7));
					provider.setSubject(results.getString(8));
					provider.setDescription(results.getString(9));
					provider.setPublisher(results.getString(10));
					provider.setContributors(results.getString(11));
					provider.setDate(results.getDate(12));
					provider.setType(results.getString(13));
					provider.setFormat(results.getString(14));
					provider.setIdentifier(results.getInt(15));
					provider.setLanguage(results.getString(16));
					provider.setRelation(results.getString(17));
					provider.setCoverage(results.getString(18));
					provider.setRights(results.getString(19));
					provider.setService(results.getBoolean(20));
					provider.setNextListSetsListFormats(results.getDate(21));
					provider.setProtocolVersion(results.getString(22));
					provider.setLastValidationDate(results.getDate(23));
					provider.setIdentify(results.getBoolean(24));
					provider.setListFormats(results.getBoolean(25));
					provider.setListSets(results.getBoolean(26));
					provider.setWarnings(results.getInt(27));
					provider.setErrors(results.getInt(28));
					provider.setRecordsAdded(results.getInt(29));
					provider.setRecordsReplaced(results.getInt(30));
					provider.setLastOaiRequest(results.getString(31));
					provider.setLastHarvestEndTime(results.getDate(32));
					provider.setLastLogReset(results.getDate(33));
					provider.setLogFileName(results.getString(34));
					provider.setNumberOfRecordsToHarvest(results.getLong(35));

					if(log.isDebugEnabled())
						log.debug("Found the provider with ID " + providerId + " in the database.");

					// Return the provider
					return provider;
				} // end if(provider found)

				if(log.isDebugEnabled())
					log.debug("The provider with ID " + providerId + " was not found in the database.");

				return null;
			} // end try(get the provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the provider with ID " + providerId, e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return loadBasicProvider(providerId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method loadBasicProvider(int)

	@Override
	public boolean insert(Provider provider) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the provider are valid
		validateFields(provider, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new provider");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a provider was not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + PROVIDERS_TABLE_NAME + " (" + COL_CREATED_AT + ", " +
	            	    													COL_UPDATED_AT + ", " +
	            	    													COL_NAME + ", " +
	            	    													COL_OAI_PROVIDER_URL + ", " +
	            	    													COL_TITLE + ", " +
	            	    													COL_CREATOR + ", " +
	            	    													COL_SUBJECT + ", " +
	            	    													COL_DESCRIPTION + ", " +
	            	    													COL_PUBLISHER + ", " +
	            	    													COL_CONTRIBUTORS + ", " +
	            	    													COL_DATE + ", " +
	            	    													COL_TYPE + ", " +
	            	    													COL_FORMAT + ", " +
	            	    													COL_IDENTIFIER + ", " +
	            	    													COL_LANGUAGE + ", " +
	            	    													COL_RELATION + ", " +
	            	    													COL_COVERAGE + ", " +
	            	    													COL_RIGHTS + ", " +
	            	    													COL_SERVICE + ", " +
	            	    													COL_NEXT_LIST_SETS_LIST_FORMATS + ", " +
	            	    													COL_PROTOCOL_VERSION + ", " +
	            	    													COL_LAST_VALIDATION_DATE + ", " +
	            	    													COL_IDENTIFY + ", " +
	            	    													COL_LISTFORMATS + ", " +
	            	    													COL_LISTSETS + ", " +
	            	    													COL_WARNINGS + ", " +
	            	    													COL_ERRORS + ", " +
	            	    													COL_RECORDS_ADDED + ", " +
	            	    													COL_RECORDS_REPLACED + ", " +
	            	    													COL_LAST_OAI_REQUEST + ", " +
	            	    													COL_LAST_HARVEST_END_TIME + ", " +
	            	    													COL_LAST_LOG_RESET + ", " +
	            	    													COL_LOG_FILE_NAME + ", " +
	            	    													COL_RECORDS_TO_HARVEST + ") " +
	            				       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?," +
	            				              " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
	            				              " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
	            				              " ?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert provider\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setDate(1, provider.getCreatedAt());
				psInsert.setTimestamp(2, provider.getUpdatedAt());
				psInsert.setString(3, provider.getName());
				psInsert.setString(4, provider.getOaiProviderUrl());
				psInsert.setString(5, provider.getTitle());
				psInsert.setString(6, provider.getCreator());
				psInsert.setString(7, provider.getSubject());
				psInsert.setString(8, provider.getDescription());
				psInsert.setString(9, provider.getPublisher());
				psInsert.setString(10, provider.getContributors());
				psInsert.setDate(11, provider.getDate());
				psInsert.setString(12, provider.getType());
				psInsert.setString(13, provider.getFormat());
				psInsert.setInt(14, provider.getIdentifier());
				psInsert.setString(15, provider.getLanguage());
				psInsert.setString(16, provider.getRelation());
				psInsert.setString(17, provider.getCoverage());
				psInsert.setString(18, provider.getRights());
				psInsert.setBoolean(19, provider.getService());
				psInsert.setDate(20, provider.getNextListSetsListFormats());
				psInsert.setString(21, provider.getProtocolVersion());
				psInsert.setDate(22, provider.getLastValidationDate());
				psInsert.setBoolean(23, provider.getIdentify());
				psInsert.setBoolean(24, provider.getListFormats());
				psInsert.setBoolean(25, provider.getListSets());
				psInsert.setInt(26, provider.getWarnings());
				psInsert.setInt(27, provider.getErrors());
				psInsert.setInt(28, provider.getRecordsAdded());
				psInsert.setInt(29, provider.getRecordsReplaced());
				psInsert.setString(30, provider.getLastOaiRequest());
				psInsert.setDate(31, provider.getLastHarvestEndTime());
				psInsert.setDate(32, provider.getLastLogReset());
				psInsert.setString(33, provider.getLogFileName());
				psInsert.setLong(34, provider.getNumberOfRecordsToHarvest());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated resource identifier ID and set it correctly on this Provider Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        provider.setId(rs.getInt(1));

				    boolean success = true;

				    // Add the correct formats for the provider
				    for(Format format : provider.getFormats())
				    	success = getProviderFormatUtilDAO().insert(provider.getId(), format.getId()) && success;

				    // Add the correct sets for the provider
				    for(Set set : provider.getSets())
					    success = (set.getId() <= 0 ? getSetDAO().insertForProvider(set, provider.getId()) : getSetDAO().addToProvider(set, provider.getId())) && success;

				    if(success)
				    	LogWriter.addInfo(logObj.getLogFileLocation(), "Added a new repository with the URL " + provider.getOaiProviderUrl());
				    else
				    {
				    	LogWriter.addWarning(logObj.getLogFileLocation(), "Added a new repository with the URL " + provider.getOaiProviderUrl() + ", but failed to mark which sets and formats it outputs");
				    	
				    	logObj.setWarnings(logObj.getWarnings() + 1);
				    	getLogDAO().update(logObj);
				    }
				    
					return success;
				} // end if(insert succeeded)
				else
				{
					LogWriter.addError(logObj.getLogFileLocation(), "Failed to add a new repository with the URL " + provider.getOaiProviderUrl());
					
					logObj.setErrors(logObj.getErrors() + 1);
			    	getLogDAO().update(logObj);
			    	
					return false;
				}
			} // end try(insert the provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new provider", e);
				
				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while trying to add a new repository with the URL " + provider.getOaiProviderUrl());

				logObj.setErrors(logObj.getErrors() + 1);
		    	getLogDAO().update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(provider);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end insert(Provider)

	@Override
	public boolean update(Provider provider) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the provider are valid
		validateFields(provider, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the provider with ID " + provider.getId());

			try
			{
				// If the PreparedStatement to update a provider is not defined, create it
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + PROVIDERS_TABLE_NAME + " SET " + COL_CREATED_AT + "=?, " +
				                                                          COL_NAME + "=?, " +
				                                                          COL_OAI_PROVIDER_URL + "=?, " +
				                                                          COL_TITLE + "=?, " +
				                                                          COL_CREATOR + "=?, " +
				                                                          COL_SUBJECT + "=?, " +
				                                                          COL_DESCRIPTION + "=?, " +
				                                                          COL_PUBLISHER + "=?, " +
				                                                          COL_CONTRIBUTORS + "=?, " +
				                                                          COL_DATE + "=?, " +
				                                                          COL_TYPE + "=?, " +
				                                                          COL_FORMAT + "=?, " +
				                                                          COL_IDENTIFIER + "=?, " +
				                                                          COL_LANGUAGE + "=?, " +
				                                                          COL_RELATION + "=?, " +
				                                                          COL_COVERAGE + "=?, " +
				                                                          COL_RIGHTS + "=?, " +
				                                                          COL_SERVICE + "=?, " +
				                                                          COL_NEXT_LIST_SETS_LIST_FORMATS + "=?, " +
				                                                          COL_PROTOCOL_VERSION + "=?, " +
				                                                          COL_LAST_VALIDATION_DATE + "=?, " +
				                                                          COL_IDENTIFY + "=?, " +
				                                                          COL_LISTFORMATS + "=?, " +
				                                                          COL_LISTSETS + "=?, " +
				                                                          COL_WARNINGS + "=?, " +
				                                                          COL_ERRORS + "=?, " +
				                                                          COL_RECORDS_ADDED + "=?, " +
				                                                          COL_RECORDS_REPLACED + "=?, " +
				                                                          COL_LAST_OAI_REQUEST + "=?, " +
				                                                          COL_LAST_HARVEST_END_TIME + "=?, " +
				                                                          COL_LAST_LOG_RESET + "=?, " +
				                                                          COL_LOG_FILE_NAME + "=?, " +
				                                                          COL_RECORDS_TO_HARVEST + "=? " +
	                                   "WHERE " + COL_PROVIDER_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the PreparedStatement to update a provider from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setDate(1, provider.getCreatedAt());
				psUpdate.setString(2, provider.getName());
				psUpdate.setString(3, provider.getOaiProviderUrl());
				psUpdate.setString(4, provider.getTitle());
				psUpdate.setString(5, provider.getCreator());
				psUpdate.setString(6, provider.getSubject());
				psUpdate.setString(7, provider.getDescription());
				psUpdate.setString(8, provider.getPublisher());
				psUpdate.setString(9, provider.getContributors());
				psUpdate.setDate(10, provider.getDate());
				psUpdate.setString(11, provider.getType());
				psUpdate.setString(12, provider.getFormat());
				psUpdate.setInt(13, provider.getIdentifier());
				psUpdate.setString(14, provider.getLanguage());
				psUpdate.setString(15, provider.getRelation());
				psUpdate.setString(16, provider.getCoverage());
				psUpdate.setString(17, provider.getRights());
				psUpdate.setBoolean(18, provider.getService());
				psUpdate.setDate(19, provider.getNextListSetsListFormats());
				psUpdate.setString(20, provider.getProtocolVersion());
				psUpdate.setDate(21, provider.getLastValidationDate());
				psUpdate.setBoolean(22, provider.getIdentify());
				psUpdate.setBoolean(23, provider.getListFormats());
				psUpdate.setBoolean(24, provider.getListSets());
				psUpdate.setInt(25, provider.getWarnings());
				psUpdate.setInt(26, provider.getErrors());
				psUpdate.setInt(27, provider.getRecordsAdded());
				psUpdate.setInt(28, provider.getRecordsReplaced());
				psUpdate.setString(29, provider.getLastOaiRequest());
				psUpdate.setDate(30, provider.getLastHarvestEndTime());
				psUpdate.setDate(31, provider.getLastLogReset());
				psUpdate.setString(32, provider.getLogFileName());
				psUpdate.setLong(33, provider.getNumberOfRecordsToHarvest());
				psUpdate.setInt(34, provider.getId());

				// Execute the update statement and return the result
				// Execute the update statement and return the result
				if(dbConnectionManager.executeUpdate(psUpdate) > 0)
				{
					// Remove the old permissions for the group
					boolean success = getProviderFormatUtilDAO().deleteFormatsForProvider(provider.getId());

					// Remove all sets from this provider that used to belong to it but no longer do
				    for(Set set : getSetDAO().getSetsForProvider(provider.getId()))
					    if(!provider.getSets().contains(set))
					    	success = getSetDAO().removeFromProvider(set, provider.getId()) && success;

				    // Add the correct sets for the provider
				    for(Set set : provider.getSets())
				    	success = (set.getId() <= 0 ? getSetDAO().insertForProvider(set, provider.getId()) : getSetDAO().addToProvider(set, provider.getId())) && success;

				    // Add the permissions to the group
				    for(Format format : provider.getFormats())
				    	success = getProviderFormatUtilDAO().insert(provider.getId(), format.getId()) && success;

				    if(success)
				    	LogWriter.addInfo(logObj.getLogFileLocation(), "Updated the repository with the URL " + provider.getOaiProviderUrl());
				    else
				    {
				    	LogWriter.addWarning(logObj.getLogFileLocation(), "Updated the repository with the URL " + provider.getOaiProviderUrl() + ", but failed to update the sets and formats it outputs");
				    
				    	logObj.setWarnings(logObj.getWarnings() + 1);
				    	getLogDAO().update(logObj);
				    }
				    
					return success;
				} // end if(update successful)
				else
				{
					LogWriter.addError(logObj.getLogFileLocation(), "Failed to update the repository with the URL " + provider.getOaiProviderUrl());
					
					logObj.setErrors(logObj.getErrors() + 1);
			    	getLogDAO().update(logObj);
			    	
					return false;
				}
			} // end try(update the provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the provider with ID " + provider.getId(), e);

				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while trying to update the repository with the URL " + provider.getOaiProviderUrl());
				
				logObj.setErrors(logObj.getErrors() + 1);
		    	getLogDAO().update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(provider);
			}
		} // end synchronized
	} // end update(Provider)

	@Override
	public boolean delete(Provider provider) throws DataException, IndexException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the ID field on the provider are valid
		validateFields(provider, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the provider with ID " + provider.getId());

			try
			{
				// If the PreparedStatement to delete a provider was not defined, create it
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM "+ PROVIDERS_TABLE_NAME + " " +
		                               "WHERE " + COL_PROVIDER_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the PreparedStatement to delete a provider the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, provider.getId());

				// Execute the delete statement and return the result
				dbConnectionManager.execute(psDelete);

				boolean success = true;

				// If the delete was successful, remove all sets from the provider
				// without deleting them.  Also mark all records from the provider
				// as deleted, as well as all records processed from them.
				if(success)
				{
					// Remove the reference from provider to the set
					for(Set set : getSetDAO().getSetsForProvider(provider.getId()))
						success = getSetDAO().removeFromProvider(set, provider.getId()) && success;
					
					// Remove the reference from provider to the harvested record set
					for(Set set : getSetDAO().getRecordSetsForProvider(provider.getId()))
						success = getSetDAO().removeFromProvider(set, provider.getId()) && success;
	
					for(Record record : getRecordService().getByProviderId(provider.getId()))
						success = markAsDeleted(record) && success;
					
					// TODO performance issue
					for(Set set : getSetDAO().getAll())
						if(getRecordService().getBySetSpec(set.getSetSpec()).size() == 0)
							getSetDAO().delete(set);
					
					((SolrIndexManager)config.getBean("SolrIndexManager")).commitIndex();
				} // end if(delete succeeded)

				if(success)
			    	LogWriter.addInfo(logObj.getLogFileLocation(), "Deleted the repository with the URL " + provider.getOaiProviderUrl());
			    else
			    {
			    	LogWriter.addWarning(logObj.getLogFileLocation(), "Deleted the repository with the URL " + provider.getOaiProviderUrl() + ", but failed to mark its sets and records as deleted");
			    	
			    	logObj.setWarnings(logObj.getWarnings() + 1);
			    	getLogDAO().update(logObj);
			    }
				
			    return success;
			} // end try(delete the provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the provider with ID " + provider.getId(), e);

				LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while trying to delete the repository with the URL " + provider.getOaiProviderUrl());
				
				logObj.setErrors(logObj.getErrors() + 1);
				getLogDAO().update(logObj);
		    	
				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(provider);
			}
		} // end synchronized
	} // end method delete(Provider)

	/**
	 * Marks a record as deleted.  Also marks any records processed from it as deleted
	 *
	 * @param deleteMe The record to delete
	 * @return true on success, false on failure
	 */
	private boolean markAsDeleted(Record record) throws DataException, IndexException
	{
		// Whether or not we deleted the record and all records processed from it successfully
		boolean success = true;

		// Mark the record as deleted
		record.setDeleted(true);
		record.setUpdatedAt(new Date());
		success = getRecordService().update(record);
		
		// TODO changed from getProcessedFrom to getSuccessors. needs to be tested
		// If there were no records processed from this record, we're done
		if(record.getSuccessors().size() == 0)
			return success;

		// If we got here, we need to recursively delete all records
		// processed from the record we just marked as deleted.
		for(Record successorRecord : record.getSuccessors())
			success = markAsDeleted(successorRecord) && success;

		// Return whether or not all deletes were successful
		return success;
	} // end method markAsDeleted(Record)
} // end class DefaultProviderDAO
