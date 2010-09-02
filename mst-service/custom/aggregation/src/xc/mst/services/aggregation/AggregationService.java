/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
import java.util.StringTokenizer;

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
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.FormatService;
import xc.mst.services.ServiceValidationException;
import xc.mst.services.aggregation.bo.HeldRecord;
import xc.mst.services.aggregation.bo.MatchIdentifiers;
import xc.mst.services.aggregation.bo.OutputRecord;
import xc.mst.services.aggregation.dao.DefaultHeldRecordDAO;
import xc.mst.services.aggregation.dao.DefaultMatchIdentifierDAO;
import xc.mst.services.aggregation.dao.DefaultOutputRecordDAO;
import xc.mst.services.aggregation.dao.HeldRecordDAO;
import xc.mst.services.aggregation.dao.MatchIdentifierDAO;
import xc.mst.services.aggregation.dao.OutputRecordDAO;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.utils.LogWriter;
import xc.mst.utils.XcRecordSplitter;
import xc.mst.utils.index.HoldingsList;
import xc.mst.utils.index.ItemList;
import xc.mst.utils.index.ManifestationList;
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
public class AggregationService extends GenericMetadataService
{
	/**
	 * DAO to manage held record
	 */
	private HeldRecordDAO heldRecordDAO = new DefaultHeldRecordDAO();
	
	/**
	 * The Properties file with information on which fields necessitate merging work elements
	 */
	private Properties workMerge = null;

	/**
	 * The Properties file with information on which fields necessitate merging manifestation elements
	 */
	private Properties manifestationMerge = null;
	
	/**
	 * The Properties file with information on which determines the base record to merge into 
	 */
	private Properties manifestationMergeBase = null;
	
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
			LOG.error("Could not connect to the database with the parameters in the configuration file.", e);
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
	    		else if(line.equals("MANIFESTATION BASE RECORD TO MERGE INTO"))
	    		{
	    			if(manifestationMergeBase == null)
	    				manifestationMergeBase = new Properties();
	    			current = manifestationMergeBase;
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
			List<Record> successors =  getRecordService().getSuccessorsCreatedByServiceId(processMe.getId(), service.getId());

			// If there are successors then the record exist and needs to be deleted. Since we are
			// deleting the record, we need to decrement the count.
			if (successors != null && successors.size() > 0) {
				inputRecordCount--;
			}
			
			// Handle reprocessing of successors
			for(Record successor : successors)
			{
				// Set the successors as deleted
				successor.setDeleted(true);
			
				// Schedule the services
				reprocessRecord(successor);
				getRecordService().update(successor);
			}
			
			// Mark the record as having been processed by this service
			processMe.addProcessedByService(service);
			processMe.removeInputForService(service);
			getRecordService().update(processMe);
		
		} 

		// Get the results of processing the record
		List<Record> results = convertRecord(processMe);
		
		boolean updatedInputRecord = false;
		
		// If results is not null, then record is processed. If results is null, then the record is held 
		if (results != null) {
			for(Record outgoingRecord : results)
			{
				// Mark the output record as a successor of the input record
				if(!processMe.getSuccessors().contains(outgoingRecord))
					processMe.addSuccessor(outgoingRecord);
	
				// Mark the input record as a predecessor of the output record
				outgoingRecord.addProcessedFrom(processMe);
				
				// Mark the record as not coming from a provider
				outgoingRecord.setProvider(null);
				
				outgoingRecord.setFormat(xcSchemaFormat);
	
				// Add all sets the outgoing record belongs to to the service's list of output sets
				for(Set outputSet : outgoingRecord.getSets())
					service.addOutputSet(outputSet);
	
				if(outputSet != null)
					outgoingRecord.addSet(outputSet);
	
				// Check whether or not this record already exists in the database
				Record oldRecord = getRecordService().getByOaiIdentifierAndService(outgoingRecord.getOaiIdentifier(), service.getId());
	
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
				getRecordService().update(processMe);
			} else if (!processMe.getDeleted()) {
				unprocessedErrorRecordIdentifiers.add(processMe.getOaiIdentifier());
			}

		} else { // If results = null then it means the record is held.
			// Mark the record as having been processed by this service
			processMe.addProcessedByService(service);
			processMe.removeInputForService(service);
			getRecordService().update(processMe);
		}
		
//		// If the input record is a new record then increment the processed record count
//		if (!updatedInputRecord  && !processMe.getDeleted() && results.size() > 0) {
//			inputRecordCount++;
//		}
		
		processedRecordCount++;
	
	}
	
	private List<Record> convertRecord(Record record)
	{
		try
		{
			
			// A list of new or updated records resulting form processing the passed record
			List<Record> results = new ArrayList<Record>();
	
			// The XML for the XC record
			Document xml = null;
	
			// Parse the XML from the record
			try
			{
				if(LOG.isDebugEnabled())
					LOG.debug("Parsing the record's XML into a Document Object.");
	
				xml = builder.build(new InputSource(new StringReader(record.getOaiXml())));
			} // end try
			catch(IOException e)
			{
				LOG.error("An error occurred while parsing the record's XML.", e);
	
				LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");
	
				return results;
			} // end catch IOException
			catch(JDOMException e)
			{
				LOG.error("An error occurred while parsing the record's XML.", e);
	
				LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");
	
				return results;
			} // end catch JDOMException
			
			// The OAI identifiers of the records that the record we're processing has uplinks to
			List<String> uplinks = new ArrayList<String>();
			
			
			// Get any records which were processed from the record we're processing
			// If there are any (there should be at most 1) we need to delete them
			List<Record> existingRecords = getRecordService().getSuccessorsCreatedByServiceId(record.getId(), service.getId());

			boolean updatedInputRecord = false;
			
			// If the record was deleted, delete and reprocess all records that were processed from it
			if(record.getDeleted())
			{

				// If there are successors then the record exist and needs to be deleted. Since we are
				// deleting the record, we need to decrement the count.
				if (existingRecords != null && existingRecords.size() > 0) {
					inputRecordCount--;
					deleteSuccessorRecords(record, existingRecords);
				}
				
				// Mark the record as having been processed by this service
				record.addProcessedByService(service);
				record.removeInputForService(service);
				getRecordService().update(record);
				
				return new ArrayList<Record>();
			
			} 		

			// If there was already a processed record for the record we just processed, delete it
			if(existingRecords.size() > 0)
			{
				updatedInputRecord = true;

				if(LOG.isDebugEnabled())
					LOG.debug("Updating the record which was processed from an older version of the record we just processed.");
				
				deleteSuccessorRecords(record, existingRecords);

			}

			
			if (record.getType().equals("XC-Work")) {
				results = processWork(record, xml);
			} else if (record.getType().equals("XC-Expression")) {
				results = processExpression(record, xml);
			} else if (record.getType().equals("XC-Manifestation")) {
				buildManifestation(xml, record);
				results = processManifestation(record, xml);
			} else if (record.getType().equals("XC-Holding")) {
				results = processHoldings(record, xml);
			} else { // If type is not available then parse the XML and find the type
				
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
						record.setType("XC-Work");
						results = processWork(record, xml);
					}
					else if(level.equals("expression") && component.getChildren().size() > 0)
					{
						if (record.getUpLinks() == null) {
							uplinks = getLinks(xml, "workExpressed");
							record.setUpLinks(uplinks);
						}
						
						results = processExpression(record, xml);
						break;
					}
					else if(level.equals("manifestation") && component.getChildren().size() > 0)
					{
						if (record.getUpLinks() == null) {
							uplinks = getLinks(xml, "expressionManifested");
							record.setUpLinks(uplinks);
						}
						
						buildManifestation(xml, record);
						results = processManifestation(record, xml);
							
						break;
					}
					else if(level.equals("holdings") && component.getChildren().size() > 0)
					{
						if (record.getUpLinks() == null) {
							uplinks = getLinks(xml, "manifestationHeld");
							record.setUpLinks(uplinks);
						}
						
						results = processHoldings(record, xml);	
						break;
					}
				} // end loop over components
			}
			
			// Return the list of FRBR components to be added or updated in the Lucene index
			return results;
		}
		catch(IndexException e)
		{
			LOG.error("An error occurred connecting to the Solr index.", e);
			
			LogWriter.addWarning(service.getServicesLogFileName(), "An error occurred connecting to the Solr index.");

			return new ArrayList<Record>();
		}
		catch(Exception e)
		{
			LOG.error("An error occurred while processing the record.", e);
			
			LogWriter.addWarning(service.getServicesLogFileName(), "An internal error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");

			return new ArrayList<Record>();
		}
	} // end method processRecord(Record)

	/*
	 * Delete the successor records for incoming updated/deleted record
	 */
	private void deleteSuccessorRecords(Record record, List<Record> successors) {
		
		OutputRecordDAO outputRecordDAO = new DefaultOutputRecordDAO(); 
		
		RecordService recordService = new DefaultRecordService();
		
		// Delete all the successor
		for (Record successor: successors) {
			try {
				// Delete the records that has uplink to the successor record to be deleted
				for(String uplink : successor.getUpLinks()) {
					List<String> linkedOaiIds = outputRecordDAO.getByUplink(uplink);
					
					// Delete linked records
					for (String linkedOaiId: linkedOaiIds) {
						deleteRecord(linkedOaiId);
					}
				}
				// Remove successor from input record
				record.removeSucessor(successor);
				
				recordService.update(record);
				
				// Delete the successor
				outputRecordDAO.deleteByOAIId(successor.getOaiIdentifier());
				
			} catch(DataException de) {
				LOG.error("Data exception occured while deleting the successor record " + successor, de);
			} catch(IndexException ie) {
				LOG.error("Index exception occured while updating the record " + record, ie);
			}
			
		}
		
	}
	
	/*
	 * Deletes the output record
	 */
	private void deleteRecord(String oaiId) {
		OutputRecordDAO outputRecordDAO = new DefaultOutputRecordDAO(); 
		
		try {
			OutputRecord outputRecord = outputRecordDAO.getByOaiId(oaiId);
			
			RecordService recordService = new DefaultRecordService();
			
			// Remove this record from its predecessor
			for (String predecessorOaiId: outputRecord.getPredecessorOaiIds()) {
				Record predecessor = recordService.getByOaiIdentifier(predecessorOaiId);
				predecessor.removeSucessor(recordService.getByOaiIdentifier(oaiId));
				recordService.update(predecessor);
			}
			
			List<String> linkedOaiIds = outputRecordDAO.getByUplink(outputRecord.getOaiId());
	
			// Delete the records that has uplink to the this record to be deleted
			for(String linkedOaiId : linkedOaiIds) {
					deleteRecord(linkedOaiId);
			}
			
			// Delete the record
			outputRecordDAO.deleteByOAIId(oaiId);
			
		} catch (DataException de) {
			LOG.error("Data exception occured while deleting the record with OAI id " + oaiId, de);
		} catch (IndexException ie) {
			LOG.error("Index exception occured while updating the predecessor record " , ie);
		}
	}
	
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
		if(LOG.isDebugEnabled())
			LOG.debug("Creating a new Work element from the record with ID " + record.getId() + ".");
		
		// Create a Work Object for the passed work element
		Work work = new Work();
		work.setFormat(xcSchemaFormat);
		work.setService(service); // Mark the Work as being from no service since it shouldn't be output
	
		try
		{
			work.setXcWorkId(getXcIdentifierForFrbrElementDAO().getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_WORK));
	
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
	
				if(LOG.isDebugEnabled())
					LOG.debug("Found an identifierForTheWork element with a value of " + value + " and a type of " + type + ".");
	
				work.addIdentifierForTheWork(type, value);
			} // end loop over identifierForTheWork elements
	
			return work;
		} // end try
		catch(JDOMException e)
		{
			LOG.error("An error occurred getting the identifierForTheWork elements from the passed Work element.", e);
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
		if(LOG.isDebugEnabled())
			LOG.debug("Creating a new Expression element from an expression component of the record with ID " + record.getId() + ".");
		
		// Create an Expression Object for the passed expression element
		Expression expression = new Expression();
		expression.setFormat(xcSchemaFormat);
		expression.setService(service); // Mark the Expression as being from no service since it shouldn't be output
	
		expression.setXcExpressionId(getXcIdentifierForFrbrElementDAO().getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_EXPRESSION));
	
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
	private void buildManifestation(Document manifestationElement, Record record)
	{
		if(LOG.isDebugEnabled())
			LOG.debug("Creating a new Manifestation element from a manifestation component of the record with ID " + record.getId() + ".");
	
//		// Create a Work Object for the passed work element
//		Manifestation manifestation = new Manifestation();
//		manifestation.setFormat(xcSchemaFormat);
//		manifestation.setService(service); // Mark the Manifestation as being from no service since it shouldn't be output
//	
//		try
//		{
//			manifestation.setXcManifestationId(frbrLevelIdDao.getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_MANIFESTATION));
//	
//			manifestation.setOaiXml(outputter.outputString(((Element)manifestationElement.getParent().clone())
//		                                                   .setContent(new Text("\n\t"))
//		                                                   .addContent(((Element)manifestationElement.clone()))
//		                                                   .addContent("\n\t")));
	
		try {
			// An XPATH expression to get the recordId elements
			XPath xpath = XPath.newInstance("/xc:frbr/xc:entity/xc:recordID");
			xpath.addNamespace(XC_NAMESPACE);
	
			// Get the subfields.
			List<Element> elements = xpath.selectNodes(manifestationElement);
	
			MatchIdentifiers matchIdentifiers = new MatchIdentifiers();
			matchIdentifiers.setOaiId(record.getOaiIdentifier());
						
			// Loop over the recordId elements and add value of each to the manifestation
			for(Element element : elements)
			{
				String value = element.getText();
				String type = element.getAttributeValue("type");
				
				if (type.equalsIgnoreCase("OCoLC")) {
					matchIdentifiers.setOclcValue(value);
				} else if (type.equalsIgnoreCase("LCCN")) {
					matchIdentifiers.setLccnValue(value);
				}
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
	
				if (type.equalsIgnoreCase("ISBN")) {
					matchIdentifiers.setIsbnValue(value);
				} else if (type.equalsIgnoreCase("ISSN")) {
					matchIdentifiers.setIssnValue(value);
				}

			} // end loop over recordId elements
			
			MatchIdentifierDAO matchIdentifierDAO = new DefaultMatchIdentifierDAO();
			matchIdentifierDAO.insert(matchIdentifiers);
			
		} // end try
		catch(JDOMException e)
		{
			LOG.error("An error occurred getting the recordId elements from the passed Manifestation element.", e);
		} // end catch(JDOMException)
		catch (DataException de) {
			LOG.error("Exception occured when adding the match identifiers to database.", de);
		}
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
		if(LOG.isDebugEnabled())
			LOG.debug("Creating a new Holdings element from a holdings component of the record with ID " + record.getId() + ".");
	
		// Create a Work Object for the passed work element
		Holdings holdings = new Holdings();
		holdings.setFormat(xcSchemaFormat);
		holdings.setService(service); // Mark the Holdings as being from no service since it shouldn't be output
	
		try
		{
			holdings.setXcHoldingsId(getXcIdentifierForFrbrElementDAO().getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_HOLDINGS));
	
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
	
				if(LOG.isDebugEnabled())
					LOG.debug("Found a recordId element with a value of " + value + " and a type of " + type + ".");
	
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
	
				if(LOG.isDebugEnabled())
					LOG.debug("Found a manifestationHeld element with a value of " + value + " and a type of " + type + ".");
	
				holdings.addManifestationHeld(type, value);
			} // end loop over manifestationHeld elements
	
			return holdings;
		} // end try
		catch(JDOMException e)
		{
			LOG.error("An error occurred getting the recordId and manifestationHeld elements from the passed Holdings element.", e);
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
		if(LOG.isDebugEnabled())
			LOG.debug("Creating a new Item element from an item component of the record with ID " + record.getId() + ".");
		
		// Create a Item Object for the passed work element
		Item item = new Item();
		item.setFormat(xcSchemaFormat);
		item.setService(service); // Mark the Item as being from no service since it shouldn't be output
	
		try
		{
			item.setXcItemId(getXcIdentifierForFrbrElementDAO().getNextXcIdForFrbrElement(XcIdentifierForFrbrElementDAO.ELEMENT_ID_ITEM));
	
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
	
				if(LOG.isDebugEnabled())
					LOG.debug("Found a holdingsExemplified element with a value of " + value + " and a type of " + type + ".");
	
				item.addHoldingsExemplified(type, value);
			} // end loop over holdingsExemplified elements
	
			return item;
		} // end try
		catch(JDOMException e)
		{
			LOG.error("An error occurred getting the holdingsExemplified elements from the passed Item element.", e);
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
	private List<Record> processWork(Record work, Document recordxml) throws IndexException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		Record newWork = new Record();
		// Set the new id on the xml and the processed record
		newWork.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Remove the ID so a new one gets generated
		newWork.setId(-1);
		
		newWork.setOaiXml(outputter.outputString(xml));

		newWork.setType("XC-Work");
		
		results.add(newWork);
		
		OutputRecordDAO outputRecordDAO = new DefaultOutputRecordDAO();
		
		for (Record result: results) {
			List<String> predecessorOAIIds = new ArrayList<String>();
			predecessorOAIIds.add(work.getOaiIdentifier());
			
			try {
				OutputRecord outputRecord = new OutputRecord(result.getOaiIdentifier(),
						result.getOaiXml(), true, predecessorOAIIds);
				outputRecordDAO.insert(outputRecord);
			} catch (DataException de) {
				LOG.error("Exception occured while inserting output record.", de);
			}
			
		}

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
	private List<Record> processExpression(Record expression, Document recordxml) throws DataException, IndexException
	{
	
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();
		
		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();

		Record newExpression = new Record();
				
		// Update the old OAI identifier found in input record's uplink with the new identifier
		OutputRecordDAO outputRecordDAO = new DefaultOutputRecordDAO();
		for (String uplink :expression.getUpLinks()) {
			List<String> succesorOAIIds = outputRecordDAO.getSuccessorByOaiId(uplink);
			
			// If the parents are not yet processed, then hold this record.
			if (succesorOAIIds == null || succesorOAIIds.size() == 0) {
				HeldRecord heldRecord = new HeldRecord(expression.getOaiIdentifier(), expression.getUpLinks());
				heldRecordDAO.insert(heldRecord);
				
				return null;
			} else {
				removeLinks(xml, "workExpressed");
			
				for (String newOAIId: succesorOAIIds) {
					addLink(xml, "workExpressed", newOAIId);
					newExpression.addUpLink(newOAIId);
				}
				
			}
			
		}

		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		newExpression.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Remove the ID so a new one gets generated
		newExpression.setId(-1);
		
		newExpression.setOaiXml(outputter.outputString(xml));
		newExpression.setType("XC-Expression");

		results.add(newExpression);
		
		for (Record result: results) {
			List<String> predecessorOAIIds = new ArrayList<String>();
			predecessorOAIIds.add(expression.getOaiIdentifier());
			
			try {
				OutputRecord outputRecord = new OutputRecord(result.getOaiIdentifier(),
						result.getOaiXml(), true, predecessorOAIIds);
				outputRecordDAO.insert(outputRecord);
			} catch (DataException de) {
				LOG.error("Exception occured while inserting output record.", de);
			}
			
		}
		
		return results;
	} // end method processExpressions(Record, Document)

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
	private List<Record> processManifestation(Record manifestation, Document recordxml) throws IndexException, DataException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		// Update the old OAI identifier found in input record's uplink with the new identifier
		OutputRecordDAO outputRecordDAO = new DefaultOutputRecordDAO();
		for (String uplink :manifestation.getUpLinks()) {
			List<String> succesorOAIIds = outputRecordDAO.getSuccessorByOaiId(uplink);
			
			// If the parents are not yet processed, then hold this record.
			if (succesorOAIIds == null || succesorOAIIds.size() == 0) {
				HeldRecord heldRecord = new HeldRecord(manifestation.getOaiIdentifier(), manifestation.getUpLinks());
				heldRecordDAO.insert(heldRecord);
				return null;
			} 			
		}
		Record newManifestation = new Record();
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		newManifestation.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Remove the ID so a new one gets generated
		newManifestation.setId(-1);
		
		List<Record> matches = matchManifestations(manifestation);
		List<String> matchedOaiIds = new ArrayList<String>();
		
		// If there were no matches add the manifestation to the list of results. 
		// If no matches it returns 1 match which is a match with its own record
		if(matches.size() == 1) {
			newManifestation.setOaiXml(outputter.outputString(xml));
			results.add(newManifestation);
			matchedOaiIds.add(manifestation.getOaiIdentifier());
		} else {
			Record baseRecord = getBaseRecord(matches);
	
			// Iterate over the matches and merge them as appropriate
			for(Record match : matches)
			{
				if (!match.equals(baseRecord)) {
					mergeManifestations(baseRecord, match);
				}
				matchedOaiIds.add(match.getOaiIdentifier());
				
				// Check if matched records already have output record
				List<String> outputRecordOAIIds = outputRecordDAO.getSuccessorByOaiId(match.getOaiIdentifier());
				
				// Delete the output records created earlier for this matched input record.
				for (String outputRecordOAIId : outputRecordOAIIds) {
					// Delete from index
					OutputRecord outputRecord = outputRecordDAO.getByOaiId(outputRecordOAIId);
					Record record = new Record();
					record.setOaiIdentifier(outputRecord.getOaiId());
					// TODO remove record id from solr index. Make OAI identifier unique key
					record.setId(-1);
					record.setDeleted(true);
					getRecordService().update(record);

					outputRecordDAO.deleteByOAIId(outputRecordOAIId);
				}
				
			} // end loop over matches
			newManifestation.setOaiXml(baseRecord.getOaiXml());
			results.add(newManifestation);
		}

		for(Record result : results) {

			try {
				xml = builder.build(new InputSource(new StringReader(result.getOaiXml())));
			
			} // end try
			catch(IOException e) {
				LOG.error("An error occurred while parsing the record's XML.", e);
			} catch(JDOMException e) {
				LOG.error("An error occurred while parsing the record's XML.", e);
			} // end catch JDOMException
			
			List<String> uplinks = getLinks(xml, "expressionManifested");
			removeLinks(xml, "expressionManifested");
			for (String uplink :uplinks) {
				List<String> succesorOAIIds = outputRecordDAO.getSuccessorByOaiId(uplink);
				
				for (String newOAIId: succesorOAIIds) {
					addLink(xml, "expressionManifested", newOAIId);
					result.addUpLink(newOAIId);
				}
			}
			
			result.setOaiXml(outputter.outputString(xml));
		}

		
		for (Record result: results) {
			
			try {
				OutputRecord outputRecord = new OutputRecord(result.getOaiIdentifier(),
						result.getOaiXml(), true, matchedOaiIds);
				outputRecordDAO.insert(outputRecord);
			} catch (DataException de) {
				LOG.error("Exception occured while inserting output record.", de);
			}
			
		}
		return results;
	} // end method processManifestations(Record, Document)

	/**
	 * Processes the Holdings elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 * @throws IndexException 
	 * @throws DatabaseConfigException 
	 */
	private List<Record> processHoldings(Record holding, Document recordxml) throws DataException, IndexException
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();
		
		// Create a new copy of the xml Document to avoid a ConcurrentModificationException
		Document xml = (Document)recordxml.clone();
		
		Record newHolding = new Record();
		
		// Update the old OAI identifier found in input record's uplink with the new identifier
		OutputRecordDAO outputRecordDAO = new DefaultOutputRecordDAO();
		for (String uplink :holding.getUpLinks()) {
			List<String> succesorOAIIds = outputRecordDAO.getSuccessorByOaiId(uplink);
			
			// If the parents are not yet processed, then hold this record.
			if (succesorOAIIds == null || succesorOAIIds.size() == 0) {
				HeldRecord heldRecord = new HeldRecord(holding.getOaiIdentifier(), holding.getUpLinks());
				heldRecordDAO.insert(heldRecord);
				return null;
			} else {
				removeLinks(xml, "manifestationHeld");
				for (String newOAIId: succesorOAIIds) {
					addLink(xml, "manifestationHeld", newOAIId);
					newHolding.addUpLink(newOAIId);
				}
			}
			
		}
		
		// Generate a new id for the processed record
		String newOaiIdentifier = getNextOaiId();
		
		// Set the new id on the xml and the processed record
		newHolding.setOaiIdentifier(newOaiIdentifier);
		setFRBRLevelIdentifier(xml, newOaiIdentifier);
		
		// Remove the ID so a new one gets generated
		newHolding.setId(-1);
		
		newHolding.setOaiXml(outputter.outputString(xml));
		newHolding.setType("XC-Holding");
		
		// Add the holdings to the list of records to insert
		results.add(newHolding);
		
		for (Record result: results) {
			List<String> predecessorOAIIds = new ArrayList<String>();
			predecessorOAIIds.add(holding.getOaiIdentifier());
			
			try {
				OutputRecord outputRecord = new OutputRecord(result.getOaiIdentifier(),
						result.getOaiXml(), true, predecessorOAIIds);
				outputRecordDAO.insert(outputRecord);
			} catch (DataException de) {
				LOG.error("Exception occured while inserting output record.", de);
			}
			
		}

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
			Record inputHoldingsLinked = getRecordService().getInputForServiceByOaiIdentifier(linkedHolding, service.getId());
			if(inputHoldingsLinked != null)
			{
				HoldingsList holdingsProducedByLinkedHoldings = getHoldingsService().getByProcessedFrom(inputHoldingsLinked);
				
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
					WorkList matchedWorks = getWorkService().getByIdentifierForTheWork(identifierForTheWork);
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
			LOG.error("Indexing exception occured.", ie);
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
	private List<Record> matchManifestations(Record matchMe)
	{
		List<Record> results = new ArrayList<Record>();

		try 
		{
			// For each recordID, add all matches to that recordID to the list of results
			// if we're configured to match on that record ID type
			MatchIdentifierDAO matchIdentifierDAO = new DefaultMatchIdentifierDAO();
			
			MatchIdentifiers matchIdentifiers = matchIdentifierDAO.getByOaiId(matchMe.getOaiIdentifier());

			
			// If we're configured to match on the type of recordID we're checking,
			// add all matches to that recordID
			if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_OCOLC, "0").equals("1"))
			{
				List<String> matchedManifestiationOaiIds = matchIdentifierDAO.getByOCLCValue(matchIdentifiers.getOclcValue());
				if (matchedManifestiationOaiIds != null && matchedManifestiationOaiIds.size() > 0) {
					for(String matchedManifestiationOaiId : matchedManifestiationOaiIds)
					{
						logInfo("Matched manifestations with OAI identifiers " + matchedManifestiationOaiId+ " and " + matchMe.getOaiIdentifier() + " because they both contained an OCoLC field with a value of " + matchIdentifiers.getOclcValue());
						results.add(getRecordService().getByOaiIdentifier(matchedManifestiationOaiId));
					}
					return results;
				} 
			}
			if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_LCCN, "0").equals("1"))
			{
				List<String> matchedManifestiationOaiIds = matchIdentifierDAO.getByLCCNValue(matchIdentifiers.getLccnValue());
				if (matchedManifestiationOaiIds != null && matchedManifestiationOaiIds.size() > 0) {
					for(String matchedManifestiationOaiId : matchedManifestiationOaiIds)
					{
						logInfo("Matched manifestations with OAI identifiers " + matchedManifestiationOaiId+ " and " + matchMe.getOaiIdentifier() + " because they both contained an LCCN field with a value of " + matchIdentifiers.getLccnValue());
						results.add(getRecordService().getByOaiIdentifier(matchedManifestiationOaiId));
					}
					return results;
				} 
			}
			if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_ISBN, "0").equals("1"))
			{
				List<String> matchedManifestiationOaiIds = matchIdentifierDAO.getByISBNValue(matchIdentifiers.getIsbnValue());
				if (matchedManifestiationOaiIds != null && matchedManifestiationOaiIds.size() > 0) {
					for(String matchedManifestiationOaiId : matchedManifestiationOaiIds)
					{
						logInfo("Matched manifestations with OAI identifiers " + matchedManifestiationOaiId+ " and " + matchMe.getOaiIdentifier() + " because they both contained an ISBN field with a value of " + matchIdentifiers.getIsbnValue());
						results.add(getRecordService().getByOaiIdentifier(matchedManifestiationOaiId));
					}
					return results;
				} 
			}
			if(manifestationMerge.getProperty(AggregationServiceConstants.CONFIG_MERGE_ISSN, "0").equals("1"))
			{
				List<String> matchedManifestiationOaiIds = matchIdentifierDAO.getByLCCNValue(matchIdentifiers.getLccnValue());
				if (matchedManifestiationOaiIds != null && matchedManifestiationOaiIds.size() > 0) {
					for(String matchedManifestiationOaiId : matchedManifestiationOaiIds)
					{
						logInfo("Matched manifestations with OAI identifiers " + matchedManifestiationOaiId+ " and " + matchMe.getOaiIdentifier() + " because they both contained an ISSN field with a value of " + matchIdentifiers.getIssnValue());
						results.add(getRecordService().getByOaiIdentifier(matchedManifestiationOaiId));
					}
					return results;
				} 
			}
			
		} 
		catch (IndexException ie) 
		{
			LOG.error("Index exception occured.", ie);
		} catch (DatabaseConfigException dce) {
			LOG.error("Exception occurred while matching the manifestation record", dce);
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
//	private List<Holdings> getHoldingsMatchingManifestation(Record matchMe)
//	{
//		List<Holdings> results = new ArrayList<Holdings>();
//
//		try 
//		{
//			// For each record ID a holdings could match on
//			// add all holdings that match on it
//			for(String xcRecordId : matchMe.getXcRecordIds())
//			{
//				HoldingsList matchedHoldings = holdingsService.getByManifestationHeld(xcRecordId);
//				for(Holdings matchedHolding : matchedHoldings)
//				{
//					logInfo("Linking the manifestation with OAI identifier " + matchMe.getOaiIdentifier() + " with the holdings with OAI identifier " + matchedHolding.getOaiIdentifier() + " because the manifestation's xcRecordId field had the same value as the holdings's manifestationHeld field.  This value was " + xcRecordId);
//					results.add(matchedHolding);
//				}
//			}
//		} 
//		catch (IndexException ie) 
//		{
//			log.error("Index exception occured.", ie);
//		}
//		return results;
//	} // end method getHoldingsMatchingManifestation(Manifestation)

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
				ManifestationList matchedManifestiations = getManifestationService().getByXcRecordId(manifestationHeld);
				for(Manifestation matchedManifestiation : matchedManifestiations)
				{
					logInfo("Linking the manifestation with OAI identifier " + matchedManifestiation.getOaiIdentifier() + " and the holdings with OAI identifier " + matchMe.getOaiIdentifier() + " because the manifestation's xcRecordId field had the same value as the holdings's manifestationHeld field.  This value was " + manifestationHeld);
					results.add(matchedManifestiation);
				}
			}
		} 
		catch (IndexException ie) 
		{
			LOG.error("Index exception occured.", ie);
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
				ItemList matchedItems = getItemService().getByHoldingsExemplified(xcRecordId);
				for(Item matchedItem : matchedItems)
				{
					logInfo("Linking the holdings with OAI identifier " + matchMe.getOaiIdentifier() + " with the item with OAI identifier " + matchedItem.getOaiIdentifier() + " because the holdings's xcRecordId field had the same value as the item's holdingsExemplified field.  This value was " + xcRecordId);
					results.add(matchedItem);
				}
			}
		} 
		catch (IndexException ie) 
		{
			LOG.error("Index exception occured.", ie);
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
				HoldingsList matchedHoldings = getHoldingsService().getByXcRecordId(holdingsExemplified);
				for(Holdings matchedHolding : matchedHoldings)
				{
					logInfo("Linking the holdings with OAI identifier " + matchedHolding.getOaiIdentifier() + " and the item with OAI identifier " + matchMe.getOaiIdentifier() + " because the holdings's xcRecordId field had the same value as the item's holdingsExemplified field.  This value was " + holdingsExemplified);
					results.add(matchedHolding);
				}
			}
		} 
		catch (IndexException ie) 
		{
			LOG.error("Index exception occured.", ie);
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
			if(LOG.isDebugEnabled())
				LOG.debug("Parsing the record's XML into a Document Object.");
	
			baseXml = builder.build(new InputSource(new StringReader(base.getOaiXml())));
			mergeIntoBaseXml = builder.build(new InputSource(new StringReader(mergeIntoBase.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			LOG.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch IOException
		catch(JDOMException e)
		{
			LOG.error("An error occurred while parsing the work's XML.", e);
	
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

	/*
	 * Returns the base record to merge the other matched records
	 */
	private Record getBaseRecord(List<Record> records) {
		
		Record baseRecord = null;
		
		if (manifestationMergeBase.getProperty(AggregationServiceConstants.CONFIG_RECORD_LENGTH, "0").equals("1")) {
			baseRecord = getBaseRecordByRecordLength(records);
		} else 	if (manifestationMergeBase.getProperty(AggregationServiceConstants.CONFIG_NUMBER_OF_ELEMENTS, "0").equals("1")) {
			baseRecord = getBaseRecordByNumberOfElements(records);
		} else	if (manifestationMergeBase.getProperty(AggregationServiceConstants.CONFIG_PROVENANCE, "0").equals("1")) {
			baseRecord = getBaseRecordByProvenance(records);
		} else {
			if (records != null && records.size() > 0) {
				baseRecord = records.get(0);
			}
		}
			
		return baseRecord;
	}

	/*
	 * Gets the base record based on the record length. Returns the record with maximum record length as base record.
	 */
	private Record getBaseRecordByRecordLength(List<Record> records) {
		
		Record baseRecord = null;
		int currentRecordLength = 0;
		int maxRecordLength = 0;
		
		for (Record record: records) {
			currentRecordLength = record.getOaiXml().length();
			
			if (currentRecordLength > maxRecordLength) {
				baseRecord = record;
				maxRecordLength = currentRecordLength;
			}
		}

		return baseRecord;
	}

	/*
	 * Gets the base record based on the number of elements. Returns the record with maximum number 
	 * of non empty elements as base record.
	 */
	private Record getBaseRecordByNumberOfElements(List<Record> records) {
		
		Record baseRecord = null;
		int currentRecordElementsCount = 0;
		int maxRecordElementsCount = 0;
		
		
		for (Record record: records) {
			
			// The XML after normalizing the record
			Document marcXml = null;

			// Parse the XML from the record
			try
			{
				marcXml = builder.build(new InputSource(new StringReader(record.getOaiXml())));
			}
			catch(IOException e)
			{
				LOG.error("An error occurred while parsing the record's XML.", e);

				return null;
			}
			catch(JDOMException e)
			{
				LOG.error("An error occurred while parsing the record's XML.\n" + record.getOaiXml(), e);

				return null;
			}
			
			currentRecordElementsCount = marcXml.getRootElement().getContentSize();
			
			if (currentRecordElementsCount > maxRecordElementsCount) {
				baseRecord = record;
				maxRecordElementsCount = currentRecordElementsCount;
			}
		}

		return baseRecord;
	}
	

	/*
	 * Gets the base record based on the provenance. Returns the record with high priority institution code as base record.
	 */
	@SuppressWarnings("unchecked")
	private Record getBaseRecordByProvenance(List<Record> records) {
		
		Record baseRecord = null;
		
		String provenancePriority = manifestationMergeBase.getProperty(AggregationServiceConstants.CONFIG_PROVENANCE);
		
		int currentPriority = 0;
		int maxPriority = 0;
		List<Element> elements = null;
		for (Record record: records) {
			
			try {
				XPath xpath = XPath.newInstance("//xc:recordID");
				xpath.addNamespace("xc", "http://www.extensiblecatalog.info/Elements");
				elements = xpath.selectNodes(record.getOaiXml());
			} catch (JDOMException je) {
				LOG.error("JDOM Exception occured when parsing XML for recordID", je);
				continue;
			}

			ArrayList<String> institutionCodes = new ArrayList<String>();
			
			for(Element element : elements) {
				institutionCodes.add(element.getAttributeValue("type"));
			}
			
			StringTokenizer tokens = new StringTokenizer(provenancePriority, ",");
			
			int index = 1;
			
			while (tokens.hasMoreTokens()) {
				
				if (institutionCodes.contains(tokens.nextToken())) {
					if (currentPriority == 0 || currentPriority > index) {
						currentPriority = index;
					}
				}
				
				index++;
			}
			
			if (maxPriority == 0 || currentPriority < maxPriority) {
				baseRecord = record;
				maxPriority = currentPriority;
			}

		}

		return baseRecord;
	}
	
	/**
	 * Merges two manifestation elements
	 *
	 * @param base The manifestation element which will have the other manifestation merged into it
	 * @param mergeIntoBase The manifestation element which will merge into the other manifestation
	 * @return The merged manifestation element.  It's manifestation ID will be the base's manifestation ID.
	 */
	@SuppressWarnings("unchecked")
	private Record mergeManifestations(Record base, Record mergeIntoBase)
	{
		// The XML for the XC record
		Document baseXml = null;
		Document mergeIntoBaseXml = null;
	
		// Parse the XML from the record
		try
		{
			if(LOG.isDebugEnabled())
				LOG.debug("Parsing the record's XML into a Document Object.");
	
			baseXml = builder.build(new InputSource(new StringReader(base.getOaiXml())));
			mergeIntoBaseXml = builder.build(new InputSource(new StringReader(mergeIntoBase.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			LOG.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch IOException
		catch(JDOMException e)
		{
			LOG.error("An error occurred while parsing the work's XML.", e);
	
			return null;
		} // end catch JDOMException
	
		// Merge xc:expressionManifested
		// Add each element in the base to the cache of used elements
		List<Element> baseExpressionManifestedElements = baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren("expressionManifested", XcRecordSplitter.XC_NAMESPACE);
		
		for(Element baseElement : baseExpressionManifestedElements)
		{
			cacheForMerging.add(baseElement.getText());
		} // end loop over base record's elements
	
		// Add each element in the base to the cache of used elements
		List<Element> mergeIntoExpressionManifestedElements = mergeIntoBaseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren("expressionManifested", XcRecordSplitter.XC_NAMESPACE);

		for(Element mergeIntoElement : mergeIntoExpressionManifestedElements)
		{
			// Add the element to the base if doing so wouldn't cause a duplicate
			if(!cacheForMerging.contains(mergeIntoElement.getText()))
			{
				addLink(baseXml, "expressionManifested", mergeIntoElement.getText());
				cacheForMerging.add(mergeIntoElement.getText());
			} // end if (the element isn't a duplicate of something in the base)
			
		} // end loop over base record's elements
		cacheForMerging.clear();
		
		// Merge xc:recordID
		// Add each element in the base to the cache of used elements
		List<Element> baseRecordIdElements = baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren("recordID", XcRecordSplitter.XC_NAMESPACE);
		for(Element baseElement : baseRecordIdElements)
		{
			cacheForMerging.add(baseElement.getText());
		} // end loop over base record's elements

		// Add each element in the base to the cache of used elements
		List<Element> mergeIntoRecordIdElements = mergeIntoBaseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE).getChildren("recordID", XcRecordSplitter.XC_NAMESPACE);
		
		for(Element mergeIntoElement : mergeIntoRecordIdElements)
		{
			// Add the element to the base if doing so wouldn't cause a duplicate
			if(!cacheForMerging.contains(mergeIntoElement.getText()))
			{
				baseXml.getRootElement().getChild("entity", XcRecordSplitter.XC_NAMESPACE)
				                                 .addContent("\t")
				                        		 .addContent((Element)mergeIntoElement.clone())
				                        		 .addContent("\n\t");
				cacheForMerging.add(mergeIntoElement.getText());
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
			if(LOG.isDebugEnabled())
				LOG.debug("Parsing the record's XML into a Document Object.");

			xml = builder.build(new InputSource(new StringReader(from.getOaiXml())));
		} // end try
		catch(IOException e)
		{
			LOG.error("An error occurred while parsing the record's XML.", e);

			LogWriter.addWarning(service.getServicesLogFileName(), "An XML parse error occurred while processing the record with OAI Identifier " + from.getOaiIdentifier() + ".");

			return from;
		} // end catch IOException
		catch(JDOMException e)
		{
			LOG.error("An error occurred while parsing the record's XML.", e);

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
