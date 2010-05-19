/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

import xc.mst.dao.BaseDAO;
import xc.mst.manager.BaseService;
import xc.mst.repo.DefaultRepository;

public class MSTScopeMetadataResolver implements ScopeMetadataResolver {
	
	@SuppressWarnings("unchecked")
	protected List<Class> prototypeScopes = null;
	
	@SuppressWarnings("unchecked")
	public MSTScopeMetadataResolver() {
		prototypeScopes = new ArrayList<Class>();
		prototypeScopes.add(DefaultRepository.class);
	}

	@SuppressWarnings("unchecked")
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		 ScopeMetadata scopeMetadata = new ScopeMetadata();
		 Class c = definition.getClass();
		 if (prototypeScopes.contains(c)) {
			 scopeMetadata.setScopeName("prototype");
		 } else if (BaseDAO.class.isAssignableFrom(c)) {
			 scopeMetadata.setScopeName("singleton");
		 } else if (BaseService.class.isAssignableFrom(c)) {
			 scopeMetadata.setScopeName("singleton");
		 }
		 return scopeMetadata;
	 }
}
