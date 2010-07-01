package xc.mst.services.normalization.test;

import xc.mst.bo.provider.Format;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "http://128.151.244.137:8080/OAIToolkit_0.6.1/oai-request.do";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getMarcXmlFormat();
	}
	
	protected void finalTest() {
		
	}
}