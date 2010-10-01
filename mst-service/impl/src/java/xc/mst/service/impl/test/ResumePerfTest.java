package xc.mst.service.impl.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.test.BaseMetadataServiceTest;

public class ResumePerfTest extends BaseMetadataServiceTest {
	
	private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);
	
	protected String getPriorServiceName() {
		return null;
	}
	
	@Test
	public void resumePerfTest() {
		try {
			getJdbcTemplate().update("delete from service_harvests");
			//((DefaultRepository)getRepository()).deleteAllData();
			Service priorService = getServicesService().getServiceByName(getPriorServiceName());
			getMetadataService().process(priorService.getMetadataService().getRepository(), null, null, null);
		} catch (Throwable t) {
			LOG.error("", t);
		}
		
	}

}
