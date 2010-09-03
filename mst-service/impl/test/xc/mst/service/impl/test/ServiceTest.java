/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.service.impl.test;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataService;

public class ServiceTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(ServiceTest.class);
	
	protected String serviceName = "r1";

	@BeforeClass
	public void setup() {
	}
	
	@Test
	public void testAll() {
		process();
	}
	
	public void process() {
		try {
			Service s = getServicesService().getServiceByName("MARCToXCTransformation");
			MetadataService ms = s.getMetadataService();
			LOG.debug("ms: "+ms);
			Repository srepo = ms.getRepository();
			repositoryDAO.dropTables(srepo.getName());
			srepo.installOrUpdateIfNecessary(null, s.getVersion());
			
			ms.process(repo, null, null, null);
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}
