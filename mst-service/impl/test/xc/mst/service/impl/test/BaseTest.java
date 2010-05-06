package xc.mst.service.impl.test;

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
