/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */

package xc.mst.action.browse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.struts2.interceptor.ServletResponseAware;

import xc.mst.bo.record.FacetFilter;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.bo.service.ErrorCode;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.BrowseRecordService;
import xc.mst.manager.record.DefaultBrowseRecordService;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;

/**
 * Browse records
 * 
 * @author Sharmila Ranganathan
 *
 */
public class BrowseRecords extends Pager implements ServletResponseAware {

	/** Generated Id */
	private static final long serialVersionUID = -2599819216740526000L;
	
	/** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/** Service to search record */
	private BrowseRecordService browseRecordService = new DefaultBrowseRecordService();
	
	/** Browse result */
	private SolrBrowseResult result;
	
	/** Facet Name selected */
	private String addFacetName;
	
	/** Facet values selected */
	private String addFacetValue;
	
	/** Facet name to be removed */
	private String removeFacetName;
	
	/** Facet value to be removed */
	private String removeFacetValue;
	
	/** Facet names separated by | */
	private String selectedFacetNames = "";
	
	/** Facet values separated by | */
	private String selectedFacetValues ="";
	
	/** Search text entered by user */
	private String query = "";
	
	/** Id of record */
	private int recordId;
	
	/** Record XML */
	private String recordXML;
	
	/** Response */
	private HttpServletResponse servletResponse;
	
	/** The end row number to retrieve */ 
	private int rowEnd;
	
	/** Denotes whether to search XML or not */ 
	private boolean searchXML;

	/** Denotes whether its initial page load */
	private boolean isInitialLoad;
	
	/** Record for which successor are queried and displayed */
	private Record successorRecord;
	
	/** Record for which predecessor are queried and displayed */
	private Record predecessorRecord;
	
	/** Error to display its information */
	private String error;
	
	/** Error description */
	private String errorDescription;
	
	/** Record to be viewed */
	private Record record;
	
	/**
     * Exceute method to load initial screen with just the facets
     */
	public String execute() {
		searchXML = true;
		isInitialLoad = true;
		browse();
		return SUCCESS;
	}
	
	/**
	 * Search for records
	 */
	public String browse() {
		log.debug("query::"+query);
		
		SolrQuery solrQuery = new SolrQuery();

		// If Query is empty retrieve all records
		if ((query == null) || (query.equals(""))) {
			solrQuery.setQuery("*:*");
		}	else {
			if (searchXML) {
				solrQuery.setQuery(query + " OR " + "oai_xml:" + query.replaceAll(":", "\\\\:"));
			} else {
				solrQuery.setQuery(query.replaceAll(":", "\\\\:"));
			}
		}
		
		StringTokenizer nameTokenizer = new StringTokenizer(selectedFacetNames, "|");
		StringTokenizer valueTokenizer = new StringTokenizer(selectedFacetValues, "|");
		
		while (nameTokenizer.hasMoreTokens()) {
	    	solrQuery.addFilterQuery(nameTokenizer.nextToken() + ":\"" + valueTokenizer.nextToken().replaceAll(":", "\\\\:") + "\"");
		}
	    
		// Add selected facet to query
	    if (addFacetName != null && addFacetName.length() > 0) {
	    	solrQuery.addFilterQuery(addFacetName + ":\"" + addFacetValue.replaceAll(":", "\\\\:") + "\"");
	    	// Add facet names and values to | separated list
	    	selectedFacetNames = selectedFacetNames + "|" + addFacetName;
	    	selectedFacetValues = selectedFacetValues + "|" + addFacetValue;
	    }
	    
	    

	    // Remove selected facet from query
	    if (removeFacetName != null && removeFacetName.length() > 0) {
	    	solrQuery.removeFilterQuery(removeFacetName + ":\"" + removeFacetValue.replaceAll(":", "\\\\:") + "\"");
	    }

	    log.debug("query after adding/removing facet ::"+query);
	    log.debug("selectedFacetNames after Add ::"+selectedFacetNames);
	    log.debug("selectedFacetValues after Add ::"+selectedFacetValues);
	
	    // Create facet names and values List
	    StringTokenizer facetNameTokenizer = new StringTokenizer(selectedFacetNames, "|");
	    List<String> facetNamesList = new ArrayList<String>();
	    
	    StringTokenizer facetValueTokenizer = new StringTokenizer(selectedFacetValues, "|");
	    List<String> facetValuesList = new ArrayList<String>();

	    
	    StringBuffer newSelectedFacetNames = new StringBuffer();
	    StringBuffer newSelectedFacetValues = new StringBuffer();
	    String myNameToken = "";
	    String myValueToken = "";
	    while (facetNameTokenizer.hasMoreTokens()) {
	    	myNameToken = facetNameTokenizer.nextToken();
	    	myValueToken = facetValueTokenizer.nextToken();
	    	if (removeFacetName != null && removeFacetName.length() > 0) {
	    		// Create facet names String separated by | 
	    		if (!(removeFacetName.equalsIgnoreCase(myNameToken) &&  removeFacetValue.equalsIgnoreCase(myValueToken))) {
		    		newSelectedFacetNames.append("|");
		    		newSelectedFacetNames.append(myNameToken);
		    		facetNamesList.add(myNameToken);
		    		
		    		newSelectedFacetValues.append("|");
	    			newSelectedFacetValues.append(myValueToken);
	    			facetValuesList.add(myValueToken);
		    	}
	    	} else {
	    		facetNamesList.add(myNameToken);
	    		facetValuesList.add(myValueToken);
	    	}
	    }
	    
	    log.debug("Final facetNamesList:"+facetNamesList);
	    log.debug("Final facetValuesList:"+facetValuesList);
	    
	    if (removeFacetValue != null && removeFacetValue.length() > 0) {
	    	selectedFacetNames = newSelectedFacetNames.toString();
	    	selectedFacetValues = newSelectedFacetValues.toString();
	    }

	    log.debug("Final after remove selectedFacetNames:"+selectedFacetNames);
	    log.debug("Final after remove selectedFacetValues:"+selectedFacetValues);
	    
	    // Query formation
		
		solrQuery.setFacet(true)
	    		 .setFacetMinCount(1);
		
		solrQuery.addFacetField("provider_name");
		solrQuery.addFacetField("service_name");
		solrQuery.addFacetField("format_name");
		solrQuery.addFacetField("set_name");
		solrQuery.addFacetField("harvest_start_time");
		solrQuery.addFacetField("error");
		
	    
		rowEnd = rowStart + numberOfResultsToShow;
		
		solrQuery.setStart(rowStart);
		solrQuery.setRows(numberOfResultsToShow);
	    result = browseRecordService.search(solrQuery);   
	    
	    log.debug("result::"+result);
	    
	    if((result != null) && (rowEnd > result.getTotalNumberOfResults()))
		{
			rowEnd = result.getTotalNumberOfResults();
		}
		
	    // Add facet name and value list to SolrBrowseResult(result) object for display in UI
	    if (result != null) {
		    for(int i = 0; i < facetNamesList.size(); i++) {
			    // Get successor/predecessor of the record to display its information
		    	if (facetNamesList.get(i).equalsIgnoreCase("successor")) {
		    		RecordService recordService = new DefaultRecordService();
		    		successorRecord = recordService.getById(Long.parseLong(facetValuesList.get(i)));
		    	}
		    	if (facetNamesList.get(i).equalsIgnoreCase("processed_from")) {
		    		RecordService recordService = new DefaultRecordService();
		    		predecessorRecord = recordService.getById(Long.parseLong(facetValuesList.get(i)));
		    	}

		    	result.addFacetFilter(new FacetFilter(facetNamesList.get(i), facetValuesList.get(i)));
		    }
	    }
		

		return SUCCESS;
	}
	
	/**
     * View record XML
     */
	public String viewRecord() throws IOException {
		
		RecordService recordService = new DefaultRecordService();
		record = recordService.getById(recordId);
		recordXML = record.getOaiXml();
		recordXML = recordXML.replaceAll("<", "&lt;");
		recordXML = recordXML.replaceAll(">", "&gt;");
		
		return SUCCESS;
	}

	/**
     * View error description
     */
	public String viewErrorDescription() throws IOException {
		
		log.debug("viewErrorDescription: error: " + error);

		ServicesService servicesService = new DefaultServicesService();
		int indexOfHypen = error.indexOf("-");
		Service service = servicesService.getServiceById(Integer.parseInt(error.substring(0, indexOfHypen)));
		
		// Get service id
		String errorCode = error.substring(indexOfHypen + 1, error.indexOf(":"));
		ErrorCode error = browseRecordService.getError(errorCode, service);

		// Get error code
		FileInputStream fis = new FileInputStream(error.getErrorDescriptionFile());
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);
  
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		StringBuffer buffer = new StringBuffer();
		String strLine;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			buffer.append(strLine);
		} 
	      
	    errorDescription = buffer.toString();
		
	    return SUCCESS;
	}
	
	public SolrBrowseResult getResult() {
		return result;
	}


	public void setResult(SolrBrowseResult result) {
		this.result = result;
	}


	public String getRemoveFacetName() {
		return removeFacetName;
	}


	public void setRemoveFacetName(String removeFacetName) {
		this.removeFacetName = removeFacetName;
	}


	public String getRemoveFacetValue() {
		return removeFacetValue;
	}


	public void setRemoveFacetValue(String removeFacetValue) {
		this.removeFacetValue = removeFacetValue;
	}


	public String getAddFacetName() {
		return addFacetName;
	}


	public void setAddFacetName(String addFacetName) {
		this.addFacetName = addFacetName;
	}


	public String getAddFacetValue() {
		return addFacetValue;
	}


	public void setAddFacetValue(String addFacetValue) {
		this.addFacetValue = addFacetValue;
	}


	public String getSelectedFacetNames() {
		return selectedFacetNames;
	}


	public void setSelectedFacetNames(String selectedFacetNames) {
		this.selectedFacetNames = selectedFacetNames;
	}


	public String getSelectedFacetValues() {
		return selectedFacetValues;
	}


	public void setSelectedFacetValues(String selectedFacetValues) {
		this.selectedFacetValues = selectedFacetValues;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public String getRecordXML() {
		return recordXML;
	}

	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}
	
	public int getRowEnd() {
		return rowEnd;
	}

	public void setRowEnd(int rowEnd) {
		this.rowEnd = rowEnd;
	}

	/**
	 * Returns the total number of hits 
	 * 
	 * @see xc.mst.action.browse.Pager#getTotalHits()
	 */
	public int getTotalHits() {
		return result.getTotalNumberOfResults();
	}

	public boolean isSearchXML() {
		return searchXML;
	}

	public void setSearchXML(boolean searchXML) {
		this.searchXML = searchXML;
	}

	public boolean isInitialLoad() {
		return isInitialLoad;
	}

	public void setInitialLoad(boolean isInitialLoad) {
		this.isInitialLoad = isInitialLoad;
	}

	public Record getSuccessorRecord() {
		return successorRecord;
	}

	public Record getPredecessorRecord() {
		return predecessorRecord;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public Record getRecord() {
		return record;
	}
}
