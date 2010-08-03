/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import xc.mst.constants.Constants;
import xc.mst.oai.Facade;
import xc.mst.oai.OaiRequestBean;

import com.opensymphony.xwork2.ActionSupport;

public class TestOAIRepositoryServlet extends ActionSupport  implements ServletRequestAware, ServletResponseAware
{

	/** Request */
	private HttpServletRequest request;
	

	/** Response */
	private HttpServletResponse servletResponse;
	
	/**
	 * A reference to the logger for this class
	 */
//	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	public String  execute() throws Exception
	{
//		log.info(" ***************** Start testing *******************") ;
//		
//		Date startTime = new Date();
//		String resumptionToken = request.getParameter("resumptionToken");
//		int loopCount = Integer.parseInt(request.getParameter("loopCount"));
//		String format = request.getParameter("format");
//		int serviceid = Integer.parseInt(request.getParameter("serviceId"));
//		String from = request.getParameter("from");
//		String until = request.getParameter("until");
//		String set = request.getParameter("set");
//
//		Date startTimeFor1req = new Date();
//
//		
//		// Bean to manage data for handling the request
//		OaiRequestBean bean = new OaiRequestBean();
//
//		// Set parameters on the bean based on the OAI request's parameters
//		bean.setVerb("ListRecords");
//		bean.setMetadataPrefix(format);
//		bean.setServiceId(serviceid);
//		bean.setFrom(from);
//		bean.setUntil(until);
//		bean.setSet(set);
//
//		
//		String oaiRepoBaseURL = "http://localhost:8080/MetadataServicesToolkit/oaiRepository";
//		
//		// Create the Facade Object, which will compute the results of the request and set them on the bean
//		Facade facade = new Facade(bean, oaiRepoBaseURL);
//
//		StringBuffer b = new StringBuffer("http://localhost:8080/MetadataServicesToolkit/Normalization Service/oaiRepository");
//		// Set the response header on the facade Object
//		facade.setResponseHeader(b);
//
//		// Execute the correct request on the Facade Object
//		facade.execute();
//		
//		// Build the OAI response
//		StringBuilder oaiResponseElement = new StringBuilder();
//
//		// Append the header
//		oaiResponseElement.append(Constants.OAI_RESPONSE_HEADER);
//
//		// Append the response date element
//		oaiResponseElement.append(bean.getResponseDateElement()).append("\n");
//
//		// Append the request element
//		oaiResponseElement.append(bean.getRequestElement()).append("\n");
//
//		// Append the response itself
//		oaiResponseElement.append(bean.getXmlResponse()).append("\n");
//
//		// Append the footer
//		oaiResponseElement.append(Constants.OAI_RESPONSE_FOOTER);
//
//	//	String oaiXMLOutput = oaiResponseElement.toString();
//
////		System.out.println(oaiXMLOutput);
//	
//		Date endTimeforreq = new Date();
//		long diffForReq = endTimeforreq.getTime() - startTimeFor1req.getTime();
//		log.info(" Harvested records=" + 1000 + " Time taken in ms:"+ diffForReq+ "  Total time taken:" + (int)((diffForReq)/60000) + " mins " + ((diffForReq)/1000) % 60 + " sec") ;
//
//		for (int i=0;i<loopCount;i++) {
//			startTimeFor1req = new Date();
//
//		
//			// Bean to manage data for handling the request
//			bean = new OaiRequestBean();
//	
//			// Set parameters on the bean based on the OAI request's parameters
//			bean.setVerb("ListRecords");
//			bean.setMetadataPrefix(format);
//			bean.setServiceId(serviceid);
//			bean.setResumptionToken(resumptionToken);
//	
//			
////			oaiRepoBaseURL = "http://localhost:8080/MetadataServicesToolkit/oaiRepository";
//			
//			// Create the Facade Object, which will compute the results of the request and set them on the bean
//			facade = new Facade(bean, oaiRepoBaseURL);
//	
////			b = new StringBuffer("http://localhost:8080/MetadataServicesToolkit/Aggregation Service/oaiRepository");
//			// Set the response header on the facade Object
//			facade.setResponseHeader(b);
//	
//			// Execute the correct request on the Facade Object
//			facade.execute();
//			
//			// Build the OAI response
//			oaiResponseElement = new StringBuilder();
//	
//			// Append the header
//			oaiResponseElement.append(Constants.OAI_RESPONSE_HEADER);
//	
//			// Append the response date element
//			oaiResponseElement.append(bean.getResponseDateElement()).append("\n");
//	
//			// Append the request element
//			oaiResponseElement.append(bean.getRequestElement()).append("\n");
//	
//			// Append the response itself
//			oaiResponseElement.append(bean.getXmlResponse()).append("\n");
//	
//			// Append the footer
//			oaiResponseElement.append(Constants.OAI_RESPONSE_FOOTER);
//	
////			oaiXMLOutput = oaiResponseElement.toString();
////			
////			servletResponse.setContentType("text/xml; charset=UTF-8");
////			
////			// Write the response
////			servletResponse.getWriter().write(oaiResponseElement.toString());
//	
//		
//			endTimeforreq = new Date();
//			diffForReq = endTimeforreq.getTime() - startTimeFor1req.getTime();
//			
//			log.info(" Harvested records=" + ((i+2) * 1000) + " Time taken in ms:"+ diffForReq+ "  Total time taken:" + (int)((diffForReq)/60000) + " mins " + ((diffForReq)/1000) % 60 + " sec") ;
//			
//		}
//		
//		Date endTime = new Date();
//		long diff = endTime.getTime() - startTime.getTime();
//		log.info(" ********************************* Finished harvest out- Time taken in ms:"+ diff+ "  Total time taken:" + (int)((endTime.getTime() - startTime.getTime())/60000) + " mins " + ((endTime.getTime() - startTime.getTime())/1000) % 60 + " sec") ;
//		servletResponse.getWriter().write("Time taken in ms:"+ diff+ "  Total time taken:" + (int)((endTime.getTime() - startTime.getTime())/60000) + " mins " + ((endTime.getTime() - startTime.getTime())/1000) % 60 + " sec") ;
//		

		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}
	
}

