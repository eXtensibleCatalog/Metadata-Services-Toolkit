
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.*;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveInputFormatUtilService;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveInputSetUtilService;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ProcessingDirectiveInputFormatUtilService;
import xc.mst.manager.processingDirective.ProcessingDirectiveInputSetUtilService;
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
public class AddProcessingDirective2 extends ActionSupport
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
     * sets the name of the output set
     * @param outputSetName output set
     */
    public void setOutputSetName(String outputSetName)
    {
        this.outputSetName = outputSetName;
    }

    /**
     * returns value of the output set
     * @return output set
     */
    public String getOutputSetName()
    {
        return outputSetName;
    }

    /**
     * sets the specifications of the output set
     * @param outputSetSpec output set
     */
    public void setOutputSetSpec(String outputSetSpec)
    {
        this.outputSetSpec = outputSetSpec;
    }

    /**
     * returns specifications of the output set
     * @return output set
     */
    public String getOutputSetSpec()
    {
        return outputSetSpec;
    }

    /**
     * sets the list of all sets that are relevant to that Provider/Service
     * @param setList list of sets
     */
    public void setSetList(List<Set> setList)
    {
        this.setList = setList;
    }

    /**
     * gets the list of all sets that are relevant to that Provider/Service
     * @return list of sets
     */
    public List<Set> getSetList()
    {
        return setList;
    }

    /**
     * sets the list of all formats that are relevant to that Provider/Service
     * @param formatList list of formats
     */
    public void setFormatList(List<Format> formatList)
    {
        this.formatList = formatList;
    }

    /**
     * gets the list of all formats that are relevant to that Provider/Service
     * @return list of formats
     */
    public List<Format> getFormatList()
    {
        return formatList;
    }

    /**
     * sets the list of sets that the user has selected
     * @param setsSelected list of sets
     */
    public void setSetsSelected(String[] setsSelected)
    {
        this.setsSelected = setsSelected;
    }

    /**
     * returns the list of sets selected by the user
     * @return list of sets
     */
    public String[] getSetsSelected()
    {
        return setsSelected;
    }

    /**
     * sets the list of formats selected by the user
     * @param formatsSelected list of formats
     */
    public void setFormatsSelected(String[] formatsSelected)
    {
        this.formatsSelected = formatsSelected;
    }

    /**
     * returns the list of formats selected by the user
     * @return list of formats
     */
    public String[] getFormatsSelected()
    {
        return formatsSelected;
    }


    /**
     * sets the boolean value representing the user's choice to maintain sets
     * @param maintainSets
     */
    public void setMaintainSourceSets(String[] maintainSourceSets)
    {

        this.maintainSourceSets = maintainSourceSets;
    }

    /**
     * returns the boolean value representing the user's choice to maintain sets
     * @return
     */
    public String[] getMaintainSourceSets()
    {
        return maintainSourceSets;
    }

    /**
     * sets the temporary processing Directive object
     * @param temporaryProcessingDirective
     */
    public void setTemporaryProcessingDirective(ProcessingDirective temporaryProcessingDirective)
    {
        this.temporaryProcessingDirective = temporaryProcessingDirective;
    }

    /**
     * returns the temporary processing directive object
     * @return processing directive object
     */
    public ProcessingDirective getTemporaryProcessingDirective()
    {
        return this.temporaryProcessingDirective;
    }

    /**
     * Overrides default implementation to view the add processing directives (step 2) page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Map sessionMap = ActionContext.getContext().getSession();
            ProcessingDirective tempProcDir = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");
            String sourceType = (String)sessionMap.get("sourceType");
            List<Format> tempFormatList = null;
            List<Set> tempSetList = null;
            if(sourceType.equalsIgnoreCase("provider"))
            {
                tempFormatList = tempProcDir.getSourceProvider().getFormats();
                tempSetList = tempProcDir.getSourceProvider().getSets();
            }
            else
            {
                tempFormatList = tempProcDir.getSourceService().getOutputFormats();
                tempSetList = setService.getAllSets();
            }
            setFormatList(tempFormatList);
            setSetList(tempSetList);

            setTemporaryProcessingDirective(tempProcDir);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("addProcessingDirectives2Error", "Error in loading page");
            errorType = "error";
            return INPUT;
        }
    }



    /**
     * Step 2 in adding new processing directive
     * @return {@link #SUCCESS}
     */
    public String addProcessingDirectives2()
    {
        try
        {
                    
            Map sessionMap = ActionContext.getContext().getSession();
            ProcessingDirective tempProcDir = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");
            String sourceType = (String)sessionMap.get("sourceType");

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
            List<Format> tempFormatList = new ArrayList();
            List<Set> tempSetList = new ArrayList();

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
                   Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
                   tempSetList.add(set);
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
                    Iterator iter = procDirList.iterator();
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
                sessionMap.put("temporaryProcessingDirective", tempProcDir);
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
                    Iterator iter = procDirList.iterator();
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
                sessionMap.put("temporaryProcessingDirective", tempProcDir);

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
                     setTemporaryProcessingDirective(tempProcDir);
                     return INPUT;
                }
                // end of validation

            }

            sessionMap.put("temporaryProcessingDirective", null);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful");
            errorType = "error";
            return ERROR;
        }
    }

    /**
     * returns to the previous step in the add processing directive operation
     * 
     * @return status of the return operation
     */
    public String addPDGoBack()
    {
         try
        {
            Map sessionMap = ActionContext.getContext().getSession();
            ProcessingDirective tempProcDir = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");

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
                List<Format> tempFormatList = new ArrayList();
                List<Set> tempSetList = new ArrayList();

                if(FormatIdList!=null)
                {
                    for(int i=0;i<FormatIdList.length;i++)
                    {
                       Format format = formatService.getFormatById(Integer.parseInt(FormatIdList[i]));
                       tempFormatList.add(format);
                    }
                }

                System.out.println("Setting the list of formats and format size is "+tempFormatList.size());
                tempProcDir.setTriggeringFormats(tempFormatList);
                if(SetIdList!=null)
                {
                    for(int i=0;i<SetIdList.length;i++)
                    {
                       Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
                       tempSetList.add(set);
                    }
                }

                System.out.println("Setting the list of sets and sets size is "+tempSetList.size());
                tempProcDir.setTriggeringSets(tempSetList);

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
                    tempProcDir.setOutputSet(setExists);
                }

                sessionMap.put("temporaryProcessingDirective", tempProcDir);
                return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return INPUT;
        }
    }

    /**
     * cancels the add processing directive operation and returns to the list of processing directives
     *
     * @return status of the return operation
     */
    public String addPDCancel()
    {
        try
        {
            Map sessionMap = ActionContext.getContext().getSession();
            sessionMap.put("temporaryProcessingDirective", null);
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return INPUT;
        }
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
