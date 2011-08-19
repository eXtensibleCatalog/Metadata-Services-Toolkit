/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.record;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;

/**
 * Represents the browse result
 *
 * @author Sharmila Ranganathan
 *
 */
public class SolrBrowseResult {

    /** Total number of results */
    private long totalNumberOfResults;

    /** Query */
    private String query;

    /** List of resords retrieved */
    private List<Record> records;

    /** List of facets created */
    private List<FacetField> facets;

    /** List of selected facets */
    private List<FacetFilter> facetFilters = new ArrayList<FacetFilter>();

    public SolrBrowseResult(List<Record> records, List<FacetField> facets) {
        this.records = records;
        this.facets = facets;
    }

    public SolrBrowseResult(List<Record> records) {
        this.records = records;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public List<FacetField> getFacets() {
        return facets;
    }

    public void setFacets(List<FacetField> facets) {
        this.facets = facets;
    }

    public List<FacetFilter> getFacetFilters() {
        return facetFilters;
    }

    public void setFacetFilters(List<FacetFilter> facetFilters) {
        this.facetFilters = facetFilters;
    }

    public void addFacetFilter(FacetFilter facetFilter) {
        facetFilters.add(facetFilter);
    }

    public long getTotalNumberOfResults() {
        return totalNumberOfResults;
    }

    public void setTotalNumberOfResults(long totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" query= "+query);
        buffer.append(" totalNumberOfResults= "+totalNumberOfResults);
        //for(Record r : records) {
        //	buffer.append(" Record id ="+r.getId());
        //}
        return buffer.toString();
    }


}
