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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xc.mst.dao.BaseDAO;
import xc.mst.manager.BaseManager;
import xc.mst.manager.BaseService;
import xc.mst.utils.Util;

public class MSTBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
	
	protected ApplicationContext applicationContext = null;
	protected Map<String, Method> serviceSetters = null;
	protected Map<String, Method> managerSetters = null;
	
	public MSTBeanPostProcessor() {
		serviceSetters = new HashMap<String, Method>();
		for (Method m : BaseService.class.getMethods()) {
			String mname = m.getName();
			if (mname.startsWith("set") && mname.endsWith("DAO")) {
				String beanName = mname.substring("set".length());
				serviceSetters.put(beanName, m);
			}
		}
		managerSetters = new HashMap<String, Method>();
		for (Method m : BaseManager.class.getMethods()) {
			String mname = m.getName();
			if (mname.startsWith("set") && mname.endsWith("Service")) {
				String beanName = mname.substring("set".length());
				managerSetters.put(beanName, m);
			}
		}
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof BaseDAO) {
			((BaseDAO)bean).setDataSource((DataSource)this.applicationContext.getBean("DataSource"));
			((BaseDAO)bean).setUtil((Util)this.applicationContext.getBean("Util"));
		} else if (bean instanceof BaseService) {
			((BaseService)bean).setUtil((Util)this.applicationContext.getBean("Util"));
			for (String s : serviceSetters.keySet()) {
				Object o = this.applicationContext.getBean(s);
				Method m = this.serviceSetters.get(s);
				try {
					m.invoke(bean, o);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}
		}
		if (bean instanceof BaseManager) {
			for (String s : managerSetters.keySet()) {
				Object o = this.applicationContext.getBean(s);
				Method m = this.managerSetters.get(s);
				try {
					m.invoke(bean, o);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}	
		}
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
