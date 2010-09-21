package xc.mst.services.transformation.test;

import gnu.trove.TLongLongHashMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.services.transformation.TransformationService;
import xc.mst.services.transformation.dao.TransformationDAO;
import xc.mst.test.BaseMetadataServiceTest;

public class TransformationDaoTest extends BaseMetadataServiceTest {
	
	private final static Logger LOG = Logger.getLogger(TransformationDaoTest.class);

	@Test
	public void testPersistBibMaps() {
		LOG.debug("getClass().getClassLoader(): "+getClass().getClassLoader());
		ClassLoader parent = getClass().getClassLoader().getParent();
		ClassLoader previousParent = null;
		while (parent != null && (previousParent == null || previousParent != parent )) {
			LOG.debug("parent: "+parent);
			previousParent = parent;
			parent = getClass().getClassLoader().getParent();	
		}
		
		TransformationDAO transformationDAO = ((TransformationService)getMetadataService()).getTransformationDAO();
		TLongLongHashMap bibsProcessedLongId = new TLongLongHashMap();
		Map<String, Long> bibsProcessedStringId = new HashMap<String, Long>();
		TLongLongHashMap bibsYet2ArriveLongId = new TLongLongHashMap();
		Map<String, Long> bibsYet2ArriveStringId = new HashMap<String, Long>();
		
		bibsProcessedLongId.put(1, 2);
		bibsProcessedLongId.put(3, 4);
		
		bibsProcessedStringId.put("5", 6L);
		bibsProcessedStringId.put("7", 8L);
		
		bibsYet2ArriveLongId.put(9, 10);
		bibsYet2ArriveLongId.put(11, 12);
		
		bibsYet2ArriveStringId.put("13", 14L);
		bibsYet2ArriveStringId.put("15", 16L);
		
		transformationDAO.persistBibMaps(bibsProcessedLongId, bibsProcessedStringId, bibsYet2ArriveLongId, bibsYet2ArriveStringId);
		
		
	}
	
}
