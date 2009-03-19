

/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;

/**
 * This action method deletes a Processing Directive
 *
 * @author Tejaswi Haramurali
 */
public class DeleteProcessingDirective extends ActionSupport
{
    /** The ID of the Processing Directive to be deleted */
    private int processingDirectiveId;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * sets the Processing directive ID
     * @param processingDirectiveId Processing Directive ID
     */
    public void setProcessingDirectiveId(int processingDirectiveId)
    {

        this.processingDirectiveId = processingDirectiveId;
    }

    /**
     * returns the ID of the processing directive
     * @return processing directive ID
     */
    public int getProcessingDirectiveId()
    {
        return processingDirectiveId;
    }

    /**
     * Overrides default implementation to delete a processing directive.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            ProcessingDirectiveService processingDirectiveService = new DefaultProcessingDirectiveService();
            processingDirectiveService.deleteProcessingDirective(processingDirectiveService.getByProcessingDirectiveId(processingDirectiveId));
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            this.addFieldError("listServicesError", "Delete Unsuccessful");
            return SUCCESS;
        }
    }
}
