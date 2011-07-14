package xc.mst.services.transformation.test;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;

public class MultipleSetsTest extends StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(MultipleSetsTest.class);
	
	@Override
	protected long getNumberOfRecordsToHarvest() {
		return Integer.MAX_VALUE;
	}
	
	public String getRepoName() {
		return "multiple_sets";
	}
	
	@Override
	protected String[] getPriorServices() {
		return new String[] {"marcnormalization"};
	}
	
	public String getProviderUrl() {
		return "file://"+MockHarvestTest.INPUT_FOLDER+"/multipleSets";
	}
	
	public Format[] getIncomingFormats() throws Exception {
		return new Format[] {getMarc21Format()};
	}

	public Format getHarvestOutFormat() throws Exception {
		return getXCFormat();
	}

}