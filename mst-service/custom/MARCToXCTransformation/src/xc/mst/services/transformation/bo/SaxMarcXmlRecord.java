package xc.mst.services.transformation.bo;

import org.apache.log4j.Logger;

import xc.mst.bo.record.marc.MarcRecord;


public class SaxMarcXmlRecord {
	
	private static final Logger LOG = Logger.getLogger(SaxMarcXmlRecord.class);
	
	protected MarcRecord marcRecord = new MarcRecord();
	
	public String getLeader() { 
		return marcRecord.getLeader(); 
	}
	
	public SaxMarcXmlRecord(String marcXml) {
		
	}
	
}