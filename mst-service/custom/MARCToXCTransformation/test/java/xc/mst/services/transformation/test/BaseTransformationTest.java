package xc.mst.services.transformation.test;

import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.services.transformation.TransformationService;
import xc.mst.services.transformation.dao.TransformationDAO;
import xc.mst.utils.MSTConfiguration;

public class BaseTransformationTest extends BaseMetadataServiceTest {
	
	protected String getServiceName() {
		return "MARCToXCTransformation";
	}
	
	/*
	protected TransformationService getTransformationService() {
		return (TransformationService)MSTConfiguration.getInstance().getBean("MetadataService");
	}
	
	protected TransformationDAO getTransformationDAO() {
		return (TransformationDAO)getTransformationService().getTransformationDAO();
	}
	*/

}
