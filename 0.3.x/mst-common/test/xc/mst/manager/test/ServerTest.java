/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.manager.test;

import org.testng.annotations.Test;

import xc.mst.bo.user.Server;
import xc.mst.bo.user.Server.ServerType;
import xc.mst.common.test.BaseTest;
import xc.mst.helper.TestHelper;
import xc.mst.manager.user.ServerService;

/**
 * Tests for Servers
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class ServerTest extends BaseTest
{
    /**
     * Method which tests all the functionality related to the Server
     *
     */
    public void addServer()
    {
    	
      	 // Initialize Solr, database, log before testing
      	 TestHelper helper = TestHelper.getInstance();
        try
        {
            ServerService serverService = (ServerService)getBean("ServerService");
            Server server = new Server();
            server.setForgotPasswordLabel("Forgot Password Label");
            server.setForgotPasswordUrl("Forgot Password Url");
            server.setShowForgotPasswordLink(false);
            server.setStartLocation("Start Location");
            server.setUserNameAttribute("User Name Attribute");
            server.setInstitution("University of Rochester");
            server.setName("Net ID Server");
            server.setPort(45);
            server.setType(ServerType.LDAP);
            server.setUrl("http://www.rochester.edu");

            serverService.insertServer(server);
            Server anotherServer = serverService.getServerById(server.getId());
            assert(anotherServer.getId()==server.getId()): "The server IDs are different";
            assert(anotherServer.getForgotPasswordLabel().equalsIgnoreCase(server.getForgotPasswordLabel())): "The Forgot Password Label fields dont match";
            assert(anotherServer.getForgotPasswordUrl().equalsIgnoreCase(server.getForgotPasswordUrl())): "The forgot password URL fields dont match";
            assert(anotherServer.getInstitution().equalsIgnoreCase(server.getInstitution())): "The Institutions dont match";
            assert(anotherServer.getName().equalsIgnoreCase(server.getName())): "The Server Names dont match";
            assert(anotherServer.getPort()==server.getPort()): "The Port numbers dont match";
            assert(anotherServer.getShowForgotPasswordLink()==false): "Fields dont match";
            assert(anotherServer.getStartLocation().equalsIgnoreCase(server.getStartLocation())): "The start locations dont match";
            assert(anotherServer.getType().equals(ServerType.LDAP)): "Server type should be LDAP";
            assert(anotherServer.getUrl().equalsIgnoreCase(server.getUrl())): "The URLs dont match";
            assert(anotherServer.getUserNameAttribute().equalsIgnoreCase(server.getUserNameAttribute())): "The username Attributes dont match";
            
            serverService.deleteServer(server);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
