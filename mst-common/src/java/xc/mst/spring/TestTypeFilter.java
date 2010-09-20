/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import xc.mst.services.MetadataService;
import xc.mst.test.BaseMetadataServiceTest;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class TestTypeFilter extends MSTAutoBeanHelper implements TypeFilter {
	
	private static final Logger LOG = Logger.getLogger(TestTypeFilter.class);
	
	@SuppressWarnings("unchecked")
	private static List<Class> testClasses = new ArrayList<Class>();
	public static MetadataService metadataService = null;

	@SuppressWarnings("unchecked")
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		try {
			ClassMetadata classMetadata = metadataReader.getClassMetadata();
			String className = classMetadata.getClassName();
			Class c = getClassLoader().loadClass(className);
			LOG.debug("checking class: "+c);
			if (BaseMetadataServiceTest.class.isAssignableFrom(c) && 
					!BaseMetadataServiceTest.class.equals(c)) {
				String filter = System.getenv("service.test");
				LOG.debug("filter: "+filter);
				LOG.debug("c.getName(): "+c.getName());
				if (StringUtils.isEmpty(filter) || c.getName().contains(filter)) {
					testClasses.add(c);
					LOG.debug("adding class to test suite: "+c);
				}
			}
		} catch (Throwable t) {
			LOG.debug("", t);
		}
		return false;
	}
	
	public static void runTests(MetadataService metadataService) {
		TestTypeFilter.metadataService = metadataService;
		try {
			TestListenerAdapter tla = new TestListenerAdapter();
			TestNG testng = new TestNG();
			LOG.debug("testClasses: "+testClasses);
			testng.setTestClasses(testClasses.toArray(new Class[]{}));
			testng.addListener(tla);
			testng.run();
			for (Class c : testClasses) {
				/*
				for (Method m : c.getMethods()) {
					for (Annotation a : m.getAnnotations()) {
						if (Test.class.isAssignableFrom(a.annotationType().getClass())) {
							Object o = c.newInstance();
							m.invoke(o);
						}
					}
				}
				*/
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
