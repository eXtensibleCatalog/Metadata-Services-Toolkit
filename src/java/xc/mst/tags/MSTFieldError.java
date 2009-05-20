/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.*;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;


/**
 * Tag to display Field Errors in the JSP
 *
 * @author Tejaswi Haramurali
 */
public class MSTFieldError extends SimpleTagSupport {

	/** Map that contains all the field Errors */
	private Map errors;

    @Override
    public void doTag() throws JspException,IOException
    {

        if( errors != null && errors.size() > 0)
                {
                        JspWriter out = this.getJspContext().getOut();
                        // get message for error map
                        List errorList = (List)errors.get(0);
                        if( errorList != null && errorList.size() > 0)
                        {
                                String error = (String)errorList.get(0);
                                out.write(error);
                        }
                }


    }

    /**
     * Sets the field error map
     *
     * @param errors error map
     */
    public void setErrors(Map errors)
    {
        this.errors = errors;
    }

    /**
     * Returns the field error map
     *
     * @return error map
     */
    public Map getErrors()
    {
        return this.errors;
    }

}



