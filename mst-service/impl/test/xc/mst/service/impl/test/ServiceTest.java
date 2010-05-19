package xc.mst.service.impl.test;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataService;

public class ServiceTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(ServiceTest.class);
	
	protected String serviceName = "r1";

	@BeforeClass
	public void setup() {
	}
	
	@Test
	public void testAll() {
		process();
	}
	
	public void process() {
		try {
			Service s = servicesService.getServiceByName("MARCNormalization");
			MetadataService ms = s.getMetadataService();
			LOG.debug("ms: "+ms);
			Repository repo = ms.getRepository();
			LOG.debug("repo: "+repo);
			repositoryDAO.dropTables(repo.getName());
			repo.installOrUpdateIfNecessary();
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}
