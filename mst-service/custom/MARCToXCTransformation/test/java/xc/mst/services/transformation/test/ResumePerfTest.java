package xc.mst.services.transformation.test;

import org.testng.annotations.Test;

public class ResumePerfTest extends xc.mst.service.impl.test.ResumePerfTest {

	@Override
	protected String getInputRepoName() {
		return "marcnormalization";
	}
	

	@Override
	@Test
	public void resumePerfTest() {
		getJdbcTemplate().update("delete from MetadataServicesToolkit.service_harvests");
		super.resumePerfTest();
	}
	
}
