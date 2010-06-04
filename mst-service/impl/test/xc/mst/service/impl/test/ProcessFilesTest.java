package xc.mst.service.impl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.repo.Repository;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.utils.MSTConfiguration;


public class ProcessFilesTest extends BaseMetadataServiceTest {
	
	private static final Logger LOG = Logger.getLogger(ProcessFilesTest.class);
	
	@Test
	public void testFiles() {
		try {
			//String serviceClassName = System.getenv("TEST_SERVICE_CLASS_NAME");
			String serviceName = MSTConfiguration.getInstance().getProperty("service.name");
			String inFolderStr = System.getenv("MST_SERVICE_TEST_FOLDER");
			LOG.debug("folderStr: "+inFolderStr);
			
			ServicesService ss = (ServicesService)MSTConfiguration.getInstance().getBean("ServicesService");
	
			ss.addNewService(serviceName);
			Service s = ss.getServiceByName(serviceName);
			GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
			
			File inputRecordsDir = new File(INPUT_RECORDS_DIR);
			List<String> folderStrs = new ArrayList<String>();
			
			if (!StringUtils.isEmpty(inFolderStr)) {
				folderStrs.add(inFolderStr);
			} else {
				for (String folderStr2 : inputRecordsDir.list()) {
					LOG.debug("folderStr2: "+folderStr2);
					if (!folderStr2.contains(".svn")) {
						folderStr2 = new File(folderStr2).getName();
						folderStrs.add(folderStr2);
					}
				}
			}

			for (String folderStr : folderStrs) {
				long id = repositoryDAO.restIdSequence(1);
				Repository repo = new TestRepository(folderStr);
				ms.setRepository(repo);
				LOG.debug("folderStr2: "+folderStr);
				ms.process(repo, null, null);
				repositoryDAO.restIdSequence(id);
				//TODO compare
			}
			
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	// TODO: diff the expected and actual output

}
