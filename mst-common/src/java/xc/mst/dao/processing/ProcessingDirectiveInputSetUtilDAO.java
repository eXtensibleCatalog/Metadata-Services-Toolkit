/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the sets that trigger a processing directive
 *
 * @author Eric Osisek
 */
public abstract class ProcessingDirectiveInputSetUtilDAO extends BaseDAO
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
     * The name of the processing directives to input sets database table
     */
    public final static String PROCESSING_DIRECTIVES_TO_INPUT_SETS_TABLE_NAME = "processing_directives_to_input_sets";

    /**
     * The name of the processing directive ID column
     */
    public final static String COL_PROCESSING_DIRECTIVE_ID = "processing_directive_id";

    /**
     * The name of the set ID column
     */
    public final static String COL_SET_ID = "set_id";

    /**
     * Inserts a row in the database assigning a set as input to a processing directive.
     *
     * @param processingDirectiveId The ID of the processing directive the set is input for
     * @param setId The ID of the set that is input for the processing directive
     * @return True on success, false on failure
     */
    public abstract boolean insert(int processingDirectiveId, int setId);

    /**
     * Deletes the row in the database assigning a set as input to a processing directive
     *
     * @param processingDirectiveId The ID of the processing directive the set is no longer input for
     * @param setId The ID of the set that is no longer input for the processing directive
     * @return True on success, false on failure
     */
    public abstract boolean delete(int processingDirectiveId, int setId);

    /**
     * Gets the IDs of all sets which are input to the processing directive
     *
     * @param processingDirectiveId The ID of the processing directive whose input set IDs should be returned
     * @return A list of set IDs for the sets that are input for the processing directive
     */
    public abstract List<Integer> getInputSetsForProcessingDirective(int processingDirectiveId);

    /**
     * Deletes all input sets assignments for a processing directive
     *
     * @param processingDirectiveId The ID of the processing directive whose input sets should be removed
     * @return True on success, false on failure
     */
    public abstract boolean deleteInputSetsForProcessingDirective(int processingDirectiveId);
} // end class ProcessingDirectiveInputSetUtilDAO
