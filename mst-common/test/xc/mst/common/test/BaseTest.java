/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import xc.mst.utils.SetupClasspath;

public class BaseTest {
	
	protected ApplicationContext applicationContext = null;

	@BeforeSuite
	public void startup() {
		System.out.println("startup");
		try {
			SetupClasspath.setupClasspath(null);
			applicationContext = new ClassPathXmlApplicationContext("spring-mst.xml");
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
		System.out.println("startup complete");
	}
	
	@AfterSuite
	public void shutdown() {
		System.out.println("shutdown");
	}
	
}
