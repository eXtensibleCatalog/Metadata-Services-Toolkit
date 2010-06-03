/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.impl;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;
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

public abstract class GenericMetadataService extends SolrMetadataService implements MetadataService {

	protected static Logger LOG = Logger.getLogger(Constants.LOGGER_PROCESSING);

	protected GenericMetadataDAO genericMetadataDAO = null;
	protected Service service = null;
	protected List<ProcessingDirective> processingDirectives = null;
	protected int warningCount = 0;
	protected int errorCount = 0;
	protected int errorCountPerCommit = 0;
	protected Emailer mailer = new Emailer();

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	protected HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	protected boolean isCanceled;
	protected boolean isPaused;
	protected int processedRecordCount = 0;
	protected int totalRecordCount = 0;
	protected int inputRecordCount = 0;
	protected Set outputSet;
	protected List<String> unprocessedErrorRecordIdentifiers = new ArrayList<String>();
	
	protected long startTime = new Date().getTime();
	protected long endTime = 0;
	protected long timeDiff = 0;
	
	protected Repository repository = null;

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
    
    /**
     * Executes the sql scripts in the folder provided
     * @param sqlFolderName Path of the folder that contains the sql scripts
     * @throws IOException 
     */
    private void executeServiceDBScripts(String fileName) throws DataException {
    	ArrayList<String> commands = new ArrayList<String>();
    	StringBuilder command = new StringBuilder();

    	MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();
    	Statement stmt = null;
		BufferedReader br = null;
    	// Read the files
    	try {
    		LOG.info("getUtil(): "+getUtil());
    		LOG.info("fileName: "+fileName);
    		LOG.info("getUtil().slurp(fileName): "+getUtil().slurp(fileName, getClass().getClassLoader()));
    		String str = getUtil().slurp(fileName, getClass().getClassLoader());
    		if (str != null) {
				br = new BufferedReader(new StringReader(str));
				String line = null;
				while((line = br.readLine()) != null){
					if(line.trim().startsWith("--"))
						continue;				
					command.append(line);
					if(line.endsWith(";")){
						commands.add(command.toString());
						command.setLength(0);
					}	
				}
	    	
		    	//Execute the commands
				stmt = dbConnectionManager.createStatement();
				for (String sql : commands) {
					stmt.execute(sql);
				}
    		}
				
		} catch (Exception e) {
			LOG.error("An exception occured while executing the sql scripts.", e);
			throw new DataException("An exception occured while executing the sql scripts.");
		}
		finally {
			if(br!=null) {
				try {
					br.close();
				} catch(IOException ioe) {
					LOG.error("An IO Exception occured while closing the buffered Reader");
					throw new DataException("An IO Exception occured while closing the buffered Reader");
				}
			}
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					LOG.error("An exception occured while closing a connection.");
				}
		}
  	
    }
    
	public Repository getRepository() {
		return this.repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setInputRecordCount(int inputRecordCount) {
	}
	
	public void install() {
		try {
			executeServiceDBScripts("xc/mst/services/install.sql");
			getRepository().installOrUpdateIfNecessary();
			postInstall();
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
	
	public void postInstall() {
	}

	public void uninstall() {
		try {
			executeServiceDBScripts("xc/mst/services/uninstall.sql");
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
		File dir = new File(getFolder()+"/sql/");
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
				executeServiceDBScripts(fn);
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

	public void process(Repository repo, Format format, Set set) {
		//TODO - create tables
		//   one of which is a list of when the last "harvest" was
		Date from = new Date(System.currentTimeMillis()-(1000*60*60*24*500));
		Date until = new Date();
		
		List<Record> records = repo.getRecords(from, until, null);
		
		//TODO - you'll want to move the batches inside of the while loop
		//       although maybe there should be different levels of batches
		getRepository().beginBatch();
		Long highestId = null;
		while (records != null && records.size() > 0) {
			for (Record in : records) {
				getRepository().injectSuccessors(in);
				List<Record> out = process(in);
				if (out != null) {
					getRepository().addRecords(out);
				}
				highestId = in.getId();
			}
			records = repo.getRecords(from, until, highestId);
		}
		getRepository().endBatch();
	}

}