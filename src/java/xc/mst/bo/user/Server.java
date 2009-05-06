/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.user;

/**
 * Represents a server which a user can authenticate against
 *
 * @author Eric Osisek
 */
public class Server
{
	/**
	 * The server's ID
	 */
	private int id = -1;

	/**
	 * The server's url
	 */
	private String url = null;

	/**
	 * The server's name
	 */
	private String name = null;

	/**
	 * The server's port
	 */
	private int port = 0;

	/**
	 * The user name attribute used to authenticate against the server
	 */
	private String usernameAttribute = null;

	/**
	 * The start location of an LDAP server
	 */
	private String startLocation = null;

	/**
	 * The server's type
	 */
	private ServerType type = ServerType.UNDEFINED;

	/**
	 * The server's institution
	 */
	private String institution = null;

	/**
	 * The URL of the server's forgot password page
	 */
	private String forgotPasswordUrl = null;

	/**
	 * The label on the URL of the server's forgot password page
	 */
	private String forgotPasswordLabel = null;

	/**
	 * Whether or not the user should be shown the server's forgot password link
	 */
	private boolean showForgotPasswordLink = false;

	/**
	 * Enumeration for the different types of servers the MST handles
	 */
	public enum ServerType { UNDEFINED, LDAP, NCIP, LOCAL }

	/**
	 * Gets the server's ID
	 *
	 * @return The server's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the server's ID
	 *
	 * @param id The server's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the server's port
	 *
	 * @return The server's port
	 */
	public int getPort()
	{
		return port;
	} // end method getPort()

	/**
	 * Sets the server's port
	 *
	 * @param port The server's new port
	 */
	public void setPort(int port)
	{
		this.port = port;
	} // end method setPort(int)

	/**
	 * Gets the server's user name attribute
	 *
	 * @return The server's user name attribute
	 */
	public String getUserNameAttribute()
	{
		return usernameAttribute;
	} // end method getUserNameAttribute()

	/**
	 * Sets the server's user name attribute
	 *
	 * @param usernameAttribute The server's new user name attribute
	 */
	public void setUserNameAttribute(String usernameAttribute)
	{
		this.usernameAttribute = usernameAttribute;
	} // end method setUserNameAttribute(String)

	/**
	 * Gets the server's start location.  Servers which are not LDAP servers have a null start location.
	 *
	 * @return The server's start location
	 */
	public String getStartLocation()
	{
		return startLocation;
	} // end method getStartLocation()

	/**
	 * Sets the server's start location.  Servers which are not LDAP servers must not have a start location
	 *
	 * @param startLocation The server's new start location
	 */
	public void setStartLocation(String startLocation)
	{
		this.startLocation = startLocation;
	} // end method setStartLocation(String)

    /**
	 * Gets the server's URL
	 *
	 * @return The server's URL
	 */
    public String getUrl()
	{
		return url;
	} // end method getUrl()

	/**
	 * Sets the server's URL
	 *
	 * @param url The server's new URL
	 */
	public void setUrl(String url)
	{
		this.url = url;
	} // end method setUrl(String)

	/**
	 * Gets the server's name
	 *
	 * @return The server's name
	 */
	public String getName()
	{
		return name;
	} // end method getName()

	/**
	 * Sets the server's name
	 *
	 * @param name The server's new name
	 */
	public void setName(String name)
	{
		this.name = name;
	} // end method setName(String)

	/**
	 * Gets the server's type
	 *
	 * @return The server's type
	 */
	public ServerType getType()
	{
		return type;
	} // end method getType()

	/**
	 * Sets the server's type.
	 *
	 * @param type The server's new type.  The passed value should be one of the TYPE constants.
	 */
	public void setType(ServerType type)
	{
		this.type = type;
	} // end method setType(ServerType)

	/**
	 * Gets the server's institution
	 *
	 * @return The server's institution
	 */
	public String getInstitution()
	{
		return institution;
	} // end method getInstitution()

	/**
	 * Sets the server's institution
	 *
	 * @param institution The server's institution
	 */
	public void setInstitution(String institution)
	{
		this.institution = institution;
	} // end method setInstitution(String)

	/**
	 * Gets the URL of the server's forgot password page
	 *
	 * @return The the URL of the server's forgot password page
	 */
	public String getForgotPasswordUrl()
	{
		return forgotPasswordUrl;
	} // end method getForgotPasswordUrl()

	/**
	 * Sets the URL of the server's forgot password page
	 *
	 * @param forgotPasswordUrl The new URL of the server's forgot password page
	 */
	public void setForgotPasswordUrl(String forgotPasswordUrl)
	{
		this.forgotPasswordUrl = forgotPasswordUrl;
	} // end method setForgotPasswordUrl(String)

	/**
	 * Gets the label of the server's forgot password page
	 *
	 * @return The label of the server's forgot password page
	 */
	public String getForgotPasswordLabel()
	{
		return forgotPasswordLabel;
	} // end method getForgotPasswordLabel()

	/**
	 * Sets the label of the server's forgot password page
	 *
	 * @param forgotPasswordLabel The new label of the server's forgot password page
	 */
	public void setForgotPasswordLabel(String forgotPasswordLabel)
	{
		this.forgotPasswordLabel = forgotPasswordLabel;
	} // end method setForgotPasswordlabel(String)

	/**
	 * Gets whether or not the user should be shown the server's forgot password link
	 *
	 * @return Whether or not the user should be shown the server's forgot password link
	 */
	public boolean getShowForgotPasswordLink()
	{
		return showForgotPasswordLink;
	} // end method getShowForgotPasswordLink()

	/**
	 * Sets whether or not the user should be shown the server's forgot password link
	 *
	 * @param showForgotPasswordLink Whether or not the user should be shown the server's forgot password link
	 */
	public void setShowForgotPasswordLink(boolean showForgotPasswordLink)
	{
		this.showForgotPasswordLink = showForgotPasswordLink;
	} // end method setShowForgotPasswordLink(boolean)
} // end class Server
