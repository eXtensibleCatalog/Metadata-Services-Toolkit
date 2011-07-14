/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.aggregation.bo;

/**
 * Represents the identifiers in a record that are used to match with other record
 * 
 * @author Sharmila Ranganathan
 */
public class MatchIdentifiers {
	
	/* Identifier */
	private int id = -1;
	
	/* OAI identifier */
	private String oaiId;
	
	/* OCoLC value */
	private String oclcValue;
	
	/* LCCN value */
	private String lccnValue;
	
	/* ISBN value */
	private String isbnValue;
	
	/* ISSN value */
	private String issnValue;
	
	/** Default constructor */
	public MatchIdentifiers(){}
	
	/**
	 * Constructor
	 * 
	 * @param oaiId OAI identifier
	 */
	public MatchIdentifiers(String oaiId) {
		this.oaiId = oaiId;
	}

	/**
	 * Get ISSN value
	 * 
	 * @return
	 */
	public String getIssnValue() {
		return issnValue;
	}

	/**
	 * Set ISSN value 
     *	
	 * @param issnValue ISSN value in the record
	 */
	public void setIssnValue(String issnValue) {
		this.issnValue = issnValue;
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
	 * Get OCoLC value
	 * 
	 * @return
	 */
	public String getOclcValue() {
		return oclcValue;
	}

	/**
	 * Set OCoLC value 
	 * 
	 * @param oclcValue OCoLC value in the record
	 */
	public void setOclcValue(String oclcValue) {
		this.oclcValue = oclcValue;
	}

	/**
	 * Get LCCN value
	 * 
	 * @return
	 */
	public String getLccnValue() {
		return lccnValue;
	}

	/**
	 * Set LCCN value
	 * 
	 * @param lccnValue LCCN value in the record
	 */
	public void setLccnValue(String lccnValue) {
		this.lccnValue = lccnValue;
	}

	/**
	 * Get ISBN value
	 * 
	 * @return 
	 */
	public String getIsbnValue() {
		return isbnValue;
	}

	/**
	 * Set ISBN value
	 * 
	 * @param isbnValue ISBN value in the record 
	 */
	public void setIsbnValue(String isbnValue) {
		this.isbnValue = isbnValue;
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
