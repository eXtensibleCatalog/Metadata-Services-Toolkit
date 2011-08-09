/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  */

package xc.mst.bo.service;

/**
 * Represents an error code used by a service in the MST
 *
 * @author Eric Osisek
 */
public class ErrorCode 
{
	/**
	 * The ID of the error code in the database
	 */
	private int errorCodeId = -1;
	
	/**
	 * The error code of the error
	 */
	private String errorCode = null;
	
	/**
	 * The file containing a description of the error described by the error code
	 */
	private String errorDescriptionFile = null;
	
	/**
	 * The service which defined the error
	 */
	private Service service = null;

	/**
	 * Gets the ID of the error code in the database
	 * 
	 * @return The ID of the error code in the database
	 */
	public int getId() 
	{
		return errorCodeId;
	}

	/**
	 * Gets the error code of the error
	 * 
	 * @return The error code of the error
	 */
	public String getErrorCode() 
	{
		return errorCode;
	}

	/**
	 * Gets the file containing a description of the error described by the error code
	 * 
	 * @return The file containing a description of the error described by the error code
	 */
	public String getErrorDescriptionFile() 
	{
		return errorDescriptionFile;
	}

	/**
	 * Gets the service which defined the error
	 * 
	 * @return The new service which defined the error
	 */
	public Service getService() 
	{
		return service;
	}

	/**
	 * Sets the ID of the error code in the database
	 * 
	 * @param errorCodeId The new ID of the error code in the database
	 */
	public void setId(int errorCodeId) 
	{
		this.errorCodeId = errorCodeId;
	}

	/**
	 * Sets the error code of the error
	 * 
	 * @param errorCode The new error code of the error
	 */
	public void setErrorCode(String errorCode) 
	{
		this.errorCode = errorCode;
	}

	/**
	 * Sets the file containing a description of the error described by the error code
	 * 
	 * @param errorDescriptionFile The new file containing a description of the error described by the error code
	 */
	public void setErrorDescriptionFile(String errorDescriptionFile) 
	{
		this.errorDescriptionFile = errorDescriptionFile;
	}

	/**
	 * Sets the service which defined the error
	 * 
	 * @param service The new service which defined the error
	 */
	public void setService(Service service) 
	{
		this.service = service;
	}
}
