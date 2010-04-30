/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.harvester;

/**
 *  Indicates an error occured during a harvest.
 *
 * @author    John Weatherley
 @see Harvester
 */
public class Hexception extends Exception {

	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 23456L;

	/**
	 *  Constructor for the Hexception object
	 *
	 * @param  msg  The error message.
	 */
	public Hexception(String msg) {
		super(msg);
	}

}

