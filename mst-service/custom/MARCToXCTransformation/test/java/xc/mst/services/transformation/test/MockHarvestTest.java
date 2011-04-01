package xc.mst.services.transformation.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class MockHarvestTest extends xc.mst.service.impl.test.MockHarvestTest {

	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);
	
	public String getRepoName() {
		return "marcnorm";
	}
	
	public String getProviderUrl() {
		return "file://"+INPUT_FOLDER+"/"+getFolder();
	}
	
	public String getFolder() {
		return "randys-30";
	}
	
	@Override
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarcXmlFormat()};
	}

	public Format getHarvestOutFormat() throws Exception {
		return getXCFormat();
	}

	@Override
	public void finalTest() throws Exception {
	}
	
	@Override
	public void testHarvestOut() {
	}

}
