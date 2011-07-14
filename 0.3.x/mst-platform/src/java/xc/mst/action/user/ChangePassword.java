/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.action.BaseActionSupport;
import xc.mst.action.UserAware;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;

/**
 * Action to handle forgot password
 *
 * @author Sharmila Ranganathan
 *
 */
public class ChangePassword extends BaseActionSupport implements  UserAware, ServletRequestAware {

	/** Eclipse generated Id */
	private static final long serialVersionUID = -5266408129646334987L;

	/**  Logger for add user action */
	private static final Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Old password entered by user  */
	private String oldPassword;

	/** New password for user */
	private String newPassword;

	/** Logged in user */
	private User user;

    /** Request */
    private HttpServletRequest request;

	/** Error type */
	private String errorType; 
	

	/**
	 * Change password
	 */
	public String changePassword() throws Exception {
		
		if (log.isDebugEnabled()){
			log.debug("Change password for user with user name:" + user.getUsername());
		}
		
		if (user.getPassword().equals(getUserService().encryptPassword(oldPassword)))
		{
			user.setPassword(getUserService().encryptPassword(newPassword));
			getUserService().updateUser(user);
			request.getSession().setAttribute("user", user);
		} else {
			addFieldError("passwordDoesnotMatch",
					"The old password does not match. Please enter the correct old password." );
			errorType = "error";
			return INPUT;

		}

		return SUCCESS;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
