/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.normalization.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class MockHarvestTest extends xc.mst.service.impl.test.MockHarvestTest {

	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "file://"+INPUT_FOLDER+"/"+getFolder();
	}
	
	public String getFolder() {
		return "randys-30";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getMarcXmlFormat();
	}

	protected Format getHarvestOutFormat() throws Exception {
		return getMarcXmlFormat();
	}

	@Override
	protected void finalTest() throws Exception {
	}
	
	@Override
	protected void testHarvestOut() {
	}

}
