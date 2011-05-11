/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import java.net.URL;
import java.net.URLClassLoader;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordCounts;
import xc.mst.dao.harvest.HarvestDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.dao.record.MessageDAO;
import xc.mst.dao.record.RecordCountsDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.processingDirective.JobService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.repository.SetService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;
import xc.mst.repo.RepositoryDAO;
import xc.mst.repo.RepositoryService;
import xc.mst.scheduling.Scheduler;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.SetupClasspath;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;
import xc.mst.utils.index.SolrIndexManager;

public class BaseTest {

	protected static final Logger LOG = Logger.getLogger(BaseTest.class);

	protected ApplicationContext applicationContext = null;
	protected XmlHelper xmlHelper = new XmlHelper();

	@BeforeSuite
	public void startup() {
		LOG.debug("startup");
		try {
			SetupClasspath.setupClasspath(null);
			applicationContext = new ClassPathXmlApplicationContext("spring-mst.xml");
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}

		LOG.debug("startup complete");
		/*
		SessionFactory sessionFactory = (SessionFactory)getBean("SessionFactory");
		sessionFactory.openSession();
		*/
	}

	protected Object getBean(String name) {
		return MSTConfiguration.getInstance().getBean(name);
	}

	@AfterSuite
	public void shutdown() {
		TimingLogger.reset(true);
		LOG.debug("shutdown");
		/*
		SessionFactory sessionFactory = (SessionFactory)getBean("SessionFactory");
		sessionFactory.close();
		*/
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


	protected void testCounts(int[] ec, RecordCounts rc) {
		int i=0;
		if (ec.length == 9) {
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.NEW_ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.NEW_DELETE), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.UPDATE_ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.UPDATE_DELETE), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.ACTIVE, Record.ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.ACTIVE, Record.DELETED), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.DELETED, Record.ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.DELETED, Record.DELETED), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.UNEXPECTED_ERROR), ec[i++]);
		} else {
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.NEW_ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.NEW_HELD), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.NEW_DELETE), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.UPDATE_ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.UPDATE_HELD), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, RecordCounts.UPDATE_DELETE), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.ACTIVE, Record.ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.ACTIVE, Record.HELD), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.ACTIVE, Record.DELETED), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.HELD, Record.ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.HELD, Record.HELD), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.HELD, Record.DELETED), ec[i++]);

			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.DELETED, Record.ACTIVE), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.DELETED, Record.HELD), ec[i++]);
			Assert.assertEquals(rc.getCount(RecordCounts.TOTALS, Record.DELETED, Record.DELETED), ec[i++]);
		}
	}

	protected ProviderDAO getProviderDAO() {
		return (ProviderDAO)getBean("ProviderDAO");
	}

	public RepositoryService getRepositoryService() {
		return (RepositoryService)getBean("RepositoryService");
	}

	public JobService getJobService() {
		return (JobService)getBean("JobService");
	}

	public ServicesService getServicesService() {
		return (ServicesService)getBean("ServicesService");
	}

	protected RepositoryDAO getRepositoryDAO() {
		return (RepositoryDAO)getBean("RepositoryDAO");
	}

	protected RecordCountsDAO getRecordCountsDAO() {
		return (RecordCountsDAO)getBean("RecordCountsDAO");
	}

	protected ServiceDAO getServiceDAO() {
		return (ServiceDAO)getBean("ServiceDAO");
	}

	protected FormatDAO getFormatDAO() {
		return (FormatDAO)getBean("FormatDAO");
	}

	protected SetDAO getSetDAO() {
		return (SetDAO)getBean("SetDAO");
	}

	protected MessageDAO getMessageDAO() {
		return (MessageDAO)getBean("MessageDAO");
	}

	protected SetService getSetService() {
		return (SetService)getBean("SetService");
	}

	protected SolrIndexManager getSolrIndexManager() {
		return (SolrIndexManager)getBean("SolrIndexManager");
	}

	protected Util getUtil() {
		return (Util)MSTConfiguration.getInstance().getBean("Util");
	}

	protected RecordService getRecordService() {
		return (RecordService)getBean("RecordService");
	}

	protected ProviderService getProviderService() {
		return (ProviderService)getBean("ProviderService");
	}

	protected ScheduleService getScheduleService() {
		return (ScheduleService)getBean("ScheduleService");
	}

	protected FormatService getFormatService() {
		return (FormatService)getBean("FormatService");
	}

	protected UserService getUserService() {
		return (UserService)getBean("UserService");
	}

	protected ServerService getServerService() {
		return (ServerService)getBean("ServerService");
	}

	protected ValidateRepository getValidateRepository() {
		return (ValidateRepository)getBean("ValidateRepository");
	}

	protected HarvestScheduleDAO getHarvestScheduleDAO() {
		return (HarvestScheduleDAO)getBean("HarvestScheduleDAO");
	}

	protected HarvestDAO getHarvestDAO() {
		return (HarvestDAO)getBean("HarvestDAO");
	}

	protected ProcessingDirectiveDAO getProcessingDirectiveDAO() {
		return (ProcessingDirectiveDAO)getBean("ProcessingDirectiveDAO");
	}

	protected Scheduler getScheduler() {
		return (Scheduler)getBean("Scheduler");
	}

	protected JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate((DataSource)getBean("DataSource"));
	}
}
