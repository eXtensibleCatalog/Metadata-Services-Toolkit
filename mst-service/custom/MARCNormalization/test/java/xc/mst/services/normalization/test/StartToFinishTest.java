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
import org.testng.annotations.Test;

import xc.mst.bo.provider.Format;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	public String getRepoName() {
		return "test_repo";
	}
	
	@Override
	protected long getNumberOfRecordsToHarvest() {
		//return Integer.MAX_VALUE;
		return 1000;
	}

	/*
	@Override
	protected String getSetSpec() {
		return getRepoName()+":publication:com_5Farchival";
	}
	*/
	
	public String getProviderUrl() {
		return "http://128.151.244.135:8080/OAIToolkit/oai-request.do";
	}
	
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarc21Format()};
	}

	public Format getHarvestOutFormat() throws Exception {
		return getMarc21Format();
	}
	
	public void finalTest() {
	}

	/**
	 * To test harvest out functionality
	 */
	public void testHarvestOut() {
	}

}