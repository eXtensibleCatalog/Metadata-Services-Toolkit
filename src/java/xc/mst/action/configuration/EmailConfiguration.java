
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.configuration;

import com.opensymphony.xwork2.ActionSupport;
import xc.mst.bo.emailconfig.EmailConfig;
import xc.mst.manager.configuration.DefaultEmailConfigService;
import xc.mst.manager.configuration.EmailConfigService;

/**
 *  The action method that is used to add/edit an email Server
 *
 * @author Tejaswi Haramurali
 */
public class EmailConfiguration extends ActionSupport
{
    /**Creates a service Object for Email Configuration */
    private EmailConfigService emailConfigService = new DefaultEmailConfigService();

    /** The temporary emailConfig object that is used to populate JSP page fields **/
    private EmailConfig temporaryEmailConfig;

    /**The URL of the email server **/
    private String emailServerAddress;

    /**The from address **/
    private String fromAddress;

    /**The port number of the email server **/
    private String port;

    /**The password for the email server **/
    private String password;

    /**The timeout period **/
    private String timeout;

    /**The type of encrypted connection (can also ne 'none') **/
    private String encryptedConnection;

    /** Information message that describes whether the email server was added correctly */
    private String message;

    private EmailConfig emailConfig = new EmailConfig();

    /**
     * sets the URL address of the email server
     * @param emailServerAddress Server Address
     */
    public void setEmailServerAddress(String emailServerAddress)
    {
        this.emailServerAddress = emailServerAddress.trim();
    }

    /**
     * gets the URL address of the server
     * @return URL address of the server
     */
    public String getEmailServerAddress()
    {
        return emailServerAddress;
    }

    /**
     * sets the from address
     * @param fromAddress from address
     */
    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress.trim();
    }

    /**
     * returns the from address
     * @return from address
     */
    public String getFromAddress()
    {
        return fromAddress;
    }

    /**
     * sets the port of the email server
     * @param port port number of the email server
     */
    public void setPort(String port)
    {
        
        this.port = port;
    }
    /**
     * returns the port number of the email server
     * @return port number
     */
    public String getPort()
    {
       
        return port;
    }
    /**
     * sets the password
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    /**
     * returns the password
     * @return password
     */
    public String getPassword()
    {
        return password;
    }
    /**
     * sets the timeout period
     * @param timeout
     */
    public void setTimeout(String timeout)
    {
        this.timeout = timeout;
    }
    /**
     * returns the timeout period
     * @return
     */
    public String getTimeout()
    {
        return timeout;
    }
    /**
     * sets the type of encrypted connection. 'None' is also an option
     * @param encryptedConnection The value of the encrypted connection
     */
    public void setEncryptedConnection(String encryptedConnection)
    {
        this.encryptedConnection = encryptedConnection;
    }
    /**
     * returns the encrypted connection
     * @return encrypted connection value
     */
    public String getEncryptedConnection()
    {
        return this.encryptedConnection;
    }

    /**
     * sets the temporary email config object
     * @param emailConfig temporary email config object
     */
    public void setTemporaryEmailConfig(EmailConfig emailConfig)
    {
        this.temporaryEmailConfig = emailConfig;
    }
    /**
     * returns the temporary email config object
     * @return temporary email config object
     */
    public EmailConfig getTemporaryEmailConfig()
    {
        return temporaryEmailConfig;
    }

     /**
     * Overrides default implementation to view the email server configuration page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            
            emailConfig = emailConfigService.getEmailConfiguration();
//            if(emailConfig!=null)
//            {
//                setTemporaryEmailConfig(emailConfig);
//            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("viewEmailConfigError", "Error : Problem displaying the Email Configuration Page");
            return INPUT;
        }
    }

    /**
     * Method that changes the details of the email server
     * @return {@link #SUCCESS}
     */
    public String changeEmailConfig()
    {
        try
        {

            
            emailConfig.setEmailServerAddress(emailServerAddress);
            emailConfig.setEncryptedConnection(encryptedConnection);

            System.out.println("The from address for the email is "+fromAddress);
            emailConfig.setFromAddress(fromAddress);
            emailConfig.setPassword(password);
            System.out.println("Inside main change method, port number is "+port);
            emailConfig.setPortNumber(Integer.parseInt(port));
            emailConfig.setTimeout(Integer.parseInt(timeout));
            emailConfigService.setEmailConfiguration(emailConfig);
            message = "Information Saved.";
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("changeEmailConfigError", fromAddress);
            return INPUT;
        }
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EmailConfig getEmailConfig() {
		return emailConfig;
	}

	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}
}
