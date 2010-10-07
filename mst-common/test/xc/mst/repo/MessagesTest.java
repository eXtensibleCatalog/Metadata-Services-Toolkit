package xc.mst.repo;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.common.test.BaseTest;

public class MessagesTest extends BaseTest {
	
	@Test
	public void test() {
		List<Record> records = new ArrayList<Record>();
		for (int i=0; i<20; i++) {
			Record r = getRepositoryDAO().getRecord("marctoxctransformation", i);
			if (r != null) {
				records.add(r);
			}
			r = getRepositoryDAO().getRecord("marcnormalization", i);
			if (r != null) {
				records.add(r);
			}
		}
		
		LOG.debug("records.size(): "+records.size());
		
		getMessageDAO().injectIntoRecords(records);
		
		for (Record r : records) {
			for (RecordMessage rm : r.getMessages()) {
				LOG.debug(rm.toString());
			}
		}
		
	}

}
