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
