/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.constants.Constants;
import xc.mst.utils.MSTConfiguration;

/**
 * Base class for all database data access objects in the MST.  Contains methods for
 * maintaining the one static Connection Object used by the MST.
 *
 * @author Eric Osisek
 */
public class MySqlConnectionManager
{
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit.
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The Connection to the database
	 */
	private static Connection dbConnection = null;

	/**
	 * Sets up and returns a connection to the MST database whose location is
	 * defined in the configuration file.
	 */
	public static Connection getDbConnection()
	{
		if(log.isDebugEnabled())
			log.debug("Entering getDbConnection()");

		// If there is an open connection, return it,
		// otherwise open a new connection and return the new one
        return (dbConnection != null ? dbConnection : openDbConnection());

		/*try
		{
			return ((dbConnection != null && dbConnection.isValid(10)) ? dbConnection : openDbConnection());
		}
		catch(Exception e) // If something went wrong verifying the connection was valid, open a new one
		{
			log.error("Error validating the connection to the database, returning a new connection.", e);

			return openDbConnection();
		}*/
	} // end method getDbConnection()

	private static Connection openDbConnection()
	{
		if(log.isDebugEnabled())
			log.debug("Entering openDbConnection()");

	    try
	    {
	        // Load the JDBC driver for MySQL
	        Class.forName("com.mysql.jdbc.Driver");

	        // Get the URL, username and password to log into the database from the configuration file
	        String url = MSTConfiguration.getProperty(Constants.CONFIG_DATABASE_URL);
	        String username = MSTConfiguration.getProperty(Constants.CONFIG_DATABASE_USERNAME);
	        String password = MSTConfiguration.getProperty(Constants.CONFIG_DATABASE_PASSWORD);

	        if(log.isDebugEnabled())
				log.debug("Building a connection to the database at " + url + " with the username " + username);

	        // Create a connection to the database
	        dbConnection = DriverManager.getConnection(url, username, password);
	        return dbConnection;
	    } // end try (open and return connection)
	    catch (ClassNotFoundException e) // Could not find the database driver
	    {
	        log.warn("Could not find the MySQL database driver.", e);

	        return null;
	    } // end catch(ClassNotFoundException)
	    catch (SQLException e) // Could not connect to the database
	    {
	    	log.warn("Could not connect to the database specified in the configuration file.", e);

	        return null;
	    } // end catch(SQLException)
	    catch(UnsatisfiedLinkError e) // Something was wrong with the URL
	    {
	    	log.warn("Could not connect to the database specified in the configuration file.", e);

	        return null;
	    } // end catch(UnsatisfiedLinkError)
        catch(Exception e) //any other error
        {
            log.error("An Exception occurred while connecting to the database.", e);

            return null;
        }
	} // end method openDbConnection()

	/**
	 * Closes the connection to the database
	 *
	 * @return true on success, false on failure
	 */
	public static boolean closeDbConnection()
	{
		try
		{
			if(dbConnection != null)
			{
				dbConnection.close();

				// Set dbConnection to null so future getDbConnection calls will know to create a new connection
				dbConnection = null;
			} // end if (dbConnection isn't null

			return true;
		} // end try (close the connection to the database)
		catch(SQLException e)
		{
			log.error("An error occurred while closing the connection to the database.", e);
			return false;
		} // end catch(SQLException)
	} // end method closeDbConnection()

	/**
	 * Closes a ResultSet and logs any errors which occurred
	 *
	 * @param results The result set to close
	 * @return true on success, false on failure
	 */
	public static boolean closeResultSet(ResultSet results)
	{
		try
		{
			if(results != null)
				results.close();

			return true;
		} // end try(close the ResultSet)
		catch(SQLException e)
		{
			log.error("An error occurred while closing the results set.", e);
			return false;
		} // end catch(SQLException)
	} // end method closeResultSet(ResultSet)
} // end class DatabaseDAO
