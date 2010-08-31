package xc.mst.services.normalization.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class PerfTest extends StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(PerfTest.class);
	
	protected String getRepoName() {
		return "135_5M";
	}
	
	protected String getProviderUrl() {
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
	
	protected void finalTest() {
	}
}