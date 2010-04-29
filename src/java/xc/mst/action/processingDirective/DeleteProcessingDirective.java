

/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;

/**
 * This action method deletes a Processing Directive
 *
 * @author Tejaswi Haramurali
 */
public class DeleteProcessingDirective extends BaseActionSupport
{
	 /** Serial ID*/
	private static final long serialVersionUID = -3203721703516486193L;

	/** The ID of the Processing Directive to be deleted */
    private int processingDirectiveId;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Error type */
	private String errorType;

    /**
     * Sets the Processing directive ID
     *
     * @param processingDirectiveId Processing Directive ID
     */
    public void setProcessingDirectiveId(int processingDirectiveId)
    {

        this.processingDirectiveId = processingDirectiveId;
    }

    /**
     * Returns the ID of the processing directive
     *
     * @return processing directive ID
     */
    public int getProcessingDirectiveId()
    {
        return processingDirectiveId;
    }

    /**
     * Overrides default implementation to delete a processing directive.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            ProcessingDirective tempProcDir = getProcessingDirectiveService().getByProcessingDirectiveId(processingDirectiveId);
            if(tempProcDir==null)
            {
                this.addFieldError("DeleteDirectiveError", "Error Deleting Processing Rule. An email has been sent to the administrator.");
                getUserService().sendEmailErrorReport();
                errorType = "error";
                return SUCCESS;
            }
            getProcessingDirectiveService().deleteProcessingDirective(tempProcDir);
            return SUCCESS;
        }
        catch(DatabaseConfigException e)
        {
            log.error("Deletion of processing rule Unsuccessful",e);
            this.addFieldError("listServicesError", "Deletion of processing rule Unsuccessful");
            errorType = "error";
            return SUCCESS;
        }
    }

    /**
     * Returns error type
     *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets error type
     * 
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
