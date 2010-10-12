/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
 
package xc.mst.services.transformation;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongProcedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.services.transformation.bo.AggregateXCRecord;
import xc.mst.services.transformation.bo.SaxMarcXmlRecord;
import xc.mst.utils.TimingLogger;
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
	
	protected TLongLongHashMap bibsProcessedLongIdAdded = new TLongLongHashMap();
	protected Map<String, Long> bibsProcessedStringIdAdded = new HashMap<String, Long>();
	protected TLongLongHashMap bibsYet2ArriveLongIdAdded = new TLongLongHashMap();
	protected Map<String, Long> bibsYet2ArriveStringIdAdded = new HashMap<String, Long>();
	
	protected TLongLongHashMap bibsProcessedLongIdRemoved = new TLongLongHashMap();
	protected Map<String, Long> bibsProcessedStringIdRemoved = new HashMap<String, Long>();
	protected TLongLongHashMap bibsYet2ArriveLongIdRemoved = new TLongLongHashMap();
	protected Map<String, Long> bibsYet2ArriveStringIdRemoved = new HashMap<String, Long>();
	
	protected TLongHashSet previouslyHeldManifestationIds = new TLongHashSet();
	protected List<long[]> heldHoldings = new ArrayList<long[]>();
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void setup() {
		getTransformationDAO().loadBibMaps(bibsProcessedLongId, bibsProcessedStringId, bibsYet2ArriveLongId, bibsYet2ArriveStringId);
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
	
	protected void add2Map(TLongLongHashMap longLongMap, Map<String, Long> stringLongMap,
			TLongLongHashMap longLongMapAdded, Map<String, Long> stringLongMapAdded, String s, long lv) {
		try {
			Long bibMarcId = Long.parseLong(s);
			longLongMap.put(bibMarcId, lv);
			longLongMapAdded.put(bibMarcId, lv);
		} catch (NumberFormatException nfe) {
			stringLongMap.put(s, lv);
			stringLongMapAdded.put(s, lv);
		}
	}
	
	protected void removeFromMap(TLongLongHashMap longLongMap, Map<String, Long> stringLongMap,
			TLongLongHashMap longLongMapRemoved, Map<String, Long> stringLongMapRemoved, String s) {
		try {
			Long bibMarcId = Long.parseLong(s);
			longLongMap.remove(bibMarcId);
			longLongMapRemoved.remove(bibMarcId);
		} catch (NumberFormatException nfe) {
			stringLongMap.remove(s);
			stringLongMapRemoved.remove(s);
		}
	}
	
	protected Long getManifestationId4BibProcessed(String s) {
		return getLongFromMap(bibsProcessedLongId, bibsProcessedStringId, s);
	}
	protected void addManifestationId4BibProcessed(String s, Long l) {
		add2Map(bibsProcessedLongId, bibsProcessedStringId, bibsProcessedLongIdAdded, bibsProcessedStringIdAdded, s, l);
	}
	protected void removeManifestationId4BibProcessed(String s) {
		removeFromMap(bibsProcessedLongId, bibsProcessedStringId, bibsProcessedLongIdRemoved, bibsProcessedStringIdRemoved, s);
	}
	protected Long getManifestationId4BibYet2Arrive(String s) {
		return getLongFromMap(bibsYet2ArriveLongId, bibsYet2ArriveStringId, s);
	}
	protected void addManifestationId4BibYet2Arrive(String s, Long l) {
		add2Map(bibsYet2ArriveLongId, bibsYet2ArriveStringId, bibsYet2ArriveLongIdAdded, bibsYet2ArriveStringIdAdded, s, l);
	}
	protected void removeManifestationId4BibYet2Arrive(String s) {
		removeFromMap(bibsYet2ArriveLongId, bibsYet2ArriveStringId, bibsYet2ArriveLongIdRemoved, bibsYet2ArriveStringIdRemoved, s);
	}
	
	@Override
	protected void endBatch() {
		try {
			TimingLogger.start("TransformationDAO.endBatch");

			TimingLogger.start("TransformationDAO.non-generic");
			// persist 4 001->recordId maps
			getTransformationDAO().persistBibMaps(
					bibsProcessedLongIdAdded, bibsProcessedStringIdAdded,
					bibsProcessedLongIdRemoved, bibsProcessedStringIdRemoved,
					bibsYet2ArriveLongIdAdded, bibsYet2ArriveStringIdAdded,
					bibsYet2ArriveLongIdRemoved, bibsYet2ArriveStringIdRemoved);
			
			bibsProcessedLongIdAdded.clear();
			bibsProcessedStringIdAdded.clear();
			bibsProcessedLongIdRemoved.clear();
			bibsProcessedStringIdRemoved.clear();
			bibsYet2ArriveLongIdAdded.clear();
			bibsYet2ArriveStringIdAdded.clear();
			bibsYet2ArriveLongIdRemoved.clear();
			bibsYet2ArriveStringIdRemoved.clear();
			
			previouslyHeldManifestationIds.forEach(new TLongProcedure() {
				public boolean execute(long recordId) {
					LOG.debug("previouslyHeldManifestationId: "+recordId+"");
					return true;
				}
			});
			getTransformationDAO().persistHeldHoldings(heldHoldings);
			getTransformationDAO().getHoldingIdsToActivate(previouslyHeldManifestationIds).forEach(
					new TLongProcedure() {
						public boolean execute(long recordId) {
							LOG.debug("getRepository().activateRecord("+recordId+")");
							getRepository().activateRecord(recordId);
							return true;
						}
					});
			TimingLogger.stop("TransformationDAO.non-generic");
			super.endBatch(false);
			TimingLogger.start("TransformationDAO.non-generic");
			heldHoldings.clear();
			getTransformationDAO().deleteHeldHoldings(previouslyHeldManifestationIds);
			previouslyHeldManifestationIds.clear();
			/*
			// TODO: use polymorphism instead
			if (!(getRepository() instanceof TestRepository)) {
				super.endBatch();				
			}
			getTransformationDAO().persistHeldHoldings(heldHoldings);
			heldHoldings.clear();
			getTransformationDAO().deleteHeldHoldings(previouslyHeldManifestationIds);
			if (getRepository() instanceof TestRepository) {
				super.endBatch();				
			}
			*/
			
			TimingLogger.stop("TransformationDAO.non-generic");
			TimingLogger.stop("TransformationDAO.endBatch");
			TimingLogger.reset();
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
	}
	
	@Override
	public List<OutputRecord> process(InputRecord record) {
		//addErrorToInput(record, 12, RecordMessage.WARN);
		//addErrorToInput(record, 13, RecordMessage.ERROR, "the input is fubed");
		LOG.debug("getHarvestedOaiIdentifier(): "+((Record)record).getHarvestedOaiIdentifier());
		LOG.debug("getOaiIdentifier(): "+((Record)record).getOaiIdentifier());
		LOG.debug("getId(): "+((Record)record).getId());
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
						List<Long> holdingIds = getRepository().getLinkedRecordIds(manifestationId);
						if (holdingIds != null) {
							for (Long holdingId : holdingIds) {
								Record orphanedHolding = new Record();
								orphanedHolding.setStatus(Record.HELD);
								orphanedHolding.setId(holdingId);
								results.add(orphanedHolding);
							}
						}
					}
				}
			} else {
				record.setMode(Record.STRING_MODE);

				SaxMarcXmlRecord originalRecord = new SaxMarcXmlRecord(record.getOaiXml());
				
				// Get the ORG code from the 035 field
				orgCode = originalRecord.getOrgCode();
				if (orgCode.equals("")) {
					// Add error
					//record.addError(service.getId() + "-100: An organization code could not be found on either the 003 or 035 field of input MARC record.");
				}
				
				boolean isBib = false;
				boolean isHolding = false;
				
				char leader06 = originalRecord.getLeader().charAt(6);
				if("abcdefghijkmnoprt".contains(""+leader06)) {
					isBib = true;
				} else if(leader06 == 'u' || leader06 == 'v' || leader06 == 'x' || leader06 == 'y') {
					isHolding = true;
				} else { // If leader 6th character is invalid, then log error and do not process that record.
					logError("Record Id " + record.getId() + " with leader character " + leader06 + " not processed.");
					return results;
				}
				
				AggregateXCRecord ar = new AggregateXCRecord();
				if (isBib) {
					processBibliographicRecord(ar, originalRecord);
				} else if (isHolding) {
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
				} else {
					inputRecordCount++;
				}
				if (isBib) {
					String bib001 = originalRecord.getControlField(1);
					Long manifestationId = getManifestationId4BibYet2Arrive(bib001);
					if (manifestationId != null) {
						TimingLogger.add("found BibYet2Arrive", 1);
						removeManifestationId4BibYet2Arrive(bib001);
						previouslyHeldManifestationIds.add(manifestationId);
					} else {
						if (ar.getPreviousManifestationId() != null) {
							manifestationId = ar.getPreviousManifestationId();
						} else {
							manifestationId = getRepositoryDAO().getNextId();
						}
					}
					addManifestationId4BibProcessed(bib001, manifestationId);
					List<OutputRecord> bibRecords = getXCRecordService().getSplitXCRecordXML(
							getRepository(), ar, manifestationId);
					if (bibRecords != null) {
						results.addAll(bibRecords);
					}
				} else if (isHolding) {
					char status = Record.ACTIVE;
					List<Long> manifestationIds = new ArrayList<Long>();
					List<Long> manifestaionsIdsInWaiting = new ArrayList<Long>();
					if (ar.getReferencedBibs() == null) {
						LOG.error("ar.getReferencedBibs() == null");
					} else {
						for (String ref001 : ar.getReferencedBibs()) {
							Long manifestationId = getManifestationId4BibProcessed(ref001);
							
							LOG.debug("input "+record.getId()+ "manifestationId: "+manifestationId);
							if (manifestationId == null) {
								manifestationId = getManifestationId4BibYet2Arrive(ref001);
								status = Record.HELD;
								if (manifestationId == null) {
									manifestationId = getRepositoryDAO().getNextId();
									addManifestationId4BibYet2Arrive(ref001, manifestationId);
								}
								manifestaionsIdsInWaiting.add(manifestationId);
							}
							manifestationIds.add(manifestationId);
						}
						List<OutputRecord> holdingsRecords = getXCRecordService().getSplitXCRecordXMLForHoldingRecord(
								getRepository(), ar, manifestationIds);
						
						if (holdingsRecords != null) {
							for (OutputRecord r : holdingsRecords) {
								//addErrorToOutput(r, 16, RecordMessage.INFO);
								//addErrorToOutput(r, 17, RecordMessage.INFO, "the output is fubed");
								if (status ==  Record.HELD) {
									for (Long mid : manifestationIds) {
										heldHoldings.add(new long[] {r.getId(), mid});								
									}
								}
								r.setStatus(status);
								results.add(r);	
							}
						} else {
							LOG.debug("holdingsRecords == null");
						}
					}
				}
				//update service accordingly w/ new record counts
			}
			TimingLogger.add("output records", results.size());
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
			AggregateXCRecord transformedRecord, SaxMarcXmlRecord originalRecord) 
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
	 * Process holding record 
	 */
	protected void processHoldingRecord(
			AggregateXCRecord transformedRecord,
			SaxMarcXmlRecord originalRecord) 
				throws  DatabaseConfigException, TransformerConfigurationException, 
					IndexException, TransformerException, DataException {

		// Run the transformation steps
		// Each one processes a different MARC XML field and adds the appropriate
		// XC fields to transformedRecord based on the field it processes.
		holdingsProcess004(originalRecord, transformedRecord);
		holdingsProcess014(originalRecord, transformedRecord);
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

}
