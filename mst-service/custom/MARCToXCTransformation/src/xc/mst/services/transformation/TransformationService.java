/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
 
package xc.mst.services.transformation;

import gnu.trove.TLongLongHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom.Element;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.services.transformation.bo.AggregateXCRecord;
import xc.mst.services.transformation.bo.MarcXmlRecord;
import xc.mst.services.transformation.service.XCRecordService;
import xc.mst.utils.XmlHelper;

/**
 * A Metadata Service which for each unprocessed marcxml record creates an XC schema
 * record from the data in the unprocessed record.
 *
 * @author Eric Osisek
 * @author Benjamin D. Anderson
 */
public class TransformationService extends SolrTransformationService {

	private final static Logger LOG = Logger.getLogger(TransformationService.class);

	protected XmlHelper xmlHelper = new XmlHelper();
	
	protected XCRecordService XCRecordService = null;
	
	public XCRecordService getXCRecordService() {
		return XCRecordService;
	}

	public void setXCRecordService(XCRecordService xCRecordService) {
		XCRecordService = xCRecordService;
	}

	/**
	 * A map containing values for $4 subfields which we should treat as
	 * roles and the rdarole they represent.
	 */
	protected HashMap<String, String> roles = new HashMap<String, String>();
	
	/**
	 * This is used to ensure that subfields from the same source get mapped to the same FRBR Work element
	 */
	protected int artificialLinkingId = 0;

	// The following HashSets are used to prevent duplicate values from being added to the XC record

	protected HashMap<String, Element> linkedCreatorFields = new HashMap<String, Element>();
	
	/**
	 * Org code used 
	 */
	protected String orgCode = "";
	
	/**
	 * Construct a TransformationService Object
	 */
	public void init()
	{
		// Initialize the list of roles
		roles.put("aut", "author");
		roles.put("lbt", "author");
		roles.put("lyr", "author");
		roles.put("cmp", "composer");
		roles.put("com", "compiler");
		roles.put("art", "artist");
		roles.put("ths", "thesisAdvisor");
		roles.put("drt", "director");
		roles.put("edt", "editor");
		roles.put("ill", "illustrator");
		roles.put("prf", "performer");
		roles.put("act", "performer");
		roles.put("dnc", "performer");
		roles.put("nrt", "performer");
		roles.put("voc", "performer");
		roles.put("itr", "performer");
		roles.put("cnd", "performer");
		roles.put("mod", "performer");
		roles.put("pro", "producer");
		roles.put("trl", "translator");
	}
	
	@Override
	protected void endBatch() {
		try {
			//TODO - create links
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
		super.endBatch();
	}
	
	//TODO - these datastructures need to be read in and they need to be persisted.
	// which begs the question about the lack of transactions... I need a way to 
	// rollback if something bad happens.  Probably the easiest thing to do is just to delete
	// records with some id higher than something.
	protected TLongLongHashMap bibsProcessedLongId = new TLongLongHashMap();
	protected Map<String, Long> bibsProcessedStringId = new HashMap<String, Long>();
	
	protected TLongLongHashMap bibsYet2ArriveLongId = new TLongLongHashMap();
	protected Map<String, Long> bibsYet2ArriveStringId = new HashMap<String, Long>();
	
	protected Long getLongFromMap(TLongLongHashMap longLongMap, Map<String, Long> stringLongMap, String s) {
		try {
			Long bibMarcId = Long.parseLong(s);
			long l = longLongMap.get(bibMarcId);
			if (l == 0) {
				return null;
			} else {
				return (Long)l;
			}
		} catch (NumberFormatException nfe) {
			return stringLongMap.get(s);
		}
	}
	
	protected void add2Map(TLongLongHashMap longLongMap, Map<String, Long> stringLongMap, String s, long lv) {
		try {
			Long bibMarcId = Long.parseLong(s);
			longLongMap.put(bibMarcId, lv);
		} catch (NumberFormatException nfe) {
			stringLongMap.put(s, lv);
		}
	}
	
	protected void removeFromMap(TLongLongHashMap longLongMap, Map<String, Long> stringLongMap, String s) {
		try {
			Long bibMarcId = Long.parseLong(s);
			longLongMap.remove(bibMarcId);
		} catch (NumberFormatException nfe) {
			stringLongMap.remove(s);
		}
	}
	
	protected Long getManifestationId4BibProcessed(String s) {
		return getLongFromMap(bibsProcessedLongId, bibsProcessedStringId, s);
	}
	protected void addManifestationId4BibProcessed(String s, Long l) {
		add2Map(bibsProcessedLongId, bibsProcessedStringId, s, l);
	}
	protected void removeManifestationId4BibProcessed(String s) {
		removeFromMap(bibsProcessedLongId, bibsProcessedStringId, s);
	}
	protected Long getManifestationId4BibYet2Arrive(String s) {
		return getLongFromMap(bibsYet2ArriveLongId, bibsYet2ArriveStringId, s);
	}
	protected void addManifestationId4BibYet2Arrive(String s, Long l) {
		add2Map(bibsYet2ArriveLongId, bibsYet2ArriveStringId, s, l);
	}
	protected void removeManifestationId4BibYet2Arrive(String s) {
		removeFromMap(bibsYet2ArriveLongId, bibsYet2ArriveStringId, s);
	}
	
	@Override
	public List<OutputRecord> process(InputRecord record) {
		
		List<OutputRecord> results = new ArrayList<OutputRecord>();
		
		boolean isBib = false;
		boolean isHolding = false;


		// This will populate the ar record with W(*) E(*) M(?) H(*)
		// It will also be populate the previously*Ids
		// All of the logic for transforming records is done in this method.
		AggregateXCRecord ar = getXCRecordService.parse(record);
		
		// This logic should be moved into the above parse method.
		record.setMode(Record.JDOM_MODE);
		Element marcXml = record.getOaiXmlEl();

		// Create a MarcXmlRecord for the record
		MarcXmlRecord originalRecord = new MarcXmlRecord(marcXml);
		char leader06 = originalRecord.getLeader().charAt(6);
		if("abcdefghijkmnoprt".contains(""+leader06)) {
			isBib = true;
		} else if(leader06 == 'u' || leader06 == 'v' || leader06 == 'x' || leader06 == 'y') {
			isHolding = true;
		} else { // If leader 6th character is invalid, then log error and do not process that record.
			logError("Record Id " + record.getId() + " with leader character " + leader06 + " not processed.");
			return results;
		}
		
		if (record.getSuccessors() != null && record.getSuccessors().size() > 0) {
			// if delete
			// else update
		} else {
			// if delete - do nothing
			if (isBib) {
				Long bibOutputId = getManifestationId4BibYet2Arrive(originalRecord.getControlField("001"));
				if (bibOutputId != null) {
					//output xc-manifestation with set aside bib_record_id
					//move bibsYetToArrive entry to bibsProcessed
					//mark held holdings as active
				} else {
					//output xc-manifestation with new record_id
					//create new entry in bibsProcessed
				}
			} else if (isHolding) {
				// BDA - can't there be more than one manifestionId per holding?
				//       This is the way it was, so I'm not going to change it now, but at some point
				//       this should be addressed.
				Long manifestationId = getManifestationId4BibProcessed(originalRecord.getControlField("004"));
				if (manifestationId != null) {
					//no map manipulation
					//output xc-holding with status A
				} else {
					manifestationId = getManifestationId4BibYet2Arrive(originalRecord.getControlField("004"));
					if (manifestationId != null) {
						//use the record_id for linking
						//output xc-holding as held
					} else {
						//generate a new oai-id for the bibYetToArrive
						//add an entry to bibsYetToArrive
					}
					//output xc-holding with status H
				}
			}
		}
		//update service accordingly w/ new record counts
		return results;
	}
	
	/*
	 * Process holding record 
	 */
	protected List<Record> processHoldingRecord(
			Record record, MarcXmlRecord originalRecord, long manifestationId) 
				throws  DatabaseConfigException, TransformerConfigurationException, 
					IndexException, TransformerException, DataException {

		// Create an XCRecord Object to hold the transformed record
		AggregateXCRecord transformedRecord = new AggregateXCRecord();
		
		// Run the transformation steps
		// Each one processes a different MARC XML field and adds the appropriate
		// XC fields to transformedRecord based on the field it processes.
		transformedRecord = holdingsProcess506(originalRecord, transformedRecord);
		transformedRecord = holdingsProcess852(originalRecord, transformedRecord);
		transformedRecord = holdingsProcess856(originalRecord, transformedRecord);
		transformedRecord = process866(originalRecord, transformedRecord);
		transformedRecord = process867(originalRecord, transformedRecord);
		transformedRecord = process868(originalRecord, transformedRecord);
		transformedRecord = holdingsProcess001And003(originalRecord, transformedRecord);
		/* holdingsProcess843 is commented for now. This will be implemented later.
		transformedRecord = holdingsProcess843(originalRecord, transformedRecord);
		*/
		
		// Get the XC records created as output & Link Holding to manifestation
		List<Record> results = getXCRecordService().getSplitXCRecordXMLForHoldingRecord(
				transformedRecord, manifestationId);
		
		// If the input record is a new record then increment the processed record count
		if (true  && results.size() > 0) {
			inputRecordCount++;
		}
		
		return results;
	}
}
