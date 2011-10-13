/**
 * Copyright (c) 2009 eXtensible Catalog Organization
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

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.user.Server;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Deletes the LDAP Server
 * 
 * @author Tejaswi Haramurali
 */
@SuppressWarnings("serial")
public class DeleteLDAP extends BaseActionSupport {
    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Error type */
    private String errorType;

    /**
     * Returns a temporary server object which is used to display details in the JSP
     * 
     * @return temporary server object
     * @throws xc.mst.dao.DataException
     */
    public Server getTemporaryServer() throws DataException {
        List<Server> serverList = getServerService().getAll();
        Iterator<Server> iter = serverList.iterator();
        Server finalServer = null;
        while (iter.hasNext()) {
            Server tempServer = (Server) iter.next();
            if (tempServer.getType() != Server.ServerType.LOCAL) {
                finalServer = tempServer;
                break;
            }
        }
        return finalServer;
    }

    /**
     * Overrides default implementation to delete an LDAP server.
     * 
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute() {
        try {
            List<Server> serverlist = getServerService().getAll();
            Iterator<Server> serverIter = serverlist.iterator();

            if (getUserService().getLDAPUserCount() == 0) {
                while (serverIter.hasNext()) {
                    Server server = (Server) serverIter.next();
                    if (server.getType() != Server.ServerType.LOCAL) {
                        getServerService().deleteServer(server);
                    }
                }

                return SUCCESS;
            } else {
                this.addFieldError("deleteLDAPError", "The LDAP Server is associated with one or more users and cannot be deleted");
                errorType = "error";
                return INPUT;
            }
        } catch (DatabaseConfigException dce) {
            log.error(dce.getMessage(), dce);
            this.addFieldError("deleteLDAPError", "Unable to connect to the database. Database configuration may be incorrect");
            errorType = "error";
            return INPUT;
        } catch (DataException de) {
            log.error(de.getMessage(), de);
            this.addFieldError("deleteLDAPError", "Error occurred while deleting LDAP Server. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            errorType = "error";
            return INPUT;
        }
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
     * @param errorType
     *            error type
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

}
