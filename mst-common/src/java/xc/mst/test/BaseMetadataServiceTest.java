/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.test;

import xc.mst.common.test.BaseTest;
import xc.mst.services.MetadataService;
import xc.mst.spring.TestTypeFilter;

public class BaseMetadataServiceTest extends BaseTest {
	
	protected String getServiceName() {
		return System.getenv("service.name");
	}
	
	protected MetadataService getMetadataService() {
		return TestTypeFilter.metadataService;
	}
	
	@Override
	public void startup() {
	}
	
	@Override
	public void shutdown() {
	}
	
}
