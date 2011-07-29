/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

import xc.mst.dao.BaseDAO;
import xc.mst.manager.BaseService;
import xc.mst.oai.Facade;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;

public class MSTScopeMetadataResolver extends MSTAutoBeanHelper implements ScopeMetadataResolver {

	private static final Logger LOG = Logger.getLogger(MSTScopeMetadataResolver.class);

	@SuppressWarnings("unchecked")
	protected List<Class> prototypeScopes = null;

	@SuppressWarnings("unchecked")
	public MSTScopeMetadataResolver() {
		prototypeScopes = new ArrayList<Class>();
		prototypeScopes.add(Repository.class);
		prototypeScopes.add(Facade.class);
		prototypeScopes.add(WorkerThread.class);
	}

	@SuppressWarnings("unchecked")
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		try {
			ScopeMetadata scopeMetadata = new ScopeMetadata();
			String className = definition.getBeanClassName();
			Class c = getClassLoader().loadClass(className);
			boolean prototype = false;
			for (Class pc : prototypeScopes) {
				if (pc.isAssignableFrom(c)) {
					prototype = true;
					break;
				}
			}
			LOG.debug("checking scope for c:"+c);
			if (prototype) {
				LOG.debug("prototype for "+c);
				scopeMetadata.setScopeName("prototype");
			} else if (BaseDAO.class.isAssignableFrom(c)) {
				scopeMetadata.setScopeName("singleton");
			} else if (BaseService.class.isAssignableFrom(c)) {
				scopeMetadata.setScopeName("singleton");
			}
			return scopeMetadata;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	 }
}
