/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.record;

import java.util.Date;
import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.RecordList;

public class DBRecordService extends DefaultRecordService {
	
	@Override
	public boolean insert(Record record) throws DataException, IndexException
	{
		// Check that the non-ID fields on the record are valid
		validateFields(record, false, true);

		if(log.isDebugEnabled())
			log.debug("Inserting a new " + record.getIndexedObjectType());

		Date now = new Date();
		record.setCreatedAt(now);
		record.setUpdatedAt(now);

		boolean retVal = getRecordDAO().insert(record);
		return retVal;
	}
	
	public void commit(int serviceId, boolean force) {
		getRecordDAO().commit(serviceId, force);
	}
	
	public Record getByOaiIdentifierAndService(String id, int serviceId) {
		return null;
	}
	
	public List<Record> getInputForServiceToProcess(int serviceId) {
		return getInputForServiceToProcess(serviceId, false);
	}
	
	public List<Record> getInputForServiceToProcess(int serviceId, boolean reset) {
		return getRecordDAO().getInputForServiceToProcess(serviceId, reset);
	}

	@Override
	public RecordList getSuccessorsCreatedByServiceIdIncludingDeletedRecords(long recordId, long serviceId) throws IndexException {
		return null;
	}
	
	@Override
	public boolean update(Record record) throws DataException, IndexException {
		// Check that the fields on the record are valid
		// validateFields(record, true, true);

		if(log.isDebugEnabled())
			log.debug("Updating the record with ID " + record.getId());
		
		return getRecordDAO().update(record);
	}
	
	public boolean delete(Record record) throws DataException {
		// Check that the ID field on the record are valid
		validateFields(record, true, false);

		if(log.isDebugEnabled())
			log.debug("Deleting the record with ID " + record.getId());

		//return recordDao.delete();
		return true;
	} // end method delete(Record)
	
	public void reset() {
		getRecordDAO().reset();
	}
}
