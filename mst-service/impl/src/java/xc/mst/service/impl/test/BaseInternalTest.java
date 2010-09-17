package xc.mst.service.impl.test;

import xc.mst.services.MetadataService;
import xc.mst.services.impl.spring.TestTypeFilter;


public class BaseInternalTest extends BaseMetadataServiceTest {
	
	protected String getServiceName() {
		return System.getenv("service.name");
	}
	
	protected MetadataService getMetadataService() {
		return TestTypeFilter.metadataService;
	}
	
	@Override
	public void startup() {
	}
	
	@Override
	public void shutdown() {
	}

}
