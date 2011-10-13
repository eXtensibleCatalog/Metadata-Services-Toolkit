/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.action;

import xc.mst.bo.user.User;

/**
 * Interface for setting the user in action
 * 
 * @author Sharmila Ranganathan
 * 
 */
public interface UserAware {

    /**
     * Set User
     * 
     * @param user
     *            Logged in user
     */
    public void setUser(User user);

}
