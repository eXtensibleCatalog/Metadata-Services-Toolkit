/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.action.BaseActionSupport;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.processingDirective.ConfigFileException;
import xc.mst.utils.MSTConfiguration;

/**
 * This is the service class for adding a new service in the MST
 *
 * @author Tejaswi Haramurali
 */
public class AddService extends BaseActionSupport
{
    /** Serial Id */
    private static final long serialVersionUID = -6656889195768543265L;

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
        try {
            serviceFiles.addAll(getServicesService().getServicesAvailableForInstall());
        } catch (Throwable t) {
            errorType = "error";
            log.error("Problem with service configuration. Check the path of service folder.");
            this.addFieldError("configFilesNotExistError","Problem with service configuration. Check the path of service folder and follow the instructions in installation manual.");
            return SUCCESS;
        }
        return SUCCESS;
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
            getServicesService().addNewService(getSelectedLocation());
            return SUCCESS;
        }
        catch(DataException de)
        {
            log.error(de.getMessage(),de);
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            return INPUT;
        }
        catch(IOException ie)
        {
            log.error(ie.getMessage(),ie);
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
            return INPUT;
        }
        catch(ConfigFileException cfe)
        {
            log.error(cfe.getMessage(),cfe);
            errorType = "error";
            this.addFieldError("addServiceError","Error occurred while adding service. An email has been sent to the administrator");
            getUserService().sendEmailErrorReport();
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
