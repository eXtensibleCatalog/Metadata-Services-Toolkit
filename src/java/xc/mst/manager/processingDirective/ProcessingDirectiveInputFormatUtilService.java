/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.processingDirective;

import java.util.List;

/**
 * Interface which defines methods for associating processing directives with input formats
 *
 * @author Tejaswi Haramurali
 */
public interface ProcessingDirectiveInputFormatUtilService
{

    	/**
	 * Inserts a row in the database assigning a format as input to a processing directive.
	 *
	 * @param processingDirectiveId The ID of the processing directive the format is input for
	 * @param formatId The ID of the format that is input for the processing directive
	 */
	public void insertPDIF(int processingDirectiveId, int formatId);

	/**
	 * Deletes the row in the database assigning a format as input to a processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive the format is no longer input for
	 * @param formatId The ID of the format that is no longer input for the processing directive
	 * @return True on success, false on failure
	 */
	public void deletePDIF(int processingDirectiveId, int formatId);

	/**
	 * Gets the IDs of all formats which are input to the processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive whose input format IDs should be returned
	 * @return A list of format IDs for the formats that are input for the processing directive
	 */
	public  List<Integer> getInputFormatsForProcessingDirective(int processingDirectiveId);

	/**
	 * Deletes all input formats assignments for a processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive whose input formats should be removed
	 * @return True on success, false on failure
	 */
	public void deleteInputFormatsForProcessingDirective(int processingDirectiveId);
}
