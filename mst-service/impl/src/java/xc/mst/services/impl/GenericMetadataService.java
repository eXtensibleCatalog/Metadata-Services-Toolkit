/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.impl;


import gnu.trove.TLongHashSet;
import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.constants.Constants;
import xc.mst.email.Emailer;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataService;
import xc.mst.services.impl.dao.GenericMetadataDAO;

/**
 * A copy of the MST is designed to interface with one or more Metadata Services depending on how it's configured.
 * There are several Metadata Services which may be used, each one of which extends the MetadataService
 * class.  The MetadataService class provides a common interface through which the MST can invoke functionality on
 * a Metadata Service.
 *
 * @author Eric Osisek
 */

public abstract class GenericMetadataService extends SolrMetadataService implements MetadataService, ApplicationContextAware {

	protected static Logger LOG = Logger.getLogger(Constants.LOGGER_PROCESSING);

	protected ApplicationContext applicationContext = null;
	protected GenericMetadataDAO genericMetadataDAO = null;
	protected Service service = null;
	protected List<ProcessingDirective> processingDirectives = null;
	protected int warningCount = 0;
	protected int errorCount = 0;
	protected int errorCountPerCommit = 0;
	protected Emailer mailer = new Emailer();
	
	protected TLongObjectHashMap predecessorKeyedMap = new TLongObjectHashMap();
	protected TLongObjectHashMap successorKeyedMap = new TLongObjectHashMap();

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	protected HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	protected boolean stopped = false;
	protected boolean paused = false;
	protected ReentrantLock running = new ReentrantLock();
	protected int processedRecordCount = 0;
	protected int totalRecordCount = 0;
	protected int inputRecordCount = 0;
	protected Set outputSet;
	protected List<String> unprocessedErrorRecordIdentifiers = new ArrayList<String>();
	
	protected long startTime = new Date().getTime();
	protected long endTime = 0;
	protected long timeDiff = 0;
	
	protected Repository repository = null;
	
	static {
		LOG.debug("GenericMetadataService class loaded!!!");
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
	
	public GenericMetadataDAO getGenericMetadataDAO() {
		return genericMetadataDAO;
	}

	public void setGenericMetadataDAO(GenericMetadataDAO genericMetadataDAO) {
		this.genericMetadataDAO = genericMetadataDAO;
	}

	public Set getOutputSet() {
		return outputSet;
	}

	public void setOutputSet(Set outputSet) {
		this.outputSet = outputSet;
	}
    
	public Repository getRepository() {
		return this.repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setInputRecordCount(int inputRecordCount) {
	}
	
	public void cancel() {stopped = true; running.lock(); running.unlock();}
	public void finish() {running.lock(); running.unlock();}
	public void pause()  {paused = true; running.lock(); running.unlock();}
	public void resume() {paused = false;}
	
	public void install() {
		try {
			getGenericMetadataDAO().executeServiceDBScripts(config.getServicePath()+"/sql/install.sql");
			postInstall();
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
	
	public void postInstall() {
	}

	public void uninstall() {
		try {
			getGenericMetadataDAO().executeServiceDBScripts(config.getServicePath()+"/sql/uninstall.sql");
			postInstall();
		} catch (Throwable t) {
			LOG.error("", t);
		}
		postUninstall();
	}
	
	public void postUninstall() {
	}
	
	public void update(String cvStr) {
		update(cvStr, getService().getVersion());
	}
	
	public void update(String pvStr, String cvStr) {
		List<String> fileNames = new ArrayList<String>();
		File dir = new File(config.getServicePath()+"/sql/");
		for (String file : dir.list()) {
			if (file.contains("update.") && file.endsWith(".sql")) {
				fileNames.add(file);
			}
		}
		update(pvStr, cvStr, fileNames);
	}
	
	public void update(String pvStr, String cvStr, List<String> fileNames) {
		List<String> orderedFileNames2run = internalUpdate(pvStr, cvStr, fileNames);
		for (String fn : orderedFileNames2run) {
			try {
				getGenericMetadataDAO().executeServiceDBScripts(fn);
			} catch (Throwable t) {
				LOG.error("", t);
			}
		}
		
		postUpdate();
	}
	
	public List<String> internalUpdate(String pvStr, String cvStr, List<String> fileNames) {
		List<Integer> pvs = getSubversions(pvStr);
		List<Integer> cvs = getSubversions(cvStr);
		
		List<List<Integer>> fileVersions = new ArrayList<List<Integer>>();
		
		String update = "update.";
		for (String file : fileNames) {
			int idx = file.indexOf(update);
			String fileVers = file.substring(idx+update.length());
			fileVers = fileVers.substring(0, fileVers.length()-4);
			fileVersions.add(getSubversions(fileVers));
		}
		
		int mostSubVersions = 0;
		List<List<Integer>> allSubVersions = new ArrayList<List<Integer>>();
		allSubVersions.addAll(fileVersions);
		allSubVersions.add(pvs);
		allSubVersions.add(cvs);
		for (List<Integer> v : allSubVersions) {
			if (v.size() > mostSubVersions) {
				mostSubVersions = v.size();
			}
		}
		for (List<Integer> v : allSubVersions) {
			while (mostSubVersions != v.size()) {
				v.add(0);
			}
		}
		
		List<List<Integer>> updates2run = new ArrayList<List<Integer>>();
		for (int i=0; i<fileVersions.size(); i++) {
			List<Integer> fv = fileVersions.get(i);
			boolean greaterThanPrevious = false;
			boolean lessThanCurrent = false;
			for (int j=0; j<fv.size(); j++) {
				Integer fsv = fv.get(j);
				Integer csv = cvs.get(j);
				Integer psv = pvs.get(j);
				
				boolean isFinalSubversion = j+1==fv.size();
				boolean stillValid = false;
				if (fsv > psv) {
					greaterThanPrevious = true;
				}
				if (fsv < csv) {
					lessThanCurrent = true;
				}
				if (!isFinalSubversion && 
						(fsv >= psv || greaterThanPrevious) && 
						(fsv <= csv || lessThanCurrent)) {
					stillValid = true;
				}
				if (isFinalSubversion && 
						(fsv > psv || greaterThanPrevious) && 
						(fsv <= csv || lessThanCurrent)) {
					stillValid = true;
				}
				
				if (!stillValid) {
					break;
				}
				if (stillValid && isFinalSubversion) {
					updates2run.add(fv);
				}
			}
		}

		List<String> orderedFileNames2run = new ArrayList<String>();
		while (updates2run.size() > 0) {
			int minVersionIdx = 0;
			for (int i=1; i<updates2run.size(); i++) {
				List<Integer> isv = updates2run.get(i);
				List<Integer> msv = updates2run.get(minVersionIdx);
				boolean newMin = true;
				for (int j=0; j<isv.size(); j++) {
					if (msv.get(j) < isv.get(j)) {
						newMin = false;
						break;
					} else if (isv.get(j) < msv.get(j)) {
						newMin = true;
						break;
					}
				}
				if (newMin) {
					minVersionIdx = i;
				}
			}
			int i=0;
			SortedMap<Integer, String> files = new TreeMap<Integer, String>();
			List<Integer> minVers = updates2run.get(minVersionIdx);
			for (List<Integer> fv : fileVersions) {
				if (fv.equals(minVers)) {
					String fileName = fileNames.get(i);
					files.put(fileName.length(), fileName);
					updates2run.remove(minVers);
				}
				i++;
			}
			for (String filename : files.values()) {
				orderedFileNames2run.add(filename);
			}
		}
		
		return orderedFileNames2run;

	}
	
	protected List<Integer> getSubversions(String s) {
		List<Integer> versions = new ArrayList<Integer>();
		for (String v : s.split("\\.")) {
			versions.add(Integer.parseInt(v));
		}
		return versions;
	}
	
	public void postUpdate() {
	}
	
	public abstract List<Record> process(Record r);

	public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {
		LOG.debug("getService(): "+getService());
		running.lock();
		predecessorKeyedMap.clear();
		successorKeyedMap.clear();
		getRepository().populatePredSuccMaps(predecessorKeyedMap, successorKeyedMap);
		
		ServiceHarvest sh = getServiceDAO().getServiceHarvest(
				inputFormat, inputSet, repo.getName(), getService());
		if (sh == null) {
			sh = new ServiceHarvest();
			sh.setFormat(inputFormat);
			sh.setRepoName(repo.getName());
			sh.setSet(inputSet);
			sh.setService(getService());
		}
		if (sh.getHighestId() == null) {
			if (sh.getUntil() != null) {
				sh.setFrom(sh.getUntil());
			} else {
				sh.setFrom(new Date(System.currentTimeMillis()-(1000*60*60*24*365*50)));
			}
			sh.setUntil(new Date());
		} else {
			if (sh.getUntil() == null || sh.getFrom() == null) {
				throw new RuntimeException("bogus data in service_harvests");
			}
		}
		getServiceDAO().persist(sh);
		
		List<Record> records = repo.getRecords(sh.getFrom(), sh.getUntil(), sh.getHighestId(), inputFormat, inputSet);
		
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
			getRepository().beginBatch();
			for (Record in : records) {
				// TODO: currently the injected records here only contain ids.
				//       This is helpful enough if you simply want to overwrite the
				//       the existing record.  Although I can't think of a reason
				//       why, someone might also want the xml with these injected records.
				//       We may want to supply an optional way of doing that.
				injectKnownSuccessors(in);
				List<Record> out = process(in);
				if (out != null) {
					for (Record rout : out) {
						injectKnownPredecessors(in, rout);
						rout.setService(getService());
					}
					getRepository().addRecords(out);
				}
				sh.setHighestId(in.getId());
			}
			getRepository().endBatch();
			getServiceDAO().persist(sh);
			records = repo.getRecords(sh.getFrom(), sh.getUntil(), sh.getHighestId(), inputFormat, inputSet);
		}
		if (!stopped) {
			sh.setHighestId(null);
			getServiceDAO().persist(sh);
		}
		if (!previouslyPaused) {
			running.unlock();
		}
	}
	
	protected void injectKnownSuccessors(Record in) {
		TLongHashSet tlal = (TLongHashSet)predecessorKeyedMap.get(in.getId());
		if (tlal != null) {
			long[] succIds = tlal.toArray();
			for (int i=0; i<succIds.length; i++) {
				Record succ = new Record();
				succ.setId(succIds[i]);
				//succ.setStatus(Record.UPDATE_REPLACE);
				in.getSuccessors().add(succ);
				tlal = (TLongHashSet)successorKeyedMap.get(succ.getId());
				if (tlal != null) {
					long[] predIds = tlal.toArray();
					for (int j=0; j<predIds.length; j++) {
						Record pred = in;
						if (predIds[j] != in.getId()) {
							pred = new Record();
							pred.setId(predIds[j]);
						}
						succ.addPredecessor(pred);
					}
				}
			}
		}
	}
	
	protected void injectKnownPredecessors(Record in, Record out) {
		if (in.getId() > -1) {
			if (outputSet != null) {
				out.addSet(outputSet);
			}
			TLongHashSet tlal = (TLongHashSet)predecessorKeyedMap.get(in.getId());
			if (tlal == null) {
				tlal = new TLongHashSet();
				predecessorKeyedMap.put(in.getId(), tlal);
			}
			tlal.add(out.getId());
			
			tlal = (TLongHashSet)successorKeyedMap.get(out.getId());
			if (tlal == null) {
				tlal = new TLongHashSet();
				successorKeyedMap.put(out.getId(), tlal);
			}
			tlal.add(in.getId());
		}
	}
}