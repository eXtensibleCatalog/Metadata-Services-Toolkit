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

import xc.mst.action.BaseActionSupport;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.processingDirective.ConfigFileException;
import xc.mst.utils.MSTConfiguration;

/**
 * Edits the details of a service
 *
 * @author Tejaswi Haramurali
 */
public class EditService extends BaseActionSupport
{
    /** serial id */
	private static final long serialVersionUID = -6594114196228418111L;

    /** Denotes the type of error */
    private String errorType;

    /** List of XCCFG config files which were present in the directory (location of this directory can be obtained from the manual/documentation) */
    private List<XccFgFile> serviceFiles = new ArrayList<XccFgFile>();

    /** The XCCFG file selected by the user */
    private String selectedLocation;

    /** The ID of the service whose details are being edited */
    private int serviceId;

    /** The temporary service object which is used to populate data in the JSP page*/
    private Service temporaryService;
    
      /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
    
	/** Indicates whether to reprocess the records or not */ 
	private boolean reprocessRecords;

     /**
     * Overrides default implementation to view the edit service page.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
        	reprocessRecords = true;
        	
            temporaryService = getServicesService().getServiceById(serviceId);
            if(temporaryService==null)
            {
                this.addFieldError("viewEditServiceError", "Error loading edit-service page. An email has been sent to the administrator.");
                return SUCCESS;
            }
            setTemporaryService(temporaryService);
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
            
            setServiceFiles(serviceFiles);
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(), dce);
            errorType = "error";
            this.addFieldError("viewEditServiceError", "Unable to connect to the database. Database configuration may be incorrect");
            return SUCCESS;
        }
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
     * The method that does the actual task of editing the details of a service
     *
     * @return {@link #SUCCESS}
     */
    public String editService()
    {
        try
        {
            Service tempService = getServicesService().getServiceById(serviceId);
            if(tempService==null)
            {
                this.addFieldError("EditServiceError", "Error occurred while editing service. An email has been sent to the administrator");
                getUserService().sendEmailErrorReport();
                return INPUT;
            }
            File file = new File(getSelectedLocation());
            getServicesService().updateService(file,tempService,reprocessRecords);
            return SUCCESS;
        }
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            errorType = "error";
            this.addFieldError("editServiceError","Unable to connect to the database. Database configuration may be incorrect");
            populateListBox();
            return INPUT;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            errorType = "error";
            this.addFieldError("editServiceError","Error occurred while editing service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
        catch(IndexException ie)
        {
            log.error(ie.getMessage(),ie);
            errorType = "error";
            this.addFieldError("editServiceError","Error occurred while editing service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
        catch(IOException ie)
        {
            log.error(ie.getMessage(),ie);      
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
         catch(ConfigFileException cfe)
        {
            log.error(cfe.getMessage(),cfe);
            errorType = "error";
            this.addFieldError("editServiceError","Error occurred while editing service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            populateListBox();
            return INPUT;
        }
    }

     /**
     * Sets the temporary service object
     *
     * @param temporaryService service object
     */
    public void setTemporaryService(Service temporaryService)
    {
        this.temporaryService = temporaryService;
    }

    /**
     * Returns the temporary Service object
     *
     * @return service object
     */
    public Service getTemporaryService()
    {
        return this.temporaryService;
    }

    /**
     * Sets the service ID of the service whose details are to be edited
     *
     * @param serviceId service ID
     */
    public void setServiceId(int serviceId)
    {
        this.serviceId = serviceId;
    }

    /**
     * Returns the ID of the service whose details are to be edited
     *
     * @return service ID
     */
    public int getServiceId()
    {
        return this.serviceId;
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
                
            }
            
            for(File xccfgFile : xccfgFolderList) {
    			XccFgFile configFile = new XccFgFile(xccfgFile.getName(), xccfgFile.getPath());
    			serviceFiles.add(configFile);
    		}
        }

    }

	public boolean isReprocessRecords() {
		return reprocessRecords;
	}

	public void setReprocessRecords(boolean reprocessRecords) {
		this.reprocessRecords = reprocessRecords;
	}
}

