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
 * Represents the identifiers that are used to match a record
 * 
 * @author Sharmila Ranganathan
 *
 */
public class MatchIdentifiers {
	
	private String oaiId;
	
	private String oclcValue;
	
	private String lccnValue;
	
	private String isbnValue;
	
	private String issnValue;
	
	public MatchIdentifiers(String oaiId) {
		this.oaiId = oaiId;
	}

	public String getIssnValue() {
		return issnValue;
	}

	public void setIssnValue(String issnValue) {
		this.issnValue = issnValue;
	}

	public String getOaiId() {
		return oaiId;
	}

	public void setOaiId(String oaiId) {
		this.oaiId = oaiId;
	}

	public String getOclcValue() {
		return oclcValue;
	}

	public void setOclcValue(String oclcValue) {
		this.oclcValue = oclcValue;
	}

	public String getLccnValue() {
		return lccnValue;
	}

	public void setLccnValue(String lccnValue) {
		this.lccnValue = lccnValue;
	}

	public String getIsbnValue() {
		return isbnValue;
	}

	public void setIsbnValue(String isbnValue) {
		this.isbnValue = isbnValue;
	}

}
