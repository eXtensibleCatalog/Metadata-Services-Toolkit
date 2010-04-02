package xc.mst.services.normalization;

import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.service.DefaultOaiIdentiferForServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DBRecordService;

public class DBNormalizationService extends NormalizationService {

	public DBNormalizationService() {
		this.recordService = new DBRecordService();
		this.oaiIdDao = new DBOaiIdentiferForServiceDAO();
	}
	
	@Override
	public boolean processRecords() {
		try {
			List<Record> inputRecords  = ((DBRecordService)recordService).getInputForServiceToProcess(service.getId(), true);
			
			while (inputRecords != null && inputRecords.size() > 0) {
				for (Record r : inputRecords) {
					r.setService(service);
					recordService.update(r);
					processRecord(r);
				}
				((DBRecordService)recordService).commit(false);
				inputRecords  = recordService.getInputForServiceToProcess(service.getId());
			}
			((DBRecordService)recordService).commit(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	@Override
	protected void insertNewRecord(Record record) throws DataException, IndexException {
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
