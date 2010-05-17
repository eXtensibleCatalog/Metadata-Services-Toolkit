package xc.mst.repo;

import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.common.test.BaseTest;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.services.ServicesManager;
import xc.mst.services.GenericMetadataService;
import xc.mst.utils.MSTConfiguration;

public class RepositoryTest extends BaseTest {

	@Test
	public void testSetup() {
		try {
			String repoName = "r1";
			Repository repo = (Repository)MSTConfiguration.getBean("Repository");
			RepositoryDAO repositoryDAO = (RepositoryDAO)MSTConfiguration.getBean("RepositoryDAO");
			RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");
			ServicesManager serviceManager = (ServicesManager)MSTConfiguration.getBean("ServicesManager");
			ServicesService servicesService = (ServicesService)MSTConfiguration.getBean("ServicesService");
			
			repositoryDAO.dropTables(repoName);
			repo.setName(repoName);
			repo.installOrUpdateIfNecessary();
			
			Record previousRecord = new Record();
			GenericMetadataService norm = serviceManager.getService("MARCNormalization");
			//Record record = recordService.createSuccessor(previousRecord, norm);
			
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
