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
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import xc.mst.constants.Constants;
import xc.mst.constants.TransformationServiceConstants.FrbrLevel;



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
	private ArrayList<Element> additionalExpressionElements = new ArrayList<Element>();

	/**
	 * A list of work elements for this XC record
	 */
	private ArrayList<Element> additionalWorkElements = new ArrayList<Element>();

	
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
		for(Element workElement : additionalWorkElements)
			xcRootElement.addContent(workElement.addContent("\n\t")).addContent("\n\t");

		xcRootElement.addContent("\t")
		             .addContent(xcExpressionElement)
		             .addContent("\n\t")
		             .addContent(xcManifestationElement)
		             .addContent("\n\t");

		// Add the holdings elements
		for(Element holdingsElement : holdingsElements)
			xcRootElement.addContent("\t").addContent(holdingsElement.addContent("\n\t")).addContent("\n\t");

		// Add the extra expression elements
		for(Element expressionElement : additionalExpressionElements)
			xcRootElement.addContent(expressionElement.addContent("\n\t")).addContent("\n\t");

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
	 * Adds a holdings element to the XC record
	 *
	 * @param holdingsElementContent The element to add
	 */
	public void addAdditionalExpressionElement(String titleOfExpression)
	{
		Element additionalExpressionElement = (Element)xcExpressionElement.clone();
		Element titleOfExpressionElement = additionalExpressionElement.getChild(Constants.ELEMENT_TITLE_OF_EXPRESSION, XC_NAMESPACE);
		
		if(titleOfExpressionElement != null)
			titleOfExpressionElement.setText(titleOfExpression);
		else
		{
			titleOfExpressionElement  = new Element(Constants.ELEMENT_TITLE_OF_EXPRESSION, XC_NAMESPACE);
			titleOfExpressionElement.setText(titleOfExpression);
		}
		
		additionalExpressionElement.addContent("\n\t\t");
		additionalExpressionElement.addContent(titleOfExpressionElement.detach());
		additionalExpressionElements.add(additionalExpressionElement);
	
	}
	
	/**
	 * Adds a work element to the XC record
	 *
	 * @param workElementContent The element to add
	 */
	public void addAdditionalWorkElement(String titleOfTheWork, Element creatorElement)
	{
		Element additionalWorkElement = (Element)xcWorkElement.clone();
		
		// Replace the titleOfWork element content with the new one
		Element titleOfWorkEmelent = additionalWorkElement.getChild(Constants.ELEMENT_TITLE_OF_WORK, RDVOCAB_NAMESPACE);
		if(titleOfWorkEmelent != null)
			titleOfWorkEmelent.setText(titleOfTheWork);
		// If there is no titleOfWork element, create it
		else
			{
				titleOfWorkEmelent  = new Element(Constants.ELEMENT_TITLE_OF_WORK, RDVOCAB_NAMESPACE);
				titleOfWorkEmelent.setText(titleOfTheWork);
			}
		
		additionalWorkElement.addContent("\n\t\t");
		additionalWorkElement.addContent(titleOfWorkEmelent.detach());
		
		// Remove existing creator element
		if(additionalWorkElement.getChild(Constants.ELEMENT_CREATOR, XC_NAMESPACE) != null)
			additionalWorkElement.removeChild(Constants.ELEMENT_CREATOR, XC_NAMESPACE);
		
		//Remove the extra subject elements other than the first two.
		if( additionalWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE)!= null )
		{
			int size = additionalWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE).size();
					
				for(;size >2; size-- )
					((Element)additionalWorkElement.getChildren(Constants.ELEMENT_SUBJECT, XC_NAMESPACE).get(size-1)).detach();	
			
		}
		
		if(creatorElement!=null)
		{
			additionalWorkElement.addContent("\n\t\t");
			additionalWorkElement.addContent(creatorElement.detach());
		}
		
		additionalWorkElements.add(additionalWorkElement);
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
}
