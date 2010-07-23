package xc.mst.service.impl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.repo.Repository;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.XmlHelper;


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
			
			Map<String, String> testFailures = new HashMap<String, String>();
	
			Service s = ss.getServiceByName(serviceName);
			getRepositoryDAO().deleteSchema(serviceName);
			ss.addNewService(serviceName);
			s = ss.getServiceByName(serviceName);
			GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
			
			File inputRecordsDir = new File(TestRepository.INPUT_RECORDS_DIR);
			LOG.debug("inputRecordsDir: "+inputRecordsDir);
			LOG.debug("new File(\".\").getAbsolutePath(): "+new File(".").getAbsolutePath());
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
				long id = repositoryDAO.resetIdSequence(1);
				Repository repo = (TestRepository)MSTConfiguration.getInstance().getBean("TestRepository");
				repo.setName(folderStr);
				ms.setRepository(repo);
				LOG.debug("folderStr2: "+folderStr);
				ms.process(repo, null, null, null);
				repositoryDAO.resetIdSequence(id);
				
				File expectedOutputFolder = new File(TestRepository.EXPECTED_OUTPUT_RECORDS+"/"+folderStr);
				Set<String> expectedOutputFiles = new HashSet<String>();
				for (String ef : expectedOutputFolder.list()) {
					if (!ef.contains(".svn")) {
						expectedOutputFiles.add(ef);
					}
				}

				File actualOutputFolder  = new File(TestRepository.ACTUAL_OUTPUT_RECORDS+"/"+folderStr);
				for (String af : actualOutputFolder.list()) {
					LOG.debug("af: "+af);
					if (expectedOutputFiles.contains(af)) {
						expectedOutputFiles.remove(af);
						if (new XmlHelper().diffXmlFiles(
								TestRepository.ACTUAL_OUTPUT_RECORDS+"/"+folderStr+"/"+af, 
								TestRepository.EXPECTED_OUTPUT_RECORDS+"/"+folderStr+"/"+af)) {
							testFailures.put(folderStr+"/"+af, "files differ");
						}
					} else {
						testFailures.put(folderStr+"/"+af, "file exists in actual, but not expected.");
					}
				}
				for (String ef : expectedOutputFiles) {
					testFailures.put(folderStr+"/"+ef, "file expected, but wasn't produced.");
				}
				
				StringBuilder sb = new StringBuilder();
				for (String key : testFailures.keySet()) {
					String value = testFailures.get(key);
					String s2 = "\n"+key+": "+value;

					sb.append(s2);
				}
				
				if (sb.length() > 0) {
					LOG.error(sb.toString());
					throw new RuntimeException(sb.toString());
				}
			}
			
		} catch (Throwable t) {
			util.throwIt(t);
		}
	}

}
