package xc.mst.services.transformation;

import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.service.DefaultOaiIdentiferForServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DBRecordService;
import xc.mst.utils.TimingLogger;

public class DBTransformationService extends TransformationService {
	
	public DBTransformationService() {
		this.recordService = new DBRecordService();
		this.oaiIdDao = new DBOaiIdentiferForServiceDAO();
	}
	
	@Override
	public boolean processRecords() {
		try {
			List<Record> inputRecords  = ((DBRecordService)recordService).getInputForServiceToProcess(service.getId(), true);
				
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
					recordService.update(r);
				}
				((DBRecordService)recordService).commit(service.getId(), true);
				inputRecords  = recordService.getInputForServiceToProcess(service.getId());
			}
			((DBRecordService)recordService).commit(service.getId(), true);
			((DBRecordService)recordService).reset();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	@Override
	protected void insertNewRecord(Record record) throws DataException, IndexException {
		record.setService(service);
		recordService.insert(record);
	}
	
	public class DBOaiIdentiferForServiceDAO extends DefaultOaiIdentiferForServiceDAO {
		protected int id=0;
		
		@Override
		public long getNextOaiIdForService(int serviceId) {
			return id++;
		}
		
	}
}
