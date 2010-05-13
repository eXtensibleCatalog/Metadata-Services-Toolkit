package xc.mst.repo;

import java.util.Date;
import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.manager.BaseManager;
import xc.mst.utils.MSTConfiguration;

public class GenericRepository extends BaseManager implements Repository {
	
	protected String name = null;

	public void installOrUpdateIfNecessary() {
		if ("0.3.0".equals(MSTConfiguration.getProperty("version"))) {
			boolean exists = getRepositoryDAO().exists(name);
			System.out.println("exists: "+exists);
			if (!exists) {
				getRepositoryDAO().createTables(name);
			}
		}
	}
	
	protected boolean exists() {
		return false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addRecords(List<Record> records) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Record> getPredecessors(Record r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record getRecord(String oaiId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record getRecord(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Record> getRecords(Date from, Date until, Integer startingId) {
		// TODO Auto-generated method stub
		return null;
	}

}
