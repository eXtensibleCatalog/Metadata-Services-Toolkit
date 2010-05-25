package xc.mst.service.impl.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.services.impl.GenericMetadataService;

public class UpdateVersionTest extends BaseTest  {
	
	private static final Logger LOG = Logger.getLogger(UpdateVersionTest.class);
	
	@Test
	public void test() {
		try {	
			Service norm = servicesService.getServiceByName("MARCNormalization");
			List<String> fileNames = new ArrayList<String>();
			fileNames.add("update.1.0.sql");
			fileNames.add("update.0.2.sql");
			fileNames.add("update.0.2.1.sql");
			fileNames.add("update.0.2.2.sql");
			fileNames.add("update.0.2.1.1.1.1.1.sql");
			fileNames.add("update.0.3.sql");
			fileNames.add("update.0.3.0.sql");
			fileNames.add("update.0.3.1.sql");
			((GenericMetadataService)norm.getMetadataService()).update("0.2.1", "0.3", fileNames);
		} catch (Throwable t) {
			LOG.error("", t);
			throw new RuntimeException(t);
		}
	}

}
