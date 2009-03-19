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

import xc.mst.dao.MySqlConnectionManager;

/**
 * MySQL implementation of the utility class for manipulating the sets that trigger a processing directive
 *
 * @author Eric Osisek
 */
public class DefaultProcessingDirectiveInputSetUtilDAO extends ProcessingDirectiveInputSetUtilDAO
{
	/**
	 * A PreparedStatement to add an input set to a processing directive into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to remove an input set to a processing directive into the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A PreparedStatement to get the input sets for a processing directive
	 */
	private static PreparedStatement psGetInputSetsForProcessingDirective = null;

	/**
	 * A PreparedStatement to remove all input sets from a processing directive
	 */
	private static PreparedStatement psDeleteInputSetsForProcessingDirective = null;

	/**
	 * Lock to prevent concurrent access of the prepared statement to add an
	 * input set to a processing directive into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to remove an
	 * input set to a processing directive into the database
	 */
	private static Object psDeleteLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to get
	 * the input sets for a processing directive
	 */
	private static Object psGetInputSetsForProcessingDirectiveLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to
	 * remove all input sets from a processing directive
	 */
	private static Object psDeleteInputSetsForProcessingDirectiveLock = new Object();

	@Override
	public boolean insert(int processingDirectiveId, int inputSetId)
	{
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Adding the set with ID " + inputSetId + " as input for the processing directive with ID " + processingDirectiveId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to add an input set to a processing directive is not defined, create it
				if(psInsert == null)
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + PROCESSING_DIRECTIVES_TO_INPUT_SETS_TABLE_NAME +
					                                    " (" + COL_PROCESSING_DIRECTIVE_ID + ", " +
	            	    								       COL_SET_ID + ") " +
	            		    		   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"add input set to processing directive\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnection.prepareStatement(insertSql);
				} // end if (insert prepared statement is null)

				// Set the parameters on the insert statement
				psInsert.setInt(1, processingDirectiveId);
				psInsert.setInt(2, inputSetId);

				// Execute the insert statement and return the result
				return psInsert.executeUpdate() > 0;
			} // end try (insert the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while adding the set with ID " + inputSetId + " as input for the processing directive with ID " + processingDirectiveId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method insert(int, int)

	@Override
	public boolean delete(int processingDirectiveId, int setId)
	{
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the set with ID " + setId + " as input for the processing directive with ID " + processingDirectiveId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to delete an input set for a processing directive is not defined, create it
				if(psDelete == null)
				{
					// SQL to insert the new row
					String deleteSql = "DELETE FROM " + PROCESSING_DIRECTIVES_TO_INPUT_SETS_TABLE_NAME + " " +
	            		    		   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=? " +
	            		    		   "AND " + COL_SET_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove input set from processing directive\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnection.prepareStatement(deleteSql);
				} // end if (insert prepared statement is null)

				// Set the parameters on the insert statement
				psDelete.setInt(1, processingDirectiveId);
				psDelete.setInt(2, setId);

				// Execute the delete statement and return the result
				return psDelete.executeUpdate() > 0;
			} // end try (delete the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while removing the input set with ID " + setId + " from the processing directive with ID " + processingDirectiveId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method delete(int, int)

	@Override
	public List<Integer> getInputSetsForProcessingDirective(int processingDirectiveId)
	{
		synchronized(psGetInputSetsForProcessingDirectiveLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the input sets for the processing directive with ID " + processingDirectiveId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of groups for the user with the passed ID
			List<Integer> setIds = new ArrayList<Integer>();

			try
			{
				// If the PreparedStatement to get input sets by processing directive ID wasn't defined, create it
				if(psGetInputSetsForProcessingDirective == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_SET_ID + " " +
	                                   "FROM " + PROCESSING_DIRECTIVES_TO_INPUT_SETS_TABLE_NAME + " " +
	                                   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get groups for user\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetInputSetsForProcessingDirective = dbConnection.prepareStatement(selectSql);
				}

				// Set the parameters on the select statement
				psGetInputSetsForProcessingDirective.setInt(1, processingDirectiveId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetInputSetsForProcessingDirective.executeQuery();

				// For each result returned, add a set ID to the list with the returned data
				while(results.next())
					setIds.add(new Integer(results.getInt(1)));

				if(log.isDebugEnabled())
					log.debug("Found " + setIds.size() + " sets that are input to the processing directive with ID " + processingDirectiveId + ".");

				return setIds;
			} // end try (get and return results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the sets that are input for the processing directive with ID " + processingDirectiveId, e);

				return setIds;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally
		} // end synchronized
	} // end method getInputSetsForProcessingDirective(int)

	@Override
	public boolean deleteInputSetsForProcessingDirective(int processingDirectiveId)
	{
		synchronized(psDeleteInputSetsForProcessingDirectiveLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the input sets from the processing directive with ID " + processingDirectiveId);

			try
			{
				// If the PreparedStatement to delete input sets by processing directive ID wasn't defined, create it
				if(psDeleteInputSetsForProcessingDirective == null)
				{
					// SQL to get the rows
					String selectSql = "DELETE FROM " + PROCESSING_DIRECTIVES_TO_INPUT_SETS_TABLE_NAME + " " +
		    		                   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove input sets from processing directive\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psDeleteInputSetsForProcessingDirective = dbConnection.prepareStatement(selectSql);
				}

				// Set the parameters on the select statement
				psDeleteInputSetsForProcessingDirective.setInt(1, processingDirectiveId);

				// Get the result of the SELECT statement

				// Execute the insert statement and return the result
				return psDeleteInputSetsForProcessingDirective.executeUpdate() > 0;
			} // end try (remove all sets that are input for the processing directive)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the sets that are input for the processing directive with ID ID " + processingDirectiveId, e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method deleteInputSetsForProcessingDirective(int)
} // end class DefaultProcessingDirectiveInputSetUtilDAO
