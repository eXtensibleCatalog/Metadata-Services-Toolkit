package xc.mst.services.example.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	@Override
	protected Format[] getIncomingFormats() throws Exception {
		return new Format[] {getDCFormat()};
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "http://geolib.geo.auth.gr/digeo/index.php/index/oai";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getDCFormat();
	}
	
	protected Format getHarvestOutFormat() throws Exception {
		return getXCFormat();
	}

	protected void testHarvestOut() {

	}

	@Override
	protected void finalTest() throws Exception {
	}
}
