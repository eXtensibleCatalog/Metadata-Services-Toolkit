/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.aggregation.bo;

/**
 * Represents the held record
 * 
 * @author Sharmila Ranganathan
 *
 */
public class HeldRecord {
	
	/** OAI id of thr record that is held */
	private String oaiId;
	
	/** OAI id of held record's parent */
	private String parentOaiId;

	public HeldRecord(String oaiId, String parentOaiId) {
		this.oaiId = oaiId;
		this.parentOaiId = parentOaiId;
	}

	public String getOaiId() {
		return oaiId;
	}

	public void setOaiId(String oaiId) {
		this.oaiId = oaiId;
	}

	public String getParentOaiId() {
		return parentOaiId;
	}

	public void setParentOaiId(String parentOaiId) {
		this.parentOaiId = parentOaiId;
	}
	
	
}
