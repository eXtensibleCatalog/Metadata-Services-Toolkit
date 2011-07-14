/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.scheduling;

/**
 * This exception is thrown when the HarvestScheduler tries to schedule a harvest
 * with invalid parameters
 *
 * @author Eric Osisek
 */
public class SchedulingException extends Exception
{
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 67891L;

	/**
	 * Constructs a new SchedulingException with null as its detail message.
	 */
	public SchedulingException()
	{
		super();
	} // end constructor()

	/**
	 * Constructs a new SchedulingException with the specified detail message.
	 *
	 * @param detail A possibly null string containing details of the exception.
	 */
	public SchedulingException(String detail)
	{
		super(detail);
	} // end constructor(String)
} // end class SchedulingException
