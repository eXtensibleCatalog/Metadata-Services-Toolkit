/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Expression;
import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Item;
import xc.mst.bo.record.Manifestation;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;

/**
 * This class splits an XC record into its individual FRBR components
 *
 * @author Eric Osisek
 */
public class XcRecordSplitter
{
	/**
	 * Builds the XML Document based on the XC record's XML
	 */
	private static final SAXBuilder builder = new SAXBuilder();

	/**
	 * Used to convert between jdom Objects and Strings
	 */
	private static final XMLOutputter outputter = new XMLOutputter();

	/**
	 * The namespace for the XC Schema
	 */
	public static final Namespace XC_NAMESPACE = Namespace.getNamespace("xc", "http://www.extensiblecatalog.info/Elements");

	/**
	 * The namespace for the RD Vocabulary
	 */
	public static final Namespace RDVOCAB_NAMESPACE = Namespace.getNamespace("rdvocab", "http://rdvocab.info/Elements");

	/**
	 * Data access object for getting formats
	 */
	private static FormatDAO formatDao = (FormatDAO)MSTConfiguration.getInstance().getBean("FormatDAO");

	/**
	 * Data access object for getting FRBR level IDs
	 */
	protected static XcIdentifierForFrbrElementDAO frbrLevelIdDao = (XcIdentifierForFrbrElementDAO)MSTConfiguration.getInstance().getBean("XcIdentifierForFrbrElementDAO");

	/**
	 * A list of Work FRBR components contained in the original XC record
	 */
	private List<Work> works = new ArrayList<Work>();

	/**
	 * A list of Expression FRBR components contained in the original XC record
	 */
	private List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * A list of Manifestation FRBR components contained in the original XC record
	 */
	private List<Manifestation> manifestations = new ArrayList<Manifestation>();

	/**
	 * A list of Holdings FRBR components contained in the original XC record
	 */
	private List<Holdings> holdings = new ArrayList<Holdings>();

	/**
	 * A list of Item FRBR components contained in the original XC record
	 */
	private List<Item> items = new ArrayList<Item>();

	/**
	 * The record ID of the record used to create this XcRecordSplitter (used for logging)
	 */
	private long recordId = -1;

	/**
	 * The service that created this XcRecordSplitter
	 */
	private Service service = null;

	/**
	 * The XC schema format
	 */
	private static Format xcSchemaFormat = null;

	/**
	 * The logger object
	 */
	private static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);

	/**
	 * Constructs an xcRecordSplitter for the passed XML, which is assumed to be
	 * an XC record.
	 *
	 * @param record The XC record to be split
	 * @param service The service creating this splitter (used for logging).
	 */
	@SuppressWarnings("unchecked")
	public XcRecordSplitter(Record record, Service service)
	{
		try
		{
			xcSchemaFormat = formatDao.getByName("xc");
		}
		catch(DatabaseConfigException e)
		{
			log.error("Could not connect to the database with the parameters from the configuration file.", e);
		}
		
		recordId = record.getId();
		this.service = service;

		// The XML for the XC record
		Document xml = null;

		// Parse the XML from the record
		try
		{
			if(log.isDebugEnabled())
				log.debug("Parsing the record's XML into a Document Object.");

			xml = builder.build(new InputSource(new StringReader(record.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			log.error("An error occurred while parsing the record's XML.", e);

			LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");

			return;
		} // end catch IOException
		catch(JDOMException e)
		{
			log.error("An error occurred while parsing the record's XML.", e);

			LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");

			return;
		} // end catch JDOMException

		// Iterate over each of the components in the passed record, and add them to
		// the correct FRBR component list.
		List<Element> components = xml.getRootElement().getChildren();
		for(Element component : components)
		{
			String level = component.getAttributeValue("type"); // Get the type of the element, which will
			                                                    // be the FRBR level it belongs to
			// Add the component to the appropriate list, parsing out the key fields
			if(level.equals("work") && component.getChildren().size() > 0)
				works.add(buildWork(component));
			else if(level.equals("expression") && component.getChildren().size() > 0)
				expressions.add(buildExpression(component));
			else if(level.equals("manifestation") && component.getChildren().size() > 0)
				manifestations.add(buildManifestation(component));
			else if(level.equals("holdings") && component.getChildren().size() > 0)
				holdings.add(buildHoldings(component));
			else if(level.equals("item") && component.getChildren().size() > 0)
				items.add(buildItem(component));
		} // end loop over components
	} // end constructor

	/**
	 * Gets the works parsed by this XcRecordSplitter
	 *
	 * @return The list of works
	 */
	public List<Work> getWorks()
	{
		return works;
	}

	/**
	 * Gets the expressions parsed by this XcRecordSplitter
	 *
	 * @return The list of expressions
	 */
	public List<Expression> getExpressions()
	{
		return expressions;
	}

	/**
	 * Gets the manifestations parsed by this XcRecordSplitter
	 *
	 * @return The list of manifestations
	 */
	public List<Manifestation> getManifestations()
	{
		return manifestations;
	}

	/**
	 * Gets the holdings parsed by this XcRecordSplitter
	 *
	 * @return The list of holdings
	 */
	public List<Holdings> getHoldings()
	{
		return holdings;
	}

	/**
	 * Gets the items parsed by this XcRecordSplitter
	 *
	 * @return The list of items
	 */
	public List<Item> getItems()
	{
		return items;
	}

	/**
	 * Builds a Work Object based on the passed element, which is
	 * expected to be an xc:entity of type "work"
	 *
	 * This method adds an up link to the returned work element from each
	 * Manifestation element from this record.
	 *
	 * @param workElement The xc:entity Element to build the work from
	 * @return The Work element
	 */
	@SuppressWarnings("unchecked")
	private Work buildWork(Element workElement)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Work element from a work component of the record with ID " + recordId + ".");

		// Create a Work Object for the passed work element
		Work work = new Work();
		work.setFormat(xcSchemaFormat);
		work.setService(service); // Mark the Work as being from no service since it shouldn't be output

		try
		{
			work.setXcWorkId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_WORK));

			workElement.setAttribute("workID", Long.toString(work.getXcWorkId()), XC_NAMESPACE);
			work.setOaiXml(outputter.outputString(((Element)workElement.getParent().clone())
                                                 .setContent(new Text("\n\t"))
                                                            .addContent(((Element)workElement.clone()))
                                                            .addContent("\n\t")));
			work.setOaiIdentifier("aggWork:" + work.getXcWorkId());

			// Add an up link from each expression to this work
			for(Expression expression : expressions)
				expression.addLinkToWork(work);

			// An XPATH expression to get the identifierForTheWork elements
			XPath xpath = XPath.newInstance("./rdvocab:identifierForTheWork");
			xpath.addNamespace(RDVOCAB_NAMESPACE);

			// Get the subfields.
			List<Element> elements = xpath.selectNodes(workElement);

			// Loop over the identifierForTheWork elements and add each to the Work element
			for(Element element : elements)
			{
				// The value of the requested control field
				String value = element.getText();
				String type = element.getAttributeValue("type");

				if(log.isDebugEnabled())
					log.debug("Found an identifierForTheWork element with a value of " + value + " and a type of " + type + ".");

				work.addIdentifierForTheWork(type, value);
			} // end loop over identifierForTheWork elements

			return work;
		} // end try
		catch(JDOMException e)
		{
			log.error("An error occurred getting the identifierForTheWork elements from the passed Work element.", e);
			return work; // don't return null since we set up the IDs
		} // end catch(JDOMException)
	} // end method buildWork(Element)

	/**
	 * Builds a Expression Object based on the passed element, which is
	 * expected to be an xc:entity of type "expression"
	 *
	 * This method adds an up link to the returned expression element from each
	 * Manifestation element from this record.  It also adds an up link from the
	 * returned expression element to each Work element from this record.
	 *
	 * @param expressionElement The xc:entity Element to build the expression from
	 * @return The Expression element
	 */
	private Expression buildExpression(Element expressionElement)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Expression element from an expression component of the record with ID " + recordId + ".");

		// Create an Expression Object for the passed expression element
		Expression expression = new Expression();
		expression.setFormat(xcSchemaFormat);
		expression.setService(service); // Mark the Expression as being from no service since it shouldn't be output

		expression.setXcExpressionId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_EXPRESSION));

		expressionElement.setAttribute("expressionID", Long.toString(expression.getXcExpressionId()), XC_NAMESPACE);
		expression.setOaiXml(outputter.outputString(((Element)expressionElement.getParent().clone())
													   .setContent(new Text("\n\t"))
													   			  .addContent(((Element)expressionElement.clone()))
													   			  .addContent("\n\t")));
		expression.setOaiIdentifier("aggExpression:" + expression.getXcExpressionId());

		// Add an up link from each manifestation to this expression
		for(Manifestation manifestation : manifestations)
			manifestation.addLinkToExpression(expression);

		// Add an up link from this expression to each work
		for(Work work : works)
			expression.addLinkToWork(work);

		return expression;
	} // end method buildExpression(Element)

	/**
	 * Builds a Manifestation Object based on the passed element, which is
	 * expected to be an xc:entity of type "manifestation"
	 *
	 * This method adds an up link to the returned manifestation element from each
	 * Holdings element from this record.  It also adds an up link from the
	 * returned manifestation element to each Expression element from this record.
	 *
	 * @param maninfestationElement The xc:entity Element to build the manifestation from
	 * @return The Manifestation element
	 */
	@SuppressWarnings("unchecked")
	private Manifestation buildManifestation(Element manifestationElement)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Manifestation element from a manifestation component of the record with ID " + recordId + ".");

		// Create a Work Object for the passed work element
		Manifestation manifestation = new Manifestation();
		manifestation.setFormat(xcSchemaFormat);
		manifestation.setService(service); // Mark the Manifestation as being from no service since it shouldn't be output

		try
		{
			manifestation.setXcManifestationId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_MANIFESTATION));

			manifestationElement.setAttribute("manifestationID", Long.toString(manifestation.getXcManifestationId()), XC_NAMESPACE);
			manifestation.setOaiXml(outputter.outputString(((Element)manifestationElement.getParent().clone())
                    									  .setContent(new Text("\n\t"))
                    									  			 .addContent(((Element)manifestationElement.clone()))
                    									  			 .addContent("\n\t")));
			manifestation.setOaiIdentifier("aggManifestation:" + manifestation.getXcManifestationId());

			// Add an up link from each holdings to this manifestation
			for(Holdings holding : holdings)
				holding.addLinkToManifestation(manifestation);

			// Add an up link from this manifestation to each expression
			for(Expression expression : expressions)
				manifestation.addLinkToExpression(expression);

			// An XPATH expression to get the recordId elements
			XPath xpath = XPath.newInstance("./xc:recordID");
			xpath.addNamespace(XC_NAMESPACE);

			// Get the subfields.
			List<Element> elements = xpath.selectNodes(manifestationElement);

			// Loop over the recordId elements and add value of each to the manifestation
			for(Element element : elements)
			{
				String value = element.getText();
				String type = element.getAttributeValue("type");

				if(log.isDebugEnabled())
					log.debug("Found a recordId element with a value of " + value + " and a type of " + type + ".");

				manifestation.addXcRecordId(type, value);
			} // end loop over recordId elements

			return manifestation;
		} // end try
		catch(JDOMException e)
		{
			log.error("An error occurred getting the recordId elements from the passed Manifestation element.", e);
			return manifestation; // don't return null since we set up the IDs
		} // end catch(JDOMException)
	} // end method buildManifestation(Element)

	/**
	 * Builds a Holdings Object based on the passed element, which is
	 * expected to be an xc:entity of type "holdings"
	 *
	 * This method adds an up link to the returned holdings element from each
	 * Item element from this record.  It also adds an up link from the
	 * returned holdings element to each Manifestation element from this record.
	 *
	 * @param holdingsElement The xc:entity Element to build the holdings from
	 * @return The Holdings element
	 */
	@SuppressWarnings("unchecked")
	private Holdings buildHoldings(Element holdingsElement)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Holdings element from a holdings component of the record with ID " + recordId + ".");

		// Create a Work Object for the passed work element
		Holdings holdings = new Holdings();
		holdings.setFormat(xcSchemaFormat);
		holdings.setService(service); // Mark the Holdings as being from no service since it shouldn't be output

		try
		{
			holdings.setXcHoldingsId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_HOLDINGS));

			holdingsElement.setAttribute("holdingsID", Long.toString(holdings.getXcHoldingsId()), XC_NAMESPACE);
			holdings.setOaiXml(outputter.outputString(((Element)holdingsElement.getParent().clone())
                    								 .setContent(new Text("\n\t"))
                    								 			.addContent(((Element)holdingsElement.clone()))
                    								 			.addContent("\n\t")));
			holdings.setOaiIdentifier("aggHoldings:" + holdings.getXcHoldingsId());

			// Add an up link from each item to this holdings
			for(Item item : items)
				item.addLinkToHoldings(holdings);

			// Add an up link from this holdings to each manifestation
			for(Manifestation manifestation : manifestations)
				holdings.addLinkToManifestation(manifestation);

			// An XPATH expression to get the recordId elements
			XPath xpath = XPath.newInstance("./xc:recordID");
			xpath.addNamespace(XC_NAMESPACE);

			// Get the subfields.
			List<Element> elements = xpath.selectNodes(holdingsElement);

			// Loop over the recordId elements and add value of each to the holdings
			for(Element element : elements)
			{
				String value = element.getText();
				String type = element.getAttributeValue("type");

				if(log.isDebugEnabled())
					log.debug("Found a recordId element with a value of " + value + " and a type of " + type + ".");

				holdings.addXcRecordId(type, value);
			} // end loop over recordId elements

			// An XPATH expression to get the manifestationHeld elements
			xpath = XPath.newInstance("./xc:manifestationHeld");
			xpath.addNamespace(XC_NAMESPACE);

			// Get the subfields.
			elements = xpath.selectNodes(holdingsElement);

			// Loop over the manifestationHeld elements and add value of each to the holdings
			for(Element element : elements)
			{
				String value = element.getText();
				String type = element.getAttributeValue("type");

				if(log.isDebugEnabled())
					log.debug("Found a manifestationHeld element with a value of " + value + " and a type of " + type + ".");

				holdings.addManifestationHeld(type, value);
			} // end loop over manifestationHeld elements

			return holdings;
		} // end try
		catch(JDOMException e)
		{
			log.error("An error occurred getting the recordId and manifestationHeld elements from the passed Holdings element.", e);
			return holdings; // don't return null since we set up the IDs
		} // end catch(JDOMException)
	} // end method buildHoldings(Element)

	/**
	 * Builds a Item Object based on the passed element, which is
	 * expected to be an xc:entity of type "item"
	 *
	 * This method adds an up link from the returned item element to
	 * each Holdings element from this record.
	 *
	 * @param itemElement The xc:entity Element to build the item from
	 * @return The Item element
	 */
	@SuppressWarnings("unchecked")
	private Item buildItem(Element itemElement)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Item element from an item component of the record with ID " + recordId + ".");

		// Create a Item Object for the passed work element
		Item item = new Item();
		item.setFormat(xcSchemaFormat);
		item.setService(service); // Mark the Item as being from no service since it shouldn't be output

		try
		{
			item.setXcItemId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_ITEM));

			itemElement.setAttribute("itemID", Long.toString(item.getXcItemId()), XC_NAMESPACE);
			item.setOaiXml(outputter.outputString(((Element)itemElement.getParent().clone())
					                             .setContent(new Text("\n\t"))
					                                         .addContent(((Element)itemElement.clone()))
					                                         .addContent("\n\t")));
			item.setOaiIdentifier("aggItem:" + item.getXcItemId());

			// An XPATH expression to get the holdingsExemplified elements
			XPath xpath = XPath.newInstance("./xc:holdingsExemplified");
			xpath.addNamespace(XC_NAMESPACE);

			// Add an up link from this item to each holdings
			for(Holdings holding : holdings)
				item.addLinkToHoldings(holding);

			// Get the subfields.
			List<Element> elements = xpath.selectNodes(itemElement);

			// Loop over the identifierForTheWork elements and add each to the Work element
			for(Element element : elements)
			{
				// The value of the requested control field
				String value = element.getText();
				String type = element.getAttributeValue("type");

				if(log.isDebugEnabled())
					log.debug("Found a holdingsExemplified element with a value of " + value + " and a type of " + type + ".");

				item.addHoldingsExemplified(type, value);
			} // end loop over holdingsExemplified elements

			return item;
		} // end try
		catch(JDOMException e)
		{
			log.error("An error occurred getting the holdingsExemplified elements from the passed Item element.", e);
			return item; // don't return null since we set up the IDs
		} // end catch(JDOMException)
	} // end method buildItem(Element)
} // end class XcRecordSplitter
