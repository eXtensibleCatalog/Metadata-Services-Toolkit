/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;

/**
 * @author JohnB
 *
 */
public class RepositoryDeletionManager extends WorkerThread {

	private final static Logger LOG = Logger.getLogger(Constants.LOGGER_GENERAL);

	protected int m_processedRecordCount = 0;
	
	// TODO can pull out an intermediate class that uses some of MetadataServiceManager as a refactoring exercise...
	protected Repository m_incomingRepository = null;
	
	public Repository getIncomingRepository() {
		return m_incomingRepository;
	}

	public void setIncomingRepository(Repository incomingRepository) {
		m_incomingRepository = incomingRepository;
	}

	/* (non-Javadoc)
	 * @see xc.mst.scheduling.WorkerThread#setup()
	 */
	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see xc.mst.scheduling.WorkerThread#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getName();
	}

	/* (non-Javadoc)
	 * @see xc.mst.scheduling.WorkerThread#doSomeWork()
	 */
	@Override
	public boolean doSomeWork() {
		LOG.debug("RepositoryDeletionManager.doSomeWork() begin method "+getName());
		if (m_incomingRepository != null) {
			List<Record> records = m_incomingRepository.getRecords(new Date(0), new Date(), 0l, /*getMarc21Format()*/ null, null);
			for (Record r : records) {
				r.setStatus(Record.DELETED);
				m_processedRecordCount++;
			}
			//TODO correct way to 'commit?' changes?  see DefaultRepository
			m_incomingRepository.commitIfNecessary(true);  // TODO - or use (true, m_processedRecordCount)? - or ???
		}
		LOG.debug("RepositoryDeletionManager.doSomeWork() end of method");
		return false;
	}

	/* (non-Javadoc)
	 * @see xc.mst.scheduling.WorkerThread#getDetailedStatus()
	 */
	@Override
	public String getDetailedStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see xc.mst.scheduling.WorkerThread#getRecordsProcessedThisRun()
	 */
	@Override
	public long getRecordsProcessedThisRun() {
		// TODO Auto-generated method stub
		return m_processedRecordCount;
	}

	/* (non-Javadoc)
	 * @see xc.mst.scheduling.WorkerThread#getRecords2ProcessThisRun()
	 */
	@Override
	public long getRecords2ProcessThisRun() {
		return m_incomingRepository.getNumRecords();
	}

}
