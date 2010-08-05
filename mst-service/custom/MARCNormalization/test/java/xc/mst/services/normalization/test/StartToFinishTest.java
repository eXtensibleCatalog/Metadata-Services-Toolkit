package xc.mst.services.normalization.test;

import gnu.trove.TLongObjectHashMap;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import xc.mst.bo.provider.Format;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;

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

	protected Format getHarvestOutFormat() throws Exception {
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

	/**
	 * To test harvest out functionality
	 */
	protected void testHarvestOut() {
		
		int numberOfRecords = 0;

		org.jdom.Document doc = new XmlHelper().getJDomDocument(getHarvestOutResponse());

		List<Element> records = doc.getRootElement().getChildren("record");
		if (records != null) {
			numberOfRecords = records.size();
		}
		
		LOG.debug("Number of records harvested out : " + numberOfRecords);
		
		assert numberOfRecords == 175 : " Number of harvested records should be 175 but instead it is " + numberOfRecords;
	}
}