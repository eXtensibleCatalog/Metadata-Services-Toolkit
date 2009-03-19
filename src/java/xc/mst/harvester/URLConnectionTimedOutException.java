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
 *  Indicates a TimedURLConnection has timed out.
 *
 * @author    John Weatherley
 * @see       TimedURLConnection
 */
public class URLConnectionTimedOutException extends Exception {

	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 45678L;


	/**
	 *  Constructs a <code>URLConnectionTimedOutException</code> with no specified detail
	 *  message.
	 */
	public URLConnectionTimedOutException() {
		super();
	}


	/**
	 *  Constructs a <code>URLConnectionTimedOutException</code> with the specified detail
	 *  message.
	 *
	 * @param  message  the detailed message.
	 */
	public URLConnectionTimedOutException(String message) {
		super(message);
	}
}

