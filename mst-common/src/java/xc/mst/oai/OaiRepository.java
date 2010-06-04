/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.oai;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;

/**
 * A simple servlet that parses an OAI request and passes the parameters to the Facade class for processing.
 *
 * @author Eric Osisek
 */
public class OaiRepository extends ActionSupport implements ServletRequestAware, ServletResponseAware
{
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 56789L;

	/** Request */
	private HttpServletRequest request;

	/** Response */
	private HttpServletResponse response;

	/** XML output */
	private String oaiXMLOutput;

	/**
	 * A reference to the logger which writes to the HarvestOut log file
	 */
	private static Logger log = Logger.getLogger(Constants.LOGGER_HARVEST_OUT);

	/**
	 * The doGet method of the servlet. This method is called when a form has its tag value method equals to get.
	 * It parses the OAI request parameters into an OaiRequestBean and calls the Facade class to process the request.
	 *
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public String execute() throws ServletException, IOException
	{
		if(log.isDebugEnabled())
			log.debug("In doGet, parsing out parameters");

		try
		{
			// Get servlet path from request 
			String servletPath = request.getServletPath();
			
			// Extract the service name from servlet path
			int firstOccuranceOfSlash = servletPath.indexOf("/");
			
			String serviceName = null;
			
			// Check if / exist in servlet path
			if (firstOccuranceOfSlash != -1) 
			{
				int secondOccuranceOfSlash = servletPath.indexOf("/", firstOccuranceOfSlash + 1);
			
				if ((firstOccuranceOfSlash != -1 && secondOccuranceOfSlash != -1) && (secondOccuranceOfSlash > firstOccuranceOfSlash))
					serviceName = servletPath.substring(firstOccuranceOfSlash + 1, secondOccuranceOfSlash);
				else // Invalid URL
					response.getWriter().write("Invalid URL");
			} 
			else // Invalid URL
				response.getWriter().write("Invalid URL");
	
			// Get the service based on the port.
			Service service = ((ServiceDAO)MSTConfiguration.getInstance().getBean("ServiceDAO")).getByServiceName(serviceName.replace("-", " "));
	
			if(service == null)
			{
				// Write the response
				response.getWriter().write("Invalid service name: " + serviceName);
		
			    return SUCCESS;
			}
			
			LogWriter.addInfo(service.getHarvestOutLogFileName(), "Received the OAI request " + request.getQueryString());
	
			// Bean to manage data for handling the request
			OaiRequestBean bean = new OaiRequestBean();
	
			// Set parameters on the bean based on the OAI request's parameters
			bean.setVerb(request.getParameter("verb"));
			bean.setFrom(request.getParameter("from"));
			bean.setUntil(request.getParameter("until"));
			bean.setMetadataPrefix(request.getParameter("metadataPrefix"));
			bean.setSet(request.getParameter("set"));
			bean.setIdentifier(request.getParameter("identifier"));
			bean.setResumptionToken(request.getParameter("resumptionToken"));
			bean.setServiceId(service != null ? service.getId() : 0);
	
			
			String oaiRepoBaseURL = "http://" + request.getServerName() + ":" +  request.getServerPort() + request.getContextPath() + "/" + serviceName + "/oaiRepository";

			// Create the Facade Object, which will compute the results of the request and set them on the bean
			Facade facade = new Facade(bean, oaiRepoBaseURL);
	
			// Set the response header on the facade Object
			facade.setResponseHeader(request.getRequestURL());
			
			// Execute the correct request on the Facade Object
			facade.execute();
	
			// Build the OAI response
			StringBuilder oaiResponseElement = new StringBuilder();
	
			// Append the header
			oaiResponseElement.append(Constants.OAI_RESPONSE_HEADER);
	
			// Append the response date element
			oaiResponseElement.append(bean.getResponseDateElement()).append("\n");
	
			// Append the request element
			oaiResponseElement.append(bean.getRequestElement()).append("\n");
	
			// Append the response itself
			oaiResponseElement.append(bean.getXmlResponse()).append("\n");
	
			// Append the footer
			oaiResponseElement.append(Constants.OAI_RESPONSE_FOOTER);
	
			oaiXMLOutput = oaiResponseElement.toString();
	
			response.setContentType("text/xml; charset=UTF-8");
			
			// Write the response
			response.getWriter().write(oaiResponseElement.toString());

		    return SUCCESS;
		}
		catch(DatabaseConfigException e)
		{	
			log.error("Cannot connect to the database with the parameters from the config file.", e);
			
			response.getWriter().write("Do to a configuration error, this OAI repository cannot access its database.");
			
			return ERROR;
		}
	}

	public HttpServletRequest getServletRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.request = servletRequest;
	}

	public HttpServletResponse getServletResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.response = servletResponse;
	}

	public String getOaiXMLOutput() {
		return oaiXMLOutput;
	}

	public void setOaiXMLOutput(String oaiXMLOutput) {
		this.oaiXMLOutput = oaiXMLOutput;
	}
}
