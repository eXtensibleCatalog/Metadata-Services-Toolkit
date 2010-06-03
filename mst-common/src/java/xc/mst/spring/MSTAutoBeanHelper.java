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
	
	protected boolean isRootContext() {
		ClassLoader cl = null;
		try {
			// This part checks whether we are currently loading a service ac
			Util util = (Util)MSTConfiguration.getBean("Util");
			cl = util.getClassLoader();
			if (cl != null) {
				LOG.debug("cl: "+cl);
				return false;
			}
			// This part checks whether a service ac has been loaded and we're in the context
			if (MSTConfiguration.getBean("Service") != null) {
				LOG.debug("MSTConfiguration.getBean(Service): "+MSTConfiguration.getBean("Service"));
				return false;
			}
		} catch (Throwable t) {
			//do nothing
		}
		return true;
	}
	
	protected boolean blackListed(String className) {
		if (isRootContext() && className.startsWith("xc.mst.services.impl")) {
			LOG.debug("blackListed: "+className);
			return true;
		} else {
			return false;
		}
	}

}
