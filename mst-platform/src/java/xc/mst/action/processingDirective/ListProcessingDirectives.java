/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;

/**
 * This class is used to display the list of processing directives that have been set up
 *
 * @author Tejaswi Haramurali
 */
public class ListProcessingDirectives extends BaseActionSupport
{
    /** Serial id */
	private static final long serialVersionUID = 8908002900698813282L;

    /** The list of processing directives that have been set up by the user */
    private List<ProcessingDirective> processingDirectives;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Error type */
	private String errorType; 

    /**
     * Overrides default implementation to list all directives
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
                     
           processingDirectives = getProcessingDirectiveService().getAllProcessingDirectives();
           setProcessingDirectives(processingDirectives);

           return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("listProcessingDirectivesError", "Unable to connect to the database. Database configuration may be incorrect.");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Sets the list of processing directives enabled by the user
     *
     * @param ProcessingDirectivesList list of processing directives
     */
    public void setProcessingDirectives(List<ProcessingDirective> processingDirectives)
    {
        this.processingDirectives = processingDirectives;
    }

    /**
     * Returns the list of processing directives enabled by the user
     *
     * @return list of processing directives
     */
    public List<ProcessingDirective> getProcessingDirectives()
    {
        return processingDirectives;
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
