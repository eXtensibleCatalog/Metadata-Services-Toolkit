package xc.mst.manager.record;

import java.util.Date;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.index.Records;

public class DBRecordService extends DefaultRecordService {
	
	DBRecordDao recordDao = new DBRecordDao();
	
	// actually these should probably go in the dao
	protected int offset = 0;
	protected int recordsAtOnce = 5000;

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

		TimingLogger.start("recordDao.insert");
		boolean retVal = recordDao.insert(record);
		TimingLogger.stop("recordDao.insert");
		return retVal;
	} // end method insert(Record)
	
	public void commit() {
		TimingLogger.log("before recordDao.commit");
		recordDao.commit();
		TimingLogger.log("after recordDao.commit");
	}
	
	public Record getByOaiIdentifierAndService(String id, int serviceId) {
		return null;
	}
	
	public Records getInputForServiceToProcess(int serviceId) {
		return getInputForServiceToProcess(serviceId, false);
	}
	
	public Records getInputForServiceToProcess(int serviceId, boolean reset) {
		// implement in dao
		return null;
	}

}
