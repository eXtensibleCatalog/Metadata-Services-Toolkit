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
import gnu.trove.TLongLongHashMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordCounts;
import xc.mst.bo.service.Service;
import xc.mst.manager.BaseService;
import xc.mst.utils.Util;

public class DefaultRepository extends BaseService implements Repository {

    private static final Logger LOG = Logger.getLogger(DefaultRepository.class);

    // This is meant only to be a cache of what is not yet in the DB. This will not
    // keep all pred-succs in memory.
    protected Map<Long, java.util.Set<Record>> predSuccMap = new HashMap<Long, java.util.Set<Record>>();

    // used to persist links to db...
    protected Map<Long, List<Long>> fromToUplinksAdded = new HashMap<Long, List<Long>>();
    protected Map<Long, List<Long>> toFromUplinksAdded = new HashMap<Long, List<Long>>();
    protected Map<Long, List<Long>> fromToUplinksRemoved = new HashMap<Long, List<Long>>();
    protected Map<Long, List<Long>> toFromUplinksRemoved = new HashMap<Long, List<Long>>();

    // really parentIDsOfRecordsToActivate
    protected TLongHashSet recordsToActivate = new TLongHashSet();
    protected Map<String, AtomicInteger> recordCountsToActivateByType = new HashMap<String, AtomicInteger>();
    protected Map<String, Long> completeListSizeMap = new LinkedHashMap<String, Long>(1000);

    protected String name = null;

    protected Provider provider = null;
    protected Service service = null;
    
    public Map<String, Long> getCompletListSizeMap() {
        return this.completeListSizeMap;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Date getLastModified() {
        return getRepositoryDAO().getLastModified(name);
    }

    public int getNumRecords() {
        return getRepositoryDAO().getNumRecords(name);
    }

    public int getNumActiveRecords() {
        return getRepositoryDAO().getNumActiveRecords(name);
    }

    public String getRecordStatsByType() {
        return getRepositoryDAO().getRecordStatsByType(name);
    }

    public void installOrUpdateIfNecessary(final String previousVersion, final String currentVersion) {
        final Repository thisthis = this;
        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    LOG.debug("config.getProperty(\"version\"): " + config.getProperty("version"));
                    if (previousVersion == null &&
                            currentVersion != null &&
                            (currentVersion.startsWith("0.3.") ||
                             currentVersion.startsWith("1.")
                             )
                        ) {
                        getRepositoryDAO().createTables(thisthis);
                    }
                } catch (Throwable t) {
                    LOG.error("", t);
                    status.setRollbackOnly();
                }
            }
        });
    }

    public void populatePredecessors(TLongHashSet predecessors) {
        getRepositoryDAO().populatePredecessors(name, predecessors);
    }

    protected boolean exists() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = getUtil().normalizeName(name);
    }

    public int getSize() {
        return getRepositoryDAO().getSize(name);
    }

    public void addRecord(Record record) {
        if (record.getPredecessors() != null) {
            for (InputRecord ir : record.getPredecessors()) {
                java.util.Set<Record> succIds = predSuccMap.get(ir.getId());
                if (succIds == null) {
                    succIds = new TreeSet<Record>();
                    predSuccMap.put(ir.getId(), succIds);
                }
                succIds.add(record);
            }
        }
        getRepositoryDAO().addRecord(name, record);
    }

    public void addRecords(List<Record> records) {
        getRepositoryDAO().addRecords(name, records);
    }

    public boolean commitIfNecessary(boolean force) {
        return commitIfNecessary(force, 0);
    }

    public boolean commitIfNecessary(boolean force, long processedRecordsCount) {
        return commitIfNecessary(force, processedRecordsCount, null, null);
    }

    public boolean commitIfNecessary(boolean force, long processedRecordsCount,
            RecordCounts incomingRecordCounts, RecordCounts outgoingRecordCounts) {
        if (getRepositoryDAO().commitIfNecessary(name, force, processedRecordsCount)) {
            predSuccMap.clear();
            
            List<long[]> uplinksAdded = getArrayListFromKeyedMap(fromToUplinksAdded);
            getRepositoryDAO().persistLinkedRecordIds(name, uplinksAdded);            
            uplinksAdded.clear();
            fromToUplinksAdded.clear(); toFromUplinksAdded.clear();
            
            List<long[]> uplinksRemoved = getArrayListFromKeyedMap(fromToUplinksRemoved);
            getRepositoryDAO().persistLinkedRecordIdsRemoved(name, uplinksRemoved);
            uplinksRemoved.clear();
            fromToUplinksRemoved.clear(); toFromUplinksRemoved.clear();

            activateRecords();
    	    activateLinkedRecords();
    	    
    	    for (Map.Entry<String, AtomicInteger> me : recordCountsToActivateByType.entrySet()) {
    	        int num = me.getValue().get();
    	        for (int i = 0; i < num; i++) {
    	            outgoingRecordCounts.incr(me.getKey(), Record.ACTIVE, Record.HELD);
    	            if (me.getKey() != null && !RecordCounts.TOTALS.equals(me.getKey())) {
    	                outgoingRecordCounts.incr(null, Record.ACTIVE, Record.HELD);
    	            }
    	        }
    	    }
    	    recordCountsToActivateByType.clear();

            getRecordCountsDAO().persistRecordCounts(name, incomingRecordCounts, outgoingRecordCounts);
            if (incomingRecordCounts != null)
                incomingRecordCounts.clear();
            if (outgoingRecordCounts != null)
                outgoingRecordCounts.clear();
            return true;
        } else {
            return force;
        }
    }

    private void activateRecords() {
	    getRepositoryDAO().activateRecords(name, recordsToActivate);
	    recordsToActivate.clear();
    }
    
    public List<Long> getPredecessorIds(Record r) {
        return getRepositoryDAO().getPredecessors(name, r.getId());
    }

    // TODO: you need to check the cache as well
    public Record getRecord(String oaiId) {
        int indexOfSlash = oaiId.lastIndexOf("/");

        if (indexOfSlash != -1) {
            String id = oaiId.substring(indexOfSlash + 1);
            return getRecord(Long.parseLong(id));
        } else {
            return null;
        }

    }

    public Record getRecord(long id) {
    	Record	r = getRepositoryDAO().getRecord(name, id);
        if (r != null) {
            r.setSets(getRepositoryDAO().getSets(name, id));
            getMessageService().injectMessages(r);
        }
        return r;
    }
    
    public Record getUnpersistedRecord(long id) {
    	return getRepositoryDAO().getUnpersistedRecord(id);
    }

    public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
    	if (Util.dateIsNull(from)) {
            return getRecords(from, until, startingId, inputFormat, inputSet, new char[] { Record.ACTIVE });
    	}
        return getRecords(from, until, startingId, inputFormat, inputSet, new char[] { Record.ACTIVE, Record.DELETED });
    }

    public List<Record> getRecords(Date from, Date until, Long startingId, Format inputFormat, Set inputSet, char[] statuses) {
        LOG.debug("from:" + from + " until:" + until + " startingId:" + startingId + " inputFormat:" + inputFormat + " inputSet:" + inputSet);
        List<Record> records = getRepositoryDAO().getRecordsWSets(name, from, until, startingId, inputFormat, inputSet, statuses);
        // List<Record> records = getRepositoryDAO().getRecords(name, from, until, startingId, inputFormat, inputSet, statuses);
        if (records == null) {
            LOG.debug("no records found");
        } else {
            LOG.debug("records.size(): " + records.size());
        }
        return records;
    }

    public long getRecordCount(final Date from, final Date until,
            final Format inputFormat, final Set inputSet) {
        final String key = "from:" + from + " until:" + until + " inputFormat:" + inputFormat + " inputSet:" + inputSet;
        LOG.debug(key);
        if (completeListSizeMap.containsKey(key)) {
            LOG.debug("found recordCount in cache");
            return completeListSizeMap.get(key);
        } else {
            long recordCount = getRepositoryDAO().getRecordCount(name, from, until, inputFormat, inputSet, false);
            completeListSizeMap.put(key, recordCount);
            if (recordCount == -1) {
                new Thread() {
                    public void run() {
                        long recordCount = getRepositoryDAO().getRecordCount(
                                name, from, until, inputFormat, inputSet, true);
                        completeListSizeMap.put(key, recordCount);
                    }
                }.start();
            }
            return recordCount;
        }
    }

    public List<Record> getRecordHeader(Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
        LOG.debug("from:" + from + " until:" + until + " startingId:" + startingId + " inputFormat:" + inputFormat + " inputSet:" + inputSet);
        List<Record> records = getRepositoryDAO().getRecordHeader(name, from, until, startingId, inputFormat, inputSet);
        if (records == null) {
            LOG.debug("no records found");
        } else {
            LOG.debug("records.size(): " + records.size());
        }
        return records;
    }

    public List<Record> getRecordsWSets(Date from, Date until, Long startingId, char[] statuses) {
        List<Record> recs = getRepositoryDAO().getRecordsWSets(name, from, until, startingId, null, null, statuses);
        for (Record r : recs) {
            getMessageService().injectMessageMessage(r);
        }
        return recs;
    }

    public void injectSuccessors(Record r) {
        List<Record> succs = getRepositoryDAO().getSuccessors(name, r.getId());
        if (succs != null) {
            r.getSuccessors().addAll(succs);
        }
    }

    public void injectSuccessorIds(Record r) {
        java.util.Set<Record> succIds = predSuccMap.get(r.getId());
        if (succIds == null) {
            succIds = getRepositoryDAO().getSuccessorIds(name, r.getId());
            predSuccMap.put(r.getId(), succIds);
        }
        if (succIds != null && succIds.size() > 0) {
            for (Record sr : succIds) {
                r.getSuccessors().add(sr.clone());
            }
        }
    }

    /**
     * I slightly future dated the timestamp of the records so that a record will always
     * have been available from it's update_date forward. We need to wait for that
     * future dating to become present before moving on here. Otherwise, the next service
     * will not pick up all the records that were just inserted.
     */
    public void sleepUntilReady() {
        boolean keepSleeping = true;
        boolean repoCreated = false;
        Date lm = null;
        while (keepSleeping) {
            repoCreated = ready4harvest();
            if (repoCreated) {
                lm = getLastModified();
                keepSleeping = (lm != null && new Date().before(lm));
            }
            try {
                Thread.sleep(500);
            } catch (Throwable t) {
                getUtil().throwIt(t);
            }
        }
    }

    public void deleteAllData() {
        getRepositoryDAO().deleteAllData(this.name);
    }
    
    protected List<Long> getLongKeyedMap(Long key, Map<Long, List<Long>> m1) {
        List<Long> m2 = m1.get(key);
        if (m2 == null) {
            m2 = new ArrayList<Long>();
            m1.put(key, m2);
        }
        return m2;
    }
    
    protected List<long[]> getArrayListFromKeyedMap (Map<Long, List<Long>> fromToMap) {
        List<long[]> arrList = new ArrayList<long[]>();
        for (Long fromRecordId : fromToMap.keySet()) {
        	List<Long> toRecordIds = fromToMap.get(fromRecordId);
        	for (Long toRecordId : toRecordIds) {
        		arrList.add(new long[] { fromRecordId, toRecordId });
        	}
        }
        return arrList;
    }

    
    public void addLink(long fromRecordId, long toRecordId) {
    	List<Long> m = getLongKeyedMap(fromRecordId, fromToUplinksAdded);
    	m.add(toRecordId);
    	m = getLongKeyedMap(toRecordId, toFromUplinksAdded);
    	m.add(fromRecordId);
    }

    public void removeLink(long fromRecordId, long toRecordId) {
    	List<Long> m = getLongKeyedMap(fromRecordId, fromToUplinksRemoved);
    	m.add(toRecordId);
    	m = getLongKeyedMap(toRecordId, toFromUplinksRemoved);
    	m.add(fromRecordId);
    }

    public List<Long> getLinkedRecordIds(Long toRecordId) {
        List<Long> l = getRepositoryDAO().getLinkedRecordIds(name, toRecordId);
	    if (toFromUplinksAdded.containsKey(toRecordId)) {    
	    	List<Long> added = getLongKeyedMap(toRecordId, toFromUplinksAdded);
	    	for (Long id: added) {
	    		if (!l.contains(id)) l.add(id);
	    	}
	    }
	    if (toFromUplinksRemoved.containsKey(toRecordId)) {
	    	List<Long> removed = getLongKeyedMap(toRecordId, toFromUplinksRemoved);
	    	for (Long id: removed) {
	    		l.remove(id);
	    	}
	    }
        return l;
    }

    public List<Long> getLinkedToRecordIds(Long fromRecordId) {
        List<Long> l = getRepositoryDAO().getLinkedToRecordIds(name, fromRecordId);
        if (fromToUplinksAdded.containsKey(fromRecordId)) {
	    	List<Long> added = getLongKeyedMap(fromRecordId, fromToUplinksAdded);
	    	for (Long id: added) {
	    		if (!l.contains(id)) l.add(id);
	    	}
        }
        if (fromToUplinksRemoved.containsKey(fromRecordId)) {
	    	List<Long> removed = getLongKeyedMap(fromRecordId, fromToUplinksRemoved);
	    	for (Long id: removed) {
	    		l.remove(id);
	    	}
        }
        return l;
    }
    
    public void activateLinkedRecords() {
    	boolean activatedRecords = false;
    	List<Integer> ids = getRepositoryDAO().getAllRecordIdsHavingStatus(name, Record.HELD);
    	for (Integer id : ids) {
    		if (getRepositoryDAO().hasActiveRecordLinks(name, id)) {
    			activateRecord("holdings", id);
    			activatedRecords = true;
    		}
    	}
    	if (activatedRecords) activateRecords();
    }

    public void activateRecord(String type, long recordId) {
        AtomicInteger ai = recordCountsToActivateByType.get(type);
        if (ai == null) {
            ai = new AtomicInteger();
            recordCountsToActivateByType.put(type, ai);
        }
        ai.incrementAndGet();
        recordsToActivate.add(recordId);
    }

    public void processComplete() {
        getRepositoryDAO().createIndicesIfNecessary(name);
        getRepositoryDAO().updateOutgoingRecordCounts(name);
    }

    public boolean ready4harvest() {
        return getRepositoryDAO().ready4harvest(name);
    }

    public int getPersistentPropertyAsInt(String key, int def) {
        String val = getRepositoryDAO().getPersistentProperty(name, key);
        if (!StringUtils.isEmpty(val)) {
            try {
                return Integer.parseInt(val);
            } catch (Throwable t) {
                // do nothing
            }
        }
        return def;
    }

    public long getPersistentPropertyAsLong(String key, long def) {
        String val = getRepositoryDAO().getPersistentProperty(name, key);
        if (!StringUtils.isEmpty(val)) {
            try {
                return Long.parseLong(val);
            } catch (Throwable t) {
                // do nothing
            }
        }
        return def;
    }

    public String getPersistentProperty(String key) {
        return getRepositoryDAO().getPersistentProperty(name, key);
    }

    public void setPersistentProperty(String key, int value) {
        getRepositoryDAO().setPersistentProperty(name, key, value + "");
    }

    public void setPersistentProperty(String key, long value) {
        getRepositoryDAO().setPersistentProperty(name, key, value + "");
    }

    public void setPersistentProperty(String key, String value) {
        getRepositoryDAO().setPersistentProperty(name, key, value);
    }

    public void injectHarvestInfo(Record r) {
        getRepositoryDAO().injectHarvestInfo(name, r);
    }

    public void populatePreviousStatuses(TLongByteHashMap previousStatuses, boolean service) {
        getRepositoryDAO().populatePreviousStatuses(name, previousStatuses, service);
    }

    public void persistPreviousStatuses(TLongByteHashMap previousStatuses) {
        getRepositoryDAO().persistPreviousStatuses(name, previousStatuses);
    }

}
