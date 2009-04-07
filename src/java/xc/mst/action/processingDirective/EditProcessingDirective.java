
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.*;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * The first step in editing a processing directive
 * @author Tejaswi Haramurali
 */
public class EditProcessingDirective extends ActionSupport
{
    /** Temporary Processing Directive object which is used to display details on the JSP */
    private ProcessingDirective temporaryProcessingDirective;

    /**ID of the processing directive whose details are to be edited */
    private int processingDirectiveId;

    /** The source that is associated with a procesisng directive*/
    String source;

    /** The complete list of processing directives in the system */
    List<ProcessingDirective> processingDirectivesList;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /**List of all providers */
    List<Provider> providerList;

    /**List of all services */
    List<Service> serviceList;

    /** specifies the type of source i.e whther it is a provider or a service */
    String sourceType;

    /** Ther service associated with the processing directive */
    String service;

    /**Creates a service object for processing directives */
    private ServicesService servService = new DefaultServicesService();

    /** creates a service object for providers */
    private ProviderService provService = new DefaultProviderService();

    /** creates a service object for processing directives */
    private ProcessingDirectiveService PDService = new DefaultProcessingDirectiveService();
    
	/** Error type */
	private String errorType; 

    /**
     * sets the ID of the processing directive to be edited
     * @param processingDirectiveId ID of the processing directive
     */
    public void setProcessingDirectiveId(String processingDirectiveId)
    {

        this.processingDirectiveId = Integer.parseInt(processingDirectiveId);
    }

    /**
     * returns the ID of the processing directive to be edited
     * @return ID of the processing directive
     */
    public int getProcessingDirectiveId()
    {

        return processingDirectiveId;
    }

    /**
     * sets the source type
     * @param sourceType source type
     */
    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    /**
     * returns the type of the source
     * @return source type
     */
    public String getSourceType()
    {
        return this.sourceType;
    }
    /**
     * sets the source for a processing directive
     * @param Source source
     */
    public void setSource(String Source)
    {
        this.source = Source;
    }

    /**
     * returns the source for a processing directive
     * @return source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * sets the service that is associated with the processing directive
     * @param service service object
     */
    public void setService(String service)
    {
        this.service = service;
    }

    /**
     * returns the service that is associated with the processing directive
     * @return service object
     */
    public String getService()
    {
        return this.service;
    }

    /**
     * sets temporary processing directive
     *
     * @param temporaryProcessingDirective
     */
    public void setTemporaryProcessingDirective(ProcessingDirective temporaryProcessingDirective)
    {
        this.temporaryProcessingDirective = temporaryProcessingDirective;
    }

    /**
     * returns processing directive
     *
     * @return processing directive object
     */
    public ProcessingDirective getTemporaryProcessingDirective()
    {
        return this.temporaryProcessingDirective;
    }

    /**
     * sets the list of all providers
     *
     * @param providerList
     */
    public void setProviderList(List<Provider> providerList)
    {
        this.providerList = providerList;
    }

    /**
     * returns the list of all providers
     * @return provider list
     */
    public List<Provider> getProviderList()
    {
        return this.providerList;
    }

    /**
     * sets the list of services
     * @param serviceList
     */
    public void setServiceList(List<Service> serviceList)
    {
        this.serviceList = serviceList;
    }

    /**
     * returns the list of services
     * @return list of services
     */
    public List<Service> getServiceList()
    {
        return this.serviceList;
    }

    /**
     * Overrides default implementation to view the edit processing directives (step 1) page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Map sessionMap =  ActionContext.getContext().getSession();
            temporaryProcessingDirective = PDService.getByProcessingDirectiveId(processingDirectiveId);
            if(temporaryProcessingDirective==null)
            {
                temporaryProcessingDirective = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");
            }
            Service tempService = temporaryProcessingDirective.getSourceService();
            Provider tempProvider = temporaryProcessingDirective.getSourceProvider();

            if(tempService!=null)
            {
                setSourceType("service");
            }
            else
            {

                setSourceType("provider");

            }

            setProviderList(provService.getAllProviders());
            setServiceList(servService.getAllServices());
            setTemporaryProcessingDirective(temporaryProcessingDirective);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("editProcessingDirectiveEror", "Error : Problem in displaying the Specfied Processing Directive");
            errorType = "error";
            return INPUT;
        }
    }

     /**
     * Step 1 in editing a Processing directive
     * @return {@link #SUCCESS}
     */
    public String editProcessingDirectives()
    {
        try
        {
            Map sessionMap =  ActionContext.getContext().getSession();
            temporaryProcessingDirective = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");

            if(temporaryProcessingDirective==null)
            {
                 temporaryProcessingDirective = PDService.getByProcessingDirectiveId(processingDirectiveId);
            }
            Provider tempProvider = provService.getProviderByName(source);
            Service tempService = servService.getServiceByName(source);
            
            if(tempProvider!=null)
            {
                sessionMap.put("sourceType", "provider");
                System.out.println("(Inside editprocdir method) temprovider is being set");
                temporaryProcessingDirective.setSourceProvider(tempProvider);
                temporaryProcessingDirective.setSourceService(null);

            }
            else
            {
                sessionMap.put("sourceType", "service");
                System.out.println("(Inside editprocdir method) tempservice is being set");
                temporaryProcessingDirective.setSourceService(tempService);
                temporaryProcessingDirective.setSourceProvider(null);

            }
            sessionMap.put("service", getService());
            temporaryProcessingDirective.setService(servService.getServiceById(Integer.parseInt(getService())));

            sessionMap.put("temporaryProcessingDirective", temporaryProcessingDirective);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("editProcessingDirectiveError", "Error in Adding a Source");
            errorType = "error";
            return INPUT;
        }

    }

    /**
     * returns error type
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * sets error type
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
