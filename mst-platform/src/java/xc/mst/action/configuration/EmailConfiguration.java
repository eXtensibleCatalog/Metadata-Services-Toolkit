/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action.configuration;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.emailconfig.EmailConfig;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * The action method that is used to add/edit an email Server
 * 
 * @author Tejaswi Haramurali
 */
public class EmailConfiguration extends BaseActionSupport {

    /** Serial id */
    private static final long serialVersionUID = 4328003705417402790L;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The temporary emailConfig object that is used to populate JSP page fields **/
    private EmailConfig temporaryEmailConfig;

    /** The URL of the email server **/
    private String emailServerAddress;

    /** The from address **/
    private String fromAddress;

    /** The port number of the email server **/
    private int port;

    /** The password for the email server **/
    private String password;

    /** The timeout period **/
    private int timeout;

    /** The type of encrypted connection (can also ne 'none') **/
    private String encryptedConnection;

    /** Information message that describes whether the email server was added correctly */
    private String message;

    private EmailConfig emailConfig = new EmailConfig();

    /** Error type */
    private String errorType;

    /**
     * Overrides default implementation to view the email server configuration page.
     * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute() {
        try {
            emailConfig = getEmailConfigService().getEmailConfiguration();
            return SUCCESS;
        } catch (DatabaseConfigException dce) {
            log.error(dce.getMessage(), dce);
            this.addFieldError("emailConfigError", "Unable to connect to the database. Database configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }

    }

    /**
     * Method that changes the details of the email server
     * 
     * @return {@link #SUCCESS}
     */
    public String changeEmailConfig() {
        try {
            emailConfig.setEmailServerAddress(emailServerAddress);
            emailConfig.setEncryptedConnection(encryptedConnection);
            emailConfig.setFromAddress(fromAddress);
            emailConfig.setPassword(password);
            emailConfig.setPortNumber(port);
            emailConfig.setTimeout(timeout);
            getEmailConfigService().setEmailConfiguration(emailConfig);
            message = "Email Configuration details saved.";
            errorType = "info";
            return SUCCESS;
        } catch (DataException de) {
            log.error(de.getMessage(), de);
            this.addFieldError("changeEmailConfigError", "Error occurred while updating email configuration. An email has been sent to the administrator.");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Sets the URL address of the email server
     * 
     * @param emailServerAddress
     *            email server address
     */
    public void setEmailServerAddress(String emailServerAddress) {
        this.emailServerAddress = emailServerAddress.trim();
    }

    /**
     * Gets the URL address of the server
     * 
     * @return email server address
     */
    public String getEmailServerAddress() {
        return emailServerAddress;
    }

    /**
     * Sets the from address
     * 
     * @param fromAddress
     *            from address
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress.trim();
    }

    /**
     * Returns the from address
     * 
     * @return from address
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Sets the port of the email server
     * 
     * @param port
     *            port number
     */
    public void setPort(int port) {

        this.port = port;
    }

    /**
     * Returns the port number of the email server
     * 
     * @return port number
     */
    public int getPort() {

        return port;
    }

    /**
     * Sets the password
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the password
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the timeout period
     * 
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the timeout period
     * 
     * @return timeout period
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the type of encrypted connection. 'None' is also an option
     * 
     * @param encryptedConnection
     */
    public void setEncryptedConnection(String encryptedConnection) {
        this.encryptedConnection = encryptedConnection;
    }

    /**
     * Returns the encrypted connection
     * 
     * @return encrypted connection
     */
    public String getEncryptedConnection() {
        return this.encryptedConnection;
    }

    /**
     * Sets the temporary email config object
     * 
     * @param emailConfig
     */
    public void setTemporaryEmailConfig(EmailConfig emailConfig) {
        this.temporaryEmailConfig = emailConfig;
    }

    /**
     * Returns the temporary email config object
     * 
     * @return temporary email configuration object
     */
    public EmailConfig getTemporaryEmailConfig() {
        return temporaryEmailConfig;
    }

    /**
     * Returns the information message
     * 
     * @return information message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the information message which describes whether the email server was added correctly or not
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the temporary email config object
     * 
     * @return temporary email configuration object
     */
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    /**
     * Sets the email config object which is used to populate JSP fields
     * 
     * @param emailConfig
     */
    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    /**
     * Returns the error type
     * 
     * @return error type
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Sets the error type
     * 
     * @param errorType
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
