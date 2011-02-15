/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.RecordService;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.utils.TimingLogger;

public class SolrIndexService extends GenericMetadataService  {
	
	private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
	
	Repository incomingRepository = null;
	
	@Override
	protected List<Record> getRecords(Repository repo, ServiceHarvest sh, Format inputFormat, Set inputSet) {
		TimingLogger.start("getRecordsWSets");
		List<Record> rs =  
			((DefaultRepository)repo).getRecordsWSets(sh.getFrom(), sh.getUntil(), sh.getHighestId());
		TimingLogger.stop("getRecordsWSets");
		return rs;
	}
	
	@Override
	protected boolean commitIfNecessary(boolean force) {
		if (super.commitIfNecessary(force)) {
			try {
				TimingLogger.start("commitIndex");
				getSolrIndexManager().commitIndex();
				TimingLogger.stop("commitIndex");
				return true;
			} catch (Throwable t) {
				getUtil().throwIt(t);
			}	
		}
		return false;
	}
	
	public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {
		this.incomingRepository = repo;
		super.process(repo, inputFormat, inputSet, outputSet);
	}
	
	public List<OutputRecord> process(InputRecord ri) {
		if (ri.getStatus() == Record.DELETED) {
			TimingLogger.start("deleteByQuery");
			getSolrIndexManager().deleteByQuery("record_id:"+ri.getId());
			TimingLogger.stop("deleteByQuery");
		} else {
			TimingLogger.add(incomingRepository.getName(), 0);
			Record r = (Record)ri;
			LOG.debug("indexing record.getId(): "+r.getId());
			if (r.getId() % 1000 == 0) {
				LOG.debug("indexing record.getId(): "+r.getId());
			}
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(RecordService.FIELD_RECORD_ID, r.getId());
			r.setMode(Record.STRING_MODE);
			//doc.addField(RecordService.FIELD_OAI_XML, r.getOaiXml());
			doc.addField(RecordService.FIELD_ALL, r.getId()+" "+r.getOaiXml());
			if (r.getFormat() != null) {
				doc.addField(RecordService.FIELD_FORMAT_ID, r.getFormat().getId());
				doc.addField("format_name", r.getFormat().getName());
			} else {
				doc.addField(RecordService.FIELD_FORMAT_ID, 0);
			}
			
			if (r.getSets() != null) {
				boolean done = false;
				for (Set s : r.getSets()) {
					if (!done) {
						//LOG.debug("r.getId(): "+r.getId()+" index set: "+s.getSetSpec());
						done = true;
					}
					doc.addField(RecordService.FIELD_SET_SPEC, s.getSetSpec());
					doc.addField(RecordService.FIELD_SET_NAME, s.getDisplayName());
				}
			}
			
			if (this.incomingRepository.getProvider() != null) {
				doc.addField(RecordService.FIELD_PROVIDER_ID, this.incomingRepository.getProvider().getId());
				doc.addField("provider_name", this.incomingRepository.getProvider().getName());
			} else {
				doc.addField(RecordService.FIELD_PROVIDER_ID, 0);
			}
			if (this.incomingRepository.getService() != null) {
				doc.addField(RecordService.FIELD_SERVICE_ID, this.incomingRepository.getService().getId());
				doc.addField("service_name", this.incomingRepository.getService().getName());
			} else {
				doc.addField(RecordService.FIELD_SERVICE_ID, 0);
			}
			if (r.getMessages() != null) {
				for (RecordMessage m : r.getMessages()) {
					try {
						LOG.debug("m: "+m);
						doc.addField(RecordService.FIELD_ERROR,
								m.getServiceId() + "-" + m.getCode() + ":" + 
								m.getMessage());
					} catch (Throwable t) {
						LOG.error("continuing, but logging", t);
					}
				}
			}
			
			try {
				getSolrIndexManager().addDoc(doc);
			} catch (IndexException ie) {
				throw new RuntimeException(ie);
			}
		}
		return null;
	}

	public void runTests() {
		//do nothing
	}

}
