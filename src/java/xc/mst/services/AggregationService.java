/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Expression;
import xc.mst.bo.record.Holdings;
import xc.mst.bo.record.Item;
import xc.mst.bo.record.Manifestation;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.Work;
import xc.mst.constants.AggregationServiceConstants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultExpressionService;
import xc.mst.manager.record.DefaultHoldingsService;
import xc.mst.manager.record.DefaultItemService;
import xc.mst.manager.record.DefaultManifestationService;
import xc.mst.manager.record.DefaultWorkService;
import xc.mst.manager.record.ExpressionService;
import xc.mst.manager.record.HoldingsService;
import xc.mst.manager.record.ItemService;
import xc.mst.manager.record.ManifestationService;
import xc.mst.manager.record.WorkService;
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
	private HashMap<String, Work> workCache = new HashMap<String, Work>();

	/**
	 *  A list of works which should be ignored since they were merged into another work
	 */
	private HashSet<Work> toIgnore = new HashSet<Work>();

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
			xcSchemaFormat = getFormatByName("xc");
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Could not connect to the database with the parameters in the configuration file.", e);
		}
	} // end constructor

	@Override
	protected void finishProcessing()
	{
	} // end method finishProcessing()

	@Override
	protected List<Record> processRecord(Record record)
	{
		XcRecordSplitter splitter = new XcRecordSplitter(record, service);

		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Process each of the FRBR levels and add the results to the list of all records
		// to be added or updated in the Lucene index
		results.addAll(processWorks(splitter));
		results.addAll(processExpressions(splitter));
		results.addAll(processManifestations(splitter));
		results.addAll(processHoldings(splitter));
		results.addAll(processItems(splitter));

		// Return the list of FRBR components to be added or updated in the Lucene index
		return results;
	} // end method processRecord(Record)

	/**
	 * Processes the Work elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 */
	private List<Record> processWorks(XcRecordSplitter splitter)
	{
		refreshIndex();

		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		List<Work> cleanedUpWorks = mergeWorksInList(splitter.getWorks());

		// Match all the works in the current record
		for(Work work : cleanedUpWorks)
		{
			List<Work> matches = matchWorks(work);

			// A list of work IDs that the current work merged with
			List<Work> newWorks= new ArrayList<Work>();

			// If there were no matches add the work to the list of results
			if(matches.size() == 0)
				results.add(work);

			// Iterate over the matches and merge them as appropriate
			for(Work match : matches)
			{
				mergeWorks(match, work);
				newWorks.add(match);
				results.add(match);
			} // end loop over matches

			// Update the IDs from the up links
			if(newWorks.size() > 0)
			{
				for(Expression expression : splitter.getExpressions())
				{
					expression.removeLinkToWork(work);
					for(Work newWork : newWorks)
						expression.addLinkToWork(newWork);
				} // end loop over expressions
			} // end if newWorkIds not empty
		} // end loop over works

		return results;
	} // end method processWorks(XcRecordSplitter)

	/**
	 * Processes the Expression elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 */
	private List<Record> processExpressions(XcRecordSplitter splitter)
	{
		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// There is currently no way to match an expression to any other FRBR element, so
		// add all the Expressions to the list to be returned
		results.addAll(splitter.getExpressions());

		return results;
	} // end method processExpressions(XcRecordSplitter)

	/**
	 * Processes the Manifestation elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the manifestations
	 */
	private List<Record> processManifestations(XcRecordSplitter splitter)
	{
		refreshIndex();

		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Match all the manifestations in the current record
		for(Manifestation manifestation : splitter.getManifestations())
		{
			List<Manifestation> matches = matchManifestations(manifestation);

			// A list of manifestation IDs that the current manifestation merged with
			List<Manifestation> newManifestations= new ArrayList<Manifestation>();

			// If there were no matches add the manifestation to the list of results
			if(matches.size() == 0)
				results.add(manifestation);

			// Iterate over the matches and merge them as appropriate
			for(Manifestation match : matches)
			{
				mergeManifestations(match, manifestation);
				newManifestations.add(match);
				results.add(match);
			} // end loop over matches

			// Update the IDs from the up links
			if(newManifestations.size() > 0)
			{
				for(Holdings holdings : splitter.getHoldings())
				{
					holdings.removeLinkToManifestation(manifestation);
					for(Manifestation newManifestation : newManifestations)
						holdings.addLinkToManifestation(newManifestation);
				} // end loop over holdings
			} // end if newManifestationIds not empty

			// Check the database for any holdings elements that need to be linked to
			// the  manifestations, and add the links as appropriate
			List<Holdings> holdings = getHoldingsMatchingManifestation(manifestation);
			for(Holdings holdingsElement : holdings)
			{
				if(newManifestations.size() == 0)
					holdingsElement.addLinkToManifestation(manifestation);
				else
					for(Manifestation newManifestation : newManifestations)
						holdingsElement.addLinkToManifestation(newManifestation);

				results.add(holdingsElement);
			} // end loop over matched holdings
		} // end loop over manifestations

		return results;
	} // end method processManifestations(XcRecordSplitter)

	/**
	 * Processes the Holdings elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 */
	private List<Record> processHoldings(XcRecordSplitter splitter)
	{
		refreshIndex();

		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Get the holdings elements from the split record
		List<Holdings> holdingsElements = splitter.getHoldings();

		// Process each holdings element
		for(Holdings holdings : holdingsElements)
		{
			// Check the database for any manifestation elements that need to be linked to
			// the  holdings, and add the links as appropriate
			List<Manifestation> manifestations = getManifestationsMatchingHoldings(holdings);
			for(Manifestation manifestation : manifestations)
				holdings.addLinkToManifestation(manifestation);

			// Check the database for any item elements that need to be linked to
			// the  holdings, and add the links as appropriate
			List<Item> items = getItemsMatchingHoldings(holdings);
			for(Item item : items)
			{
				item.addLinkToHoldings(holdings);

				// Add the item to the list of updated records
				results.add(item);
			} // end loop over matched items

			// Add the holdings to the list of records to insert
			results.add(holdings);
		} // end loop over holdings

		return results;
	} // end method processHoldings(XcRecordSplitter)

	/**
	 * Processes the Expression elements as determined by an XcRecordSplitter that
	 * has split the record to be processed.
	 *
	 * @param splitter An XcRecordSplitter that has split the record to be processed
	 * @return A list of records that need to be added or updated after processing the works
	 */
	private List<Record> processItems(XcRecordSplitter splitter)
	{
		refreshIndex();

		// A list of new or updated records resulting form processing the passed record
		List<Record> results = new ArrayList<Record>();

		// Get the item elements from the split record
		List<Item> items = splitter.getItems();

		// Process each item element
		for(Item item : items)
		{
			// Check the database for any manifestation elements that need to be linked to
			// the  holdings, and add the links as appropriate
			List<Holdings> holdingsElements = getHoldingsMatchingItem(item);
			for(Holdings holdings : holdingsElements)
				item.addLinkToHoldings(holdings);

			// Add the item to the list of records to insert
			results.add(item);
		} // end loop over item

		return results;
	} // end method processItems(XcRecordSplitter)

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
	 * Checks the list of Work elements for matches and returns the same list with the matched
	 * records merged.
	 *
	 * @param works A list of work elements
	 * @return The same list with the matched records removed
	 */
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

	/**
	 * Given a work, creates a complete XC record for that work. Up links
	 * are used to combine the correct elements in all five FRBR levels.
	 * The returned records will have processedFrom links to all FRBR
	 * elements it was created from.
	 *
	 * @param work The work to create a Record for
	 * @return A Record with information in all FRBR levels linked to the passed work
	 */
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

	/**
	 * This method parses the frbrComponent into a JDOM Element and adds
	 * the xc:entity of the frbrComponent to the xcRecord.  It then returns
	 * the modified xcRecord.
	 *
	 * @param xcRecord The XC record to which the component should be added
	 * @param frbrComponent The FRBR component to add (the XML from a Work, Expression, Manifestation, Holdings, or Item Object)
	 * @return The XC record with the passed FRBR component added
	 */
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
} // end class AggregationService
