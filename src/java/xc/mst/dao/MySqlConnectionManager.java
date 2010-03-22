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
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import java.sql.PreparedStatement;

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
	 * The singleton MySqlConnectionManager
	 */
	private static MySqlConnectionManager instance = null;

	/**
	 * The Connection to the database
	 */
	private Connection dbConnection = null;

	/**
	 * A set containing all PreparedStatements known to the connection manager.  If
	 * connection to the database is lost each of these is set to null to signal the
	 * DAOs.  If the connection is later restored the DAOs will know to recreate the
	 * PreparedStatements.
	 */
	private Set<PreparedStatement> registeredPreparedStatements = new HashSet<PreparedStatement>();

	/**
	 * A set containing all PreparedStatements known to the connection manager that
	 * have been closed.
	 */
	private Set<PreparedStatement> closedPreparedStatements = new HashSet<PreparedStatement>();

	/**
	 * Constructor for MySqlConnectionManager
	 */
	private MySqlConnectionManager()
	{
	} // end constructor

	/**
	 * Gets the singleton instance of the MySqlConnectionManager
	 *
	 * @return The singleton MySqlConnectionManager
	 */
	public static MySqlConnectionManager getInstance()
	{
		if(instance == null)
			instance = new MySqlConnectionManager();

		return instance;
	} // end method getInstance()

	/**
	 * Sets up and returns a connection to the MST database whose location is
	 * defined in the configuration file.
	 */
	public Connection getDbConnection()
	{

		// If there is an open connection, return it,
		// otherwise open a new connection and return the new one
        return (dbConnection != null ? dbConnection : openDbConnection());
	} // end method getDbConnection()

	private Connection openDbConnection()
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
	public boolean closeDbConnection()
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
	 * Registers a PreparedStatement to the connection manager.  If the connection to the database
	 * is lost the registered PreparedStatements will be closed and set to null so code trying to
	 * reuse them will know they are no longer valid.
	 *
	 * @param statement The PreparedStatement to register
	 */
	public void registerStatement(PreparedStatement statement)
	{
		registeredPreparedStatements.add(statement);
	} // end method registerStatement(PreparedStatement)

	/**
	 * Unregisters a PreparedStatement from the connection manager.
	 *
	 * @param statement The PreparedStatement to unregister
	 */
	public void unregisterStatement(PreparedStatement statement)
	{
		registeredPreparedStatements.remove(statement);
		closedPreparedStatements.remove(statement);
	} // end method registerStatement(PreparedStatement)

	/**
	 * Returns true if the passed statement is registered and has been closed
	 *
	 * @param statement The statement to check
	 * @return True if the passed statement is registered and has been closed
	 */
	public boolean isClosed(PreparedStatement statement)
	{
		return closedPreparedStatements.contains(statement);
	}

	/**
	 * Closes a ResultSet and logs any errors which occurred
	 *
	 * @param results The result set to close
	 * @return true on success, false on failure
	 */
	public boolean closeResultSet(ResultSet results)
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

	/**
	 * Creates and registers a PreparedStatement based on the SQL query.
	 *
	 * @param sql The SQL for the PreparedStatement to create
	 * @param replaces The PreparedStatement Object that is being replaced, or null if nothing is replaced
	 * @return The PreparedStatement for the passed sql string
	 * @throws SQLException If the query could not be created
	 */
	public PreparedStatement prepareStatement(String sql, PreparedStatement replaces) throws SQLException
	{
		try
		{
			PreparedStatement result = dbConnection.prepareStatement(sql);
			registerStatement(result);
			unregisterStatement(replaces);
			return result;
		}
		catch(SQLException e)
		{
			resetConnection();
			PreparedStatement result = dbConnection.prepareStatement(sql);
			registerStatement(result);
			unregisterStatement(replaces);
			return result;
		}
	}

	/**
	 * Creates a Statement on the database connection
	 *
	 * @return The Statement created for the database connection
	 * @throws SQLException If the Statement could not be created
	 */
	public Statement createStatement() throws SQLException
	{
		try
		{
			return dbConnection.createStatement();
		}
		catch(SQLException e)
		{
			resetConnection();
			throw e;
		}
	}

	/**
	 * Runs a query against the database.  If it fails, attempts to reconnect to
	 * the database.
	 *
	 * @param query The query to run
	 * @return The result of running the query
	 * @throws SQLException If the query failed twice
	 * @throws DBConnectionResetException
	 */
	public ResultSet executeQuery(PreparedStatement query) throws SQLException, DBConnectionResetException
	{
		// TODO: Should this throw an Exception?  They're running a query we don't manage, so
		//       we can't reset it on a failure.
		if(!registeredPreparedStatements.contains(query))
			log.warn("Running a query that was not registered: " + query);

		try
		{
			return query.executeQuery();
		}
		catch(SQLException e)
		{
			// Possibly the connection timed out. Hence try a reconnect to DB.
			//If reconnect fails then don't re executed query
			resetConnection();

			// Propagate the connection reset so that DAO's will re-execute
			throw new DBConnectionResetException();
		}
	}

	/**
	 * Runs a query against the database.  If it fails, attempts to reconnect to
	 * the database.
	 *
	 * @param query The query to run
	 * @return The result of running the query
	 * @throws SQLException If the query failed twice
	 * @throws DBConnectionResetException
	 */
	public int executeUpdate(PreparedStatement query) throws SQLException, DBConnectionResetException
	{
		// TODO: Should this throw an Exception?  They're running a query we don't manage, so
		//       we can't reset it on a failure.
		if(!registeredPreparedStatements.contains(query))
			log.warn("Running a query that was not registered: " + query);

		try
		{
			return query.executeUpdate();
		}
		catch(SQLException e)
		{
			// Possibly the connection timed out. Hence try a reconnect to DB.
			//If reconnect fails then don't re executed query
			resetConnection();

			// Propagate the connection reset so that DAO's will re-execute
			throw new DBConnectionResetException();
		}
	}

	/**
	 * Runs a query against the database.  If it fails, attempts to reconnect to
	 * the database.
	 *
	 * @param query The query to run
	 * @return The result of running the query
	 * @throws SQLException If the query failed twice
	 * @throws DBConnectionResetException
	 */
	public boolean execute(PreparedStatement query) throws SQLException, DBConnectionResetException
	{
		// TODO: Should this throw an Exception?  They're running a query we don't manage, so
		//       we can't reset it on a failure.
		if(!registeredPreparedStatements.contains(query))
			log.warn("Running a query that was not registered: " + query);

		try
		{
			return query.execute();
		}
		catch(SQLException e)
		{
			// Possibly the connection timed out. Hence try a reconnect to DB.
			//If reconnect fails then don't re executed query
			resetConnection();

			// Propagate the connection reset so that DAO's will re-execute
			throw new DBConnectionResetException();
		}
	}

	/**
	 * Attempts to reset the connection to the database
	 *
	 * @throws SQLException If the connection could not be re-established
	 */
	private void resetConnection() throws SQLException
	{
		// Expire the database connection
		try
		{
			if(!dbConnection.isClosed())
				dbConnection.close();
		}
		catch(SQLException e)
		{
			log.error("Error trying to close the database connection.");
		}
		finally
		{
			// Reopen the connection
			openDbConnection();
		}

		// Expire the registered PreparedStatements
		for(PreparedStatement statement : registeredPreparedStatements)
		{
			try
			{
				if(!closedPreparedStatements.contains(statement))
				{
					statement.close();
					closedPreparedStatements.add(statement);
				}
			}
			catch(SQLException e)
			{
				log.error("Error trying to close a PreparedStatement.");
			}
		}

		registeredPreparedStatements.clear();
	}
} // end class MySqlConnectionManager
