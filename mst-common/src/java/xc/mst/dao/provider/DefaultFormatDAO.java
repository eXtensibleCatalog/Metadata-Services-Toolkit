/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xc.mst.bo.provider.Format;
import xc.mst.dao.DBConnectionResetException;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * MySQL implementation of the data access object for the formats table
 *
 * @author Eric Osisek
 */
public class DefaultFormatDAO extends FormatDAO
{

    /**
     * A PreparedStatement to get all formats in the database
     */
    private static PreparedStatement psGetAll = null;

    /**
     * A PreparedStatement to get a format from the database by its ID
     */
    private static PreparedStatement psGetById = null;

    /**
     * A PreparedStatement to get a format from the database by its name
     */
    private static PreparedStatement psGetByName = null;

    /**
     * A PreparedStatement to insert a format into the database
     */
    private static PreparedStatement psInsert = null;

    /**
     * A PreparedStatement to update a format in the database
     */
    private static PreparedStatement psUpdate = null;

    /**
     * A PreparedStatement to delete a format from the database
     */
    private static PreparedStatement psDelete = null;

    /**
     * Lock to synchronize access to the PreparedStatement to get all formats in the database
     */
    private static Object psGetAllLock = new Object();

    /**
     * Lock to synchronize access to the PreparedStatement to get a formats from the database by ID
     */
    private static Object psGetByIdLock = new Object();

    /**
     * Lock to synchronize access to the PreparedStatement to get a format from the database by name
     */
    private static Object psGetByNameLock = new Object();

    /**
     * Lock to synchronize access to the PreparedStatement insert a format into the database
     */
    private static Object psInsertLock = new Object();

    /**
     * Lock to synchronize access to the PreparedStatement to update a format in the database
     */
    private static Object psUpdateLock = new Object();

    /**
     * Lock to synchronize access to the PreparedStatement to delete a format from the database
     */
    private static Object psDeleteLock = new Object();

    protected Map<Integer, Format> cacheById = new HashMap<Integer, Format>();

    @Override
    public List<Format> getAll() throws DatabaseConfigException
    {
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized(psGetAllLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting all formats");

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of all formats
            List<Format> formats = new ArrayList<Format>();

            try
            {
                // Create the PreparedStatment to get all formats if it hasn't already been created
                if(psGetAll == null || dbConnectionManager.isClosed(psGetAll))
                {
                    // SQL to get the rows
                    String selectSql = "SELECT " + COL_FORMAT_ID + ", " +
                                                   COL_NAME + ", " +
                                                   COL_NAMESPACE + ", " +
                                                   COL_SCHEMA_LOCATION + " " +
                                       "FROM " + FORMATS_TABLE_NAME;

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get all formats\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
                } // end if(get all PreparedStatement not defined)

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetAll);

                // For each result returned, add a Format object to the list with the returned data
                while(results.next())
                {
                    // The Object which will contain data on the format
                    Format format = new Format();

                    // Set the fields on the format
                    format.setId(results.getInt(1));
                    format.setName(results.getString(2));
                    format.setNamespace(results.getString(3));
                    format.setSchemaLocation(results.getString(4));

                    // Add the format to the list
                    formats.add(format);
                } // end loop over results

                if(log.isDebugEnabled())
                    log.debug("Found " + formats.size() + " formats in the database.");

                return formats;
            } // end try(get the formats)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the formats.", e);

                return formats;
            } // end catch(SQLExeption)
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
    public Format getById(int formatId) throws DatabaseConfigException
    {
        if (cacheById.containsKey(formatId)) {
            return cacheById.get(formatId);
        }
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized(psGetByIdLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the format with ID " + formatId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            try
            {
                // Create the PreparedStatment to get a format by ID if it hasn't already been created
                if(psGetById == null || dbConnectionManager.isClosed(psGetById))
                {
                    // SQL to get the row
                    String selectSql = "SELECT " + COL_FORMAT_ID + ", " +
                                                   COL_NAME + ", " +
                                                   COL_NAMESPACE + ", " +
                                                   COL_SCHEMA_LOCATION + " " +
                                       "FROM " + FORMATS_TABLE_NAME + " " +
                                       "WHERE " + COL_FORMAT_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get format by ID\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetById = dbConnectionManager.prepareStatement(selectSql, psGetById);
                } // end if(get by ID PreparedStatement not defined)

                // Set the parameters on the update statement
                psGetById.setInt(1, formatId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetById);

                // If any results were returned
                if(results.next())
                {
                    // The Object which will contain data on the format
                    Format format = new Format();

                    // Set the fields on the format
                    format.setId(results.getInt(1));
                    format.setName(results.getString(2));
                    format.setNamespace(results.getString(3));
                    format.setSchemaLocation(results.getString(4));

                    if(log.isDebugEnabled())
                        log.debug("Found the format with ID " + formatId + " in the database.");

                    // Return the format
                    cacheById.put(format.getId(), format);
                    return format;
                } // end if(result found)

                if(log.isDebugEnabled())
                    log.debug("The format with ID " + formatId + " was not found in the database.");

                return null;
            } // end try(get the format by ID)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the format with ID " + formatId, e);

                return null;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getById(formatId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally(close ResultSet)
        } // end synchronized
    } // end method getById(int)

    @Override
    public Format getByName(String name) throws DatabaseConfigException
    {
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized(psGetByNameLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the format with name " + name);

            // The ResultSet from the SQL query
            ResultSet results = null;

            try
            {
                // Create the PreparedStatment to get a format by ID if it hasn't already been created
                if(psGetByName == null || dbConnectionManager.isClosed(psGetByName))
                {
                    // SQL to get the row
                    String selectSql = "SELECT " + COL_FORMAT_ID + ", " +
                                                   COL_NAME + ", " +
                                                   COL_NAMESPACE + ", " +
                                                   COL_SCHEMA_LOCATION + " " +
                                       "FROM " + FORMATS_TABLE_NAME + " " +
                                       "WHERE " + COL_NAME + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get format by name\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetByName = dbConnectionManager.prepareStatement(selectSql, psGetByName);
                } // end if(get by name PreparedStatement not defined)

                // Set the parameters on the update statement
                psGetByName.setString(1, name);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetByName);

                // If any results were returned
                if(results.next())
                {
                    // The Object which will contain data on the format
                    Format format = new Format();

                    // Set the fields on the format
                    format.setId(results.getInt(1));
                    format.setName(results.getString(2));
                    format.setNamespace(results.getString(3));
                    format.setSchemaLocation(results.getString(4));

                    if(log.isDebugEnabled())
                        log.debug("Found the format with name " + name + " in the database.");

                    // Return the format
                    return format;
                } // end if(result found)

                if(log.isDebugEnabled())
                    log.debug("The format with name " + name + " was not found in the database.");

                return null;
            } // end try(get the format by its name)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the format with name " + name, e);

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
    public List<Format> getFormatsForProvider(int providerId) throws DatabaseConfigException
    {
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        List<Format> formats = new ArrayList<Format>();

        for(Integer formatId : getProviderFormatUtilDAO().getFormatsForProvider(providerId))
            formats.add(getById(formatId.intValue()));

        return formats;
    } // end method getFormatsForProvider(int)

    @Override
    public boolean insert(Format format) throws DataException
    {
        cacheById.clear();
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        // Check that the non-ID fields on the format are valid
        validateFields(format, false, true);

        synchronized(psInsertLock)
        {
            if(log.isDebugEnabled())
                log.debug("Inserting a new format with the name " + format.getName());

            // The result set returned by the query
            ResultSet rs = null;

            try
            {
                // Build the PreparedStatement to insert a format if it wasn't already created
                if(psInsert == null || dbConnectionManager.isClosed(psInsert))
                {
                    // SQL to insert the new row
                    String insertSql = "INSERT INTO " + FORMATS_TABLE_NAME + " (" + COL_NAME + ", " +
                                                                                      COL_NAMESPACE + ", " +
                                                                                      COL_SCHEMA_LOCATION + ") " +
                                       "VALUES (?, ?, ?)";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"insert format\" PreparedStatemnt from the SQL " + insertSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
                } // end if(insert PreparedStatement not defined)

                // Set the parameters on the insert statement
                psInsert.setString(1, format.getName());
                psInsert.setString(2, format.getNamespace());
                psInsert.setString(3, format.getSchemaLocation());

                // Execute the insert statement and return the result
                if(dbConnectionManager.executeUpdate(psInsert) > 0)
                {
                    // Get the auto-generated resource identifier ID and set it correctly on this Format Object
                    rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

                    if (rs.next())
                        format.setId(rs.getInt(1));

                    return true;
                } // end if(insert succeeded)
                else
                    return false;
            } // end try(insert the format)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while inserting a new format with the name " + format.getName(), e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return insert(format);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally(close the ResultSet)
        } // end synchronized
    } // end insert(Format)

    @Override
    public boolean update(Format format) throws DataException
    {
        cacheById.clear();
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        // Check that the fields on the format are valid
        validateFields(format, true, true);

        synchronized(psUpdateLock)
        {
            if(log.isDebugEnabled())
                log.debug("Updating the format with ID " + format.getId());

            try
            {
                // Create a PreparedStatement to update a format if it wasn't already created
                if(psUpdate == null || dbConnectionManager.isClosed(psUpdate))
                {
                    // SQL to update new row
                    String updateSql = "UPDATE " + FORMATS_TABLE_NAME + " SET " + COL_NAME + "=?, " +
                                                                          COL_NAMESPACE + "=?, " +
                                                                          COL_SCHEMA_LOCATION + "=? " +
                                       "WHERE " + COL_FORMAT_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"update format\" PreparedStatement from the SQL " + updateSql);

                    // A prepared statement to run the update SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
                } // end if(update PreparedStatement not defined)

                // Set the parameters on the update statement
                psUpdate.setString(1, format.getName());
                psUpdate.setString(2, format.getNamespace());
                psUpdate.setString(3, format.getSchemaLocation());
                psUpdate.setInt(4, format.getId());

                // Execute the update statement and return the result
                return dbConnectionManager.executeUpdate(psUpdate) > 0;
            } // end try(update the format)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while updating the format with ID " + format.getId(), e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return update(format);
            }
        } // end synchronized
    } // end update(Format)

    @Override
    public boolean delete(Format format) throws DataException
    {
        cacheById.clear();
        // Throw an exception if the connection is null.  This means the configuration file was bad.
        if(dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        // Check that the ID field on the format are valid
        validateFields(format, true, false);

        synchronized(psDeleteLock)
        {
            if(log.isDebugEnabled())
                log.debug("Deleting the format with ID " + format.getId());

            try
            {
                // Create the PreparedStatement to delete a format if it wasn't already defined
                if(psDelete == null || dbConnectionManager.isClosed(psDelete))
                {
                    // SQL to delete the row from the table
                    String deleteSql = "DELETE FROM "+ FORMATS_TABLE_NAME + " " +
                                       "WHERE " + COL_FORMAT_ID + " = ? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"delete format\" PreparedStatement the SQL " + deleteSql);

                    // A prepared statement to run the delete SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
                } // end if(delete PreparedStatement not defined)

                // Set the parameters on the delete statement
                psDelete.setInt(1, format.getId());

                // Execute the delete statement and return the result
                return dbConnectionManager.execute(psDelete);
            } // end try(delete the row)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while deleting the format with ID " + format.getId(), e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return delete(format);
            }
        } // end synchronized
    } // end method delete(Format)
} // end class DefaultFormatDAO
