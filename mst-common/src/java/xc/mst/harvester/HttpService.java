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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.jdom.Document;

import xc.mst.manager.BaseService;
import xc.mst.utils.XmlHelper;

public class HttpService extends BaseService {
	
	public static final Logger LOG = Logger.getLogger(HttpService.class);
	
	protected int timeOutMilliseconds = 180000;
	
	protected XmlHelper xmlHelper = new XmlHelper();
	
	protected HttpClient client = null;

	public HttpService() {
		HttpClientParams params = new HttpClientParams();
		int timeOutMilliseconds = Integer.parseInt(config.getProperty("harvest.timeout.ms", this.timeOutMilliseconds+""));
		params.setSoTimeout(timeOutMilliseconds);
		client = new HttpClient(params, new MultiThreadedHttpConnectionManager());
	}

	public Document sendRequest(String request) {
		if(LOG.isDebugEnabled())
			LOG.debug("Sending the OAI request: " + request);

		Document doc = null;
		
		GetMethod getOaiResponse = null;
		InputStream istm = null;
		
		try {
			int statusCode = 0; // The status code in the HTTP response
			
			long startOaiRequest = System.currentTimeMillis();

			getOaiResponse = new GetMethod(request);

			// Execute the get method to get the Voyager "first" page
			statusCode = client.executeMethod(getOaiResponse);
			
			// If the get was successful (200 is the status code for success)
	        if (statusCode == 200) {       
	        	istm = getOaiResponse.getResponseBodyAsStream();
				long finishOaiRequest = System.currentTimeMillis();
	            LOG.info("Time taken to get a response from the server " + (finishOaiRequest-startOaiRequest));
				doc = xmlHelper.getJDomDocument(istm);
	        } else {
				throw new RuntimeException("The HTTP status code was " + statusCode);
	        }
		} catch (Throwable t) {
        	String msg = "Error getting the HTML document for the request: "+request;
			LOG.error(msg, t);
		} finally {
			if (istm != null) {
				try {
					istm.close();
				} catch (Throwable t) {
					LOG.error("could not close connection.", t);
				}
			}
		}
		return doc;
	}

}
