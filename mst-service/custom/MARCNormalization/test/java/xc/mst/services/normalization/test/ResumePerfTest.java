package xc.mst.services.normalization.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.repo.Repository;
import xc.mst.services.impl.GenericMetadataService;

public class ResumePerfTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);
	
	@Test
	public void resumePerfTest() {

		try {
			Service s = getServicesService().getServiceByName("MARCNormalization");
			GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
			
			Set incomingSet = getSetDAO().getById(9);
			//Set outgoingSet = getSetDAO().getById(10);
			//LOG.debug("outgoingSet: "+outgoingSet);
			//outgoingSet = getSetDAO().getById(3);
			//LOG.debug("outgoingSet: "+outgoingSet);
			Format format = getFormatDAO().getById(3);
			
			repo = (Repository)getBean("Repository");
			repo.setName("135_5m");
			
			ms.process(repo, format, incomingSet, null);
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}
