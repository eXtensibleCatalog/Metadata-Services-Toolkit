/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.dao.user;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.user.Server;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data access object for a server which can be used for authentication.
 * Data is taken from the servers table.
 * 
 * @author Eric Osisek
 */
public abstract class ServerDAO extends BaseDAO {
    /**
     * A reference to the logger for this class
     */
    protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * The Object managing the database connection
     */
    protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

    /**
     * The name of the database table with server information
     */
    public final static String SERVERS_TABLE_NAME = "servers";

    /**
     * The name of the server ID column
     */
    public final static String COL_SERVER_ID = "server_id";

    /**
     * The name of the URL column
     */
    public final static String COL_URL = "url";

    /**
     * The name of the name column
     */
    public final static String COL_NAME = "name";

    /**
     * The name of the port column
     */
    public final static String COL_PORT = "port";

    /**
     * The name of the user name attribute column
     */
    public final static String COL_USERNAME_ATTRIBUTE = "username_attribute";

    /**
     * The name of the start location column
     */
    public final static String COL_START_LOCATION = "start_location";

    /**
     * The name of the type column
     */
    public final static String COL_TYPE = "type";

    /**
     * The name of the institution column
     */
    public final static String COL_INSTITUION = "institution";

    /**
     * The name of the forgot password url column
     */
    public final static String COL_FORGOT_PASSWORD_URL = "forgot_password_url";

    /**
     * The name of the forgot password label column
     */
    public final static String COL_FORGOT_PASSWORD_LABEL = "forgot_password_label";

    /**
     * The name of the show forgot password link column
     */
    public final static String COL_SHOW_FORGOT_PASSWORD_LINK = "show_forgot_password_link";

    /**
     * Gets all servers in the database
     * 
     * @return A list of all servers in the database
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract List<Server> getAll() throws DatabaseConfigException;

    /**
     * Gets a server from the database by its ID
     * 
     * @param serverId
     *            The ID of the server to get
     * @return The server with the passed ID
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract Server getById(int serverId) throws DatabaseConfigException;

    /**
     * Gets a server from the database by its name
     * 
     * @param name
     *            The name of the server to get
     * @return The server with the passed name
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract Server getByName(String name) throws DatabaseConfigException;

    /**
     * Inserts a new server into the database
     * 
     * @param server
     *            The server to insert
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed server was not valid for inserting
     */
    public abstract boolean insert(Server server) throws DataException;

    /**
     * Update a new server in the database
     * 
     * @param server
     *            The server to update
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed server was not valid for updating
     */
    public abstract boolean update(Server server) throws DataException;

    /**
     * Deletes a new server from the database
     * 
     * @param server
     *            The server to delete
     * @return True on success, false on failure
     * @throws DataException
     *             if the passed server was not valid for deleting
     */
    public abstract boolean delete(Server server) throws DataException;

    /**
     * Validates the fields on the passed Server Object
     * 
     * @param server
     *            The server to validate
     * @param validateId
     *            true if the ID field should be validated
     * @param validateNonId
     *            true if the non-ID fields should be validated
     * @throws DataException
     *             If one or more of the fields on the passed server were invalid
     */
    protected void validateFields(Server server, boolean validateId, boolean validateNonId) throws DataException {
        StringBuilder errorMessage = new StringBuilder();

        // Check the ID field if we're supposed to
        if (validateId) {
            if (log.isDebugEnabled())
                log.debug("Checking the ID");

            if (server.getId() < 0)
                errorMessage.append("The server_id is invalid. ");
        } // end if(we should validate the ID field)

        // Check the non-ID fields if we're supposed to
        if (validateNonId) {
            if (log.isDebugEnabled())
                log.debug("Checking the non-ID fields");

            if (server.getUrl() == null || server.getUrl().length() <= 0 || server.getUrl().length() > 255)
                errorMessage.append("The URL is invalid. ");

            if (server.getName() == null || server.getName().length() <= 0 || server.getName().length() > 255)
                errorMessage.append("The name is invalid. ");

            if (server.getUserNameAttribute() == null || server.getUserNameAttribute().length() <= 0 || server.getUserNameAttribute().length() > 255)
                errorMessage.append("The username attribute is invalid. ");

            if (server.getInstitution() != null && server.getInstitution().length() > 255)
                errorMessage.append("The institution is invalid. ");

            if (server.getForgotPasswordUrl() != null && server.getForgotPasswordUrl().length() > 255)
                errorMessage.append("The forgot password URL is invalid. ");

            if (server.getForgotPasswordLabel() != null && server.getForgotPasswordLabel().length() > 255)
                errorMessage.append("The forgot password label is invalid. ");
        } // end if(we should validate the non-ID fields)

        // Log the error and throw the exception if any fields are invalid
        if (errorMessage.length() > 0) {
            String errors = errorMessage.toString();
            log.error("The following errors occurred: " + errors);
            throw new DataException(errors);
        } // end if (error detected)
    } // end method validateFields(Server, boolean, boolean)
} // end class ServerDAO
