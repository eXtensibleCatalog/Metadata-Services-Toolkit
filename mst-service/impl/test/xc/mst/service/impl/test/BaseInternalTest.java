package xc.mst.service.impl.test;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseInternalTest extends BaseMetadataServiceTest implements ApplicationContextAware {
	
	protected ApplicationContext ac = null;
	
	protected String getServiceName() {
		return System.getenv("service.name");
	}
	
	@Override
	public void startup() {
	}
	
	@Override
	public void shutdown() {
	}

	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		this.ac = ac;		
	}
}
