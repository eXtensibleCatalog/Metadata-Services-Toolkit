/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.manager.record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.jdom.Element;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordIfc;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.bo.service.Service;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.manager.IndexException;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.Util;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.Records;
import xc.mst.utils.index.SolrIndexManager;

/**
 * Solr implementation of the service class to query, add, update and
 * delete records from an index.
 * 
 * @author Eric Osisek
 */
public class DefaultRecordService extends RecordService {
    private static final Logger LOG = Logger.getLogger(DefaultRecordService.class);

    /**
     * The FRBR level ID term
     */
    protected final static Term TERM_FRBR_LEVEL_ID = new Term(FIELD_FRBR_LEVEL_ID, "");

    /**
     * The up link term
     */
    protected final static Term TERM_UP_LINK = new Term(FIELD_UP_LINK, "");

    /**
     * The trait term
     */
    protected final static Term TERM_TRAIT = new Term(FIELD_TRAIT, "");

    // yes, these are thread safe.
    protected static final DateTimeFormatter UTC_PARSER = ISODateTimeFormat.dateTimeParser();
    protected static DateTimeFormatter UTC_FORMATTER = null;
    static {
        UTC_FORMATTER = ISODateTimeFormat.dateTime();
        UTC_FORMATTER = UTC_FORMATTER.withZone(DateTimeZone.UTC);
    }

    public OutputRecord createRecord() {
        Record rec = new Record();
        getRepositoryDAO().injectId(rec);
        return rec;
    }

    public void injectNewId(Record r) {
        getRepositoryDAO().injectId(r);
    }

    @Override
    public List<Record> getAll() throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records");

        // Create a query to get all records
        SolrQuery query = new SolrQuery().setQuery("*:*");

        // Return the list of results
        return new RecordList(query);
    } // end method getAll()

    @Override
    public Record getById(long id) throws DatabaseConfigException, IndexException {
        return getRepositoryService().getRecord(id);
    }

    @Override
    public Record loadBasicRecord(long id) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting the record with ID " + id);

        // Create a query to get the record with the requested record ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_RECORD_ID + ":" + Long.toString(id));

        // Get the result of the query
        SolrDocumentList docs = null;
        SolrIndexManager sim = (SolrIndexManager) config.getBean("SolrIndexManager");
        docs = sim.getDocumentList(query);

        // Return null if we couldn't find the record with the correct ID
        if (docs == null || docs.size() == 0) {
            if (log.isDebugEnabled())
                log.debug("Could not find the record with ID " + id + ".");

            return null;
        } // end if(record not found)

        if (log.isDebugEnabled())
            log.debug("Parcing the record with ID " + id + " from the Lucene Document it was stored in.");

        return getBasicRecordFromDocument(docs.get(0));
    } // end method loadBasicRecord(long)

    @Override
    public List<Record> getByProviderId(int providerId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with provider ID " + providerId);

        // Create a query to get the Documents with the requested provider ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROVIDER_ID + ":" + Integer.toString(providerId));

        // Return the list of results
        return new RecordList(query);
    } // end method getByProviderId(int)

    @Override
    public int getCountByProviderId(int providerId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting count of records with provider ID " + providerId);

        // Create a query to get the Documents with the requested provider ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROVIDER_ID + ":" + Integer.toString(providerId));

        RecordList records = new RecordList(query, 0);
        // Return the size
        return records.size();
    } // end method getCountByProviderId(int)

    @Override
    public List<Record> getByServiceId(int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with service ID " + serviceId);

        // Create a query to get the Documents with the requested service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Return the list of results
        return new RecordList(query);
    } // end method getByServiceId(int)

    @Override
    public Record getLastCreatedRecord(int serviceId) throws IndexException, DatabaseConfigException {
        if (log.isDebugEnabled())
            log.debug("Getting the last created record with service ID " + serviceId);

        // Create a query to get the Documents with the requested service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_SERVICE_ID + ":" + Integer.toString(serviceId));
        query.setSortField(FIELD_RECORD_ID, ORDER.desc);
        query.setRows(1);
        query.setStart(0);

        // Get the result of the query
        SolrDocumentList docs = null;
        SolrIndexManager sim = (SolrIndexManager) config.getBean("SolrIndexManager");
        docs = sim.getDocumentList(query);

        // Return null if we couldn't find the record with the correct ID
        if (docs == null || docs.size() == 0) {
            if (log.isDebugEnabled())
                log.debug("There are no records available for service Id = " + serviceId + " .");

            return null;
        } // end if(record not found)

        return getRecordFromDocument(docs.get(0));

    } // end method getByServiceId(int)

    @Override
    public long getNumberOfRecordsByServiceId(int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with service ID " + serviceId);

        // Create a query to get the Documents with the requested service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Return the list of results
        return new RecordList(query).size();
    } // end method getByServiceId(long)

    @Override
    public RecordList getProcessedByServiceId(int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with processing service ID " + serviceId);

        // Create a query to get the Documents with the requested service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROCESSED_BY_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Return the list of results
        return new RecordList(query);
    } // end method getByProcessingServiceId(int)

    @Override
    public List<Record> getByHarvestId(int harvestId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with harvest ID " + harvestId);

        // Create a query to get the Documents with the requested harvest ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_HARVEST_ID + ":" + Integer.toString(harvestId));

        // Return the list of results
        return new RecordList(query);
    } // end method getByHarvestId(int)

    @Override
    public List<Record> getByFormatIdAndServiceId(int formatId, int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with format ID " + formatId + " and service ID " + serviceId);

        // Create a query to get the Documents with the requested format ID and service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_FORMAT_ID + ":" + Integer.toString(formatId) + " AND "
                + FIELD_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Return the list of results
        return new RecordList(query);
    } // end method getByFormatIdAndServiceId(int, int)

    @Override
    public List<Record> getInputForServiceToProcess(int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records that are input for the service with service ID " + serviceId);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_INPUT_FOR_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Return the list of results
        return new Records(query);
    } // end method getInputForService(int)

    @Override
    public Records getByInputToServiceAndRecordType(int serviceId, String recordType) throws IndexException {

        if (log.isDebugEnabled())
            log.debug("Getting all records that are input for the service with service ID " + serviceId + " and have record type " + recordType);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_INPUT_FOR_SERVICE_ID + ":" + Integer.toString(serviceId) + " AND "
                + FIELD_RECORD_TYPE + ":" + recordType);

        // Return the list of results
        return new Records(query);
    }

    @Override
    public List<Record> getInputForService(int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records that are input for the service with service ID " + serviceId);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_INPUT_FOR_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Return the list of results
        return new RecordList(query);
    } // end method getInputForService(int)

    // @Override
    // public List<Record> getInputForService(int serviceId, int start, int rows) throws IndexException
    // {
    // if(log.isDebugEnabled())
    // log.debug("Getting all records that are input for the service with service ID " + serviceId);
    //
    // // Create a query to get the Documents with the requested input for service IDs
    // SolrQuery query = new SolrQuery();
    // query.setQuery(FIELD_INPUT_FOR_SERVICE_ID + ":" + Integer.toString(serviceId));
    // query.setRows(rows);
    // query.setStart(start);
    //
    // SolrDocumentList docs = indexMgr.getDocumentList(query);
    // log.info("Num of docs="+ docs.getNumFound());
    // ArrayList<Record> records = new ArrayList<Record>();
    // Iterator<SolrDocument> itr = docs.iterator();
    //
    // try {
    // while (itr.hasNext()) {
    // records.add(getRecordFromDocument(itr.next()));
    // }
    // } catch (DatabaseConfigException dce) {
    //
    // }
    //
    // // Return the list of results
    // return records;
    // } // end method getInputForService(int)

    @Override
    public int getCountOfRecordsToBeProcessedVyService(int serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Get count of records that are input for the service with service ID " + serviceId);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_INPUT_FOR_SERVICE_ID + ":" + Integer.toString(serviceId));

        RecordList recordList = new RecordList(query, 0);

        // Return the count
        return recordList.size();
    } // end method getCountOfRecordsToBeProcessedVyService(int)

    @Override
    public List<Record> getByProviderName(String providerName) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records harvested from the provider with the name " + providerName);

        // Create a query to get the Documents with the requested provider name
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROVIDER_NAME + ":" + providerName);

        // Return the list of results
        return new RecordList(query);
    } // end method getByProviderName(String)

    @Override
    public List<Record> getByProviderUrl(String providerUrl) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records harvested from the provider with the URL " + providerUrl);

        // Create a query to get the Documents with the requested provider URL
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROVIDER_URL + ":" + providerUrl);

        // Return the list of results
        return new RecordList(query);
    } // end method getByProviderUrl(String)

    @Override
    public List<Record> getBySetName(String setName) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records from the set with the name " + setName);

        // Create a query to get the Documents with the requested set name
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_SET_NAME + ":" + setName);

        // Return the list of results
        return new RecordList(query);
    } // end method getBySetName(String)

    @Override
    public List<Record> getBySetSpec(String setSpec) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records from the set with the setSpec " + setSpec);

        // Create a query to get the Documents with the requested set spec
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_SET_SPEC + ":" + setSpec.replaceAll(":", "\\\\:"));

        // Return the list of results
        return new RecordList(query);
    } // end method getBySetSpec(String)

    @Override
    public List<Record> getByFormatName(String formatName) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records from the format with the name " + formatName);

        // Create a query to get the Documents with the requested format name
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_FORMAT_NAME + ":" + formatName);

        // Return the list of results
        return new RecordList(query);
    } // end method getByFormatName(String)

    @Override
    public Record getByOaiIdentifier(String identifier) throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting the record with the OAI identifier " + identifier);

        // Create a query to get the record with the correct identifier
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_OAI_IDENTIFIER + ":" + identifier.replaceAll(" ", "_").replaceAll(":", "\\\\:") + " AND " + FIELD_DELETED + ":" + "false");

        // Get the result of the query
        RecordList records = new RecordList(query);

        // Return null if we couldn't find the record
        if (records == null || records.size() == 0) {
            if (log.isDebugEnabled())
                log.debug("Could not find the record with the OAI identifier " + identifier + ".");

            return null;
        } // end if(record not found)

        if (log.isDebugEnabled())
            log.debug("Parcing the record with the OAI identifier " + identifier + " from the Lucene Document it was stored in.");

        return records.get(0);
    } // end method getByOaiIdentifier(String)

    @Override
    public List<Record> getByOaiIdentifiers(List<String> identifiers) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting the record with the OAI identifier " + identifiers);

        // Create a query to get the record with the correct identifier
        SolrQuery query = new SolrQuery();
        StringBuffer b = new StringBuffer();

        int size = identifiers.size();

        int counter = 0;

        b.append(FIELD_DELETED + ":" + "false" + " AND ");
        for (String identifier : identifiers) {
            b.append(FIELD_OAI_IDENTIFIER + ":" + identifier.replaceAll(" ", "_").replaceAll(":", "\\\\:"));
            counter++;

            if (counter != size) {
                b.append(" OR ");
            }
        }

        query.setQuery(b.toString());

        return new RecordList(query);
    } // end method getByOaiIdentifier(String)

    @Override
    public Record getByOaiIdentifierAndProvider(String identifier, int providerId) throws DatabaseConfigException, IndexException {
        TimingLogger.start("getByOaiIdentifierAndProvider");
        if (log.isDebugEnabled())
            log.debug("Getting the record with the OAI identifier " + identifier + " and provider ID " + providerId);

        // Create a query to get the record with the requested OAI identifier and provider ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_OAI_IDENTIFIER + ":" + identifier.replaceAll(" ", "_").replaceAll(":", "\\\\:") + " AND "
                + FIELD_PROVIDER_ID + ":" + Integer.toString(providerId) + " AND " + FIELD_DELETED + ":" + "false");

        // Get the result of the query
        RecordList records = new RecordList(query);

        // Return null if we couldn't find the record
        if (records == null || records.size() == 0) {
            if (log.isDebugEnabled())
                log.debug("Could not find the record with the OAI identifier " + identifier + " and provider ID " + providerId + ".");

            return null;
        } // end if(record not found)

        if (log.isDebugEnabled())
            log.debug("Parcing the record with the OAI identifier " + identifier + " and provider ID " + providerId + " from the Lucene Document it was stored in.");

        TimingLogger.stop("getByOaiIdentifierAndProvider");
        return records.get(0);
    } // end method getByOaiIdentifierAndProvider(String, int)

    @Override
    public Record getByOaiIdentifierAndService(String identifier, int serviceId) throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting the record with the OAI identifier " + identifier + " and service ID " + serviceId);

        // Create a query to get the record with the requested OAI identifier and service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_OAI_IDENTIFIER + ":" + identifier.replaceAll(" ", "_").replaceAll(":", "\\\\:") + " AND "
                + FIELD_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Get the result of the query
        RecordList records = new RecordList(query);

        // Return null if we couldn't find the record
        if (records == null || records.size() == 0) {
            if (log.isDebugEnabled())
                log.debug("Could not find the record with the OAI identifier " + identifier + " and service ID " + serviceId + ".");

            return null;
        } // end if(record not found)

        if (log.isDebugEnabled())
            log.debug("Parcing the record with the OAI identifier " + identifier + " and service ID " + serviceId + " from the Lucene Document it was stored in.");

        return records.get(0);
    } // end method getByOaiIdentifierAndService(String, int)

    @Override
    public Record getInputForServiceByOaiIdentifier(String identifier, int serviceId) throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting the input record with the OAI identifier " + identifier + " and service ID " + serviceId);

        // Create a query to get the record with the requested OAI identifier and service ID
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_OAI_IDENTIFIER + ":" + identifier.replaceAll(" ", "_").replaceAll(":", "\\\\:") + " AND "
                + FIELD_PROCESSED_BY_SERVICE_ID + ":" + Integer.toString(serviceId));

        // Get the result of the query
        RecordList records = new RecordList(query);

        // Return null if we couldn't find the record
        if (records == null || records.size() == 0) {
            if (log.isDebugEnabled())
                log.debug("Could not find the input record with the OAI identifier " + identifier + " and service ID " + serviceId + ".");

            return null;
        } // end if(record not found)

        if (log.isDebugEnabled())
            log.debug("Parcing the input record with the OAI identifier " + identifier + " and service ID " + serviceId + " from the Lucene Document it was stored in.");

        return records.get(0);
    }

    @Override
    public List<Record> getByProcessedFrom(long processedFromId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records that were processed from the record with ID " + processedFromId);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROCESSED_FROM + ":" + Long.toString(processedFromId) + " AND " + FIELD_DELETED + ":" + "false");

        // Return the list of results
        return new RecordList(query);
    } // end method getByProcessedFrom(long)

    @Override
    public List<Record> getSuccessorsCreatedByServiceId(long recordId, long serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting successor of record Id " + recordId + " that were created by service id" + serviceId);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROCESSED_FROM + ":" + Long.toString(recordId) + " AND " + FIELD_DELETED + ":" + "false" + " AND " + FIELD_SERVICE_ID + ":" + serviceId);

        // Return the list of results
        return new RecordList(query);
    } // end method getByProcessedFrom(long)

    @Override
    public RecordList getSuccessorsCreatedByServiceIdIncludingDeletedRecords(long recordId, long serviceId) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records that were processed from the record with ID " + recordId);

        // Create a query to get the Documents with the requested input for service IDs
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_PROCESSED_FROM + ":" + Long.toString(recordId) + " AND " + FIELD_SERVICE_ID + ":" + serviceId);

        // Return the list of results
        return new RecordList(query);
    } // end method getByProcessedFrom(long)

    @Override
    public List<Record> getByTrait(String trait) throws IndexException {
        if (log.isDebugEnabled())
            log.debug("Getting all records with the trait " + trait);

        // Create a query to get the Documents with the requested trait
        SolrQuery query = new SolrQuery();
        query.setQuery(FIELD_TRAIT + ":" + trait.replaceAll(" ", "_").replaceAll(":", "\\\\:"));

        // Return the list of results
        return new RecordList(query);
    } // end method getByTrait(String)

    @Override
    public Record getEarliest(int serviceId) {
        return null;
    } // end method getEarliest(int)

    @Override
    public long getCount(Date fromDate, Date untilDate, Set set, int formatId, int serviceId) throws IndexException {
        Date from; // fromDate, or the minimum value for a Date if fromDate is null
        Date until; // toDate, or now if toDate is null

        // If from is null, set it to the minimum possible value
        // Otherwise set it to the same value as fromDate
        if (fromDate == null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(new Date(0));
            c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - ((c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000)));
            from = c.getTime();
        } else {
            from = fromDate;
        }

        // If to is null, set it to now
        // Otherwise set it to the same value as toDate
        if (untilDate == null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(new Date());
            c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - ((c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000)));

            until = c.getTime();
        } else {
            until = untilDate;
        }

        // True if we're getting the count for a specific set, false if we're getting it for all records
        boolean useSet = (set != null);

        // True if we're getting the count for a specific metadataPrefix, false if we're getting it for all records
        boolean useMetadataPrefix = (formatId > 0);

        DateFormat format = DateFormat.getInstance();

        if (log.isDebugEnabled())
            log.debug("Counting the records updated later than " + format.format(from) + " and earlier than " + format.format(until) + (useSet ? " with set ID " + set.getSetSpec() : "") + (useMetadataPrefix ? " with format ID " + formatId : ""));

        // Create a query to get the Documents for unprocessed records
        SolrQuery query = new SolrQuery();
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append(FIELD_SERVICE_ID).append(":").append(Integer.toString(serviceId));

        if (useSet)
            queryBuffer.append(" AND ").append(FIELD_SET_SPEC).append(":").append(set.getSetSpec());
        if (useMetadataPrefix)
            queryBuffer.append(" AND ").append(FIELD_FORMAT_ID).append(":").append(Integer.toString(formatId));

        queryBuffer.append(" AND ").append(FIELD_DELETED).append(":").append("false");

        query.setQuery(queryBuffer.toString());

        if (from != null && until != null)
            query.addFilterQuery(FIELD_UPDATED_AT + ":[" + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(from)) + " TO " + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(until)) + "]");

        // Get the result of the query
        RecordList records = new RecordList(query, 0);

        if (log.isDebugEnabled())
            log.debug("Found " + records.size() + " records updated later than " + format.format(from) + " and earlier than " + format.format(until) + (useSet ? " with set ID " + set.getSetSpec() : "") + (useMetadataPrefix ? " with format ID " + formatId : ""));

        // Return the list of results
        return records.size();
    } // end method getCount(Date, Date, int, int, int)

    @Override
    public SolrBrowseResult getOutgoingRecordsInRange(Date from, Date until, Set set, int formatId, int offset, int numResults, int serviceId)
            throws IndexException {
        // True if we're getting the records for a specific set, false if we're getting all records
        boolean useSet = (set != null);

        // True if we're getting the count for a specific metadataPrefix, false if we're getting it for all records
        boolean useMetadataPrefix = (formatId > 0);

        if (log.isDebugEnabled())
            log.debug("Getting the records updated later than " + from + " and earlier than " + until + (useSet ? " in set with setSPec " + set.getSetSpec() : "") + (useMetadataPrefix ? " with format ID " + formatId : ""));

        // Create a query to get the Documents for unprocessed records
        SolrQuery query = new SolrQuery();
        StringBuffer queryBuffer = new StringBuffer();

        if (useSet)
            queryBuffer.append(FIELD_SET_SPEC).append(":").append(set.getSetSpec());
        if (useSet && useMetadataPrefix)
            queryBuffer.append(" AND ");
        if (useMetadataPrefix)
            queryBuffer.append(FIELD_FORMAT_ID + ":").append(Integer.toString(formatId));

        if (useSet || useMetadataPrefix)
            queryBuffer.append(" AND ");

        queryBuffer.append(FIELD_SERVICE_ID).append(":").append(Integer.toString(serviceId));

        // Get only fields OAI header & OAI XML
        query.addField(FIELD_OAI_HEADER);
        query.addField(FIELD_OAI_XML);
        query.addField(FIELD_DELETED);
        query.setQuery(queryBuffer.toString());
        if (from != null && until != null) {
            query.addFilterQuery(FIELD_UPDATED_AT + ":[" + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(from)) + " TO " + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(until)) + "]");
        }

        if (from != null && until == null) {
            query.addFilterQuery(FIELD_UPDATED_AT + ":[" + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(from)) + " TO *]");
        }

        if (from == null && until != null) {
            query.addFilterQuery(FIELD_UPDATED_AT + ":[ * TO " + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(until)) + "]");
        }

        query.setStart(offset);
        query.setRows(numResults);
        SolrDocumentList docs = ((SolrIndexManager) config.getBean("SolrIndexManager")).getDocumentList(query);
        Iterator<SolrDocument> iteration = docs.iterator();
        List<Record> records = new ArrayList<Record>();

        while (iteration.hasNext()) {
            records.add(getRecordXMLFromDocument(iteration.next()));
        }

        SolrBrowseResult result = new SolrBrowseResult(records);
        result.setTotalNumberOfResults(docs.getNumFound());

        // Return the empty list if we couldn't find the records
        if (result.getTotalNumberOfResults() == 0) {
            if (log.isDebugEnabled())
                log.debug("Could not find any records updated later than " + from + " and earlier than " + until + (useSet ? " in set with setSPec " + set.getSetSpec() : "") + (useMetadataPrefix ? " with format ID " + formatId : ""));

        } else {
            if (log.isDebugEnabled())
                log.debug("Found " + records.size() + " records updated later than " + from + " and earlier than " + until + (useSet ? " in set with setSPec " + set.getSetSpec() : "") + (useMetadataPrefix ? " with format ID " + formatId : ""));
        }

        return result;
    } // end method getOutgoingRecordsInRange(Date, Date, int, int, int, int, int)

    @Override
    public Record getBasicRecordFromDocument(SolrDocument doc) {
        // Create a Record object to store the result
        Record record = new Record();

        // Set the fields on the record Object and return it
        record.setId(Long.parseLong((String) doc.getFieldValue(FIELD_RECORD_ID)));
        // record.setFrbrLevelId(Long.parseLong((String)doc.getFieldValue(FIELD_FRBR_LEVEL_ID)));
        record.setDeleted(Boolean.parseBoolean((String) doc.getFieldValue(FIELD_DELETED)));
        if (doc.getFieldValue(FIELD_OAI_DATESTAMP) != null) {
            record.setOaiDatestamp((Date) doc.getFieldValue(FIELD_OAI_DATESTAMP));
        }
        record.setOaiHeader((String) doc.getFieldValue(FIELD_OAI_HEADER));
        // record.setOaiIdentifier((String) doc.getFieldValue(FIELD_OAI_IDENTIFIER));
        record.setOaiXml((String) doc.getFieldValue(FIELD_OAI_XML));
        record.setHarvestScheduleName((String) doc.getFieldValue(FIELD_HARVEST_SCHEDULE_NAME));

        if (doc.getFieldValue(FIELD_CREATED_AT) != null)
            record.setCreatedAt((Date) doc.getFieldValue(FIELD_CREATED_AT));
        if (doc.getFieldValue(FIELD_UPDATED_AT) != null)
            record.setUpdatedAt((Date) doc.getFieldValue(FIELD_UPDATED_AT));

        Collection<Object> traits = doc.getFieldValues(FIELD_TRAIT);
        if (traits != null) {
            Iterator<Object> itr = traits.iterator();
            while (itr.hasNext()) {
                record.addTrait((String) itr.next());
            }
        }

        // Return the record we parsed from the document
        return record;
    } // end method getBasicRecordFromDocument(Document)

    @Override
    public Record getRecordFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException {
        // Create a Record object to store the result
        Record record = new Record();

        // The OAI identifier
        String oaiId = (String) doc.getFieldValue(FIELD_OAI_IDENTIFIER);

        // Set the fields on the record Object and return it
        record.setId(Long.parseLong((String) doc.getFieldValue(FIELD_RECORD_ID)));
        // record.setFrbrLevelId(Long.parseLong((String)doc.getFieldValue(FIELD_FRBR_LEVEL_ID)));
        record.setDeleted(Boolean.parseBoolean((String) doc.getFieldValue(FIELD_DELETED)));
        record.setFormat(getFormatDAO().getById(Integer.parseInt((String) doc.getFieldValue(FIELD_FORMAT_ID))));
        if (doc.getFieldValue(FIELD_OAI_DATESTAMP) != null) {
            record.setOaiDatestamp((Date) doc.getFieldValue(FIELD_OAI_DATESTAMP));
        }
        record.setOaiHeader((String) doc.getFieldValue(FIELD_OAI_HEADER));
        // record.setOaiIdentifier(oaiId);
        record.setOaiXml((String) doc.getFieldValue(FIELD_OAI_XML));
        record.setProvider(getProviderDAO().loadBasicProvider(Integer.parseInt((String) doc.getFieldValue(FIELD_PROVIDER_ID))));
        record.setService(getServiceDAO().loadBasicService(Integer.parseInt((String) doc.getFieldValue(FIELD_SERVICE_ID))));
        record.setHarvest(getHarvestDAO().getById(Integer.parseInt((String) doc.getFieldValue(FIELD_HARVEST_ID))));
        record.setHarvestScheduleName((String) doc.getFieldValue(FIELD_HARVEST_SCHEDULE_NAME));

        if (doc.getFieldValue(FIELD_RECORD_TYPE) != null) {
            // record.setType((String)doc.getFieldValue(FIELD_RECORD_TYPE));
        }

        Collection<Object> sets = doc.getFieldValues(FIELD_SET_SPEC);
        if (sets != null)
            for (Object set : sets)
                record.addSet(getSetDAO().getBySetSpec((String) set));

        Collection<Object> errors = doc.getFieldValues(FIELD_ERROR);
        if (errors != null)
            for (Object error : errors) {
                // record.addMessage(new RecordMessage((String)error));
            }

        Collection<Object> uplinks = doc.getFieldValues(FIELD_UP_LINK);
        if (uplinks != null) {
            for (Object uplink : uplinks) {
                record.addUpLink((String) uplink);
            }
        }

        Collection<Object> processedFroms = doc.getFieldValues(FIELD_PROCESSED_FROM);
        if (processedFroms != null) {
            record.setNumberOfPredecessors(processedFroms.size());
            for (Object processedFrom : processedFroms) {
                record.addProcessedFrom(loadBasicRecord(Long.parseLong((String) processedFrom)));
            }
        }

        Collection<Object> successors = doc.getFieldValues(FIELD_SUCCESSOR);
        if (successors != null) {
            record.setNumberOfSuccessors(successors.size());
            for (Object successor : successors) {
                record.addSuccessor(loadBasicRecord(Long.parseLong((String) successor)));
            }
        }

        Collection<Object> inputForServices = doc.getFieldValues(FIELD_INPUT_FOR_SERVICE_ID);
        if (inputForServices != null)
            for (Object inputForService : inputForServices)
                record.addInputForService(getServiceDAO().loadBasicService(Integer.parseInt((String) inputForService)));

        Collection<Object> processedByServices = doc.getFieldValues(FIELD_PROCESSED_BY_SERVICE_ID);
        if (processedByServices != null)
            for (Object processedByService : processedByServices)
                record.addProcessedByService(getServiceDAO().loadBasicService(Integer.parseInt((String) processedByService)));

        Collection<Object> traits = doc.getFieldValues(FIELD_TRAIT);
        if (traits != null)
            for (Object trait : traits)
                record.addTrait((String) trait);

        if (doc.getFieldValue(FIELD_CREATED_AT) != null) {
            record.setCreatedAt((Date) doc.getFieldValue(FIELD_CREATED_AT));
        }
        if (doc.getFieldValue(FIELD_UPDATED_AT) != null) {
            record.setUpdatedAt((Date) doc.getFieldValue(FIELD_UPDATED_AT));
        }

        // Return the record we parsed from the document
        return record;
    } // end method getRecordFromDocument(Document)

    @Override
    public Record getRecordFieldsForBrowseFromDocument(SolrDocument doc) throws DatabaseConfigException, IndexException {
        Record record = getRepositoryService().getRecord(Long.parseLong((String) doc.getFieldValue(FIELD_RECORD_ID)));

        return record;
    }

    @Override
    public Record getRecordXMLFromDocument(SolrDocument doc) {
        // Create a Record object to store the result
        Record record = new Record();

        record.setOaiHeader((String) doc.getFieldValue(FIELD_OAI_HEADER));
        record.setOaiXml((String) doc.getFieldValue(FIELD_OAI_XML));
        record.setDeleted(Boolean.parseBoolean((String) doc.getFieldValue(FIELD_DELETED)));

        // Return the record we parsed from the document
        return record;
    } // end method getRecordXMLFromDocument(Document)

    @Override
    protected SolrInputDocument setFieldsOnDocument(Record record, SolrInputDocument doc, boolean generateNewId) throws DatabaseConfigException {
        if (log.isDebugEnabled())
            log.debug("Set Field on Document");

        boolean throttleSolr = false;

        // If we need to generate an ID, set the record's ID to the next available record ID
        if (generateNewId) {
            record.setId(getXcIdentifierForFrbrElementDAO().getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_RECORD));
        }

        // If the oaiDatestamp is null, set it to the current time
        if (record.getOaiDatestamp() == null)
            record.setOaiDatestamp(generateNewId ? record.getCreatedAt() : record.getUpdatedAt());

        // If the header is null, set it based on the identifier, datestamp, and sets
        if (record.getOaiHeader() == null || record.getOaiHeader().length() <= 0) {
            StringBuilder header = new StringBuilder();
            header.append("<header>\n");
            header.append("\t<identifier>").append(record.getOaiIdentifier()).append("</identifier>\n");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

            header.append("\t<datestamp>").append(sdf.format(record.getOaiDatestamp())).append("</datestamp>\n");

            // Get each set from the list of set IDs this record belongs to. If the set is
            // not null, add its setSpec to the header.
            for (Set set : record.getSets())
                if (set != null)
                    header.append("\t<setSpec>").append(set.getSetSpec()).append("</setSpec>\n");

            header.append("</header>");

            record.setOaiHeader(header.toString());
        } // end if(header needs to be set)

        // TimingLogger.turnOff();

        // Set the appropriate fields on it.
        doc.addField(FIELD_RECORD_ID, Long.toString(record.getId()));
        /*
        if (record.getType() != null) {
            doc.addField(FIELD_RECORD_TYPE, record.getType());
            TimingLogger.add("SOLR-"+FIELD_RECORD_TYPE, record.getType().length());
        }

        doc.addField(FIELD_FRBR_LEVEL_ID, Long.toString(record.getFrbrLevelId()));
        TimingLogger.add("SOLR-"+FIELD_FRBR_LEVEL_ID, Long.toString(record.getFrbrLevelId()).length());
        */

        if (record.getCreatedAt() != null) {
            doc.addField(FIELD_CREATED_AT, record.getCreatedAt());
            TimingLogger.add("SOLR-" + FIELD_CREATED_AT, 10);
        }

        doc.addField(FIELD_DELETED, Boolean.toString(record.getDeleted()));
        TimingLogger.add("SOLR-" + FIELD_DELETED, Boolean.toString(record.getDeleted()).length());

        doc.addField(FIELD_FORMAT_ID, Integer.toString(record.getFormat().getId()));
        TimingLogger.add("SOLR-" + FIELD_FORMAT_ID, Integer.toString(record.getFormat().getId()).length());
        doc.addField(FIELD_FORMAT_NAME, record.getFormat().getName());
        TimingLogger.add("SOLR-" + FIELD_FORMAT_NAME, record.getFormat().getName().length());

        doc.addField(FIELD_PROVIDER_ID, (record.getProvider() == null ? "0" : Integer.toString(record.getProvider().getId())));
        TimingLogger.add("SOLR-" + FIELD_PROVIDER_ID, (record.getProvider() == null ? "0" : Integer.toString(record.getProvider().getId())).length());
        if (record.getProvider() != null) {
            if (!throttleSolr) {
                doc.addField(FIELD_PROVIDER_NAME, (record.getProvider().getName() == null ? "" : record.getProvider().getName()));
                TimingLogger.add("SOLR-" + FIELD_PROVIDER_NAME, (record.getProvider().getName() == null ? "" : record.getProvider().getName()).length());
                doc.addField(FIELD_PROVIDER_URL, (record.getProvider().getOaiProviderUrl() == null ? "" : record.getProvider().getOaiProviderUrl()));
                TimingLogger.add("SOLR-" + FIELD_PROVIDER_URL, (record.getProvider().getOaiProviderUrl() == null ? "" : record.getProvider().getOaiProviderUrl()).length());
            }
        }

        doc.addField(FIELD_HARVEST_ID, (record.getHarvest() == null ? "0" : Integer.toString(record.getHarvest().getId())));
        TimingLogger.add("SOLR-" + FIELD_HARVEST_ID, (record.getHarvest() == null ? "0" : Integer.toString(record.getHarvest().getId())).length());

        if (record.getHarvest() != null) {
            HarvestSchedule schedule = record.getHarvest().getHarvestSchedule();
            if (schedule != null) {
                doc.addField(FIELD_HARVEST_SCHEDULE_NAME, schedule.getScheduleName());
                TimingLogger.add("SOLR-" + FIELD_HARVEST_SCHEDULE_NAME, schedule.getScheduleName().length());
            }
        }

        if (record.getHarvest() != null && record.getProvider() != null) {
            doc.addField(FIELD_HARVEST_START_TIME, record.getProvider().getName() + " " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(record.getHarvest().getStartTime()));
            TimingLogger.add("SOLR-" + FIELD_HARVEST_START_TIME, record.getProvider().getName().length() + 10);
        }

        doc.addField(FIELD_SERVICE_ID, (record.getService() == null ? "0" : Integer.toString(record.getService().getId())));
        TimingLogger.add("SOLR-" + FIELD_SERVICE_ID, (record.getService() == null ? "0" : Integer.toString(record.getService().getId())).length());

        if (record.getService() != null) {
            doc.addField(FIELD_SERVICE_NAME, record.getService().getName());
            TimingLogger.add("SOLR-" + FIELD_SERVICE_NAME, record.getService().getName().length());
        }

        doc.addField(FIELD_OAI_IDENTIFIER, record.getOaiIdentifier());
        TimingLogger.add("SOLR-" + FIELD_OAI_IDENTIFIER, record.getOaiIdentifier().length());

        if (record.getOaiDatestamp() != null) {
            // If the record is output of a harvest then the OAI date stamp is already in UTC format
            if (record.getProvider() != null) {
                doc.addField(FIELD_OAI_DATESTAMP, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(record.getOaiDatestamp()));
                TimingLogger.add("SOLR-" + FIELD_OAI_DATESTAMP, 10);
            } else {
                doc.addField(FIELD_OAI_DATESTAMP, record.getOaiDatestamp());
                TimingLogger.add("SOLR-" + FIELD_OAI_DATESTAMP, 10);
            }
        }

        if (!throttleSolr) {
            doc.addField(FIELD_OAI_HEADER, record.getOaiHeader());
            TimingLogger.add("SOLR-" + FIELD_OAI_HEADER, record.getOaiHeader().length());

            doc.addField(FIELD_OAI_XML, record.getOaiXml());
            TimingLogger.add("SOLR-" + FIELD_OAI_XML, record.getOaiXml().length());
            // System.out.println(record.getOaiXml());
        }

        if (record.getUpdatedAt() != null) {
            doc.addField(FIELD_UPDATED_AT, record.getUpdatedAt());
            TimingLogger.add("SOLR-" + FIELD_UPDATED_AT, 10);
        }

        for (String upLink : record.getUpLinks()) {
            doc.addField(FIELD_UP_LINK, upLink);
            TimingLogger.add("SOLR-" + FIELD_UP_LINK, upLink.length());
        }

        for (Set set : record.getSets()) {
            doc.addField(FIELD_SET_SPEC, set.getSetSpec());
            TimingLogger.add("SOLR-" + FIELD_SET_SPEC, set.getSetSpec().length());
            doc.addField(FIELD_SET_NAME, set.getDisplayName());
            TimingLogger.add("SOLR-" + FIELD_SET_NAME, set.getDisplayName().length());
        } // end loop over sets

        for (Record processedFrom : record.getProcessedFrom()) {
            doc.addField(FIELD_PROCESSED_FROM, Long.toString(processedFrom.getId()));
            TimingLogger.add("SOLR-" + FIELD_PROCESSED_FROM, Long.toString(processedFrom.getId()).length());
        }

        for (RecordIfc successor : record.getSuccessors()) {
            doc.addField(FIELD_SUCCESSOR, Long.toString(successor.getId()));
            TimingLogger.add("SOLR-" + FIELD_SUCCESSOR, Long.toString(successor.getId()).length());
        }

        for (Service inputForService : record.getInputForServices()) {
            doc.addField(FIELD_INPUT_FOR_SERVICE_ID, Long.toString(inputForService.getId()));
            TimingLogger.add("SOLR-" + FIELD_INPUT_FOR_SERVICE_ID, Long.toString(inputForService.getId()).length());
        }

        for (Service processedByService : record.getProcessedByServices()) {
            doc.addField(FIELD_PROCESSED_BY_SERVICE_ID, Long.toString(processedByService.getId()));
            TimingLogger.add("SOLR-" + FIELD_PROCESSED_BY_SERVICE_ID, Long.toString(processedByService.getId()).length());
        }

        for (String trait : record.getTraits()) {
            doc.addField(FIELD_TRAIT, trait.replaceAll(" ", "_"));
            TimingLogger.add("SOLR-" + FIELD_TRAIT, trait.length());
        }

        /*
        for(RecordMessage error : record.getMessages()) {
            String message = error.getServiceId() + "-" + error.getMessageCode() + ":" + error.getMessage();
            doc.addField(FIELD_ERROR, message);
            TimingLogger.add("SOLR-"+FIELD_ERROR, message.length());
        }
        */

        StringBuffer all = new StringBuffer();
        if (record.getFormat() != null) {
            all.append(record.getFormat().getName());
            all.append(" ");
        }
        if (record.getProvider() != null) {
            all.append(record.getProvider().getName());
            all.append(" ");
        }

        for (Set set : record.getSets()) {
            all.append(set.getSetSpec());
            all.append(" ");
            all.append(set.getDisplayName());
            all.append(" ");
        }

        if (record.getService() != null) {
            all.append(record.getService().getName());
            all.append(" ");
        }

        for (RecordMessage error : record.getMessages()) {
            // all.append(error.getServiceId() + "-" + error.getMessageCode() + ":" + error.getMessage());
            all.append(" ");
        }

        if (record.getHarvest() != null) {
            all.append(record.getHarvest().getStartTime());
        }

        all.append(record.getOaiIdentifier());

        if (!throttleSolr) {
            doc.addField(FIELD_ALL, all.toString());
            TimingLogger.add("SOLR-" + FIELD_ALL, all.length());
        }

        TimingLogger.turnOn();

        return doc;
    } // end method setFieldsOnDocument(Record, Document, boolean)

    @Override
    protected String escapeString(String str) {
        return str.replaceAll(":", "\\\\:")
                  .replaceAll("\\(", "\\\\\\(")
                  .replaceAll("\\)", "\\\\\\)")
                  .replaceAll("!", "\\\\!");
    }

    public Record parse(Element recordEl) {
        return parse(recordEl, null);
    }

    @SuppressWarnings("unchecked")
    public Record parse(Element recordEl, Provider provider) {
        Record r = new Record();
        try {
            Element headerEl = recordEl.getChild("header", recordEl.getNamespace());
            Element identifierElement = headerEl.getChild("identifier", recordEl.getNamespace());
            if (identifierElement != null) {
                r.setHarvestedOaiIdentifier(identifierElement.getText());
                LOG.debug("identifierElement.getText(): " + identifierElement.getText());
            }
            Element datestampElement = headerEl.getChild("datestamp", recordEl.getNamespace());
            if (datestampElement != null && !StringUtils.isEmpty(datestampElement.getText())) {
                r.setOaiDatestamp(new Date(UTC_PARSER.parseDateTime(datestampElement.getText()).getMillis()));
            }

            /*
             * I think this isn't really necessary
             *
            Element predecessorEl = headerEl.getChild("predecessors");
            if (predecessorEl != null) {
                List children = predecessorEl.getChildren("predecessor");
                if (children != null) {
                    for (Object predObj : children) {
                        Element predEl = (Element)predObj;
                        r.addPredecessor(predEl.getText());
                    }
                }
            }
            */

            // TODO: BDA - This might not be right, but I don't think I really care
            // since this is only currently used in the filesystem testing
            // mechanism and we don't care about setSpecs there.
            List setSpecList = headerEl.getChildren("setSpec", recordEl.getNamespace());
            if (setSpecList != null && setSpecList.size() > 0) {
                for (Object setSpecObj : setSpecList) {
                    Element setSpecEl = (Element) setSpecObj;

                    String setSpec = null;
                    if (provider != null) {
                        setSpec = provider.getName().replace(' ', '-');
                    }
                    if (setSpec != null) {
                        setSpec += ":";
                    }
                    setSpec += setSpecEl.getText();

                    // Split the set into its components
                    String[] setSpecLevels = setSpec.split(":");

                    // This will build the setSpecs to which the record belongs
                    StringBuilder setSpecAtLevel = new StringBuilder();

                    // Loop over all levels in the set spec
                    for (String setSpecLevel : setSpecLevels) {
                        // Append the set at the current level to the setSpec at the previous level to
                        // get the setSpec for the current level. Append colons as needed
                        setSpecAtLevel.append(setSpecAtLevel.length() <= 0 ? setSpecLevel : ":" + setSpecLevel);

                        String currentSetSpec = setSpecAtLevel.toString();

                        // If the set's already in the index, get it
                        Set set = getSetDAO().getBySetSpec(currentSetSpec);

                        // Add the set if there wasn't already one in the database
                        if (set == null && provider != null) {
                            set = new Set();
                            set.setSetSpec(currentSetSpec);
                            set.setDisplayName(currentSetSpec);
                            set.setIsProviderSet(false);
                            set.setIsRecordSet(true);
                            TimingLogger.start("setDao.insertForProvider");
                            getSetDAO().insertForProvider(set, provider.getId());
                            TimingLogger.stop("setDao.insertForProvider");
                        }
                        // Add the set's ID to the list of sets to which the record belongs
                        r.addSet(set);
                    }
                }
            } else {
                if (provider != null) {
                    String setSpec = provider.getName().replace(' ', '-');
                    // If the set's already in the index, get it
                    Set set = getSetDAO().getBySetSpec(setSpec);

                    // Add the set if there wasn't already one in the database
                    if (set == null && provider != null) {
                        set = new Set();
                        set.setSetSpec(setSpec);
                        set.setDisplayName(set.getDisplayName());
                        set.setIsProviderSet(false);
                        set.setIsRecordSet(true);
                        TimingLogger.start("setDao.insertForProvider");
                        getSetDAO().insertForProvider(set, provider.getId());
                        TimingLogger.stop("setDao.insertForProvider");
                    }
                    r.addSet(set);
                }
            }

            String status = headerEl.getAttributeValue("status");
            if (!StringUtils.isEmpty(status)) {
                if ("DELETED".equals(status.toUpperCase()) || "D".equals(status.toUpperCase())) {
                    r.setStatus(Record.DELETED);
                } else if ("ACTIVE".equals(status.toUpperCase()) || "A".equals(status.toUpperCase())) {
                    r.setStatus(Record.ACTIVE);
                } else if ("HELD".equals(status.toUpperCase()) || "H".equals(status.toUpperCase())) {
                    r.setStatus(Record.HELD);
                } else if ("REPLACED".equals(status.toUpperCase()) || "R".equals(status.toUpperCase())) {
                    r.setStatus(Record.REPLACED);
                }
            }

            // Metadata element will not exist in case the record has status = deleted. So null check is required here.
            if (recordEl.getChild("metadata", recordEl.getNamespace()) != null) {
                Element xmlEl = (Element) recordEl.getChild("metadata", recordEl.getNamespace()).getChildren().get(0);
                xmlEl.detach();
                r.setOaiXmlEl(xmlEl);
            }
        } catch (Throwable t) {
            getUtil().throwIt(t);
        }

        return r;
    }

    public Element createJDomElement(Record r) {
        return createJDomElement(r, OAI_NS_2_0);
    }

    public Element createJDomElement(Record r, String namespace) {
        Element recordEl = new Element("record", namespace);
        Element headerEl = new Element("header", namespace);
        recordEl.addContent(headerEl);
        Element identifierElement = new Element("identifier", namespace);
        headerEl.addContent(identifierElement);
        identifierElement.setText(r.getOaiIdentifier());
        Element datestampElement = new Element("datestamp", namespace);
        headerEl.addContent(datestampElement);
        if (r.getOaiDatestamp() != null)
            datestampElement.setText(UTC_FORMATTER.print(r.getOaiDatestamp().getTime()));

        Element predsrEl = new Element("predecessors", namespace);
        headerEl.addContent(predsrEl);

        for (RecordIfc p : r.getPredecessors()) {
            Element predEl = new Element("predecessor", namespace);
            Record p2 = (Record) p;
            predEl.setText(p2.getHarvestedOaiIdentifier());
            predsrEl.addContent(predEl);
        }

        if (r.getSets() != null) {
            for (Set s : r.getSets()) {
                Element setSpecEl = new Element("setSpec", namespace);
                headerEl.addContent(setSpecEl);
                setSpecEl.setText(s.getDisplayName());
            }
        }

        if (r.getStatus() != 0) {
            if (r.getStatus() == Record.ACTIVE) {
                headerEl.setAttribute("status", "active");
            } else if (r.getStatus() == Record.DELETED) {
                headerEl.setAttribute("status", "deleted");
            } else if (r.getStatus() == Record.HELD) {
                headerEl.setAttribute("status", "held");
            } else if (r.getStatus() == Record.REPLACED) {
                headerEl.setAttribute("status", "replaced");
            }
        }
        if (r.getMode().equals(Record.STRING_MODE)) {
            r.setMode(Record.JDOM_MODE);
        }
        if (!r.getDeleted()) {
            boolean noContent = false;
            if (r.getMode().equals(Record.STRING_MODE) && StringUtils.isEmpty(r.getOaiXml())) {
                noContent = true;
                ;
            }
            if (r.getMode().equals(Record.JDOM_MODE) && r.getOaiXmlEl() == null) {
                noContent = true;
            }
            if (!noContent) {
                r.setMode(Record.JDOM_MODE);
                Element metadataEl = new Element("metadata", namespace);
                recordEl.addContent(metadataEl);
                metadataEl.addContent(r.getOaiXmlEl());
            }
        }
        LOG.debug("r: " + r);
        return recordEl;
    }

    public String getOaiIdentifier(long id, Provider p) {
        return getOaiIdentifier(id, p, null);
    }

    public String getOaiIdentifier(long id, Service s) {
        return getOaiIdentifier(id, null, s);
    }

    public String getOaiIdentifier(long id, Provider p, Service s) {
        StringBuilder sb = new StringBuilder();
        sb.append("oai:");
        sb.append(MSTConfiguration.getInstance().getProperty("DomainNameIdentifier"));
        sb.append(":");
        String name = null;
        sb.append(MSTConfiguration.getInstanceName());
        sb.append("/");
        if (p != null) {
            name = p.getName();
        }
        if (s != null) {
            name = s.getName();
        }
        name = new Util().normalizeName(name);
        sb.append(name);
        sb.append("/");
        sb.append(id);
        return sb.toString();
    }

}
