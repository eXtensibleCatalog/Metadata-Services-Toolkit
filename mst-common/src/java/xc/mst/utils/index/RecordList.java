/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.utils.index;

import java.util.AbstractList;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;

import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.MSTConfiguration;

/**
 * A list of Records resulting from a Lucene query. This class maps Lucene's
 * Hits Object into a Java collection without loading all the Records into memory at
 * once (like an ArrayList would.)
 * 
 * @author Eric Osisek
 */
public class RecordList extends AbstractList<Record> {
    /**
     * The maximum number of results to
     */
    private static final int MAX_RESULTS = 2048;

    /**
     * An Object which manages the Solr index
     */
    protected SolrIndexManager indexMgr = (SolrIndexManager) MSTConfiguration.getInstance().getBean("SolrIndexManager");

    /**
     * The current offset into the results of the query that are in the document list
     */
    private int currentOffset = 0;

    /**
     * A list of documents from the query between results currentOffset and currentOffset+MAX_RESULTS
     */
    private SolrDocumentList docs = null;

    /**
     * The query for which the RecordList was built
     */
    private SolrQuery query = null;

    /**
     * The service used to get a record from a Lucene document
     */
    private static RecordService service = (RecordService) MSTConfiguration.getInstance().getBean("RecordService");

    /**
     * The number of elements in the list
     */
    private int size = 0;

    /**
     * A reference to the logger for this class
     */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Constructs a RecordList around a Solr query. The docs returned by the query
     * are assumed to all be Record Objects
     * 
     * @param query
     *            The Solr query for which the RecordList was built
     */
    public RecordList(SolrQuery query) throws IndexException {
        if (query != null) {
            this.query = query;
            query.setRows(MAX_RESULTS);
            query.setStart(currentOffset);
            docs = indexMgr.getDocumentList(query);
            size = (int) docs.getNumFound();
        }
    }

    /**
     * Constructs a RecordList around a Solr query. The docs returned by the query
     * are assumed to all be Record Objects
     * 
     * @param query
     *            The Solr query for which the RecordList was built
     */
    public RecordList(SolrQuery query, int numRowsToFetch) throws IndexException {
        if (query != null) {
            this.query = query;
            query.setRows(numRowsToFetch);
            query.setStart(currentOffset);
            docs = indexMgr.getDocumentList(query);
            size = (int) docs.getNumFound();
        }
    }

    /**
     * Gets the record at a given index
     * 
     * @param index
     *            The index of the Record to get
     * @return The record at the specified index
     */
    public Record get(int index) {
        try {
            if (query == null || size == 0)
                return null;

            if (index < size) {
                if (index < currentOffset + MAX_RESULTS) {
                    if (currentOffset == 0) {
                        return service.getRecordFromDocument(docs.get(index));
                    } else {
                        return service.getRecordFromDocument(docs.get(index % currentOffset));
                    }

                } else {
                    query.setRows(MAX_RESULTS);
                    currentOffset = currentOffset + MAX_RESULTS;
                    query.setStart(currentOffset);
                    docs = indexMgr.getDocumentList(query);
                    if (currentOffset == 0) {
                        return service.getRecordFromDocument(docs.get(index));
                    } else {
                        return service.getRecordFromDocument(docs.get(index % currentOffset));
                    }

                }
            } else {
                log.error("Index out of bounds exception for RecordList index " + index);
                return null;
            }
        } catch (DatabaseConfigException e) {
            log.error("Cannot connect to the database with the parameters from the config file.", e);

            return null;
        } catch (IndexException ie) {
            log.error("Cannot connect to Solr Server. Check the port in configuration file.", ie);

            return null;
        } catch (Exception e) {
            log.error("RecordList error at currentOffset = " + currentOffset + " and index= " + index, e);
            return null;
        }
    }

    /**
     * The set method is not used because RecordLists are read only. It is
     * only included because it is required to extend the AbstractList class.
     * 
     * @throws UnsupportedOperationException
     *             Whenever this method is called
     */
    public Record set(int index, Record element) {
        throw new UnsupportedOperationException("An attempt was made to set an element on a RecordList.  RecordLists are read only.");
    }

    /**
     * Returns the size of the RecordList
     * 
     * @return The size of the RecordList
     */
    public int size() {
        return size;
    }
}
