/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
 * Displays the last page
 *
 * @author Sharmila Ranganathan
 *
 */
public class LastPageTag extends SimpleTagSupport {

	/** Logger */
	private static final Logger log = Logger.getLogger(LastPageTag.class);

	public void doTag() throws JspException {
		log.debug("do tag called");
		PagerTag pagerTag =
			 (PagerTag)findAncestorWithClass(this,
					 PagerTag.class);

	    if(pagerTag == null)
	    {
	    	throw new JspTagException("the <ur:lastPage> tag must"
	    			+ " be nested within a <ur:pager> tag");
	    }


		JspFragment body = getJspBody();
		PageContext pageContext = (PageContext) getJspContext();

		try {
			int currentPageNumber = 1;

			if (pagerTag.getTotalHits() != 0) {
				if (pagerTag.getTotalHits() % pagerTag.getNumberOfResultsToShow() == 0) {
					currentPageNumber = pagerTag.getTotalHits() / pagerTag.getNumberOfResultsToShow();
				} else {
					currentPageNumber = (pagerTag.getTotalHits() / pagerTag.getNumberOfResultsToShow() )+ 1;
				}

				if (pagerTag.getCurrentPageNumber() != currentPageNumber) {

					if( body != null )
					{
							pageContext.setAttribute("currentPageNumber", currentPageNumber);

							int startPageNumber = 1;
							if( currentPageNumber % pagerTag.getNumberOfPagesToShow() == 0 )
							{
								startPageNumber = currentPageNumber - pagerTag.getNumberOfPagesToShow() + 1;
							}
							else
							{
								startPageNumber = (currentPageNumber / pagerTag.getNumberOfPagesToShow()) * pagerTag.getNumberOfPagesToShow() + 1;
							}

							pageContext.setAttribute("startPageNumber", startPageNumber);

							int rowstart = (currentPageNumber * pagerTag.getNumberOfResultsToShow()) - pagerTag.getNumberOfResultsToShow();
							pageContext.setAttribute("rowstartForLastPage", rowstart);
							body.invoke(null);

					}

				}
			}
		} catch (Exception e) {
			throw new JspException(e);
		}

	}

}
