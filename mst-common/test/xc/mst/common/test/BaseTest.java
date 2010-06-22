/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.repository.SetService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;
import xc.mst.repo.Repository;
import xc.mst.repo.RepositoryDAO;
import xc.mst.repo.RepositoryService;
import xc.mst.scheduling.Scheduler;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.SetupClasspath;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.Util;

public class BaseTest {
	
	protected static final Logger LOG = Logger.getLogger(BaseTest.class);
	
	protected ApplicationContext applicationContext = null;
	
	protected Util util = null;
	protected Repository repo = null;
	protected RepositoryDAO repositoryDAO = null;
	protected RecordService recordService = null;
	protected ServicesService servicesService = null;
	protected ProviderService providerService = null;
	protected ScheduleService scheduleService = null;
	protected FormatService formatService = null;
	protected SetService setService = null;
	protected UserService userService = null;	 	
	protected ServerService serverService = null;
	protected ValidateRepository validateRepository = null;
	protected FormatDAO formatDAO = null;
	protected SetDAO setDAO = null;
	protected HarvestScheduleDAO harvestScheduleDAO = null;
	protected ProcessingDirectiveDAO processingDirectiveDAO = null;
	protected Scheduler scheduler = null;

	@BeforeSuite
	public void startup() {
		LOG.debug("startup");
		try {
			SetupClasspath.setupClasspath(null);
			applicationContext = new ClassPathXmlApplicationContext("spring-mst.xml");
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
		util = (Util)MSTConfiguration.getInstance().getBean("Util");
		repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
		repo.setName("r1");
		repositoryDAO = (RepositoryDAO)getBean("RepositoryDAO");
		recordService = (RecordService)getBean("RecordService");
		servicesService = (ServicesService)getBean("ServicesService");
		providerService = (ProviderService)getBean("ProviderService");
		scheduleService = (ScheduleService)getBean("ScheduleService");
		formatService = (FormatService)getBean("FormatService");
		setService = (SetService)getBean("SetService");
		userService = (UserService)getBean("UserService");	 	
		serverService = (ServerService)getBean("ServerService");
		validateRepository = (ValidateRepository)getBean("ValidateRepository");
		formatDAO = (FormatDAO)getBean("FormatDAO");
		harvestScheduleDAO = (HarvestScheduleDAO)getBean("HarvestScheduleDAO");
		setDAO = (SetDAO)getBean("SetDAO");  
		processingDirectiveDAO = (ProcessingDirectiveDAO)getBean("ProcessingDirectiveDAO");
		scheduler = (Scheduler)getBean("Scheduler");
		LOG.debug("startup complete");
	}
	
	protected Object getBean(String name) {
		return MSTConfiguration.getInstance().getBean(name);
	}
	
	@AfterSuite
	public void shutdown() {
		TimingLogger.reset(true);
		LOG.debug("shutdown");
	}
	
	protected void printClassPath() {
		//Get the System Classloader
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

		//Get the URLs
		URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();

		for(int i=0; i< urls.length; i++) {
			LOG.debug(urls[i].getFile());
		} 
	}
	
	protected ProviderDAO getProviderDAO() {
		return (ProviderDAO)getBean("ProviderDAO"); 
	}
	
	public RepositoryService getRepositoryService() {
		return (RepositoryService)getBean("RepositoryService");
	}

}
