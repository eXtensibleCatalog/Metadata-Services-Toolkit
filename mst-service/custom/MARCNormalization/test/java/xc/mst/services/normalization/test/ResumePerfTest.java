/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.normalization.test;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.ServiceHarvest;
import xc.mst.constants.Status;

public class ResumePerfTest extends xc.mst.service.impl.test.ResumePerfTest {
	
	private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);
	
	@Override
	protected String getInputRepoName() {
		return "135_5m";
	}
	
	@Override
	@Test
	public void resumePerfTest() {
		LOG.info("getInputRepoName(): "+getInputRepoName());
		getJdbcTemplate().update("delete from MetadataServicesToolkit.service_harvests");
		getJdbcTemplate().update("insert into MetadataServicesToolkit.service_harvests "+
				"(service_id, repo_name, from_date, until_date, highest_id) values (?, ?, ?, ?, ?)",
				getMetadataService().getService().getId(),
				getInputRepoName(),
				new Date(0),
				new Date(),
				1540000);
		/*
		ServiceHarvest sh = new ServiceHarvest();
		sh.setService(getMetadataService().getService());
		sh.setRepoName(getInputRepoName());
		sh.setFrom(new Date(0));
		sh.setHighestId(2000000l);
		getHibernateTemplate().persist(sh);
		*/
		List<ServiceHarvest> shs = getHibernateTemplate().loadAll(ServiceHarvest.class);
		LOG.info("all service harvests: "+shs);
		if (shs != null) {
			for (ServiceHarvest sh : shs) {
				LOG.info("sh.getId(): "+sh.getId());
				LOG.info("sh.getHighestId(): "+sh.getHighestId());
				LOG.info("sh.getFormat(): "+sh.getFormat());
				LOG.info("sh.getSet(): "+sh.getSet());
			}
		}
		LOG.info("getInputRepoName(): "+getInputRepoName());
		super.resumePerfTest();
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
				} else {
					LOG.debug("scheduler.getRunningJob() == null");
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
