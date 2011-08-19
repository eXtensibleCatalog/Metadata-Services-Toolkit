/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.harvest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xc.mst.dao.DBConnectionResetException;

/**
 * MySQL implementation of the utility class for manipulating the records from a harvest
 *
 * @author Eric Osisek
 */
public class DefaultHarvestRecordUtilDAO extends HarvestRecordUtilDAO
{
    /**
     * A PreparedStatement to add a record for a harvest into the database
     */
    private static PreparedStatement psInsert = null;

    /**
     * A PreparedStatement to remove a record from a harvest from the database
     */
    private static PreparedStatement psDelete = null;

    /**
     * A PreparedStatement to remove all harvest/record assignments for a record from the database
     */
    private static PreparedStatement psDeleteForRecord = null;

    /**
     * A PreparedStatement to get all records from a harvest
     */
    private static PreparedStatement psGetRecordsForHarvest = null;

    /**
     * Lock to prevent concurrent access of the prepared statement to add a record for a harvest
     */
    private static Object psInsertLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to remove a record from a harvest
     */
    private static Object psDeleteLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to remove all harvest/record assignments for a record from the database
     */
    private static Object psDeleteForRecordLock = new Object();

    /**
     * Lock to prevent concurrent access of the prepared statement to get all records from a harvest
     */
    private static Object psGetRecordsForHarvestLock = new Object();

    @Override
    public boolean insert(int harvestId, long recordId)
    {
        synchronized(psInsertLock)
        {
            if(log.isDebugEnabled())
                log.debug("Adding the record with ID " + recordId + " for the harvest with ID " + harvestId + ".");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to insert a harvest to Top Level Tab is not defined, create it
                if(psInsert == null || dbConnectionManager.isClosed(psInsert))
                {
                    // SQL to insert the new row
                    String insertSql = "INSERT INTO " + HARVESTS_TO_RECORDS_TABLE_NAME +
                                                        " (" + COL_HARVEST_ID + ", " +
                                                               COL_RECORD_ID + ") " +
                                       "VALUES (?, ?)";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"add record for a harvest\" PreparedStatement from the SQL " + insertSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psInsert = dbConnectionManager.prepareStatement(insertSql, psInsert);
                    dbConnectionManager.registerStatement(psInsert);
                } // end if (insert prepared statement is null)

                // Set the parameters on the insert statement
                psInsert.setInt(1, harvestId);
                psInsert.setLong(2, recordId);

                // Execute the insert statement and return the result
                return dbConnectionManager.executeUpdate(psInsert) > 0;
            } // end try (insert the record)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while adding tthe record with ID " + recordId + " for the harvest with ID " + harvestId + ".", e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return insert(harvestId, recordId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method insert(int, int)

    @Override
    public boolean delete(int harvestId, long recordId)
    {
        synchronized(psDeleteLock)
        {
            if(log.isDebugEnabled())
                log.debug("Removing the record with ID " + recordId + " from the harvest with ID " + harvestId + ".");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to insert a harvest to record is not defined, create it
                if(psDelete == null || dbConnectionManager.isClosed(psDelete))
                {
                    // SQL to insert the new row
                    String deleteSql = "DELETE FROM " + HARVESTS_TO_RECORDS_TABLE_NAME + " " +
                                       "WHERE " + COL_HARVEST_ID + "=? " +
                                       "AND " + COL_RECORD_ID + "=? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"remove record from a harvest\" PreparedStatement from the SQL " + deleteSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDelete = dbConnectionManager.prepareStatement(deleteSql, psDelete);
                } // end if (insert prepared statement is null)

                // Set the parameters on the insert statement
                psDelete.setInt(1, harvestId);
                psDelete.setLong(2, recordId);

                // Execute the delete statement and return the result
                return dbConnectionManager.executeUpdate(psDelete) > 0;
            } // end try (delete the record)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while removing the record with ID " + recordId + " from the harvest with ID " + harvestId + ".", e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return delete(harvestId, recordId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method delete(int, long)

    @Override
    public boolean deleteForRecord(long recordId)
    {
        synchronized(psDeleteForRecordLock)
        {
            if(log.isDebugEnabled())
                log.debug("Removing the harvest/record associations with ID " + recordId + ".");

            // The ResultSet returned by the query
            ResultSet rs = null;

            try
            {
                // If the PreparedStatement to insert a harvest to record is not defined, create it
                if(psDeleteForRecord == null || dbConnectionManager.isClosed(psDeleteForRecord))
                {
                    // SQL to insert the new row
                    String deleteSql = "DELETE FROM " + HARVESTS_TO_RECORDS_TABLE_NAME + " " +
                                       "WHERE " + COL_RECORD_ID + "=? ";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"remove record from a harvest\" PreparedStatement from the SQL " + deleteSql);

                    // A prepared statement to run the insert SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psDeleteForRecord = dbConnectionManager.prepareStatement(deleteSql, psDeleteForRecord);
                } // end if (insert prepared statement is null)

                // Set the parameters on the delete statement
                psDeleteForRecord.setLong(1, recordId);

                // Execute the delete statement and return the result
                return dbConnectionManager.executeUpdate(psDeleteForRecord) > 0;
            } // end try (delete the harvest/record associations)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while removing the harvest/record associations with ID " + recordId + ".", e);

                return false;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return deleteForRecord(recordId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(rs);
            } // end finally
        } // end synchronized
    } // end method deleteForRecord(long)

    @Override
    public List<Long> getRecordsForHarvest(int harvestId)
    {
        synchronized(psGetRecordsForHarvestLock)
        {
            if(log.isDebugEnabled())
                log.debug("Getting the records for the harvest with harvest ID " + harvestId);

            // The ResultSet from the SQL query
            ResultSet results = null;

            // The list of records for the harvest with the passed ID
            List<Long> recordIds = new ArrayList<Long>();

            try
            {
                // If the PreparedStatement to get records by harvest ID wasn't defined, create it
                if(psGetRecordsForHarvest == null || dbConnectionManager.isClosed(psGetRecordsForHarvest))
                {
                    // SQL to get the rows
                    String selectSql = "SELECT " + COL_RECORD_ID + " " +
                                       "FROM " + HARVESTS_TO_RECORDS_TABLE_NAME + " " +
                                       "WHERE " + COL_HARVEST_ID + "=?";

                    if(log.isDebugEnabled())
                        log.debug("Creating the \"get records for harvest\" PreparedStatement from the SQL " + selectSql);

                    // A prepared statement to run the select SQL
                    // This should sanitize the SQL and prevent SQL injection
                    psGetRecordsForHarvest = dbConnectionManager.prepareStatement(selectSql, psGetRecordsForHarvest);
                }

                // Set the parameters on the select statement
                psGetRecordsForHarvest.setInt(1, harvestId);

                // Get the result of the SELECT statement

                // Execute the query
                results = dbConnectionManager.executeQuery(psGetRecordsForHarvest);

                // For each result returned, add a harvest to Top Level Tab object to the list with the returned data
                while(results.next())
                    recordIds.add(new Long(results.getLong(1)));

                if(log.isDebugEnabled())
                    log.debug("Found " + recordIds.size() + " record IDs that the harvest with harvest ID " + harvestId + " belongs to.");

                return recordIds;
            } // end try (get and return the record IDs which the harvest belongs to)
            catch(SQLException e)
            {
                log.error("A SQLException occurred while getting the records for the harvest with harvest ID " + harvestId, e);

                return recordIds;
            } // end catch(SQLException)
            catch (DBConnectionResetException e){
                log.info("Re executing the query that failed ");
                return getRecordsForHarvest(harvestId);
            }
            finally
            {
                dbConnectionManager.closeResultSet(results);
            } // end finally
        } // end synchronized
    } // end method getRecordsForHarvest(int)
} // end class DefaultHarvestRecordUtilDAO
