package xc.mst.services.transformation.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	@Override
	protected long getNumberOfRecordsToHarvest() {
		//return Integer.MAX_VALUE;
		//return 100000;
		return 400;
	}
	
	public String getRepoName() {
		return "rochester_137";
	}
	
	protected String getSetSpec() {
		return "rochester_137:bib";
	}
	
	
	@Override
	protected String[] getPriorServices() {
		return new String[] {"marcnormalization"};
	}
	
	public String getProviderUrl() {
		return "http://128.151.244.137:8080/OAIToolkit/oai-request.do";
		//return "http://128.151.244.135:8080/OAIToolkit/oai-request.do";
	}
	
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarc21Format()};
	}

	public Format getHarvestOutFormat() throws Exception {
		return getXCFormat();
	}
	
	public void finalTest() {
		/*
		try {
			getJdbcTemplate().execute("delete from MetadataServicesToolkit.harvests");
			getJdbcTemplate().execute("delete from MetadataServicesToolkit.harvest_schedules");
			getJdbcTemplate().execute("delete from MetadataServicesToolkit.service_harvests");
			createHarvestSchedule();
			waitUntilFinished();
		} catch (Throwable t) {
			LOG.error("", t);
			throw new RuntimeException(t);
		}

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
	
	/*
	@Test
	public void startToFinish() throws Exception  {
		super.startToFinish();
		createHarvestSchedule();
		waitUntilFinished();
	}
	*/
	
	/*
	@Override
	public void createHarvestSchedule(String name, Set s) throws Exception {
		//do nothing
	}
	*/
}