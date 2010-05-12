package xc.mst.repo;

import java.util.Date;
import java.util.List;

import xc.mst.bo.record.Record;

public interface Repository {
	
	public String getName();
	
	public void installOrUpdateIfNecessary(); 

	public void addRecords(List<Record> records);
	
	public List<Record> getRecords(Date from, Date until, Integer startingId);
	
	public Record getRecord(String oaiId);
	
	public Record getRecord(Integer id);
	
	public List<Record> getPredecessors(Record r);

}
