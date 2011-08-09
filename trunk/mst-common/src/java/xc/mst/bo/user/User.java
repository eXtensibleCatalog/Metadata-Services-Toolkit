/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user with an account on the MST
 *
 * @author Eric Osisek
 */
public class User
{
	/**
	 * The user's ID
	 */
	private int id = -1;

	/**
	 * The user's username
	 */
	private String username = null;

	/**
	 * The user's first name
	 */
	private String firstName = null;

	/**
	 * The user's last name
	 */
	private String lastName = null;
	
	/**
	 * The user's password
	 */
	private String password = null;

	/**
	 * The user's email
	 */
	private String email = null;

	/**
	 * The Server to which the user belongs
	 */
	private Server server = null;

	/**
	 * The date and time when the user last logged into the MST
	 */
	private Date lastLogin = null;

	/**
	 * The date and time when the user's account was created
	 */
	private Date accountCreated = null;

	/**
	 * The number of times the user made an unsuccessful attempt to log into the MST
	 */
	private int failedLoginAttempts = -1;

	/**
	 * A list of the groups the user belongs to
	 */
	private List<Group> groups = new ArrayList<Group>();

	/**
	 * Gets the value of the user ID
	 *
	 * @return The user's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets user's ID
	 *
	 * @param id The user's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the user's username
	 *
	 * @return The user's username
	 */
	public String getUsername()
	{
		return username;
	} // end method getUsername()

	/**
	 * Sets the user's username
	 *
	 * @param username The user's new username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	} // end method setUsername(String)

	/**
	 * Gets the user's first name
	 *
	 * @return The user's first name
	 */
	public String getFirstName()
	{
		return firstName;
	} // end method getFirstName()

	/**
	 * Sets the user's first name
	 *
	 * @param firstName The user's new first name
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	} // end method setFirstName(String)

	/**
	 * Gets the user's last name
	 *
	 * @return The user's last name
	 */
	public String getLastName()
	{
		return lastName;
	} // end method getLastName()

	/**
	 * Sets the user's last name
	 *
	 * @param lastName The user's new last name
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	} // end method setLastName(String)
	
	/**
	 * Gets the user's password
	 *
	 * @return The user's password
	 */
	public String getPassword()
	{
		return password;
	} // end method getPassword()

	/**
	 * Sets the user's password
	 *
	 * @param password The user's new password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	} // end method setPassword(String)

	/**
	 * Gets the user's email
	 *
	 * @return The user's email
	 */
	public String getEmail()
	{
		return email;
	} // end method getEmail()

	/**
	 * Sets the user's email
	 *
	 * @param email The user's new email
	 */
	public void setEmail(String email)
	{
		this.email = email;
	} // end method setEmail(String)

	/**
	 * Gets the server the user should be authenticated against
	 *
	 * @return The server the user should be authenticated against
	 */
	public Server getServer()
	{
		return server;
	} // end method getServer()

	/**
	 * Sets the server the user should be authenticated against
	 *
	 * @param server The new server the user should be authenticated against
	 */
	public void setServer(Server server)
	{
		this.server = server;
	} // end method setServer(Server)

	/**
	 * Gets the date and time when the user last logged into the MST
	 *
	 * @return The date and time when the user last logged into the MST
	 */
	public Date getLastLogin()
	{
		return lastLogin;
	} // end method getLastLogin()

	/**
	 * Sets the date and time when the user last logged into the MST
	 *
	 * @param lastLogin The new date and time when the user last logged into the MST
	 */
	public void setLastLogin(java.util.Date lastLogin)
	{
		if(lastLogin == null)
			return;

		this.lastLogin = new Date(lastLogin.getTime());
	} // end method setLastLogin(Date)

    /**
	 * Gets the date and time when the user's account was created
	 *
	 * @return The date and time when the user's account was created
	 */
	public Date getAccountCreated()
	{
		return accountCreated;
	} // end method getAccountCreated()

	/**
	 * Sets the date and time when the user's account was created
	 *
	 * @param accountCreated The new date and time when the user's account was created
	 */
	public void setAccountCreated(java.util.Date accountCreated)
	{
		if(accountCreated == null)
			return;

		this.accountCreated = new Date(accountCreated.getTime());
	} // end method setAccountCreated(Date)

	/**
	 * Gets the number of failed attempts to login to this user's account
	 *
	 * @return The number of failed attempts to login to this user's account
	 */
	public int getFailedLoginAttempts()
	{
		return failedLoginAttempts;
	} // end method getFailedLoginAttempts()

	/**
	 * Sets the number of failed attempts to login to this user's account
	 *
	 * @param failedLoginAttempts The new number of failed attempts to login to this user's account
	 */
	public void setFailedLoginAttempts(int failedLoginAttempts)
	{
		this.failedLoginAttempts = failedLoginAttempts;
	} // end method setFailedLoginAttempts(int)

	/**
	 * Gets the groups belonging to the group
	 *
	 * @return The group's groups
	 */
	public List<Group> getGroups()
	{
		return groups;
	} // end method getGroups()

	/**
	 * Sets the groups belonging to the group
	 *
	 * @param groups A list of groups for the group
	 */
	public void setGroups(List<Group> groups)
	{
		this.groups = groups;
	} // end method setGroups(List<Group>)

	/**
	 * Adds a group to the list of groups belonging to the group
	 *
	 * @param group The group to add
	 */
	public void addGroup(Group group)
	{
		if(!groups.contains(group))
			groups.add(group);
	} // end method addGroup

	/**
	 * Removes a group from the list of groups belonging to the group
	 *
	 * @param group The group to remove
	 */
	public void removeGroup(Group group)
	{
		if(groups.contains(group))
			groups.remove(group);
	} // end method removeGroup
	
	/**
	 * Removes all groups
	 */
	public void removeAllGroups()
	{
		groups.clear();
	} // end method removeAllGroups()
} // end class User
