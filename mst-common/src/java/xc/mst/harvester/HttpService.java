/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.harvester;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;

import xc.mst.manager.BaseService;
import xc.mst.utils.XmlHelper;

public class HttpService extends BaseService {
	
	public static final Logger LOG = Logger.getLogger(HttpService.class);
	
	protected int timeOutMilliseconds = 180000;
	
	protected XmlHelper xmlHelper = new XmlHelper();
	
	protected HttpClient client = null;

	public void init() {
		client = new HttpClient(new MultiThreadedHttpConnectionManager());
		LOG.debug("client: "+client);
		LOG.debug("client.getParams(): "+client.getParams());
		client.getParams().setParameter("http.socket.timeout", 
				Integer.parseInt(config.getProperty("harvest.socket.timeout", this.timeOutMilliseconds+"")));
		client.getParams().setParameter("http.connection.timeout", 
				Integer.parseInt(config.getProperty("harvest.connection.timeout", this.timeOutMilliseconds+"")));
		client.getParams().setParameter("http.protocol.content-charset", 
				config.getProperty("harvest.protocol.content-charset", "utf-16"));
	}

	public Document sendRequest(String request) throws HttpException {
		if(LOG.isDebugEnabled())
			LOG.debug("Sending the OAI request: " + request);

		Document doc = null;
		
		GetMethod getOaiResponse = null;
		InputStream istm = null;
		Throwable t = null;
		
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
		} catch (Throwable t2) {
			t = t2;
		} finally {
			if (istm != null) {
				try {
					istm.close();
				} catch (Throwable t2) {
					LOG.error("could not close connection.", t2);
				}
			}
		}
		if (t != null) {
        	String msg = "Error getting the HTML document for the request: "+request;
			LOG.error(msg, t);
			throw new HttpException(msg);
		}
		return doc;
	}

}
