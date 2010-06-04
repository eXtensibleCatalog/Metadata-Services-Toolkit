package xc.mst.services.impl.service;

import xc.mst.manager.BaseService;
import xc.mst.services.impl.dao.GenericMetadataDAO;

public class GenericMetadataServiceService extends BaseService {
	
	protected GenericMetadataDAO genericMetadataDAO = null;

	public GenericMetadataDAO getGenericMetadataDAO() {
		return genericMetadataDAO;
	}

	public void setGenericMetadataDAO(GenericMetadataDAO genericMetadataDAO) {
		this.genericMetadataDAO = genericMetadataDAO;
	}

}
