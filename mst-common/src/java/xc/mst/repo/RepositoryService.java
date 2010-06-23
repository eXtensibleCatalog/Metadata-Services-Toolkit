package xc.mst.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.Record;
import xc.mst.manager.BaseService;

public class RepositoryService extends BaseService {
	
	private final static Logger LOG = Logger.getLogger(RepositoryService.class);
	
	//TODO if you're caching repos, then you should cache the getall call the repoDAO
	protected Map<String, Repository> repos = new HashMap<String, Repository>();
	
	public Repository getRepository(String name) {
		Repository repo = repos.get(name);
		if (repo == null) {
			repo = (Repository)config.getBean("Repository");
			repo.setName(name);
			repos.put(name, repo);
		}
		return repo;
	}
	
	public Record getRecord(long id) {
		for (Repository r : getRepositoryDAO().getAll()) {
			r = getRepository(r.getName());
			Record rec = r.getRecord(id);
			if (rec != null) {
				injectSuccessors(rec);
				injectPredecessors(rec);
				return rec;
			}
		}
		return null;
	}
	
	public void injectSuccessors(Record rec) {
		for (Repository r : getRepositoryDAO().getAll()) {
			r = getRepository(r.getName());
			LOG.debug("checking "+r.getName()+ " for succs of "+rec.getId());
			List<Record> succs = getRepositoryDAO().getSuccessors(r.getName(), rec.getId());
			LOG.debug("succs: "+succs);
			if (succs != null) {
				rec.getSuccessors().addAll(succs);
			}
		}
	}
	
	public void injectPredecessors(Record rec) {
		List<Long> preds = new ArrayList<Long>();
		for (Repository r : getRepositoryDAO().getAll()) {
			r = getRepository(r.getName());
			LOG.debug("checking "+r.getName()+ " for preds of "+rec.getId());
			List<Long> tempPreds = getRepositoryDAO().getPredecessors(r.getName(), rec.getId());
			if (tempPreds != null) {
				preds.addAll(tempPreds);
			}
		}
		LOG.debug("preds: "+preds);
		for (Repository r : getRepositoryDAO().getAll()) {
			r = getRepository(r.getName());
			for (Long id : preds) {
				Record pred = r.getRecord(id);
				if (pred != null) {
					LOG.debug("pred: "+pred);			
					rec.getPredecessors().add(pred);
				}
			}
		}
	}

}
