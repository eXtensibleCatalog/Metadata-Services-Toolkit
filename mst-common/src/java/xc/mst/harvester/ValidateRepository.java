/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.harvester;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.utils.LogWriter;
import xc.mst.utils.XmlHelper;

/**
 * This class contains methods for validating a repository's adherence to the OAI protocol using
 * the Identify, ListSets, and ListMetadataFormats OAI requests.
 *
 * @author Eric Osisek
 */
public class ValidateRepository extends HttpService {
	
	private static Logger LOG = Logger.getLogger("harvestIn");
	
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
	
	private int providerId = -1;
	private String baseUrl = null;
	private Provider provider = null;

	/**
	 * The granularity of the OAI repository we're harvesting (either GRANULARITY_DAY or GRANULARITY_SECOND)
	 */
	private String granularity = null;

	/**
	 * The policy for tracking deleted records that the OAI repository uses (either DELETED_RECORD_NO, DELETED_RECORD_TRANSIENT, or DELETED_RECORD_PERSISTENT)
	 */
	private int deletedRecord = -1;
	
	/**
	 * Validates the OAI provider with the passed provider ID
	 *
	 * @param providerId The ID of the provider we're validating
	 * @throws DatabaseConfigException 
	 */
	public void validate(int providerId) throws DatabaseConfigException {
		this.providerId = providerId;
		provider = getProviderDAO().getById(providerId);

		if(provider == null)
			throw new RuntimeException("Cannot find the provider with ID " + providerId);

		baseUrl = provider.getOaiProviderUrl();

		StringBuilder errors = new StringBuilder();

		// Validate the Identify OAI verb
		try {
			checkIdentifyInfo();
			provider.setIdentify(true);
			provider.setGranularity(getGranularity());
		} catch(Exception e) {
			provider.setIdentify(false);
			LOG.error("", e);
			errors.append(e.getMessage()).append("\n");
		} 
		
		try {
			checkSets();
			provider.setListSets(true);
		} catch(Exception e) {
			provider.setListSets(false);
			LOG.error("", e);
			errors.append(e.getMessage()).append("\n");
		}
		
		try {
			checkFormats();
			provider.setListFormats(true);
		} catch(Exception e) {
			provider.setListFormats(false);
			LOG.error("", e);
			errors.append(e.getMessage()).append("\n");
		}

		// Mark the provider as being valid for harvesting if and only if
		// it passed validation for the identify, listSets, and listMetadataFormats verbs
		provider.setService(provider.getIdentify() && provider.getListSets() && provider.getListFormats());

		try {
			getProviderDAO().update(provider);
		} catch(DataException e) {
			LOG.error("", e);
			errors.append("Error updating the provider in the database.\n");
		}

		if(errors.length() > 0)
			throw new RuntimeException(errors.toString());
	}

	public void checkIdentifyInfo() throws DatabaseConfigException, HttpException {
		String request = baseUrl + "?verb=Identify";

		LogWriter.addInfo(provider.getLogFileName(),"Making the OAI Identify request.  Request is " + request);

		Document doc = sendRequest(request);

		Element root = doc.getRootElement();
		Element errorElement = root.getChild("error", root.getNamespace());

		if (errorElement != null) {
			String oaiErrCode = errorElement.getAttribute("code").getValue();
			throw new RuntimeException(oaiErrCode+": "+errorElement.getText());
		}
		
		LOG.debug(new XmlHelper().getString(root));
		Element identifyEl = root.getChild("Identify", root.getNamespace());
		LOG.debug("identifyEl: "+identifyEl);
		LOG.debug("root.getChildren(): "+root.getChildren());
		
		Element protocolVersionEl = identifyEl.getChild("protocolVersion", root.getNamespace());
		if (protocolVersionEl != null) {
			provider.setProtocolVersion(protocolVersionEl.getText());
		} else {
			throw new RuntimeException("The data provider did not specific protocolVersion.");
		}

		Element granularityEl = identifyEl.getChild("granularity", root.getNamespace());
		if (granularityEl != null) {
			String granText = granularityEl.getText().toLowerCase();
	
			if (granText.equals("yyyy-mm-dd")) {
				granularity = Provider.DAY_GRANULARITY;
				LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider supports DAY granularity.");
			} else if (granText.equals("yyyy-mm-ddthh:mm:ssz")) {
				granularity = Provider.SECOND_GRANULARITY;
				LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider supports SECOND granularity.");
			} else {
				String msg = "Invalid granularity:" + granText;
				LogWriter.addError(provider.getLogFileName(), msg);
				provider.setErrors(provider.getErrors() + 1);
				throw new RuntimeException(msg);
			}
		} else {
			granularity = Provider.DAY_GRANULARITY;
		}

		Element deletedRecordEl = identifyEl.getChild("deletedRecord", root.getNamespace());
		if (deletedRecordEl != null) {
			String deletedRecordText = deletedRecordEl.getText();

			if (deletedRecordText.equalsIgnoreCase("no")) {
				deletedRecord = DELETED_RECORD_NO;
				LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider contains NO support for deleted records.");
			} else if (deletedRecordText.equalsIgnoreCase("transient")) {
				deletedRecord = DELETED_RECORD_TRANSIENT;
				LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider contains TRANSIENT support for deleted records.");
			} else if (deletedRecordText.equalsIgnoreCase("persistent")) {
				deletedRecord = DELETED_RECORD_PERSISTENT;
				LogWriter.addInfo(provider.getLogFileName(), "Found that the OAI provider contains PERSISTENT support for deleted records.");
			} else {
				LogWriter.addError(provider.getLogFileName(), "Provider supports an invalid deleted record support according to the OAI protocol. Invalid response: " + deletedRecordText);
				provider.setErrors(provider.getErrors() + 1);
				throw new RuntimeException("Provider shows an invalid deleted record support according to the OAI protocol. Invalid response: " + deletedRecordText);
			}
		} else {
			throw new RuntimeException("deletedRecord cannot be null.");
		}

		provider = getProviderDAO().getById(providerId);
		provider.setIdentify(true);
	}

	/**
	 * This method runs a ListMetadataFormats request against the OAI provider and updates the
	 * formats_to_providers table to match.  It then disables any schedules for a format that
	 * is no longer supported
	 *
	 * @return The current list of metadata formats supported by the OAI provider
	 * @throws DatabaseConfigException 
	 */
	public List<Format> checkFormats() throws DatabaseConfigException, HttpException {
		// Get the formats currently supported by the repository
		List<Format> currentFormats = getMetadataFormats(baseUrl);

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
	 * @throws DatabaseConfigException 
	 */
	public List<Set> checkSets() throws DatabaseConfigException, HttpException {
		// Get the sets currently supported by the repository
		List<Set> currentSets = getSets(baseUrl);

		// Get the set IDs that were supported by the repository the last time we checked
		List<Set> oldSets = getSetDAO().getSetsForProvider(providerId);

		// Loop over the current sets and add them to the provider
		for(Set currentSet : currentSets)
			provider.addSet(currentSet);

		// Loop over the old set IDs and get the name of each old set
		// Remove each old set was not in the list of current sets from the provider
		for(Set oldSet : oldSets)
			if(!currentSets.contains(oldSet))
				provider.removeSet(oldSet);

		return currentSets;
	}

	public String getGranularity() {
		return granularity;
	}

	public int getDeletedRecordSupport() {
		return deletedRecord;
	}

	/**
	 * Returns an array of the legal metadataFormats for the specified host.
	 *
	 * @param baseURL The URL of the repository to check
	 * @return A list of metadata prefixes
	 * @throws DatabaseConfigException 
	 */
	@SuppressWarnings("unchecked")
	private List<Format> getMetadataFormats(String baseURL) throws DatabaseConfigException, HttpException {
		String request = baseURL + "?verb=ListMetadataFormats";

		if (LOG.isDebugEnabled())
			LOG.debug("sending request: "+request);

		Document doc = sendRequest(request);

		Element root = doc.getRootElement();
		Element errorEl = root.getChild("error", root.getNamespace());
		if (errorEl != null) {
			String errorCode = errorEl.getAttribute("code").getValue();

			try {
				Provider provider = getProviderDAO().getById(providerId);
				provider.setService(false);
				provider.setListFormats(false);
				getProviderDAO().update(provider);
			} catch(DataException e) {
				LOG.error("Error updating the provider object.", e);
			}

			throw new RuntimeException("oaiErrCode: "+errorCode+" "+errorEl.getText());
		}

		Element listMetadataFormatsEl = root.getChild("ListMetadataFormats", root.getNamespace());

		ArrayList<Format> metadataFormats = new ArrayList<Format>();
		
		List listMetadataFormatsEls = listMetadataFormatsEl.getChildren("metadataFormat", root.getNamespace());

		for (Object listMetadataFormatsElObj : listMetadataFormatsEls) {
			Element metadataFormatEl = (Element)listMetadataFormatsElObj;
			String formatName = metadataFormatEl.getChildText("metadataPrefix", root.getNamespace());

			Format format = getFormatDAO().getByName(formatName);

			if(format == null) {
				format = new Format();

				format.setName(formatName);
				format.setSchemaLocation(metadataFormatEl.getChildText("schema", root.getNamespace()));
				format.setNamespace(metadataFormatEl.getChildText("metadataNamespace", root.getNamespace()));

				try {
					getFormatDAO().insert(format);
				} catch(DataException e) {
					LOG.error("A data exception occurred while inserting a new Format.", e);
					throw new RuntimeException(e);
				}
			}

			LogWriter.addInfo(provider.getLogFileName(), "Found the MetadataPrefix " + format.getName());

			metadataFormats.add(format);
		}

		return metadataFormats;
	}

	/**
	 * Returns an array of the legal sets for the specified host.
	 *
	 * @param baseURL The URL of the repository to check
	 * @return A list of sets
	 * @throws DatabaseConfigException 
	 */
	@SuppressWarnings("unchecked")
	private List<Set> getSets(String baseURL) throws DatabaseConfigException, HttpException
	{
		String request = baseURL + "?verb=ListSets";

		LogWriter.addInfo(provider.getLogFileName(), "Making the OAI ListSets request.  Request is " + request);

		// The resumption token returned by the ListSets request
		String resumptionToken = null;

		List<Set> setList = new ArrayList<Set>();

		Provider provider = getProviderDAO().getById(providerId);

		do {
			Document doc = sendRequest(request + (resumptionToken == null ? "" : "&resumptionToken=" + resumptionToken));

			Element root = doc.getRootElement();
			Element errorEl = root.getChild("error", root.getNamespace());
			if (errorEl != null) {
				String oaiErrCode = errorEl.getAttributeValue("code");
				String errorText = errorEl.getText();

				// If the error was that the provider doesn't use sets, return an empty list of sets
				// instead of throwing an Exception
				if(oaiErrCode.equals("noSetHierarchy"))
					return setList;

				try {
					provider.setService(false);
					provider.setListSets(false);
					getProviderDAO().update(provider);
				} catch(DataException e) {
					LOG.error("Error updating the provider object.", e);
				}

				throw new RuntimeException("oaiErrCode: "+oaiErrCode+" "+errorText);
			}

			Element listSetsEl = root.getChild("ListSets", root.getNamespace());
			List<Element> setEls = listSetsEl.getChildren("set", root.getNamespace());

			Element resumptionTokenEle = listSetsEl.getChild("resumptionToken", root.getNamespace());
			if(resumptionTokenEle != null)
				resumptionToken = resumptionTokenEle.getText();

			for (Element setEl : setEls) {
				String setSpec = setEl.getChildText("setSpec", root.getNamespace());
				String setName = setEl.getChildText("setName", root.getNamespace());

				Set set = getSetDAO().getBySetSpec(setSpec);

				if(set == null) {
					set = new Set();

					set.setDisplayName(setName);
					set.setSetSpec(setSpec);

					try {
						getSetDAO().insertForProvider(set, providerId);
					} catch(DataException e) {
						LOG.error("A data exception occurred while inserting a new Set.", e);
						throw new RuntimeException(e);
					}
				}

				LogWriter.addInfo(provider.getLogFileName(), "Found the setSpec " + set.getSetSpec());

				setList.add(set);
			}
		} while(resumptionToken != null && resumptionToken.length() > 0);

		return setList;
	}

}


