/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.manager.BaseService;

public class RepositoryService extends BaseService {
	
	private final static Logger LOG = Logger.getLogger(RepositoryService.class);
	
	public List<Repository> getAll() {
		try {
			List<Repository> repos = getRepositoryDAO().getAll();
			List<Repository> repos4real = new ArrayList<Repository>();
			for (int i=0; i<repos.size(); i++) {
				Repository r = repos.get(i);
				if (r.getService() != null) {
					r = getServicesService().getServiceById(r.getService().getId()).getMetadataService().getRepository();
					if (r != null)
						repos4real.add(r);
				} else if (r.getProvider() != null) {
					r = getRepository(r.getProvider());
					if (r != null)
						repos4real.add(r);
				}
			}
			LOG.debug("repos4real: "+repos4real);
			return repos4real;
		} catch (Throwable t) {
			util.throwIt(t);
			return null;
		}
	}
	
	public Date getLastModified() {
		Date d = null;
		for (Repository r : getAll()) {
			Date d2 = getRepositoryDAO().getLastModified(r.getName());
			if (d2 != null && (d == null || d2.after(d))) {
				d = d2;
			}
		}
		return d;
	}
	
	public Repository getRepository(Provider p) {
		Repository repo = (Repository)config.getBean("Repository");
		repo.setProvider(p);
		repo.setName(p.getName());
		return repo;
	}
	
	public Record getRecord(long id) {
		try {
			for (Repository r : getAll()) {
				Record rec = r.getRecord(id);
				
				if (rec != null) {
					injectSuccessors(rec);
					injectPredecessors(rec);
					getMessageService().injectMessages(rec);
					if (r.getService() != null) {
						rec.setService(r.getService());
					} else if (r.getProvider() != null) {
						rec.setProvider(r.getProvider());
					} else {
						LOG.error("neither service or provider set on r.getName(): "+r.getName());
					}
					return rec;
				}
			}
		} catch (Throwable t) {
			util.throwIt(t);
		}
		return null;
	}
	
	public void injectSuccessors(Record rec) {
		for (Repository r : getAll()) {
			r.injectSuccessors(rec);
		}
	}
	
	public void injectPredecessors(Record rec) {
		List<Long> preds = new ArrayList<Long>();
		for (Repository r : getAll()) {
			LOG.debug("checking "+r.getName()+ " for preds of "+rec.getId());
			List<Long> tempPreds = r.getPredecessorIds(rec);
			if (tempPreds != null) {
				preds.addAll(tempPreds);
			}
		}
		LOG.debug("preds: "+preds);
		for (Repository r : getAll()) {
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
