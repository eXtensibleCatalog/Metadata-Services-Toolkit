/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.harvest;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the records from a harvest
 *
 * @author Eric Osisek
 */
public abstract class HarvestRecordUtilDAO extends BaseDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * The Object managing the database connection
	 */
	protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

	/**
	 * The name of the harvest to records database table
	 */
	public final static String HARVESTS_TO_RECORDS_TABLE_NAME = "harvests_to_records";

	/**
	 * The name of the harvest ID column
	 */
	public final static String COL_HARVEST_ID = "harvest_id";

	/**
	 * The name of the record ID column
	 */
	public final static String COL_RECORD_ID = "record_id";

	/**
	 * Inserts a row in the database associating a harvest to a record.
	 *
	 * @param harvestId The harvest to assign to the record
	 * @param recordId The record to assign the harvest to
	 * @return True on success, false on failure
	 */
	public abstract boolean insert(int harvestId, long recordId);

	/**
	 * Deletes the row in the database associating the harvest with the record.
	 *
	 * @param harvestId The harvest to remove from the record
	 * @param recordId The record to remove the harvest from
	 * @return True on success, false on failure
	 */
	public abstract boolean delete(int harvestId, long recordId);

	/**
	 * Deletes all rows in the database associating a harvest with the record.
	 *
	 * @param recordId The record to remove harvest/record associations for
	 * @return True on success, false on failure
	 */
	public abstract boolean deleteForRecord(long recordId);

	/**
	 * Gets all records which came from a harvest
	 *
	 * @param harvestId The ID of the harvest whose records should be returned
	 * @return A list of record IDs for the records which came from the harvest
	 */
	public abstract List<Long> getRecordsForHarvest(int harvestId);
} // end class HarvestRecordUtilDAO
