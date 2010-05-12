/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xc.mst.bo.service.Service;
import xc.mst.manager.BaseManager;
import xc.mst.services.MetadataService;
import xc.mst.utils.MSTConfiguration;

public class ServicesManager extends BaseManager implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {
	
	public static final Logger LOG = Logger.getLogger(ServicesManager.class);
	
	protected Map<String, ServiceEntry> serviceEntries = new HashMap<String, ServiceEntry>();
	protected Semaphore semaphore = new Semaphore(1);
	protected ApplicationContext applicationContext = null;
	protected static int loopCount = 0;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public boolean isRootAC() {
		boolean acquired = this.semaphore.tryAcquire();
		if (acquired) {
			this.semaphore.release();
		}
		return acquired;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		try {
			System.out.println("onApplicationEvent event2: "+event);
	        if (event instanceof ContextRefreshedEvent) {
	        	boolean b1 = this.applicationContext.getParent() == null;
	        	System.out.println("b1: "+b1);
	        	boolean b = isRootAC();
	        	System.out.println("b3: "+b);
	        	if (b) {
		            // load all service application contexts
	        		List<Service> services = getServiceDAO().getAll();
	        		System.out.println("services.size(): "+services.size());
		        	for (Service s : services) {
		        		if (loopCount++ > 10) {
		        			break;
		        		}
		        		System.out.println("in for loop");
		        		new ServiceEntry(s.getIdentifier()).start();
		        	}
	        	} else {

	        		System.out.println("before oae release");
	        		this.semaphore.release();
	        		System.out.println("after oae release");
	        	}
	        }
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
    }
	
	public MetadataService getService(String name) {
		System.out.println("name: "+name);
		if (serviceEntries.containsKey(name)) {
			ServiceEntry se = serviceEntries.get(name);
			System.out.println("se: "+se);
			System.out.println("se.ac: "+se.ac);
			return (MetadataService)se.ac.getBean("Service");
		}
		return null;
	}
	
	class ServiceEntry {
		public Thread t = null;
		public ClassPathXmlApplicationContext ac = null;
		public String id = null;
		public ServiceEntry thisthis = null;
		
		public ServiceEntry(String id) {
			this.id = id;
		}
		
		public void start() {
			thisthis = this;
			try {
				this.t = new Thread() {
					public void run() {
						try {
			        		String serviceFolder = MSTConfiguration.getUrlPath()+"/services/"+id;
			        		List<URL> urls = new ArrayList<URL>();
			        		String metaInfFolderStr = serviceFolder+"/META-INF";
			        		File libFolder = new File(metaInfFolderStr+"/lib");
			        		String classesFolderStr = metaInfFolderStr+"/classes/";
			        		System.out.println("classesFolderStr: "+classesFolderStr);
			        		if (libFolder != null) {
			        			System.out.println("serviceFolder: "+serviceFolder);
			        			if (libFolder.listFiles() != null) {
					        		for (File f : libFolder.listFiles()) {
					        			if (f.getName().endsWith(".jar")) {
					        				System.out.println("f.getAbsolutePath(): "+f.getAbsolutePath());
					        				urls.add(f.toURI().toURL());
					        			}
					        		}
			        			}
			        			File f = new File(classesFolderStr);
			        			URI uri = f.toURI();
			        			URL url = uri.toURL();
			        			url = new URL(url.toString()+"/");
			        			//URL url2 = new URL(classesFolderStr);
			        			System.out.println("url.toString2(): "+url.toString());
				        		urls.add(url);
				        		URL[] urlsArr = urls.toArray(new URL[]{});
				        		URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
				        		ac = new ClassPathXmlApplicationContext();
				        		ac.setClassLoader(loader);
				        		ac.setConfigLocation("spring-service.xml");
				        		ac.setParent(applicationContext);
				        		BufferedReader br = null;
				        		try {
				        			br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("spring-service.xml")));
				        		} catch (Throwable t) {
				        			semaphore.release();
				        			return;
				        		}
				        		StringBuilder sb = new StringBuilder();
				        		String line = null;
				        		while ((line = br.readLine()) != null) {
				        		sb.append(line + "\n");
				        		}
				        		br.close();
				        		System.out.println(sb.toString());
				        		System.out.println("before thread start");
				        		System.out.println("putting in key: "+id+" value:"+thisthis);
				        		serviceEntries.put(id, thisthis);
				        		ac.refresh();
				        		System.out.println("after thread start");
			        		}
						} catch (Throwable t) {
							semaphore.release();
							t.printStackTrace(System.out);
						}
					}
				};
				semaphore.acquire();
				t.start();
				semaphore.acquire();
				semaphore.release();
				System.out.println("after ServiceEntry.start");
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
}
