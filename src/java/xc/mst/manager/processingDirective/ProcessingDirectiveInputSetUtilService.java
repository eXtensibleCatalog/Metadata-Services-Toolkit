

package xc.mst.manager.processingDirective;

import java.util.List;

/**
 * Service class which associates processing directives with input formats
 *
 * @author Tejaswi Haramurali
 */
public interface ProcessingDirectiveInputSetUtilService
{
    /**
	 * Inserts a row in the database assigning a set as input to a processing directive.
	 *
	 * @param processingDirectiveId The ID of the processing directive the set is input for
	 * @param setId The ID of the set that is input for the processing directive
	 */
	public void insertPDIS(int processingDirectiveId, int setId);

	/**
	 * Deletes the row in the database assigning a set as input to a processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive the set is no longer input for
	 * @param setId The ID of the set that is no longer input for the processing directive
	 */
	public void deletePDIS(int processingDirectiveId, int setId);

	/**
	 * Gets the IDs of all sets which are input to the processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive whose input set IDs should be returned
	 * @return A list of set IDs for the sets that are input for the processing directive
	 */
	public List<Integer> getInputSetsForProcessingDirective(int processingDirectiveId);

	/**
	 * Deletes all input sets assignments for a processing directive
	 *
	 * @param processingDirectiveId The ID of the processing directive whose input sets should be removed
	 * @return True on success, false on failure
	 */
	public void deleteInputSetsForProcessingDirective(int processingDirectiveId);
}
