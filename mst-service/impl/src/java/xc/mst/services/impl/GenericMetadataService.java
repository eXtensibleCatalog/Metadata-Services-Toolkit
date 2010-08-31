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
import java.util.concurrent.Semaphore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordIfc;
import xc.mst.bo.service.ErrorCode;
import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.email.Emailer;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataService;
import xc.mst.services.impl.dao.GenericMetadataDAO;
import xc.mst.utils.MSTConfiguration;

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
	protected Semaphore running = new Semaphore(1);
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
	
	public void cancel() {stopped = true; running.acquireUninterruptibly(); running.release();}
	public void finish() {running.acquireUninterruptibly(); running.release();}
	public void pause()  {paused = true; running.acquireUninterruptibly(); running.release();}
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
	
	/*
	 * 

Helper methods

    * RecordService.createSuccessor. Ex: 

out = getRecordService().createSuccessor(r, getService());

    * A service implementer will typically want to use this method for creating output records. If the record already exists, it isn't necessary to do it this way. Instead you can just populate the existing record with the new contents.
    * */
	
	/**
	 * @param r
	 * <ul>
	 * 		<li>
	 * 			r.status
	 * 			<ul>
	 * 				<li>
	 * 					If the InputRecord has been deleted, then the status will be Record.DELETED.
	 * 					Otherwise it will be Record.ACTIVE. 
	 * 				</li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>
	 * 			r.successors
	 * 			<ul>
	 * 				<li>
	 * 					if this InputRecord has been processed before (determined by the oai-id), then the record 
	 * 					with have successor Records attached to it. The only data attached to these records 
	 * 					is the id. The content (xml) is not attached. If implementers find it necessary to 
	 * 					have this, we may provide an optional way to get that content.
	 * 				</li>
	 * 			</ul>
	 * 	  	</li>
	 * 	 	<li>
	 * 			r.successors.predecessors
	 * 			<ul>
	 * 				<li>
	 * 					if this InputRecord has successors associated with it, then the predecessors of the successors 
	 * 					will also be attached. As with InputRecord.successors, these predecessor records only have the 
	 * 					id associated with them. For a typical one-to-one service, this data is somewhat redundant. 
	 * 					But for more complex services in which a Record may have more than one predecessor, it becomes necessary.
	 * 				</li>
	 * 			</ul>
	 * 	  	</li>
	 * </ul>
	 * @return
	 * 	The process method returns a list of records that are inserted, updated, or deleted as a result of processing this
	 *  InputRecord.  The below attributes are in the context of that list of Records returned by the process method.
	 *	<ul>
	 * 		<li>
	 * 			OutputRecord.id
	 * 			<ul>
	 * 				<li>
	 * 					If a record already exists with this id, then that record will be overwritten with the new contents.
	 * 					If this id is left blank, then the MST will assign it a new id.
	 * 				</li>
	 * 			</ul>
	 * 		</li>
	 *		<li>
	 * 			OutputRecord.status
	 * 			<ul>
	 * 				<li>
	 * 					Record.ACTIVE (default) - Record will be made available for oai-pmh harvesting once persisted.
	 * 				</li>
	 * 	 			<li>
	 * 					Record.HELD - Record will be persisted and will await further notice to be made active. 
	 * 					These records are not included in oai-pmh responses.
	 * 				</li>
	 *				<li>
	 *					Record.DELETED - Record will be marked as deleted. These records are included in oai-pmh responses.
	 *				</li>
	 * 			</ul>
	 * 		</li>
	 */
	public abstract List<OutputRecord> process(InputRecord r);
	
	protected ServiceHarvest getServiceHarvest(Format inputFormat, xc.mst.bo.provider.Set inputSet, String repoName, Service service) {
		LOG.debug("inputFormat: "+inputFormat);
		LOG.debug("inputSet: "+inputSet);
		LOG.debug("repoName: "+repoName);
		LOG.debug("service.getId(): "+service.getId());
		ServiceHarvest sh = getServiceDAO().getServiceHarvest(
				inputFormat, inputSet, repoName, getService());
		if (sh == null) {
			sh = new ServiceHarvest();
			sh.setFormat(inputFormat);
			sh.setRepoName(repoName);
			sh.setSet(inputSet);
			sh.setService(getServiceDAO().getService(getService().getId()));
		}
		if (sh.getHighestId() == null) {
			LOG.debug("sh.getHighestId(): "+sh.getHighestId());
			if (sh.getUntil() != null) {
				sh.setFrom(sh.getUntil());
			} else {
				sh.setFrom(new Date(System.currentTimeMillis()-(1000l*60*60*24*365*50)));
			}
			LOG.debug("sh.getUntil(): "+sh.getUntil());
			LOG.debug("sh.getFrom(): "+sh.getFrom());
			sh.setUntil(new Date());
		} else {
			if (sh.getUntil() == null || sh.getFrom() == null) {
				throw new RuntimeException("bogus data in service_harvests");
			}
		}
		getServiceDAO().persist(sh);
		LOG.debug("sh.getId(): "+sh.getId());
		return sh;
	}

	public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {

		running.acquireUninterruptibly();
		predecessorKeyedMap.clear();
		successorKeyedMap.clear();
		getRepository().populatePredSuccMaps(predecessorKeyedMap, successorKeyedMap);
		
		ServiceHarvest sh = getServiceHarvest(
				inputFormat, inputSet, repo.getName(), getService());
		List<Record> records = repo.getRecords(sh.getFrom(), sh.getUntil(), sh.getHighestId(), inputFormat, inputSet);
		getRepository().beginBatch();
		//repo.beginBatch();
		
		boolean previouslyPaused = false;
		while (records != null && records.size() > 0 && !stopped) {
			if (paused) {
				previouslyPaused = true;
				running.release();
				try {
					Thread.sleep(1000);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				continue;
			}
			if (previouslyPaused) {
				running.acquireUninterruptibly();
			}
			
			for (Record in : records) {
				// TODO: currently the injected records here only contain ids.
				//       This is helpful enough if you simply want to overwrite the
				//       the existing record.  Although I can't think of a reason
				//       why, someone might also want the xml with these injected records.
				//       We may want to supply an optional way of doing that.
				injectKnownSuccessors(in);
				List<OutputRecord> out = process(in);
				if (out != null) {
					for (RecordIfc rout : out) {
						Record rout2 = (Record)rout;
						rout2.addPredecessor(in);
						rout2.setService(getService());
						if (rout2.getId() == -1) {
							getRepositoryDAO().injectId(rout2);
						}
						injectKnownPredecessors(in, rout2);
						if (rout2.getMessages() != null && rout2.getMessages().size() > 0) {
							LOG.debug("** MS: rout2.getMessages():" + rout2.getMessages().get(0).getMessageCode());
						}

						getRepository().addRecord(rout2);
					}
				}
				sh.setHighestId(in.getId());
				
				// Update the error message on incoming record
				//repo.addRecord(in);
			}

			LOG.debug("sh.getId(): "+sh.getId());
			getServiceDAO().persist(sh);

			// Set number of input and output records.
			service.setInputRecordCount(service.getInputRecordCount() + records.size());
			service.setOutputRecordCount(getRepository().getSize());
			
			// TODO : currently # of output records and HarvestOutRecordsAvailable are same. So we can get rid of one of the fields in Services.
			// TODO : Should # of harvest out records available include deleted records too? 
			service.setHarvestOutRecordsAvailable(service.getOutputRecordCount());

			records = repo.getRecords(sh.getFrom(), sh.getUntil(), sh.getHighestId(), inputFormat, inputSet);
			try {
				getServiceDAO().update(service);
			} catch(DataException de) {
				LOG.error("Exception occured while updating the service", de);
			}

		}
//		repo.endBatch();
		getRepository().endBatch();
		if (!stopped) {
			sh.setHighestId(null);
			getServiceDAO().persist(sh);
		}
		if (!previouslyPaused) {
			running.release();
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