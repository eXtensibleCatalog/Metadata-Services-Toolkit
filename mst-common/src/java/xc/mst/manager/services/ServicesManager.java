package xc.mst.manager.services;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xc.mst.bo.service.Service;
import xc.mst.manager.BaseManager;
import xc.mst.services.MetadataService;
import xc.mst.utils.MSTConfiguration;

public class ServicesManager extends BaseManager implements ApplicationListener<ApplicationEvent> {
	
	protected Map<String, ApplicationContext> servicesApplicationContexts = new HashMap<String, ApplicationContext>();
    
	public void onApplicationEvent(ApplicationEvent event) {
		try {
	        if (event instanceof ContextRefreshedEvent) {
	            // load all service application contexts
	        	for (Service s : getServiceDAO().getAll()) {
	        		String serviceFolder = MSTConfiguration.getUrlPath()+"/"+s.getIdentifier();
	        		List<URL> urls = new ArrayList<URL>();
	        		String metaInfFolderStr = serviceFolder+"/META-INF";
	        		File libFolder = new File(metaInfFolderStr+"/lib");
	        		if (libFolder != null) {
	        			System.out.println("serviceFolder: "+serviceFolder);
	        			if (libFolder.listFiles() != null) {
			        		for (File f : libFolder.listFiles()) {
			        			if (f.getName().endsWith(".jar")) {
			        				urls.add(f.toURI().toURL());
			        			}
			        		}
	        			}
		        		urls.add(new File(metaInfFolderStr+"/classes").toURI().toURL());
		        		URL[] urlsArr = urls.toArray(new URL[]{});
		        		URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
		        		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext();
		        		ac.setClassLoader(loader);
		        		ac.setConfigLocation("spring-service.xml");
		        		
		        		servicesApplicationContexts.put(s.getName(), ac);
	        		}
	        	}
	        }
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
    }
	
	public MetadataService getService(String name) {
		if (servicesApplicationContexts.containsKey(name)) {
			ApplicationContext ac = servicesApplicationContexts.get(name);
			return (MetadataService)ac.getBean("Service");
		}
		return null;
	}

}
