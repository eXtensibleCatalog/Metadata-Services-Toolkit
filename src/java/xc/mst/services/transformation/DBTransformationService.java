package xc.mst.services.transformation;

import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.service.DefaultOaiIdentiferForServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DBRecordService;
import xc.mst.utils.TimingLogger;

public class DBTransformationService extends TransformationService {
	
	public void init() {
		setOaiIdentifierForServiceDAO(new DBOaiIdentiferForServiceDAO());
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
						log.error("r.getId(): "+r.getId());
						log.error("t", t);
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
	
	public class DBOaiIdentiferForServiceDAO extends DefaultOaiIdentiferForServiceDAO {
		protected int id=0;
		
		@Override
		public long getNextOaiIdForService(int serviceId) {
			return id++;
		}
		
	}
}
