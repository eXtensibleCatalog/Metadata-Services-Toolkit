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
 * Represents the output record created by aggregation
 * 
 * @author Sharmila Ranganathan
 *
 */
public class OutputRecord {
	
	private String oaiId;
	
	private String xml;
	
	private boolean updated = false;
	
	private String predecessorOaiId;

	public OutputRecord(String oaiId, String xml, boolean updated,
			String predecessorOaiId) {

		this.oaiId = oaiId;
		this.xml = xml;
		this.updated = updated;
		this.predecessorOaiId = predecessorOaiId;
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

	public String getPredecessorOaiId() {
		return predecessorOaiId;
	}

	public void setPredecessorOaiId(String predecessorOaiId) {
		this.predecessorOaiId = predecessorOaiId;
	}

}
