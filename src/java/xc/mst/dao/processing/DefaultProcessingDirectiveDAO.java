/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.processing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.DefaultSetDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;

/**
 * MySQL implementation of the data access object for the processing directives table
 *
 * @author Eric Osisek
 */
public class DefaultProcessingDirectiveDAO extends ProcessingDirectiveDAO
{
	/**
	 * The DAO for inserting and deleting providers
	 */
	private ProviderDAO providerDao = new DefaultProviderDAO();

	/**
	 * The DAO for inserting and deleting services
	 */
	private ServiceDAO serviceDao = new DefaultServiceDAO();

	/**
	 * The DAO for inserting and deleting formats
	 */
	private FormatDAO formatDao = new DefaultFormatDAO();

	/**
	 * The DAO for inserting and deleting sets
	 */
	private SetDAO setDao = new DefaultSetDAO();

	/**
	 * The DAO for adding and removing input formats form processing directives
	 */
	private ProcessingDirectiveInputFormatUtilDAO inputFormatDao = new DefaultProcessingDirectiveInputFormatUtilDAO();

	/**
	 * The DAO for adding and removing input sets form processing directives
	 */
	private ProcessingDirectiveInputSetUtilDAO inputSetDao = new DefaultProcessingDirectiveInputSetUtilDAO();

	/**
	 * A PreparedStatement to get all processing directives in the database
	 */
	private static PreparedStatement psGetAll = null;

	/**
	 * A PreparedStatement to get all processing directives in the database sorted by their full name in ascending order
	 */
	private static PreparedStatement psGetSortedAsc = null;
	
	/**
	 * A PreparedStatement to get all processing directives in the database sorted by their full name in descending order
	 */
	private static PreparedStatement psGetSortedDesc = null;
	
	/**
	 * A PreparedStatement to get a processing directive from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get processing directives from the database by their source provider ID
	 */
	private static PreparedStatement psGetBySourceProviderId = null;

	/**
	 * A PreparedStatement to get processing directives from the database by their source service ID
	 */
	private static PreparedStatement psGetBySourceServiceId = null;

	/**
	 * A PreparedStatement to insert a processing directive into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a processing directive in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a processing directives from the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * Lock to synchronize access to the get all PreparedStatement
	 */
	private static Object psGetAllLock = new Object();

	/**
	 * Lock to synchronize access to the get sorted in ascending order PreparedStatement
	 */
	private static Object psGetSortedAscLock = new Object();

	/**
	 * Lock to synchronize access to the get sorted in descending order PreparedStatement
	 */
	private static Object psGetSortedDescLock = new Object();
	
	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetByIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by source provider ID PreparedStatement
	 */
	private static Object psGetBySourceProviderIdLock = new Object();

	/**
	 * Lock to synchronize access to the get by source service PreparedStatement
	 */
	private static Object psGetBySourceServiceIdLock = new Object();

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
	public List<ProcessingDirective> getAll()
	{
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all processing directives");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all ProcessingDirectives
			ArrayList<ProcessingDirective> processingDirectives = new ArrayList<ProcessingDirective>();

			try
			{
				// If the PreparedStatemnt to get all ProcessingDirectives was not defined, create it
				if(psGetAll == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_PROCESSING_DIRECTIVE_ID + ", " +
					    						   COL_SOURCE_PROVIDER_ID + ", " +
					    						   COL_SOURCE_SERVICE_ID + ", " +
					    						   COL_SERVICE_ID + ", " +
					    						   COL_OUTPUT_SET_ID + ", " +
					    						   COL_MAINTAIN_SOURCE_SETS + " " +
	                                   "FROM " + PROCESSING_DIRECTIVE_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all processing directives\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnection.prepareStatement(selectSql);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetAll.executeQuery();

				// For each result returned, add a ProcessingDirective object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the processing directive
					ProcessingDirective processingDirective = new ProcessingDirective();

					// Set the fields on the processing directive
					processingDirective.setId(results.getInt(1));
					processingDirective.setSourceProvider(providerDao.getById(results.getInt(2)));
					processingDirective.setSourceService(serviceDao.getById(results.getInt(3)));
					processingDirective.setService(serviceDao.getById(results.getInt(4)));
					processingDirective.setOutputSet(setDao.getById(results.getInt(5)));
					processingDirective.setMaintainSourceSets(results.getBoolean(6));

					// Setup the list of triggering format IDs
					for(Integer formatId : inputFormatDao.getInputFormatsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringFormat(formatDao.getById(formatId));

					// Setup the list of triggering set IDs
					for(Integer setId : inputSetDao.getInputSetsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringSet(setDao.getById(setId));

					// Add the processing directive to the list
					processingDirectives.add(processingDirective);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + processingDirectives.size() + " processing directives in the database.");

				return processingDirectives;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the processing directives.", e);

				return processingDirectives;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getAll()

    /**
     * returns a sorted list of processing directives
     * @param asc determines whether the list of rows are to be sorted in ascending or descending order
     * @param columnSorted the column on which the rows are to be sorted.
     * @return list of processing directives
     */
	@Override
	public List<ProcessingDirective> getSorted(boolean asc,String columnSorted)
	{
		return(asc ? getSortedAsc(columnSorted) : getSortedDesc(columnSorted));
	} // end method getSortedByName(boolean)

	/**
	 * Gets all users in the database sorted in ascending order by their user name
	 * 
	 * @return A list of all users in the database sorted in ascending order by their user name
	 */
	private List<ProcessingDirective> getSortedAsc(String columnSorted)
	{
		return getAll();
	} // end method getSortedAsc()
	
	/**
	 * Gets all users in the database sorted in descending order by their user name
	 * 
	 * @return A list of all users in the database sorted in descending order by their user name
	 */
	private List<ProcessingDirective> getSortedDesc(String columnSorted)
	{
		return getAll();
	} // end method getSortedDesc()
	
	@Override
	public ProcessingDirective getById(int processingDirectiveId)
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the processing directive with ID " + processingDirectiveId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatemnt to get all ProcessingDirectives was not defined, create it
				if(psGetById == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_PROCESSING_DIRECTIVE_ID + ", " +
					    						   COL_SOURCE_PROVIDER_ID + ", " +
					    						   COL_SOURCE_SERVICE_ID + ", " +
					    						   COL_SERVICE_ID + ", " +
					    						   COL_OUTPUT_SET_ID + ", " +
					    						   COL_MAINTAIN_SOURCE_SETS + " " +
	                                   "FROM " + PROCESSING_DIRECTIVE_TABLE_NAME + " " +
					 			       "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get processing directive by ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the get by ID prepared statement
				psGetById.setInt(1, processingDirectiveId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetById.executeQuery();

				// For each result returned, add a ProcessingDirective object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the processing directive
					ProcessingDirective processingDirective = new ProcessingDirective();

					// Set the fields on the processing directive
					processingDirective.setId(results.getInt(1));
					processingDirective.setSourceProvider(results.getInt(2) == 0 ? null : providerDao.getById(results.getInt(2)));
					processingDirective.setSourceService(results.getInt(3) == 0 ? null : serviceDao.getById(results.getInt(3)));
					processingDirective.setService(serviceDao.getById(results.getInt(4)));
					processingDirective.setOutputSet(results.getInt(5) == 0 ? null :setDao.getById(results.getInt(5)));
					processingDirective.setMaintainSourceSets(results.getBoolean(6));

					// Setup the list of triggering format IDs
					for(Integer formatId : inputFormatDao.getInputFormatsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringFormat(formatDao.getById(formatId));

					// Setup the list of triggering set IDs
					for(Integer setId : inputSetDao.getInputSetsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringSet(setDao.getById(setId));

					if(log.isDebugEnabled())
						log.debug("Found the processing directive with ID " + processingDirectiveId + " in the database.");

					return processingDirective;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("Could not find the processing directive with ID " + processingDirectiveId + " in the database.");

				return null;
			} // end try(get result)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the processing directive with ID " + processingDirectiveId + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public ProcessingDirective loadBasicProcessingDirective(int processingDirectiveId)
	{
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the processing directive with ID " + processingDirectiveId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatemnt to get all ProcessingDirectives was not defined, create it
				if(psGetById == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_PROCESSING_DIRECTIVE_ID + ", " +
					    						   COL_SOURCE_PROVIDER_ID + ", " +
					    						   COL_SOURCE_SERVICE_ID + ", " +
					    						   COL_SERVICE_ID + ", " +
					    						   COL_OUTPUT_SET_ID + ", " +
					    						   COL_MAINTAIN_SOURCE_SETS + " " +
	                                   "FROM " + PROCESSING_DIRECTIVE_TABLE_NAME + " " +
					 			       "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get processing directive by ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnection.prepareStatement(selectSql);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the get by ID prepared statement
				psGetById.setInt(1, processingDirectiveId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetById.executeQuery();

				// For each result returned, add a ProcessingDirective object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the processing directive
					ProcessingDirective processingDirective = new ProcessingDirective();

					// Set the fields on the processing directive
					processingDirective.setId(results.getInt(1));
					processingDirective.setSourceProvider(providerDao.loadBasicProvider(results.getInt(2)));
					processingDirective.setSourceService(serviceDao.getById(results.getInt(3)));
					processingDirective.setService(serviceDao.getById(results.getInt(4)));
					processingDirective.setOutputSet(setDao.loadBasicSet(results.getInt(5)));
					processingDirective.setMaintainSourceSets(results.getBoolean(6));

					if(log.isDebugEnabled())
						log.debug("Found the processing directive with ID " + processingDirectiveId + " in the database.");

					return processingDirective;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("Could not find the processing directive with ID " + processingDirectiveId + " in the database.");

				return null;
			} // end try(get result)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the processing directive with ID " + processingDirectiveId + ".", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method loadBasicProcessingDirective(int)

	@Override
	public List<ProcessingDirective> getBySourceProviderId(int providerId)
	{
		synchronized(psGetBySourceProviderIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all processing directives whose source provider is " + providerId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all ProcessingDirectives
			ArrayList<ProcessingDirective> processingDirectives = new ArrayList<ProcessingDirective>();

			try
			{
				// If the PreparedStatemnt to get a ProcessingDirective by ID was not defined, create it
				if(psGetBySourceProviderId == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_PROCESSING_DIRECTIVE_ID + ", " +
					    						   COL_SOURCE_PROVIDER_ID + ", " +
					    						   COL_SOURCE_SERVICE_ID + ", " +
					    						   COL_SERVICE_ID + ", " +
					    						   COL_OUTPUT_SET_ID + ", " +
					    						   COL_MAINTAIN_SOURCE_SETS + " " +
	                                   "FROM " + PROCESSING_DIRECTIVE_TABLE_NAME + " " +
					                   "WHERE " + COL_SOURCE_PROVIDER_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get processing directives by source provider ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetBySourceProviderId = dbConnection.prepareStatement(selectSql);
				} // end if(get by source provider ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetBySourceProviderId.setInt(1, providerId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetBySourceProviderId.executeQuery();

				// For each result returned, add a ProcessingDirective object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the processing directive
					ProcessingDirective processingDirective = new ProcessingDirective();

					// Set the fields on the processing directive
					processingDirective.setId(results.getInt(1));
					processingDirective.setSourceProvider(providerDao.getById(results.getInt(2)));
					processingDirective.setSourceService(serviceDao.getById(results.getInt(3)));
					processingDirective.setService(serviceDao.getById(results.getInt(4)));
					processingDirective.setOutputSet(setDao.getById(results.getInt(5)));
					processingDirective.setMaintainSourceSets(results.getBoolean(6));

					// Setup the list of triggering format IDs
					for(Integer formatId : inputFormatDao.getInputFormatsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringFormat(formatDao.getById(formatId));

					// Setup the list of triggering set IDs
					for(Integer setId : inputSetDao.getInputSetsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringSet(setDao.getById(setId));

					// Add the processing directive to the list
					processingDirectives.add(processingDirective);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + processingDirectives.size() + " processing directives with source provider ID " + providerId + " in the database.");

				return processingDirectives;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the processing directives with source provider ID " + providerId + ".", e);

				return processingDirectives;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getBySourceProviderId(int)

	@Override
	public List<ProcessingDirective> getBySourceServiceId(int serviceId)
	{
		synchronized(psGetBySourceServiceIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all processing directives whose source service is " + serviceId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all ProcessingDirectives
			ArrayList<ProcessingDirective> processingDirectives = new ArrayList<ProcessingDirective>();

			try
			{
				// If the PreparedStatemnt to get a ProcessingDirective by ID was not defined, create it
				if(psGetBySourceServiceId == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_PROCESSING_DIRECTIVE_ID + ", " +
					    						   COL_SOURCE_PROVIDER_ID + ", " +
					    						   COL_SOURCE_SERVICE_ID + ", " +
					    						   COL_SERVICE_ID + ", " +
					    						   COL_OUTPUT_SET_ID + ", " +
					    						   COL_MAINTAIN_SOURCE_SETS + " " +
	                                   "FROM " + PROCESSING_DIRECTIVE_TABLE_NAME + " " +
					                   "WHERE " + COL_SOURCE_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get processing directives by source service ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetBySourceServiceId = dbConnection.prepareStatement(selectSql);
				} // end if(get by source service ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetBySourceServiceId.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetBySourceServiceId.executeQuery();

				// For each result returned, add a ProcessingDirective object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the processing directive
					ProcessingDirective processingDirective = new ProcessingDirective();

					// Set the fields on the processing directive
					processingDirective.setId(results.getInt(1));
					processingDirective.setSourceService(serviceDao.getById(results.getInt(2)));
					processingDirective.setSourceService(serviceDao.getById(results.getInt(3)));
					processingDirective.setService(serviceDao.getById(results.getInt(4)));
					processingDirective.setOutputSet(setDao.getById(results.getInt(5)));
					processingDirective.setMaintainSourceSets(results.getBoolean(6));

					// Setup the list of triggering format IDs
					for(Integer formatId : inputFormatDao.getInputFormatsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringFormat(formatDao.getById(formatId));

					// Setup the list of triggering set IDs
					for(Integer setId : inputSetDao.getInputSetsForProcessingDirective(processingDirective.getId()))
						processingDirective.addTriggeringSet(setDao.getById(setId));

					// Add the processing directive to the list
					processingDirectives.add(processingDirective);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + processingDirectives.size() + " processing directives with source service ID " + serviceId + " in the database.");

				return processingDirectives;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the processing directives with source service ID " + serviceId + ".", e);

				return processingDirectives;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getBySourceServiceId(int)

	@Override
	public boolean insert(ProcessingDirective processingDirective) throws DataException
	{
		// Check that the non-ID fields on the processing directive are valid
		validateFields(processingDirective, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new processing directive.");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a processing directive is not defined, create it
				if(psInsert == null)
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + PROCESSING_DIRECTIVE_TABLE_NAME + " (" + COL_SOURCE_PROVIDER_ID + ", " +
	            	    													COL_SOURCE_SERVICE_ID + ", " +
	            	    													COL_SERVICE_ID + ", " +
	            	    													COL_OUTPUT_SET_ID + ", " +
	            	    													COL_MAINTAIN_SOURCE_SETS + ") " +
	            		    		   "VALUES (?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert processing directive\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnection.prepareStatement(insertSql);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setInt(1, (processingDirective.getSourceProvider() == null ? 0 : processingDirective.getSourceProvider().getId()));
				psInsert.setInt(2, (processingDirective.getSourceService() == null ? 0 : processingDirective.getSourceService().getId()));
				psInsert.setInt(3, processingDirective.getService().getId());
				psInsert.setInt(4, (processingDirective.getOutputSet() == null ? 0 : processingDirective.getOutputSet().getId()));
				psInsert.setBoolean(5, processingDirective.getMaintainSourceSets());

				// Execute the insert statement and return the result
				if(psInsert.executeUpdate() > 0)
				{
					// Get the auto-generated processing directive ID and set it correctly on this ProcessingDirective Object
					rs = dbConnection.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        processingDirective.setId(rs.getInt(1));

				    // Add the correct input sets for the processing directive
					for(Set set : processingDirective.getTriggeringSets())
						inputSetDao.insert(processingDirective.getId(), set.getId());

					// Add the correct input formats for the processing directive
					for(Format format : processingDirective.getTriggeringFormats())
						inputFormatDao.insert(processingDirective.getId(), format.getId());

					return true;
				} // end if(insert succeeded)
				else
					return false;
			} // end try(insert row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new processing directive.", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(ProcessingDirective)

	@Override
	public boolean update(ProcessingDirective processingDirective) throws DataException
	{
		// Check that the fields on the processing directive are valid
		validateFields(processingDirective, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the processing directive with ID " + processingDirective.getId());

			try
			{
				// If the PreparedStatement to update a processing directive was not defined, create it
				if(psUpdate == null)
				{
					// SQL to update new row
					String updateSql = "UPDATE " + PROCESSING_DIRECTIVE_TABLE_NAME + " SET " + COL_SOURCE_PROVIDER_ID + "=?, " +
				                                                          COL_SOURCE_SERVICE_ID + "=?, " +
				                                                          COL_SERVICE_ID + "=?, " +
				                                                          COL_OUTPUT_SET_ID + "=?, " +
				                                                          COL_MAINTAIN_SOURCE_SETS + "=? " +
	                                   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update processing directive\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnection.prepareStatement(updateSql);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setInt(1, (processingDirective.getSourceProvider() == null ? 0 : processingDirective.getSourceProvider().getId()));
				psUpdate.setInt(2, (processingDirective.getSourceService() == null ? 0 : processingDirective.getSourceService().getId()));
				psUpdate.setInt(3, processingDirective.getService().getId());
				psUpdate.setInt(4, (processingDirective.getOutputSet() == null ? 0 : processingDirective.getOutputSet().getId()));
				psUpdate.setBoolean(5, processingDirective.getMaintainSourceSets());
				psUpdate.setInt(6, processingDirective.getId());

				// Execute the insert statement and return the result
				if(psUpdate.executeUpdate() > 0)
				{
					// Remove the old input sets and formats for the processing directive
					inputSetDao.deleteInputSetsForProcessingDirective(processingDirective.getId());
					inputFormatDao.deleteInputFormatsForProcessingDirective(processingDirective.getId());

				    // Add the correct input sets for the processing directive
					for(Set set : processingDirective.getTriggeringSets())
						inputSetDao.insert(processingDirective.getId(), set.getId());

					// Add the correct input formats for the processing directive
					for(Format format : processingDirective.getTriggeringFormats())
						inputFormatDao.insert(processingDirective.getId(), format.getId());

					return true;
				} // end if(update succeeded)
				else
					return false;
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the processing directive with ID " + processingDirective.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method update(ProcessingDirective)

	@Override
	public boolean delete(ProcessingDirective processingDirective) throws DataException
	{
		// Check that the ID field on the processing directive are valid
		validateFields(processingDirective, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the service to processing directive ID " + processingDirective.getId());

			try
			{
				// If the PreparedStatement to delete a service to output format was not defined, create it
				if(psDelete == null)
				{
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM " + PROCESSING_DIRECTIVE_TABLE_NAME + " " +
									   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete processing directive\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnection.prepareStatement(deleteSql);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, processingDirective.getId());

				// Execute the delete statement and return the result
				return psDelete.execute();
			} // end try(delete row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the processing directive with ID " + processingDirective.getId(), e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method delete(ProcessingDirective)
} // end class DefaultProcessingDirectiveDAO
