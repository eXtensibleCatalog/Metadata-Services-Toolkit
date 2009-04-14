/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.log4j.Logger;
import org.apache.struts2.StrutsStatics;
import xc.mst.action.HarvestStatusAware;
import xc.mst.scheduling.Scheduler;

/**
 * Obtains the current process that is being run by the MST and sets it to the session
 *
 * @author Tejaswi Haramurali
 */
public class HarvestStatusInterceptor extends AbstractInterceptor implements StrutsStatics
{
    /** Log */
	private static Logger log = Logger.getLogger(UserInterceptor.class);

	/**
	 * Gets the current process being run by the MST and sets it to the action.
	 *
	 * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	public String intercept(ActionInvocation invocation) throws Exception {

		final Object action = invocation.getAction();
        String currentProcess = null;
        if(Scheduler.getRunningJob()!=null)
        {
            currentProcess = Scheduler.getRunningJob().getName();
        }

		if (action instanceof HarvestStatusAware)
        {

	            ((HarvestStatusAware) action).setCurrentProcess(currentProcess);

	    }

		return invocation.invoke();
	}

}
