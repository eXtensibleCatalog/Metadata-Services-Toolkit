/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

public class MSTBeanNameGenerator extends MSTAutoBeanHelper implements BeanNameGenerator {
	
	private static final Logger LOG = Logger.getLogger(MSTBeanNameGenerator.class);

	//@SuppressWarnings("unchecked")
	public String generateBeanName(BeanDefinition definition,
            BeanDefinitionRegistry registry) {
		String beanName = getBeanName(definition.getBeanClassName());
		LOG.debug("beanName: "+beanName);
		return beanName;
	}
}
