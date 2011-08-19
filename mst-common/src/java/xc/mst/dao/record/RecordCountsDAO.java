/**
 * Copyright (c) 2011 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.dao.record;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import xc.mst.bo.record.RecordCounts;
import xc.mst.repo.RepositoryDAO;

public class RecordCountsDAO extends RepositoryDAO {

    private static final Logger LOG = Logger.getLogger(RecordCountsDAO.class);

    public final static String INCOMING_RECORD_COUNTS = "incoming_record_counts";
    public final static String OUTGOING_RECORD_COUNTS = "outgoing_record_counts";

    protected RecordCounts injectRecordCounts(RecordCounts rc, List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {
            if (rc == null) {
                rc = new RecordCounts((Date) row.get("harvest_start_date"), INCOMING_RECORD_COUNTS);
            }
            Map<String, AtomicInteger> counts = new LinkedHashMap<String, AtomicInteger>();
            rc.getCounts().put((String) row.get("type_name"), counts);

            for (Map.Entry<String, Object> me : row.entrySet()) {
                if (RecordCounts.INCOMING_STATUS_COLUMN_NAMES.contains(me.getKey())) {
                    counts.put(me.getKey(), new AtomicInteger((Integer) me.getValue()));
                } else if (RecordCounts.UNEXPECTED_ERROR.equals(me.getKey())) {
                    counts.put(me.getKey(), new AtomicInteger((Integer) me.getValue()));
                } else if (RecordCounts.UPD_PREV_COLUMN_NAMES.containsValue(me.getKey())) {
                    for (String value : RecordCounts.UPD_PREV_COLUMN_NAMES.values()) {
                        if (me.getKey().equals(value)) {
                            counts.put(me.getKey(), new AtomicInteger((Integer) me.getValue()));
                        }
                    }
                }
            }
        }
        return rc;
    }

    public RecordCounts getMostRecentIncomingRecordCounts(String repoName) {
        RecordCounts rc = null;
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
                " select * " +
                        " from " + getTableName(repoName, INCOMING_RECORD_COUNTS) +
                        " where harvest_start_date = " +
                        "(select max(harvest_start_date) from " + getTableName(repoName, INCOMING_RECORD_COUNTS) + ")");
        rc = injectRecordCounts(rc, rows);
        if (rc != null)
            rc.setIncomingOutgoing(RecordCounts.INCOMING);
        return rc;
    }

    public RecordCounts getTotalIncomingRecordCounts(String repoName) {
        RecordCounts rc = null;
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
                " select * " +
                        " from " + getTableName(repoName, INCOMING_RECORD_COUNTS) +
                        " where harvest_start_date = ? ", RecordCounts.TOTALS_DATE);
        rc = injectRecordCounts(rc, rows);
        if (rc != null)
            rc.setIncomingOutgoing(RecordCounts.INCOMING);
        return rc;
    }

    public RecordCounts getMostRecentOutgoingRecordCounts(String repoName) {
        RecordCounts rc = null;
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
                " select * " +
                        " from " + getTableName(repoName, OUTGOING_RECORD_COUNTS) +
                        " where harvest_start_date = " +
                        "(select max(harvest_start_date) from " + getTableName(repoName, OUTGOING_RECORD_COUNTS) + ")");
        rc = injectRecordCounts(rc, rows);
        if (rc != null)
            rc.setIncomingOutgoing(RecordCounts.OUTGOING);
        return rc;
    }

    public RecordCounts getTotalOutgoingRecordCounts(String repoName) {
        RecordCounts rc = null;
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
                " select * " +
                        " from " + getTableName(repoName, OUTGOING_RECORD_COUNTS) +
                        " where harvest_start_date = ? ", RecordCounts.TOTALS_DATE);
        rc = injectRecordCounts(rc, rows);
        if (rc != null)
            rc.setIncomingOutgoing(RecordCounts.OUTGOING);
        return rc;
    }

    public void persistRecordCounts(String repoName, RecordCounts incomingRecordCounts, RecordCounts outgoingRecordCounts) {
        if (incomingRecordCounts != null)
            persistRecordCounts(repoName, incomingRecordCounts, INCOMING_RECORD_COUNTS);
        if (outgoingRecordCounts != null)
            persistRecordCounts(repoName, outgoingRecordCounts, OUTGOING_RECORD_COUNTS);
    }

    protected void persistRecordCounts(String repoName, RecordCounts rc, String tableName) {
        LOG.info("\nRecordCounts for repo: " + repoName + rc.toString(repoName));
        Map<String, Map<String, AtomicInteger>> countsKeyedByType = rc.getCounts();

        for (String type : countsKeyedByType.keySet()) {
            int loops = 2;
            if (type.equals(RecordCounts.TOTALS)) {
                loops = 2;
            }
            // This loop is for the purpose of getting totals by harvest AND totals for all harvests
            for (int i = 0; i < loops; i++) {
                Date d1 = rc.getHarvestStartDate();
                if (i == 1) {
                    d1 = RecordCounts.TOTALS_DATE;
                }
                this.jdbcTemplate.update(
                        "insert ignore into " + getTableName(repoName, tableName) +
                                "  (harvest_start_date, type_name) values (?, ?);",
                        d1,
                        type);

                Map<String, Object> paramMap = new HashMap<String, Object>();

                StringBuilder sb = new StringBuilder();
                sb.append(
                        "update " + getTableName(repoName, tableName) + " set ");

                boolean updateRequired = false;
                Map<String, AtomicInteger> counts4Type = countsKeyedByType.get(type);
                for (String updateType : counts4Type.keySet()) {
                    updateRequired = true;
                    sb.append(updateType);
                    sb.append(" = ");
                    sb.append(updateType);
                    sb.append(" + :");
                    sb.append(updateType);
                    sb.append(",");
                    paramMap.put(updateType, counts4Type.get(updateType).get());
                }

                if (updateRequired) {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(" where harvest_start_date = :harvest_start_date and type_name = :type_name ");
                    paramMap.put("harvest_start_date", d1);
                    paramMap.put("type_name", type);

                    LOG.debug("paramMap!!!");
                    for (Map.Entry<String, Object> me : paramMap.entrySet()) {
                        LOG.debug(me.getKey() + ": " + me.getValue());
                    }
                    this.namedParameterJdbcTemplate.update(sb.toString(), paramMap);
                }
            }
        }
    }

}
