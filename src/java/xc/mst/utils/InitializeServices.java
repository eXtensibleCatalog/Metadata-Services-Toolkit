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

import xc.mst.bo.harvest.HarvestSchedule;
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

	/**
	 * Initialize logging
	 */
	public void init() 
	{
	    LogDAO logDao = new DefaultLogDAO();
	    
	    // Load the services
	    List<Service> services = null;
	    String servicesLogFileName = null;
	    List<HarvestSchedule> schedules = null;
		HarvestScheduleDAO scheduleDao = new DefaultHarvestScheduleDAO();
		
		try 
		{
			servicesLogFileName = logDao.getById(Constants.LOG_ID_SERVICE_MANAGEMENT).getLogFileLocation();
			ServiceDAO serviceDao = new DefaultServiceDAO();
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
	    		
	    		MetadataService.checkService(service.getId(), Constants.STATUS_SERVICE_NOT_RUNNING, false);
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
	    
	    for(HarvestSchedule schedule : schedules)
	    {
	    	schedule.setStatus(Constants.STATUS_SERVICE_NOT_RUNNING);
	    	try 
	    	{
				scheduleDao.update(schedule, false);
			} 
	    	catch (DataException e) 
			{
			}
	    }
	  }

	  public void doGet(HttpServletRequest req, HttpServletResponse res) {
	  }

}
