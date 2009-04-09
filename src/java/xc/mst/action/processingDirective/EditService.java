/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * Edits the detais of a service
 *
 * @author Tejaswi Haramurali
 */
public class EditService extends ActionSupport
{
    ServicesService servicesService = new DefaultServicesService();
    List<String> serviceFileList;
    String selectedLocation;
    String serviceId;
    Service temporaryService;

    public EditService()
    {
        serviceFileList = new ArrayList<String>();
    }

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    public void setTemporaryService(Service temporaryService)
    {
        this.temporaryService = temporaryService;
    }

    public Service getTemporaryService()
    {
        return this.temporaryService;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getServiceId()
    {
        return this.serviceId;
    }

    public List<String> getServiceFileList()
    {
        return this.serviceFileList;
    }

    public void setServiceFileList(List<String> serviceFileList)
    {
        this.serviceFileList = serviceFileList;
    }

    public void setSelectedLocation(String selectedLocation)
    {
        this.selectedLocation = selectedLocation;
    }

    public String getSelectedLocation()
    {
        return this.selectedLocation;
    }

    @Override
    public String execute()
    {
        try
        {
            temporaryService = servicesService.getServiceById(Integer.parseInt(getServiceId()));
            setTemporaryService(temporaryService);
            File dir = new File("serviceConfig");
            System.out.println("The number of files in that location is "+dir.list().length);
            FileFilter fileFilter =  new XCCGFileFilter();

            File[] fileList = dir.listFiles(fileFilter);
            for(int i=0;i<fileList.length;i++)
            {
                serviceFileList.add(fileList[i].getName());
            }
            setServiceFileList(serviceFileList);
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("viewEditServiceError", "ERROR : The page could not be loaded correctly");
            return INPUT;
        }
    }

    public String editService()
    {
        try
        {
            String location = "serviceConfig/" + getSelectedLocation();
            File file = new File(location);
            System.out.println("The path is "+location);
            servicesService.updateService(file, servicesService.getServiceById(Integer.parseInt(serviceId)));
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            this.addFieldError("editServiceError", "ERROR : The service could not be edited correctly");
            return INPUT;
        }
    }
}

