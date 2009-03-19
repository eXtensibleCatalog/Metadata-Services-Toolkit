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

import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating which formats belong to a provider
 *
 * @author Eric Osisek
 */
public class DefaultProviderFormatUtilDAO extends ProviderFormatUtilDAO
{
	/**
	 * A PreparedStatement to add a format for a provider into the database
	 */
	private static PreparedStatement psInsert = null;

	/**
	 * A PreparedStatement to remove a format from a provider into the database
	 */
	private static PreparedStatement psDelete = null;

	/**
	 * A PreparedStatement to get the formats that belong to a provider
	 */
	private static PreparedStatement psGetFormatsForProvider = null;

	/**
	 * A PreparedStatement to remove all formats for a provider
	 */
	private static PreparedStatement psDeleteFormatForProvider = null;

	/**
	 * Lock to prevent concurrent access of the prepared statement to add a format for a provider
	 */
	private static Object psInsertLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to remove a format from a provider
	 */
	private static Object psDeleteLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to get all formats for a provider
	 */
	private static Object psGetFormatsForProviderLock = new Object();

	/**
	 * Lock to prevent concurrent access of the prepared statement to remove all formats from a provider
	 */
	private static Object psDeleteFormatsForProviderLock = new Object();

	@Override
	public boolean insert(int providerId, int formatId)
	{
		synchronized(psInsertLock)
		{
			if(log.isDebugEnabled())
				log.debug("Adding the format with ID " + formatId + " to the provider with ID " + providerId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a provider to Top Level Tab is not defined, create it
				if(psInsert == null)
				{
					// SQL to insert the new row
					String insertSql = "INSERT INTO " + FORMATS_TO_PROVIDERS_TABLE_NAME +
					                                    " (" + COL_PROVIDER_ID + ", " +
	            	    								       COL_FORMAT_ID + ") " +
	            		    		   "VALUES (?, ?)";

					if(log.isDebugEnabled())
						log.debug("Creating the \"add format for a provider\" PreparedStatement from the SQL " + insertSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psInsert = dbConnection.prepareStatement(insertSql);
				} // end if (insert prepared statement is null)

				// Set the parameters on the insert statement
				psInsert.setInt(1, providerId);
				psInsert.setInt(2, formatId);

				// Execute the insert statement and return the result
				return psInsert.executeUpdate() > 0;
			} // end try (insert the format)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while adding tthe format with ID " + formatId + " for the provider with ID " + providerId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method insert(int, int)

	@Override
	public boolean delete(int providerId, int formatId)
	{
		synchronized(psDeleteLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the format with ID " + formatId + " from the provider with ID " + providerId + ".");

			// The ResultSet returned by the query
			ResultSet rs = null;

			try
			{
				// If the PreparedStatement to insert a provider to format is not defined, create it
				if(psDelete == null)
				{
					// SQL to insert the new row
					String deleteSql = "DELETE FROM " + FORMATS_TO_PROVIDERS_TABLE_NAME +
	            		    		   "WHERE " + COL_PROVIDER_ID + "=? " +
	            		    		   "AND " + COL_FORMAT_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove format from a provider\" PreparedStatement from the SQL " + deleteSql);

					// A prepared statement to run the insert SQL
					// This should sanitize the SQL and prevent SQL injection
					psDelete = dbConnection.prepareStatement(deleteSql);
				} // end if (delete prepared statement is null)

				// Set the parameters on the insert statement
				psDelete.setInt(1, providerId);
				psDelete.setInt(2, formatId);

				// Execute the delete statement and return the result
				return psDelete.executeUpdate() > 0;
			} // end try (remove the format from the provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while removing the format with ID " + formatId + " from the provider with ID " + providerId + ".", e);

				return false;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(rs);
			} // end finally
		} // end synchronized
	} // end method delete(int, int)

	@Override
	public List<Integer> getFormatsForProvider(int providerId)
	{
		synchronized(psGetFormatsForProviderLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the formats for the provider with provider ID " + providerId);

			// The ResultSet from the SQL query
			ResultSet results = null;

			// The list of formats for the provider with the passed ID
			List<Integer> formatIds = new ArrayList<Integer>();

			try
			{
				// If the PreparedStatement to get formats by provider ID wasn't defined, create it
				if(psGetFormatsForProvider == null)
				{
					// SQL to get the rows
					String selectSql = "SELECT " + COL_FORMAT_ID + " " +
	                                   "FROM " + FORMATS_TO_PROVIDERS_TABLE_NAME + " " +
	                                   "WHERE " + COL_PROVIDER_ID + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"get formats for provider\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetFormatsForProvider = dbConnection.prepareStatement(selectSql);
				} // end if(get formats for provider PreparedStatement not defined)

				// Set the parameters on the select statement
				psGetFormatsForProvider.setInt(1, providerId);

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetFormatsForProvider.executeQuery();

				// For each result returned, add a provider to Top Level Tab object to the list with the returned data
				while(results.next())
					formatIds.add(new Integer(results.getInt(1)));

				if(log.isDebugEnabled())
					log.debug("Found " + formatIds.size() + " format IDs that the provider with provider ID " + providerId + " belongs to.");

				return formatIds;
			} // end try (get and return the format IDs which the provider belongs to)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the formats for the provider with provider ID " + providerId, e);

				return formatIds;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally
		} // end synchronized
	} // end method getFormatsForProvider(int)

	@Override
	public boolean deleteFormatsForProvider(int providerId)
	{
		synchronized(psDeleteFormatsForProviderLock)
		{
			if(log.isDebugEnabled())
				log.debug("Removing the formats for the provider with provider ID " + providerId);

			try
			{
				// If the PreparedStatement to delete formats by provider ID wasn't defined, create it
				if(psDeleteFormatForProvider == null)
				{
					// SQL to get the rows
					String selectSql = "DELETE FROM " + FORMATS_TO_PROVIDERS_TABLE_NAME + " " +
		    		                   "WHERE " + COL_PROVIDER_ID + "=? ";

					if(log.isDebugEnabled())
						log.debug("Creating the \"remove formats for provider\" PreparedStatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psDeleteFormatForProvider = dbConnection.prepareStatement(selectSql);
				} // end if(delete formats for provider PreparedStatement not defined)

				// Set the parameters on the select statement
				psDeleteFormatForProvider.setInt(1, providerId);

				// Get the result of the SELECT statement

				// Execute the insert statement and return the result
				return psDeleteFormatForProvider.executeUpdate() > 0;
			} // end try (remove all formats from the provider)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while deleting the formats for the provider with provider ID " + providerId, e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method deleteFormatsForProvider(int)
} // end class DefaultProviderFormatUtilDAO
