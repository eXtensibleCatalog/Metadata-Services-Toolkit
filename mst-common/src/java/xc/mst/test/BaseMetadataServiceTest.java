/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.test;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.BeforeSuite;

import xc.mst.common.test.BaseTest;
import xc.mst.repo.Repository;
import xc.mst.services.GenericMetadataService;
import xc.mst.services.MetadataService;
import xc.mst.spring.TestTypeFilter;

public class BaseMetadataServiceTest extends BaseTest {
	
	protected JdbcTemplate jdbcTemplate = null;
	
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
	}
	
	protected JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
	
	@Override
	public void shutdown() {
	}
	
}
