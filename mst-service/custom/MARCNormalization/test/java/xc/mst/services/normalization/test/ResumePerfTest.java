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

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.constants.Status;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.services.GenericMetadataService;
import xc.mst.test.BaseMetadataServiceTest;

public class ResumePerfTest extends BaseMetadataServiceTest {
	
	private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);
	
	
	@Test
	public void resumePerfTest() {

		try {
			getRepositoryDAO().deleteSchema(getServiceName());
			getServicesService().addNewService(getServiceName());
			
			Service s = getServicesService().getServiceByName(getServiceName());
			GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
			
			((DefaultRepository)ms.getRepository()).deleteAllData();
			
			Set incomingSet = getSetDAO().getById(9);
			//Set outgoingSet = getSetDAO().getById(10);
			//LOG.debug("outgoingSet: "+outgoingSet);
			//outgoingSet = getSetDAO().getById(3);
			//LOG.debug("outgoingSet: "+outgoingSet);
			Format format = getFormatDAO().getById(3);
			
			Repository repo = (Repository)getBean("Repository");
			repo.setName("135_5m");

			//ms.process(repo, format, incomingSet, null);
			ms.process(repo, null, null, null);
			waitUntilFinished();
		} catch (Throwable t) {
			LOG.error("", t);
		}
		
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
