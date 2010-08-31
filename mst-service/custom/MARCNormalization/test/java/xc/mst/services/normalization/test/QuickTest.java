package xc.mst.services.normalization.test;

import gnu.trove.TLongHashSet;

import org.testng.annotations.Test;

import xc.mst.repo.Repository;
import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class QuickTest extends BaseMetadataServiceTest {
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	@Test
	public void quickTest() {
		repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(getRepoName());
		TLongHashSet predecessors = new TLongHashSet();
		repo.populatePredecessors(predecessors);
		LOG.debug(getRepoName()+".predecessors: "+predecessors);
		LOG.debug(new Util().getString(predecessors));
		
        repo.setName(getServiceName());
        predecessors.clear();
		repo.populatePredecessors(predecessors);
		LOG.debug(getServiceName()+".predecessors: "+predecessors);
		LOG.debug(new Util().getString(predecessors));
	}

}
