/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.oai;

/**
 * Bean which holds data for handling an OAI request
 */
public class OaiRequestBean
{
	/**
	 * The verb used in the request
	 */
	private String verb;

	/**
	 * XML response to the OAI request
	 */
	private String xmlResponse;

	/**
	 * The request element in the OAI response
	 */
	private String requestElement;

	/**
	 * The response date element in the OAI response
	 */
	private String responseDateElement;

	/**
	 * An optional argument with a UTCdatetime value, which specifies
	 * a lower bound for datestamp-based selective harvesting
	 */
	private String from;

	/**
	 * An optional argument with a UTCdatetime value, which specifies
	 * a upper bound for datestamp-based selective harvesting
	 */
	private String until;

	/**
	 * A required argument, which specifies that headers should be returned
	 * only if the metadata format matching the supplied metadataPrefix is
	 * available or, depending on the repository's support for deletions,
	 * has been deleted. The metadata formats supported by a repository and
	 * for a particular item can be retrieved using the ListMetadataFormats
	 * request.
	 */
	private String metadataPrefix;

	/**
	 * An optional argument with a setSpec value , which specifies set criteria
	 * for selective harvesting
	 */
	private String set;

	/**
	 * An exclusive argument with a value that is the flow control token returned
	 * by a previous ListIdentifiers request that issued an incomplete list.
	 */
	private String resumptionToken;

	/**
	 * The ID of the service who's OAI repository was invoked.
	 */
	private int serviceId = -1;

	/**
	 * GetRecord's identifier parameter
	 */
	private String identifier;

	public String getRequestElement()
	{
		return requestElement;
	}

	public void setRequestElement(String requestElement)
	{
		this.requestElement = requestElement;
	}

	public String getResponseDateElement()
	{
		return responseDateElement;
	}

	public void setResponseDateElement(String responseDateElement)
	{
		this.responseDateElement = responseDateElement;
	}

	/**
	 * Returns the verb.
	 * @return String
	 */
	public String getVerb()
	{
		return verb;
	}

	/**
	 * Set the verb.
	 * @param verb The verb to set
	 */
	public void setVerb(String verb)
	{
		this.verb = verb;
	}

	public String getXmlResponse()
	{
		return xmlResponse;
	}

	public void setXmlResponse(String xmlResponse)
	{
		this.xmlResponse = xmlResponse;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getMetadataPrefix()
	{
		return metadataPrefix;
	}

	public void setMetadataPrefix(String metadataPrefix)
	{
		this.metadataPrefix = metadataPrefix;
	}

	public String getResumptionToken()
	{
		return resumptionToken;
	}

	public void setResumptionToken(String resumptionToken)
	{
		this.resumptionToken = resumptionToken;
	}

	public String getSet()
	{
		return set;
	}

	public void setSet(String set)
	{
		this.set = set;
	}

	public String getUntil()
	{
		return until;
	}

	public void setUntil(String until)
	{
		this.until = until;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public int getServiceId()
	{
		return serviceId;
	}

	public void setServiceId(int serviceId)
	{
		this.serviceId = serviceId;
	}
}