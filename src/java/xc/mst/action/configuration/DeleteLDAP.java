
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
import java.util.Iterator;
import java.util.List;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.dao.DataException;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;

/**
 * Deletes the LDAP Server
 *
 * @author Tejaswi Haramurali
 */
public class DeleteLDAP extends ActionSupport
{
    /**Creates a service for Servers*/
    private ServerService serverService = new DefaultServerService();

    /**Creates service object for Users */
    private UserService userService = new DefaultUserService();

	/** Error type */
	private String errorType; 
	
    /**
     * returns a temporary server object which is used to display details in the JSP
     * @return server Object
     * @throws xc.mst.dao.DataException
     */
    public Server getTemporaryServer() throws DataException
    {
        List<Server> serverList = serverService.getAll();
        Iterator iter = serverList.iterator();
        Server finalServer = null;
        while(iter.hasNext())
        {
            Server tempServer = (Server)iter.next();
            if(tempServer.getType()!=4)
            {
                finalServer = tempServer;
                break;
            }
        }
        return finalServer;
    }

     /**
     * Overrides default implementation to delete an LDAP server.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {

            List<Server> serverlist = serverService.getAll();
            Iterator serverIter = serverlist.iterator();
            List<User> userList = userService.getAllUsersSorted(false,"username");
            Iterator userIter = userList.iterator();
            boolean deleteFlag = true;
            while(userIter.hasNext())
            {
                User user = (User)userIter.next();
                if(user.getServer().getType()!=4)
                {
                    deleteFlag = false;
                }
            }
            if(deleteFlag==true)
            {
                while(serverIter.hasNext())
                {
                    Server server = (Server)serverIter.next();
                    if(server.getType()!=4)
                    {
                        serverService.deleteServer(server);
                    }
                }

                return SUCCESS;
            }
            else
            {
                this.addFieldError("deleteLDAPError", "Error : The LDAP Server is associated with one or more users and cannot be deleted");
                errorType = "error";
                return INPUT;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.addFieldError("deleteLDAPError", "Error : Error deleting LDAP Server");
            errorType = "error";
            return INPUT;
        }
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
