/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.action.UserAware;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;

/**
 * Action to edit user account
 *
 * @author Sharmila Ranganathan
 *
 */
public class EditMyAccount extends BaseActionSupport implements UserAware {

	/** Generated id  */
	private static final long serialVersionUID = 1117303971697447644L;

	/** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** New user registering with the system */
	private User user;

	/** First name of user */
	private String firstName;

	/** Last name of user */
	private String lastName;
	
	/** Email of user */
	private String email;

	/** Error type */
	private String errorType; 
	
	/** Information message to user */
	private String message;
	
	/**
	 * To view edit my account
	 */
    @Override
	public String execute() throws DataException {
		firstName =  user.getFirstName();
		lastName =  user.getLastName();
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

		try {
				User userWithEmail = getUserService().getUserByEmail(email, user.getServer());
				if (userWithEmail == null || userWithEmail.getId() == user.getId()) {
					user.setFirstName(firstName);
					user.setLastName(lastName);
					user.setEmail(email);

					getUserService().updateUser(user);
					errorType = "info";
					message = "Account information saved.";
				} else {
					addFieldError("emailExist", "Email already exists - " + email);
					errorType = "error";
					return INPUT;
				}
		} catch (Exception e) {
			log.error("Exception occured while saving user account information", e);
			addFieldError("dataError", e.getMessage());
			errorType = "error";
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName.trim();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName.trim();
	}

	public String getMessage() {
		return message;
	}


}
