package xc.mst.service.impl.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.repo.Repository;
import xc.mst.test.BaseMetadataServiceTest;
import xc.mst.utils.MSTConfiguration;

public class ResumePerfTest extends BaseMetadataServiceTest {
	
	private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);
	
	protected String getInputRepoName() {
		return null;
	}
	
	@Test
	public void resumePerfTest() {
		try {
			LOG.debug("resumePerfTest");
			//getJdbcTemplate().update("delete from MetadataServicesToolkit.service_harvests");
			//((DefaultRepository)getRepository()).deleteAllData();
			Repository priorRepo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
			priorRepo.setName(getInputRepoName());
			LOG.debug("getInputRepoName(): "+getInputRepoName());
			LOG.debug("getMetadataService(): "+getMetadataService());
			getMetadataService().process(priorRepo, null, null, null);
		} catch (Throwable t) {
			LOG.error("", t);
		}
		
	}

}
