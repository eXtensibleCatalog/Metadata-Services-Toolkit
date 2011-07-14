/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
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
