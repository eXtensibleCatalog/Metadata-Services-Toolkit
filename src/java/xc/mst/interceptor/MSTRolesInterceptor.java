package xc.mst.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import xc.mst.bo.user.Group;
import xc.mst.bo.user.Permission;
import xc.mst.bo.user.User;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Checks theuser permission to execute the action 
 * 
 * @author Sharmila Ranganathan
 *
 */
public class MSTRolesInterceptor extends AbstractInterceptor {
	  
	/** Serial id */
	private static final long serialVersionUID = -7669556621856995137L;
	
	/** Roles allowed to view the action */
	private List<String> allowedRoles = new ArrayList<String>();
	
	/**
	 * Interceptor to check permission
	 */
	public String intercept(ActionInvocation invocation) throws Exception {
		
		User user = (User) invocation.getInvocationContext().getSession().get("user");
        List<Group> groups = user.getGroups();
        
        // If permission exist for user then continue executing the target action
        for(Group group:groups)
        {
        	for (Permission p: group.getPermissions()) {
        		for (String allowedRole: allowedRoles) {
	        		if (p.getTabName().equalsIgnoreCase(allowedRole)) {
	        			return invocation.invoke();
	        		}
        		}
        		
        	}

        }
        
        // Forward to access denied result if user does not have permission
	    return "access-denied";
	 }


	/**
	 * Splits a string into a List
	 */
	@SuppressWarnings("unchecked")
	protected List<String> stringToList(String val) {
		if (val != null) {
		String[] list = val.split("[ ]*,[ ]*");
		return Arrays.asList(list);
		} else {
		return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Set the allowed rules
	 * 
	 * @param roles
	 */
	public void setAllowedRoles(String roles) {
		this .allowedRoles = stringToList(roles);
	}

	 
}
