package xc.mst.servlet;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoaderListener;

public class MSTContextListener implements ServletContextListener {
	
	protected ContextLoaderListener contextLoaderListener = null;

    public void contextInitialized(ServletContextEvent sce) {
    	
    	String userDir = System.getProperty("user.dir");
		System.out.println("user.dir: "+userDir);
		
		String path =  sce.getServletContext().getContextPath();
		// Remove the / in '/MetadataServicesToolkit'
	    path = path.substring(1, path.length());
    	try {
	    	File workingDir = new File(".");
	    	String abs = workingDir.getAbsolutePath();
	    	System.out.println("abs: "+abs);
	    	String url = "file:/"+abs+"/MST-instances/MetadataServicesToolkit/";
	    	url = url.replaceAll("\\\\", "/");
	    	System.out.println("url: "+url);
	    	addURL(new URL(url));
	    	this.contextLoaderListener = new ContextLoaderListener();
	    	this.contextLoaderListener.contextInitialized(sce);
    	} catch (Throwable t) {
    		throw new RuntimeException(t);
    	}
    }
    
	public void contextDestroyed(ServletContextEvent sce) {
		this.contextLoaderListener.contextDestroyed(sce);
	}
	

	public static void addURL(URL u) {

		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		
		for (URL u2 : sysloader.getURLs()) {
			System.out.println("u: "+u2);
		}

		try {
			Method method = sysclass.getDeclaredMethod("addURL",  new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(sysloader, new Object[]{u});
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Error, could not add URL to system classloader");
		}

	}
}
