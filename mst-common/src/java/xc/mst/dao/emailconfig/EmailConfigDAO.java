/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.dao.emailconfig;

import org.apache.log4j.Logger;

import xc.mst.bo.emailconfig.EmailConfig;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Data Access Object for the email config table
 * 
 * @author Eric Osisek
 */
public abstract class EmailConfigDAO extends BaseDAO {
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
    public final static String TABLE_NAME = "emailconfig";

    /**
     * The name of the email config ID column
     */
    public final static String COL_EMAIL_CONFIG_ID = "email_config_id";

    /**
     * The name of the email server address column
     */
    public final static String COL_EMAIL_SERVER_ADDRESS = "server_address";

    /**
     * The name of the port number column
     */
    public final static String COL_PORT_NUMBER = "port_number";

    /**
     * The name of the from address column
     */
    public final static String COL_FROM_ADDRESS = "from_address";

    /**
     * The name of the password column
     */
    public final static String COL_PASSWORD = "password";

    /**
     * The name of the encrypted connection column
     */
    public final static String COL_ENCRYPTED_CONNECTION = "encrypted_connection";

    /**
     * The name of the timeout column
     */
    public final static String COL_TIMEOUT = "timeout";

    /**
     * The name of the forgotten password link column
     */
    public final static String COL_FORGOTTEN_PASSWORD_LINK = "forgotten_password_link";

    /**
     * Gets the Email Configuration
     * 
     * @return The email configuration, or null if there was no email configuration in the database.
     * @throws DatabaseConfigException
     *             if there was a problem connecting to the database
     */
    public abstract EmailConfig getConfiguration() throws DatabaseConfigException;

    /**
     * Updates the email configuration
     * 
     * @param emailconfig
     *            The new email configuration
     * @return True on success, false on failure
     * @throws DataException
     *             if there was a problem connecting to the database
     */
    public abstract boolean setConfiguration(EmailConfig emailconfig) throws DataException;

    /**
     * Validates the fields on the passed EmailConfig Object
     * 
     * @param emailconfig
     *            The EmailConfig Object to validate
     * @param validateId
     *            true if the ID field should be validated
     * @param validateNonId
     *            true if the non-ID fields should be validated
     * @throws DataException
     *             If one or more of the fields on the passed EmailConfig Object were invalid
     */
    protected void validateFields(EmailConfig emailconfig, boolean validateId, boolean validateNonId) throws DataException {
        StringBuilder errorMessage = new StringBuilder();

        // Check the ID field if we're supposed to
        if (validateId) {
            if (log.isDebugEnabled())
                log.debug("Checking the ID");

            if (emailconfig.getEmailConfigId() < 0)
                errorMessage.append("The email_config_id is invalid. ");
        } // end if(we should validate the ID field)

        // Check the non-ID fields if we're supposed to
        if (validateNonId) {
            if (log.isDebugEnabled())
                log.debug("Checking the non-ID fields");

            if (emailconfig.getEmailServerAddress() == null || emailconfig.getEmailServerAddress().length() <= 0 || emailconfig.getEmailServerAddress().length() > 255)
                errorMessage.append("The server address is invalid. ");

            if (emailconfig.getFromAddress() == null || emailconfig.getFromAddress().length() <= 0 || emailconfig.getFromAddress().length() > 255)
                errorMessage.append("The from address is invalid. ");

            if (emailconfig.getPassword() != null && emailconfig.getPassword().length() > 100)
                errorMessage.append("The password is invalid. ");

            if (emailconfig.getEncryptedConnection() != null && emailconfig.getEncryptedConnection().length() > 31)
                errorMessage.append("The encrypted connection is invalid. ");

            if (emailconfig.getPortNumber() < 0)
                errorMessage.append("The port number is invalid. ");
        } // end if(we should validate the non-ID fields)

        // Log the error and throw the exception if any fields are invalid
        if (errorMessage.length() > 0) {
            String errors = errorMessage.toString();
            log.error("The following errors occurred: " + errors);
            throw new DataException(errors);
        } // end if(an error occurred)
    } // end method validateFields(EmailConfig, boolean, boolean)
} // end class EmailConfigDAO
