package xc.mst.manager.record;

import java.util.Date;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.utils.TimingLogger;

public class DBRecordService extends DefaultRecordService {
	
	DefaultRecordDao recordDao = new DefaultRecordDao(); 

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
		TimingLogger.start("recordDao.commit");
		recordDao.commit();
		TimingLogger.stop("recordDao.commit");
	}
	
}
