/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.test;

import java.util.Date;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.testng.annotations.BeforeSuite;

import xc.mst.common.test.BaseTest;
import xc.mst.constants.Status;
import xc.mst.repo.Repository;
import xc.mst.services.GenericMetadataService;
import xc.mst.services.MetadataService;
import xc.mst.spring.TestTypeFilter;

public class BaseMetadataServiceTest extends BaseTest {
	
	protected JdbcTemplate jdbcTemplate = null;
	protected HibernateTemplate hibernateTemplate = null; 
	
	protected String getServiceName() {
		return getUtil().normalizeName(System.getenv("service.name"));
	}
	
	protected MetadataService getMetadataService() {
		return TestTypeFilter.metadataService;
	}
	
	protected Repository getRepository() {
		return TestTypeFilter.metadataService.getRepository();
	}
	
	@Override
	@BeforeSuite
	public void startup() {
		jdbcTemplate = new JdbcTemplate((DataSource)((GenericMetadataService)TestTypeFilter.metadataService).
				getConfig().getBean("MetadataServiceDataSource"));
		hibernateTemplate = new HibernateTemplate((SessionFactory)((GenericMetadataService)TestTypeFilter.metadataService).
				getConfig().getBean("SessionFactory"));
	}
	
	protected JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
	
	protected HibernateTemplate getHibernateTemplate() {
		return this.hibernateTemplate;
	}
	
	@Override
	public void shutdown() {
	}
	
	public void waitUntilFinished() {
		int timesNotRunning = 0;
		while (true) {
			LOG.debug("checking to see if finished");
			try {
				Thread.sleep(1000);
				Date lastModified = getRepositoryService().getLastModified();
				LOG.debug("lastModified :"+lastModified);
				if (lastModified != null && lastModified.after(new Date())) {
					LOG.debug("Future dated!");
					continue;
				}
				if (getScheduler().getRunningJob() != null) {
					LOG.debug("scheduler.getRunningJob().getJobStatus(): "+getScheduler().getRunningJob().getJobStatus());
					LOG.debug("scheduler.getRunningJob().getJobName(): "+getScheduler().getRunningJob().getJobName());
				}
				if (getScheduler().getRunningJob() == null || 
						Status.RUNNING != getScheduler().getRunningJob().getJobStatus()) {
					timesNotRunning++;
				} else {
					timesNotRunning = 0;
				}
				if (timesNotRunning > 7) {
					break;
				}
				LOG.debug("timeNotRunning: "+timesNotRunning);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			
		}
	}
	
}
