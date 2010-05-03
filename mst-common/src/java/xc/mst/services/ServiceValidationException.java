/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services;

/**
 * This exception is thrown when a metadata service is found to be invalid
 *
 * @author Eric Osisek
 */
public class ServiceValidationException extends Exception
{
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 67891L;

	/**
	 * Constructs a new ServiceValidationException with null as its detail message.
	 */
	public ServiceValidationException()
	{
		super();
	} // end constructor()

	/**
	 * Constructs a new ServiceValidationException with the specified detail message.
	 *
	 * @param detail A possibly null string containing details of the exception.
	 */
	public ServiceValidationException(String detail)
	{
		super(detail);
	} // end constructor(String)
} // end class ServiceValidationException
