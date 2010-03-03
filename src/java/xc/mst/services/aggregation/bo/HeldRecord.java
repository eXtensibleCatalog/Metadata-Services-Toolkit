/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.aggregation.bo;

import java.util.List;

/**
 * Represents the held record
 * 
 * @author Sharmila Ranganathan
 *
 */
public class HeldRecord {
	
	/** Id */
	private int id = -1;
	
	/** OAI id of thr record that is held */
	private String oaiId;
	
	/** OAI id of held record's parent */
	private List<String> parentOaiIds;

	/**
	 * Public Constructor
	 */
	public HeldRecord() {}
	
	public HeldRecord(String oaiId, List<String> parentOaiIds) {
		this.oaiId = oaiId;
		this.parentOaiIds = parentOaiIds;
	}

	public String getOaiId() {
		return oaiId;
	}

	public void setOaiId(String oaiId) {
		this.oaiId = oaiId;
	}

	public List<String> getParentOaiIds() {
		return parentOaiIds;
	}

	public void setParentOaiIds(List<String> parentOaiIds) {
		this.parentOaiIds = parentOaiIds;
	}
	
	public void addParentOaiId(String parentOaiId) {
		this.parentOaiIds.add(parentOaiId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
