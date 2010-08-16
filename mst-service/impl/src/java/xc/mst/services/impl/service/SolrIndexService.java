package xc.mst.services.impl.service;

import java.util.Date;
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
import xc.mst.services.impl.GenericMetadataService;

public class SolrIndexService extends GenericMetadataService  {
	
	private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
	
	public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {
		LOG.debug("process "+this);
		this.repository = repo;
		running.acquireUninterruptibly();
		//TODO - check and store tables of when the last "harvest" was
		Date from = new Date(System.currentTimeMillis()-(1000*60*60*24*500));
		Date until = new Date();
		
		ServiceHarvest sh = getServiceHarvest(
				inputFormat, inputSet, repo.getName(), getService());
		LOG.debug("sh.getFrom(): "+sh.getFrom()+" sh.getUntil(): "+sh.getUntil()+" sh.getHighestId(): "+sh.getHighestId()+" inputFormat: "+inputFormat+" inputSet:"+inputSet);
		List<Record> records = 
			((DefaultRepository)repo).getRecordsWSets(sh.getFrom(), sh.getUntil(), sh.getHighestId());
		
		Long highestId = null;
		boolean previouslyPaused = false;
		LOG.debug("stopped: "+stopped);
		while (records != null && records.size() > 0 && !stopped) {
			if (paused) {
				previouslyPaused = true;
				LOG.debug("paused");
				running.release();
				try {
					Thread.sleep(1000);
				} catch (Throwable t) {
					LOG.error("", t);
					throw new RuntimeException(t);
				}
				continue;
			}
			if (previouslyPaused) {
				previouslyPaused = false;
				LOG.debug("acquireUninterruptibly 1");
				running.acquireUninterruptibly();
				LOG.debug("acquireUninterruptibly 2");
			}
			for (Record in : records) {
				process(in);
				highestId = in.getId();
				sh.setHighestId(in.getId());
			}
			LOG.debug("sh.getFrom(): "+sh.getFrom()+" sh.getUntil(): "+sh.getUntil()+" sh.getHighestId(): "+sh.getHighestId()+" inputFormat: "+inputFormat+" inputSet:"+inputSet);
			records = 
				((DefaultRepository)repo).getRecordsWSets(from, until, highestId);
			LOG.debug("sh.getId(): "+sh.getId());
			getServiceDAO().persist(sh);
			try {
				getSolrIndexManager().commitIndex();
			} catch (IndexException ie) {
				throw new RuntimeException(ie);
			}
		}
		LOG.debug("stopped: "+stopped);
		if (!stopped) {
			sh.setHighestId(null);
			getServiceDAO().persist(sh);
		}
		try {
			getSolrIndexManager().commitIndex();
		} catch (IndexException ie) {
			throw new RuntimeException(ie);
		}
		if (!previouslyPaused) {
			running.release();
		}
	}
	
	public List<OutputRecord> process(InputRecord ri) {
		Record r = (Record)ri;
		if (r.getId() % 10 == 0) {
			LOG.debug("indexing record.getId(): "+r.getId());
		}
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
			boolean done = false;
			for (Set s : r.getSets()) {
				if (!done) {
					LOG.debug("r.getId(): "+r.getId()+" index set: "+s.getSetSpec());
					done = true;
				}
				doc.addField(RecordService.FIELD_SET_SPEC, s.getSetSpec());
				doc.addField(RecordService.FIELD_SET_NAME, s.getDisplayName());
			}
		}
		
		if (this.repository.getProvider() != null) {
			doc.addField(RecordService.FIELD_PROVIDER_ID, this.repository.getProvider().getId());
			doc.addField("provider_name", this.repository.getProvider().getName());
		} else {
			doc.addField(RecordService.FIELD_PROVIDER_ID, 0);
		}
		if (this.repository.getService() != null) {
			doc.addField(RecordService.FIELD_SERVICE_ID, this.repository.getService().getId());
			doc.addField("service_name", this.repository.getService().getName());
		} else {
			doc.addField(RecordService.FIELD_SERVICE_ID, 0);
		}
		if (r.getMessages() != null) {
			for (RecordMessage m : r.getMessages()) {
				doc.addField(RecordService.FIELD_ERROR,
						m.getServiceId() + "-" + m.getMessageCode() + ":" + getServicesService().getError(m.getServiceId(), m.getMessageCode()));
			}
		}
		
		try {
			getSolrIndexManager().addDoc(doc);
		} catch (IndexException ie) {
			throw new RuntimeException(ie);
		}
		return null;
	}

}
