package xc.mst.services.example.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.utils.MSTConfiguration;

public class TestInstall extends BaseTest {
	
	protected static final Logger LOG = Logger.getLogger(TestInstall.class);
	
	@Test
	public void testIntall() {
		try {
			String repoName = "example";
			ServicesService ss = (ServicesService)MSTConfiguration.getBean("ServicesService");
			LOG.debug("testInstall after");
			ss.addNewService(repoName);
			Service s = ss.getServiceByName(repoName);
			repositoryDAO.dropTables(repoName);
			LOG.debug("testinstall after");
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}
