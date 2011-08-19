/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
 * Utility class for manipulating the formats a service can accept as output
 *
 * @author Eric Osisek
 */
public abstract class ServiceOutputFormatUtilDAO extends BaseDAO
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
     * The name of the service to output formats database table
     */
    public final static String SERVICES_TO_OUTPUT_FORMATS_TABLE_NAME = "services_to_output_formats";

    /**
     * The name of the service ID column
     */
    public final static String COL_SERVICE_ID = "service_id";

    /**
     * The name of the format ID column
     */
    public final static String COL_FORMAT_ID = "format_id";

    /**
     * Inserts a row in the database showing that a service can output
     * a format.
     *
     * @param serviceId The ID of the service which can output the format
     * @param outputFormatId The ID of the format the service can output
     * @return True on success, false on failure
     */
    public abstract boolean insert(int serviceId, int formatId);

    /**
     * Deletes the row in the database showing that a service can output
     * a format.
     *
     * @param serviceId The service to remove from the output format
     * @param outputFormatId The output format to remove the service from
     * @return True on success, false on failure
     */
    public abstract boolean delete(int serviceId, int formatId);

    /**
     * Gets all formats which a service can output
     *
     * @param serviceId The ID of the service whose output formats should be returned
     * @return A list of format IDs for the formats the service can output
     */
    public abstract List<Integer> getOutputFormatsForService(int serviceId);

    /**
     * Deletes all output formats assignments for a service
     *
     * @param serviceId The ID of the service whose output format assignments should be removed
     * @return True on success, false on failure
     */
    public abstract boolean deleteOutputFormatsForService(int serviceId);
} // end class ServiceOutputFormatUtilDAO
