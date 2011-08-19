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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordIfc;
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
public class DefaultProviderDAO extends ProviderDAO {

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

    public void init() {
        super.init();

        try {
            logObj = getLogDAO().getById(Constants.LOG_ID_REPOSITORY_MANAGEMENT);
        } catch (DatabaseConfigException e) {
            log.error("Could not get the log file location for the repository management log.", e);
        }
    }

    @Override
    public List<Provider> getAll() throws DatabaseConfigException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized (psGetAllLock) {
            if (log.isDebugEnabled())
                log.debug("Getting all providers");

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of all providers
            List<Provider> providers = new ArrayList<Provider>();

            try {
                // If the PreparedStatement to get all providers was not defined, create it
                if (psGetAll == null || dbConnectionManager.isClosed(psGetAll)) {
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
                                                   COL_GRANULARITY + ", " +
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

                    if (log.isDebugEnabled())
                        log.debug("Creating the \"get all providers\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetAll = dbConnectionManager.prepareStatement(selectSql, psGetAll);
                } // end if(get all PreparedStatement not defined)

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetAll);

                // For each result returned, add a Provider object to the list with the returned data
                while (results.next()) {
                    // The Object which will contain data on the provider
                    Provider provider = new Provider();

                    int i = 1;
                    // Set the fields on the provider
                    provider.setId(results.getInt(i++));
                    provider.setCreatedAt(results.getDate(i++));
                    provider.setUpdatedAt(results.getTimestamp(i++));
                    provider.setName(results.getString(i++));
                    provider.setOaiProviderUrl(results.getString(i++));
                    provider.setTitle(results.getString(i++));
                    provider.setCreator(results.getString(i++));
                    provider.setSubject(results.getString(i++));
                    provider.setDescription(results.getString(i++));
                    provider.setPublisher(results.getString(i++));
                    provider.setContributors(results.getString(i++));
                    provider.setDate(results.getDate(i++));
                    provider.setType(results.getString(i++));
                    provider.setFormat(results.getString(i++));
                    provider.setIdentifier(results.getInt(i++));
                    provider.setLanguage(results.getString(i++));
                    provider.setRelation(results.getString(i++));
                    provider.setCoverage(results.getString(i++));
                    provider.setRights(results.getString(i++));
                    provider.setService(results.getBoolean(i++));
                    provider.setNextListSetsListFormats(results.getDate(i++));
                    provider.setProtocolVersion(results.getString(i++));
                    provider.setLastValidationDate(results.getDate(i++));
                    provider.setIdentify(results.getBoolean(i++));
                    provider.setGranularity(results.getString(i++));
                    provider.setListFormats(results.getBoolean(i++));
                    provider.setListSets(results.getBoolean(i++));
                    provider.setWarnings(results.getInt(i++));
                    provider.setErrors(results.getInt(i++));
                    provider.setRecordsAdded(results.getInt(i++));
                    provider.setRecordsReplaced(results.getInt(i++));
                    provider.setLastOaiRequest(results.getString(i++));
                    Timestamp ts = results.getTimestamp(i++);
                    if (ts != null) {
                        provider.setLastHarvestEndTime(new java.util.Date(ts.getTime()));
                    }
                    provider.setLastLogReset(results.getDate(i++));
                    provider.setLogFileName(results.getString(i++));
                    provider.setNumberOfRecordsToHarvest(results.getLong(i++));

                    provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));

                    provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
                    provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));

                    // Add the provider to the list
                    providers.add(provider);
                } // end loop over results

                if (log.isDebugEnabled())
                    log.debug("Found " + providers.size() + " providers in the database.");

                return providers;
            } // end try(get the providers)
            catch (SQLException e) {
                log.error("A SQLException occurred while getting the providers.", e);

                return providers;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return getAll();
            } finally {
                dbConnectionManager.closeResultSet(results);
            } // end finally(close ResultSet)
        } // end synchronized
    } // end method getAll()

    @Override
    public List<Provider> getSorted(boolean asc, String columnName) throws DatabaseConfigException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        if (log.isDebugEnabled())
            log.debug("Getting all providers sorted in " + (asc ? "ascending" : "descending") + " order on the column " + columnName);

        // Validate the column we're trying to sort on
        if (!sortableColumns.contains(columnName)) {
            log.error("An attempt was made to sort on the invalid column " + columnName);
            return getAll();
        } // end if(sort column invalid)

        // The ResultSet from the SQL query
        ResultSet results = null;

        // The Statement for getting the rows
        Statement getSorted = null;

        // The list of all providers
        List<Provider> providers = new ArrayList<Provider>();

        try {
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
                                           COL_GRANULARITY + ", " +
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

            if (log.isDebugEnabled())
                log.debug("Creating the \"get all providers sorted\" PreparedStatement from the SQL " + selectSql);

            // A statement to run the select SQL
            getSorted = dbConnectionManager.createStatement();

            // Get the results of the SELECT statement

            // Execute the query
            results = getSorted.executeQuery(selectSql);

            // For each result returned, add a Provider object to the list with the returned data
            while (results.next()) {
                // The Object which will contain data on the provider
                Provider provider = new Provider();

                int i = 1;
                // Set the fields on the provider
                provider.setId(results.getInt(i++));
                provider.setCreatedAt(results.getDate(i++));
                provider.setUpdatedAt(results.getTimestamp(i++));
                provider.setName(results.getString(i++));
                provider.setOaiProviderUrl(results.getString(i++));
                provider.setTitle(results.getString(i++));
                provider.setCreator(results.getString(i++));
                provider.setSubject(results.getString(i++));
                provider.setDescription(results.getString(i++));
                provider.setPublisher(results.getString(i++));
                provider.setContributors(results.getString(i++));
                provider.setDate(results.getDate(i++));
                provider.setType(results.getString(i++));
                provider.setFormat(results.getString(i++));
                provider.setIdentifier(results.getInt(i++));
                provider.setLanguage(results.getString(i++));
                provider.setRelation(results.getString(i++));
                provider.setCoverage(results.getString(i++));
                provider.setRights(results.getString(i++));
                provider.setService(results.getBoolean(i++));
                provider.setNextListSetsListFormats(results.getDate(i++));
                provider.setProtocolVersion(results.getString(i++));
                provider.setLastValidationDate(results.getDate(i++));
                provider.setIdentify(results.getBoolean(i++));
                provider.setGranularity(results.getString(i++));
                provider.setListFormats(results.getBoolean(i++));
                provider.setListSets(results.getBoolean(i++));
                provider.setWarnings(results.getInt(i++));
                provider.setErrors(results.getInt(i++));
                provider.setRecordsAdded(results.getInt(i++));
                provider.setRecordsReplaced(results.getInt(i++));
                provider.setLastOaiRequest(results.getString(i++));
                Timestamp ts = results.getTimestamp(i++);
                if (ts != null) {
                    provider.setLastHarvestEndTime(new java.util.Date(ts.getTime()));
                }
                provider.setLastLogReset(results.getDate(i++));
                provider.setLogFileName(results.getString(i++));
                provider.setNumberOfRecordsToHarvest(results.getLong(i++));

                provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));

                provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
                provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));

                // Add the provider to the list
                providers.add(provider);
            } // end loop over results

            if (log.isDebugEnabled())
                log.debug("Found " + providers.size() + " providers in the database.");

            return providers;
        } // end try(get the providers)
        catch (SQLException e) {
            log.error("A SQLException occurred while getting the providers sorted by their name in ascending order.", e);

            return providers;
        } // end catch(SQLException)
        finally {
            dbConnectionManager.closeResultSet(results);

            try {
                if (getSorted != null)
                    getSorted.close();
            } // end try(close the Statement)
            catch (SQLException e) {
                log.error("An error occurred while trying to close the \"get processing directives sorted\" Statement");
            } // end catch(SQLException)
        } // end finally(close ResultSet)
    } // end method getSorted(boolean, String)

    @Override
    public Provider getById(int providerId) throws DatabaseConfigException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        Provider provider = loadBasicProvider(providerId);

        // If we found the provider, set up its sets and formats
        if (provider != null) {
            provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
            provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));
            provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));
        } // end if(provider found)

        return provider;
    } // end method getById(int)

    @Override
    public Provider getByURL(String providerURL) throws DatabaseConfigException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized (psGetByUrlLock) {
            if (log.isDebugEnabled())
                log.debug("Getting the provider with URL " + providerURL);

            // The ResultSet from the SQL query
            ResultSet results = null;

            try {
                // If the PreparedStatement to get a provider by URL was not defined, create it
                if (psGetByUrl == null || dbConnectionManager.isClosed(psGetByUrl)) {
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
                                                   COL_GRANULARITY + ", " +
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

                    if (log.isDebugEnabled())
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
                if (results.next()) {
                    // The Object which will contain data on the provider
                    Provider provider = new Provider();

                    int i = 1;
                    // Set the fields on the provider
                    provider.setId(results.getInt(i++));
                    provider.setCreatedAt(results.getDate(i++));
                    provider.setUpdatedAt(results.getTimestamp(i++));
                    provider.setName(results.getString(i++));
                    provider.setOaiProviderUrl(results.getString(i++));
                    provider.setTitle(results.getString(i++));
                    provider.setCreator(results.getString(i++));
                    provider.setSubject(results.getString(i++));
                    provider.setDescription(results.getString(i++));
                    provider.setPublisher(results.getString(i++));
                    provider.setContributors(results.getString(i++));
                    provider.setDate(results.getDate(i++));
                    provider.setType(results.getString(i++));
                    provider.setFormat(results.getString(i++));
                    provider.setIdentifier(results.getInt(i++));
                    provider.setLanguage(results.getString(i++));
                    provider.setRelation(results.getString(i++));
                    provider.setCoverage(results.getString(i++));
                    provider.setRights(results.getString(i++));
                    provider.setService(results.getBoolean(i++));
                    provider.setNextListSetsListFormats(results.getDate(i++));
                    provider.setProtocolVersion(results.getString(i++));
                    provider.setLastValidationDate(results.getDate(i++));
                    provider.setIdentify(results.getBoolean(i++));
                    provider.setGranularity(results.getString(i++));
                    provider.setListFormats(results.getBoolean(i++));
                    provider.setListSets(results.getBoolean(i++));
                    provider.setWarnings(results.getInt(i++));
                    provider.setErrors(results.getInt(i++));
                    provider.setRecordsAdded(results.getInt(i++));
                    provider.setRecordsReplaced(results.getInt(i++));
                    provider.setLastOaiRequest(results.getString(i++));
                    Timestamp ts = results.getTimestamp(i++);
                    if (ts != null) {
                        provider.setLastHarvestEndTime(new java.util.Date(ts.getTime()));
                    }
                    provider.setLastLogReset(results.getDate(i++));
                    provider.setLogFileName(results.getString(i++));
                    provider.setNumberOfRecordsToHarvest(results.getLong(i++));

                    provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
                    provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));
                    provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));
                    if (log.isDebugEnabled())
                        log.debug("Found the provider with URL " + providerURL + " in the database.");

                    // Return the provider
                    return provider;
                } // end if(provider found)

                if (log.isDebugEnabled())
                    log.debug("The provider with URL " + providerURL + " was not found in the database.");

                return null;
            } // end synchronized
            catch (SQLException e) {
                log.error("A SQLException occurred while getting the provider with URL " + providerURL, e);

                return null;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return getByURL(providerURL);
            } finally {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        } // end synchronized
    } // end method getByURL(String)

    @Override
    public Provider getByName(String name) throws DatabaseConfigException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized (psGetByNameLock) {
            if (log.isDebugEnabled())
                log.debug("Getting the provider with the name " + name);

            // The ResultSet from the SQL query
            ResultSet results = null;

            try {
                // If the PreparedStatement to get a provider by ID was not defined, create it
                if (psGetByName == null || dbConnectionManager.isClosed(psGetByName)) {
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
                                                   COL_GRANULARITY + ", " +
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

                    if (log.isDebugEnabled())
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
                if (results.next()) {
                    // The Object which will contain data on the provider
                    Provider provider = new Provider();

                    int i = 1;
                    // Set the fields on the provider
                    provider.setId(results.getInt(i++));
                    provider.setCreatedAt(results.getDate(i++));
                    provider.setUpdatedAt(results.getTimestamp(i++));
                    provider.setName(results.getString(i++));
                    provider.setOaiProviderUrl(results.getString(i++));
                    provider.setTitle(results.getString(i++));
                    provider.setCreator(results.getString(i++));
                    provider.setSubject(results.getString(i++));
                    provider.setDescription(results.getString(i++));
                    provider.setPublisher(results.getString(i++));
                    provider.setContributors(results.getString(i++));
                    provider.setDate(results.getDate(i++));
                    provider.setType(results.getString(i++));
                    provider.setFormat(results.getString(i++));
                    provider.setIdentifier(results.getInt(i++));
                    provider.setLanguage(results.getString(i++));
                    provider.setRelation(results.getString(i++));
                    provider.setCoverage(results.getString(i++));
                    provider.setRights(results.getString(i++));
                    provider.setService(results.getBoolean(i++));
                    provider.setNextListSetsListFormats(results.getDate(i++));
                    provider.setProtocolVersion(results.getString(i++));
                    provider.setLastValidationDate(results.getDate(i++));
                    provider.setIdentify(results.getBoolean(i++));
                    provider.setGranularity(results.getString(i++));
                    provider.setListFormats(results.getBoolean(i++));
                    provider.setListSets(results.getBoolean(i++));
                    provider.setWarnings(results.getInt(i++));
                    provider.setErrors(results.getInt(i++));
                    provider.setRecordsAdded(results.getInt(i++));
                    provider.setRecordsReplaced(results.getInt(i++));
                    provider.setLastOaiRequest(results.getString(i++));
                    Timestamp ts = results.getTimestamp(i++);
                    if (ts != null) {
                        provider.setLastHarvestEndTime(new java.util.Date(ts.getTime()));
                    }
                    provider.setLastLogReset(results.getDate(i++));
                    provider.setLogFileName(results.getString(i++));
                    provider.setNumberOfRecordsToHarvest(results.getLong(i++));

                    provider.setFormats(getFormatDAO().getFormatsForProvider(provider.getId()));
                    provider.setSets(getSetDAO().getSetsForProvider(provider.getId()));
                    provider.setHarvestedRecordSets(getSetDAO().getRecordSetsForProvider(provider.getId()));

                    if (log.isDebugEnabled())
                        log.debug("Found the provider with the name " + name + " in the database.");

                    // Return the provider
                    return provider;
                } // end if(provider found)

                if (log.isDebugEnabled())
                    log.debug("The provider with the name " + name + " was not found in the database.");

                return null;
            } // end try(get provider)
            catch (SQLException e) {
                log.error("A SQLException occurred while getting the provider with the name " + name, e);

                return null;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return getByName(name);
            } finally {
                dbConnectionManager.closeResultSet(results);
            } // end finally(close ResultSet)
        } // end synchronized
    } // end method getByName(String)

    @Override
    public Provider loadBasicProvider(int providerId) throws DatabaseConfigException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        synchronized (psGetByIdLock) {
            if (log.isDebugEnabled())
                log.debug("Getting the provider with ID " + providerId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            try {
                // If the PreparedStatement to get a provider by ID was not defined, create it
                if (psGetById == null || dbConnectionManager.isClosed(psGetById)) {
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
                                                   COL_GRANULARITY + "," +
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

                    if (log.isDebugEnabled())
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
                if (results.next()) {
                    // The Object which will contain data on the provider
                    Provider provider = new Provider();

                    int i = 1;
                    // Set the fields on the provider
                    provider.setId(results.getInt(i++));
                    provider.setCreatedAt(results.getDate(i++));
                    provider.setUpdatedAt(results.getTimestamp(i++));
                    provider.setName(results.getString(i++));
                    provider.setOaiProviderUrl(results.getString(i++));
                    provider.setTitle(results.getString(i++));
                    provider.setCreator(results.getString(i++));
                    provider.setSubject(results.getString(i++));
                    provider.setDescription(results.getString(i++));
                    provider.setPublisher(results.getString(i++));
                    provider.setContributors(results.getString(i++));
                    provider.setDate(results.getDate(i++));
                    provider.setType(results.getString(i++));
                    provider.setFormat(results.getString(i++));
                    provider.setIdentifier(results.getInt(i++));
                    provider.setLanguage(results.getString(i++));
                    provider.setRelation(results.getString(i++));
                    provider.setCoverage(results.getString(i++));
                    provider.setRights(results.getString(i++));
                    provider.setService(results.getBoolean(i++));
                    provider.setNextListSetsListFormats(results.getDate(i++));
                    provider.setProtocolVersion(results.getString(i++));
                    provider.setLastValidationDate(results.getDate(i++));
                    provider.setIdentify(results.getBoolean(i++));
                    provider.setGranularity(results.getString(i++));
                    provider.setListFormats(results.getBoolean(i++));
                    provider.setListSets(results.getBoolean(i++));
                    provider.setWarnings(results.getInt(i++));
                    provider.setErrors(results.getInt(i++));
                    provider.setRecordsAdded(results.getInt(i++));
                    provider.setRecordsReplaced(results.getInt(i++));
                    provider.setLastOaiRequest(results.getString(i++));
                    Timestamp ts = results.getTimestamp(i++);
                    if (ts != null) {
                        provider.setLastHarvestEndTime(new java.util.Date(ts.getTime()));
                    }
                    provider.setLastLogReset(results.getDate(i++));
                    provider.setLogFileName(results.getString(i++));
                    provider.setNumberOfRecordsToHarvest(results.getLong(i++));

                    if (log.isDebugEnabled())
                        log.debug("Found the provider with ID " + providerId + " in the database.");

                    // Return the provider
                    return provider;
                } // end if(provider found)

                if (log.isDebugEnabled())
                    log.debug("The provider with ID " + providerId + " was not found in the database.");

                return null;
            } // end try(get the provider)
            catch (SQLException e) {
                log.error("A SQLException occurred while getting the provider with ID " + providerId, e);

                return null;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return loadBasicProvider(providerId);
            } finally {
                dbConnectionManager.closeResultSet(results);
            } // end finally(close ResultSet)
        } // end synchronized
    } // end method loadBasicProvider(int)

    @Override
    public boolean insert(Provider provider) throws DataException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        // Check that the non-ID fields on the provider are valid
        validateFields(provider, false, true);

        synchronized (psInsertLock) {
            if (log.isDebugEnabled())
                log.debug("Inserting a new provider");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try {
                // If the PreparedStatement to insert a provider was not defined, create it
                if (psInsert == null || dbConnectionManager.isClosed(psInsert)) {
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
                                                                            COL_GRANULARITY + ", " +
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
                                              " ?, ?, ?, ?, ?, ?)";

                    if (log.isDebugEnabled())
                        log.debug("Creating the \"insert provider\" PreparedStatement from the SQL " + insertSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
                } // end if(insert PreparedStatement not defined)

                int i = 1;
                // Set the parameters on the insert statement
                psInsert.setDate(i++, provider.getCreatedAt());
                psInsert.setTimestamp(i++, provider.getUpdatedAt());
                psInsert.setString(i++, provider.getName());
                psInsert.setString(i++, provider.getOaiProviderUrl());
                psInsert.setString(i++, provider.getTitle());
                psInsert.setString(i++, provider.getCreator());
                psInsert.setString(i++, provider.getSubject());
                psInsert.setString(i++, provider.getDescription());
                psInsert.setString(i++, provider.getPublisher());
                psInsert.setString(i++, provider.getContributors());
                psInsert.setDate(i++, provider.getDate());
                psInsert.setString(i++, provider.getType());
                psInsert.setString(i++, provider.getFormat());
                psInsert.setInt(i++, provider.getIdentifier());
                psInsert.setString(i++, provider.getLanguage());
                psInsert.setString(i++, provider.getRelation());
                psInsert.setString(i++, provider.getCoverage());
                psInsert.setString(i++, provider.getRights());
                psInsert.setBoolean(i++, provider.getService());
                psInsert.setDate(i++, provider.getNextListSetsListFormats());
                psInsert.setString(i++, provider.getProtocolVersion());
                psInsert.setDate(i++, provider.getLastValidationDate());
                psInsert.setBoolean(i++, provider.getIdentify());
                psInsert.setString(i++, provider.getGranularity());
                psInsert.setBoolean(i++, provider.getListFormats());
                psInsert.setBoolean(i++, provider.getListSets());
                psInsert.setInt(i++, provider.getWarnings());
                psInsert.setInt(i++, provider.getErrors());
                psInsert.setInt(i++, provider.getRecordsAdded());
                psInsert.setInt(i++, provider.getRecordsReplaced());
                psInsert.setString(i++, provider.getLastOaiRequest());
                psInsert.setTimestamp(i++, provider.getLastHarvestEndTime() == null ? null :
                        new Timestamp(provider.getLastHarvestEndTime().getTime()));
                psInsert.setDate(i++, provider.getLastLogReset());
                psInsert.setString(i++, provider.getLogFileName());
                psInsert.setLong(i++, provider.getNumberOfRecordsToHarvest());

                // Execute the insert statement and return the result
                if (dbConnectionManager.executeUpdate(psInsert) > 0) {
                    // Get the auto-generated resource identifier ID and set it correctly on this Provider Object
                    rs = dbConnectionManager.createStatement().executeQuery("SELECT LAST_INSERT_ID()");

                    if (rs.next())
                        provider.setId(rs.getInt(1));

                    boolean success = true;

                    // Add the correct formats for the provider
                    for (Format format : provider.getFormats())
                        success = getProviderFormatUtilDAO().insert(provider.getId(), format.getId()) && success;

                    // Add the correct sets for the provider
                    for (Set set : provider.getSets())
                        success = (set.getId() <= 0 ? getSetDAO().insertForProvider(set, provider.getId()) : getSetDAO().addToProvider(set, provider.getId())) && success;

                    if (success)
                        LogWriter.addInfo(logObj.getLogFileLocation(), "Added a new repository with the URL " + provider.getOaiProviderUrl());
                    else {
                        LogWriter.addWarning(logObj.getLogFileLocation(), "Added a new repository with the URL " + provider.getOaiProviderUrl() + ", but failed to mark which sets and formats it outputs");

                        logObj.setWarnings(logObj.getWarnings() + 1);
                        getLogDAO().update(logObj);
                    }

                    return success;
                } // end if(insert succeeded)
                else {
                    LogWriter.addError(logObj.getLogFileLocation(), "Failed to add a new repository with the URL " + provider.getOaiProviderUrl());

                    logObj.setErrors(logObj.getErrors() + 1);
                    getLogDAO().update(logObj);

                    return false;
                }
            } // end try(insert the provider)
            catch (SQLException e) {
                log.error("A SQLException occurred while inserting a new provider", e);

                LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while trying to add a new repository with the URL " + provider.getOaiProviderUrl());

                logObj.setErrors(logObj.getErrors() + 1);
                getLogDAO().update(logObj);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return insert(provider);
            } finally {
                dbConnectionManager.closeResultSet(rs);
            } // end finally(close ResultSet)
        } // end synchronized
    } // end insert(Provider)

    public boolean update(Provider provider) throws DataException {
        return update(provider, true);
    }

    public boolean update(Provider provider, boolean revalidate) throws DataException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        // Check that the fields on the provider are valid
        validateFields(provider, true, true);

        synchronized (psUpdateLock) {
            if (log.isDebugEnabled())
                log.debug("Updating the provider with ID " + provider.getId());

            try {
                // If the PreparedStatement to update a provider is not defined, create it
                if (psUpdate == null || dbConnectionManager.isClosed(psUpdate)) {
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
                                                                          COL_GRANULARITY + "=?, " +
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

                    if (log.isDebugEnabled())
                        log.debug("Creating the PreparedStatement to update a provider from the SQL " + updateSql);

                    // A prepared statement to run the update SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psUpdate = dbConnectionManager.prepareStatement(updateSql, psUpdate);
                } // end if(update PreparedStatement not defined)

                int i = 1;
                // Set the parameters on the update statement
                psUpdate.setDate(i++, provider.getCreatedAt());
                psUpdate.setString(i++, provider.getName());
                psUpdate.setString(i++, provider.getOaiProviderUrl());
                psUpdate.setString(i++, provider.getTitle());
                psUpdate.setString(i++, provider.getCreator());
                psUpdate.setString(i++, provider.getSubject());
                psUpdate.setString(i++, provider.getDescription());
                psUpdate.setString(i++, provider.getPublisher());
                psUpdate.setString(i++, provider.getContributors());
                psUpdate.setDate(i++, provider.getDate());
                psUpdate.setString(i++, provider.getType());
                psUpdate.setString(i++, provider.getFormat());
                psUpdate.setInt(i++, provider.getIdentifier());
                psUpdate.setString(i++, provider.getLanguage());
                psUpdate.setString(i++, provider.getRelation());
                psUpdate.setString(i++, provider.getCoverage());
                psUpdate.setString(i++, provider.getRights());
                psUpdate.setBoolean(i++, provider.getService());
                psUpdate.setDate(i++, provider.getNextListSetsListFormats());
                psUpdate.setString(i++, provider.getProtocolVersion());
                psUpdate.setDate(i++, provider.getLastValidationDate());
                psUpdate.setBoolean(i++, provider.getIdentify());
                psUpdate.setString(i++, provider.getGranularity());
                psUpdate.setBoolean(i++, provider.getListFormats());
                psUpdate.setBoolean(i++, provider.getListSets());
                psUpdate.setInt(i++, provider.getWarnings());
                psUpdate.setInt(i++, provider.getErrors());
                psUpdate.setInt(i++, provider.getRecordsAdded());
                psUpdate.setInt(i++, provider.getRecordsReplaced());
                psUpdate.setString(i++, provider.getLastOaiRequest());

                // I've seen this elsewhere, but for some reason java.sql.Date doesn't put the time into
                // the db. This goes against the documentation.
                // http://download.oracle.com/javase/6/docs/api/java/sql/Date.html
                // http://dev.mysql.com/doc/refman/5.1/en/connector-j-reference-type-conversions.html
                psUpdate.setTimestamp(i++, provider.getLastHarvestEndTime() == null ? null :
                        new Timestamp(provider.getLastHarvestEndTime().getTime()));

                psUpdate.setDate(i++, provider.getLastLogReset());
                psUpdate.setString(i++, provider.getLogFileName());
                psUpdate.setLong(i++, provider.getNumberOfRecordsToHarvest());
                psUpdate.setInt(i++, provider.getId());

                // Execute the update statement and return the result
                // Execute the update statement and return the result
                if (dbConnectionManager.executeUpdate(psUpdate) > 0) {
                    if (revalidate) {
                        // Remove the old permissions for the group
                        boolean success = getProviderFormatUtilDAO().deleteFormatsForProvider(provider.getId());

                        // Remove all sets from this provider that used to belong to it but no longer do
                        for (Set set : getSetDAO().getSetsForProvider(provider.getId()))
                            if (!provider.getSets().contains(set))
                                success = getSetDAO().removeFromProvider(set, provider.getId()) && success;

                        // Add the correct sets for the provider
                        for (Set set : provider.getSets())
                            success = (set.getId() <= 0 ? getSetDAO().insertForProvider(set, provider.getId()) : getSetDAO().addToProvider(set, provider.getId())) && success;

                        // Add the permissions to the group
                        for (Format format : provider.getFormats())
                            success = getProviderFormatUtilDAO().insert(provider.getId(), format.getId()) && success;

                        if (success)
                            LogWriter.addInfo(logObj.getLogFileLocation(), "Updated the repository with the URL " + provider.getOaiProviderUrl());
                        else {
                            LogWriter.addWarning(logObj.getLogFileLocation(), "Updated the repository with the URL " + provider.getOaiProviderUrl() + ", but failed to update the sets and formats it outputs");

                            logObj.setWarnings(logObj.getWarnings() + 1);
                            getLogDAO().update(logObj);
                        }

                        return success;
                    } else {
                        return true;
                    }
                } // end if(update successful)
                else {
                    LogWriter.addError(logObj.getLogFileLocation(), "Failed to update the repository with the URL " + provider.getOaiProviderUrl());

                    logObj.setErrors(logObj.getErrors() + 1);
                    getLogDAO().update(logObj);

                    return false;
                }
            } // end try(update the provider)
            catch (SQLException e) {
                log.error("A SQLException occurred while updating the provider with ID " + provider.getId(), e);

                LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while trying to update the repository with the URL " + provider.getOaiProviderUrl());

                logObj.setErrors(logObj.getErrors() + 1);
                getLogDAO().update(logObj);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return update(provider);
            }
        } // end synchronized
    } // end update(Provider)

    @Override
    public boolean delete(Provider provider) throws DataException, IndexException {
        // Throw an exception if the connection is null. This means the configuration file was bad.
        if (dbConnectionManager.getDbConnection() == null)
            throw new DatabaseConfigException("Unable to connect to the database using the parameters from the configuration file.");

        // Check that the ID field on the provider are valid
        validateFields(provider, true, false);

        synchronized (psDeleteLock) {
            if (log.isDebugEnabled())
                log.debug("Deleting the provider with ID " + provider.getId());

            try {
                // If the PreparedStatement to delete a provider was not defined, create it
                if (psDelete == null || dbConnectionManager.isClosed(psDelete)) {
                    // SQL to delete the row from the table
                    String deleteSql = "DELETE FROM " + PROVIDERS_TABLE_NAME + " " +
                                       "WHERE " + COL_PROVIDER_ID + " = ? ";

                    if (log.isDebugEnabled())
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
                // without deleting them. Also mark all records from the provider
                // as deleted, as well as all records processed from them.
                if (success) {
                    // Remove the reference from provider to the set
                    for (Set set : getSetDAO().getSetsForProvider(provider.getId()))
                        success = getSetDAO().removeFromProvider(set, provider.getId()) && success;

                    // Remove the reference from provider to the harvested record set
                    for (Set set : getSetDAO().getRecordSetsForProvider(provider.getId()))
                        success = getSetDAO().removeFromProvider(set, provider.getId()) && success;

                    for (Record record : getRecordService().getByProviderId(provider.getId()))
                        success = markAsDeleted(record) && success;

                    // TODO performance issue
                    for (Set set : getSetDAO().getAll())
                        if (getRecordService().getBySetSpec(set.getSetSpec()).size() == 0)
                            getSetDAO().delete(set);

                    ((SolrIndexManager) config.getBean("SolrIndexManager")).commitIndex();
                } // end if(delete succeeded)

                if (success)
                    LogWriter.addInfo(logObj.getLogFileLocation(), "Deleted the repository with the URL " + provider.getOaiProviderUrl());
                else {
                    LogWriter.addWarning(logObj.getLogFileLocation(), "Deleted the repository with the URL " + provider.getOaiProviderUrl() + ", but failed to mark its sets and records as deleted");

                    logObj.setWarnings(logObj.getWarnings() + 1);
                    getLogDAO().update(logObj);
                }

                return success;
            } // end try(delete the provider)
            catch (SQLException e) {
                log.error("A SQLException occurred while deleting the provider with ID " + provider.getId(), e);

                LogWriter.addError(logObj.getLogFileLocation(), "An error occurred while trying to delete the repository with the URL " + provider.getOaiProviderUrl());

                logObj.setErrors(logObj.getErrors() + 1);
                getLogDAO().update(logObj);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e) {
                log.info("Re executing the query that failed ");
                return delete(provider);
            }
        } // end synchronized
    } // end method delete(Provider)

    /**
     * Marks a record as deleted. Also marks any records processed from it as deleted
     * 
     * @param deleteMe
     *            The record to delete
     * @return true on success, false on failure
     */
    private boolean markAsDeleted(Record record) throws DataException, IndexException {
        // Whether or not we deleted the record and all records processed from it successfully
        boolean success = true;

        // Mark the record as deleted
        record.setDeleted(true);
        record.setUpdatedAt(new Date());
        success = getRecordService().update(record);

        // TODO changed from getProcessedFrom to getSuccessors. needs to be tested
        // If there were no records processed from this record, we're done
        if (record.getSuccessors().size() == 0)
            return success;

        // If we got here, we need to recursively delete all records
        // processed from the record we just marked as deleted.
        for (RecordIfc sucRec : record.getSuccessors()) {
            Record successorRecord = (Record) sucRec;
            success = markAsDeleted(successorRecord) && success;
        }

        // Return whether or not all deletes were successful
        return success;
    } // end method markAsDeleted(Record)
} // end class DefaultProviderDAO
