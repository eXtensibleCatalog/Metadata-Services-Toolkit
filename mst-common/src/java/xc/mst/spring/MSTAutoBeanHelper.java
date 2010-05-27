package xc.mst.spring;

import org.apache.log4j.Logger;

import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class MSTAutoBeanHelper {
	
	private static final Logger LOG = Logger.getLogger(MSTAutoBeanHelper.class);
	
	protected ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			Util util = (Util)MSTConfiguration.getBean("Util");
			cl = util.getClassLoader();
		} catch (Throwable t) {
			//LOG.error("", t);
		}
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
		return cl;
	}

}
