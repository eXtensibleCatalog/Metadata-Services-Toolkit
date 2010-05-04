package xc.mst.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoaderListener;

import xc.mst.utils.MSTConfiguration;

public class MSTContextListener implements ServletContextListener {
	
	protected ContextLoaderListener contextLoaderListener = null;

    public void contextInitialized(ServletContextEvent sce) {
		String path =  sce.getServletContext().getContextPath();
		// Remove the / in '/MetadataServicesToolkit'
	    path = path.substring(1, path.length());
    	try {
    		String rootDir = null;
    		/*
    		if (System.getenv("MST_ROOT_DIR") != null) {
    			rootDir = System.getenv("MST_ROOT_DIR");
    		}
    		*/
    		if (rootDir == null) {
	    		try {
	    			BufferedReader reader = new BufferedReader(new InputStreamReader(
	    					getClass().getClassLoader().getResourceAsStream(
		    			        "env.properties")));
		    		Properties props = new Properties();
		    		props.load(reader);
		    		reader.close();
		    		if (props.getProperty("mst.root.dir") != null) {
		    			rootDir = props.getProperty("mst.root.dir");
		    		}
	    		} catch (Throwable t) {
	    			t.printStackTrace(System.out);
	    			t.printStackTrace(System.err);
	    		}
    		}

    		if (rootDir == null) {
    	    	File workingDir = new File(".");
    	    	rootDir = workingDir.getAbsolutePath();
    	    	rootDir += "/";
    		}
    		MSTConfiguration.rootDir = rootDir;
    		System.out.println("rootDir: "+rootDir);
    		String fileProto = "file:";
    		if (!rootDir.startsWith("/")) {
    			fileProto = fileProto+"/";
    		}
	    	String url = fileProto+rootDir+"/MST-instances/MetadataServicesToolkit/";
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
	

	@SuppressWarnings("unchecked")
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
