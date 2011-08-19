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
 * This tag displays the next page
 *
 * @author Sharmila Ranganathan
 *
 */
public class NextPageTag extends SimpleTagSupport {

    /** Logger */
    private static final Logger log = Logger.getLogger(NextPageTag.class);

    public void doTag() throws JspException {
        log.debug("do tag called");

        PagerTag pagerTag =
             (PagerTag)findAncestorWithClass(this,
                     PagerTag.class);

        if(pagerTag == null)
        {
            throw new JspTagException("the <ur:nextPage> tag must"
                    + " be nested within a <ur:pager> tag");
        }

        JspFragment body = getJspBody();
        PageContext pageContext = (PageContext) getJspContext();

        try {
            if (pagerTag.getTotalPageNumber() > 1) {
                int rowStart = ((pagerTag.getCurrentPageNumber() + 1) * pagerTag.getNumberOfResultsToShow())  - pagerTag.getNumberOfResultsToShow();
                pageContext.setAttribute("rowStart", rowStart);
                int nextPageStartPageNumber = 1;

                nextPageStartPageNumber = getStartPageNumber(pagerTag);

                pageContext.setAttribute("nextPageStartPageNumber", nextPageStartPageNumber);

                if (pagerTag.getTotalPageNumber() != pagerTag.getCurrentPageNumber()) {
                    if( body != null )
                    {
                        body.invoke(null);
                    }

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

        int nextPageStartPageNumber = 1;

        // Ex: 1  2  3 ...  next
        // Say 3 is the current page
        // Determines the page number to start with when user clicks on "Next". In this case the user is on page 3 and
        // when user clicks on "NEXT" page number should start from '4' .
        // So that "4  5  6 .. Next" will be displayed
        if( (pagerTag.getEndPageNumber() == pagerTag.getCurrentPageNumber())
                && (pagerTag.getEndPageNumber() < pagerTag.getTotalPageNumber()) )
        {
            nextPageStartPageNumber = pagerTag.getStartPageNumber() + pagerTag.getNumberOfPagesToShow();
        }
        // Ex: 1  2  3 ...  next
        // Say 1 or 2 is the current page
        // Determines the page number to start with when user clicks on "Next". In this case the user is on page 1 or 2 and
        // when user clicks on "Next", the  page number should still start from '1' .
        // So that "1  2  3 .. Next" will be displayed
        else if (pagerTag.getCurrentPageNumber() < pagerTag.getEndPageNumber())
        {
            nextPageStartPageNumber = pagerTag.getStartPageNumber();
        }

        return nextPageStartPageNumber;
    }

}
