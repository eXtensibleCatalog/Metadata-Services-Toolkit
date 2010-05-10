/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.tags.pager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

/**
 * Displays the first page
 *
 * @author Sharmila Ranganathan
 *
 */
public class FirstPageTag extends SimpleTagSupport {

	/** Logger */
	private static final Logger log = Logger.getLogger(FirstPageTag.class);

	public void doTag() throws JspException {
		log.debug("do tag called");
		PagerTag pagerTag =
			 (PagerTag)findAncestorWithClass(this,
					 PagerTag.class);

	    if(pagerTag == null)
	    {
	    	throw new JspTagException("the <ur:firstPage> tag must"
	    			+ " be nested within a <ur:pager> tag");
	    }


		JspFragment body = getJspBody();

		try {
			if (pagerTag.getCurrentPageNumber() != 1) {

				if( body != null )
				{
				    body.invoke(null);
				}

			}
		} catch (Exception e) {
			throw new JspException(e);
		}

	}

}
