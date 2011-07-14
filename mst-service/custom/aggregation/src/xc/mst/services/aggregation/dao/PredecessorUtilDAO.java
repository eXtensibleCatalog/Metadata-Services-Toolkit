/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.aggregation.dao;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Accesses output record in the database
 *
 * @author Sharmila Ranganathan
 */
public abstract class PredecessorUtilDAO
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
	public final static String PREDECESSOR_RECORD_TABLE_NAME = "predecessor_record";
	
	/**
	 * The name of the ID column
	 */
	public final static String PREDECESSOR_RECORD_ID = "predecessor_record_id";

	/**
	 * The name of the predecessor oai ID column
	 */
	public final static String COL_PREDECESSOR_OAI_ID = "predecessor_oai_id";

	/**
	 * The name of the output record id column
	 */
	public final static String COL_OUTPUT_RECORD_ID = "output_record_id";

	/**
	 * Gets list of predecessor OAI identifiers that match the given output reclord id
	 *
	 * @param outputRecordId The output record Id
	 * @return list of predecessor OAI identifiers that match the given output record id
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<String> getByOutputRecordId(int outputRecordId) throws DatabaseConfigException;
		
	/**
	 * Inserts predecessor record into the database
	 *
	 * @param outputRecordId The output record id
	 * @param predecessorOAIId Predecessor OAI id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for inserting
	 */
	public abstract boolean insert(int outputRecordId, String predecessorOAIId) throws DataException;

	/**
	 * Deletes the predecessors of given output record id 
	 *
	 * @param outputRecordId The output record id
	 * @return True on success, false on failure
	 * @throws DataException if the passed values are not valid for deleting
	 */
	public abstract boolean deletePredecessorsForOutputRecordId(int outputRecordId) throws DataException;
	
} 
