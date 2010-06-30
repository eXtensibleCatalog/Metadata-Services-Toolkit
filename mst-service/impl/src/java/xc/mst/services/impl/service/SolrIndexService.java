package xc.mst.services.impl.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.RecordService;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.services.impl.GenericMetadataService;

public class SolrIndexService extends GenericMetadataService  {
	
	private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
	protected Provider provider2index = null;
	protected Service service2index = null;
	
	public void setProvider2index(Provider provider) {
		this.provider2index = provider;
	}
	
	public void setService2index(Service service) {
		this.service2index = service;
	}
	
	public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {
		running.lock();
		//TODO - check and store tables of when the last "harvest" was
		Date from = new Date(System.currentTimeMillis()-(1000*60*60*24*500));
		Date until = new Date();
		
		List<Record> records = 
			((DefaultRepository)repo).getRecordsWSets(from, until, null, inputFormat, inputSet);
		
		Long highestId = null;
		boolean previouslyPaused = false;
		while (records != null && records.size() > 0 && !stopped) {
			if (paused) {
				previouslyPaused = true;
				running.unlock();
				try {
					Thread.sleep(1000);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				continue;
			}
			if (previouslyPaused) {
				running.lock();
			}
			for (Record in : records) {
				if (getRepository() != null) {
					// TODO:  This is sort of different than the current implementation
					//        Although, it does about the same thing.  Perhaps it'll 
					//        work as long as you configure the cache correctly
					// NOTE: successor records need to have predecessors injected into them
					//getRepository().injectSuccessors(in);
				}
				List<Record> out = process(in);
				highestId = in.getId();
			}
			records = 
				((DefaultRepository)repo).getRecordsWSets(from, until, highestId, inputFormat, inputSet);
		}
		try {
			getSolrIndexManager().commitIndex();
		} catch (IndexException ie) {
			throw new RuntimeException(ie);
		}
		if (!previouslyPaused) {
			running.unlock();
		}
	}
	
	public List<Record> process(Record r) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(RecordService.FIELD_RECORD_ID, r.getId());
		r.setMode(Record.STRING_MODE);
		doc.addField(RecordService.FIELD_OAI_XML, r.getOaiXml());
		doc.addField(RecordService.FIELD_ALL, r.getOaiXml());
		if (r.getFormat() != null) {
			doc.addField(RecordService.FIELD_FORMAT_ID, r.getFormat().getId());
			doc.addField("format_name", r.getFormat().getName());
		} else {
			doc.addField(RecordService.FIELD_FORMAT_ID, 0);
		}
		
		if (r.getSets() != null) {
			for (Set s : r.getSets()) {
				doc.addField(RecordService.FIELD_SET_SPEC, s.getSetSpec());
				doc.addField(RecordService.FIELD_SET_NAME, s.getDisplayName());
			}
		}
		
		if (this.provider2index != null) {
			doc.addField(RecordService.FIELD_PROVIDER_ID, this.provider2index.getId());
			doc.addField("provider_name", this.provider2index.getName());
		} else {
			doc.addField(RecordService.FIELD_PROVIDER_ID, 0);
		}
		if (this.service2index != null) {
			doc.addField(RecordService.FIELD_SERVICE_ID, this.service2index.getId());
			doc.addField("service_name", this.service2index.getName());
		} else {
			doc.addField(RecordService.FIELD_SERVICE_ID, 0);
		}
		
		try {
			getSolrIndexManager().addDoc(doc);
		} catch (IndexException ie) {
			throw new RuntimeException(ie);
		}
		return null;
	}

}
