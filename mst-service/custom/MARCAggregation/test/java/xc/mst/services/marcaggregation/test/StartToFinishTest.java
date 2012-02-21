/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

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

    @Override
    protected void finalTest() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void testHarvestOut() {
        // TODO Auto-generated method stub
    }

}
