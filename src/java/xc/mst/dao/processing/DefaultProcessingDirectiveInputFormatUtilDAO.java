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

/**
 * MySQL implementation of the utility class for manipulating the formats that trigger a processing directive
 *
 * @author Eric Osisek
 */
public class DefaultProcessingDirectiveInputFormatUtilDAO extends ProcessingDirectiveInputFormatUtilDAO
{
	/**
	 * A PreparedStatement to add an input format to a processing directive into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to remove an input format to a processing directive into the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A PreparedStatement to get the input formats for a processing directive
	 */
	private static PreparedStatement psGetInputFormatsForProcessingDirective = null;

	/**
	 * A PreparedStatement to remove all input formats from a processing directive
	 */
	private static PreparedStatement psDeleteInputFormatsForProcessingDirective = null;

	/**
	 * Lock to prevent concurrent access of the prepared statement to add an
	 * input format to a processing directive into the database
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to remove an
	 * input format to a processing directive into the database
	 */
	private static Object psDeleteLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to get
	 * the input formats for a processing directive
	 */
	private static Object psGetInputFormatsForProcessingDirectiveLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to
	 * remove all input formats from a processing directive
	 */
	private static Object psDeleteInputFormatsForProcessingDirectiveLock = new Object();

	@Override
	public boolean insert(int processingDirectiveId, int inputFormatId)
	{
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Adding the format with ID " + inputFormatId + " as input for the processing directive with ID " + processingDirectiveId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to add an input format to a processing directive is not defined, create it
				if(psInsert == null || dbConnectionManager.isClosed(psInsert))
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + PROCESSING_DIRECTIVES_TO_INPUT_FORMATS_TABLE_NAME +
					                                    " (" + COL_PROCESSING_DIRECTIVE_ID + ", " +
	            	    								       COL_FORMAT_ID + ") " +
	            		    		   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"add input format to processing directive\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
				} // end if (insert prepared statement is null)

				// Format the parameters on the insert statement
				psInsert.setInt(1, processingDirectiveId);
				psInsert.setInt(2, inputFormatId);

				// Execute the insert statement and return the result
				return dbConnectionManager.executeUpdate(psInsert) > 0;
			} // end try (insert the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while adding the format with ID " + inputFormatId + " as input for the processing directive with ID " + processingDirectiveId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method insert(int, int)

	@Override
	public boolean delete(int processingDirectiveId, int formatId)
	{
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the format with ID " + formatId + " as input for the processing directive with ID " + processingDirectiveId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to delete an input format for a processing directive is not defined, create it
				if(psDelete == null || dbConnectionManager.isClosed(psDelete))
				{
					// SQL to insert the new row
					String deleteSql = "DELETE FROM " + PROCESSING_DIRECTIVES_TO_INPUT_FORMATS_TABLE_NAME + " " +
	            		    		   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=? " +
	            		    		   "AND " + COL_FORMAT_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove input format from processing directive\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
				} // end if (insert prepared statement is null)

				// Format the parameters on the insert statement
				psDelete.setInt(1, processingDirectiveId);
				psDelete.setInt(2, formatId);

				// Execute the delete statement and return the result
				return dbConnectionManager.executeUpdate(psDelete) > 0;
			} // end try (delete the group)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while removing the input format with ID " + formatId + " from the processing directive with ID " + processingDirectiveId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method delete(int, int)

	@Override
	public List<Integer> getInputFormatsForProcessingDirective(int processingDirectiveId)
	{
		synchronized(psGetInputFormatsForProcessingDirectiveLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the input formats for the processing directive with ID " + processingDirectiveId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of groups for the user with the passed ID
			List<Integer> formatIds = new ArrayList<Integer>();

			try
			{
				// If the PreparedStatement to get input formats by processing directive ID wasn't defined, create it
				if(psGetInputFormatsForProcessingDirective == null || dbConnectionManager.isClosed(psGetInputFormatsForProcessingDirective))
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_FORMAT_ID + " " +
	                                   "FROM " + PROCESSING_DIRECTIVES_TO_INPUT_FORMATS_TABLE_NAME + " " +
	                                   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get groups for user\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetInputFormatsForProcessingDirective = dbConnectionManager.prepareStatement(selectSql, psGetInputFormatsForProcessingDirective);
				}

				// Format the parameters on the select statement
				psGetInputFormatsForProcessingDirective.setInt(1, processingDirectiveId);

				// Get the result of the SELECT statement

				// Execute the query
				results = dbConnectionManager.executeQuery(psGetInputFormatsForProcessingDirective);

				// For each result returned, add a format ID to the list with the returned data
				while(results.next())
					formatIds.add(new Integer(results.getInt(1)));

				if(log.isDebugEnabled())
					log.debug("Found " + formatIds.size() + " formats that are input to the processing directive with ID " + processingDirectiveId + ".");

				return formatIds;
			} // end try (get and return results)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the formats that are input for the processing directive with ID " + processingDirectiveId, e);

				return formatIds;
			} // end catch(SQLException)
			finally
			{
				dbConnectionManager.closeResultSet(results);
			} // end finally
		} // end synchronized
	} // end method getInputFormatsForProcessingDirective(int)

	@Override
	public boolean deleteInputFormatsForProcessingDirective(int processingDirectiveId)
	{
		synchronized(psDeleteInputFormatsForProcessingDirectiveLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the input formats from the processing directive with ID " + processingDirectiveId);

			try
			{
				// If the PreparedStatement to delete input formats by processing directive ID wasn't defined, create it
				if(psDeleteInputFormatsForProcessingDirective == null || dbConnectionManager.isClosed(psDeleteInputFormatsForProcessingDirective))
				{
					// SQL to get the rows
					String selectSql = "DELETE FROM " + PROCESSING_DIRECTIVES_TO_INPUT_FORMATS_TABLE_NAME + " " +
		    		                   "WHERE " + COL_PROCESSING_DIRECTIVE_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove input formats from processing directive\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psDeleteInputFormatsForProcessingDirective = dbConnectionManager.prepareStatement(selectSql, psDeleteInputFormatsForProcessingDirective);
				}

				// Format the parameters on the select statement
				psDeleteInputFormatsForProcessingDirective.setInt(1, processingDirectiveId);

				// Get the result of the SELECT statement

				// Execute the insert statement and return the result
				return dbConnectionManager.executeUpdate(psDeleteInputFormatsForProcessingDirective) > 0;
			} // end try (remove all formats that are input for the processing directive)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the formats that are input for the processing directive with ID ID " + processingDirectiveId, e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method deleteInputFormatsForProcessingDirective(int)
} // end class DefaultProcessingDirectiveInputFormatUtilDAO
