package xc.mst.bo.record;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RecordCounts {
	
	public static String INCOMING = "incoming";
	public static String OUTGOING = "outgoing";
	
	public static String NEW_ACTIVE = "new_act_cnt";
	public static String NEW_HELD = "new_act_cnt";
	public static String NEW_DELETE = "new_del_cnt";
	public static String UPDATE_ACTIVE = "upd_act_cnt";
	public static String UPDATE_HELD = "upd_held_cnt";
	public static String UPDATE_DELETE = "upd_del_cnt";
	
	private static Map<String, String> UPD_PREV_COLUMN_NAMES = null;
	
	static {
		UPD_PREV_COLUMN_NAMES = new HashMap<String, String>();
		
		UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE+""+Record.ACTIVE, "upd_act_prev_act_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE+""+Record.HELD, "upd_act_prev_held_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.ACTIVE+""+Record.DELETED, "upd_act_prev_del_cnt");
		
		UPD_PREV_COLUMN_NAMES.put(Record.HELD+""+Record.ACTIVE, "upd_held_prev_act_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.HELD+""+Record.HELD, "upd_held_prev_held_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.HELD+""+Record.DELETED, "upd_held_prev_del_cnt");
		
		UPD_PREV_COLUMN_NAMES.put(Record.DELETED+""+Record.ACTIVE, "upd_del_prev_act_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.DELETED+""+Record.HELD, "upd_del_prev_held_cnt");
		UPD_PREV_COLUMN_NAMES.put(Record.DELETED+""+Record.DELETED, "upd_del_prev_del_cnt");
	}
	
	protected Map<String, Map<String, AtomicInteger>> counts = null;
	protected Date harvestStartDate = null;
	protected String incomingOutgoing = null;
	
	public RecordCounts(Date harvestStartDate, String incomingOutgoing) {
		this.counts = new HashMap<String, Map<String, AtomicInteger>>();
		this.harvestStartDate = harvestStartDate;
		this.incomingOutgoing = incomingOutgoing;
	}
	
	protected Map<String, AtomicInteger> getCountsByType(String type) {
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
	
	public void incr(String type, char newStatus, char prevStatus) {
		String col_1 = null;
		if (prevStatus == 0) {
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
		getCount(getCountsByType(type), col_1).addAndGet(1);
		if (prevStatus != 0) {
			getCount(getCountsByType(type), newStatus+""+prevStatus).addAndGet(1);	
		}
	}
	
	public Map<String, Map<String, AtomicInteger>> getCounts() {
		return counts;
	}
	
	public Date getHarvestStartDate() {
		return this.harvestStartDate;
	}
	
	public void clear() {
		this.counts = new HashMap<String, Map<String, AtomicInteger>>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(incomingOutgoing);
		sb.append(" ");
		for (String type : counts.keySet()) {
			Map<String, AtomicInteger> counts4Type = counts.get(type);
			for (String updateType : counts4Type.keySet()) {
				sb.append(updateType+": "+counts4Type.get(updateType));
			}
		}	
		
		return sb.toString();
	}
	
}
