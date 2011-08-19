package xc.mst.services.test;

import xc.mst.common.test.BaseTest;
import xc.mst.services.MetadataService;
import xc.mst.services.MetadataServiceExtras;

public class MetadataServiceSpecificTest extends BaseTest {

    protected String getServiceName() {
        return System.getenv("service.name");
    }

    protected void go() {
        try {
            startup();
            boolean skipMstInstall = "TRUE".equals(System.getenv("skip.mst.install").toUpperCase());
            if (skipMstInstall) {
                skipMstInstall = !"TRUE".equals(System.getenv("still.install.service").toUpperCase());
            }
            if (!skipMstInstall) {
                try {
                    getRepositoryDAO().deleteSchema(getServiceName());
                } catch (Throwable t) {
                }
                getServicesService().addNewService(getServiceName());
            }
            System.out.println("getServiceName(): "+getServiceName());
            MetadataService ms = getServicesService().getServiceByName(getServiceName()).getMetadataService();
            if (ms instanceof MetadataServiceExtras) {
                ((MetadataServiceExtras)ms).runTests();
            }
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
