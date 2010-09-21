/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.impl.service;

import xc.mst.dao.MetadataServiceDAO;
import xc.mst.manager.BaseService;
import xc.mst.services.MetadataService;

public class GenericMetadataServiceService extends BaseService {
	
	protected MetadataServiceDAO genericMetadataDAO = null;

	public MetadataServiceDAO getGenericMetadataDAO() {
		return genericMetadataDAO;
	}

	public void setGenericMetadataDAO(MetadataServiceDAO genericMetadataDAO) {
		this.genericMetadataDAO = genericMetadataDAO;
	}

	public MetadataService getMetadataService() {
		return (MetadataService)config.getBean("MetadataService");
	}

}
