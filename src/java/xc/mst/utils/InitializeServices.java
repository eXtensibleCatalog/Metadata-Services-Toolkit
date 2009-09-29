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
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.harvest.DefaultHarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.processingDirective.DefaultJobService;
import xc.mst.manager.processingDirective.JobService;
import xc.mst.manager.record.DefaultRecordService;
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
	    LogDAO logDao = new DefaultLogDAO();
	    ServiceDAO serviceDao = new DefaultServiceDAO();
	    
	    // Load the services
	    List<Service> services = null;
	    String servicesLogFileName = null;
	    List<HarvestSchedule> schedules = null;
		HarvestScheduleDAO scheduleDao = new DefaultHarvestScheduleDAO();
		
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
	    		
	    		MetadataService.checkService(service.getId(), "", false);
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

	    // Run service which has error status and has records waiting to be processed
	    try {
		    List<Service> allServices = serviceDao.getAll();
    		RecordService recordService = new DefaultRecordService();
			JobService jobService = new DefaultJobService();
		    
		    for(Service service:allServices) {
		    	if (service.getStatus().equalsIgnoreCase(Constants.STATUS_SERVICE_ERROR)) {
		    		int count = recordService.getCountOfRecordsToBeProcessedVyService(service.getId());
		    		if (count > 0) {
		    			// Add job to queue in database
						try {
							Job job = new Job(service, 0);
							job.setOrder(jobService.getMaxOrder() + 1); 
							jobService.insertJob(job);
						} catch (DatabaseConfigException dce) {
							log.error("DatabaseConfig exception occured when ading jobs to database", dce);
						}
						
		    		}
		    	}
		    }
	    } catch (DatabaseConfigException dce) {
	    	log.error("Problem connecting to database. Check database configuration." , dce);
	    } catch (IndexException ie) {
	    	log.error("Problem connecting to database. Check database configuration." , ie);
	    }
	  }

	  public void doGet(HttpServletRequest req, HttpServletResponse res) {
	  }

}
