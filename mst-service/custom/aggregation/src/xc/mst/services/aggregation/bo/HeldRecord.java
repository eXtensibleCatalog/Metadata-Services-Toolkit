/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
	
	/** OAI id of the record that is held */
	private String oaiId;
	
	/** OAI id of held record's parent */
	private List<String> parentOaiIds;

	/**
	 * Public Constructor
	 */
	public HeldRecord() {}
	
	/**
	 * Constructor 
	 * 
	 * @param oaiId OAI Identifier of held record
	 * @param parentOaiIds parent OAI identifier of held record
	 */
	public HeldRecord(String oaiId, List<String> parentOaiIds) {
		this.oaiId = oaiId;
		this.parentOaiIds = parentOaiIds;
	}

	/**
	 * Get OAI identifier
	 * 
	 * @return
	 */
	public String getOaiId() {
		return oaiId;
	}

	/**
	 * Set OAI identifier
	 * 
	 * @param oaiId OAI identifier
	 */
	public void setOaiId(String oaiId) {
		this.oaiId = oaiId;
	}

	/**
	 * Get parent OAI identifier
	 * 
	 * @return
	 */
	public List<String> getParentOaiIds() {
		return parentOaiIds;
	}

	/**
	 * Set parent OAI identifier
	 * 
	 * @param parentOaiIds parent OAI identifier
	 */
	public void setParentOaiIds(List<String> parentOaiIds) {
		this.parentOaiIds = parentOaiIds;
	}
	
	/**
	 * Add parent OAI identifier
	 * 
	 * @param parentOaiId parent OAI identifier
	 */
	public void addParentOaiId(String parentOaiId) {
		this.parentOaiIds.add(parentOaiId);
	}

	/**
	 * Get Id
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set id
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
}
