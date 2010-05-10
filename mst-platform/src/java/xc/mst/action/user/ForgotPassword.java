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
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.utils.MSTConfiguration;

/**
 * Action to handle forgot password
 *
 * @author Sharmila Ranganathan
 *
 */
public class ForgotPassword extends BaseActionSupport {

	/** Eclipse generated Id */
	private static final long serialVersionUID = 7491973408867996093L;

	/**  Logger for add user action */
	private static final Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Email id to send the password details */
	private String email;

	/** Indicates whether the password reset is success or not. */
	private boolean resetSuccess= false;

	/** Error type */
	private String errorType; 
	
	/**  Object used to read properties from the default configuration file */
	protected static final Configuration defaultConfiguration = ConfigurationManager.getConfiguration();
	
	/**  Indicates if error in configuration */
	public boolean configurationError = false;
	
	public String execute() {
		
		if (!MSTConfiguration.mstInstanceFolderExist) {
			addFieldError("instancesFolderError", "MST configuration is incomplete. " + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + " folder is missing under tomcat working directory. Please refer to MST installation manual for configuring correctly.");
			log.error("MST configuration is incomplete. " +defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + " folder is missing under tomcat working directory. Please refer to MST installation manual for configuring correctly.");
			configurationError = true;
			errorType = "error";
			return INPUT;
		} else if (!MSTConfiguration.currentInstanceFolderExist) {
			int beginIndex = MSTConfiguration.getUrlPath().indexOf(MSTConfiguration.FILE_SEPARATOR);
			String instanceFolderName = MSTConfiguration.getUrlPath().substring(beginIndex + 1);
			addFieldError("currentInstancesFolderError", "MST configuration is incomplete. " + instanceFolderName + " folder is missing under &lt;tomcat-working-directory&gt;/" + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + ". Please refer to MST installation manual for configuring correctly.");
			log.error("MST configuration is incomplete. " +instanceFolderName + " folder is missing under &lt;tomcat-working-directory&gt;/"  + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + ". Please refer to MST installation manual for configuring correctly.");
			configurationError = true;
			errorType = "error";
			return INPUT;
		} 

		
		
		return SUCCESS;
		
	}

	/**
	 * Execute method
	 *
	 */
	public String resetPassword() throws Exception {
		log.debug("Execute called email:" + email );

		Server server = getServerService().getServerByName("Local");
		User user = getUserService().getUserByEmail(email, server);

		if (user == null)
		{
			addFieldError("emailDoesnotExist",
					"The Email id " + email + " does not exist in the system." );
			errorType = "error";
			return INPUT;
		} else {
			String newPassword = getUserService().createRandomPassword();
			user.setPassword(getUserService().encryptPassword(newPassword));
			
			boolean emailSent = getUserService().sendEmailForForgotPassword(newPassword, user);
			
			if (!emailSent) {
				StringBuffer errorMessage = new StringBuffer();
				errorMessage.append("Emailing new password failed. E-mail is not configured for the application.");
				addFieldError("emailError",  errorMessage.toString());
				errorType = "error";
				return INPUT;
			}
			getUserService().updateUser(user);
			resetSuccess = true;
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

	public boolean isConfigurationError() {
		return configurationError;
	}



}
