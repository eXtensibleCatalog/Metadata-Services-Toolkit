/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.spring;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xc.mst.dao.BaseDAO;
import xc.mst.manager.BaseManager;
import xc.mst.manager.BaseService;
import xc.mst.utils.Util;

public class MSTBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
	
	private static final Logger LOG = Logger.getLogger(MSTBeanPostProcessor.class);
	
	protected ApplicationContext applicationContext = null;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof BaseDAO) {
			((BaseDAO)bean).setDataSource((DataSource)this.applicationContext.getBean("DataSource"));
			((BaseDAO)bean).setUtil((Util)this.applicationContext.getBean("Util"));
		} else if (bean instanceof BaseService) {
			LOG.info("bean: "+bean+" setUtil: "+this.applicationContext.getBean("Util"));
			((BaseService)bean).setUtil((Util)this.applicationContext.getBean("Util"));
			
			Map<String, Method> serviceSetters = new HashMap<String, Method>();
			for (Method m : bean.getClass().getMethods()) {
				String mname = m.getName();
				if (mname.startsWith("set") && mname.endsWith("DAO")) {
					String bn = mname.substring("set".length());
					serviceSetters.put(bn, m);
				}
			}
			for (String s : serviceSetters.keySet()) {
				Object o = null;
				Method m = null;
				try {
					m = serviceSetters.get(s);
					o = this.applicationContext.getBean(s);
					m.invoke(bean, o);
				} catch (Throwable t) {
					LOG.error(t.getMessage()+"error calling "+bean.getClass()+"."+m.getName());
				}
			}
		}
		
		if (bean instanceof BaseManager) {
			Map<String, Method> managerSetters = new HashMap<String, Method>();
			for (Method m : bean.getClass().getMethods()) {
				String mname = m.getName();
				if (mname.startsWith("set") && mname.endsWith("Service") && !mname.equals("setService")) {
					String bn = mname.substring("set".length());
					managerSetters.put(bn, m);
				}
			}
			for (String s : managerSetters.keySet()) {
				Object o = null;
				Method m = null;
				try {
					m = managerSetters.get(s);
					o = this.applicationContext.getBean(s);
					m.invoke(bean, o);
				} catch (Throwable t) {
					LOG.error(t.getMessage()+" error calling "+bean.getClass()+"."+m.getName());
				}
			}	
		}
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
