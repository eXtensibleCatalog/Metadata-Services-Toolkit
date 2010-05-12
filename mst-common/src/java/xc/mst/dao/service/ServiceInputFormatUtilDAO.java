/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.service;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the formats a service can accept as input
 *
 * @author Eric Osisek
 */
public abstract class ServiceInputFormatUtilDAO extends BaseDAO
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
	 * The name of the service to input formats database table
	 */
	public final static String SERVICES_TO_INPUT_FORMATS_TABLE_NAME = "services_to_input_formats";

	/**
	 * The name of the service ID column
	 */
	public final static String COL_SERVICE_ID = "service_id";

	/**
	 * The name of the format ID column
	 */
	public final static String COL_FORMAT_ID = "format_id";

	/**
	 * Inserts a row in the database showing that a service can accept
	 * a format as input.
	 *
	 * @param serviceId The ID of the service which can accept the format as input
	 * @param inputFormatId The ID of the format the service can accept as input
	 * @return True on success, false on failure
	 */
	public abstract boolean insert(int serviceId, int formatId);

	/**
	 * Deletes the row in the database showing that a service can accept
	 * a format as input.
	 *
	 * @param serviceId The service to remove from the input format
	 * @param inputFormatId The input format to remove the service from
	 * @return True on success, false on failure
	 */
	public abstract boolean delete(int serviceId, int formatId);

	/**
	 * Gets all formats which a service can accept as input
	 *
	 * @param serviceId The ID of the service whose input formats should be returned
	 * @return A list of format IDs for the formats the service can accept as input
	 */
	public abstract List<Integer> getInputFormatsForService(int serviceId);

	/**
	 * Deletes all input formats assignments for a service
	 *
	 * @param serviceId The ID of the service whose input format assignments should be removed
	 * @return True on success, false on failure
	 */
	public abstract boolean deleteInputFormatsForService(int serviceId);
} // end class ServiceInputFormatUtilDAO
