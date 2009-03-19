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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.utils.LogWriter;

/**
 * A simple servlet that parses an OAI request and passes the parameters to the Facade class for processing.
 *
 * @author Eric Osisek
 */
public class OaiRepository extends HttpServlet
{
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 56789L;

	/**
	 * Data access object for getting services
	 */
	private static ServiceDAO serviceDao = new DefaultServiceDAO();

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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if(log.isDebugEnabled())
			log.debug("In doGet, parsing out parameters");

		// Get the port on which the request is coming in.  This will
		// tell us which service's records to expose.
		int port = request.getLocalPort();

		// Get the service based on the port.
		Service service = serviceDao.getByPort(port);

		LogWriter.addInfo(service.getHarvestOutLogFileName(), "Received the OAI request " + request.getRequestURL());

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

		// Create the Facade Object, which will compute the results of the request and set them on the bean
		Facade facade = new Facade(bean);

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

		// Write the response
	    response.getWriter().write(oaiResponseElement.toString());
	}

	/**
	 * The doPost method of the servlet. This method is called when a form has its tag value method equals to
	 * post.  It invokes the doGet method.
	 *
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if(log.isDebugEnabled())
			log.debug("Received a POST request, passing through the doGet method.");

		doGet(request, response);
	}
}
