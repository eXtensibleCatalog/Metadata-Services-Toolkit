package xc.mst.services.normalization.test;

import org.apache.log4j.Logger;

import gnu.trove.TLongObjectHashMap;
import xc.mst.bo.provider.Format;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "http://128.151.244.137:8080/OAIToolkit_0.6.1/oai-request.do";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getMarcXmlFormat();
	}
	
	protected void finalTest() {
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