/**
  * Copyright (c) 2009 eXtensible Catalog Organization
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
	/** The verb used in the request */
	private String verb;

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
	
	/** OAI repository'e base URL  */
	private String oaiRepoBaseURL;

	/** The ID of the service who's OAI repository was invoked. */
	private int serviceId = -1;

	/** GetRecord's identifier parameter */
	private String identifier;
	
	/** Because the request is embedded in the output, we need to know what it is */
	protected String request = null;

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

	/**
	 * Get from date to harvest records from
	 * 
	 * @return from from date to harvest records from
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * Set from date to harvest records from
	 * 
	 * @param from from date to harvest records from
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

	/**
	 * Get format of the record to harvest
	 * 
	 * @return format of the record to harvest
	 */
	public String getMetadataPrefix()
	{
		return metadataPrefix;
	}

	/**
	 * Set format of the record to harvest
	 * 
	 * @param metadataPrefix format of the record to harvest
	 */
	public void setMetadataPrefix(String metadataPrefix)
	{
		this.metadataPrefix = metadataPrefix;
	}

	/**
	 * Get resumption token
	 * 
	 * @return resumption token
	 */
	public String getResumptionToken()
	{
		return resumptionToken;
	}

	/**
	 * Set resumption token
	 * 
	 * @param resumptionToken resumption token
	 */
	public void setResumptionToken(String resumptionToken)
	{
		this.resumptionToken = resumptionToken;
	}

	/**
	 * Get set spec of record to harvest
	 * 
	 * @return set of record to harvest
	 */
	public String getSet()
	{
		return set;
	}

	/**
	 * Set set spec of record to harvest
	 * 
	 * @param set  set spec of record to harvest
	 */
	public void setSet(String set)
	{
		this.set = set;
	}

	/**
	 * Get until date
	 *  
	 * @return until date
	 */
	public String getUntil()
	{
		return until;
	}

	/**
	 * Set until date 
	 * 
	 * @param until until date of record to harvest
	 */
	public void setUntil(String until)
	{
		this.until = until;
	}

	/**
	 * Get requested OAI identifier 
	 *  
	 * @return oai identifier
	 */
	public String getIdentifier()
	{
		return identifier;
	}

	/**
	 * Set requested OAI identifier
	 * 
	 * @param identifier requested OAI identifier
	 */
	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Get service id
	 * 
	 * @return service id
	 */
	public int getServiceId() {
		return serviceId;
	}

	/**
	 * Set service id
	 * 
	 * @param serviceId service id
	 */
	public void setServiceId(int serviceId)
	{
		this.serviceId = serviceId;
	}

	/**
	 * Get oai repository base URL
	 * 
	 * @return oai repository base URL
	 */
	public String getOaiRepoBaseURL() {
		return oaiRepoBaseURL;
	}

	/**
	 * Set oai repository base URL
	 * 
	 * @param oaiRepoBaseURL oai repository base URL
	 */
	public void setOaiRepoBaseURL(String oaiRepoBaseURL) {
		this.oaiRepoBaseURL = oaiRepoBaseURL;
	}
	
	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
}