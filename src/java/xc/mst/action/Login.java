/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.user.Permission;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Action class for user login
 *
 * @author Sharmila Ranganathan
 *
 */
public class Login extends ActionSupport implements ServletRequestAware {

	/**
	 * Generated id
	 */
	private static final long serialVersionUID = -6850951668228864727L;

	/** User name for the user */
	private String userName;

	/** Password for the user */
	private String password;

	/** User logged in */
    private User user;

    /** Request */
    private HttpServletRequest request;

	/** User service */
	private UserService userService = new DefaultUserService();

	/** Server service */
	private ServerService serverService = new DefaultServerService();

    /**The first page that the user has permission to access */
    private String forwardLink;

    /** List of servers */
    private List<Server> servers;

    /** Server selected */
    private String serverName;

	/** A reference to the logger for this class */
	private static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Error type */
	private String errorType;
	
	/**  Object used to read properties from the default configuration file */
	protected static final Configuration defaultConfiguration = ConfigurationManager.getConfiguration();
	
	/**  Indicates if error in configuration */
	public boolean configurationError = false;


    /**
     * Overriding default implementation to login the user.
     *
     * @return {@link #SUCCESS}
     */
    @Override
	public String execute() throws DataException {
    	try {
    		servers = serverService.getAll();
    	}  catch (DatabaseConfigException dce) {
    		if (!MSTConfiguration.mstInstanceFolderExist) {
    			addFieldError("instancesFolderError", defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + " folder is missing under tomcat working directory. Please refer to MST installation manual for configuring correctly.");
    			log.error(defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + " folder is missing under tomcat working directory. Please refer to MST installation manual for configuring correctly.");
    		} else if (!MSTConfiguration.currentInstanceFolderExist) {
    			int beginIndex = MSTConfiguration.getUrlPath().indexOf(MSTConfiguration.FILE_SEPARATOR);
    			String instanceFolderName = MSTConfiguration.getUrlPath().substring(beginIndex + 1);
    			addFieldError("currentInstancesFolderError", instanceFolderName + " folder is missing under &lt;tomcat-working-directory&gt;/" + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + ". Please refer to MST installation manual for configuring correctly.");
    			log.error(instanceFolderName + " folder is missing under &lt;tomcat-working-directory&gt;/"  + defaultConfiguration.getProperty(Constants.INSTANCES_FOLDER_NAME) + ". Please refer to MST installation manual for configuring correctly.");
    		} else {
	    		log.error(dce.getMessage(), dce);
	    		addFieldError("dbConfigError", "Unable to access the database to get Server type information. There may be problem with database configuration.");
    		}
    		configurationError = true;
    		errorType = "error";
			return INPUT;
    	}

    	// Get the user object in session
		User sessionUser = (User) request.getSession().getAttribute("user");

		if (log.isDebugEnabled()) {
			log.debug("User in session: " + sessionUser);
		}

		// If user exist in session then forward to page for which user has permission
		if (sessionUser != null) {

            List<Permission> permissions = userService.getPermissionsForUserByTabOrderAsc(sessionUser);

            if (permissions != null) {
            	if (permissions.size() == 0) {
            		return "no-permission";
            	} else if (permissions.get(0).getTabName().equalsIgnoreCase("Repositories")) {
	            	setForwardLink("allRepository.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Harvest")) {
	            	setForwardLink("allSchedules.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Services")) {
	            	setForwardLink("listServices.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Processing Rules")) {
	            	setForwardLink("listProcessingDirectives.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Browse Records")) {
	            	setForwardLink("viewBrowseRecords.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Logs")) {
	            	setForwardLink("generalLog.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Users/Groups")) {
	            	setForwardLink("allUsers.action");
	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Configuration")) {
	            	setForwardLink("viewEmailConfig.action");
	            } else {
	            	return "no-permission";
	            }
            }
            if (log.isDebugEnabled()) {
            	log.debug("User exist in session. User forwarded to : " + forwardLink);
            }

            return "user-initial-page";
		}

    	return SUCCESS;
    }

	/**
     * Logs the user in to the system
     */
	public String login() throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Trying to login username :" + userName);
		}
		Server server = serverService.getServerByName(serverName);
        user = userService.getUserByUserName(userName, server);

		boolean result = false;
		String resultName = INPUT;

		if (user != null) {
			if (server.getName().equalsIgnoreCase("local")) {
				result = userService.authenticateUser(user, password);
			} else {
				result = userService.authenticateLDAPUser(user, password, server);
			}

			User completeUserData = userService.getUserById(user.getId());
			if (result) {
				// Place the user object in session
				request.getSession().setAttribute("user", completeUserData);

                //get Calendar instance
                Calendar now = Calendar.getInstance();
                //get current TimeZone using getTimeZone method of Calendar class
                TimeZone timeZone = now.getTimeZone();
                //display current TimeZone using getDisplayName() method of TimeZone class
                request.getSession().setAttribute("timeZone",timeZone.getDisplayName());

				List<Permission> permissions = userService.getPermissionsForUserByTabOrderAsc(completeUserData);

                if (permissions != null) {
                	if (permissions.size() == 0) {
                		return "no-permission";
                	} else if (permissions.get(0).getTabName().equalsIgnoreCase("Repositories")) {
    	            	setForwardLink("allRepository.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Harvest")) {
    	            	setForwardLink("allSchedules.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Services")) {
    	            	setForwardLink("listServices.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Processing Rules")) {
    	            	setForwardLink("listProcessingDirectives.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Browse Records")) {
    	            	setForwardLink("viewBrowseRecords.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Logs")) {
    	            	setForwardLink("generalLog.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Users/Groups")) {
    	            	setForwardLink("allUsers.action");
    	            } else if (permissions.get(0).getTabName().equalsIgnoreCase("Configuration")) {
    	            	setForwardLink("viewEmailConfig.action");
    	            } else {
    	            	return "no-permission";
    	            }
                } else {
                	return "no-permission";
                }

				resultName = SUCCESS;
			} else {
				servers = serverService.getAll();
				addFieldError("loginError", "Invalid username / password. Please try again");
				errorType = "error";
				resultName = INPUT;
			}
		} else {
			servers = serverService.getAll();
			addFieldError("loginError", "Invalid username / password. Please try again");
			errorType = "error";
			resultName = INPUT;
		}
		return resultName;
	}

	/**
	 * Get user name
	 *
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set User name
	 *
	 * @param userName User name of the user logging in
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get password
	 *
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

    /**
     * sets the link that the user should be forwarded to.
     * @param forwardLink page link
     */
    public void setForwardLink(String forwardLink)
    {
        this.forwardLink = forwardLink;
    }

    /**
     * returns the link that the user should be forwarded to
     * @return forward link
     */
    public String getForwardLink()
    {
        return this.forwardLink;
    }

	public List<Server> getServers() {
		return servers;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
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
