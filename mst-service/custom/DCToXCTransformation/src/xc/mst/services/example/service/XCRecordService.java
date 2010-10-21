/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.example.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
//import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataService;
import xc.mst.services.impl.service.GenericMetadataServiceService;
import xc.mst.services.example.DCTransformationServiceConstants.FrbrLevel;
import xc.mst.services.example.bo.XCRecord;
import xc.mst.services.example.dao.DCTransformationDAO;
//import xc.mst.utils.MSTConfiguration;
//import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class XCRecordService extends GenericMetadataServiceService {

	private static final Logger LOG = Logger.getLogger(XCRecordService.class);
	protected XmlHelper xmlHelper = new XmlHelper();
	
	DCTransformationDAO dcTransformationDAO = null;
	
	public DCTransformationDAO getDCTransformationDAO() {
		return dcTransformationDAO;
	}

	public void setDCTransformationDAO(DCTransformationDAO dcTransformationDAO) {
		this.dcTransformationDAO = dcTransformationDAO;
	}

	public String getType(Record r) {
		r.setMode(Record.JDOM_MODE);
		Element el = r.getOaiXmlEl();
		Element entityEl = el.getChild("entity", XCRecord.XC_NAMESPACE);
		String type = entityEl.getAttributeValue("type");

		return type;
	}

	/**
	 * Gets a Document Object containing the XC record
	 *
	 * @return A Document Object containing the XC record
	 */
	public Document getXcRecordXml(XCRecord record) {
		// Add an endline character to the last child of each FRBR group.
		// This doesn't do anything except format the XML to make it easier to read.

		record.xcRootElement = (new Element("frbr", XCRecord.XC_NAMESPACE))
		                     .addContent(record.xcWorkElement);

		// Add the extra work elements
		for(String key : record.linkingFieldToWorkElement.keySet())
			record.xcRootElement.addContent(record.linkingFieldToWorkElement.get(key));

		// Add the extra work elements
		
		// BDA - what is the point of this?  newWorkElement isn't used anywhere?
		for(Hashtable<String, Element> subElements : record.subElementsOfWorkElements){
			
			Element newWorkElement = (Element)record.xcWorkElement.clone();	
			
			// Remove existing creator element
			if(newWorkElement.getChild(Constants.ELEMENT_CREATOR, XCRecord.XC_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_CREATOR, XCRecord.XC_NAMESPACE);
			
			// Remove existing author element
			if(newWorkElement.getChild(Constants.ELEMENT_AUTHOR, XCRecord.RDAROLE_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_AUTHOR, XCRecord.RDAROLE_NAMESPACE);
			
			//Remove the extra subject elements other than the first two.
			if( newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XCRecord.XC_NAMESPACE)!= null )
			{
				int size = newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XCRecord.XC_NAMESPACE).size();
						
					for(;size >2; size-- )
						((Element)newWorkElement.getChildren(
								Constants.ELEMENT_SUBJECT, XCRecord.XC_NAMESPACE).get(size-1)).detach();	
				
			}
			
			for (String element : subElements.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION)){

					// If exists, remove and add
					if(newWorkElement.getChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, XCRecord.XC_NAMESPACE) != null){
						newWorkElement.removeChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, XCRecord.XC_NAMESPACE);
						newWorkElement.addContent(subElements.get(element));
					}
					// Else just add 
					else{
						newWorkElement.addContent(subElements.get(element));
					}

					// If exists, replace the title
					Element titleOfExpressionElement = subElements.get(element);
					newWorkElement.addContent(titleOfExpressionElement.detach());
				}
				else if(element.equals(Constants.ELEMENT_CREATOR)) {
					newWorkElement.addContent(subElements.get(element));

				}
			}
		}

		record.xcRootElement.addContent(record.xcExpressionElement)
		             .addContent(record.xcManifestationElement);

		// Add the holdings elements
		for(Element holdingsElement : record.holdingsElements)
			record.xcRootElement.addContent(holdingsElement);

		// Add the extra expression elements
		for(Hashtable<String, Element> subElements : record.subElementsOfExpressionElements){
			
			// Make a copy of the original 
			Element newExpressionElement = (Element)record.xcExpressionElement.clone();

			// Get the original titleOfExpression element
			for (String element : subElements.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION))
				{
					// If exists, replace the title
					Element titleOfExpressionElement = subElements.get(element);
					newExpressionElement.addContent(titleOfExpressionElement.detach());
				}
			}

			record.xcRootElement.addContent(newExpressionElement);
		}

		record.xcRootElement.addContent(record.xcItemElement);

		// Add the namespaces which the XC record needs
		record.xcRootElement.addNamespaceDeclaration(XCRecord.XSI_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.RDVOCAB_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.DCTERMS_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.RDAROLE_NAMESPACE);

		record.xcXml = (new Document()).setRootElement(record.xcRootElement);

		return record.xcXml;
	}

	/**
	 * Adds an element to the XC record at the specified FRBR level
	 *
	 * @param elementName The name of the element to add
	 * @param elementValue The value of the element to add
	 * @param namespace The namespace to which the element belongs.  This must be one of the static constant
	 *                  Namespace Objects in the XCRecord class
	 * @param attributes An ArrayList of Attribute Objects containing the attributes for the element we're adding
	 * @param level The FRBR level we should add the element to.
	 */
	public void addElement(XCRecord record, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, FrbrLevel level)
	{
		if(!addingWontCauseDuplicates(record, elementName, elementValue, namespace, attributes, level))
			return;

		// The Element we're adding
		Element newElement = new Element(elementName, namespace);

		// Set the Element's value
		newElement.setText(elementValue);

		// Set the Element's attributes
		newElement.setAttributes(attributes);

		// Add the Element to the correct FRBR level
		switch (level)
		{
			case WORK:
				record.xcWorkElement.addContent(newElement);
				record.hasBibInfo = true;
				break;
			case EXPRESSION:
				record.xcExpressionElement.addContent(newElement);
				record.hasBibInfo = true;
				break;
			case MANIFESTATION:
				record.xcManifestationElement.addContent(newElement);
				record.hasBibInfo = true;
				break;
			case ITEM:
				record.xcItemElement.addContent(newElement);
				break;
			case HOLDINGS:
				for(Element holdingsElement : record.holdingsElements)
					holdingsElement.addContent((Element)newElement.clone());
				break;
		}
	}
	
	/**
	 * Checks whether or not the element described by the passed parameters has been checked before.  If
	 * the same input is passed to this function multiple times, it will return true the first time and
	 * false every other time.
	 *
	 * @param elementName The name of the element to add
	 * @param elementValue The value of the element to add
	 * @param namespace The namespace to which the element belongs.  This must be one of the static constant
	 *                  Namespace Objects in the XCRecord class
	 * @param attributes An ArrayList of Attribute Objects containing the attributes for the element we're adding
	 * @param level The FRBR level we should add the element to.
	 * @return
	 */
	private boolean addingWontCauseDuplicates(XCRecord record, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, FrbrLevel level)
	{
		StringBuilder value = new StringBuilder();

		value.append(elementName).append(elementValue).append(namespace.getPrefix()).append(level);
		for(Attribute attribute : attributes)
			value.append(attribute.getNamespacePrefix()).append(attribute.getName()).append(attribute.getValue());

		if(record.addedElements.contains(value.toString()))
			return false;

		record.addedElements.add(value.toString());
		return true;
	}

	/**
	 * Checks whether or not the element described by the passed parameters has been checked before.  If
	 * the same input is passed to this function multiple times, it will return true the first time and
	 * false every other time.
	 *
	 * @param elementName The name of the element to add
	 * @param elementValue The value of the element to add
	 * @param namespace The namespace to which the element belongs.  This must be one of the static constant
	 *                  Namespace Objects in the XCRecord class
	 * @param attributes An ArrayList of Attribute Objects containing the attributes for the element we're adding
	 * @param linkingField The linking field value whose work element we're adding the element to.
	 * @return
	 */
	protected boolean addingWontCauseDuplicates(XCRecord record, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, String linkingField)
	{
		StringBuilder value = new StringBuilder();

		value.append(elementName).append(elementValue).append(namespace.getPrefix()).append(linkingField);
		for(Attribute attribute : attributes)
			value.append(attribute.getNamespacePrefix()).append(attribute.getName()).append(attribute.getValue());

		if(record.addedElements.contains(value.toString()))
			return false;

		record.addedElements.add(value.toString());
		return true;
	}

	/**
	 * Gets a list of documents that represent the output of transformation service. Each document in 
	 * the list represents a FRBR level with its own OAI id. 
	 * 
	 * @param transformationService 
	 * @return
	 */
	public List<OutputRecord> getSplitXCRecordXML(Repository repo, XCRecord record, Long manifestationId) 
		throws TransformerConfigurationException, TransformerException, DatabaseConfigException{

		List<Long> expressionIds = new ArrayList<Long>();
		List<Long> holdingIds = new ArrayList<Long>();
		List<OutputRecord> records = new ArrayList<OutputRecord>();
		
		// Create the root document
		record.xcRootElement = new Element("frbr", XCRecord.XC_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.XSI_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.RDVOCAB_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.DCTERMS_NAMESPACE);
		record.xcRootElement.addNamespaceDeclaration(XCRecord.RDAROLE_NAMESPACE);

		/*$$ WORK $$*/
		// Create original Work Document
		Long workId = getId(record.getPreviousWorkIds());
		String workOaiID = getRecordService().getOaiIdentifier( workId, getMetadataService().getService());
		Element tempXcWorkElement = (Element)record.xcWorkElement.clone();
		tempXcWorkElement.setAttribute(new Attribute("id",workOaiID));
		record.xcRootElement.addContent(tempXcWorkElement);
		Element rootElement = (Element)record.xcRootElement.clone();
		records.add(createRecord(record, workId, rootElement, null));
		record.xcRootElement.removeContent();
		
		/*$$ EXPRESSION $$*/
		// Create original Expression Document
		Long expressionId = getId(record.getPreviousExpressionIds());
		expressionIds.add(expressionId);
		Element expressionToWorkLinkingElement = new Element("workExpressed", XCRecord.XC_NAMESPACE);
		expressionToWorkLinkingElement.setText(workOaiID);
		
		String expressionOaiID = getRecordService().getOaiIdentifier(expressionId, getMetadataService().getService());
		Element tempXcExpressionElement = (Element)record.xcExpressionElement.clone(); 
		tempXcExpressionElement.addContent(expressionToWorkLinkingElement.detach());
		tempXcExpressionElement.setAttribute(new Attribute("id",expressionOaiID));
		record.xcRootElement.addContent(tempXcExpressionElement);
		rootElement = (Element)record.xcRootElement.clone();
		List<String> workElementOaiIDs = new ArrayList<String>();
		workElementOaiIDs.add(workOaiID);
		records.add(createRecord(record, expressionId, rootElement, workElementOaiIDs));
		record.xcRootElement.removeContent();
		
		/*$$ LINKED WORK & EXPRESSION $$*/
		// Create the extra Work & Expression documents
		int index = 0;
		for(Hashtable<String, Element> workElement : record.subElementsOfWorkElements) {
			// Work
			// Clone the orig work element
			Element newWorkElement = (Element)record.xcWorkElement.clone();	
			// Remove creator & author element from the orig element
			if(newWorkElement.getChild(Constants.ELEMENT_CREATOR, XCRecord.XC_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_CREATOR, XCRecord.XC_NAMESPACE);
			if(newWorkElement.getChild(Constants.ELEMENT_AUTHOR, XCRecord.RDAROLE_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_AUTHOR, XCRecord.RDAROLE_NAMESPACE);
					
			//Remove the extra subject elements other than the first two.
			if( newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XCRecord.XC_NAMESPACE)!= null ) {
				int size = newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XCRecord.XC_NAMESPACE).size();
						
				for(;size >2; size-- )
					((Element)newWorkElement.getChildren(
							Constants.ELEMENT_SUBJECT, XCRecord.XC_NAMESPACE).get(size-1)).detach();	
			
			}
			
			// Add title and creator
			for (String element : workElement.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_WORK)){

					// If exists, remove and add
					if(newWorkElement.getChild(Constants.ELEMENT_TITLE_OF_WORK, 
							XCRecord.RDVOCAB_NAMESPACE) != null){
						newWorkElement.removeChild(Constants.ELEMENT_TITLE_OF_WORK, 
								XCRecord.RDVOCAB_NAMESPACE);
						newWorkElement.addContent(workElement.get(element));
					}
					// Else just add 
					else{
						newWorkElement.addContent(workElement.get(element));
					}

					// If exists, replace the title
					Element titleOfExpressionElement = workElement.get(element);
					newWorkElement.addContent(titleOfExpressionElement.detach());
				}
				else if(element.equals(Constants.ELEMENT_CREATOR)) {
					newWorkElement.addContent(workElement.get(element));
				}
			}
			
			// Set the OAI id
			long newWorkId = getId(record.getPreviousWorkIds());
			LOG.debug("newWorkId: "+newWorkId);
			String newWorkOaiID = getRecordService().getOaiIdentifier(newWorkId, getMetadataService().getService());
			LOG.debug("newWorkOaiID: "+newWorkOaiID);
			newWorkElement.setAttribute(new Attribute("id", newWorkOaiID));
			record.xcRootElement.addContent(newWorkElement);
			records.add(createRecord(record, newWorkId, (Element)record.xcRootElement.clone(), null));
			record.xcRootElement.removeContent();

			// Expression
			// Clone the original expression
			Element newExpressionElement = (Element)record.xcExpressionElement.clone();
			// Generate the OAI id
			long newExpressionId = getId(record.getPreviousExpressionIds());
			expressionIds.add(newExpressionId);
			String newExpressionOaiID = getRecordService().getOaiIdentifier(newExpressionId, getMetadataService().getService());
			newExpressionElement.setAttribute(new Attribute("id", newExpressionOaiID));

			// Add or replace title
			Hashtable<String, Element> expElement = record.subElementsOfExpressionElements.get(index);
			for (String element : expElement.keySet()) {
					if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION))
					{
					Element titleOfExpressionElement = expElement.get(element);
					// If exists, replace the title
					if (newExpressionElement.getChild(
							Constants.ELEMENT_TITLE_OF_EXPRESSION,
							XCRecord.XC_NAMESPACE) != null) {
						newExpressionElement.removeChild(
								Constants.ELEMENT_TITLE_OF_EXPRESSION,
								XCRecord.XC_NAMESPACE);
					}
					newExpressionElement.addContent(titleOfExpressionElement
							.detach());
				}
			}
			
			// Link to corresponding work doc
			expressionToWorkLinkingElement = new Element("workExpressed", XCRecord.XC_NAMESPACE);
			expressionToWorkLinkingElement.setText(newWorkOaiID);
			newExpressionElement.addContent(expressionToWorkLinkingElement.detach());
			
			record.xcRootElement.addContent(newExpressionElement);
			
			List<String> workExpressedOaiIDs = new ArrayList<String>();
			workExpressedOaiIDs.add(newWorkOaiID);
			records.add(createRecord(record, newExpressionId, (Element)record.xcRootElement.clone(), workExpressedOaiIDs));
			record.xcRootElement.removeContent();
			
			index++;
		}		
		
		/*$$ MANIFESTATION $$*/
		// Set the OAI id
		if (manifestationId == null) {
			manifestationId = record.getPreviousManifestationId();
			if (manifestationId == null) {
				manifestationId = getRepositoryDAO().getNextId();
			}
		}
		String manifestationOaiId = getRecordService().getOaiIdentifier(manifestationId, getMetadataService().getService());
		record.xcManifestationElement.setAttribute("id", manifestationOaiId);
		// Link to expression docs
		List<String> linkExpressionOAIIds = new ArrayList<String>();
		for (Long eId : expressionIds) {
			Element linkExpression =  new Element(
					"expressionManifested", XCRecord.XC_NAMESPACE);
			String linkExpressionOAIId = getRecordService().getOaiIdentifier(eId, getMetadataService().getService());
			linkExpressionOAIIds.add(linkExpressionOAIId);
			linkExpression.setText(linkExpressionOAIId);
			record.xcManifestationElement.addContent(linkExpression.detach());
		}
		record.xcRootElement.addContent(record.xcManifestationElement);
		records.add(createRecord(record, manifestationId, (Element)record.xcRootElement.clone(), linkExpressionOAIIds));
		record.xcRootElement.removeContent();

		/*$$ ITEM $$*/
		// Create the Items documents
		if(record.xcItemElement.getChildren().size() != 0) {
			//TODO - when items being being used - see the strategy above for getting ids
			long itemId = getRepositoryDAO().getNextId();
			String itemOaiId = getRecordService().getOaiIdentifier(itemId, getMetadataService().getService());
			
			record.xcItemElement.setAttribute("id", itemOaiId);
			// Create back links to expression
			for (Long hId : holdingIds) {
				Element linkExpression =  new Element(
						"holdingsExemplified", XCRecord.XC_NAMESPACE);
				String hoaid = getRecordService().getOaiIdentifier(hId, getMetadataService().getService());
				linkExpression.setText(hoaid);
				record.xcItemElement.addContent(linkExpression.detach());
			}
			record.xcRootElement.addContent(record.xcItemElement);
			records.add(createRecord(record, itemId, (Element)record.xcRootElement.clone(), null));
			record.xcRootElement.removeContent();
		}
		
		for (List<Long> previousIds : new List[] {
				record.getPreviousWorkIds(),
				record.getPreviousExpressionIds(),
				record.getPreviousHoldingIds()
		}) {
			if (previousIds != null && previousIds.size() > 0) {
				for (int i=0; i<previousIds.size(); i++) {
					Record record2delete = new Record();
					record2delete.setId(previousIds.get(i));
					record2delete.setStatus(Record.DELETED);
					records.add(record2delete);
				}
			}	
		}
		
		return records;
	}

	protected Long getId(List<Long> previousIds) {
		Long recordId = null;
		if (previousIds != null && previousIds.size() > 0) {
			recordId = previousIds.get(0);
			previousIds.remove(0);
		}
		if (recordId == null) {
			recordId = getRepositoryDAO().getNextId();
		}
		return recordId;
	}
	
	/*
	 * Creates the record with the specified record type
	 * 
	 * @param recordType
	 * @param document
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws DatabaseConfigException
	 */
	private Record createRecord(XCRecord record, Long recordId, 
			Element oaiXmlEl, List<String> upLinks) 
				throws TransformerConfigurationException, TransformerException, DatabaseConfigException {

		Record xcRecord = new Record();
		xcRecord.setId(recordId);
		xcRecord.setMode(Record.JDOM_MODE);
		xcRecord.setOaiXmlEl(oaiXmlEl);
		xcRecord.setFormat(record.xcFormat);
		
		// BDA - I don't think these uplinks are actually being used.  I'll leave them for now.
		if (upLinks != null) {
			for (String upLink: upLinks) {
				xcRecord.addUpLink(upLink);
			}
		}
		return xcRecord;
	}

	public MetadataService getMetadataService() {
		return (MetadataService)config.getBean("DCTransformationService");
	}
}
