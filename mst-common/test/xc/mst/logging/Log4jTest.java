package xc.mst.logging;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;

public class Log4jTest extends BaseTest {
	
	private static final Logger X_ROOT = Logger.getLogger("xc.mst.logging");
	private static final Logger DEBUG = Logger.getLogger("xc.mst.logging.DEBUG");
	private static final Logger ERROR = Logger.getLogger("xc.mst.logging.ERROR");
	private static final Logger DefaultLogDAO = Logger.getLogger("xc.mst.dao.log.DefaultLogDAO");

	@Test
	public void test() {
		X_ROOT.debug("X_ROOT debug");
		X_ROOT.error("X_ROOT error");
		DEBUG.debug("DEBUG debug");
		DEBUG.error("DEBUG error");
		ERROR.debug("ERROR debug");
		ERROR.error("ERROR error");	
		DefaultLogDAO.debug("DefaultLogDAO debug");
		DefaultLogDAO.error("DefaultLogDAO error");
	}

}
