/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

import xc.mst.bo.user.User;

/**
 * This is home action.
 *
 * @author Sharmila Ranganathan
 *
 */
public class Home extends BaseActionSupport {

	/**
	 * Generated id
	 */
	private static final long serialVersionUID = -1408128908379762953L;

	/** User Logged in */
	private User user;

	/** Id of User Logged in */
	private int userId;

	/** Test message */
	private String test = "Login Success full";

	/**
     * A default implementation that does nothing and returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception
    {

    	user = getUserService().getUserById(userId);

        return SUCCESS;
    }

	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	public String getTest() {

		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
