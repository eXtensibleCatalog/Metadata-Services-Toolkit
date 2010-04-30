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
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

/**
 * Displays the previous page
 *
 * @author Sharmila Ranganathan
 *
 */
public class PreviousPageTag extends SimpleTagSupport {

	/** Logger */
	private static final Logger log = Logger.getLogger(PreviousPageTag.class);

	public void doTag() throws JspException {
		log.debug("do tag called");
		PagerTag pagerTag =
			 (PagerTag)findAncestorWithClass(this,
					 PagerTag.class);

	    if(pagerTag == null)
	    {
	    	throw new JspTagException("the <ur:previousPage> tag must"
	    			+ " be nested within a <ur:pager> tag");
	    }


		JspFragment body = getJspBody();
		PageContext pageContext = (PageContext) getJspContext();

		try {
			if (pagerTag.getCurrentPageNumber() != 1 && pagerTag.getTotalPageNumber() > 1) {

				if( body != null )
				{
					int rowStart = ((pagerTag.getCurrentPageNumber() - 1) * pagerTag.getNumberOfResultsToShow())  - pagerTag.getNumberOfResultsToShow();
					pageContext.setAttribute("rowStart", rowStart);

					int startPageNumber = 1;
					if( pagerTag.getCurrentPageNumber()  == pagerTag.getStartPageNumber() )
					{
						startPageNumber = pagerTag.getStartPageNumber() - pagerTag.getNumberOfPagesToShow();
					}
					else
					{
						startPageNumber = pagerTag.getStartPageNumber();
					}

					pageContext.setAttribute("prevousPageStartPageNumber", startPageNumber);
				    body.invoke(null);
				}

			}
		} catch (Exception e) {
			throw new JspException(e);
		}

	}

}
