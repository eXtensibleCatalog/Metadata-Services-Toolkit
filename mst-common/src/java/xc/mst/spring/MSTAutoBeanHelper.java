package xc.mst.spring;

import org.apache.log4j.Logger;

import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class MSTAutoBeanHelper {
	
	private static final Logger LOG = Logger.getLogger(MSTAutoBeanHelper.class);
	
	protected ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			Util util = (Util)MSTConfiguration.getInstance().getBean("Util");
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
			Util util = (Util)MSTConfiguration.getInstance().getBean("Util");
			cl = util.getClassLoader();
			if (cl != null) {
				return false;
			}
			// This part checks whether a service ac has been loaded and we're in the context
			if (MSTConfiguration.getInstance().getBean("Service") != null) {
				LOG.debug("MSTConfiguration.getBean(Service): "+MSTConfiguration.getInstance().getBean("Service"));
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
	
	protected String getBeanName(String className) {
		if (ServiceTypeFilter.serviceBeans.containsKey(className)) {
			return ServiceTypeFilter.serviceBeans.get(className);
		}
		int idx0 = className.lastIndexOf(".");
		if (idx0 != -1) {
			className = className.substring(idx0+1);
		}
		idx0 = className.lastIndexOf("Default");
		if (idx0 != -1) {
			className = className.substring(idx0+"Default".length());
		}
		return className;
	}

}
