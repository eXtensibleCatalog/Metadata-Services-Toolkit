/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.emailconfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import xc.mst.bo.emailconfig.EmailConfig;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * MySQL implementation of the Data Access Object for the email config table
 *
 * @author Eric Osisek
 */
public class DefaultEmailConfigDAO extends EmailConfigDAO
{
	/**
	 * A PreparedStatement to get an email server from the database by its ID
	 */
	private static PreparedStatement psGetConfiguration = null;

	/**
	 * A PreparedStatement to update an email server in the database
	 */
	private static PreparedStatement psSetConfiguration = null;

	/**
	 * Lock to synchronize access to the get by ID PreparedStatement
	 */
	private static Object psGetConfigurationLock = new Object();

	/**
	 * Lock to synchronize access to the update PreparedStatement
	 */
	private static Object psSetConfigurationLock = new Object();

    @Override
	public EmailConfig getConfiguration() throws DatabaseConfigException
	{
    	// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnection == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		synchronized(psGetConfigurationLock)
		{
			if(log.isDebugEnabled())
				log.debug("Getting the email server configuration");

			// The ResultSet from the SQL query
			ResultSet results = null;

			try
			{
				// If the PreparedStatement to get the email configuration was not defined, create it
				if(psGetConfiguration == null)
				{
					// SQL to get the row
					String selectSql = "SELECT " + COL_EMAIL_CONFIG_ID + ", " +
					                               COL_EMAIL_SERVER_ADDRESS + ", " +
                                                   COL_PORT_NUMBER + ", " +
                                                   COL_FROM_ADDRESS + ", " +
                                                   COL_PASSWORD + ", " +
                                                   COL_ENCRYPTED_CONNECTION + ", " +
                                                   COL_TIMEOUT + ", " +
                                                   COL_FORGOTTEN_PASSWORD_LINK + " " +
	                                   "FROM " + TABLE_NAME;

					if(log.isDebugEnabled())
						log.debug("Creating the \"get email configuration\" PreparedSatement from the SQL " + selectSql);

					// A prepared statement to run the select SQL
					// This should sanitize the SQL and prevent SQL injection
					psGetConfiguration = dbConnection.prepareStatement(selectSql);
				} // end if(get configuration PreparedStatement not defined)

				// Get the result of the SELECT statement

				// Execute the query
				results = psGetConfiguration.executeQuery();

				// If any results were returned
				if(results.next())
				{
					// The Object which will contain data on the email configuration
					EmailConfig emailconfig = new EmailConfig();

					// Set the fields on the email configuration
					emailconfig.setEmailConfigId(results.getInt(1));
					emailconfig.setEmailServerAddress(results.getString(2));
					emailconfig.setPortNumber(results.getInt(3));
					emailconfig.setFromAddress(results.getString(4));
					emailconfig.setPassword(results.getString(5));
					emailconfig.setEncryptedConnection(results.getString(6));
					emailconfig.setTimeout(results.getLong(7));
					emailconfig.setForgottenPasswordLink(results.getBoolean(8));

					if(log.isDebugEnabled())
						log.debug("Found the email configuration in the database.");

					// Return the email configuration
					return emailconfig;
				} // end if(result found)

				if(log.isDebugEnabled())
					log.debug("The email configuration was not found in the database.");

				return null;
			} // end try(get the row)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while getting the email server configuration", e);

				return null;
			} // end catch(SQLException)
			finally
			{
				MySqlConnectionManager.closeResultSet(results);
			} // end finally(close ResultSet)
		} // end synchronized
	} // end method getById(int)

	@Override
	public boolean setConfiguration(EmailConfig emailconfig) throws DataException
	{
		// Throw an exception if the connection is null.  This means the configuration file was bad.
		if(dbConnection == null)
			throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");
		
		// Validate the fields on the passed EmailConfig Object
		validateFields(emailconfig, false, true);

		synchronized(psSetConfigurationLock)
		{
			if(log.isDebugEnabled())
				log.debug("Setting the email server configuration.");

			try
			{
				// If the PreparedStatement to update the email configuration was not defined, create it
				if(psSetConfiguration == null)
				{
					// SQL to update new row
					String updateSql = "UPDATE " + TABLE_NAME + " SET " + COL_EMAIL_SERVER_ADDRESS + "=?, " +
	                                                                      COL_PORT_NUMBER + "=?, " +
				                                                          COL_FROM_ADDRESS + "=?, " +
				                                                          COL_PASSWORD + "=?, " +
				                                                          COL_ENCRYPTED_CONNECTION + "=?, " +
				                                                          COL_TIMEOUT + "=?, " +
				                                                          COL_FORGOTTEN_PASSWORD_LINK + "=?";

					if(log.isDebugEnabled())
						log.debug("Creating the \"update email server config\" PreparedStatement from the SQL " + updateSql);

					// A prepared statement to run the update SQL
					// This should sanitize the SQL and prevent SQL injection
					psSetConfiguration = dbConnection.prepareStatement(updateSql);
				} /// end if(update PreparedStatement not defined)

				// Set the parameters on the update statement
				psSetConfiguration.setString(1, emailconfig.getEmailServerAddress());
				psSetConfiguration.setInt(2, emailconfig.getPortNumber());
				psSetConfiguration.setString(3, emailconfig.getFromAddress());
				psSetConfiguration.setString(4, emailconfig.getPassword());
				psSetConfiguration.setString(5, emailconfig.getEncryptedConnection());
				psSetConfiguration.setLong(6, emailconfig.getTimeout());
				psSetConfiguration.setBoolean(7, emailconfig.getForgottenPasswordLink());

				// Execute the update statement.  If nothing was updated we need to insert instead of update
				if(psSetConfiguration.executeUpdate() == 0)
				{
					String insertSql = "INSERT INTO " + TABLE_NAME + " (" + COL_EMAIL_SERVER_ADDRESS + ", " +
					                                                        COL_PORT_NUMBER + ", " +
					                                                        COL_FROM_ADDRESS + ", " +
					                                                        COL_PASSWORD + ", " +
					                                                        COL_ENCRYPTED_CONNECTION + ", " +
					                                                        COL_TIMEOUT + ", " +
					                                                        COL_FORGOTTEN_PASSWORD_LINK + ") " +
                                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
					
					PreparedStatement psInsert = dbConnection.prepareStatement(insertSql);
					
					// Set the parameters on the insert statement
					psInsert.setString(1, emailconfig.getEmailServerAddress());
					psInsert.setInt(2, emailconfig.getPortNumber());
					psInsert.setString(3, emailconfig.getFromAddress());
					psInsert.setString(4, emailconfig.getPassword());
					psInsert.setString(5, emailconfig.getEncryptedConnection());
					psInsert.setLong(6, emailconfig.getTimeout());
					psInsert.setBoolean(7, emailconfig.getForgottenPasswordLink());
					
					return psInsert.executeUpdate() > 0;
				}
				
				return false;
			} // end try(set the email configuration)
			catch(SQLException e)
			{
				log.error("A SQLException occurred while updating the email server configuration.", e);

				return false;
			} // end catch(SQLException)
		} // end synchronized
	} // end method setConfiguration(EmailConfig)
} // end DefaultEmailConfigDAO class
