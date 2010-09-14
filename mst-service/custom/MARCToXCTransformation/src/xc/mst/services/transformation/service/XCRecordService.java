/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.transformation.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.services.impl.service.GenericMetadataServiceService;
import xc.mst.services.transformation.TransformationServiceConstants.FrbrLevel;
import xc.mst.services.transformation.bo.AggregateXCRecord;
import xc.mst.services.transformation.dao.TransformationDAO;
import xc.mst.utils.XmlHelper;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class XCRecordService extends GenericMetadataServiceService {

	protected XmlHelper xmlHelper = new XmlHelper();
	
	TransformationDAO transformationDAO = null;
	
	public TransformationDAO getTransformationDAO() {
		return transformationDAO;
	}

	public void setTransformationDAO(TransformationDAO transformationDAO) {
		this.transformationDAO = transformationDAO;
	}

	public String getType(Record r) {
		r.setMode(Record.JDOM_MODE);
		Element el = r.getOaiXmlEl();
		Element entityEl = el.getChild("entity", AggregateXCRecord.XC_NAMESPACE);
		String type = entityEl.getAttributeValue("type");

		return type;
	}

	/**
	 * Gets a Document Object containing the XC record
	 *
	 * @return A Document Object containing the XC record
	 */
	public Document getXcRecordXml(AggregateXCRecord ar) {
		// Add an endline character to the last child of each FRBR group.
		// This doesn't do anything except format the XML to make it easier to read.
		ar.xcWorkElement.addContent("\n\t");
		ar.xcExpressionElement.addContent("\n\t");
		ar.xcManifestationElement.addContent("\n\t");
		ar.xcItemElement.addContent("\n\t");

		ar.xcRootElement = (new Element("frbr", AggregateXCRecord.XC_NAMESPACE))
		                     .addContent("\n\t")
		                     .addContent(ar.xcWorkElement)
		                     .addContent("\n");

		// Add the extra work elements
		for(String key : ar.linkingFieldToWorkElement.keySet())
			ar.xcRootElement.addContent("\t").addContent(ar.linkingFieldToWorkElement.get(key).addContent("\n\t")).addContent("\n");

		// Add the extra work elements
		
		// BDA - what is the point of this?  newWorkElement isn't used anywhere?
		for(Hashtable<String, Element> subElements : ar.subElementsOfWorkElements){
			
			Element newWorkElement = (Element)ar.xcWorkElement.clone();	
			
			// Remove existing creator element
			if(newWorkElement.getChild(Constants.ELEMENT_CREATOR, AggregateXCRecord.XC_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_CREATOR, AggregateXCRecord.XC_NAMESPACE);
			
			// Remove existing author element
			if(newWorkElement.getChild(Constants.ELEMENT_AUTHOR, AggregateXCRecord.RDAROLE_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_AUTHOR, AggregateXCRecord.RDAROLE_NAMESPACE);
			
			//Remove the extra subject elements other than the first two.
			if( newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, AggregateXCRecord.XC_NAMESPACE)!= null )
			{
				int size = newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, AggregateXCRecord.XC_NAMESPACE).size();
						
					for(;size >2; size-- )
						((Element)newWorkElement.getChildren(
								Constants.ELEMENT_SUBJECT, AggregateXCRecord.XC_NAMESPACE).get(size-1)).detach();	
				
			}
			
			for (String element : subElements.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION)){

					// If exists, remove and add
					if(newWorkElement.getChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, AggregateXCRecord.XC_NAMESPACE) != null){
						newWorkElement.removeChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, AggregateXCRecord.XC_NAMESPACE);
						newWorkElement.addContent(subElements.get(element));
					}
					// Else just add 
					else{
						newWorkElement.addContent(subElements.get(element));
					}

					// If exists, replace the title
					Element titleOfExpressionElement = subElements.get(element);
					newWorkElement.addContent("\n\t\t");
					newWorkElement.addContent(titleOfExpressionElement.detach());
				}
				else if(element.equals(Constants.ELEMENT_CREATOR)) {
					newWorkElement.addContent("\n\t\t");
					newWorkElement.addContent(subElements.get(element));

				}
			}
		}

		ar.xcRootElement.addContent("\t")
		             .addContent(ar.xcExpressionElement)
		             .addContent("\n\t")
		             .addContent(ar.xcManifestationElement)
		             .addContent("\n\t");

		// Add the holdings elements
		for(Element holdingsElement : ar.holdingsElements)
			ar.xcRootElement.addContent("\t").addContent(holdingsElement.addContent("\n\t")).addContent("\n\t");

		// Add the extra expression elements
		for(Hashtable<String, Element> subElements : ar.subElementsOfExpressionElements){
			
			// Make a copy of the original 
			Element newExpressionElement = (Element)ar.xcExpressionElement.clone();

			// Get the original titleOfExpression element
			for (String element : subElements.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION))
				{
					// If exists, replace the title
					Element titleOfExpressionElement = subElements.get(element);
					newExpressionElement.addContent("\n\t\t");
					newExpressionElement.addContent(titleOfExpressionElement.detach());
				}
			}

			ar.xcRootElement.addContent(newExpressionElement.addContent("\n\t")).addContent("\n\t");
		}

		ar.xcRootElement.addContent(ar.xcItemElement)
		             .addContent("\n");

		// Add the namespaces which the XC record needs
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.XSI_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.RDVOCAB_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.DCTERMS_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.RDAROLE_NAMESPACE);

		ar.xcXml = (new Document()).setRootElement(ar.xcRootElement);

		return ar.xcXml;
	}


	public List<String> getXcRecordXmlSplit(AggregateXCRecord ar)
	{
		// A list to store the results
		ArrayList<String> results = new ArrayList<String>();

		// Add the main work element
		AggregateXCRecord temp = new AggregateXCRecord();
		if(ar.xcWorkElement.getChildren().size() > 0)
		{
			temp.xcWorkElement.addContent(ar.xcWorkElement.cloneContent());
			results.add(xmlHelper.getString(getXcRecordXml(temp).getRootElement()));
		}

		// Add the expression element
		if(ar.xcExpressionElement.getChildren().size() > 0)
		{
			temp = new AggregateXCRecord();
			temp.xcExpressionElement.addContent(ar.xcExpressionElement.cloneContent());
			results.add(xmlHelper.getString(getXcRecordXml(temp).getRootElement()));
		}

		// Add the manifestation element
		if(ar.xcManifestationElement.getChildren().size() > 0)
		{
			temp = new AggregateXCRecord();
			temp.xcManifestationElement.addContent(ar.xcManifestationElement.cloneContent());
			results.add(xmlHelper.getString(getXcRecordXml(temp).getRootElement()));
		}

		// Add the item element
		if(ar.xcItemElement.getChildren().size() > 0)
		{
			temp = new AggregateXCRecord();
			temp.xcItemElement.addContent(ar.xcItemElement.cloneContent());
			results.add(xmlHelper.getString(getXcRecordXml(temp).getRootElement()));
		}

		// Add any additional work elements
		for(String key : ar.linkingFieldToWorkElement.keySet())
		{
			temp = new AggregateXCRecord();
			temp.xcWorkElement.addContent(ar.linkingFieldToWorkElement.get(key).cloneContent());
			results.add(xmlHelper.getString(getXcRecordXml(temp).getRootElement()));
		}

		// Return the list of results
		return results;
	}
	

	public String getXcRecordXmlNoSplit(AggregateXCRecord ar)
	{
		// Return the result
		return xmlHelper.getString(getXcRecordXml(ar).getRootElement());
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
	public void addElement(AggregateXCRecord ar, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, FrbrLevel level)
	{
		if(!addingWontCauseDuplicates(ar, elementName, elementValue, namespace, attributes, level))
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
				ar.xcWorkElement.addContent("\n\t\t").addContent(newElement);
				ar.hasBibInfo = true;
				break;
			case EXPRESSION:
				ar.xcExpressionElement.addContent("\n\t\t").addContent(newElement);
				ar.hasBibInfo = true;
				break;
			case MANIFESTATION:
				ar.xcManifestationElement.addContent("\n\t\t").addContent(newElement);
				ar.hasBibInfo = true;
				break;
			case ITEM:
				ar.xcItemElement.addContent("\n\t\t").addContent(newElement);
				break;
			case HOLDINGS:
				for(Element holdingsElement : ar.holdingsElements)
					holdingsElement.addContent("\n\t\t").addContent((Element)newElement.clone());
				break;
		}
	}
	

	/**
	 * Adds a holdings element to the XC record
	 *
	 * @param holdingsElementContent The element to add
	 */
	public void addHoldingsElement(AggregateXCRecord ar, List<Element> holdingsElementContent)
	{
		Element holdingsElement = (new Element("entity", AggregateXCRecord.XC_NAMESPACE)).setAttribute("type", "holdings");

		for(Element content : holdingsElementContent)
			holdingsElement.addContent("\n\t\t").addContent(content);

		ar.holdingsElements.add(holdingsElement);
	}

	/**
	 * Adds an title to be added as a titleOfExpression to new expression element in 
	 * the XC record
	 *
	 * @param titleOfExpression The title to add
	 */
	public void addLinkedWorkAndExpression(AggregateXCRecord ar, 
			Hashtable<String, Element> workSubElements, Hashtable<String, Element> expressionSubElements){
	
		ar.subElementsOfWorkElements.add(workSubElements);
		ar.subElementsOfExpressionElements.add(expressionSubElements);
	}
	
		/**
	 * Adds an element to the XC record to a non-default work level element based on a specific linking field
	 *
	 * @param elementName The name of the element to add
	 * @param elementValue The value of the element to add
	 * @param namespace The namespace to which the element belongs.  This must be one of the static constant
	 *                  Namespace Objects in the XCRecord class
	 * @param attributes An ArrayList of Attribute Objects containing the attributes for the element we're adding
	 * @param linkingField The linking field value whose work element we're adding the element to.
	 */
	public void addElementBasedOnLinkingField(AggregateXCRecord ar, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, String linkingField)
	{
		if(!addingWontCauseDuplicates(ar, elementName, elementValue, namespace, attributes, linkingField))
			return;

		// The Element we're adding
		Element newElement = new Element(elementName, namespace);

		// Set the Element's value
		newElement.setText(elementValue);

		// Set the Element's attributes
		newElement.setAttributes(attributes);

		// The work element to add the new element to
		Element workElement = null;

		// Get the work element to add the new element to
		// If we haven't seen the specified linking field before, create a new work
		// element for it and add it to the map
		if(ar.linkingFieldToWorkElement.containsKey(linkingField))
			workElement = ar.linkingFieldToWorkElement.get(linkingField);
		else
		{
			workElement = (new Element("entity", AggregateXCRecord.XC_NAMESPACE)).setAttribute("type", "work");
			ar.linkingFieldToWorkElement.put(linkingField, workElement);
		}

		// Add the new element to the work element for the linking field
		workElement.addContent("\n\t\t").addContent(newElement);
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
	private boolean addingWontCauseDuplicates(AggregateXCRecord ar, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, FrbrLevel level)
	{
		StringBuilder value = new StringBuilder();

		value.append(elementName).append(elementValue).append(namespace.getPrefix()).append(level);
		for(Attribute attribute : attributes)
			value.append(attribute.getNamespacePrefix()).append(attribute.getName()).append(attribute.getValue());

		if(ar.addedElements.contains(value.toString()))
			return false;

		ar.addedElements.add(value.toString());
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
	protected boolean addingWontCauseDuplicates(AggregateXCRecord ar, String elementName, String elementValue, 
			Namespace namespace, ArrayList<Attribute> attributes, String linkingField)
	{
		StringBuilder value = new StringBuilder();

		value.append(elementName).append(elementValue).append(namespace.getPrefix()).append(linkingField);
		for(Attribute attribute : attributes)
			value.append(attribute.getNamespacePrefix()).append(attribute.getName()).append(attribute.getValue());

		if(ar.addedElements.contains(value.toString()))
			return false;

		ar.addedElements.add(value.toString());
		return true;
	}

	/**
	 * Gets a list of documents that represent the output of transformation service. Each document in 
	 * the list represents a FRBR level with its own OAI id. 
	 * 
	 * @param transformationService 
	 * @return
	 */
	public List<Record> getSplitXCRecordXML(AggregateXCRecord ar, Long manifestationId) 
			throws TransformerConfigurationException, TransformerException, DatabaseConfigException{

		List<Document> documents = new ArrayList<Document>();
		List<Record> records = new ArrayList<Record>();
		
		// Create the root document
		ar.xcRootElement = new Element("frbr", AggregateXCRecord.XC_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.XSI_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.RDVOCAB_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.DCTERMS_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.RDAROLE_NAMESPACE);

		/*$$ WORK $$*/
		// Create original Work Document
		Long workId = getId(ar.getPreviousWorkIds());
		String workOaiID = getRecordService().getOaiIdentifier(
				workId, getMetadataService().getService());
		Element tempXcWorkElement = (Element)ar.xcWorkElement.clone();
		tempXcWorkElement.setAttribute(new Attribute("id",workOaiID));
		ar.xcRootElement.addContent(tempXcWorkElement);
		Element rootElement = (Element)ar.xcRootElement.clone();
		records.add(createRecord(ar, workId, rootElement, null));
		ar.xcRootElement.removeContent();
		
		/*$$ EXPRESSION $$*/
		// Create original Expression Document
		Long expressionId = getId(ar.getPreviousExpressionIds());
		Element expressionToWorkLinkingElement = new Element("workExpressed", AggregateXCRecord.XC_NAMESPACE);
		expressionToWorkLinkingElement.setText(workOaiID);
		 
		String expressionOaiID = getRecordService().getOaiIdentifier(
				expressionId, getMetadataService().getService());
		Element tempXcExpressionElement = (Element)ar.xcExpressionElement.clone(); 
		tempXcExpressionElement.addContent(expressionToWorkLinkingElement.detach());
		tempXcExpressionElement.setAttribute(new Attribute("id",expressionOaiID));
		ar.xcRootElement.addContent(tempXcExpressionElement);
		rootElement = (Element)ar.xcRootElement.clone();
		List<String> workElementOaiIDs = new ArrayList<String>();
		workElementOaiIDs.add(workOaiID);
		records.add(createRecord(ar, expressionId, rootElement, workElementOaiIDs));
		ar.xcRootElement.removeContent();
		
		/*$$ LINKED WORK & EXPRESSION $$*/
		// Create the extra Work & Expression documents
		int index = 0;
		for(Hashtable<String, Element> workElement : ar.subElementsOfWorkElements) {
			// Work
			// Clone the orig work element
			Element newWorkElement = (Element)ar.xcWorkElement.clone();	
			// Remove creator & author element from the orig element
			if(newWorkElement.getChild(Constants.ELEMENT_CREATOR, AggregateXCRecord.XC_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_CREATOR, AggregateXCRecord.XC_NAMESPACE);
			if(newWorkElement.getChild(Constants.ELEMENT_AUTHOR, AggregateXCRecord.RDAROLE_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_AUTHOR, AggregateXCRecord.RDAROLE_NAMESPACE);
					
			//Remove the extra subject elements other than the first two.
			if( newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, AggregateXCRecord.XC_NAMESPACE)!= null )
			{
				int size = newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, AggregateXCRecord.XC_NAMESPACE).size();
						
					for(;size >2; size-- )
						((Element)newWorkElement.getChildren(
								Constants.ELEMENT_SUBJECT, AggregateXCRecord.XC_NAMESPACE).get(size-1)).detach();	
			
			}
			
			// Add title and creator
			for (String element : workElement.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_WORK)){

					// If exists, remove and add
					if(newWorkElement.getChild(Constants.ELEMENT_TITLE_OF_WORK, 
							AggregateXCRecord.RDVOCAB_NAMESPACE) != null){
						newWorkElement.removeChild(Constants.ELEMENT_TITLE_OF_WORK, 
								AggregateXCRecord.RDVOCAB_NAMESPACE);
						newWorkElement.addContent(workElement.get(element));
					}
					// Else just add 
					else{
						newWorkElement.addContent(workElement.get(element));
					}

					// If exists, replace the title
					Element titleOfExpressionElement = workElement.get(element);
					newWorkElement.addContent("\n\t\t");
					newWorkElement.addContent(titleOfExpressionElement.detach());
				}
				else if(element.equals(Constants.ELEMENT_CREATOR)) {
					newWorkElement.addContent("\n\t\t");
					newWorkElement.addContent(workElement.get(element));

				}
			}
			
			// Set the OAI id
			long newWorkId = getId(ar.getPreviousWorkIds()); 
			String newWorkOaiID = getRecordService().getOaiIdentifier(
					newWorkId, getMetadataService().getService());
			newWorkElement.setAttribute(new Attribute("id", newWorkOaiID));
			ar.xcRootElement.addContent(newWorkElement);
			records.add(createRecord(ar, newWorkId, (Element)ar.xcRootElement.clone(), null));
			ar.xcRootElement.removeContent();

			// Expression
			// Clone the original expression
			Element newExpressionElement = (Element)ar.xcExpressionElement.clone();
			// Generate the OAI id
			long newExpressionId = getId(ar.getPreviousExpressionIds());
			String newExpressionOaiID = getRecordService().getOaiIdentifier(
					newExpressionId, getMetadataService().getService());
			newExpressionElement.setAttribute(new Attribute("id", newExpressionOaiID));

			// Add or replace title
			Hashtable<String, Element> expElement = ar.subElementsOfExpressionElements.get(index);
			for (String element : expElement.keySet()) {
					
					if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION))
					{
					Element titleOfExpressionElement = expElement.get(element);
					// If exists, replace the title
					if (newExpressionElement.getChild(
							Constants.ELEMENT_TITLE_OF_EXPRESSION,
							AggregateXCRecord.XC_NAMESPACE) != null) {
						newExpressionElement.removeChild(
								Constants.ELEMENT_TITLE_OF_EXPRESSION,
								AggregateXCRecord.XC_NAMESPACE);
					}
					newExpressionElement.addContent(titleOfExpressionElement
							.detach());
				}
			}
			
			// Link to corresponding work doc
			expressionToWorkLinkingElement = new Element("workExpressed", AggregateXCRecord.XC_NAMESPACE);
			expressionToWorkLinkingElement.setText(newWorkOaiID);
			newExpressionElement.addContent(expressionToWorkLinkingElement.detach());
			
			ar.xcRootElement.addContent(newExpressionElement);
			doc = (new Document()).setRootElement((Element)ar.xcRootElement.clone());
			
			List<String> workExpressedOaiIDs = new ArrayList<String>();
			workExpressedOaiIDs.add(newWorkOaiID);
			records.add(createRecord(ar, newWorkId, "XC-Expression", doc, workExpressedOaiIDs));
			documents.add(doc);
			ar.xcRootElement.removeContent();
			
			index++;
		}		
		
		/*		for(String key : linkingFieldToWorkElement.keySet())
				{
						xcRootElement.addContent("\t").addContent(linkingFieldToWorkElement.get(key).addContent("\n\t")).addContent("\n");
						list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
						xcRootElement.removeContent();
				}
		*/
		
		/*$$ MANIFESTATION $$*/
		// Set the OAI id
		if (manifestationId == null) {
			manifestationId = getRepositoryDAO().getNextId();
		}
		String manifestationOaiId = getRecordService().getOaiIdentifier(
				manifestationId, getMetadataService().getService());
		ar.xcManifestationElement.setAttribute("id", manifestationOaiId);
		// Link to expression docs
		List<String> linkExpressionOAIIds = new ArrayList<String>();
		for (Document document : documents) {
			if(document.getRootElement().getChild("entity", AggregateXCRecord.XC_NAMESPACE).
					getAttributeValue("type").equals("expression"))
			{
				Element linkExpression =  new Element(
						"expressionManifested",AggregateXCRecord.XC_NAMESPACE);
				String linkExpressionOAIId = document.getRootElement().
						getChild("entity",AggregateXCRecord.XC_NAMESPACE).getAttributeValue("id");
				linkExpressionOAIIds.add(linkExpressionOAIId);
				linkExpression.setText(linkExpressionOAIId);
				ar.xcManifestationElement.addContent("\n\t\t")
									  .addContent(linkExpression.detach());
			}
		}
		ar.xcRootElement.addContent("\n\t")
		 			 .addContent(ar.xcManifestationElement)
		 			 .addContent("\n");
		doc = (new Document()).setRootElement((Element)ar.xcRootElement.clone());
		records.add(createRecord(ar, manifestationId, "XC-Manifestation", doc, linkExpressionOAIIds));
		documents.add(doc);
		ar.xcRootElement.removeContent();

		/*$$ HOLDINGS $$*/
		// Create the Holdings documents
		List<String> manifestationHeldOAIIds = new ArrayList<String>();
		for(Element holdingsElement : ar.holdingsElements){
			
			long holdingId = getRepositoryDAO().getNextId();
			String holdingOaiId = getRecordService().getOaiIdentifier(
					holdingId, getMetadataService().getService());
			holdingsElement.setAttribute("id", holdingOaiId);
			// Create back links to manifestation
			for (Document document : documents) {
				if(document.getRootElement().getChild("entity", AggregateXCRecord.XC_NAMESPACE).
						getAttributeValue("type").equals("manifestation"))
				{
					Element linkManifestation =  new Element(
							"manifestationHeld", AggregateXCRecord.XC_NAMESPACE);
					String manifestationHeld = document.getRootElement().getChild(
							"entity", AggregateXCRecord.XC_NAMESPACE).getAttributeValue("id");
					manifestationHeldOAIIds.add(manifestationHeld);
					linkManifestation.setText(manifestationHeld);
					holdingsElement.addContent("\n\t\t").addContent(linkManifestation.detach());
				}
				
			}
			ar.xcRootElement.addContent("\n\t").addContent(holdingsElement.addContent("\n\t")).addContent("\n");
			doc = (new Document()).setRootElement((Element)ar.xcRootElement.clone());
			records.add(createRecord(ar, holdingId, "XC-Holding", doc, manifestationHeldOAIIds));
			documents.add(doc);
			
			ar.xcRootElement.removeContent();
		}

		/*$$ ITEM $$*/
		// Create the Items documents
		if(ar.xcItemElement.getChildren().size() != 0)
		{
			long itemId = getRepositoryDAO().getNextId();
			String itemOaiId = getRecordService().getOaiIdentifier(
					itemId, getMetadataService().getService());
			
			ar.xcItemElement.setAttribute("id", itemOaiId);
			// Create back links to expression
			for (Document document : documents) {
				if(document.getRootElement().getChild(
						"entity", AggregateXCRecord.XC_NAMESPACE).getAttributeValue("type").equals("holdings"))
				{
					Element linkExpression =  new Element(
							"holdingsExemplified", AggregateXCRecord.XC_NAMESPACE);
					linkExpression.setText(document.getRootElement().getChild(
							"entity", AggregateXCRecord.XC_NAMESPACE).getAttributeValue("id"));
					ar.xcItemElement.addContent("\n\t\t").addContent(linkExpression.detach());
				}
			}
			ar.xcRootElement.addContent("\n\t")
			 .addContent(ar.xcItemElement)
			 .addContent("\n");
			doc = (new Document()).setRootElement((Element)ar.xcRootElement.clone());
			records.add(createRecord(ar, itemId, "XC-Item", doc, null));
			documents.add(doc);
			ar.xcRootElement.removeContent();

		}
		
		for (List<Long> previousIds : new List[] {
				ar.getPreviousWorkIds(),
				ar.getPreviousExpressionIds(),
				ar.getPreviousHoldingIds()
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
	
	
	/**
	 * Gets a list of documents that represent the output of transformation service. Each document in 
	 * the list represents a FRBR level with its own OAI id. 
	 * 
	 * @param transformationService 
	 * @return
	 */
	public List<Record> getSplitXCRecordXMLForHoldingRecord(
			AggregateXCRecord ar,
			long manifestationId) 
				throws TransformerConfigurationException, TransformerException, DatabaseConfigException{

		List<Record> records = new ArrayList<Record>();
		
		// Create the root document
		ar.xcRootElement = new Element("frbr", AggregateXCRecord.XC_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.XSI_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.RDVOCAB_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.DCTERMS_NAMESPACE);
		ar.xcRootElement.addNamespaceDeclaration(AggregateXCRecord.RDAROLE_NAMESPACE);

		/*$$ HOLDINGS $$*/
		// Create the Holdings documents
		List<String> manifestationHeldOAIIds = new ArrayList<String>();
		manifestationHeldOAIIds.add(getRecordService().getOaiIdentifier(manifestationId, 
				getMetadataService().getService()));
		for(Element holdingsElement : ar.holdingsElements){
			
			long holdingId = getRepositoryDAO().getNextId();
			String holdingOaiId = getRecordService().getOaiIdentifier(
					holdingId, getMetadataService().getService());
			
			holdingsElement.setAttribute("id", holdingOaiId);
			
			// Create back links to Manifestation
			Element linkManifestation =  new Element(
					"manifestationHeld", AggregateXCRecord.XC_NAMESPACE);
			linkManifestation.setText(getRecordService().getOaiIdentifier(manifestationId, 
					getMetadataService().getService()));
			holdingsElement.addContent("\n\t\t").addContent(linkManifestation.detach());

			ar.xcRootElement.addContent("\n\t").addContent(holdingsElement.addContent("\n\t")).addContent("\n");
			Document doc = (new Document()).setRootElement((Element)ar.xcRootElement.clone());
			records.add(createRecord(ar, holdingId, "XC-Holding", doc, manifestationHeldOAIIds));
			
			ar.xcRootElement.removeContent();
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
	private Record createRecord(AggregateXCRecord ar, Long recordId, 
			Element oaiXmlEl, List<String> upLinks) 
				throws TransformerConfigurationException, TransformerException, DatabaseConfigException {

		Record xcRecord = new Record();
		xcRecord.setId(recordId);
		xcRecord.setMode(Record.JDOM_MODE);
		xcRecord.setOaiXmlEl(oaiXmlEl);
		xcRecord.setFormat(ar.xcFormat);
		
		// BDA - I don't think these uplinks are actually being used.  I'll leave them for now.
		if (upLinks != null) {
			for (String upLink: upLinks) {
				xcRecord.addUpLink(upLink);
			}
		}

		return xcRecord;
	}
	
}
