package xc.mst.service.impl.test;

import xc.mst.services.MetadataService;
import xc.mst.services.impl.GenericMetadataService;

public class MetadataServiceSpecificTest extends BaseMetadataServiceTest {
	
	protected String getServiceName() {
		return System.getenv("service.name");
	}
	
	protected void go() {
		try {
			startup();
			try {
				repositoryDAO.deleteSchema(getServiceName());
			} catch (Throwable t) {
			}
			getServicesService().addNewService(getServiceName());
			System.out.println("getServiceName(): "+getServiceName());
			MetadataService ms = getServicesService().getServiceByName(getServiceName()).getMetadataService();
			((GenericMetadataService)ms).runTests();
			shutdown();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MetadataServiceSpecificTest test = new MetadataServiceSpecificTest();
		test.go();
		System.exit(0);
	}
}
