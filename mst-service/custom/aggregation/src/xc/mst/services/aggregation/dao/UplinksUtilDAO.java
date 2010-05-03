/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.aggregation.dao;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Accesses uplinks of output record in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class UplinksUtilDAO
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
	 * The name of the database table we're interacting with
	 */
	public final static String UPLINK_TABLE_NAME = "output_record_uplinks";
	
	/**
	 * The name of the ID column
	 */
	public final static String UPLINK_ID = "output_record_uplinks_id";

	/**
	 * The name of the uplink oai ID column
	 */
	public final static String COL_UPLINK_OAI_ID = "uplink_oai_id";

	/**
	 * The name of the output records id column
	 */
	public final static String COL_OUTPUT_RECORD_ID = "output_record_id";
		
	/**
	 * Inserts uplinks into the database
	 *
	 * @param outputRecordId The output record id
	 * @param uplinkOAIId Uplink OAI id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for inserting
	 */
	public abstract boolean insert(int outputRecordId, String uplinkOAIId) throws DataException;

	/**
	 * Deletes uplinks for given output record id 
	 *
	 * @param outputRecordId The output record id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for deleting
	 */
	public abstract boolean deleteUplinksByOutputRecordId(int outputRecordId) throws DataException;
	
} 
