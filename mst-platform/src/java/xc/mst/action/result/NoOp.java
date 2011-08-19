/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.result;

import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.opensymphony.xwork2.ActionInvocation;


/**
 * Action that does nothing becuase the action writes output to
 * the stream.
 *
 */
public class NoOp extends StrutsResultSupport {

    /**
     * Eclipse generated id.
     */
    private static final long serialVersionUID = 5057577751602240162L;


    public NoOp() {
        super();
    }

    /**
     * @see org.apache.struts2.dispatcher.StrutsResultSupport#doExecute(java.lang.String, com.opensymphony.xwork2.ActionInvocation)
     */
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        //no op
    }

}
