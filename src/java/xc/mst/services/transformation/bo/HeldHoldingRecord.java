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

}
