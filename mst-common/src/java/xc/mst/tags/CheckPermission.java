/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.tags;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.bo.user.User;

/**
 * Tag to check user permissions
 *
 * @author Sharmila Ranganathan
 */
public class CheckPermission extends SimpleTagSupport {

	/** Permission to check on the user */
	private String permission;

    @Override
    public void doTag() throws JspException
    {
        Boolean permissionGranted = false;
        PageContext pageContext = (PageContext) getJspContext();

        User user = (User) pageContext.getSession().getAttribute("user");

        List<Group> groups = user.getGroups();

        for(Group group:groups)
        {
        	for (Permission p: group.getPermissions()) {
        		if (p.getTabName().equalsIgnoreCase(permission)) {
        			permissionGranted = true;
        			break;
        		}
        	}

        	if (permissionGranted) {
        		break;
        	}
        }

        // If permission exist then execute the JSP
        if (permissionGranted) {
    	    try
    		{
    	    	if( getJspBody() != null )
    	    	{
    		        getJspBody().invoke(null);
    	    	}
    		}
    		catch(Exception e)
    		{
    			throw new JspException("could not invoke body",e);
    		}
        }


    }

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

}
