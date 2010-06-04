/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.index.SolrIndexManager;

/**
 * Action class for optimizing Solr index
 *
 * @author Sharmila Ranganathan
 *
 */
public class IndexOptimization extends BaseActionSupport  {

	/** Generated id  */
	private static final long serialVersionUID = 1316812472174881465L;

    /** Server selected */
    private String message;

	/** Error type */
	private String errorType;
	
	
    /**
     * Overriding default implementation to login the user.
     *
     * @return {@link #SUCCESS}
     */
    @Override
	public String execute() throws DataException {
    	
    	try {
    		((SolrIndexManager)MSTConfiguration.getInstance().getBean("SolrIndexManager")).optimizeIndex();
    	}  catch (IndexException ie) {
        	this.addFieldError("optimizeError", "Optimization of Solr index failed due to some problem.");
            errorType = "error";
            return SUCCESS;
    	}

    	message = "Solr index optimized successfully!";
    	
       	return SUCCESS;
    }


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getErrorType() {
		return errorType;
	}


	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}


}
