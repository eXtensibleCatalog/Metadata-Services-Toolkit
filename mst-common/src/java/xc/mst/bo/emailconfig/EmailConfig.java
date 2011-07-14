/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.emailconfig;

/**
 * Represents the SMTP server configuration for sending email from the MST.
 *
 * @author Shreyansh Vakil
 */
public class EmailConfig
{
	/**
	 * The email configuration's ID
	 */
	private int emailConfigId = -1;

	/**
	 * The email configuration's SMTP server address
	 */
	private String emailServerAddress = null;

	/**
	 * The email configuration's SMTP server port
	 */
	private int portNumber = -1;

	/**
	 * The email address on the SMTP server from which the MST should send
	 * email notifications according to the email configuration
	 */
	private String fromAddress = null;

	/**
	 * The password for the email address on the SMTP server from which the MST should send
	 * email notifications according to the email configuration
	 */
	private String password = null;

	/**
	 * The type of encrypted connection used by the SMTP server
	 * according to the email configuration
	 */
	private String encryptedConnection = null;

	/**
	 * The email configuration's timeout
	 */
	private long timeout = -1;

	/**
	 * The presence of the forgotten password link for the email configuration screen
	 */
	private boolean forgottenPasswordLink = false;

	/**
	 * Gets the email configuration's ID
	 *
	 * @return The email configuration's ID
	 */
	public int getEmailConfigId()
	{
		return emailConfigId;
	} // end method getEmailConfigId()

	/**
	 * Sets the email configuration's ID
	 *
	 * @param emailConfigId The email configuration's new ID
	 */
	public void setEmailConfigId(int emailConfigId)
	{
		this.emailConfigId = emailConfigId;
	} // end method setEmailConfigId(int)

	/**
	 * Gets the email configuration's SMTP server address
	 *
	 * @return The email configuration's SMTP server address
	 */
	public String getEmailServerAddress()
	{
		return emailServerAddress;
	} // end method getEmailServerAddress()

	/**
	 * Sets the email configuration's SMTP server address
	 *
	 * @param emailServerAddress The email configuration's new SMTP server address
	 */
	public void setEmailServerAddress(String emailServerAddress)
	{
		this.emailServerAddress = emailServerAddress;
	} // end method setEmailServerAddress(String)

	/**
	 * Gets the email configuration's SMTP server port
	 *
	 * @return The email configuration's SMTP server port
	 */
	public int getPortNumber()
	{
		return portNumber;
	} // end method getPortNumber()

	/**
	 * Sets the email configuration's SMTP server port
	 *
	 * @param portNumber The email configuration's new SMTP server port
	 */
	public void setPortNumber(int portNumber)
	{
		this.portNumber = portNumber;
	} // end method setPortNumber(int)

	/**
	 * Gets the email address on the SMTP server from which the MST should send
	 * email notifications according to the email configuration.
	 *
	 * @return fromAddress The email address on the SMTP server from which the MST should send
	 *                     email notifications according to the email configuration
	 */
	public String getFromAddress()
	{
		return fromAddress;
	} // end method getFromAddress()

	/**
	 * Sets the email address on the SMTP server from which the MST should send
	 * email notifications according to the email configuration
	 *
	 * @param fromAddress The new email address on the SMTP server from which the MST should send
	 *                    email notifications according to the email configuration
	 */
	public void setFromAddress(String fromAddress)
	{
		this.fromAddress = fromAddress;
	} // end method setFromAddress(String)

	/**
	 * Gets the password for the email address on the SMTP server from which the MST should send
	 * email notifications according to the email configuration
	 *
	 * @return The password for the email address on the SMTP server from which the MST should send
	 *         email notifications according to the email configuration
	 */
	public String getPassword()
	{
		return password;
	} // end method getPassword()

	/**
	 * Sets the password for the email address on the SMTP server from which the MST should send
	 * email notifications according to the email configuration
	 *
	 * @param password The new password for the email address on the SMTP server from which the MST should send
	 *                 email notifications according to the email configuration
	 */
	public void setPassword(String password)
	{
		this.password = password;
	} // end method setPassword(String)

	/**
	 * Gets the type of encrypted connection used by the SMTP server
	 * according to the email configuration
	 *
	 * @return encryptedConnection The type of encrypted connection used by the SMTP server
	 *                             according to the email configuration.
	 */
	public String getEncryptedConnection()
	{
		return encryptedConnection;
	} // end method getEncryptedConnection()

	/**
	 * Sets the type of encrypted connection used by the SMTP server
	 * according to the email configuration
	 *
	 * @param encryptedConnection The new type of encrypted connection used by the SMTP server
	 *                            according to the email configuration
	 */
	public void setEncryptedConnection(String encryptedConnection)
	{
		this.encryptedConnection = encryptedConnection;
	} // end method setEncryptedConnection(String)

	/**
	 * Gets the email configuration's timeout in ms.
	 *
	 * @return timeout The email configuration's timeout in ms.
	 */
	public long getTimeout()
	{
		return timeout;
	} // end method getTimeout()

	/**
	 * Sets the email configuration's timeout in ms
	 *
	 * @param timeout The email configuration's new timeout in ms
	 */
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	} // end method setTimeout(long)

	/**
	 * Gets the presence of the forgotten password link for the email configuration screen
	 *
	 * @return The presence of the forgotten password link for the email configuration screen
	 */
	public boolean getForgottenPasswordLink()
	{
		return forgottenPasswordLink;
	} // end method getForgottenPasswordLink()

	/**
	 * Sets the presence of the forgotten password link for the email configuration screen
	 *
	 * @param forgottenPasswordLink True iff the forgotten password link for the SMTP server
	 *                              should be displayed.
	 */
	public void setForgottenPasswordLink(boolean forgottenPasswordLink)
	{
		this.forgottenPasswordLink = forgottenPasswordLink;
	} // end method setForgottenPasswordLink(boolean)
} // end Class EmailConfig