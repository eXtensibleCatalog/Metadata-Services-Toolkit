package xc.mst.services.transformation.test;

import gnu.trove.TLongArrayList;
import gnu.trove.TLongLongHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.repo.RepositoryDAO;
import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.services.transformation.TransformationService;
import xc.mst.services.transformation.dao.TransformationDAO;

public class TransformationDaoTest extends BaseMetadataServiceTest {
	
	private final static Logger LOG = Logger.getLogger(TransformationDaoTest.class);

	@Test
	public void testBibMaps() {
		LOG.debug("getClass().getClassLoader(): "+getClass().getClassLoader());
		ClassLoader parent = getClass().getClassLoader().getParent();
		ClassLoader previousParent = null;
		while (parent != null && (previousParent == null || previousParent != parent )) {
			LOG.debug("parent: "+parent);
			previousParent = parent;
			parent = getClass().getClassLoader().getParent();	
		}

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
		
		getTransformationDAO().persistBibMaps(bibsProcessedLongId, bibsProcessedStringId,
				null, null,
				bibsYet2ArriveLongId, bibsYet2ArriveStringId,
				null, null);
		
		bibsProcessedLongId.clear();
		bibsProcessedStringId.clear();
		bibsYet2ArriveLongId.clear();
		bibsYet2ArriveStringId.clear();
		
		getTransformationDAO().loadBibMaps(bibsProcessedLongId, bibsProcessedStringId, 
				bibsYet2ArriveLongId, bibsYet2ArriveStringId);
		
		assert bibsProcessedLongId.size() == 2;
		assert bibsProcessedStringId.size() == 2;
		assert bibsYet2ArriveLongId.size() == 2;
		assert bibsYet2ArriveStringId.size() == 2;
	}
	
	@Test
	public void testLinks() {
		List<long[]> links = new ArrayList<long[]>();
		links.add(new long[]{1L, 2L});
		links.add(new long[]{3L, 2L});
		links.add(new long[]{4L, 5L});
		
		getRepositoryDAO().persistLinkedRecordIds(getServiceName(), links);
		
		Set<Long> expectedRecordIds = new HashSet<Long>();
		expectedRecordIds.add(1L);
		expectedRecordIds.add(3L);
		List<Long> recordIds = getRepositoryDAO().getLinkedRecordIds(getServiceName(), 2L);
		for (Long id : recordIds) {
			assert expectedRecordIds.contains(id);
			expectedRecordIds.remove(id);
		}
		assert expectedRecordIds.size() == 0;
		expectedRecordIds = new HashSet<Long>();
		expectedRecordIds.add(4L);
		recordIds = getRepositoryDAO().getLinkedRecordIds(getServiceName(), 5L);
		for (Long id : recordIds) {
			assert expectedRecordIds.contains(id);
			expectedRecordIds.remove(id);
		}
		assert expectedRecordIds.size() == 0;
	}
	
	@Test
	public void testActivateHeldHoldings() {
		List<Long> holdingIds = new ArrayList<Long>();
		int numRecords=10;
		long manId = 555L;
		getRepository().beginBatch();
		for (int i=0 ; i<numRecords; i++) {
			Record r = new Record();
			r.setId(getRepositoryDAO().getNextId());
			r.setStatus(Record.HELD);
			getRepository().addRecord(r);
			holdingIds.add(r.getId());
		}
		getRepository().endBatch();
		List<long[]> links = new ArrayList<long[]>();
		
		getRepositoryDAO().persistLinkedRecordIds(getServiceName(), links);
		for (int i=0 ; i<numRecords/2; i++) {
			links.add(new long[]{holdingIds.get(i), manId});
		}
		getRepositoryDAO().persistLinkedRecordIds(getServiceName(), links);
		
		TLongArrayList manifestionIdsPreviouslyHeld = new TLongArrayList();
		manifestionIdsPreviouslyHeld.add(manId);
		getRepositoryDAO().activateLinkedRecords(getServiceName(), manifestionIdsPreviouslyHeld);
		
		List<Map<String, Object>> records = getJdbcTemplate().queryForList(
				" select record_id, status "+
				" from "+RepositoryDAO.RECORDS_TABLE+
				" order by record_id");
		
		assert numRecords == records.size();
		int i=0;
		for (Map<String, Object> row : records) {
			if (i < numRecords/2) {
				assert (row.get("status")+"").equals(Record.ACTIVE+"");
			} else {
				assert (row.get("status")+"").equals(Record.HELD+"");
			}
			i++;
		}
	}
	
	protected TransformationDAO getTransformationDAO() {
		return ((TransformationService)getMetadataService()).getTransformationDAO();	
	}
}
