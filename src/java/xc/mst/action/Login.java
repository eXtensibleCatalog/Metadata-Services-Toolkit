/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

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
    private int serverId;

	/** A reference to the logger for this class */
	private static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Overriding default implementation to login the user.
     *
     * @return {@link #SUCCESS}
     */
    @Override
	public String execute() throws DataException {

    	servers = serverService.getAll();

    	return SUCCESS;
    }

	/**
     * Logs the user in to the system
     */
	public String login() throws Exception {

		Server server = serverService.getServerById(serverId);
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
                List<Group> groupList = user.getGroups();
                if (groupList.size() == 0) {
                	return "no-permission";
                }
                Iterator<Group> groupIter = groupList.iterator();
                Group tempGroup = (Group)groupIter.next();
                List<Permission> tempPermissions = tempGroup.getPermissions();
                Iterator<Permission> permissionsIter = tempPermissions.iterator();
                Permission tempPermission = (Permission)permissionsIter.next();
                switch(tempPermission.getTabId())
                {
                    case 1 : setForwardLink("allRepository.action");
                             break;
                    case 2 : setForwardLink("allSchedules.action");
                             break;
                    case 3:  setForwardLink("listServices.action");
                             break;
                    case 4 : setForwardLink("browseRecords.action");
                             break;
                    case 5 : setForwardLink("serviceLog.action");
                             break;
                    case 6 : setForwardLink("allUsers.action");
                             break;
                    case 7 : setForwardLink("emailConfig.action");
                             break;
                    case 8 : setForwardLink("searchIndex.action");
                             break;
                    default: setForwardLink("logout.action");
                             break;
                }
				resultName = SUCCESS;
			} else {
				servers = serverService.getAll();
				addFieldError("loginError", "Invalid username / password. Please try again");
				resultName = INPUT;
			}
		} else {
			servers = serverService.getAll();
			addFieldError("loginError", "Invalid username / password. Please try again");
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

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

}
