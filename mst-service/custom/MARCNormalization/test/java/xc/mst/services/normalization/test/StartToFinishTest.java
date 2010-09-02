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

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.provider.Format;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	protected String getServiceName() {
		return "MARCNormalization";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "http://128.151.244.132:8080/OAIToolkit_testDataset_size10/oai-request.do";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getMarcXmlFormat();
	}

	protected Format getHarvestOutFormat() throws Exception {
		return getMarcXmlFormat();
	}
	
	protected void finalTest() {
		repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
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

	/**
	 * To test harvest out functionality
	 */
	protected void testHarvestOut() {
		try {
			int numberOfRecords = 0;
	
			org.jdom.Document doc = new XmlHelper().getJDomDocument(getHarvestOutResponse());
	

			Namespace ns = doc.getRootElement().getNamespace();
			List<Element> records = doc.getRootElement().getChild("ListRecords", ns).getChildren("record", ns);
			if (records != null) {
				numberOfRecords = records.size();
			}
			
			LOG.debug("Number of records harvested out : " + numberOfRecords);
			
			assert numberOfRecords == 28 : " Number of harvested records should be 175 but instead it is " + numberOfRecords;
			LOG.debug("Number of records harvested out : " + numberOfRecords);
		} catch (Throwable t) {
			util.throwIt(t);
		}

	}
}