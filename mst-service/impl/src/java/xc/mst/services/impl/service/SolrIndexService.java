package xc.mst.services.impl.service;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.record.Record;
import xc.mst.services.impl.GenericMetadataService;

public class SolrIndexService extends GenericMetadataService  {
	
	private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
	
	public List<Record> process(Record r) {
		LOG.debug("r.getId(): "+r.getId());
		return null;
	}

}
