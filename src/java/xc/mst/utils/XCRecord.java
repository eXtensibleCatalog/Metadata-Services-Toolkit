/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import xc.mst.constants.Constants;
import xc.mst.constants.TransformationServiceConstants.FrbrLevel;
import xc.mst.services.MetadataService;



/**
 * This class contains methods to add, update, and get the values of various XC record fields.
 *
 * @author Eric Osisek
 */
public class XCRecord
{
	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);

	/**
	 * The namespace for XML Schema Instance
	 */
	public static final Namespace XSI_NAMESPACE = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

	/**
	 * The namespace for the XC Schema
	 */
	public static final Namespace XC_NAMESPACE = Namespace.getNamespace("xc", "http://www.extensiblecatalog.info/Elements");

	/**
	 * The namespace for the RD Vocabulary
	 */
	public static final Namespace RDVOCAB_NAMESPACE = Namespace.getNamespace("rdvocab", "http://rdvocab.info/Elements");

	/**
	 * The namespace for DC Terms
	 */
	public static final Namespace DCTERMS_NAMESPACE = Namespace.getNamespace("dcterms", "http://purl.org/dc/terms/");

	/**
	 * The namespace for RDA Roles
	 */
	public static final Namespace RDAROLE_NAMESPACE = Namespace.getNamespace("rdarole", "http://rdvocab.info/roles");

	/**
	 * The element for the work FRBR level
	 */
	private Element xcWorkElement = (new Element("entity", XC_NAMESPACE)).setAttribute("type", "work");

	/**
	 * The element for the expression FRBR level
	 */
	private Element xcExpressionElement = (new Element("entity", XC_NAMESPACE)).setAttribute("type", "expression");

	/**
	 * The element for the manifestation FRBR level
	 */
	private Element xcManifestationElement = (new Element("entity", XC_NAMESPACE)).setAttribute("type", "manifestation");

	/**
	 * The element for the item FRBR level
	 */
	private Element xcItemElement = (new Element("entity", XC_NAMESPACE)).setAttribute("type", "item");

	/**
	 * An XC record can contain extra work elements describing works within the manifestation (such as the tracks on a CD.)
	 * Data on which information belongs in which of these work elements is maintained in the MARCXML linking fields.
	 * This HashMap maps a linking field value to the specific work element build for that linking field
	 */
	private HashMap<String, Element> linkingFieldToWorkElement = new HashMap<String, Element>();

	/**
	 * A list of holdings elements for this XC record
	 */
	private HashSet<Element> holdingsElements = new HashSet<Element>();

	/**
	 * A list of expression elements for this XC record
	 */
	private ArrayList<Hashtable<String,Element>> subElementsOfExpressionElements = new ArrayList<Hashtable<String,Element>>();

	/**
	 * A list of work elements for this XC record
	 */
	private ArrayList<Hashtable<String,Element>> subElementsOfWorkElements = new ArrayList<Hashtable<String,Element>>();

	/**
	 * Used to ensure that duplicates are not added to the XC record
	 */
	private HashSet<String> addedElements = new HashSet<String>();

	/**
	 * The root element for the XC Record
	 */
	private Element xcRootElement = null;

	/**
	 * Used to convert between jdom Objects and Strings
	 */
	private XMLOutputter outputter = new XMLOutputter();

	/**
	 * The MARC XML Document we're managing
	 */
	private Document xcXml = null;

	/**
	 * True iff the record contains non-holdings components
	 */
	private boolean hasBibInfo = false;

	/**
	 * The xc:recordID's type followed by its value
	 */
	private String xcRecordId = null;

	/**
	 * Gets the xc:recordID's type followed by its value
	 *
	 * @return The xc:recordID's type followed by its value
	 */
	public String getXcRecordId()
	{
		if(xcRecordId == null)
		{

		}

		return xcRecordId;
	}

	/**
	 * Return true iff the record contains non-holdings components
	 *
	 * @return True iff the record contains non-holdings components
	 */
	public boolean getHasBibInfo()
	{
		return hasBibInfo;
	}

	/**
	 * Constructs an empty XCRecord.
	 */
	public XCRecord()
	{
	} // end constructor

	/**
	 * Constructs a XCRecord based on a the passed XML file which follows the XC schema.
	 *
	 * @param xcXml The XC record we're managing
	 */
	@SuppressWarnings("unchecked")
	public XCRecord(Document xcXml)
	{
		// True if we've set the main work element, false otherwise
		boolean workSet = false;

		// An artifical linking field for adding extra work elements
		int artLinkingField = 1;

		// Get the content of the xc record
		List<Element> elements = xcXml.getRootElement().getChildren();

		for(Element element : elements)
		{
			String frbrLevel = element.getAttributeValue("type");

			if(frbrLevel.equals("work"))
			{
				hasBibInfo = true;

				if(!workSet)
				{
					xcWorkElement = element;
					workSet = true;
				}
				else
					linkingFieldToWorkElement.put("" + artLinkingField++, element);
			}
			else if(frbrLevel.equals("expression"))
			{
				xcExpressionElement = element;
				hasBibInfo = true;
			}
			else if(frbrLevel.equals("manifestation"))
			{
				xcManifestationElement = element;
				hasBibInfo = true;
			}
			else if(frbrLevel.equals("item"))
				xcItemElement = element;
			else if(frbrLevel.equals("holdings"))
				holdingsElements.add(element);
		}
	} // end constructor

	/**
	 * Gets a Document Object containing the XC record
	 *
	 * @return A Document Object containing the XC record
	 */
	public Document getXcRecordXml()
	{
		// Add an endline character to the last child of each FRBR group.
		// This doesn't do anything except format the XML to make it easier to read.
		xcWorkElement.addContent("\n\t");
		xcExpressionElement.addContent("\n\t");
		xcManifestationElement.addContent("\n\t");
		xcItemElement.addContent("\n\t");

		xcRootElement = (new Element("frbr", XC_NAMESPACE))
		                     .addContent("\n\t")
		                     .addContent(xcWorkElement)
		                     .addContent("\n");

		// Add the extra work elements
		for(String key : linkingFieldToWorkElement.keySet())
			xcRootElement.addContent("\t").addContent(linkingFieldToWorkElement.get(key).addContent("\n\t")).addContent("\n");

		// Add the extra work elements
		
		for(Hashtable<String, Element> subElements : subElementsOfWorkElements){
			
			Element newWorkElement = (Element)xcWorkElement.clone();	
			
			// Remove existing creator element
			if(newWorkElement.getChild(Constants.ELEMENT_CREATOR, XC_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_CREATOR, XC_NAMESPACE);
			
			// Remove existing author element
			if(newWorkElement.getChild(Constants.ELEMENT_AUTHOR, RDAROLE_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_AUTHOR, RDAROLE_NAMESPACE);
			
			//Remove the extra subject elements other than the first two.
			if( newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE)!= null )
			{
				int size = newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE).size();
						
					for(;size >2; size-- )
						((Element)newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE).get(size-1)).detach();	
				
			}
			
			for (String element : subElements.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION)){

					// If exists, remove and add
					if(newWorkElement.getChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, XC_NAMESPACE) != null){
						newWorkElement.removeChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, XC_NAMESPACE);
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

		xcRootElement.addContent("\t")
		             .addContent(xcExpressionElement)
		             .addContent("\n\t")
		             .addContent(xcManifestationElement)
		             .addContent("\n\t");

		// Add the holdings elements
		for(Element holdingsElement : holdingsElements)
			xcRootElement.addContent("\t").addContent(holdingsElement.addContent("\n\t")).addContent("\n\t");

		// Add the extra expression elements
		for(Hashtable<String, Element> subElements : subElementsOfExpressionElements){
			
			// Make a copy of the original 
			Element newExpressionElement = (Element)xcExpressionElement.clone();

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

			xcRootElement.addContent(newExpressionElement.addContent("\n\t")).addContent("\n\t");
		}

		xcRootElement.addContent(xcItemElement)
		             .addContent("\n");

		// Add the namespaces which the XC record needs
		xcRootElement.addNamespaceDeclaration(XSI_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(RDVOCAB_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(DCTERMS_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(RDAROLE_NAMESPACE);

		xcXml = (new Document()).setRootElement(xcRootElement);

		return xcXml;
	}

	public List<String> getXcRecordXmlSplit()
	{
		// A list to store the results
		ArrayList<String> results = new ArrayList<String>();

		// Add the main work element
		XCRecord temp = new XCRecord();
		if(this.xcWorkElement.getChildren().size() > 0)
		{
			temp.xcWorkElement.addContent(this.xcWorkElement.cloneContent());
			results.add(outputter.outputString(temp.getXcRecordXml()));
		}

		// Add the expression element
		if(this.xcExpressionElement.getChildren().size() > 0)
		{
			temp = new XCRecord();
			temp.xcExpressionElement.addContent(this.xcExpressionElement.cloneContent());
			results.add(outputter.outputString(temp.getXcRecordXml()));
		}

		// Add the manifestation element
		if(this.xcManifestationElement.getChildren().size() > 0)
		{
			temp = new XCRecord();
			temp.xcManifestationElement.addContent(this.xcManifestationElement.cloneContent());
			results.add(outputter.outputString(temp.getXcRecordXml()));
		}

		// Add the item element
		if(this.xcItemElement.getChildren().size() > 0)
		{
			temp = new XCRecord();
			temp.xcItemElement.addContent(this.xcItemElement.cloneContent());
			results.add(outputter.outputString(temp.getXcRecordXml()));
		}

		// Add any additional work elements
		for(String key : linkingFieldToWorkElement.keySet())
		{
			temp = new XCRecord();
			temp.xcWorkElement.addContent(linkingFieldToWorkElement.get(key).cloneContent());
			results.add(outputter.outputString(temp.getXcRecordXml()));
		}

		// Return the list of results
		return results;
	}

	public String getXcRecordXmlNoSplit()
	{
		// Return the result
		return outputter.outputString(getXcRecordXml());
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
	public void addElement(String elementName, String elementValue, Namespace namespace, ArrayList<Attribute> attributes, FrbrLevel level)
	{
		if(!addingWontCauseDuplicates(elementName, elementValue, namespace, attributes, level))
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
				xcWorkElement.addContent("\n\t\t").addContent(newElement);
				hasBibInfo = true;
				break;
			case EXPRESSION:
				xcExpressionElement.addContent("\n\t\t").addContent(newElement);
				hasBibInfo = true;
				break;
			case MANIFESTATION:
				xcManifestationElement.addContent("\n\t\t").addContent(newElement);
				hasBibInfo = true;
				break;
			case ITEM:
				xcItemElement.addContent("\n\t\t").addContent(newElement);
				break;
			case HOLDINGS:
				for(Element holdingsElement : holdingsElements)
					holdingsElement.addContent("\n\t\t").addContent((Element)newElement.clone());
				break;
		}
	}

	/**
	 * Adds a holdings element to the XC record
	 *
	 * @param holdingsElementContent The element to add
	 */
	public void addHoldingsElement(List<Element> holdingsElementContent)
	{
		Element holdingsElement = (new Element("entity", XC_NAMESPACE)).setAttribute("type", "holdings");

		for(Element content : holdingsElementContent)
			holdingsElement.addContent("\n\t\t").addContent(content);

		holdingsElements.add(holdingsElement);
	}

	/**
	 * Adds an title to be added as a titleOfExpression to new expression element in 
	 * the XC record
	 *
	 * @param titleOfExpression The title to add
	 */
	public void addLinkedWorkAndExpression( Hashtable<String, Element> workSubElements, Hashtable<String, Element> expressionSubElements){
	
		subElementsOfWorkElements.add(workSubElements);
		subElementsOfExpressionElements.add(expressionSubElements);
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
	public void addElementBasedOnLinkingField(String elementName, String elementValue, Namespace namespace, ArrayList<Attribute> attributes, String linkingField)
	{
		if(!addingWontCauseDuplicates(elementName, elementValue, namespace, attributes, linkingField))
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
		if(linkingFieldToWorkElement.containsKey(linkingField))
			workElement = linkingFieldToWorkElement.get(linkingField);
		else
		{
			workElement = (new Element("entity", XC_NAMESPACE)).setAttribute("type", "work");
			linkingFieldToWorkElement.put(linkingField, workElement);
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
	private boolean addingWontCauseDuplicates(String elementName, String elementValue, Namespace namespace, ArrayList<Attribute> attributes, FrbrLevel level)
	{
		StringBuilder value = new StringBuilder();

		value.append(elementName).append(elementValue).append(namespace.getPrefix()).append(level);
		for(Attribute attribute : attributes)
			value.append(attribute.getNamespacePrefix()).append(attribute.getName()).append(attribute.getValue());

		if(addedElements.contains(value.toString()))
			return false;

		addedElements.add(value.toString());
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
	private boolean addingWontCauseDuplicates(String elementName, String elementValue, Namespace namespace, ArrayList<Attribute> attributes, String linkingField)
	{
		StringBuilder value = new StringBuilder();

		value.append(elementName).append(elementValue).append(namespace.getPrefix()).append(linkingField);
		for(Attribute attribute : attributes)
			value.append(attribute.getNamespacePrefix()).append(attribute.getName()).append(attribute.getValue());

		if(addedElements.contains(value.toString()))
			return false;

		addedElements.add(value.toString());
		return true;
	}

	/**
	 * Gets a list of documents that represent the output of transformation service. Each document in 
	 * the list represents a FRBR level with its own OAI id. 
	 * 
	 * @param transformationService 
	 * @return
	 */
	public ArrayList<Document> getSplitXCRecordXML(MetadataService transformationService){

		ArrayList<Document> list = new ArrayList<Document>();

		// Create the root document
		xcRootElement = new Element("frbr", XC_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(XSI_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(RDVOCAB_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(DCTERMS_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(RDAROLE_NAMESPACE);

		/*$$ WORK $$*/
		// Create original Work Document
		String workElementOaiID = transformationService.getNextOaiId();
		Element tempXcWorkElement = (Element)xcWorkElement.clone();
		tempXcWorkElement.setAttribute(new Attribute("id",workElementOaiID));
		xcRootElement.addContent("\n\t")
					 .addContent(tempXcWorkElement)
					 .addContent("\n");
		list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
		xcRootElement.removeContent();
		
		/*$$ EXPRESSION $$*/
		// Create original Expression Document
		Element expressionToWorkLinkingElement = new Element("workExpressed", XC_NAMESPACE);
		expressionToWorkLinkingElement.setText(workElementOaiID);
		Element tempXcExpressionElement = (Element)xcExpressionElement.clone();
		
		tempXcExpressionElement.addContent("\n\t\t").addContent(expressionToWorkLinkingElement.detach());
		tempXcExpressionElement.setAttribute(new Attribute("id",transformationService.getNextOaiId()));
		xcRootElement.addContent("\n\t")
        			 .addContent(tempXcExpressionElement)
					 .addContent("\n");
		list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
		xcRootElement.removeContent();
		
		/*$$ LINKED WORK & EXPRESSION $$*/
		// Create the extra Work & Expression documents
		int index = 0;
		for(Hashtable<String, Element> workElement : subElementsOfWorkElements)
		{
			// Work
			// Clone the orig work element
			Element newWorkElement = (Element)xcWorkElement.clone();	
			// Remove creator & author element from the orig element
			if(newWorkElement.getChild(Constants.ELEMENT_CREATOR, XC_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_CREATOR, XC_NAMESPACE);
			if(newWorkElement.getChild(Constants.ELEMENT_AUTHOR, RDAROLE_NAMESPACE) != null)
				newWorkElement.removeChild(Constants.ELEMENT_AUTHOR, RDAROLE_NAMESPACE);
					
			//Remove the extra subject elements other than the first two.
			if( newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE)!= null )
			{
				int size = newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE).size();
						
					for(;size >2; size-- )
						((Element)newWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE).get(size-1)).detach();	
			
			}
			
			// Add title and creator
			for (String element : workElement.keySet()) {
				
				if(element.equals(Constants.ELEMENT_TITLE_OF_WORK)){

					// If exists, remove and add
					if(newWorkElement.getChild(Constants.ELEMENT_TITLE_OF_WORK, RDVOCAB_NAMESPACE) != null){
						newWorkElement.removeChild(Constants.ELEMENT_TITLE_OF_WORK, RDVOCAB_NAMESPACE);
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
			workElementOaiID = transformationService.getNextOaiId();
			newWorkElement.setAttribute(new Attribute("id",workElementOaiID));
			xcRootElement.addContent("\n\t")
						 .addContent(newWorkElement)
						 .addContent("\n");
			list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
			xcRootElement.removeContent();

			// Expression
			// Clone the original expression
			Element newExpressionElement = (Element)xcExpressionElement.clone();
			// Generate the OAI id
			newExpressionElement.setAttribute(new Attribute("id",transformationService.getNextOaiId()));
			
			// Add or replace title
			Hashtable<String, Element> expElement = subElementsOfExpressionElements.get(index);
			for (String element : expElement.keySet()) {
					
					if(element.equals(Constants.ELEMENT_TITLE_OF_EXPRESSION))
					{
					Element titleOfExpressionElement = expElement.get(element);
					// If exists, replace the title
					if (newExpressionElement.getChild(Constants.ELEMENT_TITLE_OF_EXPRESSION,XC_NAMESPACE) != null) {
						newExpressionElement.removeChild(Constants.ELEMENT_TITLE_OF_EXPRESSION,XC_NAMESPACE);
					}
					newExpressionElement.addContent("\n\t\t");
					newExpressionElement.addContent(titleOfExpressionElement
							.detach());
				}
			}
			
			// Link to corresponding work doc
			expressionToWorkLinkingElement = new Element("workExpressed", XC_NAMESPACE);
			expressionToWorkLinkingElement.setText(workElementOaiID);
			newExpressionElement.addContent("\n\t\t")
								.addContent(expressionToWorkLinkingElement.detach());
			
			xcRootElement.addContent("\n\t")
						 .addContent(newExpressionElement.addContent("\n\t")).addContent("\n");
			list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
			xcRootElement.removeContent();
			
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
		xcManifestationElement.setAttribute("id", transformationService.getNextOaiId());
		// Link to expression docs
		for (Document document : list) {
			if(document.getRootElement().getChild("entity",XC_NAMESPACE).getAttributeValue("type").equals("expression"))
			{
				Element linkExpression =  new Element("expressionManifested",XC_NAMESPACE);
				linkExpression.setText(document.getRootElement().getChild("entity",XC_NAMESPACE).getAttributeValue("id"));
				xcManifestationElement.addContent("\n\t\t")
									  .addContent(linkExpression.detach());
			}
		}
		xcRootElement.addContent("\n\t")
		 			 .addContent(xcManifestationElement)
		 			 .addContent("\n");
		list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
		xcRootElement.removeContent();

		/*$$ HOLDINGS $$*/
		// Create the Holdings documents
		for(Element holdingsElement : holdingsElements){
			holdingsElement.setAttribute("id",transformationService.getNextOaiId());
			// Create back links to expression
			for (Document document : list) {
				if(document.getRootElement().getChild("entity",XC_NAMESPACE).getAttributeValue("type").equals("manifestation"))
				{
					Element linkExpression =  new Element("manifestationHeld",XC_NAMESPACE);
					linkExpression.setText(document.getRootElement().getChild("entity",XC_NAMESPACE).getAttributeValue("id"));
					holdingsElement.addContent("\n\t\t").addContent(linkExpression.detach());
				}
				
			}
			xcRootElement.addContent("\n\t").addContent(holdingsElement.addContent("\n\t")).addContent("\n");
			list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
			xcRootElement.removeContent();
		}

		/*$$ ITEM $$*/
		// Create the Items documents
		if(xcItemElement.getChildren().size() != 0)
		{
			xcItemElement.setAttribute("id", transformationService.getNextOaiId());
			// Create back links to expression
			for (Document document : list) {
				if(document.getRootElement().getChild("entity",XC_NAMESPACE).getAttributeValue("type").equals("holdings"))
				{
					Element linkExpression =  new Element("holdingsExemplified",XC_NAMESPACE);
					linkExpression.setText(document.getRootElement().getChild("entity",XC_NAMESPACE).getAttributeValue("id"));
					xcItemElement.addContent("\n\t\t").addContent(linkExpression.detach());
				}
			}
			xcRootElement.addContent("\n\t")
			 .addContent(xcItemElement)
			 .addContent("\n");
			list.add((new Document()).setRootElement((Element)xcRootElement.clone()));
			xcRootElement.removeContent();

		}
		
		return list;
	}
	
}
