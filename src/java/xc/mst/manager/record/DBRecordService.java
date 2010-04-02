package xc.mst.manager.record;

import java.util.Date;
import java.util.List;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.utils.index.RecordList;

public class DBRecordService extends DefaultRecordService {
	
	DBRecordDao recordDao = new DBRecordDao();


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

		// Create a Document object and set it's type field
		//SolrInputDocument doc = new SolrInputDocument();
		//doc.addField(FIELD_INDEXED_OBJECT_TYPE, record.getIndexedObjectType());

		//doc = setFieldsOnDocument(record, doc, true);

		boolean retVal = recordDao.insert(record);
		return retVal;
	} // end method insert(Record)
	
	public void commit(boolean force) {
		recordDao.commit(force);
	}
	
	public Record getByOaiIdentifierAndService(String id, int serviceId) {
		return null;
	}
	
	public List<Record> getInputForServiceToProcess(int serviceId) {
		return getInputForServiceToProcess(serviceId, false);
	}
	
	public List<Record> getInputForServiceToProcess(int serviceId, boolean reset) {
		return recordDao.getInputForServiceToProcess(serviceId, reset);
	}

	@Override
	public RecordList getSuccessorsCreatedByServiceIdIncludingDeletedRecords(long recordId, long serviceId) throws IndexException {
		return null;
	}
	
	@Override
	public boolean update(Record record) throws DataException, IndexException {
		return recordDao.update(record);
	}
}
