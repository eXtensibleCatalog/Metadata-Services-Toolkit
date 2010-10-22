package xc.mst.services.transformation.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	public String getRepoName() {
		return "test_repo";
	}
	
	@Override
	protected String[] getPriorServices() {
		return new String[] {"marcnormalization"};
	}
	
	public String getProviderUrl() {
		return "http://128.151.244.137:8080/OAIToolkit/oai-request.do";
	}
	
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarcXmlFormat()};
	}

	public Format getHarvestOutFormat() throws Exception {
		return getXCFormat();
	}
	
	public void finalTest() {
		/*
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
		*/
	}

	/**
	 * To test harvest out functionality
	 */
	public void testHarvestOut() {
		/*
		try {
			int numberOfRecords = 0;
	
			org.jdom.Document doc = new XmlHelper().getJDomDocument(getHarvestOutResponse());
	

			Namespace ns = doc.getRootElement().getNamespace();
			List<Element> records = doc.getRootElement().getChild("ListRecords", ns).getChildren("record", ns);
			if (records != null) {
				numberOfRecords = records.size();
			}
			
			LOG.debug("Number of records harvested out : " + numberOfRecords);
			
			assert numberOfRecords == 28 : " Number of harvested records should be 175 but instead it is " + numberOfRecords;
			LOG.debug("Number of records harvested out : " + numberOfRecords);
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
	*/
	}
}