/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.harvester;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.DefaultSetDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.utils.LogWriter;

/**
 * This class contains methods for validating a repository's adherence to the OAI protocol using
 * the Identify, ListSets, and ListMetadataFormats OAI requests.
 *
 * @author Eric Osisek
 */
public class ValidateRepository implements ErrorHandler
{
	/**
	 * Data access object for getting and updating providers
	 */
	private static ProviderDAO providerDao = new DefaultProviderDAO();

	/**
	 * Data access object for getting formats
	 */
	private static FormatDAO formatDao = new DefaultFormatDAO();

	/**
	 * Data access object for getting sets
	 */
	private static SetDAO setDao = new DefaultSetDAO();

	/**
	 * A list of XML errors in the OAI response
	 */
	private String xmlerrors;

	/**
	 * A list of XML warnings in the OAI response
	 */
	private String xmlwarnings;

	/**
	 * How long the Harvester should wait for a response from the OAI server before giving up and throwing an error
	 */
	private int timeOutMilliseconds = 180000;

    /**
     * The ID of the provider we're validating
     */
	private int providerId = -1;

	/**
	 * The URL of the provider we're checking
	 */
	private String baseUrl = null;

	/**
	 * A reference to the logger which writes to the HarvestIn log file
	 */
	private static Logger log = Logger.getLogger("harvestIn");

	private Provider provider = null;

	/**
	 * Signifies that the OAI repository uses yyyy-mm-dd granularity in its timestamps
	 */
	public final static int GRAN_DAY = 1;

	/**
	 * Signifies that the OAI repository uses yyyy-mm-ddthh:mm:ssz granularity in its timestamps
	 */
	public final static int GRAN_SECOND = 2;

	/**
	 * The granularity of the OAI repository we're harvesting (either GRAN_DAY or GRAN_SECOND)
	 */
	private int granularity = -1;

	/**
	 * Signifies that the OAI repository does not track deleted records
	 */
	public final static int DELETED_RECORD_NO = 0;

	/**
	 * Signifies that the OAI repository maintains transient deletes for records
	 */
	public final static int DELETED_RECORD_TRANSIENT = 1;

	/**
	 * Signifies that the OAI repository maintains persistant deletes for records
	 */
	public final static int DELETED_RECORD_PERSISTENT = 2;

	/**
	 * The policy for tracking deleted records that the OAI repository uses (either DELETED_RECORD_NO, DELETED_RECORD_TRANSIENT, or DELETED_RECORD_PERSISTENT)
	 */
	private int deletedRecord = -1;

	/**
	 * HttpClient used for making OAI requests
	 */
	private HttpClient client = null;
	
	/**
	 * Constructs the ValidateRepository Object
	 */
	public ValidateRepository()
	{
		HttpClientParams params = new HttpClientParams();
		params.setSoTimeout(timeOutMilliseconds);
		client = new HttpClient(params, new MultiThreadedHttpConnectionManager());
	}
	
	/**
	 * Validates the OAI provider with the passed provider ID
	 *
	 * @param providerId The ID of the provider we're validating
	 * @throws Hexception If the validation failed
	 * @throws DatabaseConfigException 
	 */
	public void validate(int providerId) throws Hexception, DatabaseConfigException
	{
		this.providerId = providerId;

		provider = providerDao.getById(providerId);

		if(provider == null)
			throw new Hexception("Cannot find the provider with ID " + providerId);

		baseUrl = provider.getOaiProviderUrl();

		StringBuilder errors = new StringBuilder();

		// Validate the Identify OAI verb
		try
		{
			checkIdentifyInfo();
			provider.setIdentify(true);
		} // end try(validate identify)
		catch(Exception e)
		{
			provider.setIdentify(false);
			errors.append(e.getMessage()).append("\n");
		} // end catch(Exception)

		// Validate the listSets verb
		try
		{
			checkSets();
			provider.setListSets(true);
		} // end try(validate listSets)
		catch(Exception e)
		{
			provider.setListSets(false);
			errors.append(e.getMessage()).append("\n");
		} // end catch(Exception)

		// Validate the listMetadataFormats verb
		try
		{
			checkFormats();
			provider.setListFormats(true);
		} // end try(validate listMetadataFormats)
		catch(Exception e)
		{
			provider.setListFormats(false);
			errors.append(e.getMessage()).append("\n");
		} // end catch(Exception)

		// Mark the provider as being valid for harvesting if and only if
		// it passed validation for the identify, listSets, and listMetadataFormats verbs
		provider.setService(provider.getIdentify() && provider.getListSets() && provider.getListFormats());

		try
		{
			providerDao.update(provider);
		} // end try(update the provider)
		catch(DataException e)
		{
			errors.append("Error updating the provider in the database.\n");
		} // end catch(DataException)

		if(errors.length() > 0)
			throw new Hexception(errors.toString());
	} // end method validate(int)

	/**
	 * Validates the OAI provider with the passed provider ID as part of a harvest,
	 * and as such the problems which occur are logged under that harvest.
	 *
	 * @param providerId The provider we're validating
	 * @param harvestId The harvest ID we should use for logging errors
	 * @throws Hexception If there was no provider with the passed provider ID
	 * @throws DatabaseConfigException 
	 */
	public void validate(int providerId, int harvestId) throws Hexception, DatabaseConfigException
	{
		this.providerId = providerId;

		provider = providerDao.getById(providerId);

		if(provider == null)
			throw new Hexception("Cannot find the provider with ID " + providerId);

		baseUrl = provider.getOaiProviderUrl();

		StringBuilder errors = new StringBuilder();

		// Validate the Identify OAI verb
		try
		{
			checkIdentifyInfo();
			provider.setIdentify(true);
		} // end try(validate identify)
		catch(Exception e)
		{
			provider.setIdentify(false);
			errors.append(e.getMessage()).append("\n");
		} // end catch(Exception)

		// Validate the listSets verb
		try
		{
			checkSets();
			provider.setListSets(true);
		} // end try(validate listSets)
		catch(Exception e)
		{
			provider.setListSets(false);
			errors.append(e.getMessage()).append("\n");
		} // end catch(Exception)

		// Validate the listMetadataFormats verb
		try
		{
			checkFormats();
			provider.setListFormats(true);
		} // end try(validate listMetadataFormats)
		catch(Exception e)
		{
			provider.setListFormats(false);
			errors.append(e.getMessage()).append("\n");
		} // end catch(Exception)

		// Mark the provider as being valid for harvesting if and only if
		// it passed validation for the identify, listSets, and listMetadataFormats verbs
		provider.setService(provider.getIdentify() && provider.getListSets() && provider.getListFormats());

		try
		{
			providerDao.update(provider);
		} // end try(update the provider)
		catch(DataException e)
		{
			errors.append("Error updating the provider in the database.\n");
		} // end catch(DataException)

		if(errors.length() > 0)
			throw new Hexception(errors.toString());
	} // end method validate(int, int)

	/**
	 * This method checks the OAI repository's implementation of the Identify verb for validity and
	 * sets the granularity the provider uses to represent dates and the type of deleted record support
	 * it implements. Granularity is used for the "from" and "until" arguments: either GRAN_DAY or
	 * GRAN_SECOND. Deleted record support is used to determine whether incremental updates are supported.
	 *
	 * @exception Hexception If an error occurred while validating the Identify verb
	 * @exception OAIErrorException If OAI error was returned by the OAI provider
	 * @throws DatabaseConfigException 
	 */
	public void checkIdentifyInfo() throws Hexception, OAIErrorException, DatabaseConfigException
	{
		String request = baseUrl + "?verb=Identify";
		if (log.isDebugEnabled())
			log.debug("A request for Identify has been made. Establishing connection with the data provider...");

		LogWriter.addInfo(provider.getLogFileName(),"Making the OAI Identify request.  Request is " + request);

		Document doc = getDoc(request);

		Element root = doc.getDocumentElement();
		Element errorElement = findChild(root, "error");

		if (errorElement != null)
		{
			String oaiErrCode = errorElement.getAttribute("code");
			String errMsg = getContent(errorElement);
			if (errMsg == null)
				errMsg = "";

			throw new OAIErrorException(oaiErrCode, getContent(errorElement));
		}

		Element verbele = mustFindChild(root, "Identify");

		Element granele = null;
		try
		{
			granele = mustFindChild(verbele, "granularity");
		}
		catch (Hexception e)
		{
			// Check for protocol version v1.x:
			try
			{
				mustFindChild(root, "protocolVersion");
			}
			catch (Throwable te)
			{
				throw new Hexception("The data provider returned an invalid response to the Identify request: " + e.getMessage());
			}

			granularity = GRAN_DAY;
		}

		// Set the supported date granularity
		String gran = getContent(granele).toLowerCase();

		if (gran.equals("yyyy-mm-dd"))
		{
			granularity = GRAN_DAY;

			if (log.isDebugEnabled())
				log.debug("granularity: day");

			LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider supports DAY granularity.");
		}
		else if (gran.equals("yyyy-mm-ddthh:mm:ssz"))
		{
			granularity = GRAN_SECOND;

			if (log.isDebugEnabled())
				log.debug("granularity: second");

			LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider supports SECOND granularity.");
		}
		else
		{
			LogWriter.addError(provider.getLogFileName(), "Provider supports an invalid granularity according to the OAI protocol. Invalid response: " + gran);
			provider.setErrors(provider.getErrors() + 1);

			throw new Hexception("Provider supports an invalid granularity according to the OAI protocol. Invalid response: " + gran);
		}

		Element deletedredele = null;
		try
		{
			deletedredele = mustFindChild(verbele, "deletedRecord");
		}
		catch (Hexception e)
		{
			LogWriter.addError(provider.getLogFileName(), "The data provider returned an invalid response to the Identify request.");
			provider.setErrors(provider.getErrors() + 1);

			throw new Hexception("The data provider returned an invalid response to the Identify request: " + e.getMessage());
		}

		// Set the level of deleted record support
		String deletedRecordString = getContent(deletedredele);

		if (deletedRecordString.equalsIgnoreCase("no"))
		{
			deletedRecord = DELETED_RECORD_NO;

			if (log.isDebugEnabled())
				log.debug("deleted record: no");

			LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider contains NO support for deleted records.");
		}
		else if (deletedRecordString.equalsIgnoreCase("transient"))
		{
			deletedRecord = DELETED_RECORD_TRANSIENT;

			if (log.isDebugEnabled())
				log.debug("deleted record: transient");

			LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider contains TRANSIENT support for deleted records.");
		}
		else if (deletedRecordString.equalsIgnoreCase("persistent"))
		{
			deletedRecord = DELETED_RECORD_PERSISTENT;

			if (log.isDebugEnabled())
				log.debug("deleted record: persistent");

			LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider contains PERSISTENT support for deleted records.");
		}
		else
		{
			LogWriter.addError(provider.getLogFileName(), "Provider supports an invalid deleted record support according to the OAI protocol. Invalid response: " + deletedRecordString);
			provider.setErrors(provider.getErrors() + 1);

			throw new Hexception("Provider shows an invalid deleted record support according to the OAI protocol. Invalid response: " + deletedRecordString);
		}

		provider = providerDao.getById(providerId);
		
		// Check the protocol version
		try
		{
			Element versionElem = mustFindChild(verbele, "protocolVersion");
			provider.setProtocolVersion(getContent(versionElem));
		}
		catch (Hexception e)
		{
			throw new Hexception("The data provider returned an invalid response to the Identify request: " + e.getMessage());
		}

		provider.setIdentify(true);
	}

	/**
	 * This method runs a ListMetadataFormats request against the OAI provider and updates the
	 * formats_to_providers table to match.  It then disables any schedules for a format that
	 * is no longer supported
	 *
	 * @return The current list of metadata formats supported by the OAI provider
	 * @exception Hexception If an error occurred while validating the Identify verb
	 * @exception OAIErrorException If OAI error was returned by the OAI provider
	 * @throws DatabaseConfigException 
	 */
	public List<Format> checkFormats() throws Hexception, OAIErrorException, DatabaseConfigException
	{
		// Get the formats currently supported by the repository
		List<Format> currentFormats = getPrefices(baseUrl);

		// A list of the formats that were supported by the repository the last time we checked
		List<Format> oldFormats = provider.getFormats();

		// For each old format, if the old format was not in the list of current formats,
		// remove it from the provider
		for(Format oldFormat : oldFormats)
			if(!currentFormats.contains(oldFormat))
				provider.removeFormat(oldFormat);

		// If there's a metadata format that the provider supports which wasn't in the old list
		// of supported formats, mark in the database that the provider supports it now.
		for(Format currentFormat : currentFormats)
			provider.addFormat(currentFormat);

		return currentFormats;
	} // end method checkFormats

	/**
	 * This method runs a ListMetadataSets request against the OAI provider and updates the
	 * sets_to_providers table to match.  It then disables any schedules for a set that
	 * is no longer supported
	 *
	 * @return The current list of metadata sets supported by the OAI provider
	 * @exception Hexception If an error occurred while validating the Identify verb
	 * @exception OAIErrorException If OAI error was returned by the OAI provider
	 * @throws DatabaseConfigException 
	 */
	public List<Set> checkSets() throws Hexception, OAIErrorException, DatabaseConfigException
	{
		// Get the sets currently supported by the repository
		List<Set> currentSets = getSets(baseUrl);

		// Get the set IDs that were supported by the repository the last time we checked
		List<Set> oldSets = setDao.getSetsForProvider(providerId);

		// Loop over the current sets and add them to the provider
		for(Set currentSet : currentSets)
			provider.addSet(currentSet);

		// Loop over the old set IDs and get the name of each old set
		// Remove each old set was not in the list of current sets from the provider
		for(Set oldSet : oldSets)
			if(!currentSets.contains(oldSet))
				provider.removeSet(oldSet);

		return currentSets;
	} // end method checkSets()

	/**
	 * Gets the provider's granularity
	 *
	 * @return The provider's granularity
	 */
	public int getGranularity()
	{
		return granularity;
	} // end method getGranularity()

	/**
	 * Gets the provider's deleted record support
	 *
	 * @return The provider's deleted record support
	 */
	public int getDeletedRecordSupport()
	{
		return deletedRecord;
	} // end method getDeletedRecordSupport()

	/**
	 * Retrieves an OAI XML document via http and parses the XML.
	 *
	 * @param request The http request, e.g., "http://www.x.com/..."
	 * @return The doc value
	 * @exception Hexception If an error occurs
	 */
	private Document getDoc(String request)throws Hexception
	{
		if(log.isDebugEnabled())
			log.debug("Sending the OAI request: " + request);

		Document doc = null;
		
		GetMethod getOaiResponse = null;
		
		try
		{
			int statusCode = 0; // The status code in the HTTP response
			
			long startOaiRequest = System.currentTimeMillis();

			synchronized(client)
			{
				// Instantiate a GET HTTP method to get the Voyager "first" page
				getOaiResponse = new GetMethod(request);
				
				// Execute the get method to get the Voyager "first" page
				statusCode = client.executeMethod(getOaiResponse);
			}
			
			// If the get was successful (200 is the status code for success)
	        if(statusCode == 200)
	        {	        
	        	InputStream istm = getOaiResponse.getResponseBodyAsStream();
				long finishOaiRequest = System.currentTimeMillis();
	
	            log.info("Time taken to get a response from the server " + (finishOaiRequest-startOaiRequest));
	
				DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
				docfactory.setCoalescing(true);
				docfactory.setExpandEntityReferences(true);
				docfactory.setIgnoringComments(true);
				docfactory.setNamespaceAware(true);
	
				// We must set validation false since jdk1.4 parser
				// doesn't know about schemas.
				docfactory.setValidating(false);
	
				// Ignore whitespace doesn't work unless setValidating(true),
				// according to javadocs.
				docfactory.setIgnoringElementContentWhitespace(false);
	
				DocumentBuilder docbuilder = docfactory.newDocumentBuilder();
	
				xmlerrors = "";
				xmlwarnings = "";
				docbuilder.setErrorHandler(this);
				doc = docbuilder.parse(istm);
				istm.close();
	
				if (xmlerrors.length() > 0 || xmlwarnings.length() > 0)
				{
					String msg = "XML validation failed.\n";
	
					if (xmlerrors.length() > 0)
					{
						msg += "Errors:\n" + xmlerrors;
						LogWriter.addError(provider.getLogFileName(), "The OAI provider's response had the following XML errors:\n" + xmlerrors);
						provider.setErrors(provider.getErrors() + 1);
					}
					if (xmlwarnings.length() > 0)
					{
						msg += "Warnings:\n" + xmlwarnings;
						LogWriter.addWarning(provider.getLogFileName(), "The OAI provider's response had the following XML warnings:\n" + xmlwarnings);
						provider.setWarnings(provider.getWarnings() + 1);
					}
	
					throw new Hexception(msg);
				}
	        }
	        else
	        {
	        	String msg = "Error getting the HTML document, the HTTP status code was " + statusCode;
	        	
	        	log.error(msg);
	        	
	        	LogWriter.addError(provider.getLogFileName(), msg);
	        	provider.setErrors(provider.getErrors() + 1);
				
				throw new Hexception(msg);
	        }
		}
		catch (Exception exc)
		{
			String msg = "";

			if (exc.getMessage().matches(".*respcode.*"))
				msg = "The request for data resulted in an invalid response from the provider. The baseURL indicated may be incorrect or the service may be unavailable. HTTP response: " + exc.getMessage();
			else
				msg = "The request for data resulted in an invalid response from the provider. Error: " + exc.getMessage();

			LogWriter.addError(provider.getLogFileName(), msg);
			provider.setErrors(provider.getErrors() + 1);

			throw new Hexception(msg);
		}
		return doc;
	}

	/**
	 * Finds the first immediate child of the specified Element having the specified tag; throws Hexception if
	 * none found.
	 *
	 * @param ele The element to get the child of
	 * @param tag The child we're looking for
	 * @return The requested child of the passed element
	 * @exception Hexception If the child could not be found
	 */
	private Element mustFindChild(Element ele, String tag) throws Hexception
	{
		Element res = findChild(ele, tag);
		if (res == null)
		{
			LogWriter.addError(provider.getLogFileName(), "A required element \"" + tag + "\" was missing from the OAI response");
			provider.setErrors(provider.getErrors() + 1);

			throw new Hexception("Required element not found: \"" + tag + "\"");
		}

		return res;
	}

	/**
	 * Finds the first immediate child of the specified Element having the specified tag; returns null if none
	 * found.
	 *
	 * @param ele The element to get the child of
	 * @param tag The child we're looking for
	 * @return The requested child of the passed element, or null if there was no child of the
	 *         passed element with the requested tag.
	 */
	private Element findChild(Element ele, String tag)
	{
		Element res = null;
		Node nd = ele.getFirstChild();

		while (nd != null)
		{
			if (nd.getNodeType() == Node.ELEMENT_NODE && nd.getNodeName().equals(tag))
			{
				res = (Element) nd;
				break;
			}

			nd = nd.getNextSibling();
		}

		return res;
	}

	/**
	 * Finds the first following sibling of the specified Element having the specified tag; returns null if none
	 * found.
	 *
	 * @param ele The element to get the sibling of
	 * @param tag The sibling we're looking for
	 * @return The requested sibling of the passed element, or null if there was no sibling of the
	 *         passed element with the requested tag.
	 */
	private Element findSibling(Element ele, String tag)
	{
		Element res = null;
		Node nd = ele.getNextSibling();

		while (nd != null)
		{
			if (nd.getNodeType() == Node.ELEMENT_NODE && nd.getNodeName().equals(tag))
			{
				res = (Element) nd;
				break;
			}

			nd = nd.getNextSibling();
		}
		return res;
	}

	/**
	 * Returns the concatenation of all text node content under the specified node.
	 *
	 * @param nd The node to get the content for
	 * @return The concatenation of all text node content under the specified node.
	 */
	private String getContent(Node nd)
	{
		StringBuffer resbuf = new StringBuffer();
		getContentSub(nd, resbuf);
		return resbuf.toString();
	}


	/**
	 *  Appends to resbuf the concatenation of all text node content under the specified node.
	 *
	 * @param nd The node to get the content for
	 * @param resbuf The buffer to hold the results
	 */
	private void getContentSub(Node nd, StringBuffer resbuf)
	{
		switch (nd.getNodeType())
		{
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				resbuf.append(nd.getNodeValue().trim());
				break;
			case Node.ELEMENT_NODE:
				// recurse on children
				Node subnd = nd.getFirstChild();
				while (subnd != null)
				{
					getContentSub(subnd, resbuf);
					subnd = subnd.getNextSibling();
				}
				break;
			default: // ignore all else
		}
	}

	/**
	 * Returns an array of the legal metadataFormats for the specified host.
	 *
	 * @param baseURL The URL of the repository to check
	 * @return A list of metadata prefixes
	 * @exception Hexception If an internal error occurred
	 * @exception OAIErrorException If an OAI error occurred
	 * @throws DatabaseConfigException 
	 */
	private List<Format> getPrefices(String baseURL) throws Hexception, OAIErrorException, DatabaseConfigException
	{
		String request = baseURL + "?verb=ListMetadataFormats";

		if (log.isDebugEnabled())
			log.debug("A request for ListMetadataFormats has been made. Establishing connection with the data provider... ");

		LogWriter.addInfo(provider.getLogFileName(), "Making the OAI ListMetadataFormats request.  Request is " + request);

		Document doc = getDoc(request);

		Element root = doc.getDocumentElement();
		Element errele = findChild(root, "error");
		if (errele != null)
		{
			String oaiErrCode = errele.getAttribute("code");
			String errMsg = getContent(errele);

			if (errMsg == null)
				errMsg = "";

			try
			{
				Provider provider = providerDao.getById(providerId);
				provider.setService(false);
				provider.setListFormats(false);
				providerDao.update(provider);
			}
			catch(DataException e)
			{
				log.error("Error updating the provider object.", e);
			}

			throw new OAIErrorException(oaiErrCode, getContent(errele));
		}

		Element verbele = mustFindChild(root, "ListMetadataFormats");

		ArrayList<Format> reslist = new ArrayList<Format>();
		Element pfxele = mustFindChild(verbele, "metadataFormat");

		while (pfxele != null)
		{
			String formatName = getContent(mustFindChild(pfxele, "metadataPrefix")).replaceAll(":", "/");

			Format format = formatDao.getByName(formatName);

			if(format == null)
			{
				format = new Format();

				format.setName(formatName);
				format.setSchemaLocation(getContent(mustFindChild(pfxele, "schema")));
				format.setNamespace(getContent(mustFindChild(pfxele, "metadataNamespace")));

				try
				{
					formatDao.insert(format);
				}
				catch(DataException e)
				{
					log.error("A data exception occurred while inserting a new Format.", e);

					throw new Hexception(e.getMessage());
				}
			}

			LogWriter.addInfo(provider.getLogFileName(), "Found the MetadataPrefix " + format.getName());

			reslist.add(format);

			pfxele = findSibling(pfxele, "metadataFormat");
		}

		return reslist;
	}

	/**
	 * Returns an array of the legal sets for the specified host.
	 *
	 * @param baseURL The URL of the repository to check
	 * @return A list of sets
	 * @exception Hexception If an internal error occurred
	 * @exception OAIErrorException If an OAI error occurred
	 * @throws DatabaseConfigException 
	 */
	private List<Set> getSets(String baseURL) throws Hexception, OAIErrorException, DatabaseConfigException
	{
		String request = baseURL + "?verb=ListSets";

		if (log.isDebugEnabled())
			log.debug("A request for ListSets has been made. Establishing connection with the data provider... ");

		LogWriter.addInfo(provider.getLogFileName(), "Making the OAI ListSets request.  Request is " + request);

		// The resumption token returned by the ListSets request
		String resumptionToken = null;

		List<Set> reslist = new ArrayList<Set>();

		Provider provider = providerDao.getById(providerId);

		do
		{
			Document doc = getDoc(request + (resumptionToken == null ? "" : "&resumptionToken=" + resumptionToken));

			Element root = doc.getDocumentElement();
			Element errele = findChild(root, "error");
			if (errele != null)
			{
				String oaiErrCode = errele.getAttribute("code");
				String errMsg = getContent(errele);

				if (errMsg == null)
					errMsg = "";

				// If the error was that the provider doesn't use sets, return an empty list of sets
				// instead of throwing an Exception
				if(oaiErrCode.equals("noSetHierarchy"))
					return reslist;

				try
				{
					provider.setService(false);
					provider.setListSets(false);
					providerDao.update(provider);
				}
				catch(DataException e)
				{
					log.error("Error updating the provider object.", e);
				}

				throw new OAIErrorException(oaiErrCode, getContent(errele));
			}

			Element verbele = mustFindChild(root, "ListSets");
			Element setele = mustFindChild(verbele, "set");

			Element resumptionTokenEle = findSibling(setele, "resumptionToken");
			if(resumptionTokenEle != null)
				resumptionToken = getContent(resumptionTokenEle);

			while (setele != null)
			{
				String setSpec = getContent(mustFindChild(setele, "setSpec"));
				String setName = getContent(mustFindChild(setele, "setName"));

				Set set = setDao.getBySetSpec(setSpec);

				if(set == null)
				{
					set = new Set();

					set.setDisplayName(setName);
					set.setSetSpec(setSpec);

					try
					{
						setDao.insertForProvider(set, providerId);
					}
					catch(DataException e)
					{
						log.error("A data exception occurred while inserting a new Set.", e);

						throw new Hexception(e.getMessage());
					}
				}

				LogWriter.addInfo(provider.getLogFileName(), "Found the setSpec " + set.getSetSpec());

				reslist.add(set);

				setele = findSibling(setele, "set");
			}
		}while(resumptionToken != null && resumptionToken.length() > 0);

		return reslist;
	}

	/**
	 *  Handles fatal errors. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void fatalError(SAXParseException exc)
	{
		xmlerrors += exc;
	}

	/**
	 *  Handles errors. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void error(SAXParseException exc)
	{
		xmlerrors += exc;
	}

	/**
	 * Handles warnings. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void warning(SAXParseException exc)
	{
		xmlwarnings += exc;
	}
}


