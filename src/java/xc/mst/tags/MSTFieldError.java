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
	private Map error;

    @Override
    public void doTag() //throws JspException,IOException
    {
        try
        {
            if( error != null && error.size() > 0)
                    {
                            JspWriter out = this.getJspContext().getOut();
                            // get message for error map
                            Iterator iter = error.values().iterator();
                            {
                                List<String> tempList= (List<String>)iter.next();
                                if(tempList!=null && tempList.size()>0)
                                {
                                    out.write(tempList.get(0));
                                }
                            }
                    }
        }
        catch(Exception e)
        {
            
        }

    }

    /**
     * Sets the field error map
     *
     * @param errors error map
     */
    public void setError(Map error)
    {
        this.error = error;
    }

    /**
     * Returns the field error map
     *
     * @return error map
     */
    public Map getError()
    {
        return this.error;
    }

}



