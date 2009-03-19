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
 *  Indicates an <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions">
 *  OAI protocol error code</a> was returned by the data provider during a harvest.
 *
 * @author    John Weatherley
 */
public class OAIErrorException extends Exception {

	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 34567L;

	String errorCode = null;
	String errorMsg = null;


	/**
	 *  Constructor for the OAIErrorException object
	 *
	 * @param  errorCode  The OAI error code.
	 * @param  errorMsg   Description of the error.
	 */
	public OAIErrorException(String errorCode, String errorMsg) {
		super("OAI code '" + errorCode + "' was returned by the data provider. Message: " + errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}


	/**
	 *  Gets the oAIErrorCode attribute of the OAIErrorException object
	 *
	 * @return    The oAIErrorCode value
	 */
	public String getOAIErrorCode() {
		return errorCode;
	}


	/**
	 *  Gets the oAIErrorMessage attribute of the OAIErrorException object
	 *
	 * @return    The oAIErrorMessage value
	 */
	public String getOAIErrorMessage() {
		return errorMsg;
	}

}

