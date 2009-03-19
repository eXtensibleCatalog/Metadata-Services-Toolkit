/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.interceptor;

import org.apache.log4j.Logger;
import org.apache.struts2.StrutsStatics;

import xc.mst.action.UserAware;
import xc.mst.bo.user.User;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Gets user from Session and sets in the action
 *
 * @author Sharmila Ranganathan
 *
 */
public class UserInterceptor extends AbstractInterceptor implements StrutsStatics{

	/**
	 * Eclipse generated id
	 */
	private static final long serialVersionUID = -7386840495405549846L;

	/** Log */
	private static Logger log = Logger.getLogger(UserInterceptor.class);

	/**
	 * Gets the user and sets them in the session.
	 *
	 * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	public String intercept(ActionInvocation invocation) throws Exception {

		final Object action = invocation.getAction();
		User user = (User) invocation.getInvocationContext().getSession().get("user");

		log.debug("Logged In User :"  +  user);
		// If user is not in session then it means the user has not logged in. So forward the user to login page.
		if (user == null) {
			return "user-login";
		}

		// Check if user has any permissions assigned. If not show error message.
		if (user != null && (user.getGroups() == null || user.getGroups().size() == 0)) {
			return "no-permission";
		}

		if (action instanceof UserAware) {
			if( user != null )
			{
	            ((UserAware) action).setUser(user);
			}
	    }

		return invocation.invoke();
	}


}
