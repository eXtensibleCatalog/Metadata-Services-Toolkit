package xc.mst.services.normalization.test;

import gnu.trove.TLongObjectHashMap;

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
		TLongObjectHashMap predecessorKeyedMap = new TLongObjectHashMap();
		TLongObjectHashMap successorKeyedMap = new TLongObjectHashMap();
		repo.populatePredSuccMaps(predecessorKeyedMap, successorKeyedMap);
		LOG.debug(getRepoName()+".predecessorKeyedMap: "+predecessorKeyedMap);
		LOG.debug(new Util().getString(predecessorKeyedMap));
		LOG.debug(getRepoName()+".successorKeyedMap: "+successorKeyedMap);
		LOG.debug(new Util().getString(successorKeyedMap));
		
        repo.setName(getServiceName());
		predecessorKeyedMap.clear();
		successorKeyedMap.clear();
		repo.populatePredSuccMaps(predecessorKeyedMap, successorKeyedMap);
		LOG.debug(getServiceName()+".predecessorKeyedMap: "+predecessorKeyedMap);
		LOG.debug(new Util().getString(predecessorKeyedMap));
		LOG.debug(getServiceName()+".successorKeyedMap: "+successorKeyedMap);
		LOG.debug(new Util().getString(successorKeyedMap));
	}

}
