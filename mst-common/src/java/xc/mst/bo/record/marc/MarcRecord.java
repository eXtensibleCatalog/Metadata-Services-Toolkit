package xc.mst.bo.record.marc;

import gnu.trove.TIntObjectHashMap;

public class MarcRecord {

	protected String leader = null;
	
	/**
	 * The structure is as follows
	 *  tag    
	 *    1 -> 
	 */
	protected TIntObjectHashMap controlFields = new TIntObjectHashMap();
	protected TIntObjectHashMap dataFields = new TIntObjectHashMap();
	
	public String getLeader() {
		return leader;
	}
	public void setLeader(String leader) {
		this.leader = leader;
	}
	public TIntObjectHashMap getControlFields() {
		return controlFields;
	}
	public void setControlFields(TIntObjectHashMap controlFields) {
		this.controlFields = controlFields;
	}
	public TIntObjectHashMap getDataFields() {
		return dataFields;
	}
	public void setDataFields(TIntObjectHashMap dataFields) {
		this.dataFields = dataFields;
	}
}
