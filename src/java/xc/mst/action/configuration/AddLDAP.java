/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.configuration;

import com.opensymphony.xwork2.ActionSupport;
import java.util.*;
import xc.mst.bo.user.Server;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.ServerService;

/**
 * This class is used to add a new LDAP server to the system
 *
 * @author Tejaswi Haramurali
 */
public class AddLDAP extends ActionSupport
{
    /**Creates a service object for Servers */
    private ServerService serverService = new DefaultServerService();

    /**The display name used for the LDAP server **/
    private String displayName;

    /** The URL of the LDAP server */
    private String serverURL;

    /**The port number on the LDAP server **/
    private String port;

    private String userNameAttribute;

    private String startLocation;

    /**This is a temporary server object that is used to pre-fill JSP form fields */
    private Server server = new Server();

    /**Provides the status of the add Operation in the JSP page */
    private String message;

    /**sets the display name of the LDAP server **/
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName.trim();
    }

    /**returns the display name of the LDAP server */
    public String getDisplayName()
    {
        return this.displayName;
    }

    /**sets the URL of the server to the specified value */
    public void setServerURL(String serverURL)
    {
        this.serverURL = serverURL.trim();
    }

    /**returns the server URL */
    public String getServerURL()
    {
        return this.serverURL;
    }

    /** sets the port number of the LDAP server to the specified value */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**returns the port number of the LDAP server */
    public String getPort()
    {
        return port;
    }

    /**
     * Sets the user name attribute
     * @param userNameAttribute user name attribute
     */
    public void setUserNameAttribute(String userNameAttribute)
    {
        this.userNameAttribute = userNameAttribute;
    }

    /**
     * returns the user name attribute
     * @return user name attribute
     */
    public String getUserNameAttribute()
    {
        return this.userNameAttribute;
    }

    /**
     * sets the start location
     * @param startLocation start location
     */
    public void setStartLocation(String startLocation)
    {
        this.startLocation = startLocation;
    }

    /**
     * returns the start location
     * @return
     */
    public String getStartLocation()
    {
        return this.startLocation;
    }


     /**
     * Overrides default implementation to add an LDAP server.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
                
                List<Server> serverList = serverService.getAll();
                Iterator iter = serverList.iterator();

                while(iter.hasNext())
                   {
                       Server tempServer = (Server)iter.next();
                       if(tempServer.getType()!=4)
                       {
                           setServer(tempServer);
                           break;
                       }

                   }
                return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("addLDAPError", "Error : LDAP server could not be configured correctly");
            return INPUT;
        }
    }

    /**
     * The method that does the actual task of adding a new LDAP server.
     * @return {@link #SUCCESS}
     */
    public String addLDAP()
    {
        try
        {
            boolean serverExists = false;
         
            List<Server> serverList = serverService.getAll();
            Iterator iter = serverList.iterator();

            while(iter.hasNext())
            {
                Server tempServer = (Server)iter.next();
                if(tempServer.getType()!=4)
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
                server.setType(1);
                server.setStartLocation(getStartLocation());
                server.setUserNameAttribute(getUserNameAttribute());
                serverService.insertServer(server);
            }
            else
            {
                server.setName(getDisplayName());
                server.setUrl(getServerURL());
                server.setPort(Integer.parseInt(getPort()));
                server.setType(1);
                server.setStartLocation(getStartLocation());
                server.setUserNameAttribute(getUserNameAttribute());
                serverService.updateServer(server);
            }

            message = "Information Saved.";
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("addLDAPError", "Error : LDAP server could not be configured correctly");
            return INPUT;
        }
    }

    /**
     * returns the status of the add operation
     * @return message status
     */
	public String getMessage() {
		return message;
	}

    /**
     * sets the status of the add operation
     * @param message message status
     */
	public void setMessage(String message) {
		this.message = message;
	}

    /**
     * returns the temporary server object that is used to display details on the JSP
     * @return server object
     */
	public Server getServer() {
		return server;
	}

    /**
     * sets the temporary server object which is used to display details on the JSP
     * @param server server Object
     */
	public void setServer(Server server) {
		this.server = server;
	}
}
