/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.example.test;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.utils.MSTConfiguration;

public class CustomTest extends BaseMetadataServiceTest {
	
	private static final Logger LOG = Logger.getLogger(CustomTest.class);
	
	@Test
	public void testStuff() throws Exception {
		
		getRepositoryDAO().getRecordsWSets("test_repo", null, null, null);
		
		SolrQuery sq = new SolrQuery("*:*");
		SolrDocumentList sdl = getSolrIndexManager().getDocumentList(sq);
		LOG.debug("sdl.size(): "+sdl.size());
		
		sq = new SolrQuery("dc");
		sdl = getSolrIndexManager().getDocumentList(sq);
		LOG.debug("sdl.size(): "+sdl.size());
		LOG.debug("sdl.getNumFound(): "+sdl.getNumFound());
		
		Repository repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
		repo.setName("test_repo");
		List<Record> records = ((DefaultRepository)repo).getRecordsWSets(new Date(System.currentTimeMillis()-1000*60*60*24*50), new Date(), null);
		
		for (Record r : records) {
			LOG.debug(r.getId()+" "+r.getSets());
		}
	}
}
