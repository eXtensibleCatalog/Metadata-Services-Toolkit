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
 * Utility class for manipulating the sets a service can output
 *
 * @author Eric Osisek
 */
public abstract class ServiceOutputSetUtilDAO extends BaseDAO
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
     * The name of the service to output sets database table
     */
    public final static String SERVICES_TO_OUTPUT_SETS_TABLE_NAME = "services_to_output_sets";

    /**
     * The name of the service ID column
     */
    public final static String COL_SERVICE_ID = "service_id";

    /**
     * The name of the set ID column
     */
    public final static String COL_SET_ID = "set_id";

    /**
     * Inserts a row in the database showing that a service can output.
     *
     * @param serviceId The ID of the service which can output the set
     * @param setId The ID of the set the service can output
     * @return True on success, false on failure
     */
    public abstract boolean insert(int serviceId, int setId);

    /**
     * Deletes the row in the database showing that a service can output a set.
     *
     * @param serviceId The service to remove from the output set
     * @param setId The output set to remove the service from
     * @return True on success, false on failure
     */
    public abstract boolean delete(int serviceId, int setId);

    /**
     * Gets all sets which a service can output
     *
     * @param serviceId The ID of the service whose output sets should be returned
     * @return A list of set IDs for the sets the service can output
     */
    public abstract List<Integer> getOutputSetsForService(int serviceId);

    /**
     * Deletes all output sets assignments for a service
     *
     * @param serviceId The ID of the service whose output set assignments should be removed
     * @return True on success, false on failure
     */
    public abstract boolean deleteOutputSetsForService(int serviceId);
} // end class ServiceOutputSetUtilDAO
