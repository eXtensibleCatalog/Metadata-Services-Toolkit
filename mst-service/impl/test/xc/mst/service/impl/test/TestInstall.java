/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
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
			ss.addNewService("MARCToXCTransformation");
			System.out.println("testInstall after");
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}

}
