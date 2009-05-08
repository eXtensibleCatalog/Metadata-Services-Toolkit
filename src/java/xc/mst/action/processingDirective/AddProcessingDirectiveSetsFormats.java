
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.*;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.repository.SetService;

/**
 *  This action method is the first step in adding a new processing directive
 *
 * @author Tejaswi Haramurali
 */
public class AddProcessingDirectiveSetsFormats extends ActionSupport implements ServletRequestAware
{
    /** Ceates service object for processing directives*/
    private ProcessingDirectiveService PDService = new DefaultProcessingDirectiveService();

    /** creates service object for services */
    private ServicesService servicesService =  new DefaultServicesService();

    /** creates service object for providers */
    private ProviderService providerService = new DefaultProviderService();

    /** The full list of all sets in the system */
    private List<Set> setList;

    /** The full list of all formats in the system */
    private List<Format> formatList;

    /** Stores the temporary processing directive object that is used to display details on the JSP */
    private ProcessingDirective temporaryProcessingDirective;

    /** The sets that have been selecetd by the user in the previous page */
    private String[] setsSelected;

    /** The formats that have been selected by the user in the previous page */
    private String[] formatsSelected;

    /** Request */
    private HttpServletRequest request;

    /** indicates whether the user has chosen to maintain sets */
    private String[] maintainSourceSets;

    /** The output set name for the processing directive */
    private String outputSetName;

    /** The output set spec for the processing directive */
    private String outputSetSpec;

    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Creates a service object for all sets */
    private SetService setService = new DefaultSetService();

    /** creates a service object for all formats */
    private FormatService formatService = new DefaultFormatService();
    
	/** Error type */
	private String errorType; 

    /**
     * Overrides default implementation to view the add processing directives (step 2) page.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");
            String sourceType = (String)request.getSession().getAttribute("sourceType");
            List<Format> tempFormatList = new ArrayList<Format>();
            List<Set> tempSetList = null;
            if(sourceType.equalsIgnoreCase("provider"))
            {
            	// Loop over the formats the source provider can produce
            	// If the service can accept it as input, add it to the list
            	// of Formats the user can select
            	for(Format tempFormat : tempProcDir.getSourceProvider().getFormats())
            		if(tempProcDir.getService().getInputFormats().contains(tempFormat))
                        tempFormatList.add(tempFormat);
            	
                tempSetList = tempProcDir.getSourceProvider().getSets();
            }
            else
            {
            	// Loop over the formats the source service can produce
            	// If the service can accept it as input, add it to the list
            	// of Formats the user can select
            	for(Format tempFormat : tempProcDir.getSourceService().getOutputFormats())
            		if(tempProcDir.getService().getInputFormats().contains(tempFormat))
                        tempFormatList.add(tempFormat);
            	
                tempSetList = tempProcDir.getSourceService().getOutputSets();
            }
            setFormatList(tempFormatList);
            setSetList(tempSetList);

            setTemporaryProcessingDirective(tempProcDir);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("Error in loading Step 2 of Add Processing Rules page",e);
            this.addFieldError("addProcessingDirectivesSetsFormatsError", "Error in loading Step 2 of Add Processing Rules page");
            errorType = "error";
            return INPUT;
        }
    }



    /**
     * Step 2 in adding new processing directive
     *
     * @return {@link #SUCCESS}
     */
    public String addProcessingDirectivesSetsFormats()
    {
        try
        {
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");
            String sourceType = (String)request.getSession().getAttribute("sourceType");

            if(maintainSourceSets==null)
            {
                tempProcDir.setMaintainSourceSets(false);
            }
            else
            {
                tempProcDir.setMaintainSourceSets(true);
            }

            String[] SetIdList = getSetsSelected();
            String[] FormatIdList = getFormatsSelected();
            List<Format> tempFormatList = new ArrayList<Format>();
            List<Set> tempSetList = new ArrayList<Set>();

            if(FormatIdList!=null)
            {
                for(int i=0;i<FormatIdList.length;i++)
                {

                       Format format = formatService.getFormatById(Integer.parseInt(FormatIdList[i]));
                       tempFormatList.add(format);


                }
            }

            if(SetIdList!=null)
            {
                for(int i=0;i<SetIdList.length;i++)
                {
                    if(Integer.parseInt(SetIdList[i])!=0)
                    {
                        Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
                        tempSetList.add(set);
                    }
                    else
                    {
                        if(sourceType.equalsIgnoreCase("provider"))
                        {
                            tempSetList = tempProcDir.getSourceProvider().getSets();
                        }
                        else
                        {
                            tempSetList = tempProcDir.getSourceService().getOutputSets();
                        }
                        break;
                    }
                }
            }

           
          
            if(sourceType.equalsIgnoreCase("provider"))
            {
                Provider tempProvider = providerService.getProviderByName(tempProcDir.getSourceProvider().getName());

                // Validation to ensure that processing directive with same details is not inserted again
                int flag = 1;
                List<ProcessingDirective> procDirList = PDService.getBySourceProviderId(tempProvider.getId());
                if(!procDirList.isEmpty())
                {
                    Iterator<ProcessingDirective> iter = procDirList.iterator();
                    while(iter.hasNext())
                    {
                        ProcessingDirective temp = (ProcessingDirective)iter.next();

                        if(tempProcDir.getService().getId()== temp.getService().getId())
                        {
                            flag = 0;
                            break;
                        }
                    }
                }

                tempProcDir.setTriggeringFormats(tempFormatList);
                tempProcDir.setTriggeringSets(tempSetList);
                request.getSession().setAttribute("temporaryProcessingDirective",tempProcDir);
                if(flag==1)
                {
                    Set setExists = setService.getSetBySetSpec(outputSetSpec);
                    if(setExists==null)
                    {
                        if((outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase("")))
                        {
                            Set tempSet = new Set();
                            tempSet.setDisplayName(outputSetName);
                            tempSet.setSetSpec(outputSetSpec);
                            setService.insertSet(tempSet);
                            tempProcDir.setOutputSet(tempSet);
                        }
                        else
                        {
                            tempProcDir.setOutputSet(setExists);
                        }
                    }
                    else
                    {
                         this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Output Source Set with set specification "+setExists.getSetSpec()+" already exists");
                         errorType = "error";
                         setFormatList(tempProcDir.getSourceProvider().getFormats());
                         setSetList(tempProcDir.getSourceProvider().getSets());
                         setTemporaryProcessingDirective(tempProcDir);
                         return INPUT;
                    }
                    PDService.insertProcessingDirective(tempProcDir);
                }
                else
                {
                     this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Cannot insert Processing Directive with same Source:'"+tempProcDir.getSourceProvider().getName()+"' and Service:'"+tempProcDir.getService().getName()+"' combination");
                     errorType = "error";
                     setFormatList(tempProcDir.getSourceProvider().getFormats());
                     setSetList(tempProcDir.getSourceProvider().getSets());
                     Set tempSet = new Set();
                     tempSet.setDisplayName(outputSetName);
                     tempSet.setSetSpec(outputSetSpec);
                     tempProcDir.setOutputSet(tempSet);
                     setTemporaryProcessingDirective(tempProcDir);
                     return INPUT;
                }
                // end of validation and insertion(if applicable)

            }
            else
            {

                Service tempService = servicesService.getServiceByName(tempProcDir.getSourceService().getName());
                // Validation to ensure that processing directive with same details is not inserted again
                int flag = 1;
                List<ProcessingDirective> procDirList = PDService.getBySourceServiceId(tempService.getId());
                if(!procDirList.isEmpty())
                {
                    Iterator<ProcessingDirective> iter = procDirList.iterator();
                    while(iter.hasNext())
                    {
                        ProcessingDirective temp = (ProcessingDirective)iter.next();
                        if(tempProcDir.getService().getId()== temp.getService().getId())
                        {
                            flag = 0;
                            break;
                        }
                    }
                }

                tempProcDir.setTriggeringFormats(tempFormatList);
                tempProcDir.setTriggeringSets(tempSetList);
                request.getSession().setAttribute("temporaryProcessingDirective",tempProcDir);

                if(flag==1)
                {
                    Set setExists = setService.getSetBySetSpec(outputSetSpec);
                    if(setExists==null)
                    {
                        if((outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase("")))
                        {
                            Set tempSet = new Set();
                            tempSet.setDisplayName(outputSetName);
                            tempSet.setSetSpec(outputSetSpec);
                            setService.insertSet(tempSet);
                            tempProcDir.setOutputSet(tempSet);
                        }
                        else
                        {
                            tempProcDir.setOutputSet(setExists);
                        }
                    }
                    else
                    {
                         this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Output Source Set with set specification "+setExists.getSetSpec()+" already exists");
                         errorType = "error";
                         setFormatList(tempProcDir.getSourceService().getOutputFormats());
                         setSetList(setService.getAllSets());
                         setTemporaryProcessingDirective(tempProcDir);
                         return INPUT;
                    }
                    PDService.insertProcessingDirective(tempProcDir);
                }
                else
                {
                     this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Cannot insert Processing Directive with same Source:'"+tempProcDir.getSourceService().getName()+"' and Service:'"+tempProcDir.getService().getName()+"' combination");
                     errorType = "error";
                     setFormatList(tempProcDir.getSourceService().getOutputFormats());
                     setSetList(setService.getAllSets());
                     Set tempSet = new Set();
                     tempSet.setDisplayName(outputSetName);
                     tempSet.setSetSpec(outputSetSpec);
                     tempProcDir.setOutputSet(tempSet);
                     setTemporaryProcessingDirective(tempProcDir);
                     return INPUT;
                }
                // end of validation

            }

            request.getSession().setAttribute("temporaryProcessingDirective",null);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("Insertion of Processing Rule unsuccessful",e);
            this.addFieldError("listProcessingDirectivesError", "Insertion of Processing Rule unsuccessful");
            errorType = "error";
            return ERROR;
        }
    }

    /**
     * Returns to the previous step in the add processing directive operation
     * 
     * @return
     */
    public String addProcessingDirectiveGoBack()
    {
         try
        {
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");

            if(maintainSourceSets==null)
                {

                    tempProcDir.setMaintainSourceSets(false);
                }
                else
                {

                    tempProcDir.setMaintainSourceSets(true);
                }

                String[] SetIdList = setsSelected;
                String[] FormatIdList = formatsSelected;
                List<Format> tempFormatList = new ArrayList<Format>();
                List<Set> tempSetList = new ArrayList<Set>();

                if(FormatIdList!=null)
                {
                    for(int i=0;i<FormatIdList.length;i++)
                    {
                       Format format = formatService.getFormatById(Integer.parseInt(FormatIdList[i]));
                       tempFormatList.add(format);
                    }
                }

                
                tempProcDir.setTriggeringFormats(tempFormatList);
                if(SetIdList!=null)
                {
                    for(int i=0;i<SetIdList.length;i++)
                    {
                       Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
                       tempSetList.add(set);
                    }
                }

                
                tempProcDir.setTriggeringSets(tempSetList);

                Set setExists = setService.getSetBySetSpec(outputSetSpec);
                if(setExists==null)
                {
                    if((outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase("")))
                    {
                        Set tempSet = new Set();
                        tempSet.setDisplayName(outputSetName);
                        tempSet.setSetSpec(outputSetSpec);
                        //setService.insertSet(tempSet);
                        tempProcDir.setOutputSet(tempSet);
                    }
                    else
                    {
                        tempProcDir.setOutputSet(setExists);
                    }
                }
                else
                {
                    tempProcDir.setOutputSet(setExists);
                }

                request.getSession().setAttribute("temporaryProcessingDirective",tempProcDir);
                return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("There was a problem in redirecting to Add Processing Rule(Step 1)",e);
            return INPUT;
        }
    }

    /**
     * Cancels the add processing directive operation and returns to the list of processing directives
     *
     * @return
     */
    public String addProcessingDirectiveCancel()
    {
        try
        {
            request.getSession().setAttribute("temporaryProcessingDirective",null);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.error("There was a problem redirecting to 'All Processing Rules' page",e);
            return INPUT;
        }
    }

        /**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

    /**
     * Sets the name of the output set
     *
     * @param outputSetName name of the new output set
     */
    public void setOutputSetName(String outputSetName)
    {
        this.outputSetName = outputSetName;
    }

    /**
     * Returns value of the output set
     *
     * @return output set name
     */
    public String getOutputSetName()
    {
        return outputSetName;
    }

    /**
     * Sets the specifications of the output set
     *
     * @param outputSetSpec output set specifications
     */
    public void setOutputSetSpec(String outputSetSpec)
    {
        this.outputSetSpec = outputSetSpec;
    }

    /**
     * Returns specifications of the output set
     *
     * @return output set specifications
     */
    public String getOutputSetSpec()
    {
        return outputSetSpec;
    }

    /**
     * Sets the list of all sets that are relevant to that Provider/Service
     *
     * @param setList list of sets
     */
    public void setSetList(List<Set> setList)
    {
        this.setList = setList;
    }

    /**
     * Gets the list of all sets that are relevant to that Provider/Service
     *
     * @return list of all sets
     */
    public List<Set> getSetList()
    {
        return setList;
    }

    /**
     * Sets the list of all formats that are relevant to that Provider/Service
     *
     * @param formatList list of all formats
     */
    public void setFormatList(List<Format> formatList)
    {
        this.formatList = formatList;
    }

    /**
     * Gets the list of all formats that are relevant to that Provider/Service
     *
     * @return list of all formats
     */
    public List<Format> getFormatList()
    {
        return formatList;
    }

    /**
     * Sets the list of sets that the user has selected
     *
     * @param setsSelected sets selected
     */
    public void setSetsSelected(String[] setsSelected)
    {
        this.setsSelected = setsSelected;
    }

    /**
     * Returns the list of sets selected by the user
     *
     * @return list of selected sets
     */
    public String[] getSetsSelected()
    {
        return setsSelected;
    }

    /**
     * Sets the list of formats selected by the user
     *
     * @param formatsSelected list of formats selected
     */
    public void setFormatsSelected(String[] formatsSelected)
    {
        this.formatsSelected = formatsSelected;
    }

    /**
     * Returns the list of formats selected by the user
     *
     * @return list of formats
     */
    public String[] getFormatsSelected()
    {
        return formatsSelected;
    }


    /**
     * Sets the boolean value representing the user's choice to maintain sets
     *
     * @param maintainSets
     */
    public void setMaintainSourceSets(String[] maintainSourceSets)
    {

        this.maintainSourceSets = maintainSourceSets;
    }

    /**
     * Returns the boolean value representing the user's choice to maintain sets
     *
     * @return
     */
    public String[] getMaintainSourceSets()
    {
        return maintainSourceSets;
    }

    /**
     * Sets the temporary processing Directive object
     *
     * @param temporaryProcessingDirective
     */
    public void setTemporaryProcessingDirective(ProcessingDirective temporaryProcessingDirective)
    {
        this.temporaryProcessingDirective = temporaryProcessingDirective;
    }

    /**
     * Returns the temporary processing directive object
     *
     * @return processing directive object
     */
    public ProcessingDirective getTemporaryProcessingDirective()
    {
        return this.temporaryProcessingDirective;
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
     * @param errorType
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
