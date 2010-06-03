package xc.mst.services.example.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.utils.MSTConfiguration;

public class TestInstall extends BaseMetadataServiceTest {
	
	protected static final Logger LOG = Logger.getLogger(TestInstall.class);
	
	@Test
	public void testIntall() {
		try {
			String repoName = MSTConfiguration.getProperty("service.name");
			ServicesService ss = (ServicesService)MSTConfiguration.getBean("ServicesService");
			LOG.debug("repoName: "+repoName);
			Service s = ss.getServiceByName(repoName);
			if (s != null) {
				ss.deleteService(s);
			}
			ss.addNewService(repoName);
			s = ss.getServiceByName(repoName);
			//processRecords((GenericMetadataService)s.getMetadataService());
			//repositoryDAO.dropTables(repoName);
			LOG.debug("testinstall after");
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}
