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

public class RecordCounts {

	private static final Logger LOG = Logger.getLogger(RecordCounts.class);

	public static final Date TOTALS_DATE = new Date(0);
	public static final String TOTALS = "TOTALS";

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

		UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE+""+Record.ACTIVE, "upd_act_prev_act_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE+""+Record.HELD, "upd_act_prev_held_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE+""+Record.DELETED, "upd_act_prev_del_cnt");

		UPD_PREV_COLUMN_NAMES.put(Record.HELD+""+Record.ACTIVE, "upd_held_prev_act_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.HELD+""+Record.HELD, "upd_held_prev_held_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.HELD+""+Record.DELETED, "upd_held_prev_del_cnt");

		UPD_PREV_COLUMN_NAMES.put(Record.DELETED+""+Record.ACTIVE, "upd_del_prev_act_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.DELETED+""+Record.HELD, "upd_del_prev_held_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.DELETED+""+Record.DELETED, "upd_del_prev_del_cnt");

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

	public void incr(String type, String col_1) {
		if (type == null) {
			type = TOTALS;
		}
		if (col_1 == null) {
			throw new RuntimeException("bogus");
		}
		getCount(getCountsByType(type), col_1).addAndGet(1);
	}

	public void incr(String type, char newStatus, char prevStatus) {
		LOG.debug("incr - type:"+type+" newStatus:"+newStatus+" prevStatus:"+prevStatus);
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
			LOG.error("type: "+type);
			LOG.error("newStatus: "+newStatus);
			LOG.error("prevStatus: "+prevStatus);
			throw re;
		}
		if (prevStatus != 0 && prevStatus != Record.NULL) {
			getCount(getCountsByType(type), newStatus+""+prevStatus).addAndGet(1);
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
			AtomicInteger ai = counts4type.get(UPD_PREV_COLUMN_NAMES.get(status+""+prevStatus));
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
		for (String type : counts.keySet()) {
			Map<String, AtomicInteger> counts4Type = counts.get(type);

			type = StringUtils.rightPad(type, 25);
			int col=0;
			List<String> colNames = new ArrayList<String>();
			colNames.addAll(INCOMING_STATUS_COLUMN_NAMES);
			if (RecordCounts.OUTGOING.equals(this.incomingOutgoing)) {
				colNames.addAll(UPD_PREV_COLUMN_NAMES.values());
			} else {
				colNames.add(UNEXPECTED_ERROR);
			}
			String date = "all time            ";
			LOG.debug("TOTALS_DATE.getTime(): "+TOTALS_DATE.getTime());
			LOG.debug("this.harvestStartDate.getTime(): "+this.harvestStartDate.getTime());
			if (this.harvestStartDate.getTime() != TOTALS_DATE.getTime()) {
				LOG.debug("new Util().printDateTime(this.harvestStartDate): "+new Util().printDateTime(this.harvestStartDate));
				LOG.debug("new Util().printDateTime(TOTALS_DATE): "+new Util().printDateTime(TOTALS_DATE));

				date = new Util().printDateTime(this.harvestStartDate);
			}
			for (String updateType : colNames) {
				if (col == 0)
					sb.append("\n"+incomingOutgoing+" "+date+" "+type+" ");
				long num = 0;
				if (counts4Type.get(updateType) != null) {
					num = counts4Type.get(updateType).get();
				}
				DecimalFormat myFormatter = new DecimalFormat("###,###,###");
				String line = StringUtils.leftPad(updateType, 25)+": "+StringUtils.leftPad(myFormatter.format(num), 12)+"  ";
				sb.append(line);

				if (++col == 3) {
					col = 0;
				}
			}
		}

		return sb.toString();
	}

}
