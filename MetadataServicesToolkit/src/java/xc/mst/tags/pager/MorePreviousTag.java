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
 * This tag displays the previous set of pages
 *
 * @author Sharmila Ranganathan
 *
 */
public class MorePreviousTag extends SimpleTagSupport {

	/** Logger */
	private static final Logger log = Logger.getLogger(MorePreviousTag.class);

	public void doTag() throws JspException {
		log.debug("do tag called");

		PagerTag pagerTag =
			 (PagerTag)findAncestorWithClass(this,
					 PagerTag.class);

	    if(pagerTag == null)
	    {
	    	throw new JspTagException("the <ur:morePrevious> tag must"
	    			+ " be nested within a <ur:PreviousPage> tag");
	    }

		JspFragment body = getJspBody();
		PageContext pageContext = (PageContext) getJspContext();

		try {
			int startPageNumberForPreviousSet = 1;

			startPageNumberForPreviousSet = getStartPageNumber(pagerTag);

			pageContext.setAttribute("startPageNumberForPreviousSet", startPageNumberForPreviousSet);

			int rowStart = (startPageNumberForPreviousSet * pagerTag.getNumberOfResultsToShow())  - pagerTag.getNumberOfResultsToShow();
			pageContext.setAttribute("rowStart", rowStart);

			if (pagerTag.getStartPageNumber() > 1) {
				if( body != null )
				{
				    body.invoke(null);
				}
			}
		} catch (Exception e) {
			throw new JspException(e);
		}

	}

	/*
	 * Determines the starting page number for the display
	 * when user clicks on NEXT
	 *
	 */
	private int getStartPageNumber(PagerTag pagerTag) {

		int startPageNumberForPreviousSet = 1;

		// Ex: ... 4 5 6
		// Determines the page number to start with when user clicks on "...". In this case the page
		// number should start from 1.
		// So that "1 2 3 .. Next" will be displayed
		startPageNumberForPreviousSet = pagerTag.getStartPageNumber() - pagerTag.getNumberOfPagesToShow();

		return startPageNumberForPreviousSet;
	}

}
