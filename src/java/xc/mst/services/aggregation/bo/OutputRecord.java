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
 * Represents the output record created by aggregation
 * 
 * @author Sharmila Ranganathan
 *
 */
public class OutputRecord {
	
	private int id = -1;
	
	private String oaiId;
	
	private String xml;
	
	private boolean updated = false;
	
	private List<String> predecessorOaiIds;
	
	public OutputRecord() {}

	public OutputRecord(String oaiId, String xml, boolean updated,
			List<String> predecessorOaiIds) {

		this.oaiId = oaiId;
		this.xml = xml;
		this.updated = updated;
		this.predecessorOaiIds = predecessorOaiIds;
	}

	public String getOaiId() {
		return oaiId;
	}

	public void setOaiId(String oaiId) {
		this.oaiId = oaiId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public List<String> getPredecessorOaiIds() {
		return predecessorOaiIds;
	}

	public void setPredecessorOaiId(List<String> predecessorOaiIds) {
		this.predecessorOaiIds = predecessorOaiIds;
	}
	
	public void addPredecessor(String oaiId) {
		predecessorOaiIds.add(oaiId);
	}
	
	public void removePredecessor(String oaiId) {
		predecessorOaiIds.remove(oaiId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
