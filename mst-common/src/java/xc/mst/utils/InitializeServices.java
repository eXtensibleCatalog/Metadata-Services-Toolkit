/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.Job;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.dao.service.OaiIdentifierForServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.processingDirective.JobService;
import xc.mst.manager.record.RecordService;
import xc.mst.services.MetadataService;

/**
 * Initialize services
 *
 * @author Eric Osisek
 */
public class InitializeServices  extends HttpServlet {

	/**
	 * Eclipse generated id
	 */
	private static final long serialVersionUID = 684759119754656298L;
	
	/** The logger object */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * Initialize logging
	 */
	public void init() 
	{
	    LogDAO logDao = (LogDAO)MSTConfiguration.getBean("LogDAO");
	    ServiceDAO serviceDao = (ServiceDAO)MSTConfiguration.getBean("ServiceDAO");
	    
	    // Load the services
	    List<Service> services = null;
	    String servicesLogFileName = null;
	    List<HarvestSchedule> schedules = null;
		HarvestScheduleDAO scheduleDao = (HarvestScheduleDAO)MSTConfiguration.getBean("HarvestScheduleDAO");
		
		try 
		{
			servicesLogFileName = logDao.getById(Constants.LOG_ID_SERVICE_MANAGEMENT).getLogFileLocation();
			services = serviceDao.getAll();
			schedules = scheduleDao.getAll();
		} 
		catch (DatabaseConfigException e1) 
		{
			return;
		}
		
	    for(Service service : services)
	    {
	    	String jar = service.getServiceJar();
			String className = service.getClassName();
			
	    	// The .jar file we need to load the service from
    		File jarFile = new File(jar);
    		try 
    		{
    			// The class loader for the MetadataService class
    			ClassLoader serviceLoader = MetadataService.class.getClassLoader();
    			
    			// Load the class from the .jar file
    			URLClassLoader loader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, serviceLoader);
				loader.loadClass(className);
	    		
	    		ServiceUtil.getInstance().checkService(service.getId(), "", false);
			} 
    		catch (ClassNotFoundException e) 
    		{
    			LogWriter.addError(servicesLogFileName, "Error loading service: The class " + service.getClassName() + " could not be found in the .jar file " + service.getServiceJar());
			}
    		catch (MalformedURLException e) 
    		{
    			LogWriter.addError(servicesLogFileName, "Error loading service: " + service.getName());
			}
	    }
	    
	    LogWriter.addInfo(servicesLogFileName, "Loaded services");
	    
	    // TODO why schedule is set to NOT_RUNNING. In case of schedule with status ERROR, it will be replaced  by NOT_RUNNING
	    for(HarvestSchedule schedule : schedules)
	    {
	    	schedule.setStatus(Constants.STATUS_SERVICE_NOT_RUNNING);
	    	try 
	    	{
				scheduleDao.update(schedule, false);
			} 
	    	catch (DataException e) 
			{
	    		log.error("Exception occured while updating database with schedule status", e);
			}
	    }

	    /* Run service which has error status and has records waiting to be processed
	     * Also run service with status 'running'. If service has status running it means there was a power shutdown when service 
	     * was running and so it needs to be stated again.
	     */ 
	    try {
		    List<Service> allServices = serviceDao.getAll();
    		RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");
			JobService jobService = (JobService)MSTConfiguration.getBean("JobService");
		    
		    for(Service service:allServices) {
	    		if (service.getStatus().equalsIgnoreCase(Constants.STATUS_SERVICE_RUNNING)) {
	    			// If service status is 'running' then the reason must be like server shutdown when service was running. 
		    		// Because of this, there was no chance to save the last used record id and OAI identifiers. 
		    		// So here we can going to update db with last used record id and OAI identifiers
		    		updateRecordAndOAIIds(service.getId());
		    		
		    		int count = recordService.getCountOfRecordsToBeProcessedVyService(service.getId());
		    		if (count > 0) {
		    			// Add job to queue in database
						Job job = new Job(service, 0, Constants.THREAD_SERVICE);
						// Setting order to 1 so that it will continue the running service first rather than starting the next job in queue
						// There cannot be 2 jobs with order 1 because already a service has status 'running' which means the order 1 is already done.
						job.setOrder(1); 
						jobService.insertJob(job);
						
		    		} else {
		    			// Then it means service finished committing only status needs to be updated. 
		    			service.setStatus(Constants.STATUS_SERVICE_NOT_RUNNING);
		    			serviceDao.update(service);
		    		}
		    	}
		    }
		    
		    for(Service service:allServices) {
		    	if (service.getStatus().equalsIgnoreCase(Constants.STATUS_SERVICE_ERROR)) {
		    		int count = recordService.getCountOfRecordsToBeProcessedVyService(service.getId());
		    		if (count > 0) {
		    			// Add job to queue in database
						Job job = new Job(service, 0, Constants.THREAD_SERVICE);
						// Setting order to 1 so that it will continue the errored service first rather than starting the next job in queue
						// There cannot be 2 jobs with order 1 because already a service has status 'error'  which means the order 1 is already done.
						job.setOrder(1); 
						jobService.insertJob(job);
						
		    		} 
		    	}
		    }
	    } catch (DatabaseConfigException dce) {
	    	log.error("Problem connecting to database. Check database configuration." , dce);
	    } catch (IndexException ie) {
	    	log.error("Problem connecting to database. Check database configuration." , ie);
	    } catch (DataException de) {
	    	log.error("Problem connecting to database. Check database configuration." , de);
	    }
	  }

	  public void doGet(HttpServletRequest req, HttpServletResponse res) {
	  }

	  /*
	   * Updates last used record and OAI identifier in database
	   */
	  private void updateRecordAndOAIIds(int serviceId) throws IndexException, DatabaseConfigException {
		  
		  RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");
		  Record record = recordService.getLastCreatedRecord(serviceId);
		  
		  if (record != null) {
			  
			  // Update last record ID
			  XcIdentifierForFrbrElementDAO xcIdentifierDAO = (XcIdentifierForFrbrElementDAO)MSTConfiguration.getBean("XcIdentifierForFrbrElementDAO");
			  xcIdentifierDAO.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_RECORD, record.getId());
			  
			  // Update last OAI Identifier
			  OaiIdentifierForServiceDAO oaiIdentifierDAO = (OaiIdentifierForServiceDAO)MSTConfiguration.getBean("OaiIdentifierForServiceDAO");
			  String oaiIdentifier = record.getOaiIdentifier();
			  int indexOfSlash = oaiIdentifier.lastIndexOf("/");
			  int identifier = Integer.valueOf(oaiIdentifier.substring(indexOfSlash + 1));
			  oaiIdentifierDAO.writeNextOaiId(serviceId, identifier);
			  
			  log.info("Updates database with next to use record id as " + (record.getId() + 1) + " and OAI identifiers as " + (identifier + 1) + " for service Id = " + serviceId);
		  }
	  }
}
