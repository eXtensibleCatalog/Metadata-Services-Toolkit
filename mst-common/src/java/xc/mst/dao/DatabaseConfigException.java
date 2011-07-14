/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao;

/**
 * This exception is thrown by a class implementing DatabaseObject when it was
 * unable to perform a requested action against the database
 *
 * @author Eric Osisek
 */
public class DatabaseConfigException extends DataException
{
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 1132345L;

	/**
	 * Constructs a new DataException with null as its detail message.
	 */
	public DatabaseConfigException()
	{
		super();
	} // end constructor()

	/**
	 * Constructs a new DataException with the specified detail message.
	 *
	 * @param detail A possibly null string containing details of the exception.
	 */
	public DatabaseConfigException(String detail)
	{
		super(detail);
	} // end constructor(String)
} // end class DatabaseConfigException
