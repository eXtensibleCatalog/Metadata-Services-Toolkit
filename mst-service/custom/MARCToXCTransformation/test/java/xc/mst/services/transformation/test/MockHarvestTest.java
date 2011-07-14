package xc.mst.services.transformation.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class MockHarvestTest extends xc.mst.service.impl.test.MockHarvestTest {

	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);

	@Override
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarc21Format()};
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
