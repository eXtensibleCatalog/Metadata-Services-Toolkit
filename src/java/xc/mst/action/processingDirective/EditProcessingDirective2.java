
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Iterator;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.SetService;

/**
 * This action method is the second and last step in editing a processing directive
 *
 * @author Tejaswi Haramurali
 */
public class EditProcessingDirective2 extends ActionSupport
{
    /** Temporary Processing Directive Object that is used to display details on the JSP */
    ProcessingDirective temporaryProcessingDirective;

    /** creates service object for sets  */
    private SetService setService = new DefaultSetService();

    /** creates service object for formats  */
    private FormatService formatService = new DefaultFormatService();

    /** creates service object for services  */
    private ServicesService servicesService = new DefaultServicesService();

    /** creates service object for Processing Directives  */
    private ProcessingDirectiveService PDService = new DefaultProcessingDirectiveService();

    /** The complete list of sets in the system */
    private List<Set> setList;

    /** The complete list of formats in the system */
    private List<Format> formatList;


    /** The ID of the processing directive to be edited */
    private int processingDirectiveId;

    /** The list of sets that have been newly selected by the user */
    private String[] setsSelected;

    /** The list of formats that have been newly selected by the user */
    private String[] formatsSelected;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Attribute that determies whether sets are to be maintained*/
    private String[] maintainSourceSets;

    /** The output set name for a processing directive*/
    private String outputSetName;

    /** The specifications of the output set*/
    private String outputSetSpec;
    /**
     * sets the temporary processing directive
     *
     * @param temporaryProcessingDirective
     */
    public void setTemporaryProcessingDirective(ProcessingDirective temporaryProcessingDirective)
    {
        this.temporaryProcessingDirective = temporaryProcessingDirective;
    }

    /**
     * returns temporary processing directive
     * @return processing directive object
     */
    public ProcessingDirective getTemporaryProcessingDirective()
    {
        return this.temporaryProcessingDirective;
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
     * Overrides default implementation to view the edit processing directives (step 2) page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            Map sessionMap =  ActionContext.getContext().getSession();
            ProcessingDirective tempProcDir = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");
            String sourceType = (String)sessionMap.get("sourceType");
            setSetList(setService.getAllSets());
            List<Format> tempFormatList = null;
            if(sourceType.equalsIgnoreCase("provider"))
            {
                tempFormatList = tempProcDir.getSourceProvider().getFormats();
            }
            else
            {
                tempFormatList = tempProcDir.getSourceService().getOutputFormats();
            }
            setFormatList(formatService.getAllFormats());
            setTemporaryProcessingDirective(tempProcDir);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("editProcessingDirective2Error", "Error : Problem in editing the directive");
            return INPUT;
        }
    }

     /**
     * Step 2 in editing new processing directive
     * @return {@link #SUCCESS}
     */
    public String editProcessingDirectives2()
    {

         try
           {
                Map sessionMap =  ActionContext.getContext().getSession();
                temporaryProcessingDirective = (ProcessingDirective)sessionMap.get("temporaryProcessingDirective");

                if(getMaintainSourceSets()!=null)
                {
                     temporaryProcessingDirective.setMaintainSourceSets(true);
                }
                else
                {
                     temporaryProcessingDirective.setMaintainSourceSets(false);
                }
               Set setExists = setService.getSetBySetSpec(outputSetSpec);
                if(setExists==null)
                {
                    Set tempSet = new Set();
                    tempSet.setDisplayName(outputSetName);
                    tempSet.setSetSpec(outputSetSpec);
                    setService.insertSet(tempSet);
                    temporaryProcessingDirective.setOutputSet(tempSet);
                }
                else
                {
                    temporaryProcessingDirective.setOutputSet(setExists);
                }
               
                String sourceType = (String)sessionMap.get("sourceType");

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
                    // Validation to ensure that processing directive with same details is not inserted again
                    int flag = 1;
                    List<ProcessingDirective> procDirList = PDService.getBySourceProviderId(temporaryProcessingDirective.getSourceProvider().getId());
                    if(!procDirList.isEmpty())
                    {
                        Iterator iter = procDirList.iterator();
                        while(iter.hasNext())
                        {
                            ProcessingDirective tempProcDir = (ProcessingDirective)iter.next();

                            if(tempProcDir.getService().getId()== temporaryProcessingDirective.getService().getId())
                            {
                                if(temporaryProcessingDirective.getId()!=tempProcDir.getId())
                                {
                                    flag = 0;
                                    break;
                                }
                            }
                        }
                    }
                    temporaryProcessingDirective.setTriggeringFormats(tempFormatList);
                    temporaryProcessingDirective.setTriggeringSets(tempSetList);
                    sessionMap.put("temporaryProcessingDirective", temporaryProcessingDirective);
                    if(flag==1)
                    {
                       PDService.updateProcessingDirective(temporaryProcessingDirective);
                    }
                    else
                    {
                         this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Cannot insert Processing Directive with same Source:'"+temporaryProcessingDirective.getSourceProvider().getName()+"' and Service:'"+temporaryProcessingDirective.getService()+"' combination");
                         setTemporaryProcessingDirective(temporaryProcessingDirective);
                         setFormatList(formatService.getAllFormats());
                         setSetList(setService.getAllSets());
                         return INPUT;
                    }
                    // end of validation and insertion(if applicable)

                }
                else
                {
                    // Validation to ensure that processing directive with same details is not inserted again
                    int flag = 1;
                    List<ProcessingDirective> procDirList = PDService.getBySourceServiceId(temporaryProcessingDirective.getSourceService().getId());
                    if(!procDirList.isEmpty())
                    {
                        Iterator iter = procDirList.iterator();
                        while(iter.hasNext())
                        {
                            ProcessingDirective tempProcDir = (ProcessingDirective)iter.next();
                            if(tempProcDir.getService().getId()== temporaryProcessingDirective.getService().getId())
                            {
                               if(temporaryProcessingDirective.getId()!=tempProcDir.getId())
                                {
                                    flag = 0;
                                    break;
                                }
                            }
                        }
                    }
                    temporaryProcessingDirective.setTriggeringFormats(tempFormatList);
                    temporaryProcessingDirective.setTriggeringSets(tempSetList);
                    sessionMap.put("temporaryProcessingDirective", temporaryProcessingDirective);
                    if(flag==1)
                    {
                        new DefaultProcessingDirectiveService().updateProcessingDirective(temporaryProcessingDirective);
                    }
                    else
                    {
                         this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Cannot insert Processing Directive with same Source:'"+temporaryProcessingDirective.getSourceService().getName()+"' and Service:'"+temporaryProcessingDirective.getService().getName()+"' combination");
                         setFormatList(formatService.getAllFormats());
                         setSetList(setService.getAllSets());
                         setTemporaryProcessingDirective(temporaryProcessingDirective);
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
                this.addFieldError("editProcessingDirectives2Error", "Error : The edit was unsuccessful");
                return ERROR;
           }

    }

    /**
     * returns to the previous step in the edit Processing Directive process
     * 
     * @return status of the operation
     */
    public String editPDGoBack()
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
     * Cancels the editing of the processing directive and returns to the list of processing directives
     *
     * @return status of the return operation
     */
    public String editPDCancel()
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
}
