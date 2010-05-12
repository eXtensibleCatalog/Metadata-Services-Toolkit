/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.spring;

import java.io.IOException;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import xc.mst.dao.BaseDAO;

public class DAOTypeFilter implements TypeFilter {

	@SuppressWarnings("unchecked")
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) 
			throws IOException{
		try {
			ClassMetadata classMetadata = metadataReader.getClassMetadata();
			String className = classMetadata.getClassName();
			Class c = getClass().getClassLoader().loadClass(className);
			//Class c = classMetadata.getClass();
			if (BaseDAO.class.isAssignableFrom(c) && !BaseDAO.class.equals(c)) {
				System.out.println("c: "+c.getName());
				return true;
			} else {
				return false;
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
