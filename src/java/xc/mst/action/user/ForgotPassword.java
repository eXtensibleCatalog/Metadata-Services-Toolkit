/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import org.apache.log4j.Logger;

import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Action to handle forgot password
 *
 * @author Sharmila Ranganathan
 *
 */
public class ForgotPassword extends ActionSupport {

	/** Eclipse generated Id */
	private static final long serialVersionUID = 7491973408867996093L;

	/**  Logger for add user action */
	private static final Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Email id to send the password details */
	private String email;

	/** User service */
	private UserService userService = new DefaultUserService();

	/** Server service */
	private ServerService serverService = new DefaultServerService();

	/** Indicates whether the password reset is success or not. */
	private boolean resetSuccess= false;

	/** Error type */
	private String errorType; 
	

	/**
	 * Execute method
	 *
	 */
	public String resetPassword() throws Exception {
		log.debug("Execute called email:" + email );

		//TODO : to be changed for LDAP server
		Server server = serverService.getServerByName("Local");
		User user = userService.getUserByEmail(email, server);

		if (user == null)
		{
			addFieldError("emailDoesnotExist",
					"The Email id does not exist in the system : " + email);
			errorType = "error";
			return INPUT;
		} else {
			String newPassword = userService.createRandomPassword();
			user.setPassword(userService.encryptPassword(newPassword));
			userService.updateUser(user);
			resetSuccess = true;
			userService.sendEmailForForgotPassword(newPassword, user);
		}

		return SUCCESS;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isResetSuccess() {
		return resetSuccess;
	}

	public void setResetSuccess(boolean resetSuccess) {
		this.resetSuccess = resetSuccess;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}



}
