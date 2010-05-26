package xc.mst.services.example;

import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.services.impl.GenericMetadataService;

public class ExampleMetadataService extends GenericMetadataService {
	
	public List<Record> process(Record r) {
		List<Record> records = new ArrayList<Record>();
		Record out = getRecordService().createSuccessor(r, getService());
		out.setOaiXml("<foo>"+out.getId()+"</foo>");
		records.add(out);
		return records;
	}

}
