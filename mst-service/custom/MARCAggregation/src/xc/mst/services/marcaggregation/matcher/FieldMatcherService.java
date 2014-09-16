/**
  * Copyright (c) 2011 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.matcher;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.manager.BaseService;
import xc.mst.services.marcaggregation.MASSolrIndexManager;
import xc.mst.services.marcaggregation.MASSolrService;
import xc.mst.services.marcaggregation.MarcAggregationService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

/**
 * This abstract class ties the interface into the MST by making use of the platform
 * infrastucture.  (by subclassing BaseService)
 *
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public abstract class FieldMatcherService extends BaseService implements FieldMatcher {

    private static final Logger LOG = Logger.getLogger(FieldMatcherService.class);

    private MarcAggregationService mas;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MarcAggregationService getMarcAggregationService() {
        return mas;
    }
    public void setMarcAggregationService(MarcAggregationService mas) {
        this.mas = mas;
    }


    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir, List<Long> filterBy) {
        return null;
    }

    protected SolrServer getMASSolrServer() {
        MASSolrService mss = (MASSolrService)config.getBean("MASSolrService");
        return mss.getServer();
    }

    protected MASSolrIndexManager getMASSolrIndexManager() {
        MASSolrIndexManager mss = (MASSolrIndexManager)config.getBean("MASSolrIndexManager");
        return mss;
    }

    protected List<String> getConfigFileValues(String name) {
        try {
            // there is probably a more righteous way to grab the service name.
            final PropertiesConfiguration props = new PropertiesConfiguration(MSTConfiguration.getUrlPath() + "/services/" + getUtil().normalizeName("MARCAggregation") +
                    "/META-INF/classes/xc/mst/services/custom.properties");

            final List<String> values = Util.castList(String.class, props.getList(name));
            return values;
        } catch (Exception e) {
            LOG.error("Error loading custom.properties for MAS service.", e);
            return null;
        }
    }
    
}
