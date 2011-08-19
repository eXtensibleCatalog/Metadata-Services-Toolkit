/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Class to get, cache, and update the next unique OAI identifiers for records output
 * by each service.  A Metadata Service can use the methods on this class to maintain
 * the correct values for the next OAI identifier for a service while minimizing the
 * number of SQL queries it makes.
 *
 * @author Eric Osisek
 */
public abstract class OaiIdentifierForServiceDAO extends BaseDAO
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
     * A static map from the service ID to the next OAI Identifier for that service
     * The implementing class should use this to cache values read from the database.
     * This will allow the services to get unique IDs for all records (of which there
     * may be millions) without having to query the database more than twice (once to
     * get the last value, once to write the new value when the service finishes.)
     */
    protected static HashMap<Integer, Long> nextOaiIdForService = new HashMap<Integer, Long>();

    /**
     * The name of the database table we're interacting with.
     */
    protected final static String OAI_IDENTIFIER_FOR_SERVICES_TABLE_NAME = "oai_identifier_for_services";

    /**
     * The name of the oai identifier for service ID column
     */
    protected final static String COL_OAI_IDENTIFIER_FOR_SERVICE_ID = "oai_identifier_for_service_id";

    /**
     * The name of the next oai ID column
     */
    protected final static String COL_NEXT_OAI_ID = "next_oai_id";

    /**
     * The name of the service ID column
     */
    protected final static String COL_SERVICE_ID = "service_id";

    /**
     * Gets the next unused OAI identifier for the service with the passed service ID.
     * The returned OAI identifier is considered to be used after it is returned by this
     * method.
     *
     * After all the necessary calls to this method are completed, the calling code is
     * expected to call the writeNextOaiId method passing the same service ID.  This method
     * returns an ID but the database is not updated with the new value until writeNextOaiId
     * is called.  This is to improve performance by reducing the number of SQL queries.
     * Without this improvement the Metadata Services had a run time of days instead of hours.
     *
     * @param serviceId The ID of the service whose next OAI identifier we're getting
     * @return The next unused OAI identifier for the service with the passed ID.  This
     *         OAI identifier is considered to be used after it is returned.
     */
    public abstract long getNextOaiIdForService(int serviceId);

    /**
     * Updates the next ID for the service in the database based on the current known
     * value for that service This method should be called by a service after that
     * service finishes assigning OAI identifiers to records.
     *
     * @param serviceId The service ID whose next OAI ID is to be updated
     * @return true on success, false on failure
     */
    public abstract boolean writeNextOaiId(int serviceId);

    /**
     * Updates the next ID for the servicein the database based on the given
     * value for that service. This method should be called when server is restarted after
     * unexpected shut down when service was running.
     *
     * @param serviceId The service ID whose next OAI ID is to be updated
     * @param nextOaiId next OAI identifier ID to be used
     */
    public abstract void writeNextOaiId(int serviceId, long nextOaiId);
} // end class OaiIdentifierForServiceDAO
