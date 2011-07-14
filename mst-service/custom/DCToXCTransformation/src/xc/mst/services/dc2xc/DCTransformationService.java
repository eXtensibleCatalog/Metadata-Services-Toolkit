/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.dc2xc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.AggregateXCRecord;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.constants.TransformationServiceConstants.FrbrLevel;
import xc.mst.services.impl.service.GenericMetadataService;
import xc.mst.utils.XmlHelper;

public class DCTransformationService extends GenericMetadataService {
	
	private static final Logger LOG = Logger.getLogger(DCTransformationService.class);

	/**
	 * The namespace for Dubline Core
	 */
	protected static Namespace[] dcNamespaces = new Namespace[] {
		Namespace.getNamespace("dcterms", "http://purl.org/dc/terms"),
		Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/")
	};
	
	protected XmlHelper xmlHelper = new XmlHelper();

	protected HashMap<String, String> dctermsElements = new HashMap<String, String>();
	protected HashMap<String, List<Element>> dcValues = new HashMap<String, List<Element>>();

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

	@SuppressWarnings("unchecked")
	public List<OutputRecord> process(InputRecord processMe) {
		try {			
			List<OutputRecord> results = new ArrayList<OutputRecord>();

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
				
				boolean isValidate = checkMetadataFormat(metadataEl.getName());
				
				java.util.Set<String> keySet = dctermsElements.keySet();
				Iterator<String> keyItrtr = keySet.iterator();
				while ( keyItrtr.hasNext() ) {
					// Get element
					String element = keyItrtr.next();

					List<Element> fields = new ArrayList<Element>();
					for (Namespace ns : dcNamespaces) {
						// Get the data fields
						List<Element> fieldsInner = metadataEl.getChildren(element, ns);
						if (fieldsInner != null && fieldsInner.size() > 0) {
							fields.addAll(fieldsInner);
						}
					}

					// Set the data fields
					dcValues.put(element, fields);
				} // end loop over data fields

				// Format is different or record is empty
				if (dcValues.size() == 0 || !isValidate ) {
					LOG.debug("dcValues: "+dcValues);
					LOG.debug("isValidate: "+isValidate);
					addMessage(processMe, 101, RecordMessage.ERROR);
					return results;
				}
				
				AggregateXCRecord ar = new AggregateXCRecord();
				processBibliographicRecord(ar, processMe);
				if (processMe.getSuccessors() != null && processMe.getSuccessors().size() > 0) {
					for (OutputRecord or : processMe.getSuccessors()) {
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
				results = getXCRecordService().getSplitXCRecordXML(getRepository(), ar, null, 0);
			}
			return results;
			
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}

	private boolean checkMetadataFormat(String format) {
		boolean isValidate = false;
		if (format.equals("oai_dc") || format.equals("dcterms") || format.equals("dc")) {
			isValidate = true;
		} else {
			LOG.debug("Format error :" + format);
		}
		return isValidate;
	}
	
	protected void processBibliographicRecord(
			AggregateXCRecord transformedRecord, InputRecord originalRecord) {
		java.util.Set<String> keySet = dctermsElements.keySet();
		Iterator<String> keyItrtr = keySet.iterator();
		while ( keyItrtr.hasNext() ) {
			String element = keyItrtr.next();
			String frbrLevel = dctermsElements.get(element);
			transformedRecord = dctermsTransform(transformedRecord, element, frbrLevel);
		}
	}


	private AggregateXCRecord dctermsTransform(AggregateXCRecord transformInto, String element, String frbrLevel)
	{
		FrbrLevel level = FrbrLevel.MANIFESTATION;
		if (frbrLevel.equals("work"))
			level = FrbrLevel.WORK;
		else if (frbrLevel.equals("expression"))
			level = FrbrLevel.EXPRESSION;
		
		return processFieldBasic(transformInto, dcValues, element, level);
	}
	
	protected AggregateXCRecord processFieldBasic(AggregateXCRecord transformInto, HashMap<String, List<Element>>dcValues, String elementName, FrbrLevel level)
	{
		// Get the target elements Dublin Core record
		List<Element> fields = dcValues.get(elementName);
		
		// If there were no matching fields return the unmodified XC record
		if( fields.isEmpty() || fields == null || fields.size() == 0)
			return transformInto;

		for ( Iterator<Element> itr = fields.iterator(); itr.hasNext();) {
			Element eitr = itr.next();
			String value = eitr.getTextTrim(); 
			
			if(LOG.isDebugEnabled())
				LOG.debug("Adding a " + level + " level " + elementName  + " of \"" + value + "\"");

			// Setup the attribute list
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();

			// Add the element to the XC record
			getXCRecordService().addElement(transformInto, elementName, value.trim(), AggregateXCRecord.DCTERMS_NAMESPACE, attributes, level);
		}
		
		// Return the result
		return transformInto;
	}
	
}
