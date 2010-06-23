package xc.mst.services.impl.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.services.impl.GenericMetadataService;

public class SolrIndexService extends GenericMetadataService  {
	
	private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
	
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
		if (!previouslyPaused) {
			running.unlock();
		}
	}
	
	public List<Record> process(Record r) {
		LOG.debug("r.getSets(): "+r.getSets());
		LOG.debug("r.getId(): "+r.getId());
		return null;
	}

}
