/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Action class for user logout
 * 
 * @author Sharmila Ranganathan
 * 
 */
public class Logout extends ActionSupport implements ServletRequestAware {

    /** Generated id */
    private static final long serialVersionUID = 8874524856047656439L;

    /** Request */
    private HttpServletRequest request;

    /**
     * Overriding default implementation to logout the user.
     * 
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {

        request.getSession().setAttribute("user", null);

        return SUCCESS;
    }

    /**
     * Set the servlet request.
     * 
     * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
     */
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

}
