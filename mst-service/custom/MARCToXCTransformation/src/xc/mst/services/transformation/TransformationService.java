/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
 
package xc.mst.services.transformation;

import gnu.trove.TLongArrayList;
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
	
	//TODO - these datastructures need to be read in and they need to be persisted.
	// which begs the question about the lack of transactions... I need a way to 
	// rollback if something bad happens.  Probably the easiest thing to do is just to delete
	// records with some id higher than something.
	protected TLongLongHashMap bibsProcessedLongId = new TLongLongHashMap();
	protected Map<String, Long> bibsProcessedStringId = new HashMap<String, Long>();
	
	protected TLongLongHashMap bibsYet2ArriveLongId = new TLongLongHashMap();
	protected Map<String, Long> bibsYet2ArriveStringId = new HashMap<String, Long>();
	
	// Struggled to come up with a good name here.  manifestations are not held,
	// but holdings pointing to them are.  The point of this is to switch the status
	// on holdings records from H to A.  Instead of keeping all the previously held
	// holdings ids (that are no active) in memory, it'll be easier to keep the 
	// manifestation id that was being waited on (but finally came through) since
	// I already have it.
	protected TLongArrayList manifestionIdsPreviouslyHeld = new TLongArrayList();
	
	@Override
	public void init() {
		super.init();
		//TODO load all data structures
	}
	
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
	protected void endBatch() {
		try {
			// persist 4 001->recordId maps
			
			// persist links
			
			// flip holdings records from H to A based on manifestionIdsPreviouslyHeld
			//   (use the links table)  
			
			manifestionIdsPreviouslyHeld.clear();
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
		super.endBatch();
	}
	
	@Override
	public List<OutputRecord> process(InputRecord record) {
		try {			
			List<OutputRecord> results = new ArrayList<OutputRecord>();

			if (Record.DELETED == record.getStatus()) {
				if (record.getSuccessors() != null) {
					Long manifestationId = null; 
					for (OutputRecord or : record.getSuccessors()) {
						or.setStatus(Record.DELETED);
						results.add(or);
						Record r = getRepository().getRecord(or.getId());
						if (AggregateXCRecord.MANIFESTATION.equals(getXCRecordService().getType(r))) {
							manifestationId = or.getId();
						}
					}
					if (manifestationId != null) {
						List<Long> holdingIds = getTransformationDAO().getLinkedRecordIds(manifestationId);
						if (holdingIds != null) {
							for (Long holdingId : holdingIds) {
								Record deletedHolding = new Record();
								deletedHolding.setId(holdingId);
								results.add(deletedHolding);
							}
						}
					}
				}
			} else {
				record.setMode(Record.JDOM_MODE);
				Element marcXml = record.getOaiXmlEl();

				MarcXmlRecord originalRecord = new MarcXmlRecord(marcXml);
				
				// Get the ORG code from the 035 field
				orgCode = originalRecord.getOrgCode();
				if (orgCode.equals("")) {
					// Add error
					//record.addError(service.getId() + "-100: An organization code could not be found on either the 003 or 035 field of input MARC record.");
				}
				
				boolean hasBib = false;
				boolean hasHolding = false;
				
				char leader06 = originalRecord.getLeader().charAt(6);
				if("abcdefghijkmnoprt".contains(""+leader06)) {
					hasBib = true;
				} else if(leader06 == 'u' || leader06 == 'v' || leader06 == 'x' || leader06 == 'y') {
					hasHolding = true;
				} else { // If leader 6th character is invalid, then log error and do not process that record.
					logError("Record Id " + record.getId() + " with leader character " + leader06 + " not processed.");
					return results;
				}
				
				AggregateXCRecord ar = new AggregateXCRecord();
				if (hasBib) {
					processBibliographicRecord(ar, originalRecord);
				}
				// not sure if this should be an else if
				if (hasHolding) {
					processHoldingRecord(ar, originalRecord);
				}
				
				if (record.getSuccessors() != null && record.getSuccessors().size() > 0) {
					for (OutputRecord or : record.getSuccessors()) {
						Record succ = getRepository().getRecord(or.getId());
						String type = getXCRecordService().getType(succ);
						if (AggregateXCRecord.HOLDINGS.equals(type)) {
							ar.getPreviousHoldingIds().add(or.getId());
						} else if (AggregateXCRecord.MANIFESTATION.equals(type)) {
							ar.setPreviousManifestationId(or.getId());
						} else if (AggregateXCRecord.EXPRESSION.equals(type)) {
							ar.getPreviousExpressionIds().add(or.getId());
						} else if (AggregateXCRecord.WORK.equals(type)) {
							ar.getPreviousWorkIds().add(or.getId());
						} else {
							throw new RuntimeException("bogus");
						}
					}
				}
				if (hasBib) {
					String bib001 = originalRecord.getControlField("001");
					Long manifestationId = getManifestationId4BibYet2Arrive(bib001);
					if (manifestationId != null) {
						removeManifestationId4BibYet2Arrive(bib001);
						manifestionIdsPreviouslyHeld.add(manifestationId);
						//mark held holdings as active
					} else {
						if (ar.getPreviousManifestationId() != null) {
							manifestationId = getRepositoryDAO().getNextId();	
						} else {
							manifestationId = ar.getPreviousManifestationId();
						}
					}
					addManifestationId4BibProcessed(bib001, manifestationId);
					List<Record> bibRecords = getBibliographicRecords(ar, manifestationId);
					if (bibRecords != null) {
						results.add(bibRecords);
					}
				} if (hasHolding) {
					String holding004 = originalRecord.getControlField("004");
					Long manifestationId = getManifestationId4BibProcessed(holding004);
					char status = 0;
					if (manifestationId != null) {
						status = Record.ACTIVE;
					} else {
						manifestationId = getManifestationId4BibYet2Arrive(holding004);
						status = Record.HELD;
						if (manifestationId == null) {
							manifestationId = getRepositoryDAO().getNextId();
							addManifestationId4BibYet2Arrive(holding004, manifestationId);
						}
					}
					List<Record> holdingsRecords = getHoldingsRecords(ar, status);
					if (holdingsRecords != null) {
						results.add(holdingsRecords);
					}
				}
				//update service accordingly w/ new record counts
			}
			return results;
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
	
	/*
	 * Process bibliographic record
	 */
	protected void processBibliographicRecord(
			AggregateXCRecord transformedRecord, MarcXmlRecord originalRecord) 
				throws DataException, DatabaseConfigException, TransformerConfigurationException, 
					IndexException, TransformerException{
		
		// Run the transformation steps
		// Each one processes a different MARC XML field and adds the appropriate
		// XC fields to transformedRecord based on the field it processes.
		transformedRecord = process010(originalRecord, transformedRecord);
		
		transformedRecord = process015(originalRecord, transformedRecord);
		
		transformedRecord = process016(originalRecord, transformedRecord);
		
		transformedRecord = process022(originalRecord, transformedRecord);
		
		transformedRecord = process024(originalRecord, transformedRecord);
		
		transformedRecord = process028(originalRecord, transformedRecord);
		
		transformedRecord = process030(originalRecord, transformedRecord);
		
		transformedRecord = process035(originalRecord, transformedRecord);
		
		transformedRecord = process037(originalRecord, transformedRecord);
		
		transformedRecord = process050(originalRecord, transformedRecord);
		
		transformedRecord = process055(originalRecord, transformedRecord);
		
		transformedRecord = process060(originalRecord, transformedRecord);
		
		transformedRecord = process074(originalRecord, transformedRecord);
		
		transformedRecord = process082(originalRecord, transformedRecord);
		transformedRecord = process084(originalRecord, transformedRecord);
		
		transformedRecord = process086(originalRecord, transformedRecord);
		
		transformedRecord = process090(originalRecord, transformedRecord);
		
		transformedRecord = process092(originalRecord, transformedRecord);
		
		transformedRecord = process100(originalRecord, transformedRecord);
		
		transformedRecord = process110(originalRecord, transformedRecord);
		
		transformedRecord = process111(originalRecord, transformedRecord);
		
		transformedRecord = process130(originalRecord, transformedRecord);
		
		transformedRecord = process210(originalRecord, transformedRecord);
		
		transformedRecord = process222(originalRecord, transformedRecord);
		
		transformedRecord = process240(originalRecord, transformedRecord);
		
		transformedRecord = process243(originalRecord, transformedRecord);
		
		transformedRecord = process245(originalRecord, transformedRecord);
		
		transformedRecord = process246(originalRecord, transformedRecord);
		
		transformedRecord = process247(originalRecord, transformedRecord);
		
		transformedRecord = process250(originalRecord, transformedRecord);
		
		transformedRecord = process254(originalRecord, transformedRecord);
		
		transformedRecord = process255(originalRecord, transformedRecord);
		
		transformedRecord = process260(originalRecord, transformedRecord);
		
		transformedRecord = process300(originalRecord, transformedRecord);
		
		transformedRecord = process310(originalRecord, transformedRecord);
		
		transformedRecord = process321(originalRecord, transformedRecord);
		
		transformedRecord = process362(originalRecord, transformedRecord);
		
		transformedRecord = process440(originalRecord, transformedRecord);
		
		transformedRecord = process490(originalRecord, transformedRecord);
		
		transformedRecord = process500(originalRecord, transformedRecord);
		
		transformedRecord = process501(originalRecord, transformedRecord);
		
		transformedRecord = process502(originalRecord, transformedRecord);
		
		transformedRecord = process504(originalRecord, transformedRecord);
		transformedRecord = process505(originalRecord, transformedRecord);
		transformedRecord = process506(originalRecord, transformedRecord);
		transformedRecord = process507(originalRecord, transformedRecord);
		transformedRecord = process508(originalRecord, transformedRecord);
		transformedRecord = process510(originalRecord, transformedRecord);
		transformedRecord = process511(originalRecord, transformedRecord);
		transformedRecord = process513(originalRecord, transformedRecord);
		transformedRecord = process515(originalRecord, transformedRecord);
		transformedRecord = process518(originalRecord, transformedRecord);
		transformedRecord = process520(originalRecord, transformedRecord);
		transformedRecord = process521(originalRecord, transformedRecord);
		transformedRecord = process522(originalRecord, transformedRecord);
		transformedRecord = process525(originalRecord, transformedRecord);
		transformedRecord = process530(originalRecord, transformedRecord);
		transformedRecord = process533(originalRecord, transformedRecord);
		transformedRecord = process534(originalRecord, transformedRecord);
		transformedRecord = process538(originalRecord, transformedRecord);
		transformedRecord = process540(originalRecord, transformedRecord);
		transformedRecord = process544(originalRecord, transformedRecord);
		transformedRecord = process546(originalRecord, transformedRecord);
		transformedRecord = process547(originalRecord, transformedRecord);
		transformedRecord = process550(originalRecord, transformedRecord);
		transformedRecord = process555(originalRecord, transformedRecord);
		transformedRecord = process580(originalRecord, transformedRecord);
		transformedRecord = process586(originalRecord, transformedRecord);
		transformedRecord = process59X(originalRecord, transformedRecord);
		transformedRecord = process600(originalRecord, transformedRecord);
		transformedRecord = process610(originalRecord, transformedRecord);
		transformedRecord = process611(originalRecord, transformedRecord);
		transformedRecord = process630(originalRecord, transformedRecord);
		transformedRecord = process648(originalRecord, transformedRecord);
		transformedRecord = process650(originalRecord, transformedRecord);
		transformedRecord = process651(originalRecord, transformedRecord);
		transformedRecord = process653(originalRecord, transformedRecord);
		transformedRecord = process654(originalRecord, transformedRecord);
		transformedRecord = process655(originalRecord, transformedRecord);
		transformedRecord = process720(originalRecord, transformedRecord);
		transformedRecord = process740(originalRecord, transformedRecord);
		transformedRecord = process752(originalRecord, transformedRecord);
		transformedRecord = process760(originalRecord, transformedRecord);
		transformedRecord = process765(originalRecord, transformedRecord);
		transformedRecord = process770(originalRecord, transformedRecord);
		transformedRecord = process772(originalRecord, transformedRecord);
		transformedRecord = process773(originalRecord, transformedRecord);
		transformedRecord = process775(originalRecord, transformedRecord);
		transformedRecord = process776(originalRecord, transformedRecord);
		transformedRecord = process777(originalRecord, transformedRecord);
		transformedRecord = process780(originalRecord, transformedRecord);
		transformedRecord = process785(originalRecord, transformedRecord);
		transformedRecord = process786(originalRecord, transformedRecord);
		transformedRecord = process787(originalRecord, transformedRecord);
		transformedRecord = process800(originalRecord, transformedRecord);
		transformedRecord = process810(originalRecord, transformedRecord);
		transformedRecord = process811(originalRecord, transformedRecord);
		transformedRecord = process830(originalRecord, transformedRecord);
		transformedRecord = process852(originalRecord, transformedRecord);
		transformedRecord = process856(originalRecord, transformedRecord);
		transformedRecord = process866(originalRecord, transformedRecord);
		transformedRecord = process867(originalRecord, transformedRecord);
		transformedRecord = process868(originalRecord, transformedRecord);
		transformedRecord = process931(originalRecord, transformedRecord);
		transformedRecord = process932(originalRecord, transformedRecord);
		transformedRecord = process933(originalRecord, transformedRecord);
		transformedRecord = process934(originalRecord, transformedRecord);
		transformedRecord = process935(originalRecord, transformedRecord);
		transformedRecord = process937(originalRecord, transformedRecord);
		transformedRecord = process939(originalRecord, transformedRecord);
		transformedRecord = process943(originalRecord, transformedRecord);
		transformedRecord = process945(originalRecord, transformedRecord);
		transformedRecord = process947(originalRecord, transformedRecord);
		transformedRecord = process959(originalRecord, transformedRecord);
		transformedRecord = process963(originalRecord, transformedRecord);
		transformedRecord = process965(originalRecord, transformedRecord);
		transformedRecord = process967(originalRecord, transformedRecord);
		transformedRecord = process969(originalRecord, transformedRecord);
		transformedRecord = process700(originalRecord, transformedRecord);
		transformedRecord = process710(originalRecord, transformedRecord);
		transformedRecord = process711(originalRecord, transformedRecord);
		transformedRecord = process730(originalRecord, transformedRecord);
	
	}
	
	
	/*
	 * Process bibliographic record
	 */
	protected List<OutputRecord> getBibliographicRecords(AggregateXCRecord ar, long manifestationRecordId) 
			throws DataException, DatabaseConfigException, TransformerConfigurationException, IndexException, TransformerException{

		// A list of records resulting from processing the incoming record
		List<OutputRecord> results = new ArrayList<OutputRecord>();

		// Get the XC records created as output
		results = getXCRecordService().getSplitXCRecordXML(ar, manifestationRecordId);
		
		Record manifestationRecord = null;
		// Store the bib oai id, bib 001 field and its manifestation in database
		for (Record outputRecord:results) {

			if (outputRecord.getType().equals("XC-Manifestation")) {
				manifestationRecord = outputRecord;
				
				BibliographicManifestationMapping bibliographicManifestationMapping 
					= new BibliographicManifestationMapping(record.getOaiIdentifier(), outputRecord.getOaiIdentifier(), originalRecord.getControlField("001"));
				bibliographicManifestationMappingDAO.insert(bibliographicManifestationMapping);
				break;
			}
		}
		
		// Check for any held record
		heldHoldingRecords = heldHoldingRecordDAO.getByHolding004Field(originalRecord.getControlField("001"));
		
		// Get already processed holding record that has matching 004 field
		List<XCHoldingRecord> xcHoldingRecords = xcHoldingDAO.getByHolding004Field(originalRecord.getControlField("001"));
		
		// link the manifestation for already processed xc holding records
		linkManifestation(xcHoldingRecords, manifestationRecord.getOaiIdentifier());
		
		// If the input record is a new record then increment the processed record count
		if (!updatedInputRecord  && results.size() > 0) {
			inputRecordCount++;
		}
		
		return results;
	
	}
	
	
	/*
	 * Process holding record 
	 */
	protected void processHoldingRecord(
			AggregateXCRecord transformedRecord,
			MarcXmlRecord originalRecord) 
				throws  DatabaseConfigException, TransformerConfigurationException, 
					IndexException, TransformerException, DataException {

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
	}
	
	protected List<Record> getHoldingRecords(AggregateXCRecord ar, long manifestationId) {
		try {
			// Get the XC records created as output & Link Holding to manifestation
			List<Record> results = getXCRecordService().getSplitXCRecordXMLForHoldingRecord(
					ar, manifestationId);
			
			// If the input record is a new record then increment the processed record count
			if (true  && results.size() > 0) {
				inputRecordCount++;
			}
			
			return results;
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
}
