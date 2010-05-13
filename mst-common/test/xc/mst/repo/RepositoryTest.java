package xc.mst.repo;

import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;
import xc.mst.utils.MSTConfiguration;

public class RepositoryTest extends BaseTest {

	@Test
	public void testSetup() {
		try {
			String repoName = "r1";
			Repository repo = (Repository)MSTConfiguration.getBean("Repository");
			RepositoryDAO repositoryDAO = (RepositoryDAO)MSTConfiguration.getBean("RepositoryDAO");
			repositoryDAO.dropTables(repoName);
			repo.setName(repoName);
			repo.installOrUpdateIfNecessary();
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
