/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.processingDirective.ConfigFileException;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.UserService;
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This is the service class for adding a new service in the MST
 *
 * @author Tejaswi Haramurali
 */
public class AddService extends ActionSupport
{
    /** Serial Id */
	private static final long serialVersionUID = -6656889195768543265L;

	/** Service object to ineract with the services in the MST */
    private ServicesService servicesService = new DefaultServicesService();

    /** User Service Object **/
    UserService userService = new DefaultUserService();

    /** Denotes the type of error */
    private String errorType;

    /** List of XCCFG files */
    private List<XccFgFile> serviceFiles = new ArrayList<XccFgFile>();

    /** The XCCFG file that is selected by the user */
    private String selectedLocation;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Returns the list of XCCFG config files at the hard-coded location (location can be found in documentation/manual)
     *
     * @return list of config files
     */
    public List<XccFgFile> getServiceFiles()
    {
        return this.serviceFiles;
    }

    /**
     * Sets the list of XCCFG config files which were found at the hard-coded location (location can be found in documentation/manual)
     *
     * @param serviceFileList list of config files
     */
    public void setServiceFiles(List<XccFgFile> serviceFiles)
    {
        this.serviceFiles = serviceFiles;
    }

    /**
     * The XCCFG file that is selected by the user
     *
     * @param selectedLocation config file
     */
    public void setSelectedLocation(String selectedLocation)
    {
        this.selectedLocation = selectedLocation;
    }

    /**
     * Returns the XCCFG file selected by the user
     *
     * @return config file
     */
    public String getSelectedLocation()
    {
        return this.selectedLocation;
    }

    /**
     * Overrides default implementation to view the add service page.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
    	File dir = new File(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "services");
    	FileFilter fileFilter =  new XCCGFileFilter();

    	File[] fileList = dir.listFiles();
    	
    	if (fileList == null) 
    	{
    		errorType = "error";
    		log.error("Problem with service configuration. Check the path of service folder.");
    		this.addFieldError("configFilesNotExistError","Problem with service configuration. Check the path of service folder and follow the instructions in installation manual.");
    		return SUCCESS;
    	}
    	for(File file : fileList)
    	{
    		String xccfgFolderLocation = file.getPath() + MSTConfiguration.FILE_SEPARATOR + "serviceConfig";
    		File xccfgFolder = new File(xccfgFolderLocation);
    		
    		if(!xccfgFolder.exists() || !xccfgFolder.isDirectory())
    			continue;
            	
    		File[] xccfgFolderList = xccfgFolder.listFiles(fileFilter);
    		
    		if (xccfgFolderList == null) 
    		{
    			errorType = "error";
    			log.error("Problem with service configuration. Check the path of service folder.");
    			this.addFieldError("configFilesNotExistError","Problem with service configuration. Check the path of service folder and follow the instructions in installation manual.");
    			return SUCCESS;
    		}
                
    		for(File xccfgFile : xccfgFolderList) {
    			XccFgFile configFile = new XccFgFile(xccfgFile.getName(), xccfgFile.getPath());
    			serviceFiles.add(configFile);
    		}
    	}
    	
    	return SUCCESS;
    }

    /**
     * Class to represent xccfg file
     * 
     * @author sharmilar
     *
     */
    public class XccFgFile {
    	/** Name of file */
    	private String name;
    	
    	/** Path of file */
    	private String path;

    	/** Constructor */
    	XccFgFile(String name, String path) {
    		this.name = name;
    		this.path = path;
    		
    	}
    	
    	/** 
    	 * Get file name 
    	 */
		public String getName() {
			return name;
		}

		/**
		 * Get file path
		 * @return
		 */
		public String getPath() {
			return path;
		}
    }
    
    /**
     * The method that actually adds the service to the MST
     *
     * @return {@link #SUCCESS}
     */
    public String addService()
    {
        try
        {
            File file = new File(getSelectedLocation());
            servicesService.addNewService(file);
            return SUCCESS;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            userService.sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
        catch(IOException ie)
        {
            log.error(ie.getMessage(),ie);           
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            userService.sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
        catch(ConfigFileException cfe)
        {
            log.error(cfe.getMessage(),cfe);           
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            userService.sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
        
        /*
        finally
        {
            File dir = new File(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "serviceConfig");
            FileFilter fileFilter =  new XCCGFileFilter();

            File[] fileList = dir.listFiles(fileFilter);
            for(int i=0;i<fileList.length;i++)
            {
                serviceFiles.add(fileList[i].getName());
            }
            setServiceFiles(serviceFiles);
            return INPUT;
        }
         * */
    }

    /**
     * Returns error type
     *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets error type
     *
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

    private void populateListBox()
    {
    	File dir = new File(MSTConfiguration.getUrlPath() + MSTConfiguration.FILE_SEPARATOR + "services");
    	FileFilter fileFilter =  new XCCGFileFilter();

    	File[] fileList = dir.listFiles();
    	
    	if (fileList == null) 
    	{
    		errorType = "error";
    		log.error("Problem with service configuration. Check the path of service folder.");
    		this.addFieldError("configFilesNotExistError","Problem with service configuration. Check the path of service folder and follow the instructions in installation manual.");
    		return;
    	}
    	for(File file : fileList)
    	{
    		String xccfgFolderLocation = file.getPath() + MSTConfiguration.FILE_SEPARATOR + "serviceConfig";
    		File xccfgFolder = new File(xccfgFolderLocation);
    		
    		if(!xccfgFolder.exists() || !xccfgFolder.isDirectory())
    			continue;
            	
    		File[] xccfgFolderList = xccfgFolder.listFiles(fileFilter);
    		
    		if (xccfgFolderList == null) 
    		{
    			errorType = "error";
    			log.error("Problem with service configuration. Check the path of service folder.");
    			this.addFieldError("configFilesNotExistError","Problem with service configuration. Check the path of service folder and follow the instructions in installation manual.");
    			return;
    		}
                
    		for(File xccfgFile : xccfgFolderList) {
    			XccFgFile configFile = new XccFgFile(xccfgFile.getName(), xccfgFile.getPath());
    			serviceFiles.add(configFile);
    		}
    	}
    	setServiceFiles(serviceFiles);
    }
}



/**
 * This class is used to filter out files which dont have the required .xccf extension
 *
 * @author Tejaswi Haramurali
 */
class XCCGFileFilter implements FileFilter
{
    public boolean accept(File file)
    {
        return file.getName().contains(".xccfg");
    }
}
