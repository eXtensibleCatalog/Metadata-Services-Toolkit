package xc.mst.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;

public class RecordCountTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(RecordCountTest.class);
	
	@Test
	public void getRecordCounts() {
		LOG.error("getRecordCounts()");
		
		List<String[]> incomingHarvestRecords = null;
		List<String[]> incomingServiceRecords = null;
		List<String[]> outgoingServiceRecords = null;
		
		for (Repository r : getRepositoryService().getAll()) {
			getRepositoryDAO().updateOutgoingRecordCounts(r.getName());
		}

		incomingHarvestRecords = getRepositoryService().getIncomingHarvestRecords();
		incomingServiceRecords = getRepositoryService().getIncomingServiceRecords();
		outgoingServiceRecords = getRepositoryService().getOutgoingServiceRecords();

		List<List<String[]>> allTables = new ArrayList<List<String[]>>();
		allTables.add(incomingHarvestRecords);
		allTables.add(incomingServiceRecords);
		allTables.add(outgoingServiceRecords);
		
		int colWidth = 30;
		
		StringBuilder sb = new StringBuilder();
		for (List<String[]> table : allTables) {
			LOG.debug("new table: ");
			if (table != null) {
				LOG.debug("table != null");
				for (String[] row : table) {
					sb.delete(0, sb.length());
					for (String col : row) {
						if (col != null) {
							for (int i=0; i<(colWidth-col.length()); i++) {
								sb.append(" ");
							}
							sb.append(col);
						} else {
							for (int i=0; i<colWidth; i++) {
								sb.append(" ");
							}
						}
					}
					LOG.debug(sb.toString());
				}
			}
		}
	}
}
