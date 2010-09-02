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
 * Represents MARCXML holding record that are held for processing
 * 
 * @author Sharmila Ranganathan
 *
 */
public class HeldHoldingRecord {
	
	/* Id */
	private int id = -1;
	
	/** OAI Identifier of held MARCXML holding record */
	private String holdingRecordOAIID;
	
	/** 004 field value of MARCXML holding record */
	private String holding004Field;

	/** Default constructor */
	public HeldHoldingRecord() {}
	
	/**
	 * Constructor
	 * 
	 * @param holdingRecordOAIID  OAI Identifier of held MARCXML holding record 
	 * @param holding004Field 004 field value of MARCXML holding record
	 */
	public HeldHoldingRecord(String holdingRecordOAIID, String holding004Field) {
		this.holdingRecordOAIID = holdingRecordOAIID;
		this.holding004Field = holding004Field;		
	}
	
	/**
	 * Set OAI Identifier of held MARCXML holding record 
	 * 
	 * @param holdingRecordOAIID OAI Identifier of held MARCXML holding record 
	 */
	public void setHoldingRecordOAIID(String holdingRecordOAIID) {
		this.holdingRecordOAIID = holdingRecordOAIID;
	}

	/**
	 * Get 004 field value of MARCXML holding record
	 * 
	 * @return 004 field value of MARCXML holding record
	 */
	public String getHolding004Field() {
		return holding004Field;
	}

	/**
	 * Set 004 field value of MARCXML holding record
	 * 
	 * @param holding004Field 004 field value of MARCXML holding record
	 */
	public void setHolding004Field(String holding004Field) {
		this.holding004Field = holding004Field;
	}
	
	/**
	 * Get OAI Identifier of held MARCXML holding record 
	 * 
	 * @return OAI Identifier of held MARCXML holding record 
	 */
	public String getHoldingRecordOAIID() {
		return holdingRecordOAIID;
	}

	/** Get id
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
        if (!(o instanceof XCHoldingRecord)) return false;

		final XCHoldingRecord other = (XCHoldingRecord)o;

        if( ( holdingRecordOAIID!= null && !holdingRecordOAIID.equals(other.getHoldingRecordOAIID()) ) ||
                ( holdingRecordOAIID == null && other.getHoldingRecordOAIID() != null ) ) return false;
        
         return true;

	} // end method equals(Object)
	
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
            int value = 0;
            value += holdingRecordOAIID == null ? 0 : holdingRecordOAIID.hashCode();
            return value;
    }
}
