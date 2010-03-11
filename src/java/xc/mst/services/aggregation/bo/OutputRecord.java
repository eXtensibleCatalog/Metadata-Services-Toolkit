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
	
	/* Id */
	private int id = -1;
	
	/* OAI identifier of the record */
	private String oaiId;
	
	/* Represents XML in the record */ 
	private String xml;
	
	/* Represents if the record is updated recently */
	private boolean updated = false;
	
	/* OAI Ids of Predecessor record */
	private List<String> predecessorOaiIds;
	
	/* Uplinks of record */
	private List<String> uplinks;
	
	/** 
	 * Default constructor
	 */
	public OutputRecord() {}

	/**
	 * Constructor
	 * 
	 * @param oaiId OAI identifier
	 * @param xml XML content of record
	 * @param updated True if XML is updated and different from the one in Solr
	 * @param predecessorOaiIds OAI Ids of Predecessor record
	 */
	public OutputRecord(String oaiId, String xml, boolean updated,
			List<String> predecessorOaiIds) {

		this.oaiId = oaiId;
		this.xml = xml;
		this.updated = updated;
		this.predecessorOaiIds = predecessorOaiIds;
	}

	/**
	 * Get Id
	 * 
	 * @return
	 */
	public String getOaiId() {
		return oaiId;
	}

	/** 
	 * Set Id
	 * 
	 * @param oaiId
	 */
	public void setOaiId(String oaiId) {
		this.oaiId = oaiId;
	}

	/**
	 * Get Xml of the record
	 * 
	 * @return
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * Set record XML
	 *  
	 * @param xml
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

	/**
	 * Returns true if record is updated, else returns false
	 * 
	 * @return
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * Sets true if record is updated, else sets false
	 * 
	 * @param updated
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	/**
	 * Get OAI Ids of Predecessor record
	 *  
	 * @return
	 */
	public List<String> getPredecessorOaiIds() {
		return predecessorOaiIds;
	}

	/**
	 * Set OAI Ids of Predecessor record
	 * 
	 * @param predecessorOaiIds
	 */
	public void setPredecessorOaiId(List<String> predecessorOaiIds) {
		this.predecessorOaiIds = predecessorOaiIds;
	}
	
	/**
	 * Add OAI Id of Predecessor record
	 * 
	 * @param oaiId OAI Id of Predecessor record
	 */
	public void addPredecessor(String oaiId) {
		predecessorOaiIds.add(oaiId);
	}

	/**
	 * Remove OAI Id of Predecessor record
	 * 
	 * @param oaiId
	 */
	public void removePredecessor(String oaiId) {
		predecessorOaiIds.remove(oaiId);
	}

	/**
	 * Get id
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

	public List<String> getUplinks() {
		return uplinks;
	}

	public void setUplinks(List<String> uplinks) {
		this.uplinks = uplinks;
	}

}
