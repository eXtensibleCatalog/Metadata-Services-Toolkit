/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.jdom.Element;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Record;
import xc.mst.repo.DefaultRepository;
import xc.mst.utils.XmlHelper;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	public String getRepoName() {
		return "test_repo";
	}
	
	public String getProviderUrl() {
		return "http://geolib.geo.auth.gr/digeo/index.php/index/oai";
	}
	
	@Override
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getDCFormat()};
	}
	
	public Format getHarvestOutFormat() throws Exception {
		return getDCFormat();
	}

}
