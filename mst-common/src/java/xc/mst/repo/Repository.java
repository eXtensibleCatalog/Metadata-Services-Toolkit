/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.repo;

import gnu.trove.TLongByteHashMap;
import gnu.trove.TLongHashSet;

import java.util.Date;
import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordCounts;
import xc.mst.bo.service.Service;

public interface Repository {

    public String getName();

    public void setName(String name);

    public int getSize();

    public Date getLastModified();

    public int getNumRecords();

    public int getNumActiveRecords();

    public String getRecordStatsByType();

    public void installOrUpdateIfNecessary(String previousVersion, String currentVersion);

    public void addRecord(Record record);

    public void addRecords(List<Record> records);

    public boolean commitIfNecessary(boolean force, long processedRecordsCount,
            RecordCounts incomingRecordCounts, RecordCounts outgoingRecordCounts);

    public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet);

    /**
     * Get number of records that satisfy the given criteria
     *
     * @param from
     *            From date to harvest the records
     * @param until
     *            Until date to harvest records
     * @param startingId
     *            starting record id to query from
     * @param inputFormat
     *            format of the record
     * @param inputSet
     *            Set of record
     * @return
     */
    public long getRecordCount(Date from, Date until, Format inputFormat, Set inputSet);

    /**
     * Get record header information
     *
     * @param from
     *            From date to harvest the records
     * @param until
     *            Until date to harvest records
     * @param startingId
     *            starting record id to query from
     * @param inputFormat
     *            format of the record
     * @param inputSet
     *            Set of record
     * @return
     */
    public List<Record> getRecordHeader(Date from, Date until, Long startingId, Format inputFormat, Set inputSet);

    public Record getRecord(String oaiId);

    public Record getRecord(long id);

    public List<Long> getPredecessorIds(Record r);

    public void injectSuccessors(Record r);

    public void injectSuccessorIds(Record r);

    public Provider getProvider();

    public Service getService();

    public void setProvider(Provider p);

    public void setService(Service s);

    public void populatePredecessors(TLongHashSet predecessors);

    public void addLink(long fromRecordId, long toRecordId);

    public void activateRecord(String type, long recordId);

    public List<Long> getLinkedRecordIds(Long toRecordId);

    public void processComplete();

    public boolean ready4harvest();

    public int getPersistentPropertyAsInt(String key, int def);

    public long getPersistentPropertyAsLong(String key, long def);

    public String getPersistentProperty(String key);

    public void setPersistentProperty(String key, int value);

    public void setPersistentProperty(String key, long value);

    public void setPersistentProperty(String key, String value);

    public void injectHarvestInfo(Record r);

    public void populatePreviousStatuses(TLongByteHashMap previousStatuses, boolean service);

    public void persistPreviousStatuses(TLongByteHashMap previousStatuses);

}
