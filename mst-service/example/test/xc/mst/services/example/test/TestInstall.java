package xc.mst.services.example.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.utils.MSTConfiguration;

public class TestInstall extends BaseTest {
	
	protected static final Logger LOG = Logger.getLogger(TestInstall.class);
	
	@Test
	public void testIntall() {
		try {
			ServicesService ss = (ServicesService)MSTConfiguration.getBean("ServicesService");
			System.out.println("testInstall before");
			ss.addNewService("example");
			System.out.println("testInstall after");
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}
