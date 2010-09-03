/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class SetupClasspath {
	
	public static void setupClasspath(String dir) {
		System.setProperty("line.separator", "\n");
		if (dir == null) {
			dir = "MetadataServicesToolkit";
		}
    	try {
    		String rootDir = null;
    		if (System.getenv("MST_ROOT_DIR") != null) {
    			rootDir = System.getenv("MST_ROOT_DIR");
    			if (rootDir.indexOf("beluga") != -1) {
    				rootDir = null;
    			}
    		}
    		for (String fStr : new String[] {
    				"install.properties", "env.properties"}) {
	    		if (rootDir == null) {
		    		try {
		    			InputStream reader = SetupClasspath.class.getClassLoader().getResourceAsStream(fStr);
		    			if (reader == null) {
		    				File f = new File(fStr);
		    				if (f.exists()) {
		    					reader = new FileInputStream(f);
		    				}
		    			}
		    			if (reader != null) {
				    		Properties props = new Properties();
				    		props.load(reader);
				    		reader.close();
				    		if (props.getProperty("mst.root.dir") != null) {
				    			rootDir = props.getProperty("mst.root.dir");
				    		}
				    		break;
		    			}
		    		} catch (Throwable t) {
		    			t.printStackTrace(System.out);
		    			t.printStackTrace(System.err);
		    		}
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
	    	String url = fileProto+rootDir+"MST-instances/"+dir+"/";
	    	url = url.replaceAll("\\\\", "/");
	    	System.out.println("url: "+url);
	    	addURL(new URL(url));
	    	
	    	System.setProperty("mst.root.dir", rootDir);
	    	
	    	PropertyConfigurator.configure(rootDir+"MST-instances/"+dir+"/log4j.config.txt");
    	} catch (Throwable t) {
    		throw new RuntimeException(t);
    	}
	}
	

	@SuppressWarnings("unchecked")
	public static void addURL(URL u) {

		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		
		for (URL u2 : sysloader.getURLs()) {
			//System.out.println("u: "+u2);
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
