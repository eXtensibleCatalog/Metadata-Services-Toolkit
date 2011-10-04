/**
 * Copyright (c) 2009 eXtensible Catalog Organization
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
import java.util.*;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.struts2.interceptor.ServletResponseAware;

import xc.mst.bo.record.FacetFilter;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SolrBrowseResult;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.BrowseRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.repo.RepositoryService;
import xc.mst.services.SolrIndexService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.XmlHelper;

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
    static Logger LOG = Logger.getLogger(BrowseRecords.class);

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
    private String selectedFacetValues = "";

    /** Search text entered by user */
    private String query = "";

    /** Id of record */
    private int recordId;

    /** Record XML */
    private String recordXML;

    /** Response */
    private HttpServletResponse servletResponse;

    /** The end row number to retrieve */
    private long rowEnd;

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

    /** Error type */
    private String errorType;

    private LinkedHashMap<String,String> identifiers;
    private ArrayList<String> idKeys;

    /** for individual query can pick one from identifiers list **/
    private String identifier;

    /**
     * Execute method to load initial screen with just the facets
     */
    public String execute() {
        searchXML = true;
        isInitialLoad = true;
        try {
            initializeIdentifiers();
        } catch (DatabaseConfigException e) {
            log.error("Search failed. Problem with connecting to database using the parameters in configuration file.", e);
            errorType = "error";
            addFieldError("dbError", "Search failed.Problem with connecting to database using the parameters in configuration file.");
            return INPUT;
        }
        browse();
        return SUCCESS;
    }

    protected void addFilterQuery(SolrQuery solrQuery, String name, String value) {
        RepositoryService repositoryService = (RepositoryService) MSTConfiguration.getInstance().getBean("RepositoryService");
        if ("successor".equals(name) || "processed_from".equals(name)) {
            Record r = repositoryService.getRecord(Long.parseLong(value));
            List<Record> records = new ArrayList<Record>();
            if ("successor".equals(name)) {
                List<InputRecord> irs = ((Record) r).getPredecessors();
                if (irs != null) {
                    for (InputRecord ir : irs) {
                        records.add((Record) ir);
                    }
                }
            } else {
                List<OutputRecord> ors = ((Record) r).getSuccessors();
                if (ors != null) {
                    for (OutputRecord or : ors) {
                        records.add((Record) or);
                    }
                }
            }
            name = "record_id";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < records.size(); i++) {
                Record r2 = records.get(i);
                sb.append("record_id:");
                sb.append(r2.getId() + "");
                if (i < records.size() - 1) {
                    sb.append(" OR ");
                }
            }
            solrQuery.addFilterQuery(sb.toString());
        } else {
            solrQuery.addFilterQuery(name + ":\"" + value.replaceAll(":", "\\\\:") + "\"");
        }
    }

    /**
     * Search for records
     */
    public String browse() {
        RecordService recordService = (RecordService) MSTConfiguration.getInstance().getBean("RecordService");
        if (log.isDebugEnabled()) {
            log.debug("User entered query::" + query);
        }
        try {
            SolrQuery solrQuery = new SolrQuery();

            // If Query is empty retrieve all records
            if ((query == null) || (query.equals(""))) {
                solrQuery.setQuery("*:*");
            } else {
                if (!StringUtils.isEmpty(identifier)) {
                    String qFld = getIdentifiers().get(identifier);
                    if (query.indexOf(" ") != -1) {
                        query = "(" + query + ")";
                    }
                    if (qFld != null) {
                        query = query.replaceAll(":", "\\\\:");  // replacing with \\: and solr ends up thinking the token before : is a field
                                                                 //  warning:  replacing the 1st : (the delim between field and text) breaks
                                                                 //             the search, no results returned.
                        String q = new String(qFld+":"+query);
                        solrQuery.setQuery(q);
                        if (log.isDebugEnabled()) {
                            LOG.debug("*** browse() ident="+qFld+" query="+q);
                        }
                    }
                    else {
                        if (log.isDebugEnabled()) {
                            LOG.debug("*** NO field associated with IDENTIFIER! "+" query="+query+" identifier="+identifier);
                        }
                        // solr schema: <copyField source="*_key"  dest="text" />
                        if (query.indexOf(" ") != -1) {
                            query = "(" + query + ")";
                        }
                        // TODO having trouble getting this working, leave as a TODO for now, and drop back to default search on record_id.
                        // (should never get here anyways, an identifier will always be picked.
                        //solrQuery.setQuery("text:"+ query);
                        solrQuery.setQuery(query.replaceAll(":", "\\\\:"));
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        LOG.debug("*** NO IDENTIFIER FOUND! "+" query="+query);
                    }
                    solrQuery.setQuery("*:*");
                }
            }

            StringTokenizer nameTokenizer = new StringTokenizer(selectedFacetNames, "|");
            StringTokenizer valueTokenizer = new StringTokenizer(selectedFacetValues, "|");

            while (nameTokenizer.hasMoreTokens()) {
                String name = nameTokenizer.nextToken();
                String value = valueTokenizer.nextToken();
                addFilterQuery(solrQuery, name, value);
            }

            // Add selected facet to query
            if (addFacetName != null && addFacetName.length() > 0) {
                addFilterQuery(solrQuery, addFacetName, addFacetValue);
                // Add facet names and values to | separated list
                selectedFacetNames = selectedFacetNames + "|" + addFacetName;
                selectedFacetValues = selectedFacetValues + "|" + addFacetValue;
            }

            // Remove selected facet from query
            if (removeFacetName != null && removeFacetName.length() > 0) {
                solrQuery.removeFilterQuery(removeFacetName + ":\"" + removeFacetValue.replaceAll(":", "\\\\:") + "\"");
            }

            if (log.isDebugEnabled()) {
                log.debug("Query after adding/removing facet ::" + query);
                log.debug("After Adding facet names ::" + selectedFacetNames);
                log.debug("After Adding facet values ::" + selectedFacetValues);
            }
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
                    if (!(removeFacetName.equalsIgnoreCase(myNameToken) && removeFacetValue.equalsIgnoreCase(myValueToken))) {
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

            if (removeFacetValue != null && removeFacetValue.length() > 0) {
                selectedFacetNames = newSelectedFacetNames.toString();
                selectedFacetValues = newSelectedFacetValues.toString();
            }

            if (log.isDebugEnabled()) {
                log.debug("After removing facet names(final):" + selectedFacetNames);
                log.debug("After removing facet values(final):" + selectedFacetValues);
            }

            // Query formation
            solrQuery.setFacet(true)
                     .setFacetMinCount(1);
            solrQuery.addFacetField("status");
            solrQuery.addFacetField("provider_name");
            solrQuery.addFacetField("service_name");
            solrQuery.addFacetField("format_name");
            solrQuery.addFacetField("set_name");
            solrQuery.addFacetField("error");

            // Fields to load
            solrQuery.addField(RecordService.FIELD_RECORD_ID);
            solrQuery.addField(RecordService.FIELD_FORMAT_ID);
            solrQuery.addField(RecordService.FIELD_PROVIDER_ID);
            solrQuery.addField(RecordService.FIELD_SERVICE_ID);
            solrQuery.addField(RecordService.FIELD_HARVEST_SCHEDULE_NAME);
            solrQuery.addField(RecordService.FIELD_ERROR);
            solrQuery.addField(RecordService.FIELD_PROCESSED_FROM);
            solrQuery.addField(RecordService.FIELD_SUCCESSOR);
            solrQuery.addField(RecordService.FIELD_OAI_IDENTIFIER);

            getIdentifiers();

            rowEnd = rowStart + numberOfResultsToShow;

            // In initial page load, we are not going to show any records. Only facets will be shown
            if (isInitialLoad) {
                solrQuery.setStart(0);
                solrQuery.setRows(0);
            } else {
                solrQuery.setStart(rowStart);
                solrQuery.setRows(numberOfResultsToShow);
            }
            BrowseRecordService browseRecordService = (BrowseRecordService) MSTConfiguration.getInstance().getBean("BrowseRecordService");
            result = browseRecordService.search(solrQuery);

            if (log.isDebugEnabled()) {
                log.debug("Search result::" + result);
            }

            if ((result != null) && (rowEnd > result.getTotalNumberOfResults())) {
                rowEnd = result.getTotalNumberOfResults();
            }

            // Add facet name and value list to SolrBrowseResult(result) object for display in UI
            if (result != null) {
                for (int i = 0; i < facetNamesList.size(); i++) {
                    // Get successor/predecessor of the record to display its information
                    if (facetNamesList.get(i).equalsIgnoreCase("successor")) {
                        successorRecord = recordService.getById(Long.parseLong(facetValuesList.get(i)));
                    }
                    if (facetNamesList.get(i).equalsIgnoreCase("processed_from")) {

                        predecessorRecord = recordService.getById(Long.parseLong(facetValuesList.get(i)));
                    }

                    result.addFacetFilter(new FacetFilter(facetNamesList.get(i), facetValuesList.get(i)));
                }
            }
        } catch (DatabaseConfigException dce) {
            log.error("Search failed. Problem with connecting to database using the parameters in configuration file.", dce);
            errorType = "error";
            addFieldError("dbError", "Search failed.Problem with connecting to database using the parameters in configuration file.");
            return INPUT;
        } catch (IndexException ie) {
            log.error("Search failed. Problem with connecting to Solr server. Check the path to solr folder.", ie);
            errorType = "error";
            addFieldError("dbError", "Search failed. Problem with connecting to Solr server. Check the path to solr folder.");
            return INPUT;
        }

        return SUCCESS;
    }

    /**
     * View record XML
     */
    public String viewRecord() throws IOException {

        if (log.isDebugEnabled()) {
            log.debug("viewRecord:  view record Id : " + recordId);
        }

        try {
            RepositoryService repositoryService = (RepositoryService) MSTConfiguration.getInstance().getBean("RepositoryService");
            record = repositoryService.getRecord(recordId);
            recordXML = record.getOaiXml();
            if (recordXML != null) {
                XmlHelper xh = new XmlHelper();
                recordXML = StringEscapeUtils.escapeHtml(xh.getStringPretty(xh.getJDomDocument(recordXML).getRootElement()));
            }
        } catch (Throwable t) {
            log.error("", t);
            errorType = "error";
            addFieldError("dbError", "Problem with connecting to database using the parameters in configuration file.");
            return INPUT;
        }

        return SUCCESS;
    }

    /**
     * View error description  (additional help file)
     */
    public String viewErrorDescription() {

        if (log.isDebugEnabled()) {
            log.debug("viewErrorDescription: error to view : " + error);
        }

        try {
            ServicesService servicesService = (ServicesService) MSTConfiguration.getInstance().getBean("ServicesService");
            // at this point we don't have much to go on, as far as I can tell, we only know the message text (error)
            // ,one negative of this new workaround way is if 2 services contain same error text, we'll grab 1st one we find,
            // and its associated help file,
            Service service = null;
            String fileName = null;
            String errorCode = error.substring(0, error.indexOf(":"));
            for (Service s : servicesService.getAllServices()) {
                String codez = s.getMetadataService().getConfig().getProperty("error." + errorCode + ".text");
                log.info("Error= " + error + " errorCode=" + errorCode + " this service's text=" + codez + " tried prop:" + "error." + errorCode + ".text");
                if (codez != null) {
                    if (error.indexOf(codez) > -1) {
                        service = s;
                        fileName = s.getMetadataService().getConfig().getProperty("error." + errorCode + ".descriptionFile");
                        break;
                    }
                }
            }
            if (service != null && fileName != null) {
                // Get error code
                FileInputStream fis = new FileInputStream(
                        MSTConfiguration.getInstance().getServicePath() + service.getName() + "/errors/" + fileName);
                BufferedInputStream bis = new BufferedInputStream(fis);
                DataInputStream dis = new DataInputStream(bis);
                BufferedReader br = new BufferedReader(new InputStreamReader(dis));
                StringBuffer buffer = new StringBuffer();
                String strLine;
                // Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    buffer.append(strLine);
                }
                errorDescription = buffer.toString();
            } else {
                log.debug("No errorDescription found for " + error);
                errorDescription = "No further information is available for this error.";
            }
        } catch (DatabaseConfigException dce) {
            log.error("Problem with getting error information. Exception occured while connecting to database using the parameters in configuration file.", dce);
            errorType = "error";
            addFieldError("dbError", "Problem with getting error information. Exception occured while connecting to database using the parameters in configuration file.");
            return INPUT;
        } catch (Exception e) {
            log.error("Problem with getting error information.", e);
            errorType = "error";
            addFieldError("dbError", "Exception occured while getting error information.");
            return INPUT;
        }
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

    public long getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(long rowEnd) {
        this.rowEnd = rowEnd;
    }

    /**
     * Returns the total number of hits
     *
     * @see xc.mst.action.browse.Pager#getTotalHits()
     */
    public long getTotalHits() {
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

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    public HashMap<String, String> getIdentifiers() {
        if (identifiers == null || identifiers.size() < 1) {
            try {
                initializeIdentifiers();
            } catch (DatabaseConfigException e) {
                log.error("", e);
                errorType = "error";
                addFieldError("dbError", "Problem with connecting to database using the parameters in configuration file.");
            }
        }
        return identifiers;
    }
    public void setIdentifiers(LinkedHashMap<String, String> id) {
        this.identifiers = id;
    }
    public ArrayList<String> getIdKeys() {
        if (idKeys == null || idKeys.size() < 1) {
            try {
                initializeIdentifiers();
            } catch (DatabaseConfigException e) {
                log.error("", e);
                errorType = "error";
                addFieldError("dbError", "Problem with connecting to database using the parameters in configuration file.");
            }
        }
        return idKeys;
    }
    public void setIdKeys(ArrayList<String> key) {
        this.idKeys = key;
    }
    public String getIdentifier() {
        return this.identifier;
    }
    public void setIdentifier(String id) {
        this.identifier = id;
    }

    public void initializeIdentifiers() throws DatabaseConfigException {

        SolrIndexService solrIndexService = getSolrIndexService();
        Map<String,String> ids = solrIndexService.getIdentifiers();
        if (identifiers == null) {
            identifiers = new LinkedHashMap<String, String>();
        }
        if (idKeys == null) {
            idKeys = new ArrayList<String>();
        }
        if (ids != null && ids.size() > 0) {
            identifiers.putAll(ids);
            idKeys.addAll(ids.keySet());
        }
    }
}
