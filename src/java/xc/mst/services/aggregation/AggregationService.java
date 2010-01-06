/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.aggregation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.jdom.Attribute;
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
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Expression;
import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Item;
import xc.mst.bo.record.Manifestation;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.record.DefaultXcIdentifierForFrbrElementDAO;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultExpressionService;
import xc.mst.manager.record.DefaultHoldingsService;
import xc.mst.manager.record.DefaultItemService;
import xc.mst.manager.record.DefaultManifestationService;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.DefaultWorkService;
import xc.mst.manager.record.ExpressionService;
import xc.mst.manager.record.HoldingsService;
import xc.mst.manager.record.ItemService;
import xc.mst.manager.record.ManifestationService;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.record.WorkService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.FormatService;
import xc.mst.services.MetadataService;
import xc.mst.services.ServiceValidationException;
import xc.mst.utils.LogWriter;
import xc.mst.utils.XcRecordSplitter;
import xc.mst.utils.index.ExpressionList;
import xc.mst.utils.index.HoldingsList;
import xc.mst.utils.index.ItemList;
import xc.mst.utils.index.ManifestationList;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.Records;
import xc.mst.utils.index.WorkList;

/**
 * A MetadataService which aggregates data from the different FRBR levels
 * spread throughout multiple records into a single XC schema record.
 * One XC record is output for each unique manifestation element, and the
 * works, expressions, holdings and items associated with it are added to
 * the XC record.
 *
 * @author Eric Osisek
 */
public class AggregationService extends MetadataService
{
	/**
	 * Manager for getting, inserting and updating works
	 */
	private static WorkService workService = new DefaultWorkService();

	/**
	 * Manager for getting, inserting and updating expressions
	 */
	private static ExpressionService expressionService = new DefaultExpressionService();

	/**
	 * Manager for getting, inserting and updating manifestations
	 */
	private static ManifestationService manifestationService = new DefaultManifestationService();

	/**
	 * Manager for getting, inserting and updating holdings
	 */
	private static HoldingsService holdingsService = new DefaultHoldingsService();

	/**
	 * Manager for getting, inserting and updating item
	 */
	private static ItemService itemService = new DefaultItemService();

	/**
	 * The Properties file with information on which fields necessitate merging work elements
	 */
	private Properties workMerge = null;

	/**
	 * The Properties file with information on which fields necessitate merging manifestation elements
	 */
	private Properties manifestationMerge = null;

	/**
	 * Data access object for getting FRBR level IDs
	 */
	protected static XcIdentifierForFrbrElementDAO frbrLevelIdDao = new DefaultXcIdentifierForFrbrElementDAO();
	
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
	 * A Map from an identifierForTheWork value to the Work element containing it.
	 * Used to match works within the same record.
	 */
	//private HashMap<String, Work> workCache = new HashMap<String, Work>();

	/**
	 *  A list of works which should be ignored since they were merged into another work
	 */
	//private HashSet<Work> toIgnore = new HashSet<Work>();

	/**
	 *  A set of Strings representing elements we've added to a merged element.
	 *  This is used to avoid adding duplicate elements.
	 */
	private HashSet<String> cacheForMerging = new HashSet<String>();

	/**
	 * The format ID of the XC schema format
	 */
	private Format xcSchemaFormat = null;
	
	/**
	 * Builds the XML Document based on the XC record's XML
	 */
	private static final SAXBuilder builder = new SAXBuilder();

	/**
	 * Used to convert between jdom Objects and Strings
	 */
	private static final XMLOutputter outputter = new XMLOutputter();
	
	private static RecordService recordService = new DefaultRecordService();

	/**
	 * Construct a NormalizationService Object
	 */
	public AggregationService()
	{
		try 
		{
			FormatService formatService = new DefaultFormatService();
			xcSchemaFormat = formatService.getFormatByName("xc");
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Could not connect to the database with the parameters in the configuration file.", e);
		}
	} // end constructor

	@Override
	public void loadConfiguration(String configuration)
	{	
		String[] configurationLines = configuration.split("\n");
	
		// The Properties file we're currently populating
		Properties current = null;
	
	    for(String line : configurationLines)
	    {
			line = line.trim();
			
	    	// Skip comments and blank lines
	    	if(line.startsWith("#") || line.length() == 0)
	    		continue;
	    	// If the line contains a property, add it to the current Properties Object
	    	else if(line.contains("="))
	    	{
	    		String key = line.substring(0, line.indexOf('=')).trim();
	    		String value = (line.contains("#") ? line.substring(line.indexOf('=')+1, line.indexOf('#')) : line.substring(line.indexOf('=')+1)).trim();
	    		current.setProperty(key, value);
	    	}
	    	// Otherwise check whether the line contains a valid properties heading
	    	else
	    	{
	    		if(line.equals("WORK MERGE FIELDS"))
	    		{
	    			if(workMerge == null)
	    				workMerge = new Properties();
	    			current = workMerge;
	    		}
	    		else if(line.equals("MANIFESTATION MERGE FIELDS"))
	    		{
	    			if(manifestationMerge == null)
	    				manifestationMerge = new Properties();
	    			current = manifestationMerge;
	    		}
	    	}
	    }
	}

	@Override
	protected void validateService() throws ServiceValidationException 
	{
		if(workMerge == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: WORK MERGE FIELDS");
		else if(manifestationMerge == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: MANIFESTATION MERGE FIELDS");		
	}

	@Override
	public void  processRecord(Record processMe) throws Exception {
		// If the record was deleted, delete and reprocess all records that were processed from it
		if(processMe.getDeleted())
		{
			List<Record> successors =  recordService.getSuccessorsCreatedByServiceId(processMe.getId(), service.getId());

			// If there are successors then the record exist and needs to be deleted. Since we are
			// deleting the record, we need to decrement the count.
			if (successors != null && successors.size() > 0) {
				inputRecordCount--;
			}
			
			// Handle reprocessing of successors
			for(Record successor : successors)
			{
				// Set the successors ad deleted
				successor.setDeleted(true);
			
				// Schedule the services
				reprocessRecord(successor);
				recordService.update(successor);
			}
			
			// Mark the record as having been processed by this service
			processMe.addProcessedByService(service);
			processMe.removeInputForService(service);
			recordService.update(processMe);
		
		} 

		// Get the results of processing the record
		List<Record> results = convertRecord(processMe);
		
		boolean updatedInputRecord = false;
		for(Record outgoingRecord : results)
		{
			// Mark the output record as a successor of the input record
			if(!processMe.getSuccessors().contains(outgoingRecord))
				processMe.addSuccessor(outgoingRecord);

			// Mark the input record as a predecessor of the output record
			outgoingRecord.addProcessedFrom(processMe);
			
			// Mark the record as not coming from a provider
			outgoingRecord.setProvider(null);

			// Add all sets the outgoing record belongs to to the service's list of output sets
			for(Set outputSet : outgoingRecord.getSets())
				service.addOutputSet(outputSet);

			if(outputSet != null)
				outgoingRecord.addSet(outputSet);

			// Check whether or not this record already exists in the database
			Record oldRecord = recordService.getByOaiIdentifierAndService(outgoingRecord.getOaiIdentifier(), service.getId());

			// If the current record is a new record, insert it
			if(oldRecord == null) {
				insertNewRecord(outgoingRecord);
			}
			// Otherwise we've seen the record before.  Update it as appropriate
			// If outgoingRecord's deleted flag is set to true, the record will
			// be deleted.
			else {
				updateExistingRecord(outgoingRecord, oldRecord);
				
				// If output record exist then it means that the incoming record is an updated record
				// So we set updatedInputRecord to true. This will be used to determine whether the input
				// record is new record or updated record.
				updatedInputRecord = true;
			}
		} // end loop over processed records
		
		// Mark the input record as done(processed by this service) only when its results are not empty.
		// If results are empty then it means some exception occurred and no output records created
		if (results.size() > 0) { 
			// Mark the record as having been processed by this service
			processMe.addProcessedByService(service);
			processMe.removeInputForService(service);
			recordService.update(processMe);
		} else if (!processMe.getDeleted()) {
			unprocessedErrorRecordIdentifiers.add(processMe.getOaiIdentifier());
		}
		
		// If the input record is a new record then increment the processed record count
		if (!updatedInputRecord  && !processMe.getDeleted() && results.size() > 0) {
			inputRecordCount++;
		}
		
		processedRecordCount++;
	
	}
	
	private List<Record> convertRecord(Record record)
	{
		try
		{
			refreshIndex();
			
			// A list of new or updated records resulting form processing the passed record
			List<Record> results = new ArrayList<Record>();
	
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
	
				return results;
			} // end catch IOException
			catch(JDOMException e)
			{
				log.error("An error occurred while parsing the record's XML.", e);
	
				LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");
	
				return results;
			} // end catch JDOMException
			
			// The OAI identifiers of the records that the record we're processing has uplinks to
			List<String> uplinks = new ArrayList<String>();
			
			// Iterate over each of the components in the passed record, and add them to
			// the correct FRBR component list.
			List<Element> components = xml.getRootElement().getChildren();
			for(Element component : components)
			{
				String level = component.getAttributeValue("type"); // Get the type of the element, which will
				                                                    // be the FRBR level it belongs to
				// Add the component to the appropriate list, parsing out the key fields
				if(level.equals("work") && component.getChildren().size() > 0)
				{	
					Work work = buildWork(component, record);
					results.addAll(processWork(work, xml));
				}
				else if(level.equals("expression") && component.getChildren().size() > 0)
				{
					uplinks = getLinks(xml, "workExpressed");
					Expression expression = buildExpression(component, record);
					results.addAll(processExpression(expression, xml));
				}
				else if(level.equals("manifestation") && component.getChildren().size() > 0)
				{
					uplinks = getLinks(xml, "expressionManifested");
					Manifestation manifestation = buildManifestation(component, record);
					results.addAll(processManifestation(manifestation, xml));
				}
				else if(level.equals("holdings") && component.getChildren().size() > 0)
				{
					uplinks = getLinks(xml, "manifestationHeld");
					Holdings holdings = buildHoldings(component, record);
					results.addAll(processHoldings(holdings, xml));
				}
				else if(level.equals("item") && component.getChildren().size() > 0)
				{
					uplinks = getLinks(xml, "holdingsExemplified");
					Item item = buildItem(component, record);
					results.addAll(processItem(item, xml));
				}
			} // end loop over components
			
			// Add the uplinks from the input as traits on the output
			// indicating that the record the output was processed from
			// had an uplink to those input records
			for(Record outrecord : results)
				for(String uplink : uplinks)
					outrecord.addTrait("inputHasUplink:" + uplink);
			
			// Return the list of FRBR components to be added or updated in the Lucene index
			return results;
		}
		catch(IndexException e)
		{
			log.error("An error occurred connecting to the Solr index.", e);
			
			LogWriter.addWarning(service.getServicesLogFileName(), "An error occurred connecting to the Solr index.");

			return new ArrayList<Record>();
		}
		catch(Exception e)
		{
			log.error("An error occurred while processing the record.", e);
			
			LogWriter.addWarning(service.getServicesLogFileName(), "An internal error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");

			return new ArrayList<Record>();
		}
	} // end method processRecord(Record)

	//###############################################################
	// Methods for creating a FRBR level specific record
	// from a generic record.
	//###############################################################
	
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
	private Work buildWork(Element workElement, Record record)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Work element from the record with ID " + record.getId() + ".");
		
		// Create a Work Object for the passed work element
		Work work = new Work();
		work.setFormat(xcSchemaFormat);
		work.setService(service); // Mark the Work as being from no service since it shouldn't be output
	
		try
		{
			work.setXcWorkId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_WORK));
	
			work.setOaiXml(outputter.outputString(((Element)workElement.getParent().clone())
	                                             .setContent(new Text("\n\t"))
	                                                        .addContent(((Element)workElement.clone()))
	                                                        .addContent("\n\t")));
	
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
	private Expression buildExpression(Element expressionElement, Record record)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Expression element from an expression component of the record with ID " + record.getId() + ".");
		
		// Create an Expression Object for the passed expression element
		Expression expression = new Expression();
		expression.setFormat(xcSchemaFormat);
		expression.setService(service); // Mark the Expression as being from no service since it shouldn't be output
	
		expression.setXcExpressionId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_EXPRESSION));
	
		expression.setOaiXml(outputter.outputString(((Element)expressionElement.getParent().clone())
													               .setContent(new Text("\n\t"))
													   			  .addContent(((Element)expressionElement.clone()))
													   			  .addContent("\n\t")));
	
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
	private Manifestation buildManifestation(Element manifestationElement, Record record)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Manifestation element from a manifestation component of the record with ID " + record.getId() + ".");
	
		// Create a Work Object for the passed work element
		Manifestation manifestation = new Manifestation();
		manifestation.setFormat(xcSchemaFormat);
		manifestation.setService(service); // Mark the Manifestation as being from no service since it shouldn't be output
	
		try
		{
			manifestation.setXcManifestationId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_MANIFESTATION));
	
			manifestation.setOaiXml(outputter.outputString(((Element)manifestationElement.getParent().clone())
		                                                   .setContent(new Text("\n\t"))
		                                                   .addContent(((Element)manifestationElement.clone()))
		                                                   .addContent("\n\t")));
	
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
	
			// An XPATH expression to get the recordId elements
			xpath = XPath.newInstance("./dcterms:identifier");
			xpath.addNamespace(XC_NAMESPACE);
	
			// Get the subfields.
			elements = xpath.selectNodes(manifestationElement);
	
			// Loop over the recordId elements and add value of each to the manifestation
			for(Element element : elements)
			{
				String value = element.getText();
				String type = element.getAttributeValue("type");
	
				if(log.isDebugEnabled())
					log.debug("Found an identifier element with a value of " + value + " and a type of " + type + ".");
	
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
	private Holdings buildHoldings(Element holdingsElement, Record record)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Holdings element from a holdings component of the record with ID " + record.getId() + ".");
	
		// Create a Work Object for the passed work element
		Holdings holdings = new Holdings();
		holdings.setFormat(xcSchemaFormat);
		holdings.setService(service); // Mark the Holdings as being from no service since it shouldn't be output
	
		try
		{
			holdings.setXcHoldingsId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_HOLDINGS));
	
			holdings.setOaiXml(outputter.outputString(((Element)holdingsElement.getParent().clone())
	                                                 .setContent(new Text("\n\t"))
	                                                 .addContent(((Element)holdingsElement.clone()))
	                                                 .addContent("\n\t")));
	
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
	private Item buildItem(Element itemElement, Record record)
	{
		if(log.isDebugEnabled())
			log.debug("Creating a new Item element from an item component of the record with ID " + record.getId() + ".");
		
		// Create a Item Object for the passed work element
		Item item = new Item();
		item.setFormat(xcSchemaFormat);
		item.setService(service); // Mark the Item as being from no service since it shouldn't be output
	
		try
		{
			item.setXcItemId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_ITEM));
	
			item.setOaiXml(outputter.outputString(((Element)itemElement.getParent().clone())
					                             .setContent(new Text("\n\t"))
					                             .addContent(((Element)itemElement.clone()))
					                             .addContent("\n\t")));
	
			// An XPATH expression to get the holdingsExemplified elements
			XPath xpath = XPath.newInstance("./xc:holdingsExemplified");
			xpath.addNamespace(XC_NAMESPACE);
	
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

	//###############################################################
	// Methods for aggregating an XC record with matching records
	// which have been previously processed.
	//###############################################################
	
	/**
	 * Processes the Work elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @param recordxml The XML for the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 * @throws IndexException If an error occurred connecting to the Solr index
	 */
	private List<Record> processWork(Work work, Document recordxml) throws IndexException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Get the id of the unprocessed record
		String oldOaiIdentifier = getFRBRLevelIdentifier(xml);
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		work.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		List<Work> matches = matchWorks(work);

		// A list of work IDs that the current work merged with
		List<Work> newWorks= new ArrayList<Work>();

		// If there were no matches add the work to the list of results
		if(matches.size() == 0)
		{
			// Remove the ID so a new one gets generated
			work.setId(-1);
			
			work.setOaiXml(outputter.outputString(xml));
			
			results.add(work);
		}

		// Iterate over the matches and merge them as appropriate
		for(Work match : matches)
		{
			mergeWorks(match, work);
			newWorks.add(match);
			results.add(match);
		} // end loop over matches
		
		// Get the output records that were processed from records linked to the
		// record we just processed
		RecordService recordService = new DefaultRecordService();
		RecordList linkedToInput = recordService.getByTrait("inputHasUplink:" + oldOaiIdentifier);
		
		// For each output record that were processed from records linked to the
		// record we just processed, add a link from each current processed record to it
		for(Record linked : linkedToInput)
			updateRecord(Expression.buildExpressionFromRecord(addLinkToRecordXml(linked, results, "workExpressed")));
		
		return results;
	} // end method processWork(Work work)

	/**
	 * Processes the Expression elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @param recordxml The XML for the record
	 * @return A list of records that need to be added or updated after processing the works
	 * @throws IndexException 
	 * @throws DatabaseConfigException 
	 */
	private List<Record> processExpression(Expression expression, Document recordxml) throws DatabaseConfigException, IndexException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();
		
		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Get the id of the unprocessed record
		String oldOaiIdentifier = getFRBRLevelIdentifier(xml);
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		expression.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Get the works from the input record set that the
		// expression we're processing was linked to
		List<String> linkedWorks = getLinks(xml, "workExpressed");
		
		// Remove all linked works from the XML since the output record
		// will have different links
		xml = removeLinks(xml, "workExpressed");
		
		// For each work in the input set that the expression was linked to,
		// add a link from the expression we're creating to the works 
		// processed from the work in the input set
		for(String linkedWork : linkedWorks)
		{
			Record inputWorkLinked = recordService.getInputForServiceByOaiIdentifier(linkedWork, service.getId());
			if(inputWorkLinked != null)
			{
				WorkList worksProducedByLinkedWork = workService.getByProcessedFrom(inputWorkLinked);
				
				for(Work linkToMe : worksProducedByLinkedWork)
				{
					xml = addLink(xml, "workExpressed", linkToMe.getOaiIdentifier());
					expression.addLinkToWork(linkToMe);
				}
			}
		}
		
		// Remove the ID so a new one gets generated
		expression.setId(-1);
		
		expression.setOaiXml(outputter.outputString(xml));
		
		// Add the expression to the list of results since its identifiers and linked works 
		// have been updated.  Expressions currently don't have aggregation rules matching
		// them with other expressions or other FRBR levels so no further processing is required.
		results.add(expression);

		// Get the output records that were processed from records linked to the
		// record we just processed
		RecordList linkedToInput = recordService.getByTrait("inputHasUplink:" + oldOaiIdentifier);
		
		// For each output record that were processed from records linked to the
		// record we just processed, add a link from each current processed record to it
		for(Record linked : linkedToInput)
			updateRecord(addLinkToRecordXml(linked, results, "expressionManifested"));
		
		return results;
	} // end method processExpressions(XcRecordSplitter)

	/**
	 * Processes the Manifestation elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param manifestation The manifestation we're processing
	 * @param recordxml The XML of the manifestation we're processing
	 * @return A list of records that need to be added or updated after processing the manifestations
	 * @throws IndexException If an error occurred while connecting to the Solr index
	 * @throws DatabaseConfigException 
	 */
	private List<Record> processManifestation(Manifestation manifestation, Document recordxml) throws IndexException, DatabaseConfigException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Get the id of the unprocessed record
		String oldOaiIdentifier = getFRBRLevelIdentifier(xml);
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		manifestation.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Get the works from the input record set that the
		// expression we're processing was linked to
		List<String> linkedExpressions = getLinks(xml, "expressionManifested");
		
		// Remove all linked works from the XML since the output record
		// will have different links
		xml = removeLinks(xml, "expressionManifested");
		
		// For each work in the input set that the expression was linked to,
		// add a link from the manifestation we're creating to the expressions 
		// processed from the expression in the input set
		for(String linkedExpression : linkedExpressions)
		{
			Record inputExpressionLinked = recordService.getInputForServiceByOaiIdentifier(linkedExpression, service.getId());
			if(inputExpressionLinked != null)
			{
				ExpressionList expressionsProducedByLinkedExpression = expressionService.getByProcessedFrom(inputExpressionLinked);
				
				for(Expression linkToMe : expressionsProducedByLinkedExpression)
				{
					xml = addLink(xml, "expressionManifested", linkToMe.getOaiIdentifier());
					manifestation.addLinkToExpression(linkToMe);
				}
			}
		}
		
		// Remove the ID so a new one gets generated
		manifestation.setId(-1);
		
		manifestation.setOaiXml(outputter.outputString(xml));
		
		List<Manifestation> matches = matchManifestations(manifestation);
		
		// If there were no matches add the manifestation to the list of results
		if(matches.size() == 0)
			results.add(manifestation);

		// Iterate over the matches and merge them as appropriate
		for(Manifestation match : matches)
		{
			mergeManifestations(match, manifestation);
			results.add(match);
		} // end loop over matches
		
		// Get the output records that were processed from records linked to the
		// record we just processed
		RecordService recordService = new DefaultRecordService();
		RecordList linkedToInput = recordService.getByTrait("inputHasUplink:" + oldOaiIdentifier);
		
		// For each output record that were processed from records linked to the
		// record we just processed, add a link from each current processed record to it
		for(Record linked : linkedToInput)
			updateRecord(addLinkToRecordXml(linked, results, "manifestationHeld"));
		
		refreshIndex();
		
		// Check the database for any holdings elements that need to be linked to
		// the  manifestations, and add the links as appropriate
		List<Holdings> holdings = getHoldingsMatchingManifestation(manifestation);
		for(Holdings holdingsElement : holdings)
		{
			addLinkToRecordXml(holdingsElement, results, "manifestationHeld");

			results.add(holdingsElement);
		} // end loop over matched holdings
		
		return results;
	} // end method processManifestations(XcRecordSplitter)

	/**
	 * Processes the Holdings elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 * @throws IndexException 
	 * @throws DatabaseConfigException 
	 */
	private List<Record> processHoldings(Holdings holdings, Document recordxml) throws DatabaseConfigException, IndexException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();
		
		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Get the id of the unprocessed record
		String oldOaiIdentifier = getFRBRLevelIdentifier(xml);
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		holdings.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Get the manifestations from the input record set that the
		// holdings we're processing was linked to
		List<String> linkedManifestations = getLinks(xml, "manifestationHeld");
		
		// Remove all linked works from the XML since the output record
		// will have different links
		xml = removeLinks(xml, "manifestationHeld");
		
		// For each manifestation in the input set that the holdings was linked to,
		// add a link from the holdings we're creating to the manifestations 
		// processed from the manifestation in the input set
		for(String linkedManifestation : linkedManifestations)
		{
			Record inputManifestationLinked = recordService.getInputForServiceByOaiIdentifier(linkedManifestation, service.getId());
			if(inputManifestationLinked != null)
			{
				ManifestationList manifestationsProducedByLinkedManifestation = manifestationService.getByProcessedFrom(inputManifestationLinked);
				
				for(Manifestation linkToMe : manifestationsProducedByLinkedManifestation)
				{
					xml = addLink(xml, "manifestationHeld", linkToMe.getOaiIdentifier());
					holdings.addLinkToManifestation(linkToMe);
				}
			}
		}
		
		refreshIndex();
		
		// Check the database for any manifestation elements that need to be linked to
		// the  holdings, and add the links as appropriate
		List<Manifestation> manifestations = getManifestationsMatchingHoldings(holdings);
		for(Manifestation manifestation : manifestations)
		{
			holdings.addLinkToManifestation(manifestation);
			xml = addLink(xml, "manifestationHeld", manifestation.getOaiIdentifier());
		}
		
		// Remove the ID so a new one gets generated
		holdings.setId(-1);
		
		holdings.setOaiXml(outputter.outputString(xml));
		
		// Add the holdings to the list of records to insert
		results.add(holdings);

		// Check the database for any item elements that need to be linked to
		// the  holdings, and add the links as appropriate
		List<Item> items = getItemsMatchingHoldings(holdings); 
		for(Item item : items)
			updateRecord(addLinkToRecordXml(item, results, "holdingsExemplified"));

		// Get the output records that were processed from records linked to the
		// record we just processed
		RecordList linkedToInput = recordService.getByTrait("inputHasUplink:" + oldOaiIdentifier);
		
		// For each output record that were processed from records linked to the
		// record we just processed, add a link from each current processed record to it
		for(Record linked : linkedToInput)
			updateRecord(addLinkToRecordXml(linked, results, "holdingsExemplified"));
		
		return results;
	} // end method processHoldings(XcRecordSplitter)

	/**
	 * Processes the Expression elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 * @throws IndexException 
	 * @throws DatabaseConfigException 
	 */
	private List<Record> processItem(Item item, Document recordxml) throws DatabaseConfigException, IndexException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		item.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Get the works from the input record set that the
		// expression we're processing was linked to
		List<String> linkedHoldings = getLinks(xml, "holdingsExemplified");
		
		// Remove all linked works from the XML since the output record
		// will have different links
		xml = removeLinks(xml, "holdingsExemplified");
		
		// For each holdings in the input set that the item was linked to,
		// add a link from the item we're creating to the holdings 
		// processed from the holdings in the input set
		for(String linkedHolding : linkedHoldings)
		{
			Record inputHoldingsLinked = recordService.getInputForServiceByOaiIdentifier(linkedHolding, service.getId());
			if(inputHoldingsLinked != null)
			{
				HoldingsList holdingsProducedByLinkedHoldings = holdingsService.getByProcessedFrom(inputHoldingsLinked);
				
				for(Holdings linkToMe : holdingsProducedByLinkedHoldings)
				{
					xml = addLink(xml, "holdingsExemplified", linkToMe.getOaiIdentifier());
					item.addLinkToHoldings(linkToMe);
				}
			}
		}
		
		// Check the database for any manifestation elements that need to be linked to
		// the  holdings, and add the links as appropriate
		List<Holdings> holdingsElements = getHoldingsMatchingItem(item);
		for(Holdings holdings : holdingsElements)
		{
			xml = addLink(xml, "holdingsExemplified", holdings.getOaiIdentifier());
			item.addLinkToHoldings(holdings);
		}
		
		// Remove the ID so a new one gets generated
		item.setId(-1);
		
		item.setOaiXml(outputter.outputString(xml));
		
		// Add the item to the list of records to insert
		results.add(item);

		return results;
	} // end method processItems(XcRecordSplitter)

	//###############################################################
	// Methods for getting records to aggregate with an XC record.
	//###############################################################
	
	/**
	 * Checks the passed Work element against elements in the lucene index for matches.
	 * Returns the matched records.
	 *
	 * @param matchMe The Work to match
	 * @return A list of records matching the passed Work.
	 */
	private List<Work> matchWorks(Work matchMe)
	{
		List<Work> results = new ArrayList<Work>();

		try 
		{
			// If we should match works on the identifierForTheWork field
			if(workMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_IDENTIFIER_FOR_THE_WORK, "0").equals("1"))
			{
				// For each identifierForTheWork, add all matches to that identifierForTheWork to the list of results
				for(String identifierForTheWork : matchMe.getIdentifierForTheWorks())
				{
					WorkList matchedWorks = workService.getByIdentifierForTheWork(identifierForTheWork);
					for(Work matchedWork : matchedWorks)
					{
						logInfo("Merging works with OAI identifiers " + matchedWork.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an identifierForTheWork field with a value of " + identifierForTheWork);
						results.add(matchedWork);
					}
				}
			} // end if (we're configured to match on identifierForTheWork
		} 
		catch(IndexException ie) 
		{
			log.error("Indexing exception occured.", ie);
		}
		return results;
	} // end method matchWorks(Work)

	/**
	 * Checks the passed Manifestation element against elements in the lucene index for matches.
	 * Returns the matched records.
	 *
	 * @param matchMe The Manifestation to match
	 * @return A list of records matching the passed Manifestation.
	 */
	private List<Manifestation> matchManifestations(Manifestation matchMe)
	{
		List<Manifestation> results = new ArrayList<Manifestation>();

		try 
		{
			// For each recordID, add all matches to that recordID to the list of results
			// if we're configured to match on that record ID type
			for(String xcRecordId : matchMe.getXcRecordIds())
			{
				String type = xcRecordId.substring(xcRecordId.indexOf('(')+1, xcRecordId.indexOf(')'));
	
				// If we're configured to match on the type of recordID we're checking,
				// add all matches to that recordID
				if(type.equals("OCoLC"))
				{
					if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_OCOLC, "0").equals("1"))
					{
						ManifestationList matchedManifestiations = manifestationService.getByXcRecordId(xcRecordId);
						for(Manifestation matchedManifestiation : matchedManifestiations)
						{
							logInfo("Merging manifestations with OAI identifiers " + matchedManifestiation.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an xcRecordId field with a value of " + xcRecordId);
							results.add(matchedManifestiation);
						}
					}
				} // end if (recordID is an OCoLC ID)
				else if(type.equals("LCCN"))
				{
					if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_LCCN, "0").equals("1"))
					{
						ManifestationList matchedManifestiations = manifestationService.getByXcRecordId(xcRecordId);
						for(Manifestation matchedManifestiation : matchedManifestiations)
						{
							logInfo("Merging manifestations with OAI identifiers " + matchedManifestiation.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an xcRecordId field with a value of " + xcRecordId);
							results.add(matchedManifestiation);
						}
					}
				} // end if (recordID is an LCCN ID)
				else if(type.equals("ISBN"))
				{
					if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_ISBN, "0").equals("1"))
					{
						ManifestationList matchedManifestiations = manifestationService.getByXcRecordId(xcRecordId);
						for(Manifestation matchedManifestiation : matchedManifestiations)
						{
							logInfo("Merging manifestations with OAI identifiers " + matchedManifestiation.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an xcRecordId field with a value of " + xcRecordId);
							results.add(matchedManifestiation);
						}
					}
				} // end if (recordID is an ISBN ID)
				else if(type.equals("ISSN"))
				{
					if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_ISSN, "0").equals("1"))
					{
						ManifestationList matchedManifestiations = manifestationService.getByXcRecordId(xcRecordId);
						for(Manifestation matchedManifestiation : matchedManifestiations)
						{
							logInfo("Merging manifestations with OAI identifiers " + matchedManifestiation.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an xcRecordId field with a value of " + xcRecordId);
							results.add(matchedManifestiation);
						}
					}
				} // end if (recordID is an ISSN ID)
				else
				{
					if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_RECORD_ID, "0").equals("1"))
					{
						ManifestationList matchedManifestiations = manifestationService.getByXcRecordId(xcRecordId);
						for(Manifestation matchedManifestiation : matchedManifestiations)
						{
							logInfo("Merging manifestations with OAI identifiers " + matchedManifestiation.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an xcRecordId field with a value of " + xcRecordId);
							results.add(matchedManifestiation);
						}
					}
				} // end if (recordID is an unrecognized ID)
			} // end loop over recordID elements
		} 
		catch (IndexException ie) 
		{
			log.error("Index exception occured.", ie);
		}
		return results;
	} // end method matchManifestations(Manifestation)

	/**
	 * Checks the passed Manifestation element against elements in the lucene index for
	 * holdings that match. Returns the matched holdings.
	 *
	 * @param matchMe The Manifestation to match
	 * @return A list of holdings elements matching the passed Manifestation.
	 */
	private List<Holdings> getHoldingsMatchingManifestation(Manifestation matchMe)
	{
		List<Holdings> results = new ArrayList<Holdings>();

		try 
		{
			// For each record ID a holdings could match on
			// add all holdings that match on it
			for(String xcRecordId : matchMe.getXcRecordIds())
			{
				HoldingsList matchedHoldings = holdingsService.getByManifestationHeld(xcRecordId);
				for(Holdings matchedHolding : matchedHoldings)
				{
					logInfo("Linking the manifestation with OAI identifier " + matchMe.getOaiIdentifier() + " with the holdings with OAI identifier " + matchedHolding.getOaiIdentifier() + " because the manifestation's xcRecordId field had the same value as the holdings's manifestationHeld field.  This value was " + xcRecordId);
					results.add(matchedHolding);
				}
			}
		} 
		catch (IndexException ie) 
		{
			log.error("Index exception occured.", ie);
		}
		return results;
	} // end method getHoldingsMatchingManifestation(Manifestation)

	/**
	 * Checks the passed Holdings element against elements in the lucene index for
	 * manifestations that match. Returns the matched manifestations.
	 *
	 * @param matchMe The Holdings to match
	 * @return A list of manifestation elements matching the passed Holdings.
	 */
	private List<Manifestation> getManifestationsMatchingHoldings(Holdings matchMe)
	{
		List<Manifestation> results = new ArrayList<Manifestation>();
		try 
		{
			// For each manifestationHeld a manifestation could match on
			// add all manifestations that match on it
			for(String manifestationHeld : matchMe.getManifestationsHeld())
			{
				ManifestationList matchedManifestiations = manifestationService.getByXcRecordId(manifestationHeld);
				for(Manifestation matchedManifestiation : matchedManifestiations)
				{
					logInfo("Linking the manifestation with OAI identifier " + matchedManifestiation.getOaiIdentifier() + " and the holdings with OAI identifier " + matchMe.getOaiIdentifier() + " because the manifestation's xcRecordId field had the same value as the holdings's manifestationHeld field.  This value was " + manifestationHeld);
					results.add(matchedManifestiation);
				}
			}
		} 
		catch (IndexException ie) 
		{
			log.error("Index exception occured.", ie);
		}
		return results;
	} // end method getManifestationsMatchingHoldings(Holdings)

	/**
	 * Checks the passed Holdings element against elements in the lucene index for
	 * items that match. Returns the matched items.
	 *
	 * @param matchMe The Holdings to match
	 * @return A list of item elements matching the passed Holdings.
	 */
	private List<Item> getItemsMatchingHoldings(Holdings matchMe)
	{
		List<Item> results = new ArrayList<Item>();
		
		try 
		{
			// For each record ID a holdings could match on
			// add all holdings that match on it
			for(String xcRecordId : matchMe.getXcRecordIds())
			{
				ItemList matchedItems = itemService.getByHoldingsExemplified(xcRecordId);
				for(Item matchedItem : matchedItems)
				{
					logInfo("Linking the holdings with OAI identifier " + matchMe.getOaiIdentifier() + " with the item with OAI identifier " + matchedItem.getOaiIdentifier() + " because the holdings's xcRecordId field had the same value as the item's holdingsExemplified field.  This value was " + xcRecordId);
					results.add(matchedItem);
				}
			}
		} 
		catch (IndexException ie) 
		{
			log.error("Index exception occured.", ie);
		}
		
		return results;
	} // end method getItemsMatchingHoldings(Holdings)

	/**
	 * Checks the passed Item element against elements in the lucene index for
	 * holdings that match. Returns the matched holdings.
	 *
	 * @param matchMe The Item to match
	 * @return A list of holdings elements matching the passed Item.
	 */
	private List<Holdings> getHoldingsMatchingItem(Item matchMe)
	{
		List<Holdings> results = new ArrayList<Holdings>();
		
		try 
		{
			// For each record ID a holdings could match on
			// add all holdings that match on it
			for(String holdingsExemplified : matchMe.getHoldingsExemplified())
			{
				HoldingsList matchedHoldings = holdingsService.getByXcRecordId(holdingsExemplified);
				for(Holdings matchedHolding : matchedHoldings)
				{
					logInfo("Linking the holdings with OAI identifier " + matchedHolding.getOaiIdentifier() + " and the item with OAI identifier " + matchMe.getOaiIdentifier() + " because the holdings's xcRecordId field had the same value as the item's holdingsExemplified field.  This value was " + holdingsExemplified);
					results.add(matchedHolding);
				}
			}
		} 
		catch (IndexException ie) 
		{
			log.error("Index exception occured.", ie);
		}
		
		return results;
	} // end method getHoldingsMatchingItem(Item)

	//###############################################################
	// Methods for merging two records with the same FRBR level.
	//###############################################################
	
	/**
	 * Merges two work elements
	 *
	 * @param base The work element which will have the other work merged into it
	 * @param mergeIntoBase The work element which will merge into the other work
	 * @return The merged work element.  It's work ID will be the base's work ID.
	 */
	@SuppressWarnings("unchecked")
	private Work mergeWorks(Work base, Work mergeIntoBase)
	{
		cacheForMerging.clear();
	
		// The XML for the XC record
		Document baseXml = null;
		Document mergeIntoBaseXml = null;
	
		// Parse the XML from the record
		try
		{
			if(log.isDebugEnabled())
				log.debug("Parsing the record's XML into a Document Object.");
	
			baseXml = builder.build(new InputSource(new StringReader(base.getOaiXml())));
			mergeIntoBaseXml = builder.build(new InputSource(new StringReader(mergeIntoBase.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			log.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch IOException
		catch(JDOMException e)
		{
			log.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch JDOMException
	
		// Add each element in the base to the cache of used elements
		List<Element> baseElements = baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren();
		for(Element baseElement : baseElements)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(baseElement.getName()).append("|").append(baseElement.getText());
	
			List<Attribute> atts = baseElement.getAttributes();
			for(Attribute att : atts)
				builder.append("|").append(att.getName()).append("|").append(att.getValue());
	
			cacheForMerging.add(builder.toString());
		} // end loop over base record's elements
	
		// Add each element in the base to the cache of used elements
		List<Element> mergeIntoElements = mergeIntoBaseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren();
		for(Element mergeIntoElement : mergeIntoElements)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(mergeIntoElement.getName()).append("|").append(mergeIntoElement.getText());
	
			List<Attribute> atts = mergeIntoElement.getAttributes();
			for(Attribute att : atts)
				builder.append("|").append(att.getName()).append("|").append(att.getValue());
	
			// Add the element to the base if doing so wouldn't cause a duplicate
			if(!cacheForMerging.contains(builder.toString()))
			{
				baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE)
				                                 .addContent("\t")
				                        		 .addContent((Element)mergeIntoElement.clone())
				                        		 .addContent("\n\t");
				cacheForMerging.add(builder.toString());
			} // end if (the element isn't a duplicate of something in the base)
		} // end loop over base record's elements
	
		base.setOaiXml(outputter.outputString(baseXml));
	
		return base;
	} // end method mergeWorks(Work, Work)

	/**
	 * Merges two manifestation elements
	 *
	 * @param base The manifestation element which will have the other manifestation merged into it
	 * @param mergeIntoBase The manifestation element which will merge into the other manifestation
	 * @return The merged manifestation element.  It's manifestation ID will be the base's manifestation ID.
	 */
	@SuppressWarnings("unchecked")
	private Manifestation mergeManifestations(Manifestation base, Manifestation mergeIntoBase)
	{
		cacheForMerging.clear();
	
		// The XML for the XC record
		Document baseXml = null;
		Document mergeIntoBaseXml = null;
	
		// Parse the XML from the record
		try
		{
			if(log.isDebugEnabled())
				log.debug("Parsing the record's XML into a Document Object.");
	
			baseXml = builder.build(new InputSource(new StringReader(base.getOaiXml())));
			mergeIntoBaseXml = builder.build(new InputSource(new StringReader(mergeIntoBase.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			log.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch IOException
		catch(JDOMException e)
		{
			log.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch JDOMException
	
		// Add each element in the base to the cache of used elements
		List<Element> baseElements = baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren();
		for(Element baseElement : baseElements)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(baseElement.getName()).append("|").append(baseElement.getText());
	
			List<Attribute> atts = baseElement.getAttributes();
			for(Attribute att : atts)
				builder.append("|").append(att.getName()).append("|").append(att.getValue());
	
			cacheForMerging.add(builder.toString());
		} // end loop over base record's elements
	
		// Add each element in the base to the cache of used elements
		List<Element> mergeIntoElements = mergeIntoBaseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren();
		for(Element mergeIntoElement : mergeIntoElements)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(mergeIntoElement.getName()).append("|").append(mergeIntoElement.getText());
	
			List<Attribute> atts = mergeIntoElement.getAttributes();
			for(Attribute att : atts)
				builder.append("|").append(att.getName()).append("|").append(att.getValue());
	
			// Add the element to the base if doing so wouldn't cause a duplicate
			if(!cacheForMerging.contains(builder.toString()))
			{
				baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE)
				                                 .addContent("\t")
				                        		 .addContent((Element)mergeIntoElement.clone())
				                        		 .addContent("\n\t");
				cacheForMerging.add(builder.toString());
			} // end if (the element isn't a duplicate of something in the base)
		} // end loop over base record's elements
	
		base.setOaiXml(outputter.outputString(baseXml));
	
		return base;
	} // end method mergeManifestations(Manifestation, Manifestation)

	//###############################################################
	// Methods for getting and setting IDs and links on XC records
	//###############################################################
	
	/**
	 * Gets the ID attribute of the passed XC record
	 * 
	 * @param xml The XC record's XML
	 * @return The XC record's ID attribute
	 */
	@SuppressWarnings("unchecked")
	private String getFRBRLevelIdentifier(Document xml)
	{
		XPath xpath;
		try 
		{
			xpath = XPath.newInstance("//xc:entity/@id");

			xpath.addNamespace("xc", "http://www.extensiblecatalog.info/Elements");
			List<Attribute> atts = xpath.selectNodes(xml);

			for(Attribute att : atts)
				return att.getValue();
			
			return "";
		} 
		catch (JDOMException e) 
		{
			return "";
		}
	} // end method getFRBRLevelIdentifier(Document)

	/**
	 * Gets the ID attribute of the passed XC record
	 * 
	 * @param xml The XC record's XML
	 * @return The XC record's ID attribute
	 */
	@SuppressWarnings("unchecked")
	private void setFRBRLevelIdentifier(Document xml, String identifier)
	{
		XPath xpath;
		try 
		{
			xpath = XPath.newInstance("//xc:entity/@id");

			xpath.addNamespace("xc", "http://www.extensiblecatalog.info/Elements");
			List<Attribute> atts = xpath.selectNodes(xml);

			for(Attribute att : atts)
				att.setValue(identifier);
		} 
		catch (JDOMException e) 
		{
		}
	} // end method getFRBRLevelIdentifier(Document)
	
	/**
	 * Adds a link from one record to another in the record's XML
	 * 
	 * @param from The record to add the link from
	 * @param to The record to link to
	 * @param linkToAdd The name of the link field to add
	 * @return The from record after the link has been added
	 */
	private Record addLinkToRecordXml(Record from, List<Record> to, String linkToAdd) 
	{
		// The XML for the XC record
		Document xml = null;

		// Parse the XML from the record
		try
		{
			if(log.isDebugEnabled())
				log.debug("Parsing the record's XML into a Document Object.");

			xml = builder.build(new InputSource(new StringReader(from.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			log.error("An error occurred while parsing the record's XML.", e);

			LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + from.getOaiIdentifier() + ".");

			return from;
		} // end catch IOException
		catch(JDOMException e)
		{
			log.error("An error occurred while parsing the record's XML.", e);

			LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + from.getOaiIdentifier() + ".");

			return from;
		} // end catch JDOMException
		
		List<String> currentLinks = getLinks(xml, linkToAdd);
		
		for(Record linkTo : to)
		{
			if(!currentLinks.contains(linkTo.getOaiIdentifier()))
			{
				xml = addLink(xml, linkToAdd, linkTo.getOaiIdentifier());
				
				// TODO SHARMILA commented it out to avoid error since upLink is changed to String.
//				from.addUpLink(linkTo);
				currentLinks.add(linkTo.getOaiIdentifier());
			}
		}
		
		from.setOaiXml(outputter.outputString(xml));
		
		return from;
	}

	/**
	 * Gets the ID attribute of the passed XC record
	 * 
	 * @param xml The XC record's XML
	 * @return The XC record's ID attribute
	 */
	@SuppressWarnings("unchecked")
	private Document addLink(Document xml, String linkType, String linkContent)
	{
		if(getLinks(xml, linkType).contains(linkContent)) // Don't add a duplicate link
			return xml;
		
		Element newLink = new Element(linkType, XC_NAMESPACE);
		newLink.setText(linkContent);
		
		xml.getRootElement().getChild("entity", XC_NAMESPACE).addContent("\t\t").addContent(newLink).addContent("\n");
		
		return xml;
	} // end method getFRBRLevelIdentifier(Document)

	/**
	 * Gets the ID attribute of the passed XC record
	 * 
	 * @param xml The XC record's XML
	 * @return The XC record's ID attribute
	 */
	@SuppressWarnings("unchecked")
	private List<String> getLinks(Document xml, String linkType)
	{
		XPath xpath;
		List<String> results = new ArrayList<String>();
		
		try 
		{
			xpath = XPath.newInstance("//xc:" + linkType);

			xpath.addNamespace("xc", "http://www.extensiblecatalog.info/Elements");
			List<Element> elements = xpath.selectNodes(xml);

			for(Element element : elements)
				results.add(element.getText());
			
			return results;
		} 
		catch (JDOMException e) 
		{
			return results;
		}
	} // end method getFRBRLevelIdentifier(Document)
	
	/**
	 * Gets the ID attribute of the passed XC record
	 * 
	 * @param xml The XC record's XML
	 * @return The XC record's ID attribute
	 */
	@SuppressWarnings("unchecked")
	private Document removeLinks(Document xml, String linkType)
	{
		XPath xpath;
		
		try 
		{
			xpath = XPath.newInstance("//xc:" + linkType);

			xpath.addNamespace(XC_NAMESPACE);
			List<Element> elements = xpath.selectNodes(xml);

			for(Element element : elements)
				xml.getRootElement().getChild("entity", XC_NAMESPACE).removeContent(element);
			
			return xml;
		} 
		catch (JDOMException e) 
		{
			return xml;
		}
	} // end method getFRBRLevelIdentifier(Document)


	//###############################################################
	// Methods for building pyramids based on a Work.
	// Since the Aggregation service now produces records with only
	// one FRBR level these are no longer needed.
	//###############################################################
	
	/*
	 * This method parses the frbrComponent into a JDOM Element and adds
	 * the xc:entity of the frbrComponent to the xcRecord.  It then returns
	 * the modified xcRecord.
	 *
	 * @param xcRecord The XC record to which the component should be added
	 * @param frbrComponent The FRBR component to add (the XML from a Work, Expression, Manifestation, Holdings, or Item Object)
	 * @return The XC record with the passed FRBR component added
	 */
	/*
	private Element addFrbrComponantToXcRecord(Element xcRecord, String frbrComponent)
	{
		// The XML for the FRBR component
		Document frbr = null;
	
		// Parse the XML from the FRBR component
		try
		{
			if(log.isDebugEnabled())
				log.debug("Parsing the record's XML into a Document Object.");
	
			frbr = builder.build(new InputSource(new StringReader(frbrComponent)));
		} // end try
		catch(IOException e)
		{
			log.error("An error occurred while parsing the frbr component's XML.", e);
	
			return xcRecord;
		} // end catch IOException
		catch(JDOMException e)
		{
			log.error("An error occurred while parsing the frbr component's XML.", e);
	
			return xcRecord;
		} // end catch JDOMException
	
		// Get the entity from the FRBR component
		Element component = frbr.getRootElement().getChild("entity", XC_NAMESPACE);
	
		// Add the entity to the XC record
		xcRecord.addContent("\n\t").addContent((Element)component.clone());
	
		// Return the modified XC record
		return xcRecord;
	} // end method addFrbrComponantToXcRecord(Element, String)
	*/
	
	/*
	 * Given a work, creates a complete XC record for that work. Up links
	 * are used to combine the correct elements in all five FRBR levels.
	 * The returned records will have processedFrom links to all FRBR
	 * elements it was created from.
	 *
	 * @param work The work to create a Record for
	 * @return A Record with information in all FRBR levels linked to the passed work
	 */
	/*
	private Record buildRecordFromWork(Work work)
	{
		// Lists of the various FRBR level elements
		List<Expression> expressions = new ArrayList<Expression>();
		List<Manifestation> manifestations = new ArrayList<Manifestation>();
		List<Holdings> holdings = new ArrayList<Holdings>();
		List<Item> items = new ArrayList<Item>();
	
		try {
			// Add the Expressions for the passed work
			for(Expression expression : expressionService.getByLinkedWork(work))
			{
				expressions.add(expression);
	
				// Add the Manifestations for the current expression
				for(Manifestation manifestation : manifestationService.getByLinkedExpression(expression))
				{
					manifestations.add(manifestation);
	
					// Add the Holdings for the current manifestation to the list of Holdings elements
					for(Holdings holdingsElement : holdingsService.getByLinkedManifestation(manifestation))
					{
						holdings.add(holdingsElement);
	
						// Add the Items associated with the current Holdings element to the list of Items
						for(Item item : itemService.getByLinkedHoldings(holdingsElement))
							items.add(item);
					} // end loop over Holdings
				} // end loop over Manifestations
			} // end loop over expressions
		} catch (IndexException ie) {
			log.error("Index exception occured.", ie);
		}
		
		// If there were no manifestations, don't return a record
		if(manifestations.size() == 0)
			return null;
	
		// Setup the result record
		Record record = new Record();
		record.setService(service);
		record.setFormat(xcSchemaFormat);
	
		record.setOaiIdentifier(getNextOaiId());
	
		// Set the datestamp, and header to null so they get computed when we insert the normalized record
		record.setOaiDatestamp(null);
		record.setOaiHeader(null);
	
		// Build the record XML from the FRBR elements.
	
		// The root element for the Record
		Element xcRootElement = (new Element("frbr", XC_NAMESPACE));
	
		// Add the work to the XC record
		addFrbrComponantToXcRecord(xcRootElement, work.getOaiXml());
	
		record.addProcessedFrom(work);
	
		// Add the expressions to the XC record
		for(Expression expression : expressions)
		{
			addFrbrComponantToXcRecord(xcRootElement, expression.getOaiXml());
	
			record.addProcessedFrom(expression);
		} // end loop over expressions
	
		// Add the manifestations to the XC record
		for(Manifestation manifestation : manifestations)
		{
			addFrbrComponantToXcRecord(xcRootElement, manifestation.getOaiXml());
	
			record.addProcessedFrom(manifestation);
		} // end loop over manifestations
	
		// Add the holdings to the XC record
		for(Holdings holdingsElement : holdings)
		{
			addFrbrComponantToXcRecord(xcRootElement, holdingsElement.getOaiXml());
	
			record.addProcessedFrom(holdingsElement);
		} // end loop over holdings
	
		// Add the items to the XC record
		for(Item item : items)
		{
			addFrbrComponantToXcRecord(xcRootElement, item.getOaiXml());
	
			record.addProcessedFrom(item);
		} // end loop over items
	
		xcRootElement.addContent("\n");
	
		// Add the namespaces to the element
		xcRootElement.addNamespaceDeclaration(XSI_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(RDVOCAB_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(DCTERMS_NAMESPACE);
		xcRootElement.addNamespaceDeclaration(RDAROLE_NAMESPACE);
	
		record.setOaiXml(outputter.outputString(xcRootElement));
	
		// Add the record to the list of results
		return record;
	} // end method buildRecordFromWork(Work)
	*/
	
	/*
	 * Checks the list of Work elements for matches and returns the same list with the matched
	 * records merged.
	 *
	 * @param works A list of work elements
	 * @return The same list with the matched records removed
	 */
	/*
	private List<Work> mergeWorksInList(List<Work> works)
	{
		workCache.clear(); // reset the cached list of works
		toIgnore.clear(); // reset the list of ignored works
	
		// If we should match works on the identifierForTheWork field
		if(workMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_IDENTIFIER_FOR_THE_WORK, "0").equals("1"))
		{
			// Loop over the works, checking for multiple works with the same identifierForTheWork element.
			for(Work matchMe : works)
			{
				// For each identifierForTheWork, add all matches to that identifierForTheWork to the list of results
				for(String identifierForTheWork : matchMe.getIdentifierForTheWorks())
				{
					if(workCache.containsKey(identifierForTheWork))
					{
						Work matched = workCache.get(identifierForTheWork);
						
						logInfo("Merging works with OAI identifiers " + matched.getOaiIdentifier() + " and " + matchMe.getOaiIdentifier() + " because they both contained an identifierForTheWork field with a value of " + identifierForTheWork);
						
						matched = mergeWorks(matched, matchMe);
						toIgnore.add(matchMe);
					} // end if (identifierForTheWork matches another work
					else
						workCache.put(identifierForTheWork, matchMe);
				} // end loop over identifierForTheWork elements
			} // end loop over works
		} // end if (we're configured to match on identifierForTheWork)
	
		// Remove all works which were merged into other works from the list we're returning
		for(Work removeMe : toIgnore)
			works.remove(removeMe);
	
		return works;
	} // end method mergeWorksInList
	 */

	
	public void setInputRecordCount(int inputRecordCount) {
		this.inputRecordCount = inputRecordCount;
	}

} // end class AggregationService
