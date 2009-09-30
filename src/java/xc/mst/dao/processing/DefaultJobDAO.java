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

import xc.mst.bo.processing.Job;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.harvest.DefaultHarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;

/**
 * MySQL implementation of the data access object for the job table
 *
 * @author Sharmila Ranganathan
 */
public class DefaultJobDAO extends JobDAO
{
	/**
	 * The DAO for inserting and deleting harvest schedule
	 */
	private HarvestScheduleDAO harvestScheduleDao = new DefaultHarvestScheduleDAO();

	/**
	 * The DAO for inserting and deleting services
	 */
	private ServiceDAO serviceDao = new DefaultServiceDAO();
	
	/**
	 * The DAO for inserting and deleting Processing Directive
	 */
	private ProcessingDirectiveDAO processingDirectiveDao = new DefaultProcessingDirectiveDAO();

	/**
	 * A PreparedStatement to get all jobs in the database
	 */
	private static PreparedStatement psGetAll = null;
	
	/**
	 * A PreparedStatement to get a job from the database by its ID
	 */
	private static PreparedStatement psGetById = null;

	/**
	 * A PreparedStatement to get jobs from the database by their harvest schedule ID
	 */
	private static PreparedStatement psGetByHarvestScheduleId = null;

	/**
	 * A PreparedStatement to get jobs from the database by their service ID
	 */
	private static PreparedStatement psGetByServiceId = null;

	/**
	 * A PreparedStatement to insert a job into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to update a job in the database
	 */
	private static PreparedStatement psUpdate = null;

	/**
	 * A PreparedStatement to delete a jobs from the database
	 */
	private static PreparedStatement psDelete = null;
	
	/**
	 * A PreparedStatement to get max order from the database
	 */
	private static PreparedStatement psGetMaxOrder = null;
	
	/**
	 * A PreparedStatement to get next job from the database
	 */
	private static PreparedStatement psNextJobToExecute = null;

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
	 * Lock to synchronize access to the get by service PreparedStatement
	 */
	private static Object psGetByServiceIdLock = new Object();

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
	 * Lock to synchronize access to the get max order PreparedStatement
	 */
	private static Object psMaxOrderLock = new Object();
	
	/**
	 * Lock to synchronize access to the get next job PreparedStatement
	 */
	private static Object psNextJobToExecuteLock = new Object();


	@Override
	public List<Job> getAll() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetAllLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all jobs");

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all Jobs
			List<Job> jobs = new ArrayList<Job>();

			try
			{
				// If the PreparedStatemnt to get all Jobs was not defined, create it
				if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_JOB_ID + ", " +
					    						   COL_HARVEST_SCHEDULE_ID + ", " +
					    						   COL_SERVICE_ID + ", " +
					    						   COL_PROCESSING_DIRECTIVE_ID + ", " +
					    						   COL_OUTPUT_SET_ID + ", " +
					    						   COL_ORDER + " " +
	                                   "FROM " + JOBS_TABLE_NAME + " ORDER BY " + COL_ORDER + " ASC";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get all jobs\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
				} // end if(get all PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetAll);

				// For each result returned, add a Job object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the job
					Job job = new Job();

					// Set the fields on the job
					job.setId(results.getInt(1));
					job.setHarvestSchedule(harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(2)));
					job.setService(serviceDao.loadBasicService(results.getInt(3)));
					job.setProcessingDirective(results.getInt(4) == 0 ? null : processingDirectiveDao.loadBasicProcessingDirective(results.getInt(4)));
					job.setOutputSetId(results.getInt(5));
					job.setOrder(results.getInt(6));

			
					// Add the job to the list
					jobs.add(job);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + jobs.size() + " jobs in the database.");

				return jobs;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the jobs.", e);

				return jobs;
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
	public Job getById(int jobId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the job with ID " + jobId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatemnt to get all Jobs was not defined, create it
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_JOB_ID + ", " +
									   COL_HARVEST_SCHEDULE_ID + ", " +
									   COL_SERVICE_ID + ", " +
									   COL_PROCESSING_DIRECTIVE_ID + ", " +
									   COL_OUTPUT_SET_ID + ", " +
									   COL_ORDER + " " +
	                                   "FROM " + JOBS_TABLE_NAME + " " +
					 			       "WHERE " + COL_JOB_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get job by ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the get by ID prepared statement
				psGetById.setInt(1, jobId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// For each result returned, add a Job object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the job
					Job job = new Job();

					// Set the fields on the job
					job.setId(results.getInt(1));
					job.setHarvestSchedule(results.getInt(2) == 0 ? null : harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(2)));
					job.setService(results.getInt(3) == 0 ? null : serviceDao.loadBasicService(results.getInt(3)));
					job.setProcessingDirective(results.getInt(4) == 0 ? null : processingDirectiveDao.loadBasicProcessingDirective(results.getInt(4)));
					job.setOutputSetId(results.getInt(5));
					job.setOrder(results.getInt(6));

					if(log.isDebugEnabled())
						log.debug("Found the job with ID " + jobId + " in the database.");

					return job;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("Could not find the job with ID " + jobId + " in the database.");

				return null;
			} // end try(get result)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the job with ID " + jobId + ".", e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				
				log.info("Re executing the query that failed ");
				return getById(jobId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public Job loadBasicJob(int jobId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the job with ID " + jobId + ".");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatemnt to get all Jobs was not defined, create it
				if(psGetById == null || dbConnectionManager.isClosed(psGetById))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_JOB_ID + ", " +
									   COL_HARVEST_SCHEDULE_ID + ", " +
									   COL_SERVICE_ID + ", " +
									   COL_PROCESSING_DIRECTIVE_ID + ", " +
									   COL_OUTPUT_SET_ID + ", " +
									   COL_ORDER + " " +
				                       "FROM " + JOBS_TABLE_NAME + " " +
					 			       "WHERE " + COL_JOB_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get job by ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
				} // end if(get by ID PreparedStatement not defined)

				// Set the parameters on the get by ID prepared statement
				psGetById.setInt(1, jobId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetById);

				// For each result returned, add a Job object to the list with the returned data
				if(results.next())
				{
					// The Object which will contain data on the job
					Job job = new Job();

					// Set the fields on the job
					job.setId(results.getInt(1));
					job.setHarvestSchedule(results.getInt(2) == 0 ? null : harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(2)));
					job.setService(results.getInt(3) == 0 ? null : serviceDao.loadBasicService(results.getInt(3)));
					job.setProcessingDirective(results.getInt(4) == 0 ? null : processingDirectiveDao.loadBasicProcessingDirective(results.getInt(4)));
					job.setOutputSetId(results.getInt(5));
					job.setOrder(results.getInt(6));

					if(log.isDebugEnabled())
						log.debug("Found the job with ID " + jobId + " in the database.");

					return job;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("Could not find the job with ID " + jobId + " in the database.");

				return null;
			} // end try(get result)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the job with ID " + jobId + ".", e);

				return null;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				
				log.info("Re executing the query that failed ");
				return loadBasicJob(jobId);
			}			
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method loadBasicJob(int)

	@Override
	public List<Job> getByHarvestScheduleId(int harvestScheduleId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByHarvestScheduleIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all jobs whose harvest schedule is " + harvestScheduleId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all Jobs
			List<Job> jobs = new ArrayList<Job>();

			try
			{
				// If the PreparedStatemnt to get a Job by ID was not defined, create it
				if(psGetByHarvestScheduleId == null || dbConnectionManager.isClosed(psGetByHarvestScheduleId))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_JOB_ID + ", " +
									   COL_HARVEST_SCHEDULE_ID + ", " +
									   COL_SERVICE_ID + ", " +
									   COL_PROCESSING_DIRECTIVE_ID + ", " +
									   COL_OUTPUT_SET_ID + ", " +
									   COL_ORDER + " " +
				                       "FROM " + JOBS_TABLE_NAME + " " +
					 			       "WHERE " + COL_HARVEST_SCHEDULE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get jobs by harvest schedule ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByHarvestScheduleId = dbConnectionManager.prepareStatement(selectSql, psGetByHarvestScheduleId);
				} // end if(get by source harvest schedule ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetByHarvestScheduleId.setInt(1, harvestScheduleId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByHarvestScheduleId);

				// For each result returned, add a Job object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the job
					Job job = new Job();

					// Set the fields on the job
					job.setId(results.getInt(1));
					job.setHarvestSchedule(results.getInt(2) == 0 ? null : harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(2)));
					job.setService(results.getInt(3) == 0 ? null : serviceDao.loadBasicService(results.getInt(3)));
					job.setProcessingDirective(results.getInt(4) == 0 ? null : processingDirectiveDao.loadBasicProcessingDirective(results.getInt(4)));
					job.setOutputSetId(results.getInt(5));
					job.setOrder(results.getInt(6));

					// Add the job to the list
					jobs.add(job);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + jobs.size() + " jobs with harvest schedule ID " + harvestScheduleId + " in the database.");

				return jobs;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the jobs with harvest schedule ID " + harvestScheduleId + ".", e);

				return jobs;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByHarvestScheduleId(harvestScheduleId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getByHarvestScheduleId(int)

	@Override
	public List<Job> getByServiceId(int serviceId) throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetByServiceIdLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting all jobs whose source service is " + serviceId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of all Jobs
			List<Job> jobs = new ArrayList<Job>();

			try
			{
				// If the PreparedStatemnt to get a Job by ID was not defined, create it
				if(psGetByServiceId == null || dbConnectionManager.isClosed(psGetByServiceId))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_JOB_ID + ", " +
									   COL_HARVEST_SCHEDULE_ID + ", " +
									   COL_SERVICE_ID + ", " +
									   COL_PROCESSING_DIRECTIVE_ID + ", " +
									   COL_OUTPUT_SET_ID + ", " +
									   COL_ORDER + " " +
				                       "FROM " + JOBS_TABLE_NAME + " " +
					 			       "WHERE " + COL_SERVICE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get jobs by source service ID\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetByServiceId = dbConnectionManager.prepareStatement(selectSql, psGetByServiceId);
				} // end if(get by source service ID PreparedStatement not defined)

				// Set the parameters on the PreparedStatement
				psGetByServiceId.setInt(1, serviceId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetByServiceId);

				// For each result returned, add a Job object to the list with the returned data
				while(results.next())
				{
					// The Object which will contain data on the job
					Job job = new Job();

					// Set the fields on the job
					job.setId(results.getInt(1));
					job.setHarvestSchedule(results.getInt(2) == 0 ? null : harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(2)));
					job.setService(results.getInt(3) == 0 ? null : serviceDao.loadBasicService(results.getInt(3)));
					job.setProcessingDirective(results.getInt(4) == 0 ? null : processingDirectiveDao.loadBasicProcessingDirective(results.getInt(4)));
					job.setOutputSetId(results.getInt(5));
					job.setOrder(results.getInt(6));
					
					// Add the job to the list
					jobs.add(job);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + jobs.size() + " jobs with source service ID " + serviceId + " in the database.");

				return jobs;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the jobs with source service ID " + serviceId + ".", e);

				return jobs;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getByServiceId(serviceId);
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getBySourceServiceId(int)

	@Override
	public int getMaxOrder() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

		int maxOrder = 0;
		
		synchronized(psMaxOrderLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting max order");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatemnt to get a Job by ID was not defined, create it
				if(psGetMaxOrder == null || dbConnectionManager.isClosed(psGetMaxOrder))
				{
					// SQL to get the rows
					String selectSql = "SELECT MAX(" + COL_ORDER + ") " +
				                       "FROM " + JOBS_TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get max order\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetMaxOrder = dbConnectionManager.prepareStatement(selectSql, psGetMaxOrder);
				} // end if(get by source service ID PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetMaxOrder);

				while(results.next())
				{
					maxOrder = results.getInt(1);
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found " + maxOrder + " as max order in the database.");

				return maxOrder;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting max order.", e);

				return maxOrder;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getMaxOrder();
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getBySourceServiceId(int)
	
	@Override
	public Job getNextJobToExecute() throws DatabaseConfigException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

		synchronized(psNextJobToExecuteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Get next job in queue");

			// The ResultSet from the SQL query
			ResultSet results = null;
			
			// The Object which will contain data on the job
			Job job = null;

			try
			{
				// If the PreparedStatemnt to get a Job by ID was not defined, create it
				if(psNextJobToExecute == null || dbConnectionManager.isClosed(psNextJobToExecute))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_JOB_ID + ", " +
									   COL_HARVEST_SCHEDULE_ID + ", " +
									   COL_SERVICE_ID + ", " +
									   COL_PROCESSING_DIRECTIVE_ID + ", " +
									   COL_OUTPUT_SET_ID + ", " +
									   COL_ORDER + " " +
				                       "FROM " + JOBS_TABLE_NAME + " " +
					 			       "WHERE " + COL_ORDER + "=(SELECT MIN(" + COL_ORDER + ") " +
				                       "FROM " + JOBS_TABLE_NAME + ")";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get max order\" PreparedStatemnt from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psNextJobToExecute = dbConnectionManager.prepareStatement(selectSql, psNextJobToExecute);
				} // end if(get by source service ID PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psNextJobToExecute);

				while(results.next())
				{
					job = new Job();
					// Set the fields on the job
					job.setId(results.getInt(1));
					job.setHarvestSchedule(results.getInt(2) == 0 ? null : harvestScheduleDao.loadBasicHarvestSchedule(results.getInt(2)));
					job.setService(results.getInt(3) == 0 ? null : serviceDao.loadBasicService(results.getInt(3)));
					job.setProcessingDirective(results.getInt(4) == 0 ? null : processingDirectiveDao.getById(results.getInt(4)));
					job.setOutputSetId(results.getInt(5));
					job.setOrder(results.getInt(6));
					
				} // end loop over results

				if(log.isDebugEnabled())
					log.debug("Found job " + job + " in the database.");

				return job;
			} // end try(get results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting max order.", e);

				return job;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return getNextJobToExecute();
			}
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getBySourceServiceId(int)

	@Override
	public boolean insert(Job job) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the non-ID fields on the job are valid
		validateFields(job, false, true);

		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Inserting a new job.");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a job is not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + JOBS_TABLE_NAME + " (" + COL_HARVEST_SCHEDULE_ID + ", " +
	            	    													COL_SERVICE_ID + ", " +
	            	    													COL_PROCESSING_DIRECTIVE_ID + ", " +
	            	    													COL_OUTPUT_SET_ID + ", " +
	            	    													COL_ORDER + ") " +
	            		    		   "VALUES (?, ?, ?, ?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"insert job\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if(insert PreparedStatement not defined)

				// Set the parameters on the insert statement
				psInsert.setInt(1, (job.getHarvestSchedule() == null ? 0 : job.getHarvestSchedule().getId()));
				psInsert.setInt(2, (job.getService() == null ? 0: job.getService().getId()));
				psInsert.setInt(3, (job.getProcessingDirective() == null ? 0 : job.getProcessingDirective().getId()));
				psInsert.setInt(4, job.getOutputSetId());
				psInsert.setInt(5, job.getOrder());

				// Execute the insert statement and return the result
				if(dbConnectionManager.executeUpdate(psInsert) > 0)
				{
					// Get the auto-generated job ID and set it correctly on this Job Object
					rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

				    if (rs.next())
				        job.setId(rs.getInt(1));

					return true;
				} // end if(insert succeeded)
				else
					return false;
			} // end try(insert row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while inserting a new job.", e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return insert(job);
			}
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method insert(Job)

	@Override
	public boolean update(Job job) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the fields on the job are valid
		validateFields(job, true, true);

		synchronized(psUpdateLock)
		{
			if(log.isDebugEnabled())
				log.debug("Updating the job with ID " + job.getId());

			try
			{
				// If the PreparedStatement to update a job was not defined, create it
				if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
				{
					// SQL to update new row
					String updateSql = "UPDATE " + JOBS_TABLE_NAME + " SET " + COL_HARVEST_SCHEDULE_ID + "=?, " +
				                                                          COL_SERVICE_ID + "=?, " +
				                                                          COL_PROCESSING_DIRECTIVE_ID + "=?, " +
				                                                          COL_OUTPUT_SET_ID + "=?, " +
				                                                          COL_ORDER + "=? " +
	                                   "WHERE " + COL_JOB_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update job\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
				} // end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psUpdate.setInt(1, (job.getHarvestSchedule() == null ? 0 : job.getHarvestSchedule().getId()));
				psUpdate.setInt(2, (job.getService() == null ? 0 : job.getService().getId()));
				psUpdate.setInt(3, (job.getProcessingDirective() == null ? 0 : job.getProcessingDirective().getId()));
				psUpdate.setInt(4, job.getOutputSetId());
				psUpdate.setInt(5, job.getOrder());
				psUpdate.setInt(6, job.getId());

				// Execute the insert statement and return the result
				return (dbConnectionManager.executeUpdate(psUpdate) > 0);
			} // end try(update the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the job with ID " + job.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return update(job) ;
			}
		} // end synchronized
	} // end method update(Job)

	@Override
	public boolean delete(Job job) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnectionManager.getDbConnection() == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Check that the ID field on the job are valid
		validateFields(job, true, false);

		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Deleting the service to job ID " + job.getId());

			try
			{
				// If the PreparedStatement to delete a service to output format was not defined, create it
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					
					// SQL to delete the row from the table
					String deleteSql = "DELETE FROM " + JOBS_TABLE_NAME + " " +
									   "WHERE " + COL_JOB_ID + " = ? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"delete job\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the delete SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if(delete PreparedStatement not defined)

				// Set the parameters on the delete statement
				psDelete.setInt(1, job.getId());

				// Execute the delete statement and return the result
				return dbConnectionManager.execute(psDelete);
			} // end try(delete row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the job with ID " + job.getId(), e);

				return false;
			} // end catch(SQLException)
			catch (DBConnectionResetException e){
				log.info("Re executing the query that failed ");
				return delete(job);
			}
		} // end synchronized
	} // end method delete(Job)
} // end class DefaultJobDAO
