/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.bo.record;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xc.mst.utils.Util;

/**
 * Requirements this tries to adhere to can be found here:
 * http://code.google.com/p/xcmetadataservicestoolkit/wiki/LoggingRecordCounts
 *
 * I found it so vital to entangling what is going on here that I am going to include part of it here as well.
 *
 * Dave in email on 2011-03-31

    I guess I have an initial comment to start:

    We are counting active, updates, and deletes, but I believe there are at least four categories
    - perhaps we should count the following kinds of arriving records
    (I am assuming that active is defined as a record that is not marked-deleted):

    1) new record (active)

    2) new record (marked-deleted)

    3) updated record (active)

    4) updated record (marked-deleted)

    We could also count some additional categories that would also look at what the updated records are replacing (two of the above cases) :

    3a) updated (active replacing an existing active)

    3b) updated (active replacing an existing marked-deleted)

    4a) updated (marked-deleted replacing an existing active)

    4b) updated (marked-deleted replacing an existing marked-deleted)

 *
 * @author Benjamin Anderson
 *
 */
public class RecordCounts {

    private static final Logger LOG = Logger.getLogger(RecordCounts.class);

    public static final Date TOTALS_DATE = new Date(0);
    public static final String TOTALS = "TOTALS";
    public static final String OTHER = "unknown";

    public static String INCOMING = "incoming";
    public static String OUTGOING = "outgoing";

    public static String NEW_ACTIVE = "new_act_cnt";
    public static String NEW_HELD = "new_held_cnt";
    public static String NEW_DELETE = "new_del_cnt";
    public static String UPDATE_ACTIVE = "upd_act_cnt";
    public static String UPDATE_HELD = "upd_held_cnt";
    public static String UPDATE_DELETE = "upd_del_cnt";

    public static String UNEXPECTED_ERROR = "unexpected_error_cnt";

    public static Set<String> INCOMING_STATUS_COLUMN_NAMES = null;
    public static Map<String, String> UPD_PREV_COLUMN_NAMES = null;

    static {
        UPD_PREV_COLUMN_NAMES = new LinkedHashMap<String, String>();

        UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE + "" + Record.ACTIVE, "upd_act_prev_act_cnt");
        UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE + "" + Record.HELD, "upd_act_prev_held_cnt");
        UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE + "" + Record.DELETED, "upd_act_prev_del_cnt");

        UPD_PREV_COLUMN_NAMES.put(Record.HELD + "" + Record.ACTIVE, "upd_held_prev_act_cnt");
        UPD_PREV_COLUMN_NAMES.put(Record.HELD + "" + Record.HELD, "upd_held_prev_held_cnt");
        UPD_PREV_COLUMN_NAMES.put(Record.HELD + "" + Record.DELETED, "upd_held_prev_del_cnt");

        UPD_PREV_COLUMN_NAMES.put(Record.DELETED + "" + Record.ACTIVE, "upd_del_prev_act_cnt");
        UPD_PREV_COLUMN_NAMES.put(Record.DELETED + "" + Record.HELD, "upd_del_prev_held_cnt");
        UPD_PREV_COLUMN_NAMES.put(Record.DELETED + "" + Record.DELETED, "upd_del_prev_del_cnt");

        INCOMING_STATUS_COLUMN_NAMES = new LinkedHashSet<String>();

        INCOMING_STATUS_COLUMN_NAMES.add(NEW_ACTIVE);
        INCOMING_STATUS_COLUMN_NAMES.add(NEW_HELD);
        INCOMING_STATUS_COLUMN_NAMES.add(NEW_DELETE);
        INCOMING_STATUS_COLUMN_NAMES.add(UPDATE_ACTIVE);
        INCOMING_STATUS_COLUMN_NAMES.add(UPDATE_HELD);
        INCOMING_STATUS_COLUMN_NAMES.add(UPDATE_DELETE);

    }

    protected Map<String, Map<String, AtomicInteger>> counts = null;
    protected Date harvestStartDate = null;
    protected String incomingOutgoing = null;

    public RecordCounts(Date harvestStartDate, String incomingOutgoing) {
        this.counts = new HashMap<String, Map<String, AtomicInteger>>();
        this.harvestStartDate = harvestStartDate;
        this.incomingOutgoing = incomingOutgoing;
        getCountsByType(RecordCounts.TOTALS);
    }

    public String getIncomingOutgoing() {
        return incomingOutgoing;
    }

    public void setIncomingOutgoing(String incomingOutgoing) {
        this.incomingOutgoing = incomingOutgoing;
    }

    protected Map<String, AtomicInteger> getCountsByType(String type) {
        if (type == null) {
            type = TOTALS;
        }
        Map<String, AtomicInteger> counts4type = counts.get(type);
        if (counts4type == null) {
            counts4type = new HashMap<String, AtomicInteger>();
            counts.put(type, counts4type);
        }
        return counts4type;
    }

    protected AtomicInteger getCount(Map<String, AtomicInteger> counts4type, String col_1) {
        if (UPD_PREV_COLUMN_NAMES.containsKey(col_1)) {
            col_1 = UPD_PREV_COLUMN_NAMES.get(col_1);
        }
        AtomicInteger ai = counts4type.get(col_1);
        if (ai == null) {
            ai = new AtomicInteger(0);
            counts4type.put(col_1, ai);
        }
        return ai;
    }

    /**
     * generally do not externally want to call this one.
     * currently it is only externally called in the case of an error.
     * @param type
     * @param col_1
     */
    public void incr(String type, String col_1) {
        if (type == null) {
            type = TOTALS;
        }
        if (col_1 == null) {
            throw new RuntimeException("bogus");
        }
        getCount(getCountsByType(type), col_1).addAndGet(1);
    }

    /**
     * You probably want to call this one.
     *
     * A collection of things I have learned.
     *
     * This method generally ends up incrementing two counters.
     *
     * i.e. lets take the example:
     * We received 144 generic updated active records.
     * These break down further to all 144 being updated active records replacing active records.
     * So 2 counters get updated, upd_act_cnt += 144 and upd_act_prev_act_cnt += 144.
     *
     * They are both incremented as a result of a updated active record being received that replaces one that was previously active.
     * It could have been an updated active record being received that replaces one that was previously held,
     * or an updated active record received that replaces one that was previously deleted.
     *
     * Another example:
     * upd_act_cnt:        6,755
     * upd_act_prev_act_cnt:        4,212
     * upd_act_prev_held_cnt:        2,543
     * upd_act_prev_del_cnt:            0
     * For the above, we have 6,755 upd_act_cnt, and that breaks down to 4,212 of 1 type and 2,543 of another.
     * The below code does that.
     *
     *
     * RecordIfc tells through comments that get/setType are key for RecordCount reporting.
     * So if you want to see something besides 'Totals' and 'RecordCounts.OTHER'
     * then the service must set the Record's type.
     *
     * As explained in DCTransformationService:
     *          //
                // setting this here increments this type in the record counts when
                // incremented in GenericMetadataService.process() -- else it then
                // increments RecordCounts.OTHER
                //
     *
     * @see RecordIfc
     * @param type - set by the service, see comments above
     * @param newStatus - need to know the prevStatus and the newStatus
                          then with both of these make the decision on which counter(s) to increment
     * @param prevStatus - need to know the prevStatus and the newStatus
                          then with both of these make the decision on which counter(s) to increment
     */
    public void incr(String type, char newStatus, char prevStatus) {
        // LOG.debug("incr - type:"+type+" newStatus:"+newStatus+" prevStatus:"+prevStatus);
        if (type == null) {
            type = TOTALS;
        }
        String col_1 = null;
        if (prevStatus == 0 || prevStatus == Record.NULL) {
            if (newStatus == Record.ACTIVE) {
                col_1 = RecordCounts.NEW_ACTIVE;
            } else if (newStatus == Record.HELD) {
                col_1 = RecordCounts.NEW_HELD;
            } else if (newStatus == Record.DELETED) {
                col_1 = RecordCounts.NEW_DELETE;
            }
        } else {
            if (newStatus == Record.ACTIVE) {
                col_1 = RecordCounts.UPDATE_ACTIVE;
            } else if (newStatus == Record.HELD) {
                col_1 = RecordCounts.UPDATE_HELD;
            } else if (newStatus == Record.DELETED) {
                col_1 = RecordCounts.UPDATE_DELETE;
            }
        }
        try {
            incr(type, col_1);
        } catch (RuntimeException re) {
            LOG.error("type: " + type);
            LOG.error("newStatus: " + newStatus);
            LOG.error("prevStatus: " + prevStatus);
            throw re;
        }
        // the other incr:?
        if (prevStatus != 0 && prevStatus != Record.NULL) {
            getCount(getCountsByType(type), newStatus + "" + prevStatus).addAndGet(1);
        }
    }

    public Map<String, Map<String, AtomicInteger>> getCounts() {
        return counts;
    }

    public int getCount(String type, String col) {
        Map<String, AtomicInteger> counts4type = counts.get(type);
        if (counts4type != null) {
            AtomicInteger ai = counts4type.get(col);
            if (ai != null) {
                return ai.get();
            }
        }
        return 0;
    }

    public int getCount(String type, char status, char prevStatus) {
        Map<String, AtomicInteger> counts4type = counts.get(type);
        if (counts4type != null) {
            AtomicInteger ai = counts4type.get(UPD_PREV_COLUMN_NAMES.get(status + "" + prevStatus));
            if (ai != null) {
                return ai.get();
            }
        }
        return 0;
    }

    public Date getHarvestStartDate() {
        return this.harvestStartDate;
    }

    public void clear() {
        this.counts = new HashMap<String, Map<String, AtomicInteger>>();
    }

    public String toString(String repoName) {
        StringBuilder sb = new StringBuilder();

        // This is for the purpose of making the totals appear last
        List<String> keys = new ArrayList<String>();
        for (String type : counts.keySet()) {
            if (!type.equals(TOTALS) && !type.equals(OTHER)) {
                keys.add(type);
            }
        }
        keys.add(OTHER);
        keys.add(TOTALS);
        for (String type : keys) {
            Map<String, AtomicInteger> counts4Type = counts.get(type);
            if (counts4Type == null)
                continue;

            type = StringUtils.rightPad(type, 25);
            int col = 0;
            List<String> colNames = new ArrayList<String>();
            colNames.addAll(INCOMING_STATUS_COLUMN_NAMES);
            if (RecordCounts.OUTGOING.equals(this.incomingOutgoing)) {
                colNames.addAll(UPD_PREV_COLUMN_NAMES.values());
            } else {
                colNames.add(UNEXPECTED_ERROR);
            }
            String date = "all time            ";
            LOG.debug("TOTALS_DATE.getTime(): " + TOTALS_DATE.getTime());
            LOG.debug("this.harvestStartDate.getTime(): " + this.harvestStartDate.getTime());
            if (this.harvestStartDate.getTime() != TOTALS_DATE.getTime()) {
                LOG.debug("new Util().printDateTime(this.harvestStartDate): " + new Util().printDateTime(this.harvestStartDate));
                LOG.debug("new Util().printDateTime(TOTALS_DATE): " + new Util().printDateTime(TOTALS_DATE));

                date = new Util().printDateTime(this.harvestStartDate);
            }
            for (String updateType : colNames) {
                if (col == 0)
                    sb.append("\n" + incomingOutgoing + " " + date + " " + type + " ");
                long num = 0;
                if (counts4Type.get(updateType) != null) {
                    num = counts4Type.get(updateType).get();
                }
                DecimalFormat myFormatter = new DecimalFormat("###,###,###");
                String line = StringUtils.leftPad(updateType, 25) + ": " + StringUtils.leftPad(myFormatter.format(num), 12) + "  ";
                sb.append(line);

                if (++col == 3) {
                    col = 0;
                }
            }
        }

        return sb.toString();
    }

}
