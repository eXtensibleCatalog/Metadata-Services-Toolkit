/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */

package xc.mst.action.browse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.struts2.interceptor.ServletResponseAware;

import xc.mst.bo.record.FacetFilter;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.constants.Constants;
import xc.mst.manager.record.BrowseRecordService;
import xc.mst.manager.record.DefaultBrowseRecordService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Browse records
 * 
 * @author Sharmila Ranganathan
 *
 */
public class BrowseRecords extends Pager implements ServletResponseAware {

	/** Generated Id */
	private static final long serialVersionUID = -2599819216740526000L;
	
	/** Search text entered by user */
	private String searchText;
	
	/** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/** Service to search record */
	private BrowseRecordService recordSearch = new DefaultBrowseRecordService();
	
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
	
	/** Search query */
	private String query = "";
	
	private int recordId;
	
	private String recordXML;
	
	/** Response */
	private HttpServletResponse servletResponse;
	
	private int rowEnd;
	

	
	/**
	 * Search for records
	 */
	public String browse() {
		log.debug("searchText::"+searchText);
		log.debug("query::"+query);
		
		// Copy search text to query
		if (searchText != null && searchText.length() > 0) {
			query = searchText;
		}
	
		// Add selected facet to query
	    if (addFacetName != null && addFacetName.length() > 0) {
	    	query = query.concat(" AND " + addFacetName + ":" + addFacetValue);
	    	
	    	// Add facet names and values to | separated list
	    	selectedFacetNames = selectedFacetNames + "|" + addFacetName;
	    	selectedFacetValues = selectedFacetValues + "|" + addFacetValue;
	    }

	    // Remove selected facet to query
	    if (removeFacetName != null && removeFacetName.length() > 0) {
	    	query = query.replaceAll(" AND " + removeFacetName + ":" + removeFacetValue, "");
	    }

	    log.debug("query after adding/removing facet ::"+query);
	    log.debug("selectedFacetNames after Add ::"+selectedFacetNames);
	    log.debug("selectedFacetValues after Add ::"+selectedFacetValues);
	
	    // Create facet names List
	    StringTokenizer tokenizer = new StringTokenizer(selectedFacetNames, "|");
	    List<String> facetNamesList = new ArrayList<String>();
	    
	    StringBuffer newSelectedFacetNames = new StringBuffer();
	    StringBuffer newSelectedFacetValues = new StringBuffer();
	    String myToken = "";
	    while (tokenizer.hasMoreTokens()) {
	    	myToken = tokenizer.nextToken();
	    	if (removeFacetName != null && removeFacetName.length() > 0) {
	    		// Create facet names String separated by | 
	    		if (!removeFacetName.equalsIgnoreCase(myToken)) {
		    		newSelectedFacetNames.append("|");
		    		newSelectedFacetNames.append(myToken);
		    		facetNamesList.add(myToken);
		    	}
	    	} else {
	    		facetNamesList.add(myToken);
	    	}
	    }
	    
	    log.debug("Final facetNamesList:"+facetNamesList);
	    
	    // Create facet values List
	    tokenizer = new StringTokenizer(selectedFacetValues, "|");
	    List<String> facetValuesList = new ArrayList<String>();
	    while (tokenizer.hasMoreTokens()) {
	    	myToken = tokenizer.nextToken();
	    	if (removeFacetValue != null && removeFacetValue.length() > 0) {
	    		if (!removeFacetValue.equalsIgnoreCase(myToken)) {
	    	
	    			newSelectedFacetValues.append("|");
	    			newSelectedFacetValues.append(myToken);
	    			facetValuesList.add(myToken);
		    	}
	    	} else {		    	
	    		facetValuesList.add(myToken);
	    	}
	    }
	    
	    log.debug("Final facetValuesList:"+facetValuesList);
	    
	    if (removeFacetValue != null && removeFacetValue.length() > 0) {
	    	selectedFacetNames = newSelectedFacetNames.toString();
	    	selectedFacetValues = newSelectedFacetValues.toString();
	    }

	    log.debug("Final after remove selectedFacetNames:"+selectedFacetNames);
	    log.debug("Final after remove selectedFacetValues:"+selectedFacetValues);
	    
	    // Query formation
		SolrQuery solarQuery = new SolrQuery();
		solarQuery.setQuery( query )
	    	.setFacet(true)
	    	.setFacetMinCount(1)
	    	.setRows(25)
	    	.setStart(0);
		
		if (!facetNamesList.contains("format_name")) {
			solarQuery.addFacetField("format_name");
		}
		
		if (!facetNamesList.contains("provider_name")) {
			solarQuery.addFacetField("provider_name");
		}
		
		if (!facetNamesList.contains("set_name")) {
			solarQuery.addFacetField("set_name");
		}
		
		if (!facetNamesList.contains("harvest_schedule_name")) {
			solarQuery.addFacetField("harvest_schedule_name");
		}
	    
		rowEnd = rowStart + numberOfResultsToShow;
		
		solarQuery.setStart(rowStart);
		solarQuery.setRows(numberOfResultsToShow);
	    result = recordSearch.search(solarQuery);   
	    
	    log.debug("result::"+result.toString());
	    
	    if(rowEnd > result.getTotalNumberOfResults())
		{
			rowEnd = result.getTotalNumberOfResults();
		}
		
	    // Add facet name and value list to SolrBrowseResult(result) object for display in UI
	    if (result != null) {
		    for(int i = 0; i < facetNamesList.size(); i++) {
		    	result.addFacetFilter(new FacetFilter(facetNamesList.get(i), facetValuesList.get(i)));
		    }
	    }
		
		return SUCCESS;
	}
	
	public String viewRecord() throws IOException {
		
		log.debug("records XML:"+recordXML);
		servletResponse.getWriter().write(recordXML);
		
		return SUCCESS;
	}


	public String getSearchText() {
		return searchText;
	}


	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public BrowseRecordService getRecordSearch() {
		return recordSearch;
	}


	public void setRecordSearch(BrowseRecordService recordSearch) {
		this.recordSearch = recordSearch;
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

	public void setRecordXML(String recordXML) {
		this.recordXML = recordXML;
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
}
