package xc.mst.services.dc2xc.service;

import xc.mst.services.dc2xc.dao.FooDAO;
import xc.mst.services.impl.service.GenericMetadataServiceService;

public class FooService extends GenericMetadataServiceService {

	protected FooDAO fooDAO = null;

	public FooDAO getFooDAO() {
		return fooDAO;
	}

	public void setFooDAO(FooDAO fooDAO) {
		this.fooDAO = fooDAO;
	}
	
	public void fooFound(String name) {
		getFooDAO().insertFoo(name);
	}
}
