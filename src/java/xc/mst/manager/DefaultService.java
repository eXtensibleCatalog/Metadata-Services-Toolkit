package xc.mst.manager;

import xc.mst.manager.record.DBRecordDao;

public class DefaultService {
	
	protected DBRecordDao recordDao = null;

	public DBRecordDao getRecordDao() {
		return recordDao;
	}

	public void setRecordDao(DBRecordDao recordDao) {
		this.recordDao = recordDao;
	}

}
