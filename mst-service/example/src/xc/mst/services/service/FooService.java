package xc.mst.services.service;

import xc.mst.services.example.dao.FooDAO;
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
