/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.transformation.bo;

/**
 * Represents mapping between bibliographic record and its manifestation record
 * 
 * @author Sharmila Ranganathan
 *
 */
public class BibliographicManifestationMapping {
	
	/** Id of bibliographic manifestation mapping */
	private int id = -1;
	
	/** OAI Id of bibliographic record */
	private String bibliographicRecordOAIId;
	
	/** OAI Id of manifestation record */
	private String manifestationRecordOAIId;
	
	/** 001 field of bibliographic record */
	private String bibliographicRecord001Field;
	
	/**
	 * Constructor 
	 * 
	 * @param bibliographicRecordOAIId OAI Id of bibliographic record
	 * @param manifestationRecordOAIId OAI Id of manifestation record
	 * @param bibliographicRecord001Field 001 field of bibliographic record 
	 */
	public BibliographicManifestationMapping(String bibliographicRecordOAIId, String manifestationRecordOAIId, String bibliographicRecord001Field) {
		
		this.bibliographicRecordOAIId =  bibliographicRecordOAIId;
		this.manifestationRecordOAIId = manifestationRecordOAIId;
		this.bibliographicRecord001Field = bibliographicRecord001Field;
		
	}
	
	/**
	 * Default constructor
	 */
	public BibliographicManifestationMapping() {}

	/**
	 * Get OAI Id of bibliographic record
	 * 
	 * @return OAI Id of bibliographic record
	 */
	public String getBibliographicRecordOAIId() {
		return bibliographicRecordOAIId;
	}

	/**
	 * Set OAI Id of bibliographic record
	 * 
	 * @param bibliographicRecordOAIId OAI Id of bibliographic record
	 */
	public void setBibliographicRecordOAIId(String bibliographicRecordOAIId) {
		this.bibliographicRecordOAIId = bibliographicRecordOAIId;
	}

	/**
	 * Get 001 field of bibliographic record 
	 * 
	 * @return 001 field of bibliographic record 
	 */
	public String getBibliographicRecord001Field() {
		return bibliographicRecord001Field;
	}

	/**
	 * Set 001 field of bibliographic record 
	 * 
	 * @param bibliographicRecord001Field 001 field of bibliographic record 
	 */
	public void setBibliographicRecord001Field(String bibliographicRecord001Field) {
		this.bibliographicRecord001Field = bibliographicRecord001Field;
	}

	/**
	 * Get OAI Id of manifestation record
	 * 
	 * @return OAI Id of manifestation record
	 */
	public String getManifestationRecordOAIId() {
		return manifestationRecordOAIId;
	}

	/**
	 * Set OAI Id of manifestation record
	 * 
	 * @param manifestationRecordOAIId OAI Id of manifestation record
	 */
	public void setManifestationRecordOAIId(String manifestationRecordOAIId) {
		this.manifestationRecordOAIId = manifestationRecordOAIId;
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
	
	
}
