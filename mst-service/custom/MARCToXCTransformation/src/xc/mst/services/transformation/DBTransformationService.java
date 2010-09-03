/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.transformation;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.service.DefaultOaiIdentifierForServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DBRecordService;
import xc.mst.utils.TimingLogger;

public class DBTransformationService extends TransformationService {
	
	private static final Logger LOG = Logger.getLogger(DBTransformationService.class);
	
	public void init() {
		setOaiIdentifierForServiceDAO(new DBOaiIdentifierForServiceDAO());
	}
	
	@Override
	public boolean processRecords() {
		try {
			List<Record> inputRecords  = ((DBRecordService)getRecordService()).getInputForServiceToProcess(service.getId(), true);
				
			while (inputRecords != null && inputRecords.size() > 0) {
				TimingLogger.log("inputRecords.size(): "+inputRecords.size());
				for (Record r : inputRecords) {
					r.setService(service);
					try {
						processRecord(r);
					} catch (Throwable t) {
						TimingLogger.log("ouch - problems");
						LOG.error("r.getId(): "+r.getId());
						LOG.error("t", t);
					}
					getRecordService().update(r);
				}
				((DBRecordService)getRecordService()).commit(service.getId(), true);
				inputRecords  = getRecordService().getInputForServiceToProcess(service.getId());
			}
			((DBRecordService)getRecordService()).commit(service.getId(), true);
			((DBRecordService)getRecordService()).reset();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	@Override
	protected void insertNewRecord(Record record) throws DataException, IndexException {
		record.setService(service);
		getRecordService().insert(record);
	}
	
	public class DBOaiIdentifierForServiceDAO extends DefaultOaiIdentifierForServiceDAO {
		protected int id=0;
		
		@Override
		public long getNextOaiIdForService(int serviceId) {
			return id++;
		}
		
	}
}
