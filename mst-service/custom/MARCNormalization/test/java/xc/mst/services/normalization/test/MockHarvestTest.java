package xc.mst.services.normalization.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class MockHarvestTest extends xc.mst.service.impl.test.MockHarvestTest {

	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "file://../test/mock_harvest/"+getFolder();
	}
	
	public String getFolder() {
		return "randys-30";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getMarcXmlFormat();
	}

	protected Format getHarvestOutFormat() throws Exception {
		return getMarcXmlFormat();
	}

	@Override
	protected void finalTest() throws Exception {
	}
	
	@Override
	protected void testHarvestOut() {
	}

}
