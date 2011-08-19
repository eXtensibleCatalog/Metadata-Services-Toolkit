/**
 * Copyright (c) 2009 eXtensible Catalog Organization
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
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;

import xc.mst.dao.BaseDAO;
import xc.mst.manager.BaseManager;
import xc.mst.manager.BaseService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class MSTBeanPostProcessor extends MSTAutoBeanHelper implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger LOG = Logger.getLogger(MSTBeanPostProcessor.class);

    protected ApplicationContext applicationContext = null;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BaseDAO) {
            try {
                ((BaseDAO) bean).setDataSource((DataSource) this.applicationContext.getBean("MetadataServiceDataSource"));
            } catch (NoSuchBeanDefinitionException nsbde) {
                ((BaseDAO) bean).setDataSource((DataSource) this.applicationContext.getBean("DataSource"));
            }
            try {
                ((BaseDAO) bean).setConfig((MSTConfiguration) this.applicationContext.getBean("MetadataServiceMSTConfiguration"));
            } catch (NoSuchBeanDefinitionException nsbde) {
                ((BaseDAO) bean).setConfig((MSTConfiguration) this.applicationContext.getBean("MSTConfiguration"));
            }
            ((BaseDAO) bean).setSessionFactory((SessionFactory) this.applicationContext.getBean("SessionFactory"));
            ((BaseDAO) bean).setUtil((Util) this.applicationContext.getBean("Util"));
        } else if (bean instanceof BaseService) {
            ((BaseService) bean).setUtil((Util) this.applicationContext.getBean("Util"));
            try {
                ((BaseService) bean).setTransactionManager((PlatformTransactionManager) this.applicationContext.getBean("MetadataServiceTransactionManager"));
            } catch (NoSuchBeanDefinitionException nsbde) {
                ((BaseService) bean).setTransactionManager((PlatformTransactionManager) this.applicationContext.getBean("TransactionManager"));
            }
            try {
                ((BaseService) bean).setConfig((MSTConfiguration) this.applicationContext.getBean("MetadataServiceMSTConfiguration"));
            } catch (NoSuchBeanDefinitionException nsbde) {
                ((BaseService) bean).setConfig((MSTConfiguration) this.applicationContext.getBean("MSTConfiguration"));
            }

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
                    try {
                        o = this.applicationContext.getBean("Generic" + s);
                        LOG.debug("using Generic" + s + " for " + bean.getClass() + "." + m.getName());
                    } catch (Throwable t) {
                    }
                    m.invoke(bean, o);
                } catch (Throwable t) {
                    LOG.error(bean.getClass() + "." + m.getName());
                    if (o != null) {
                        LOG.error("o: " + o);
                    }
                    LOG.error("", t);
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
                    // I'm filter this one out because it happens everytime the app loads and is
                    // confusing users
                    if (m.getName() != null && !m.getName().equals("setMetadataService")) {
                        LOG.error(t.getMessage() + " error calling " + bean.getClass() + "." + m.getName());
                    }
                }
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
