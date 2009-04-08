
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
import java.util.*;
import org.apache.log4j.Logger;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.*;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 * This action method is the first step in adding a new processing directive
 *
 * @author Tejaswi Haramurali
 */
public class AddProcessingDirective extends ActionSupport
{
    /**This String value denotes whether the Processing Directive session variable needs to be reset */
    private String refreshSession;

    /** Creates a service object for Services */
    private ServicesService servService = new DefaultServicesService();

    /** Creates a service object for Providers */
    private ProviderService provService = new DefaultProviderService();

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The source that is associated with the processing directive */
    private String source;

    /** The service associated with the processing Directive */
    private String service;

    /** List of all providers */
    private List<Provider> providerList;

    /** List of all services */
    private List<Service> serviceList;

    /** Temporary processing directive object that is used to display details on the JSP */
    private ProcessingDirective temporaryProcessingDirective;
    
	/** Error type */
	private String errorType; 

    /**
     * Sets the temporary processing directive object
     *
     * @param temporaryProcessingDirective processing directive object
     */
    public void setTemporaryProcessingDirective(ProcessingDirective temporaryProcessingDirective)
    {
        this.temporaryProcessingDirective = temporaryProcessingDirective;
    }

    /**
     * Returns the temporary processing directive object which is used to display details in the JSP
     *
     * @return processing directive object
     */
    public ProcessingDirective getTemporaryProcessingDirective()
    {
        return this.temporaryProcessingDirective;
    }

    /**
     * Sets the list of all providers
     *
     * @param providerList list of all providers
     */
    public void setProviderList(List<Provider> providerList)
    {
        this.providerList = providerList;
    }

    /**
     * Returns the list of all providers
     *
     * @return list of all providers
     */
    public List<Provider> getProviderList()
    {
        return this.providerList;
    }

    /**
     * Sets the list of all services
     *
     * @param serviceList list of all services
     */
    public void setServiceList(List<Service> serviceList)
    {
        this.serviceList = serviceList;
    }

    /**
     * Returns the list of all services
     *
     * @return list of all services
     */
    public List<Service> getServiceList()
    {
        return this.serviceList;
    }

    /**
     * Sets the value of refresh session
     *
     * @param refreshSession
     */
    public void setRefreshSession(String refreshSession)
    {
        this.refreshSession = refreshSession;
    }

    /**
     * Returns the value of refresh session
     *
     * @return
     */
    public String getRefreshSession()
    {
        return this.refreshSession;
    }

    /**
     * Sets the source for a particular processing directive (Can be either a service or a provider)
     *
     * @param Source source
     */
    public void setSource(String Source)
    {
        this.source = Source;
    }

    /**
     * Gets the source that is associated with a processing directive
     *
     * @return source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Sets the service associated with the processing directive
     *
     * @param service service object
     */
    public void setService(String service)
    {
        this.service = service;
    }

    /**
     * Returns the service associated with the processing directive
     *
     * @return service object
     */
    public String getService()
    {
        return this.service;
    }


     /**
     * Overrides default implementation to view the add processing directives page.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Map sessionMap =  ActionContext.getContext().getSession();
            setProviderList(provService.getAllProviders());
            setServiceList(servService.getAllServices());
            if(refreshSession==null)
            {
                ProcessingDirective tempProcDir = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");

                if(tempProcDir!=null)
                {
                    setTemporaryProcessingDirective(tempProcDir);
                }
            }
            else
            {
                sessionMap.remove("temporaryProcessingDirective");
            }
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("addProcessingDirectiveError", "Add Processing Directive Page cannot be displayed");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Step 1 in adding a new Processing directive
     *
     * @return {@link #SUCCESS}
     */
    public String addProcessingDirectives()
    {
        try
        {
            Map sessionMap =  ActionContext.getContext().getSession();

            temporaryProcessingDirective = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");
            if(temporaryProcessingDirective==null)
            {
                temporaryProcessingDirective = new ProcessingDirective();
            }

            Provider tempProvider = provService.getProviderByName(source);
            Service tempService = servService.getServiceByName(source);
            if(tempProvider!=null)
            {
                 temporaryProcessingDirective.setSourceProvider(tempProvider);
                 temporaryProcessingDirective.setSourceService(null);
                 sessionMap.put("sourceType", "provider");
            }
            else
            {
                 temporaryProcessingDirective.setSourceService(tempService);
                 temporaryProcessingDirective.setSourceProvider(null);
                 sessionMap.put("sourceType", "service");
            }
            temporaryProcessingDirective.setService(servService.getServiceById(Integer.parseInt(service)));
            sessionMap.put("temporaryProcessingDirective", temporaryProcessingDirective);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("addProcessingDirectiveError", "Error in Adding a Source");
            errorType = "error";
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
