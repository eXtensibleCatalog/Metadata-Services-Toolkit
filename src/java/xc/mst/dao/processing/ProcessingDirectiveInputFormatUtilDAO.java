/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.processing;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the formats that trigger a processing directive
 *
 * @author Eric Osisek
 */
public abstract class ProcessingDirectiveInputFormatUtilDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * The Object managing the database connection
	 */
	protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

	/**
	 * The name of the processing directives to input formats database table
	 */
	public final static String PROCESSING_DIRECTIVES_TO_INPUT_FORMATS_TABLE_NAME = "processing_directives_to_input_formats";

	/**
	 * The name of the processing directive ID column
	 */
	public final static String COL_PROCESSING_DIRECTIVE_ID = "processing_directive_id";

	/**
	 * The name of the format ID column
	 */
	public final static String COL_FORMAT_ID = "format_id";

	/**
	 * Inserts a row in the database assigning a format as input to a processing directive.
	 *
	 * @param processingDirectiveId The ID of the processing directive the format is input for
	 * @param formatId The ID of the format that is input for the processing directive
	 * @return True on success, false on failure
	 */
	public abstract boolean insert(int processingDirectiveId, int formatId);

	/**
	 * Deletes the row in the database assigning a format as input to a processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive the format is no longer input for
	 * @param formatId The ID of the format that is no longer input for the processing directive
	 * @return True on success, false on failure
	 */
	public abstract boolean delete(int processingDirectiveId, int formatId);

	/**
	 * Gets the IDs of all formats which are input to the processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive whose input format IDs should be returned
	 * @return A list of format IDs for the formats that are input for the processing directive
	 */
	public abstract List<Integer> getInputFormatsForProcessingDirective(int processingDirectiveId);

	/**
	 * Deletes all input formats assignments for a processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive whose input formats should be removed
	 * @return True on success, false on failure
	 */
	public abstract boolean deleteInputFormatsForProcessingDirective(int processingDirectiveId);
} // end class ProcessingDirectiveInputFormatUtilDAO
