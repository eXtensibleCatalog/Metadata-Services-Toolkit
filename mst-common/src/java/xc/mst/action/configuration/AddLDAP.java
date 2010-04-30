/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.configuration;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.user.Server;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This class is used to add a new LDAP server to the system
 *
 * @author Tejaswi Haramurali
 */
public class AddLDAP extends ActionSupport
{
    /**
	 * Eclipse generated id
	 */
	private static final long serialVersionUID = 531062895841167505L;

    /** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**The display name used for the LDAP server **/
    private String displayName;

    /** The URL of the LDAP server */
    private String serverURL;

    /**The port number on the LDAP server **/
    private String port;

    /** The user name attribute */
    private String userNameAttribute;

    /** Start Location */
    private String startLocation;

    /**This is a temporary server object that is used to pre-fill JSP form fields */
    private Server server = new Server();

    /**Provides the status of the add Operation in the JSP page */
    private String message;
    
	/** Error type */
	private String errorType; 
	
	/** Indicates whether to show forgot password URL */
	private String showForgotPasswordLink;
	
	/** URL to forward the user to get forgot password */
	private String forgotPasswordUrl;
   

     /**
     * Overrides default implementation to add an LDAP server.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
                ServerService serverService = (ServerService)MSTConfiguration.getBean("ServerService");
                List<Server> serverList = serverService.getAll();
                Iterator<Server> iter = serverList.iterator();

                while(iter.hasNext())
                   {
                       Server tempServer = (Server)iter.next();
                       if(tempServer.getType()!=Server.ServerType.LOCAL)
                       {
                           setServer(tempServer);
                           break;
                       }

                   }
                return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("addLDAPError", "Unable to connect to the database. Database configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * The method that does the actual task of adding a new LDAP server.
     *
     * @return {@link #SUCCESS}
     */
    public String addLDAP()
    {
        try
        {
            boolean serverExists = false;
         
            ServerService serverService = (ServerService)MSTConfiguration.getBean("ServerService");
            List<Server> serverList = serverService.getAll();
            Iterator<Server> iter = serverList.iterator();

            while(iter.hasNext())
            {
                Server tempServer = (Server)iter.next();
                if(tempServer.getType()!=Server.ServerType.LOCAL)
                {
                    serverExists = true;
                    setServer(tempServer);
                    break;
                }

            }
            if(serverExists==false)
            {
                server.setName(getDisplayName());
                server.setUrl(getServerURL());
                server.setPort(Integer.parseInt(getPort()));
                server.setType(Server.ServerType.LDAP);
                server.setStartLocation(getStartLocation());
                server.setUserNameAttribute(getUserNameAttribute());
                
                if (showForgotPasswordLink.equalsIgnoreCase("yes")) {
                	server.setForgotPasswordUrl(forgotPasswordUrl);
                    server.setShowForgotPasswordLink(true);
                } else {
                	server.setForgotPasswordUrl(null);
                    server.setShowForgotPasswordLink(false);
                }
                if(displayName.equalsIgnoreCase("local"))
                {
                    this.addFieldError("addLDAPError", "Cannot add a server with name 'Local'. Please choose a different name");
                    errorType = "error";
                    return SUCCESS;
                }
                else
                {
                    serverService.insertServer(server);
                }
               
            }
            else
            {
                server.setName(getDisplayName());
                server.setUrl(getServerURL());
                server.setPort(Integer.parseInt(getPort()));
                server.setType(Server.ServerType.LDAP);
                server.setStartLocation(getStartLocation());
                server.setUserNameAttribute(getUserNameAttribute());
                
                if (showForgotPasswordLink.equalsIgnoreCase("yes")) {
                	server.setForgotPasswordUrl(forgotPasswordUrl);
                    server.setShowForgotPasswordLink(true);
	            } else {
	            	server.setForgotPasswordUrl(null);
                    server.setShowForgotPasswordLink(false);
	            }
                if(displayName.equalsIgnoreCase("local"))
                {
                    this.addFieldError("addLDAPError", "Cannot update a server with name 'Local'. Please choose a different name");
                    errorType = "error";
                    return SUCCESS;
                }
                else
                {
                    serverService.updateServer(server);
                }
            }

            message = "LDAP Server Information Saved.";
            errorType = "info";
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("addLDAPError", "Unable to connect to the database. Database configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            this.addFieldError("addLDAPError", "Error occurred while adding LDAP Server. An email has been sent to the administrator");
            UserService userService = (UserService)MSTConfiguration.getBean("UserService");
            userService.sendEmailErrorReport();
            errorType = "error";
            return INPUT;
        }
    }

     /**
     * Sets the display name of the LDAP server
     * @param displayName display name of server
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName.trim();
    }

    /**
     * Returns the display name of the LDAP server
     * @return display name
     */
    public String getDisplayName()
    {
        return this.displayName;
    }

    /**
     * Sets the URL of the server to the specified value
     *
     * @param serverURL URL of the server
     */
    public void setServerURL(String serverURL)
    {
        this.serverURL = serverURL.trim();
    }

    /**
     * Returns the server URL
     *
     * @return URL of the server
     */
    public String getServerURL()
    {
        return this.serverURL;
    }

    /**
     * Sets the port number of the LDAP server to the specified value
     *
     * @param port port number
     */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**
     * Returns the port number of the LDAP server
     *
     * @return port number
     */
    public String getPort()
    {
        return port;
    }

    /**
     * Sets the user name attribute
     *
     * @param userNameAttribute username attribute
     */
    public void setUserNameAttribute(String userNameAttribute)
    {
        this.userNameAttribute = userNameAttribute;
    }

    /**
     * Returns the user name attribute
     *
     * @return username attribute
     */
    public String getUserNameAttribute()
    {
        return this.userNameAttribute;
    }

    /**
     * Sets the start location
     *
     * @param startLocation start location
     */
    public void setStartLocation(String startLocation)
    {
        this.startLocation = startLocation;
    }

    /**
     * Returns the start location
     *
     * @return start location
     */
    public String getStartLocation()
    {
        return this.startLocation;
    }


    /**
     * Returns the status of the add operation
     *
     * @return information message
     */
	public String getMessage() {
		return message;
	}

    /**
     * Sets the status of the add operation
     *
     * @param message information message
     */
	public void setMessage(String message) {
		this.message = message;
	}

    /**
     * Returns the temporary server object that is used to display details on the JSP
     *
     * @return server object
     */
	public Server getServer() {
		return server;
	}

    /**
     * Sets the temporary server object which is used to display details on the JSP
     *
     * @param server object
     */
	public void setServer(Server server) {
		this.server = server;
	}

    /**
     * Returns error type
     *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets error type
     *
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String isShowForgotPasswordLink() {
		return showForgotPasswordLink;
	}

	public void setShowForgotPasswordLink(String showForgotPasswordLink) {
		this.showForgotPasswordLink = showForgotPasswordLink;
	}

	public String getForgotPasswordUrl() {
		return forgotPasswordUrl;
	}

	public void setForgotPasswordUrl(String forgotPasswordUrl) {
		this.forgotPasswordUrl = forgotPasswordUrl;
	}
}
