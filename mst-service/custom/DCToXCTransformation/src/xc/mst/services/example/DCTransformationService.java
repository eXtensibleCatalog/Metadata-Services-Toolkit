package xc.mst.services.example;

import gnu.trove.TLongLongHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.services.example.DCTransformationServiceConstants.FrbrLevel;
import xc.mst.services.example.bo.XCRecord;
import xc.mst.utils.XmlHelper;

public class DCTransformationService extends SolrTransformationService {
	
	private static final Logger LOG = Logger.getLogger(DCTransformationService.class);

	/**
	 * The namespace for Dubline Core
	 */
	protected static Namespace dcterms_ns = Namespace.getNamespace("dcterms", "http://purl.org/dc/terms/");
	protected static Namespace dc_ns = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
	
	protected XmlHelper xmlHelper = new XmlHelper();

	//TODO - these datastructures need to be read in and they need to be persisted.
	// which begs the question about the lack of transactions... I need a way to 
	// rollback if something bad happens.  Probably the easiest thing to do is just to delete
	// records with some id higher than something.
	protected TLongLongHashMap bibsProcessedLongId = new TLongLongHashMap();
	protected Map<String, Long> bibsProcessedStringId = new HashMap<String, Long>();
	
	protected TLongLongHashMap bibsYet2ArriveLongId = new TLongLongHashMap();
	protected Map<String, Long> bibsYet2ArriveStringId = new HashMap<String, Long>();

	protected HashMap<String, String> dctermsElements = new HashMap<String, String>();
	protected HashMap<String, List<Element>> dcValues = new HashMap<String, List<Element>>();

	
	@Override
	public void init() {
		// Initialize the list of dcterms element
		dctermsElements.put("abstract", "work");
		dctermsElements.put("audience", "work");
		dctermsElements.put("coverage", "work");
		dctermsElements.put("creator", "work");
		dctermsElements.put("isReplacedBy", "work");
		dctermsElements.put("replaces", "work");
		dctermsElements.put("spatial", "work");
		dctermsElements.put("subject", "work");
		dctermsElements.put("temporal", "work");

		dctermsElements.put("available", "expression");
		dctermsElements.put("bibliographicCitation", "expression");
		dctermsElements.put("conformsTo", "expression");
		dctermsElements.put("contributor", "expression");
		dctermsElements.put("dateAccepted", "expression");
		dctermsElements.put("dateCopyrighted", "expression");
		dctermsElements.put("educationLevel", "expression");
		dctermsElements.put("hasFormat", "expression");
		dctermsElements.put("hasVersion", "expression");
		dctermsElements.put("instructionMethod", "expression");
		dctermsElements.put("isFormatOf", "expression");
		dctermsElements.put("isReferencedBy", "expression");
		dctermsElements.put("isRequiredBy", "expression");
		dctermsElements.put("isVersionOf", "expression");
		dctermsElements.put("language", "expression");
		dctermsElements.put("mediator", "expression");
		dctermsElements.put("references", "expression");
		dctermsElements.put("relation", "expression");
		dctermsElements.put("requires", "expression");
		dctermsElements.put("source", "expression");
		dctermsElements.put("type", "expression");

		dctermsElements.put("accessRights", "manifestation");
		dctermsElements.put("accrualMethod", "manifestation");
		dctermsElements.put("accrualPeriodicity", "manifestation");
		dctermsElements.put("accrualPolicy", "manifestation");
		dctermsElements.put("alternative", "manifestation");
		dctermsElements.put("created", "manifestation");
		dctermsElements.put("date", "manifestation");
		dctermsElements.put("dateSubmitted", "manifestation");
		dctermsElements.put("description", "manifestation");
		dctermsElements.put("extent", "manifestation");
		dctermsElements.put("format", "manifestation");
		dctermsElements.put("hasPart", "manifestation");
		dctermsElements.put("identifier", "manifestation");
		dctermsElements.put("isPartOf", "manifestation");
		dctermsElements.put("issued", "manifestation");
		dctermsElements.put("license", "manifestation");
		dctermsElements.put("medium", "manifestation");
		dctermsElements.put("modified", "manifestation");
		dctermsElements.put("provenance", "manifestation");
		dctermsElements.put("publisher", "manifestation");
		dctermsElements.put("rights", "manifestation");
		dctermsElements.put("rightsHolder", "manifestation");
		dctermsElements.put("tableOfContents", "manifestation");
		dctermsElements.put("title", "manifestation");
		dctermsElements.put("valid", "manifestation");
	}
	
	@Override
	public void setup() {
		getTransformationDAO().loadBibMaps(bibsProcessedLongId, bibsProcessedStringId, bibsYet2ArriveLongId, bibsYet2ArriveStringId);
	}

	@Override	
	protected void endBatch() {
		try {
			getTransformationDAO().persistBibMaps(bibsProcessedLongId, bibsProcessedStringId, 
					bibsYet2ArriveLongId, bibsYet2ArriveStringId);
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
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
	
	protected void addManifestationId4BibProcessed(String s, Long l) {
		add2Map(bibsProcessedLongId, bibsProcessedStringId, s, l);
	}

	protected void removeManifestationId4BibProcessed(String s) {
		removeFromMap(bibsProcessedLongId, bibsProcessedStringId, s);
	}

	protected Long getManifestationId4BibYet2Arrive(String s) {
		return getLongFromMap(bibsYet2ArriveLongId, bibsYet2ArriveStringId, s);
	}
	
	protected void removeManifestationId4BibYet2Arrive(String s) {
		removeFromMap(bibsYet2ArriveLongId, bibsYet2ArriveStringId, s);
	}

	public List<OutputRecord> process(InputRecord processMe) {
		try {			
			List<OutputRecord> results = new ArrayList<OutputRecord>();
			List<RecordMessage> errors = new ArrayList<RecordMessage>();

			if (Record.DELETED == processMe.getStatus()) {
				if (processMe.getSuccessors() != null) {
					for (OutputRecord or : processMe.getSuccessors()) {
						or.setStatus(Record.DELETED);
						results.add(or);
					}
				}
			} else {
				dcValues.clear();
				
				processMe.setMode(Record.JDOM_MODE);
				Element metadataEl = processMe.getOaiXmlEl();
				
				boolean isValidate = CheckMetadataFormat(metadataEl.getName());
				
				java.util.Set<String> keySet = dctermsElements.keySet();
				Iterator<String> keyItrtr = keySet.iterator();
				while ( keyItrtr.hasNext() ) {
					// Get element
					String element = keyItrtr.next();

					// Get the data fields
					List<Element> fields = metadataEl.getChildren(element, dcterms_ns);
					if ( fields.isEmpty() ) 
						fields = metadataEl.getChildren(element, dc_ns);

					// Set the data fields
					dcValues.put(element, fields);
				} // end loop over data fields

				// Format is different or record is empty
				if (dcValues.size() == 0 || !isValidate ) {
					errors.add(new RecordMessage(service.getId(), "101", "error", "Invalid format record."));
					processMe.setMessages(errors);
					results.add((OutputRecord)processMe);
					return results;
				}
				
				XCRecord xcRecord = new XCRecord();
				processBibliographicRecord(xcRecord, processMe);

				if (processMe.getSuccessors() != null && processMe.getSuccessors().size() > 0) {
					for (OutputRecord or : processMe.getSuccessors()) {
						Record succ = getRepository().getRecord(or.getId());
						String type = getXCRecordService().getType(succ);

						if (XCRecord.MANIFESTATION.equals(type)) {
							xcRecord.setPreviousManifestationId(or.getId());
						} else if (XCRecord.EXPRESSION.equals(type)) {
							List<Long> expressionIds = new ArrayList<Long>();
							expressionIds.add(or.getId());
							xcRecord.setPreviousExpressionIds(expressionIds);
						} else if (XCRecord.WORK.equals(type)) {
							List<Long> workIds = new ArrayList<Long>();
							workIds.add(or.getId());
							xcRecord.setPreviousWorkIds(workIds);
						} else {
							throw new RuntimeException("bogus");
						}
					}
				} else {
					inputRecordCount++;
				}

				String bib001 = ((Record)processMe).getHarvestedOaiIdentifier();
				Long manifestationId = getManifestationId4BibYet2Arrive(bib001);

				if (manifestationId != null) {
					removeManifestationId4BibYet2Arrive(bib001);
				} else {
					if (xcRecord.getPreviousManifestationId() != null) {
						manifestationId = xcRecord.getPreviousManifestationId();
					} else {
						manifestationId = getRepositoryDAO().getNextId();
					}
				}

				addManifestationId4BibProcessed(bib001, manifestationId);
				List<OutputRecord> bibRecords = getXCRecordService().getSplitXCRecordXML(
						getRepository(), xcRecord, manifestationId);

				if (bibRecords != null) {
					results.addAll(bibRecords);
					// TODO : Should I insert the records?
				}
			}
			return results;
			
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}

	private boolean CheckMetadataFormat(String format) {
		boolean isValidate = false;
		if (format.equals("oai_dc") || format.equals("dcterms")) {
			isValidate = true;
		} else {
			LOG.debug("Format error :" + format);
		}
		return isValidate;
	}

	
	@Override
	public void postInstall() {
		// The install.sql is automatically run, so you don't need to run
		// that explicitly here.  But perhaps you need this hook for something
		// else.
		LOG.debug("postInstall()");
	}
	
	@Override
	public void postUninstall() {
		// The uninstall.sql is automatically run, so you don't need to run
		// that explicitly here.  But perhaps you need this hook for something
		// else.
		LOG.debug("postUnInstall()");
	}
	/*
	 * Process bibliographic record
	 */
	protected void processBibliographicRecord(
			XCRecord transformedRecord, InputRecord originalRecord) {
		// Run the transformation steps
		// Each one processes a different MARC XML field and adds the appropriate
		// XC fields to transformedRecord based on the field it processes.
		java.util.Set<String> keySet = dctermsElements.keySet();
		Iterator<String> keyItrtr = keySet.iterator();
		while ( keyItrtr.hasNext() ) {
			String element = keyItrtr.next();
			String frbrLevel = dctermsElements.get(element);
			transformedRecord = dctermsTransform(transformedRecord, element, frbrLevel);
		}
	}

	private XCRecord dctermsTransform(XCRecord transformInto, String element, String frbrLevel)
	{
		FrbrLevel level = FrbrLevel.MANIFESTATION;
		if (frbrLevel.equals("work"))
			level = FrbrLevel.WORK;
		else if (frbrLevel.equals("expression"))
			level = FrbrLevel.EXPRESSION;
		
		return processFieldBasic(transformInto, dcValues, element, level);
	}
	
}
