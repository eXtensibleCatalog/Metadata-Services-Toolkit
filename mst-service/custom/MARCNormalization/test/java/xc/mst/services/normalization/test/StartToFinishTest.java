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
import xc.mst.bo.provider.Set;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	public String getRepoName() {
		return "test_repo";
	}
	
	@Override
	protected long getNumberOfRecordsToHarvest() {
		//return Integer.MAX_VALUE;
		return 500;
	}
	
	public String getProviderUrl() {
		return "http://128.151.244.137:8080/OAIToolkit/oai-request.do";
	}
	
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarcXmlFormat()};
	}

	public Format getHarvestOutFormat() throws Exception {
		return getMarcXmlFormat();
	}
	
	@Test
	public void startToFinish() throws Exception  {
		dropOldSchemas();
		installProvider();
		
		Set s = getSetService().getSetBySetSpec("bib");
		createHarvestSchedule("hs1", s);
		
		waitUntilFinished();
		s = getSetService().getSetBySetSpec("hold");
		createHarvestSchedule("hs2", s);
		
		waitUntilFinished();		
	}
	
	public void finalTest() {
	}

	/**
	 * To test harvest out functionality
	 */
	public void testHarvestOut() {
	}
}