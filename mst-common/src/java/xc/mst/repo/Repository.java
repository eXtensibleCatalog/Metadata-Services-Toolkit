/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import java.util.Date;
import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;

public interface Repository {
	
	public String getName();
	public void setName(String name);
	
	public int getSize();
	
	public void beginBatch();
	
	public void endBatch();
	
	public void installOrUpdateIfNecessary(String previousVersion, String currentVersion); 

	public void addRecord(Record record);
	public void addRecords(List<Record> records);
	
	public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet);
	
	public Record getRecord(String oaiId);
	
	public Record getRecord(long id);
	
	public List<Long> getPredecessorIds(Record r);
	
	public void injectSuccessors(Record r);
	
	public Provider getProvider();
	public Service getService();
	public void setProvider(Provider p);
	public void setService(Service s);

}
