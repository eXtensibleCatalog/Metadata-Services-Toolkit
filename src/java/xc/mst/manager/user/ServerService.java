
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.user;

import java.util.List;
import xc.mst.bo.user.Server;
import xc.mst.dao.DataException;

/**
 * Service class which interacts with server objects
 *
 * @author Tejaswi Haramurali
 */
public interface ServerService {

    /**
     * Returns a server object
     *
     * @param serverId the Id of the server object to be returned
     * @return The server object that is returned
     */
    public Server getServerById(int serverId);

    /**
     * Returns a server object
     *
     * @param serverName the name of the server object to be returned
     * @return The server object that is returned
     */
    public Server getServerByName(String serverName);

    /**
     * Inserts a new server object
     *
     * @param server The server object to be inserted
     */
    public void insertServer(Server server) throws DataException;

    /**
     * Deletes a server object
     *
     * @param server The server object to be deleted
     */
    public void deleteServer(Server server) throws DataException;

    /**
     * Updates the details of a server
     * 
     * @param server The server whose details are to be updated.
     */
    public void updateServer(Server server) throws DataException;

    /**
     * Get all servers
     *
     * @return All servers
     * @throws DataException
     */
    public List<Server> getAll() throws DataException ;
}
