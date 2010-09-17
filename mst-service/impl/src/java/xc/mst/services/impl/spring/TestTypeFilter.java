/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.impl.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import xc.mst.service.impl.test.BaseInternalTest;
import xc.mst.spring.MSTAutoBeanHelper;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class TestTypeFilter extends MSTAutoBeanHelper implements TypeFilter {
	
	private static final Logger LOG = Logger.getLogger(TestTypeFilter.class);
	
	@SuppressWarnings("unchecked")
	private static List<Class> testClasses = new ArrayList<Class>(); 

	@SuppressWarnings("unchecked")
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		try {
			ClassMetadata classMetadata = metadataReader.getClassMetadata();
			String className = classMetadata.getClassName();
			Class c = getClassLoader().loadClass(className);
			if (BaseInternalTest.class.isAssignableFrom(c) && 
					!BaseInternalTest.class.equals(c)) {
				testClasses.add(c);
			}
		} catch (Throwable t) {
			LOG.debug("", t);
		}
		return false;
	}
	
	public static void runTests() {
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
