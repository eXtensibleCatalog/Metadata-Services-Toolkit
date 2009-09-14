/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.oai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jdom.Element;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.ResumptionToken;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultSetDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.dao.record.DefaultResumptionTokenDAO;
import xc.mst.dao.record.ResumptionTokenDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.XMLUtil;

/**
 * A facade class to create the response to the different OAI verb requests
 *
 * @author Peter Kiraly -- Initial code
 * @author Eric Osisek -- Updated for Metadata Services Toolkit
 */
public class Facade
{
	/**
	 * Data access object for getting services
	 */
	private static ServiceDAO serviceDao = new DefaultServiceDAO();

	/**
	 * Data access object for getting and updating resumption tokens
	 */
	private static ResumptionTokenDAO resumptionTokenDao = new DefaultResumptionTokenDAO();

	/**
	 * Data access object for getting formats
	 */
	private static FormatDAO formatDao = new DefaultFormatDAO();

	/**
	 * Data access object for getting sets
	 */
	private static SetDAO setDao = new DefaultSetDAO();

	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration();

	/**
	 * The logger object
	 */
	private static Logger log = Logger.getLogger(Constants.LOGGER_HARVEST_OUT);

	/**
	 * OAI request bean
	 */
	private OaiRequestBean bean;

	/**
	 * The number of warnings in executing the current request
	 */
	private int warningCount = 0;

	/**
	 * The number of errors in executing the current request
	 */
	private int errorCount = 0;

	// OAI request parameters

	/**
	 * The OAI verb parameter
	 */
	private String verb;

	/**
	 * The OAI from parameter
	 */
	private String from;

	/**
	 * The OAI until parameter
	 */
	private String until;

	/**
	 * The OAI metadataPrefix parameter
	 */
	private String metadataPrefix;

	/**
	 * The OAI set parameter
	 */
	private String set;

	/**
	 * The OAI identifier parameter
	 */
	private String identifier;

	/**
	 * The OAI resumptionToken parameter
	 */
	private String resumptionToken;

	/**
	 * The ID of the service whose data we're getting
	 */
	private int serviceId = 0;

	/**
	 * The service being harvested
	 */
	private Service service = null;
	
	/**
	 * The OAI repository base URL
	 */
	private String oaiRepoBaseURL = null;

	/**
	 * Creates a new Facade object
	 *
	 * @param oaiBean The OAI request bean
	 */
	public Facade(OaiRequestBean oaiBean, String oaiRepoBaseURL)
	{
		if(log.isDebugEnabled())
			log.debug("Constructing a new Facade Object.");

		// Populate the private variables storing the request parameters
		// with values from the bean storing the form's data.
		bean = oaiBean;
		verb = bean.getVerb();
		from = bean.getFrom();
		until = bean.getUntil();
		metadataPrefix = bean.getMetadataPrefix();
		set = bean.getSet();
		identifier = bean.getIdentifier();
		resumptionToken = bean.getResumptionToken();
		serviceId = bean.getServiceId();
		this.oaiRepoBaseURL = oaiRepoBaseURL;
	}

	/**
	 * Creates the common header for all responses
	 *
	 * @param url The requested URL
	 */
	public void setResponseHeader(StringBuffer url)
	{
		if(log.isDebugEnabled())
			log.debug("Creating the response header for the OAI repsonse.");

		// Add the element with the timestamp of the response
		try
		{
			Element responseDate = new Element("responseDate");
			String formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());
			responseDate.addContent(formattedDate);
			bean.setResponseDateElement(XMLUtil.format.outputString(responseDate));

			if(log.isDebugEnabled())
				log.debug("Setting the responseDate in the OAI response header to " + formattedDate);
		}
		catch(Exception e)
		{
			log.error("An exception occurred setting the response date.", e);
			bean.setResponseDateElement("");
		}

		// Add the request element, which echoes the data from the request we're responding to
		// Each field which is not null is added as an attribute on the request element
		try
		{
			Element requestEl = new Element("request");
			requestEl.addContent(url.toString());
			if(verb != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the verb in the OAI response header to " + verb);

				requestEl.setAttribute("verb", verb);
			}
			if(from != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the from field in the OAI response header to " + from);

				requestEl.setAttribute("from", from);
			}
			if(until != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the until field in the OAI response header to " + until);

				requestEl.setAttribute("until", until);
			}
			if(metadataPrefix != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the metadataPrefix in the OAI response header to " + metadataPrefix);

				requestEl.setAttribute("metadataPrefix", metadataPrefix);
			}
			if(set != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the set in the OAI response header to " + set);

				requestEl.setAttribute("set", set);
			}
			if(identifier != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the identifier in the OAI response header to " + identifier);

				requestEl.setAttribute("identifier", identifier);
			}
			if(resumptionToken != null)
			{
				if(log.isDebugEnabled())
					log.debug("Setting the resumptionToken in the OAI response header to " + resumptionToken);

				requestEl.setAttribute("resumptionToken", resumptionToken);
			}

			// Set the request header to the form's URL field
			bean.setRequestElement(XMLUtil.format.outputString(requestEl));
		}
		catch(Exception e)
		{
			log.error("An exception occurred setting up the response's request element.", e);
			bean.setRequestElement("");
		}
	}

	/**
	 * Executes the correct OAI function based on the verb
	 * @throws DatabaseConfigException
	 */
	public void execute() throws DatabaseConfigException
	{
		if(log.isDebugEnabled())
			log.debug("Executing request for verb " + verb + ".");

		// Get the service
		service = serviceDao.getById(serviceId);

		// If the verb was null, return a bad verb error
		// Otherwise execute the correct funtionality, and
		// return a bad verb error only when the verb is not
		// recognized
		try
		{
			if(verb == null)
			{
				LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI request did not contain a verb.");
				warningCount++;

				bean.setXmlResponse(ErrorBuilder.badVerbError());
			}
			else if(verb.equalsIgnoreCase("Identify"))
				doIdentify();
			else if(verb.equalsIgnoreCase("ListSets"))
				doListSets();
			else if(verb.equalsIgnoreCase("ListMetadataFormats"))
				doListMetadataFormats();
			else if(verb.equalsIgnoreCase("ListIdentifiers"))
				doListIdentifiers();
			else if(verb.equalsIgnoreCase("ListRecords"))
				doListRecords();
			else if(verb.equalsIgnoreCase("GetRecord"))
				doGetRecord();
			else
			{
				LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI request contained an invalid verb: " + verb + ".");
				warningCount++;

				bean.setXmlResponse(ErrorBuilder.badVerbError());
			}
		}
		catch(Exception e)
		{
			log.error("An exception occurred while executing the request.", e);
			bean.setXmlResponse("");

			LogWriter.addError(service.getHarvestOutLogFileName(), "An unexpected error occurred while executing the " + verb + " request.");
			errorCount++;
		}
		finally // Update the error and warning count for the service
		{
			// Load the provider again in case it was updated during the harvest
			Service service = serviceDao.getById(this.service.getId());

			// Increase the warning and error counts as appropriate, then update the provider
			service.setHarvestOutWarnings(service.getHarvestOutWarnings() + warningCount);
			service.setHarvestOutErrors(service.getHarvestOutErrors() + errorCount);
			
			// Increase number of harvests if this is the initial request for harvest
			if((verb.equalsIgnoreCase("ListRecords")) && (resumptionToken == null || resumptionToken.trim().length() == 0) && (metadataPrefix != null || metadataPrefix.trim().length() != 0))
			{
				service.setNumberOfHarvests(service.getNumberOfHarvests() + 1);
			}

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e)
			{
				log.warn("Unable to update the provider's warning and error counts due to a Data Exception.", e);
			}
		}
	}

	/**
	 * Create response to the Identify verb. The parameters arrive in the
	 * form (which is a FormBean). The XML response will be set to the value of
	 * the form bean's xml field.
	 * @throws DatabaseConfigException
	 */
	public void doIdentify() throws DatabaseConfigException
	{
		if(log.isDebugEnabled())
			log.debug("Entering doIdentify");

		// Get the service so we know which port to report
		Service service = serviceDao.getById(serviceId);

		// Get the port from the service, using 8080 as the default
		int port = (service != null ? service.getPort() : 8080);

		// Build the Identify element which will contain information on the OAI repository.
		// Most of the information is pulled from the configuration file, but the earliestDatestamp
		// is read from the database as the lowest value for the OAI_datestamp column in the results table
		Element root = new Element("Identify");
		root.addContent(XMLUtil.xmlEl("repositoryName", MSTConfiguration.getProperty(Constants.CONFIG_OAI_REPO_NAME)));
		root.addContent(XMLUtil.xmlEl("baseURL", oaiRepoBaseURL + ":" + port));
		root.addContent(XMLUtil.xmlEl("protocolVersion", configuration.getProperty(Constants.CONFIG_OAI_REPO_PROTOCOL_VERSION)));
		root.addContent(XMLUtil.xmlEl("adminEmail", MSTConfiguration.getProperty(Constants.CONFIG_OAI_REPO_ADMIN_EMAIL)));
		root.addContent(XMLUtil.xmlEl("deletedRecord", configuration.getProperty(Constants.CONFIG_OAI_REPO_DELETED_RECORD)));
		root.addContent(XMLUtil.xmlEl("granularity", configuration.getProperty(Constants.CONFIG_OAI_REPO_GRANULARITY)));

		// Get the earliest record.  If it's not null, set the earliestDatestamp to it's datestamp.
		// Otherwise, there were no records, and we will set it to the beginning of the epoch
		Record earliest = recordService.getEarliest(serviceId);
		root.addContent(XMLUtil.xmlEl("earliestDatestamp", (earliest != null ? new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(earliest.getOaiDatestamp()) : "1970-01-01T12:00:00Z")));

		String[] compressions = configuration.getProperty(Constants.CONFIG_OAI_REPO_COMPRESSION).split(";");
		for(String compression : compressions)
			root.addContent(XMLUtil.xmlEl("compression", compression));

		// Set up and add the description element.

		// Create the description's oai-identifier element
		Element oaiIdentifier = new Element("oai-identifier");
		//oaiIdentifier.addNamespaceDeclaration(Namespace.getNamespace("http://www.openarchives.org/OAI/2.0/oai-identifier"));
		//oaiIdentifier.addNamespaceDeclaration(Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		//oaiIdentifier.addNamespaceDeclaration(Namespace.getNamespace("schemaLocation", "http://www.openarchives.org/OAI/2.0/oai-identifier\nhttp://www.openarchives.org/OAI/2.0/oai-identifier.xsd"));

		// Add child elements to the oaiIdentifier element with useful information
		oaiIdentifier.addContent(XMLUtil.xmlEl("scheme", configuration.getProperty(Constants.CONFIG_OAI_REPO_SCHEME)));
		oaiIdentifier.addContent(XMLUtil.xmlEl("repositoryIdentifier", MSTConfiguration.getProperty(Constants.CONFIG_OAI_REPO_IDENTIFIER)));
		oaiIdentifier.addContent(XMLUtil.xmlEl("delimiter", configuration.getProperty(Constants.CONFIG_OAI_REPO_DELIMITER)));
		oaiIdentifier.addContent(XMLUtil.xmlEl("sampleIdentifier", "oai:" + MSTConfiguration.getProperty(Constants.CONFIG_OAI_REPO_IDENTIFIER) + ":" + service.getName() + "/1"));

		// Add a description element with the oai-identifier element we just created
		root.addContent(XMLUtil.xmlEl("description", null).addContent(oaiIdentifier));

		if(log.isDebugEnabled())
			log.debug("Setting the doIdentifiy response based on values in the configuration file.");

		bean.setXmlResponse(XMLUtil.format.outputString(root).replaceAll(" xmlns=\"\"", ""));
	}

	/**
	 * Create an XML response to the ListMetadataFormat verb.
	 * @throws DatabaseConfigException
	 */
	public void doListMetadataFormats() throws DatabaseConfigException, IndexException
	{
		// Create the ListMetadataFormats element for the OAI response
		Element listMetadataFormats = new Element("ListMetadataFormats");

		// Get the service so we know which port to report
		Service service = serviceDao.getById(serviceId);

		// If the identifier was not null, it contains the record ID of the specific record whose format
		// should be returned.  In this case the response will contain just the format of that record
		// (since we won't be able to convert it to different formats.)  Otherwise it will contain all formats
		// supported by the service.
		if(identifier != null && identifier.length() > 0)
		{
			// Get the record
			Record record = recordService.getByOaiIdentifierAndService(identifier, serviceId);

			// If the record didn't exist, the XML should be an error explaining this.
			if(record == null)
			{
				log.warn("The record with OAI identifier " + identifier + " could not be found.");

				bean.setXmlResponse(XMLUtil.xmlTag("error", Constants.ERROR_ID_DOES_NOT_EXIST, new String[]{"code", "idDoesNotExist"}));

				return;
			}

			// Get the record's format.
			Format format = record.getFormat();

			// Add the format to the response if it wasn't null.  If it was null, the requested record did not exist
			if(format != null)
			{
				if(log.isDebugEnabled())
					log.debug("Adding the format " + format.getName() + " as the format for the requested record with identifier " + identifier + ".");

				// Add the metadata prefix, schema, and namespace information to the ListMetadataFormats element
				listMetadataFormats.addContent(XMLUtil.xmlEl("metadataFormat", null).addContent(XMLUtil.xmlEl("metadataPrefix", format.getName()))
						                                                            .addContent(XMLUtil.xmlEl("schema", format.getSchemaLocation()))
						                                                            .addContent(XMLUtil.xmlEl("metadataNamespace", format.getNamespace())));
			}
			else // The record didn't exist so the format could not be found.  Log the problem and return an error
			{
				if(log.isDebugEnabled())
					log.debug("The format for the requested record with identifier " + identifier + " did not exist.  This suggests the record itself did not exist");

				LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListMetadataFormats request did not contain a metadataFormat.");
				warningCount++;

				bean.setXmlResponse(ErrorBuilder.idDoesNotExistError());
				return;
			}
		}
		else // The OAI Identifier was not specified, so just return all formats supported by the OAI repository
		{
			// Iterate over the formats and add each to the ListMetadataFormats element
			for(Format format : service.getOutputFormats())
			{
				if(log.isDebugEnabled())
					log.debug("Adding the format " + format.getName() + " to the list of returned formats.");

				LogWriter.addInfo(service.getHarvestOutLogFileName(), "Adding the format " + format.getName() + " to the list of returned formats.");

				listMetadataFormats.addContent(XMLUtil.xmlEl("metadataFormat", null).addContent(XMLUtil.xmlEl("metadataPrefix", format.getName()))
                                                                                    .addContent(XMLUtil.xmlEl("schema", format.getSchemaLocation()))
                                                                                    .addContent(XMLUtil.xmlEl("metadataNamespace", format.getNamespace())));
			}
		}

		// Set the ListMetadataFormat XML as the OAI response on the form
		bean.setXmlResponse(XMLUtil.format.outputString(listMetadataFormats));
	}

	/**
	 * Create response to the ListSets verb. List the sets in XML format.
	 * The parameters arrive in the form parameter (which is a FormBean). The XML
	 * response will be the value of the form xml field.
	 * @throws DatabaseConfigException
	 */
	public void doListSets() throws DatabaseConfigException
	{
		// Get all the sets in the database
		List<Set> sets = setDao.getAll();

		// Get the service for which the sets are listed 
		Service service = serviceDao.getById(bean.getServiceId());
		
		// The ListSets element for the OAI response
		Element listSets = new Element("ListSets");

		if(sets == null || sets.size() <= 0)
		{
			if(log.isDebugEnabled())
				log.debug("There are no sets in the repository.");

			LogWriter.addInfo(service.getHarvestOutLogFileName(), "There are no sets in the OAI Repository, so sending a noSetHierarchy error in the ListSets response.");


			bean.setXmlResponse(ErrorBuilder.noSetHierarchyError());
			return;
		}
		// Loop over the sets in the database and add each one to the listSets element
		
		for(Set set : sets)
		{
			
			if(log.isDebugEnabled())
				log.debug("Adding the set " + set.getSetSpec() + " to the list of returned sets.");

			LogWriter.addInfo(service.getHarvestOutLogFileName(), "Adding the set " + set.getSetSpec() + " to the list of returned sets.");

			// If service sets are requested, then do not add all sets
			if( service != null )
				break;
			
			// Else list all
			else
				listSets.addContent(XMLUtil.xmlEl("set", null).addContent(XMLUtil.xmlEl("setSpec", set.getSetSpec()))
                        .addContent(XMLUtil.xmlEl("setName", set.getDisplayName())));

		}

		// If service sets are requested, then list only the sets for that service
		if(service!=null)
			for (Set set : service.getOutputSets()) {
				
				listSets.addContent(XMLUtil.xmlEl("set", null).addContent(XMLUtil.xmlEl("setSpec", set.getSetSpec()))
                        .addContent(XMLUtil.xmlEl("setName", set.getDisplayName())));

		}
		

		// Set the result to the form
		bean.setXmlResponse(XMLUtil.format.outputString(listSets));
	}

	/**
	 * Create the response to the ListIdentifiers verb.
	 * @throws DatabaseConfigException
	 */
	public void doListIdentifiers() throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Entering doListIdentifiers");

		// Return an error if the metadataPrefix was null or empty
		if(metadataPrefix == null || metadataPrefix.trim().length() == 0)
		{
			log.warn("The OAI request did not contain an metadataPrefix element.");

			LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListIdentifiers request did not contain a metadataFormat.");
			warningCount++;

			bean.setXmlResponse(ErrorBuilder.badArgumentError("Missing metadataPrefix parameter"));
			return;
		}

		// Get the XML for the identifiers
		// The last parameter is true to query for just the identifiers and not the full records
		String xml = handleRecordLists(from, until, metadataPrefix, set, resumptionToken, false);
		bean.setXmlResponse(xml);
	}

	/**
	 * Create the response to the ListRecords verb.
	 * @throws DatabaseConfigException
	 */
	public void doListRecords() throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Entering doListRecords");

		// Return an error if the metadataPrefix was null or empty
		if((resumptionToken == null || resumptionToken.trim().length() == 0) && (metadataPrefix == null || metadataPrefix.trim().length() == 0))
		{
			log.warn("The OAI request did not contain an metadataPrefix element.");

			LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListRecords request did not contain a metadataFormat.");
			warningCount++;

			bean.setXmlResponse(ErrorBuilder.badArgumentError("Missing metadataPrefix parameter"));
			return;
		}

		// Get the XML for the full records
		// The last parameter is true to query for the full records and not just the identifiers
		String xml = handleRecordLists(from, until, metadataPrefix, set, resumptionToken, true);
		bean.setXmlResponse(xml);
	}

	/**
	 * Create response to the GetRecord verb
	 * @throws DatabaseConfigException
	 */
	public void doGetRecord() throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Entering doGetRecord");

		// Return an error if the identifier was null or empty
		if(identifier == null || identifier.trim().length() == 0)
		{
			log.warn("The OAI request did not contain an identifier element.");

			LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI GetRecord request did not contain an identifier.");
			warningCount++;

			bean.setXmlResponse(ErrorBuilder.badArgumentError("Missing identifier parameter"));
			return;
		}

		// Return an error if the metadataPrefix was null or empty
		if(metadataPrefix == null || metadataPrefix.trim().length() == 0)
		{
			log.warn("The OAI request did not contain an metadataPrefix element.");

			LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI GetRecord request did not contain a metadataFormat.");
			warningCount++;

			bean.setXmlResponse(ErrorBuilder.badArgumentError("Missing metadataPrefix parameter"));
			return;
		}

		// Get the record
		Record record = recordService.getByOaiIdentifierAndService(identifier, serviceId);

		// If the record didn't exist, the XML should be an error explaining this.
		if(record == null)
		{
			log.warn("The record with OAI identifier " + identifier + " could not be found.");

			LogWriter.addWarning(service.getHarvestOutLogFileName(), "The record with the OAI Identifier " + identifier + " could not be found.");
			warningCount++;

			bean.setXmlResponse(XMLUtil.xmlTag("error", Constants.ERROR_ID_DOES_NOT_EXIST, new String[]{"code", "idDoesNotExist"}));
		}
		else
		{
			// Get the record's format type
			Format format = record.getFormat();

			// If the format didn't exist or didn't match the metadataPrefix,
			// the XML should be an error explaining this.
			// Otherwise it should be the XML for the OAI record
			if(format == null || !format.getName().equals(metadataPrefix))
			{
				log.warn("The record with OAI identifier " + identifier + " did not match the metadataPrefix " + metadataPrefix + ".");

				LogWriter.addWarning(service.getHarvestOutLogFileName(), "The record with the OAI Identifier " + identifier + " did not match the metadata format " + metadataPrefix + ".");
				warningCount++;

				bean.setXmlResponse(XMLUtil.xmlTag("error", Constants.ERROR_CANNOT_DISSEMINATE_FORMAT, new String[]{"code", "cannotDisseminateFormat"}));
			}
			else
			{
				if(log.isDebugEnabled())
					log.debug("Setting the record with OAI identifier " + identifier + " on the XML parameter of the form.");

				LogWriter.addInfo(service.getHarvestOutLogFileName(), "Found the record with the OAI Identifier " + identifier + ".");

				bean.setXmlResponse(XMLUtil.xmlTag("GetRecord", record.getOaiXml().replaceAll("<\\?xml.*\\?>", "")));
			}
		}
	}

	/**
	 * Returns the OAI XML for a list of records or identifiers.
	 *
	 * @param from The earliest date for returned records or identifiers.  If null or empty the earliest date out of all records will be used
	 * @param until The latest date for returned records or identifiers.  If null or empty the current date will be used
	 * @param metadataPrefix The type of metadata which should be returned
	 * @param set The set which is being queried
	 * @param resumptionTokenId The resumption token's ID
	 * @param getRecords true if we should return the full records, false if we should only return the headers
	 * @return The XML containing a list of headers or record and header combinations as well as a resumption token.
	 *         These should be included in the response's ListRecords or ListIdentifiers element.
	 * @throws DatabaseConfigException
	 */
	private String handleRecordLists(String from, String until, String metadataPrefix, String set, String resumptionTokenId, boolean getRecords)
		throws DatabaseConfigException, IndexException
	{
		if(log.isDebugEnabled())
			log.debug("Entering handleRecordLists");

		// Get the maximum number of records we should return at a time from the configuration file
		// This value is different depending on whether we're returning the full records or just the identifiers
		int recordLimit = (getRecords ? Integer.parseInt(configuration.getProperty(Constants.CONFIG_OAI_REPO_MAX_RECORDS)) :
			                            Integer.parseInt(configuration.getProperty(Constants.CONFIG_OAI_REPO_MAX_IDENTIFIERS)));

		// Get the maximum length of the results returned in bytes
		// This value is different depending on whether we're returning the full records or just the identifiers
		int maxLength = (getRecords ? Integer.parseInt(configuration.getProperty(Constants.CONFIG_OAI_REPO_MAX_RECORDS_LENGTH)) :
			                          Integer.parseInt(configuration.getProperty(Constants.CONFIG_OAI_REPO_MAX_IDENTIFIERS_LENGTH)));

		// The from and until dates.  They will be null if the passed Strings could not be parsed
		Date fromDate = parseDate(from);
		Date untilDate = parseDate(until);

		//The Format and Set Objects associated with the OAI request
		Format format;
		Set setObject = null;

		// If there was a resumption token, get it from the database
		ResumptionToken resToken = null;
		if(resumptionTokenId != null)
		{
			if(log.isDebugEnabled())
				log.debug("The request had a resumption token with ID " + resumptionTokenId);

			// Get the resumption token
			resToken = resumptionTokenDao.getById(Long.parseLong(resumptionTokenId));

			// Return an error if the resumption token could not be found
			if(resToken == null)
			{
				log.warn("Could not find the resumption token with ID " + resumptionTokenId);

				LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI " + verb + " request contained an unrecognized resumption token \"" + resumptionTokenId + "\".");
				warningCount++;

				return ErrorBuilder.badResumptionTokenError();
			}

			// Set the values from the resumption token rather than keeping the ones parsed from the request
			fromDate = resToken.getFrom();
			untilDate = resToken.getUntil();
			set = resToken.getSetSpec();
			metadataPrefix = resToken.getMetadataFormat();

			// Get the format ID for the requested metadataPrefix
			format = formatDao.getByName(metadataPrefix);

			// Get the Set Object for the requested set
			if(set != null)
			{
				setObject = setDao.getBySetSpec(set);

				// If the set they asked for didn't exist, return an error
				if(setObject == null)
				{
					log.warn("The requested set could not be found.  set was " + set + ".");

					LogWriter.addWarning(service.getHarvestOutLogFileName(), "The requested set \"" + set + "\" could not be found.");
					warningCount++;

					return XMLUtil.xmlTag("error", Constants.ERROR_BAD_SET, new String[]{"code", "badArgument"});
				}
			}
		}
		else
		{
			format = formatDao.getByName(metadataPrefix);

			// Get the Set Object for the requested set
			if(set != null)
			{
				setObject = setDao.getBySetSpec(set);

				// If the set they asked for didn't exist, return an error
				if(setObject == null)
				{
					log.warn("The requested set could not be found.  set was " + set + ".");

					LogWriter.addWarning(service.getHarvestOutLogFileName(), "The requested set \"" + set + "\" could not be found.");
					warningCount++;

					return XMLUtil.xmlTag("error", Constants.ERROR_BAD_SET, new String[]{"code", "badArgument"});
				}
			}
		}

		// Return an error if the format was null
		if(format == null)
		{
			log.warn("The requested metadataPrefix could not be found.  metadataPrefix was " + metadataPrefix + ".");

			LogWriter.addWarning(service.getHarvestOutLogFileName(), "The requested metadataPrefix \"" + metadataPrefix + "\" could not be found.");
			warningCount++;

			return XMLUtil.xmlTag("error", Constants.ERROR_NO_RECORDS_MATCH, new String[]{"code", "noRecordsMatch"});
		}

		// The offset into the returned results which we should start from
		int offset = (resToken == null ? 0 : resToken.getOffset());
		
		// If from is null, set it to the minimum possible value
		// Otherwise set it to the same value as fromDate
		if(fromDate == null) {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(new Date(0));
			c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - ((c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET))/(60*60*1000)));
			fromDate = c.getTime();
		}

		// If to is null, set it to now
		// Otherwise set it to the same value as toDate
		if(untilDate == null) {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(new Date());
			c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - ((c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET))/(60*60*1000)));

			untilDate = c.getTime();
		}

		// Get records from offset to record limit
		SolrBrowseResult result = recordService.getOutgoingRecordsInRange(fromDate, untilDate, setObject, format.getId(), offset, recordLimit, serviceId);

		// Total number of records satisfying the criteria. This is not the number of records loaded. 
		long totalRecords = result.getTotalNumberOfResults();

		// The XML for the OAI result
		StringBuffer xml = new StringBuffer();

		// If there were no records returned, set an error signifying that no records matched.
		// Otherwise, append data for each returned record to the result and insert a resumption token
		// to the database if needed
		if(totalRecords == 0)
		{
			LogWriter.addInfo(service.getHarvestOutLogFileName(), "There were no records which matched the parameters provided in the " + verb + " request.");

			xml.append(XMLUtil.xmlTag("error", Constants.ERROR_NO_RECORDS_MATCH, new String[]{"code", "noRecordsMatch"}));
		}
		else
		{
			
			// True if there are more results remaining than we can return at once
			boolean hasMore = (totalRecords > (offset + recordLimit));

			if(log.isDebugEnabled())
				log.debug("Returning results " + offset + " - " + offset + recordLimit + " " + (set == null ? "" : "of set " + set + " ") + " and format " + format.getId() + (from == null ? "" : "from " + from + " ") + (until == null ? "" : "until " + until) + ".");

			// The number of records returned
			int returnedRecordsCount = 0;

			// Add whitespace to make the result more readable
			xml.append("\n");
			
			// Append XML for each record to the result
			for(Record record:result.getRecords())
			{
				// If we're to get the records, append the record's OAI XML.
				// Otherwise, we're just supposed to get the identifiers, so
				// append the record's OAI header
				if(getRecords) {
					xml.append("<record>\n")
					          .append(record.getOaiHeader().replaceAll("<\\?xml.*\\?>", ""))
					          .append("\n<metadata>\n")
					          .append(record.getOaiXml().replaceAll("<\\?xml.*\\?>", ""))
					          .append("\n</metadata>\n")
					          .append("\n</record>\n");
				} else {
			    	xml.append(record.getOaiHeader().replaceAll("<\\?xml.*\\?>", "")).append("\n");
				}
				// Increment the counter for the number of records returned
				returnedRecordsCount++;

				// If the length of the results excedes the maximum allowed length,
				// break from the loop
				if(xml.length() >= maxLength)
				{
					if(log.isDebugEnabled())
						log.debug("Breaking from the loop because after adding " + returnedRecordsCount + " records, the length of the results is " + xml.length() + " which exceeds the maximum allowed length of " + maxLength);

					hasMore = true;
					break;
				}
			}

			// Increase the offset by the number of records we returned
			offset += returnedRecordsCount;

			// If there are more results which need to be returned in a future call, set up a resumption token
			if(hasMore)
			{
				// If there was already a resumption token, update it with the new offset
				if(resToken != null)
				{
					if(log.isDebugEnabled())
						log.debug("Updating resumption token with ID " + resToken.getId() + " to have offset " + offset);

					resToken.setOffset(offset);

					try
					{
						if(resumptionTokenDao.update(resToken))
						{
							xml.append(XMLUtil.xmlTag("resumptionToken", "" + resToken.getId(),
										new String[] { "cursor", "" + offset, "completeListSize", ""+totalRecords } ));

							LogWriter.addInfo(service.getHarvestOutLogFileName(), "Returning " + totalRecords + " records and the resumptionToken " + resToken.getId() + " in response to the " + verb + " request.");
						}
					}
					catch(DataException e)
					{
						log.error("Could not update the resumption token.", e);
					}
				}
				// There was not already a resumption token, so insert one.
				else
				{
					// The resumption token to return with the result
					ResumptionToken newResToken = new ResumptionToken();

					// Set the fields on the resumption token
					newResToken.setFrom(fromDate);
					newResToken.setUntil(untilDate);
					newResToken.setSetSpec(set);
					newResToken.setOffset(offset);
					newResToken.setMetadataFormat(metadataPrefix);

					// Insert the new resumption token, this will set the resumption token's ID as a side effect
					try
					{
						if(resumptionTokenDao.insert(newResToken))
						{
							xml.append(XMLUtil.xmlTag("resumptionToken", "" + newResToken.getId(),
										new String[] { "cursor", "" + offset, "completeListSize", ""+totalRecords } ));

							LogWriter.addInfo(service.getHarvestOutLogFileName(), "Returning " + totalRecords + " records and the resumptionToken " + newResToken.getId() + " in response to the " + verb + " request.");
						}
					}
					catch(DataException e)
					{
						log.error("Could not insert the new resumption token.", e);
					}
				}
			}
			// else there are no records which they haven't harvested.
			// If there was a resumption token, delete it
			else
			{
				if(resToken != null)
				{
					try
					{
						xml.append(XMLUtil.xmlTag("resumptionToken", null,
								new String[] { "cursor", "" + offset, "completeListSize", ""+totalRecords } ));

						LogWriter.addInfo(service.getHarvestOutLogFileName(), "Returning " + totalRecords + " records and a null resumptionToken in response to the " + verb + " request.");

						resumptionTokenDao.delete(resToken);
					}
					catch(DataException e)
					{
						log.error("An error occurred while deleting the resumption token.", e);
					}
				}
				else
					LogWriter.addInfo(service.getHarvestOutLogFileName(), "Returning " + totalRecords + " records and no resumptionToken in response to the " + verb + " request.");
			}
		}

		return XMLUtil.xmlTag((getRecords ? "ListRecords" : "ListIdentifiers"), xml.toString());
	}

	/**
	 * Given a String containing a date in the yyyy-MM-dd'T'HH:mm:ssZ format, returns
	 * a Date Object parsed from that String.  If the String could not be parsed, returns
	 * null
	 *
	 * @param dateString A String with a date in the yyyy-MM-dd'T'HH:mm:ssZ format
	 * @return A Date Object which was parsed from dateString, or null if it could not be parsed
	 */
	private Date parseDate(String dateString)
	{
		if(log.isDebugEnabled())
			log.debug("Parsing a date from the String " + dateString);

		// If the passed string was null, return null
		if(dateString == null || dateString.length() <= 0)
			return null;

		// Try to parse a date from the String.  If the parse fails, return null
		try
		{
			dateString = dateString.replace('T', ' ');
			dateString = dateString.replaceFirst("Z", "");
			dateString = dateString.replaceFirst("z", "");
			
			// Parse assuming granularity is to the nearest second
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		}
		catch(ParseException e)
		{
			try
			{
				// Granularity wasn't to the nearest second, try to the nearest day instead
				return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			}
			catch(ParseException e1)
			{
				log.warn("Could not parse a date from the String " + dateString, e1);

				return null;
			}
		}
	}
}
