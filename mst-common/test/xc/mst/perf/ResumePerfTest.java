package xc.mst.perf;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.services.GenericMetadataService;

public class ResumePerfTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);
	
	@Test
	public void resumePerfTest() {

		try {
			getJdbcTemplate().update("delete from service_harvests");
			
			String serviceName = "MARCToXCTransformation";
			String repoName = "MARCNormalization";
			
			getRepositoryDAO().deleteSchema(serviceName);
			getServicesService().addNewService(serviceName);
			
			Service s = getServicesService().getServiceByName(serviceName);
			GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
			
			((DefaultRepository)ms.getRepository()).deleteAllData();
			Repository repo = (Repository)getBean("Repository");
			repo.setName(repoName);
			ms.process(repo, null, null, null);
		} catch (Throwable t) {
			LOG.error("", t);
		}
		
	}

}
