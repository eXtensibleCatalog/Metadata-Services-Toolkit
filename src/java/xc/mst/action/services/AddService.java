/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.services;

import xc.mst.action.*;
import xc.mst.action.processingDirective.*;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * This is the service class for adding a new service in the MST
 *
 * @author Tejaswi Haramurali
 */
public class AddService extends ActionSupport
{
    /** Service object to ineract with the services in the MST */
    private ServicesService servicesService = new DefaultServicesService();

    /** Denotes the type of error */
    private String errorType;

    /** List of XCCFG files */
    private List<String> serviceFiles = new ArrayList<String>();

    /** The XCCFG file that is selected by the user */
    private String selectedLocation;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**
     * Returns the list of XCCFG config files at the hard-coded location (location can be found in documentation/manual)
     *
     * @return list of config files
     */
    public List<String> getServiceFiles()
    {
        return this.serviceFiles;
    }

    /**
     * Sets the list of XCCFG config files which were found at the hard-coded location (location can be found in documentation/manual)
     *
     * @param serviceFileList list of config files
     */
    public void setServiceFiles(List<String> serviceFiles)
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
        try
        {
            File dir = new File("serviceConfig");
            FileFilter fileFilter =  new XCCGFileFilter();

            File[] fileList = dir.listFiles(fileFilter);
            for(int i=0;i<fileList.length;i++)
            {
                serviceFiles.add(fileList[i].getName());
            }
            setServiceFiles(serviceFiles);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("The Add Service page could not be loaded correctly",e);
            this.addFieldError("viewAddServiceError", "The Add Service page could not be loaded correctly");
            return INPUT;
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
            String location = "serviceConfig/" + getSelectedLocation();
            File file = new File(location);
            servicesService.addNewService(file);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("Error in adding a new Service",e);
            errorType = "error";
            this.addFieldError("addServiceError",e.getMessage());
            File dir = new File("serviceConfig");
            FileFilter fileFilter =  new XCCGFileFilter();

            File[] fileList = dir.listFiles(fileFilter);
            for(int i=0;i<fileList.length;i++)
            {
                serviceFiles.add(fileList[i].getName());
            }
            setServiceFiles(serviceFiles);
            return INPUT;
        }
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