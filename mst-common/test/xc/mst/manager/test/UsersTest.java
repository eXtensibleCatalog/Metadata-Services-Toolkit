/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import xc.mst.bo.user.User;
import xc.mst.common.test.BaseTest;
import xc.mst.helper.TestHelper;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;

/**
 * Tests for Users
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class UsersTest extends BaseTest
{
    /**
     * Method for testing all the functionality related to the User
     *
     */
    public void addUser()
    {
           // Initialize Solr, database, log before testing
           TestHelper helper = TestHelper.getInstance();
        try
        {
            List groupList = new ArrayList();
            UserService userService = (UserService)getBean("UserService");
            ServerService serverService = (ServerService)getBean("ServerService");
            User user = new User();
            user.setAccountCreated(new Date(2009,1,1));
            user.setEmail("JohnDoe@gmail.com");
            user.setFailedLoginAttempts(0);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setGroups(groupList);
            user.setLastLogin(new Date(2009,1,1));
            user.setPassword("JohnDoePassword");
            user.setServer(serverService.getServerById(1));
            user.setUsername("John_Doe");

            userService.insertUser(user);

            User anotherUser = userService.getUserById(user.getId());
            //assert (anotherUser.getAccountCreated().compareTo(user.getAccountCreated())==0): "The Account created Dates should match";
            assert (anotherUser.getEmail().equalsIgnoreCase(user.getEmail())): "";
            assert (anotherUser.getFailedLoginAttempts()==0): "The number of failed Login attempts should be 0";
            assert (anotherUser.getFirstName().equalsIgnoreCase(user.getFirstName())): "The First Names of the users should match";
            assert (anotherUser.getLastName().equalsIgnoreCase(user.getLastName())): "The Last Names of the users should match";
            assert (anotherUser.getGroups().isEmpty()): "The group list should be empty";
            assert (anotherUser.getLastLogin().compareTo(user.getLastLogin())==0): "The Last Login dates should match";
            assert (anotherUser.getPassword().equalsIgnoreCase(user.getPassword())): "The passwords of both users should match";
            assert (anotherUser.getServer().getId()==1): "The server ID should be 1";
            assert (anotherUser.getUsername().equalsIgnoreCase(user.getUsername())): "The usernames of the 2 users dont match";

            userService.deleteUser(user);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
