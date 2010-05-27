/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.RecordService;
import xc.mst.repo.Repository;
import xc.mst.repo.RepositoryDAO;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.SetupClasspath;

public class BaseTest {
	
	protected static final Logger LOG = Logger.getLogger(BaseTest.class);
	
	protected ApplicationContext applicationContext = null;
	
	protected Repository repo = null;
	protected RepositoryDAO repositoryDAO = null;
	protected RecordService recordService = null;
	protected ServicesService servicesService = null;

	@BeforeSuite
	public void startup() {
		LOG.debug("startup");
		try {
			SetupClasspath.setupClasspath(null);
			applicationContext = new ClassPathXmlApplicationContext("spring-mst.xml");
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
		repo = (Repository)MSTConfiguration.getBean("Repository");
		repo.setName("r1");
		repositoryDAO = (RepositoryDAO)MSTConfiguration.getBean("RepositoryDAO");
		recordService = (RecordService)MSTConfiguration.getBean("RecordService");
		servicesService = (ServicesService)MSTConfiguration.getBean("ServicesService");
		LOG.debug("startup complete");
	}
	
	@AfterSuite
	public void shutdown() {
		LOG.debug("shutdown");
	}
	
}
