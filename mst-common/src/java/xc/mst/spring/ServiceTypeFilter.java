/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import xc.mst.manager.BaseManager;
import xc.mst.manager.BaseService;
import xc.mst.services.MetadataService;
import xc.mst.utils.MSTConfiguration;

public class ServiceTypeFilter extends MSTAutoBeanHelper implements TypeFilter {

    private static final Logger LOG = Logger.getLogger(ServiceTypeFilter.class);

    public static Map<String, String> serviceBeans = null;

    static {
        serviceBeans = new HashMap<String, String>();
        serviceBeans.put("xc.mst.harvester.ValidateRepository", "ValidateRepository");
        serviceBeans.put("xc.mst.manager.record.MSTSolrServer", "MSTSolrServer");
        serviceBeans.put("xc.mst.manager.record.DefaultRecordService", "DefaultRecordService");
    }

    @SuppressWarnings("unchecked")
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException {
        try {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String className = classMetadata.getClassName();
            try {
                if (MSTConfiguration.getInstance().getBean(getBeanName(className)) != null) {
                    return false;
                }
            } catch (Throwable t) {
                // do nothing
            }
            if (blackListed(className)) {
                return false;
            }
            Class c = getClassLoader().loadClass(className);
            if (BaseService.class.isAssignableFrom(c) && !BaseManager.class.equals(c)
                    && !BaseService.class.equals(c)) {
                LOG.debug("c: " + c.getName());
                return true;
            } else if (serviceBeans.containsKey(className)) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
