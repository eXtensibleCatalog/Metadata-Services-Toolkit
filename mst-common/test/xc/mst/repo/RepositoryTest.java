package xc.mst.repo;

import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;
import xc.mst.manager.services.ServicesManager;
import xc.mst.services.MetadataService;
import xc.mst.utils.MSTConfiguration;

public class RepositoryTest extends BaseTest {

	@Test
	public void testSetup() {
		try {
			String repoName = "r1";
			Repository repo = (Repository)MSTConfiguration.getBean("Repository");
			RepositoryDAO repositoryDAO = (RepositoryDAO)MSTConfiguration.getBean("RepositoryDAO");
			ServicesManager serviceManager = (ServicesManager)MSTConfiguration.getBean("ServicesManager");
			repositoryDAO.dropTables(repoName);
			repo.setName(repoName);
			repo.installOrUpdateIfNecessary();
			MetadataService norm = serviceManager.getService("MARCNormalization");
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
