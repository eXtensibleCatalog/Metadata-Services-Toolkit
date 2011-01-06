/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.normalization.test;

import gnu.trove.TLongHashSet;

import org.testng.annotations.Test;

import xc.mst.repo.Repository;
import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class QuickTest extends BaseMetadataServiceTest {
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	@Test
	public void quickTest() {
		Repository repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(getRepoName());
		TLongHashSet predecessors = new TLongHashSet();
		repo.populatePredecessors(predecessors);
		LOG.debug(getRepoName()+".predecessors: "+predecessors);
		LOG.debug(new Util().getString(predecessors));
		
        repo.setName(getServiceName());
        predecessors.clear();
		repo.populatePredecessors(predecessors);
		LOG.debug(getServiceName()+".predecessors: "+predecessors);
		LOG.debug(new Util().getString(predecessors));
	}

}
