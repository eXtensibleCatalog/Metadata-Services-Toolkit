/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.log;

import java.sql.Date;

/**
 * Represents a general log file used by the MST
 *
 * @author Eric Osiseka
 */
public class Log
{
	/**
	 * The log's ID
	 */
	private int id = -1;

	/**
	 * A counter tracking the number of warnings in the log
	 */
	private int warnings = 0;

	/**
	 * A counter tracking the number of errors in the log
	 */
	private int errors = 0;

	/**
	 * The timestamp when the log was last reset
	 */
	private Date lastLogReset = null;

	/**
	 * The name of the log file
	 */
	private String logFileName = null;
	
	/**
	 * The location of the log file
	 */
	private String logFileLocation = null;

	/**
	 * Gets the log's ID
	 *
	 * @return The log's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the log's ID
	 *
	 * @param id The log's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the number of warnings in the log
	 *
	 * @return The number of warnings in the log
	 */
	public int getWarnings()
	{
		return warnings;
	} // end method getWarnings()

	/**
	 * Sets the number of warnings in the log
	 *
	 * @param warnings The new number of warnings in the log
	 */
	public void setWarnings(int warnings)
	{
		this.warnings = warnings;
	} // end method setWarnings(int)

	/**
	 * Gets the number of errors in the log
	 *
	 * @return The number of errors in the log
	 */
	public int getErrors()
	{
		return errors;
	} // end method getErrors()

	/**
	 * Sets the number of errors in the log
	 *
	 * @param recordsReplaced The new number of errors in the log
	 */
	public void setErrors(int errors)
	{
		this.errors = errors;
	} // end method setErrors(int)

	/**
	 * Gets the date when the provider's logs were last reset
	 *
	 * @return The date when the provider's logs were last reset
	 */
	public Date getLastLogReset()
	{
		return lastLogReset;
	} // end method getLastLogReset()

	/**
	 * Sets the date when the log was last reset
	 *
	 * @param lastLogReset The new date when the log was last reset
	 */
	public void setLastLogReset(java.util.Date lastLogReset)
	{
		this.lastLogReset = (lastLogReset == null ? null : new Date(lastLogReset.getTime()));
	} // end method setLastLogReset(Date)

	/**
	 * Gets the name of the log file
	 *
	 * @return The name of the log file
	 */
	public String getLogFileName()
	{
		return logFileName;
	} // end method getLogFileName()

	/**
	 * Sets the name of the log file
	 *
	 * @param logFileName The new name of the log file
	 */
	public void setLogFileName(String logFileName)
	{
		this.logFileName = logFileName;
	} // end method setLogFileName(String)
	
	/**
	 * Gets the location of the log file
	 *
	 * @return The location of the log file
	 */
	public String getLogFileLocation()
	{
		return logFileLocation;
	} // end method getLogFileLocation()

	/**
	 * Sets the location of the log file
	 *
	 * @param logFileLocation The new location of the log file
	 */
	public void setLogFileLocation(String logFileLocation)
	{
		this.logFileLocation = logFileLocation;
	} // end method setLogFileLocation(String)

	
} // end class Log
