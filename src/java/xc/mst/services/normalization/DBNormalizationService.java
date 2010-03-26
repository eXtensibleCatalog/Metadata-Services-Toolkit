package xc.mst.services.normalization;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.service.DefaultOaiIdentiferForServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DBRecordService;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.Records;

public class DBNormalizationService extends NormalizationService {

	public DBNormalizationService() {
		this.recordService = new DBRecordService();
		this.oaiIdDao = new DBOaiIdentiferForServiceDAO();
	}
	
	public RecordList getSuccessorsCreatedByServiceIdIncludingDeletedRecords(int recordId, int serviceId) {
		return null;
	}
	
	public boolean processRecords() {
		try {
			Records inputRecords  = ((DBRecordService)recordService).getInputForServiceToProcess(service.getId(), true);
			
			while (inputRecords != null && inputRecords.size() > 0) {
				for (Record r : inputRecords) {
					processRecord(r);
				}
				inputRecords  = recordService.getInputForServiceToProcess(service.getId());
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	protected void insertNewRecord(Record record) throws DataException, IndexException {
		
	}
	
	public class DBOaiIdentiferForServiceDAO extends DefaultOaiIdentiferForServiceDAO {
		protected int id=0;
		
		public long getNextOaiIdForService(int serviceId) {
			return id++;
		}
		
	}
	
}
