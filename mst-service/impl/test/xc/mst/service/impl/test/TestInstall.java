package xc.mst.service.impl.test;

import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.utils.MSTConfiguration;

public class TestInstall extends BaseTest {
	
	@Test
	public void testIntall() {
		try {
			ServicesService ss = (ServicesService)MSTConfiguration.getBean("ServicesService");
			System.out.println("testInstall before");
			ss.addNewService("MARCNormalization");
			System.out.println("testInstall after");
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
	

}
