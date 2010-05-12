/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoaderListener;

import xc.mst.utils.SetupClasspath;

public class MSTContextListener implements ServletContextListener {
	
	protected ContextLoaderListener contextLoaderListener = null;

    public void contextInitialized(ServletContextEvent sce) {
		String path =  sce.getServletContext().getContextPath();
		// Remove the / in '/MetadataServicesToolkit'
	    path = path.substring(1, path.length());
	    SetupClasspath.setupClasspath(path);
    	this.contextLoaderListener = new ContextLoaderListener();
    	this.contextLoaderListener.contextInitialized(sce);
    }
    
	public void contextDestroyed(ServletContextEvent sce) {
		this.contextLoaderListener.contextDestroyed(sce);
	}
	

}
