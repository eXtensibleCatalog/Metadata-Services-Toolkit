package xc.mst.services.normalization.test;

import gnu.trove.TLongHashSet;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

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

	/**
	 * To test harvest out functionality
	 */
	protected void testHarvestOut() {
		try {
			int numberOfRecords = 0;
	
			org.jdom.Document doc = new XmlHelper().getJDomDocument(getHarvestOutResponse());
	
	
			Namespace ns = doc.getRootElement().getNamespace();
			List<Element> records = doc.getRootElement().getChild("ListRecords", ns).getChildren("record", ns);
			if (records != null) {
				numberOfRecords = records.size();
			}
			
			LOG.debug("Number of records harvested out : " + numberOfRecords);
			
			assert numberOfRecords == 175 : " Number of harvested records should be 175 but instead it is " + numberOfRecords;
		} catch (Throwable t) {
			util.throwIt(t);
		}
	}
}