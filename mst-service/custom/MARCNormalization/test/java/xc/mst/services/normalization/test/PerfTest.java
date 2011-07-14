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

public class PerfTest extends StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(PerfTest.class);
	
	public String getRepoName() {
		return "135_5M";
	}
	
	public String getProviderUrl() {
		//return "http://128.151.244.132:8080/OAIToolkit_ver0.6.4/oai-request.do";
		//return "http://128.151.244.137:8080/OAIToolkit/oai-request.do";
		return "http://128.151.244.135:8080/OAIToolkit/oai-request.do";
	}
	
	protected long getNumberOfRecordsToHarvest() {
		return 0;
	}

	@Test
	public void startToFinish() throws Exception  {
		dropOldSchemas();
		LOG.debug("after dropOldSchemas");
		installProvider();
		LOG.debug("after installProvider");
		installService();
		LOG.debug("after installService");

		configureProcessingRules();
		LOG.debug("after configureProcessingRules");
		createHarvestSchedule();
		LOG.debug("after createHarvestSchedule");

		waitUntilFinished();
		LOG.debug("after waitUntilFinished");
		
		finalTest();
	}
	
	public void finalTest() {
	}
}