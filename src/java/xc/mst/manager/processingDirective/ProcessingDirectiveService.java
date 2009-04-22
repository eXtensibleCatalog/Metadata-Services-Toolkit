
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

import xc.mst.bo.processing.ProcessingDirective;

/**
 * Interface used for creation/deletion/updating Processing Directives.
 *
 * @author Tejaswi Haramurali
 */
public interface ProcessingDirectiveService {

    /**
     * Returns a processing directive based on the ID of the source provider
     *
     * @return processing directive
     */
    public List<ProcessingDirective> getBySourceProviderId(int providerId);

    /**
     * Returns a processing directive based on the ID of the source service
     *
     * @return processing directive
     */
    public List<ProcessingDirective> getBySourceServiceId(int serviceId);

    /**
     * Returns a processing directive based on the ID
     *
     * @param processingDirectiveId The ID based on which the processing Directive object is retrieved
     * @return
     */
    public ProcessingDirective getByProcessingDirectiveId(int processingDirectiveId);

    /**
     * Returns list of processing directives
     *
     * @return list of processing directives
     */
    public List<ProcessingDirective> getAllProcessingDirectives();

    /**
     * Inserts a new Processing Directive
     *
     * @param processingDirective The processing Directive Object to be inserted
     */
    public void insertProcessingDirective(ProcessingDirective processingDirective);

    /**
     * Deletes a processing directive
     *
     * @param processingDirective The processingDirective Object to be deleted
     */
    public void deleteProcessingDirective(ProcessingDirective processingDirective);

    /**
     * Updates a processing directive
     * 
     * @param processingDirective The processing directive to be updated
     */
    public void updateProcessingDirective(ProcessingDirective processingDirective);
}
