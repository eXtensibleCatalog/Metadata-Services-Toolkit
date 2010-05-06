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
	
	protected ServicesManager parentServicesManager = null;
	protected Map<String, ServiceEntry> servicesApplicationContexts = new HashMap<String, ServiceEntry>();
	protected Semaphore semaphore = new Semaphore(1);
	protected ServiceEntry initializingService = null;
	protected ApplicationContext applicationContext = null;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	public void setParentServicesManager(ServicesManager parentServicesManager) {
		this.parentServicesManager = parentServicesManager;
	}
	
	public boolean isRootAC() {
		return this.parentServicesManager == null;
		/*
		try {
			Object o = this.applicationContext.getBean("Service");
			if (o != null) {
				return false;
			}
		} catch (Throwable t) {
		}
		return true;
		*/
	}

	public void onApplicationEvent(ApplicationEvent event) {
		try {
	        if (event instanceof ContextRefreshedEvent) {
	        	if (isRootAC()) {
		            // load all service application contexts
		        	for (Service s : getServiceDAO().getAll()) {
		        		initializingService = new ServiceEntry(s.getIdentifier());
		        		initializingService.start();
		        	}
	        	} else {
	        		servicesApplicationContexts.put(
	        				parentServicesManager.initializingService.id, parentServicesManager.initializingService);
	        		System.out.println("before oae release");
	        		parentServicesManager.semaphore.release();
	        		System.out.println("after oae release");
	        	}
	        }
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
    }
	
	public MetadataService getService(String name) {
		if (servicesApplicationContexts.containsKey(name)) {
			ServiceEntry se = servicesApplicationContexts.get(name);
			return (MetadataService)se.ac.getBean("Service");
		}
		return null;
	}
	
	class ServiceEntry {
		public Thread t = null;
		public ClassPathXmlApplicationContext ac = null;
		public String id = null;
		
		public ServiceEntry(String id) {
			this.id = id;
		}
		
		public void start() {
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
			        			System.out.println("url.toString(): "+url.toString());
				        		urls.add(url);
				        		URL[] urlsArr = urls.toArray(new URL[]{});
				        		URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
				        		ac = new ClassPathXmlApplicationContext();
				        		ac.setClassLoader(loader);
				        		ac.setConfigLocation("spring-service.xml");
				        		BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("spring-service.xml")));
				        		StringBuilder sb = new StringBuilder();
				        		String line = null;
				        		while ((line = br.readLine()) != null) {
				        		sb.append(line + "\n");
				        		}
				        		br.close();
				        		System.out.println(sb.toString());
				        		
				        		//System.out.println("before ac.start");
				        		//Class c = loader.loadClass("xc.mst.services.MetadataService");
				        		//System.out.println("c: "+c);
				        		//ac = new ClassPathXmlApplicationContext(new String[] {"spring-service.xml"}, c, applicationContext);
				        		System.out.println("before thread start");
				        		ac.refresh();
				        		System.out.println("after thread start");
			        		}
						} catch (Throwable t) {
							throw new RuntimeException(t);
						}
					}
				};
				t.start();
				semaphore.acquire();
				semaphore.acquire();
				semaphore.release();
				System.out.println("after ServiceEntry.start");
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
}
