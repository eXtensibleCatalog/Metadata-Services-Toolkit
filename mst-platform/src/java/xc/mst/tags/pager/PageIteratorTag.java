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
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

/**
 * This tag displays the page iterator
 * 
 * @author Sharmila Ranganathan
 * 
 */
public class PageIteratorTag extends SimpleTagSupport {

    /** Iterator variable name */
    private String var;

    /** Logger */
    private static final Logger log = Logger.getLogger(PageIteratorTag.class);

    public void doTag() throws JspException {
        log.debug("do tag called");

        PagerTag pagerTag =
                (PagerTag) findAncestorWithClass(this,
                        PagerTag.class);

        if (pagerTag == null) {
            throw new JspTagException("the <ur:forEachPage> tag must"
                    + " be nested within a <ur:pager> tag");
        }

        JspFragment body = getJspBody();

        try {
            for (int i = pagerTag.getStartPageNumber(); i <= pagerTag.getEndPageNumber(); i++) {
                getJspContext().setAttribute(var, i);
                if (body != null) {
                    int rowStart = (i * pagerTag.getNumberOfResultsToShow()) - pagerTag.getNumberOfResultsToShow();
                    getJspContext().setAttribute("rowStart", rowStart);
                    body.invoke(null);
                }
            }

        } catch (Exception e) {
            throw new JspException(e);
        }

    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

}
