package xc.mst.repo;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;

public class CompleteListSizeServer extends BaseTest {
	
	private final static Logger LOG = Logger.getLogger(CompleteListSizeServer.class);
	
	@Test
	public void test() {
		try {
			System.out.println("beluga");
			LOG.debug("beluga");
			Repository repo = (Repository)getConfig().getBean("Repository");
			repo.setName("xc_marctoxctransformation");
			
			long count = repo.getRecordCount(null, null, getXCFormat(), null);
			LOG.debug("count: "+count);
			System.out.println("count: "+count);
			
			//2011-05-17 05:38:09
		} catch (Throwable t) {
			LOG.debug("", t);
		}
	}

}
