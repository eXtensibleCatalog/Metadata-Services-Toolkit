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

import org.apache.log4j.Logger;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import xc.mst.dao.BaseDAO;
import xc.mst.utils.MSTConfiguration;

public class DAOTypeFilter extends MSTAutoBeanHelper implements TypeFilter {

    private static final Logger LOG = Logger.getLogger(DAOTypeFilter.class);

    @SuppressWarnings("unchecked")
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException{
        try {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String className = classMetadata.getClassName();
            try {
                if (MSTConfiguration.getInstance().getBean(getBeanName(className)) != null) {
                    return false;
                }
            } catch (Throwable t) {
                //do nothing
            }
            if (blackListed(className)) {
                return false;
            }
            Class c = getClassLoader().loadClass(className);
            if (BaseDAO.class.isAssignableFrom(c) && !BaseDAO.class.equals(c)) {
                LOG.debug("c: "+c.getName());
                return true;
            } else {
                return false;
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
