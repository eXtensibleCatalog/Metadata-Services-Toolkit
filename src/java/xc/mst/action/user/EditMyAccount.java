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

import xc.mst.action.UserAware;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Action to edit user account
 *
 * @author Sharmila Ranganathan
 *
 */
public class EditMyAccount extends ActionSupport implements UserAware {

	/** Generated id  */
	private static final long serialVersionUID = 1117303971697447644L;

	/** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** New user registering with the system */
	private User user;

	/** User service */
	private UserService userService =  new DefaultUserService();

	/** Full name of user */
	private String fullName;

	/** User of user */
	private String userName;

	/** Email of user */
	private String email;

	/**
	 * To view edit my account
	 */
	public String execute() throws DataException {
		fullName =  user.getFullName();
		userName = user.getUsername();
		email = user.getEmail();

		return SUCCESS;
	}

	/**
	 * Save user account information
	 *
	 * @return
	 */
	public String saveMyAccount() {

		log.debug(EditMyAccount.class + ":" + "saveMyAccount()" );

		User otherUser = userService.getUserByUserName(userName, user.getServer());

		try {
			if (otherUser == null || otherUser.getId() == user.getId()) {
				User userWithEmail = userService.getUserByEmail(email, user.getServer());
				if (userWithEmail == null || userWithEmail.getId() == user.getId()) {
					user.setUsername(userName);
					user.setFullName(fullName);
					user.setEmail(email);

					userService.updateUser(user);
				} else {
					addFieldError("emailExist", "Email already exist - " + email);
					return INPUT;
				}
			} else {
				addFieldError("userNameExist", "User name already exist - " + userName);
				return INPUT;
			}
		} catch (Exception e) {
			addFieldError("dataError", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}



	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
